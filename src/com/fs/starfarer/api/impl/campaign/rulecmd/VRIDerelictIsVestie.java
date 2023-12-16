package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class VRIDerelictIsVestie extends BaseCommandPlugin {
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

            if (((DerelictShipEntityPlugin)(dialog.getInteractionTarget().getCustomPlugin())).getData().ship.getVariant().getHullSpec().getManufacturer().contains("Vestige")) {
                return true;
            }

        return false;
    }
}
