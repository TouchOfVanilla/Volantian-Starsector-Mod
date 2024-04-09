package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;

public class VRIFleetInflationListenerBackup implements FleetInflationListener {
    public static final String VOLSHIELDS = "azorian_shields"; //target hullmod ID
    public static final String VOLAUX = "volaux";
    public static final float CHANCE = 10f; //chance for hullmod to spawn smod

    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {

        if (fleet.isPlayerFleet()) return;
        if (fleet.getFleetData().getCommander().equals(Global.getSector().getPlayerPerson())) return;
        if (!fleet.getFaction().getId().equals("vri")) return; //change the quote to the ID of target faction

        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            boolean chance = (MathUtils.getRandomNumberInRange(0,100) > CHANCE);

            if (!fleetMemberAPI.isFighterWing()) continue;
            if (!fleetMemberAPI.isStation()) continue;
            if (!fleetMemberAPI.isMothballed()) continue;
            if (chance) continue;
            if (fleetMemberAPI.getVariant().getSource() != VariantSource.REFIT) {
                ShipVariantAPI var = fleetMemberAPI.getVariant();
                var.setOriginalVariant(null);
                var.setHullVariantId(Misc.genUID());
                var.setSource(VariantSource.REFIT);
                fleetMemberAPI.setVariant(var, false, true);
            }
            fleetMemberAPI.getVariant().addPermaMod(VOLSHIELDS, true);
            fleetMemberAPI.updateStats();
        }
    }
}