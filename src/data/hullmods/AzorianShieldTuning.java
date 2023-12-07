package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AsteroidAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AzorianShieldTuning extends BaseHullMod {
    public final IntervalUtil crackleTimer = new IntervalUtil(0.5f, 0.5f);
    final float maxAngle = 40f;
    final float minAngle = 10f;

    final float SHIELD_DAM_MULT = 10f;



    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
        stats.getShieldDamageTakenMult().modifyMult(id,1f + SHIELD_DAM_MULT * 0.01f);
    }

    public void advanceInCombat(ShipAPI ship, float amount) {

        List<CombatEntityAPI> validTargets = new ArrayList<CombatEntityAPI>();
        for (CombatEntityAPI entityToTest : CombatUtils.getEntitiesWithinRange(ship.getLocation(), 300f)) {
            if (entityToTest instanceof ShipAPI || entityToTest instanceof AsteroidAPI || entityToTest instanceof MissileAPI) {
                //Phased targets, and targets with no collision, are ignored
                if (entityToTest instanceof ShipAPI) {
                    if (((ShipAPI) entityToTest).isPhased() || entityToTest == ship || (entityToTest.getOwner() == ship.getOwner())) {
                        continue;
                    }
                }
                //This Should mean it targets enemy missiles and not friendly ones...?
                if (entityToTest instanceof MissileAPI) {
                    if ((entityToTest.getOwner() == ship.getOwner()) || (((MissileAPI) entityToTest).isFizzling())) {
                        continue;
                    }
                }
                if (entityToTest.getCollisionClass().equals(CollisionClass.NONE)) {
                    continue;
                }
                validTargets.add(entityToTest);
            }
        }


        if (crackleTimer.intervalElapsed() && ship.getShield().isOn()) {
            float arcDistance = MathUtils.getRandomNumberInRange(minAngle, maxAngle);
            float StartAngle = MathUtils.getRandomNumberInRange(0, ship.getShield().getActiveArc() - arcDistance);
            float shieldStartAngle = ship.getShield().getFacing() + (ship.getShield().getActiveArc() * 0.5f);
            float arcStartAngle = shieldStartAngle - StartAngle;

            Vector2f arcStart = MathUtils.getPointOnCircumference(ship.getShieldCenterEvenIfNoShield(), ship.getShield().getRadius(), arcStartAngle);
            Vector2f arcEnd = MathUtils.getPointOnCircumference(ship.getShieldCenterEvenIfNoShield(), ship.getShield().getRadius(), arcStartAngle - arcDistance);
            CombatEntityAPI target = new SimpleEntity(arcEnd);


                if (!validTargets.isEmpty()){
                target = validTargets.get(MathUtils.getRandomNumberInRange(0, validTargets.size() - 1));
            }

            Global.getCombatEngine().spawnEmpArc(ship, arcStart, ship, target,
                    DamageType.ENERGY, //Damage type
                    100f, //Damage
                    10f, //Emp
                    100000, //Max range
                    "realitydisruptor_emp_impact", //Impact sound
                    20f, // thickness of the lightning bolt
                    new Color(0, 255, 207, 127), //Central color
                    new Color(0, 255, 207, 127) //Fringe Color
            );
            crackleTimer.setInterval(1f,1f);
        }
        crackleTimer.advance(0.25f);
      }
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getShield() == null){
            return false;
        } else if (ship.getShield().getType().equals(ShieldAPI.ShieldType.PHASE)){
            return false;
        }
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {
        if (ship.getShield() == null) {
            return "Ship has no shields";
        }

        if (ship.getShield().getType() == ShieldAPI.ShieldType.PHASE) {
            return "Incompatible with phase ships";
        }

        return null;
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "" + (int) ((1f - 0.9f) * 100f) + "%";
        return null;
    }
    }
