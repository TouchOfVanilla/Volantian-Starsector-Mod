package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.OfficerDataAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class VRIHasAveryAsOfficer extends BaseCommandPlugin {
    @Override


    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        PersonAPI avery = Global.getSector().getImportantPeople().getPerson("avery");
        PersonAPI p = avery; //get your person by an ID or so
        for (OfficerDataAPI data: Global.getSector().getPlayerFleet().getFleetData().getOfficersCopy()) {
            if (data.getPerson() == p) return true;
        }

        return false;
    }
}
