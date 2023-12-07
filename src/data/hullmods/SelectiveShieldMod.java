package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;

import java.util.Iterator;
import java.util.List;

public class SelectiveShieldMod extends BaseHullMod {

    Boolean overloading = false;
    Boolean init = false;

    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {


        CombatEngineAPI engine = Global.getCombatEngine();
        if (engine.isEntityInPlay(ship) && ship.isAlive()){

            if (!init){
                Float targetradius = ship.getParentStation().getShield().getRadius();
                ship.getShield().setRadius(targetradius - 10);
                ship.getShipAI().getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS);
                ship.getMutableStats().getShieldTurnRateMult().modifyMult("selshield", 100);
            }
            init = true;
            List<DamagingProjectileAPI> projs = engine.getProjectiles();
            Iterator projcounter = projs.iterator();
            while (projcounter.hasNext()) {
                DamagingProjectileAPI boolet = (DamagingProjectileAPI) projcounter.next();
                double bulletX = boolet.getLocation().x;
                double bulletY = boolet.getLocation().y;
                double shipX = ship.getParentStation().getLocation().x;
                double shipY = ship.getParentStation().getLocation().y;

                double distance = Math.sqrt(Math.pow(shipX - bulletX, 2) + Math.pow(shipY - bulletY, 2));
                float shipFacing = ship.getFacing(); // Assuming getFacing() returns the facing angle in degrees

// Calculate the angle in radians
                double angleRadians = Math.atan2(bulletY - shipY, bulletX - shipX);

// Convert the angle to degrees
                double angleDegrees = Math.toDegrees(angleRadians);

// Calculate the relative angle accounting for the ship's facing angle
                double relativeAngle = (angleDegrees - shipFacing + 360) % 360;
                float projangle = (float)relativeAngle;

                if (boolet.getSource() == ship) {
                    continue;
                }

                if ((distance <= ship.getParentStation().getShieldRadiusEvenIfNoShield() + 100) && boolet.getDamageAmount() > 750) {
                    ship.getShipAI().getAIFlags().setFlag(ShipwideAIFlags.AIFlags.KEEP_SHIELDS_ON);
                    ship.getShield().toggleOn();
                } else {
                    ship.getShipAI().getAIFlags().setFlag(ShipwideAIFlags.AIFlags.DO_NOT_USE_SHIELDS);
                    ship.getShield().toggleOff();
                }
            }

            if ((ship.getCurrFlux() > 0) && overloading == false) {
                ship.getFluxTracker().forceOverload(100);
                overloading = true;
            }
            if ((ship.getCurrFlux() == 0) && overloading == true) {
                ship.getFluxTracker().stopOverload();
                overloading = false;
            }

        }
    }
}
