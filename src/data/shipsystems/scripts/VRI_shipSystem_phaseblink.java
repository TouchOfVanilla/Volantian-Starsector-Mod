
//Author: lord_dalton with permission to be used by TouchOfVanilla for the mod VRI
package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import org.dark.shaders.distortion.DistortionAPI;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.WaveDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;
import org.magiclib.util.MagicUI;

import java.awt.*;

public class VRI_shipSystem_phaseblink extends BaseShipSystemScript {
    public static Color JITTER = new Color(70, 70, 70, 160);
    private boolean runOnce = false;
    private final static float activediss = 0.2f;
    private final static float activetimeflow = 3f;
    //controls how much meter is lost when first diving
    private final static float initialdivecharge = 20f;
    CombatEngineAPI engine = Global.getCombatEngine();
    SpriteAPI sprite1 = Global.getSettings().getSprite("fx","vol_incursor_glow1",true);
    SpriteAPI sprite2 = Global.getSettings().getSprite("fx","vol_incursor_glow2",true);


    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {

        ShipSystemAPI cloak = playerShip.getPhaseCloak();
        if (cloak == null) {
            cloak = playerShip.getSystem();
        }
        if (cloak == null) {
            return;
        }
    }

    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        ShipAPI ship = null;
        boolean player = false;
        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else {
            return;
        }

        if (player) {
            maintainStatus(ship, state, effectLevel);
        }

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, id);
            return;
        }

        phaseblinklistener blistener = null;

        if(!ship.hasListenerOfClass(phaseblinklistener.class)) {

            blistener = new phaseblinklistener(ship);

            ship.addListener(blistener);
        }else{
            blistener = ship.getListeners(phaseblinklistener.class).get(0);
        }


        if (ship.getSystem().isActive()) {
            if(!runOnce){
                blistener.currcharge-=initialdivecharge;
                runOnce=true;
            }

                ship.setPhased(true);
                stats.getTimeMult().modifyMult(id, activetimeflow);
                stats.getFluxDissipation().modifyMult(id, activediss);
                stats.getAcceleration().modifyMult(id, 3 * effectLevel);
                stats.getTurnAcceleration().modifyMult(id, 3 * effectLevel);
                ship.setJitterUnder(ship, JITTER, effectLevel, 7, 1f, 3f);
                ship.setExtraAlphaMult(MathUtils.clamp(1f - effectLevel, 0.1f, 1f));
                ship.setApplyExtraAlphaToEngines(true);


                int numCopies = 14;
            Color phasColour = new Color(112, 0, 126, 100);
                Color phaseColour = new Color(112, 0, 126, (int)(90*effectLevel));
                Color white = new Color(255,255,255,(int)(100*effectLevel));

                int maxRange = 14;
                for (int i = 0; i < numCopies; i++) {

                    Vector2f randomLoc = MathUtils.getRandomPointInCircle(ship.getLocation(), maxRange);
                    MagicRender.singleframe(sprite2, randomLoc, new Vector2f(sprite2.getWidth(), sprite2.getHeight()), ship.getFacing()-90, phaseColour, false);

                }

                numCopies = 3;
                maxRange = 3;
                for (int i = 0; i < numCopies; i++) {
                    Vector2f randomLoc = MathUtils.getRandomPointInCircle(ship.getLocation(), maxRange);
                    MagicRender.singleframe(sprite1, randomLoc, new Vector2f(sprite1.getWidth(), sprite1.getHeight()), ship.getFacing()-90, white, false);

                }

        } else {
            if(runOnce){
                ship.getSystem().forceState(ShipSystemAPI.SystemState.OUT,0f);
            }
        }

    }
    public void unapply(MutableShipStatsAPI stats, String id) {
        runOnce = false;
        ShipAPI ship = null;
        boolean player = false;

        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else {
            return;
        }

        phaseblinklistener blistener = null;

        if(!ship.hasListenerOfClass(phaseblinklistener.class)) {

            blistener = new phaseblinklistener(ship);

            ship.addListener(blistener);
        }else{
            blistener = ship.getListeners(phaseblinklistener.class).get(0);
        }

        stats.getFluxDissipation().unmodifyMult(id);
        stats.getTimeMult().unmodifyMult(id);
        stats.getAcceleration().unmodify(id);
        stats.getTurnAcceleration().unmodify(id);

        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);


    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    public static class phaseblinklistener implements AdvanceableListener, DamageTakenModifier {

        IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);
        public final ShipAPI ship;
        public final static float maxcharge = 100f;
        public float currcharge = 100f;
        public final static float dtcratio = 0.001f;

        private final static float passivegainrate = 0.2f;
        private final static float activelossrate = 0.5f;

        public phaseblinklistener(ShipAPI ship) {
            this.ship = ship;
        }

        public float getcharge(){
            return currcharge/maxcharge;
        }


        @Override
        public void advance(float amount) {
            interval.advance(amount);

            MagicUI.drawSystemBar(ship, new Color(0.5f, 0.5f, 1f, 1f), getcharge(), 0);

            if (interval.intervalElapsed()){
                if(ship.getSystem().getState().equals(ShipSystemAPI.SystemState.ACTIVE)){
                    currcharge-=activelossrate;
                }else{
                    currcharge+=passivegainrate;
                }
            }

            if(currcharge>100f){
                currcharge = 100f;
            }
            if (currcharge<=0f){
                currcharge = 1f;
                ship.getSystem().forceState(ShipSystemAPI.SystemState.OUT, 0f);
                Global.getSoundPlayer().playSound("system_phase_cloak_deactivate", 1,1,ship.getLocation(),ship.getVelocity());
            }
            if (currcharge <= 30f && ship.getSystem().getState().equals(ShipSystemAPI.SystemState.IDLE)){
                ship.getSystem().setCooldownRemaining(0.1f);
            }
        }

        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if(shieldHit){
                currcharge += dtcratio * damage.getDamage();
            }
            return null;
        }
    }
}
