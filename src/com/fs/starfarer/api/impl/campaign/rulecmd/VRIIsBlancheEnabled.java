package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRILunaSettings;

import java.util.List;
import java.util.Map;

public class VRIIsBlancheEnabled extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (Global.getSettings().getModManager().isModEnabled("lunalib")){
        return VRILunaSettings.BlancheBoolean();
        }
        return true;
    }
}
