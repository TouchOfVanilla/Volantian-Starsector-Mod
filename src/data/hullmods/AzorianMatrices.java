package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.mission.FleetSide;

import static data.hullmods.AzorianNavigationRelay.calculatePercentageDifference;
import static data.hullmods.AzorianTelemetryRelay.getTotal;

public class AzorianMatrices extends BaseHullMod {
    private CombatEngineAPI engine = null;
    private static final float AZORIAN_MULT = 2f;
    boolean playerlost = false;
    boolean playerwon = true;
    @Override
    public void advanceInCombat(ShipAPI ship, float amount) {
        engine = Global.getCombatEngine();
        String AzorianMod = "azorian";
        float player = getTotal(engine.getFleetManager(FleetSide.PLAYER));
        float enemy = getTotal(engine.getFleetManager(FleetSide.ENEMY));
        float totaldiff = (player - enemy);
        if (totaldiff < 0){
            playerlost = true;
            float finalrating = totaldiff;
        }
        if (totaldiff > 0);
        playerwon = true;
        float finalrating = (totaldiff*AZORIAN_MULT);

        if (playerwon = true){
            ship.getMutableStats().getBallisticWeaponRangeBonus().modifyPercent(AzorianMod, finalrating);
            ship.getMutableStats().getEnergyWeaponRangeBonus().modifyPercent(AzorianMod, finalrating);
        }
        if (playerlost = true){
            ship.getMutableStats().getBallisticWeaponRangeBonus().modifyPercent(AzorianMod, finalrating);
            ship.getMutableStats().getEnergyWeaponRangeBonus().modifyPercent(AzorianMod, finalrating);
        }
        if (finalrating < 0){
            finalrating = 0;
        }
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            if (finalrating > 0) {
                Global.getCombatEngine().maintainStatusForPlayerShip("AzorianRangeMod", "graphics/icons/hullsys/fortress_shield.png", "Azorian ECM Matrix", (int)finalrating + "% increased range based on " + (int)totaldiff + "% ECM rating", false);
            }
            if (finalrating == 0){
                Global.getCombatEngine().maintainStatusForPlayerShip("AzorianRangeMod", "graphics/icons/hullsys/fortress_shield.png", "Azorian ECM Matrix", "No range effect", false);

            }
        }

        float basespeed = ship.getMutableStats().getMaxSpeed().base;
        float modspeed = ship.getMutableStats().getMaxSpeed().modified;

        float speedbuffpercent = calculatePercentageDifference(basespeed, modspeed);
        if (speedbuffpercent < 0){
            speedbuffpercent = 0;
        }

        ship.getMutableStats().getTurnAcceleration().modifyPercent("azorian_nav",speedbuffpercent);
        ship.getMutableStats().getAcceleration().modifyPercent("azorian_nav", speedbuffpercent * 2f);
        ship.getMutableStats().getDeceleration().modifyPercent("azorian_nav", speedbuffpercent);
        ship.getMutableStats().getMaxTurnRate().modifyPercent("azorian_nav", speedbuffpercent);
        if(ship == Global.getCombatEngine().getPlayerShip()) {
            Global.getCombatEngine().maintainStatusForPlayerShip("azorian_nav", "graphics/icons/hullsys/fortress_shield.png", "Azorian Nav Matrix", (int) speedbuffpercent + "% maneuverability based on " + (int) speedbuffpercent + "% total increase to max speed", false);
        }
    }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return "speed";
        } else if (index == 1) {
            return "maneuverability";
        } else if (index == 2) {
            return "ECM rating";
        } else if (index == 3) {
            return "range";
            } else {
            return index == 4 ? "double the ECM rating" : null;
        }
    }
}
