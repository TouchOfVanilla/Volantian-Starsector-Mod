package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.PluginPick;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.BaseCampaignPlugin;

public class VRICampaignPluginImpl extends BaseCampaignPlugin {
    public PluginPick<AICoreOfficerPlugin> pickAICoreOfficerPlugin(String commodityId) {
        if ("volantian_core".equals(commodityId)) {
            return new PluginPick<AICoreOfficerPlugin>(new VRICoreOfficerPlugin(), PickPriority.MOD_SET);
        }
        if ("vestige_core".equals(commodityId)) {
            return new PluginPick<AICoreOfficerPlugin>(new VRICoreOfficerPlugin(), PickPriority.MOD_SET);
        }
        else{
            return null;
        }
    }
    public PluginPick<AICoreAdminPlugin> pickAICoreAdminPlugin(String commodityId) {
        if ("volantian_core".equals(commodityId)) {
            return new PluginPick<AICoreAdminPlugin>(new VRICoreAdminPlugin(), PickPriority.MOD_SET);
        }
        else{
            return null;
        }
    }
}
