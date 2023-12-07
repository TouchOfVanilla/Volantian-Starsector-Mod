package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.hullmods.AutoforgeOverclocking;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.hullmods.AutoforgeOverclocking.SHIELD_ARC_MULT;

//Thank u ruddy you are the bestsest help
public class VRI_weapon_azorian_lance implements BeamEffectPlugin {

    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = null;

        if ((beam.getDamageTarget() instanceof ShipAPI)) {
            target = beam.getDamageTarget();
        } else return;

        if (!(target instanceof ShipAPI) || ((ShipAPI) target).isFighter() || ((ShipAPI) target).getHullSpec().isPhase() || ((ShipAPI) target).getShield() == null) return;
        ShipAPI targetReal = (ShipAPI) target;

        if (targetReal.hasListenerOfClass(AzorianLanceAdvanceableListener.class)) {

            AzorianLanceAdvanceableListener listener = ((ShipAPI) target).getListeners(AzorianLanceAdvanceableListener.class).get(0);
            listener.isHitting = true;

        } else {
            targetReal.addListener(new AzorianLanceAdvanceableListener(targetReal));
        }
    }

    public static class AzorianLanceAdvanceableListener implements AdvanceableListener {

        public boolean isHitting = true;
        public final ShipAPI target;
        public final Color originalShieldColour;
        public final Color endColour = new Color(0, 255, 207, 75);
        public final IntervalUtil crackleTimer = new IntervalUtil(0.5f, 0.5f);

        float inOutTime = 1.5f; //effect will fade in / out over 3 seconds
        float timeOnTarget = 0f; //amount of time the target has been hit by the beam

        final float maxAngle = 40f;
        final float minAngle = 10f;

        public AzorianLanceAdvanceableListener(ShipAPI target) {

                this.target = target;
                originalShieldColour = target.getShield().getInnerColor();
        }

        @Override
        public void advance(float amount) {

            if (isHitting) {
                timeOnTarget += amount;
                crackleTimer.advance(amount);
            } else {
                timeOnTarget -= amount;
            }

            //this allows for a smooth fade / ramp up
            timeOnTarget = MathUtils.clamp(timeOnTarget, 0, inOutTime); //clamps the value to within the range, no reason for it to go over / under
            float effectLevel = (timeOnTarget / inOutTime);

            //this is just yoinked directly
            if (target.getShield() != null && target.getShield().getType().equals(ShieldAPI.ShieldType.FRONT)) {
                target.getMutableStats().getShieldArcBonus().modifyMult("azorian_lance", target.getShield().getArc()/2);
                target.getShield().setActiveArc(target.getShield().getArc()/2);
            } else if (target.getShield() != null && target.getShield().getType().equals(ShieldAPI.ShieldType.OMNI)) {
                target.getMutableStats().getShieldArcBonus().modifyMult("azorian_lance", target.getShield().getArc()/2);
                target.getShield().setActiveArc(target.getShield().getArc()/2);
                target.getMutableStats().getShieldTurnRateMult().modifyMult("azorian_lance", .5f);
            } else if (target.getShield().isOff()){
                target.setDefenseDisabled(true);
            }


            //not super sure about this tbh, might end up getting stuck somehow?
            Color fadeColour = Misc.interpolateColor(originalShieldColour, endColour, effectLevel);
            if (target.getShield() != null) {
                target.getShield().setInnerColor(fadeColour);
                target.getMutableStats().getShieldDamageTakenMult().modifyMult("azorian_lance", effectLevel);
            }

            //this is just yoinked directly from the old code
            if (crackleTimer.intervalElapsed() && target.getShield() != null) {
                float arcDistance = MathUtils.getRandomNumberInRange(minAngle, maxAngle);
                float StartAngle = MathUtils.getRandomNumberInRange(0, target.getShield().getActiveArc() - arcDistance);
                float shieldStartAngle = target.getShield().getFacing() + (target.getShield().getActiveArc() * 0.5f);
                float arcStartAngle = shieldStartAngle - StartAngle;


                Vector2f arcStart = MathUtils.getPointOnCircumference(target.getShieldCenterEvenIfNoShield(), target.getShield().getRadius(), arcStartAngle);
                Vector2f arcEnd = MathUtils.getPointOnCircumference(target.getShieldCenterEvenIfNoShield(), target.getShield().getRadius(), arcStartAngle - arcDistance);

                Global.getCombatEngine().spawnEmpArcVisual(arcStart, target, arcEnd, target, 10,
                        new Color(0, 255, 207, 255),
                        new Color(0, 255, 207, 255));
            }

            //if we're done, remove the listener & unmodify the bonuses
            if (effectLevel == 0 && !isHitting) {
                target.getMutableStats().getShieldArcBonus().unmodify("azorian_lance");
                target.getMutableStats().getShieldTurnRateMult().unmodify("azorian_lance");
                target.getMutableStats().getShieldDamageTakenMult().unmodify("azorian_lance");
                target.setDefenseDisabled(false);


                target.removeListener(this);
                return;
            }

            isHitting = false; //needs to be set to false at the end of a frame

        }
    }
}
