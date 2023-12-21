package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CustomCampaignEntityPlugin;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_ModPlugin;
import org.apache.log4j.Logger;

import java.util.List;
import java.util.Map;

public class VRIDerelictIsVestie extends BaseCommandPlugin {
    private static Logger log = Global.getLogger(VRIDerelictIsVestie.class);
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
                if ((dialog.getInteractionTarget().getCustomPlugin() != null)){
                    log.info("custom plugin is not null");
                    if (dialog.getInteractionTarget().getCustomPlugin() instanceof DerelictShipEntityPlugin){
                        log.info("custom plugin is a derelict ship");
                        if (((DerelictShipEntityPlugin) (dialog.getInteractionTarget().getCustomPlugin())).getData() != null) {
                            log.info("custom plugin data is not null");
                            if (((DerelictShipEntityPlugin) (dialog.getInteractionTarget().getCustomPlugin())).getData().ship.getVariant().getHullSpec().getManufacturer().contains("Vestige")) {
                                log.info("its a vestie");
                                return true;
                            }
                        }
                    }
                }
        return false;
    }
}
