package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
    public class VRIPlayerFleetHasHullmod extends BaseCommandPlugin {
        @Override
        public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
            boolean hasMod = false;
            CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
            for (FleetMemberAPI ship : fleet.getFleetData().getMembersListCopy()) {
                Collection<String> mods = ship.getVariant().getHullMods();
                for (String m : mods) {
                    if (m.equals("azorian_matrices")) {
                        hasMod = true;
                        break;
                    }
                }
                if (hasMod) break;
            }
        return hasMod;}
    }





