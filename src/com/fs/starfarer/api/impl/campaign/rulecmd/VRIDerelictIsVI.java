package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class VRIDerelictIsVI extends BaseCommandPlugin {
    private static Logger log = Global.getLogger(VRIDerelictIsVI.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if ((dialog.getInteractionTarget().getCustomPlugin() != null)){
            log.info("custom plugin is not null");
            if (dialog.getInteractionTarget().getCustomPlugin() instanceof DerelictShipEntityPlugin){
                log.info("custom plugin is a derelict ship");
                if (((DerelictShipEntityPlugin) (dialog.getInteractionTarget().getCustomPlugin())).getData() != null) {
                    log.info("custom plugin data is not null");
                    if (((DerelictShipEntityPlugin) (dialog.getInteractionTarget().getCustomPlugin())).getData().ship.getVariant().getHullSpec().isBuiltInMod("sixth")) {
                        log.info("its a vi bote");
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
