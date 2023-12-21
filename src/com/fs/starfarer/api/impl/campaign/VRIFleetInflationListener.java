package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.listeners.FleetInflationListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class VRIFleetInflationListener implements FleetInflationListener {
    public static final String VOLSHIELDS = "azorian_shields"; //target hullmod ID
    public static final String VOLAUX = "volaux";
    public static final float CHANCE = 0.75f; //chance for hullmod to spawn smod
    public int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }
    @Override
    public void reportFleetInflated(CampaignFleetAPI fleet, FleetInflater inflater) {

        if(fleet.isPlayerFleet()) return;
        if(fleet.getFleetData().getCommander().equals(Global.getSector().getPlayerPerson())) return;
        if(!fleet.getFaction().getId().contains("vri")) return; //change the quote to the ID of target faction

        for (FleetMemberAPI fleetMemberAPI : fleet.getMembersWithFightersCopy()) {
            boolean vricombat = fleetMemberAPI.getHullSpec().getBuiltInMods().contains(VOLAUX);
            boolean chance = (Math.random() > CHANCE);

            if(fleetMemberAPI.isFighterWing())continue;
            if(fleetMemberAPI.isStation())continue;
            if(fleetMemberAPI.isMothballed())continue;
            if(!vricombat)continue;
            if(chance)continue;

            int SModsAmount = (determineAmountofSmods(fleet.getFaction()));
            fleetMemberAPI.getStats().getDynamic().getMod(Stats.MAX_PERMANENT_HULLMODS_MOD).modifyFlat("VolInflation", (SModsAmount));
            fleetMemberAPI.getVariant().getSModdedBuiltIns().add(VOLSHIELDS);
            fleetMemberAPI.updateStats();
        }
    }

    public int determineAmountofSmods(FactionAPI factionAPI) {
        return getRandomNumber(0,1);
    }

}