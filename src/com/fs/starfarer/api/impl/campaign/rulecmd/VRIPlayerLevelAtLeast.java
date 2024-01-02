package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

/**
 * returns true if player level is greater than or equal to inputted value
 * VRI_PlayerLevelAtLeast <value>
*/
public class VRIPlayerLevelAtLeast extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        float playerlevel = (float)Global.getSector().getPlayerPerson().getStats().getLevel();
        float comparelevel = params.get(0).getFloat(memoryMap);

        if (playerlevel >= comparelevel){
            return true;
        } else {
            return false;
        }
    }
}
