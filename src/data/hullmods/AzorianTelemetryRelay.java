package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.fleet.FleetData;
import com.fs.starfarer.combat.CombatEngine;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript;

import java.util.Iterator;
import java.util.List;

import static com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript.BASE_MAXIMUM;
import static com.fs.starfarer.api.impl.campaign.skills.ElectronicWarfareScript.PER_JAMMER;

public class AzorianTelemetryRelay extends BaseHullMod {
    private CombatEngineAPI engine = null;
    private static final float AZORIAN_MULT = 2f;
    boolean playerlost = false;
    boolean playerwon = true;

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
        if (ship == Global.getCombatEngine().getPlayerShip()) {
            if (finalrating > 0) {
                Global.getCombatEngine().maintainStatusForPlayerShip("AzorianRangeMod", "graphics/icons/hullsys/fortress_shield.png", "Azorian Range Buff", (int)finalrating + "% increased range based on " + (int)totaldiff + "% ECM rating", false);
            }
            if (finalrating < 0) {
                Global.getCombatEngine().maintainStatusForPlayerShip("AzorianRangeMod", "graphics/icons/hullsys/fortress_shield.png", "Azorian Range Debuff", (int)finalrating + "% decreased range based on " + (int)totaldiff + "% ECM rating", true);
            }
            if (finalrating == 0){
                Global.getCombatEngine().maintainStatusForPlayerShip("AzorianRangeMod", "graphics/icons/hullsys/fortress_shield.png", "Azorian Range Modifier", "ECM ratings are equal, no effect", false);

            }
        }
    }

    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id){
    }

    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

    }

    private float getTotal(CombatFleetManagerAPI manager) {
        float max = 0.0F;
        PersonAPI commander;
        for (Iterator var4 = manager.getAllFleetCommanders().iterator(); var4.hasNext(); max = Math.max(max, BASE_MAXIMUM + commander.getStats().getDynamic().getValue("electronic_warfare_max", 0.0F))) {
            commander = (PersonAPI) var4.next();
        }

        float total = 0.0F;
        List<DeployedFleetMemberAPI> deployed = manager.getDeployedCopyDFM();
        Iterator var6 = deployed.iterator();

        while (var6.hasNext()) {
            DeployedFleetMemberAPI member = (DeployedFleetMemberAPI) var6.next();
            if (!member.isFighterWing() && !member.isStationModule()) {
                float curr = member.getShip().getMutableStats().getDynamic().getValue("electronic_warfare_flat", 0.0F);
                total += curr;
            }
        }

        var6 = this.engine.getObjectives().iterator();

        while (var6.hasNext()) {
            BattleObjectiveAPI obj = (BattleObjectiveAPI) var6.next();
            if (obj.getOwner() == manager.getOwner() && "sensor_array".equals(obj.getType())) {
                total += PER_JAMMER;
            }
        }
        return total;
        }
    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return "percentage-based range modifier";
        } else if (index == 1) {
            return "total, uncapped ECM rating";
        } else if (index == 2) {
            return "stack";
        } else {
            return index == 3 ? "50%" : null;
        }
    }
}

