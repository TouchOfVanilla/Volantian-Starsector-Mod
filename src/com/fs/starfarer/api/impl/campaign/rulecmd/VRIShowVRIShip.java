package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VRIShowVRIShip extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        } else {
            String id = ((Misc.Token) params.get(0)).getString(memoryMap);
            Iterator var7 = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().iterator();

            while (var7.hasNext()) {
                FleetMemberAPI member = (FleetMemberAPI) var7.next();
                if (member.getHullSpec().getBuiltInMods().equals(id)) {
                    dialog.getVisualPanel().showFleetMemberInfo(member, true);
                    return true;

                }
            }
        }
        return true;
    }
}

