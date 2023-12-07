package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.mission.FleetSide;
import data.world.industries_and_buildings.VRI_MidlineMarketPlugin;
import org.apache.log4j.Logger;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


public class AzorianNavigationRelay extends BaseHullMod{
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        float basespeed = ship.getMutableStats().getMaxSpeed().base;
        float modspeed = ship.getMutableStats().getMaxSpeed().modified;

        float speedbuffpercent = calculatePercentageDifference(basespeed, modspeed);

        ship.getMutableStats().getTurnAcceleration().modifyPercent("azorian_nav",speedbuffpercent);
        ship.getMutableStats().getAcceleration().modifyPercent("azorian_nav", speedbuffpercent * 2f);
        ship.getMutableStats().getDeceleration().modifyPercent("azorian_nav", speedbuffpercent);
        ship.getMutableStats().getMaxTurnRate().modifyPercent("azorian_nav", speedbuffpercent);
        if(ship == Global.getCombatEngine().getPlayerShip()) {
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
        if(ship.getVariant().hasHullMod("azorian") || ship.getVariant().hasHullMod("azorian_matrices")){
            return false;
        }
        return true;
    }

    public String getUnapplicableReason(ShipAPI ship) {

        if(ship.getVariant().hasHullMod("azorian")) return "Unable to use with an existing crystal network";
        if(ship.getVariant().hasHullMod("azorian_matrices")) return "Already present within the existing Azorian network";
        return null;
    }
}
