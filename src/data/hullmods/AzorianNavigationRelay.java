package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;


public class AzorianNavigationRelay extends BaseHullMod {
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float basespeed = ship.getMutableStats().getMaxSpeed().base;
        float modspeed = ship.getMutableStats().getMaxSpeed().modified;

        float speedbuffpercent = calculatePercentageDifference(basespeed, modspeed);

        ship.getMutableStats().getTurnAcceleration().modifyPercent("azorian_nav", speedbuffpercent);
        ship.getMutableStats().getAcceleration().modifyPercent("azorian_nav", speedbuffpercent * 2f);
        ship.getMutableStats().getDeceleration().modifyPercent("azorian_nav", speedbuffpercent);
        ship.getMutableStats().getMaxTurnRate().modifyPercent("azorian_nav", speedbuffpercent);
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            Global.getCombatEngine().maintainStatusForPlayerShip("azorian_nav", "graphics/icons/hullsys/fortress_shield.png", "Azorian Nav Relay", (int) speedbuffpercent + "% maneuverability based on " + (int) speedbuffpercent + "% total modifier to max speed", false);
        }
    }

    public static float calculatePercentageDifference(float base, float modified) {
        // Prevent division by zero
        if (base == 0) {
            return modified == 0 ? 0 : 100; // If both are zero, consider them equal; otherwise, it's a 100% increase
        }

        // Calculate percentage difference
        return ((modified - base) / base) * 100;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getVariant().hasHullMod("azorian_relay") || ship.getVariant().hasHullMod("azorian_matrices")) {
            return false;
        }
        if (ship.getHullSpec().getManufacturer().equals("Low Tech") || ship.getHullSpec().getManufacturer().equals("High Tech") || ship.getHullSpec().getManufacturer().equals("Midline") || ship.getHullSpec().getManufacturer().equals("Remnant")) {
            return true;
        }
        return false;
    }

    public String getUnapplicableReason(ShipAPI ship) {

        if (ship.getVariant().hasHullMod("azorian_relay")) return "Unable to use with an existing crystal network";
        if (ship.getVariant().hasHullMod("azorian_matrices"))
            return "Already present within the existing Azorian network";
        return "There is no known configuration of Azorian crystals for a hull of this design type.";
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return "All";
        }
        return null;
    }
}
