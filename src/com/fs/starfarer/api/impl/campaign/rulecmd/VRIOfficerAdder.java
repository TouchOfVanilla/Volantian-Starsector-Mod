package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.util.Misc;
import java.util.List;
import java.util.Map;

//thank u silly fuel person

@SuppressWarnings("empty-statement")
public class VRIOfficerAdder extends BaseCommandPlugin {

    @Override
    public boolean execute(final String ruleId, final InteractionDialogAPI dialog, final List<Misc.Token> params, final Map<String, MemoryAPI> memoryMap) {
        if (dialog == null) {
            return false;
        }

        final String officer = params.get(0).getString((Map)memoryMap);
        final CampaignFleetAPI playerFleet = Global.getSector().getPlayerFleet();
        final PersonAPI person = Global.getSector().getImportantPeople().getPerson(officer);;

        Global.getSoundPlayer().playUISound("ui_cargo_marines_drop", 1f, 1f);
        playerFleet.getFleetData().addOfficer(person);

        return true;
    }

}