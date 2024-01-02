package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;

public class VestSkill extends BaseHullMod {
    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
        if (!this.isApplicableToShip(ship)){
            ship.getVariant().removeMod(this.spec.getId());
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) return "20/40/60/80";

        return null;
    }

    @Override
    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship.getCaptain() == null || !ship.getCaptain().isAICore()) {
            return false;
        }
        if (ship.getCaptain() != null) {
            if (ship.getCaptain().isAICore()) {
                if (!ship.getCaptain().getAICoreId().equals("vestige_core")) {
                    return false;
                }
            }
        }
        return true;
    }
}
