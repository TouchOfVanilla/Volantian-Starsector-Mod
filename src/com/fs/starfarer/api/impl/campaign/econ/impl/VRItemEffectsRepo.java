package com.fs.starfarer.api.impl.campaign.econ.impl;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SpecialItemData;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.InstallableIndustryItemPlugin;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.HashMap;
import java.util.Map;

import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.COLD_OR_EXTREME_COLD;

public class VRItemEffectsRepo {

    public static void addItemEffectsToVanillaRepo() {
        ItemEffectsRepo.ITEM_EFFECTS.putAll(ITEM_EFFECTS);
    }

    public static float VRI_SKYFORGE_ACCESS_BONUS  = 0.2f;

    public static String VRI_Skyforge_Access_Bonus;
    public static String NOT_HIGH_GRAVITY = "does not have high gravity";

    public static Map<String, InstallableItemEffect> ITEM_EFFECTS = new HashMap<String, InstallableItemEffect>() {
        {

            put("VRI_Skyforge", new BaseInstallableItemEffect("VRI_Skyforge") {

                public void apply(Industry industry) {
                        industry.getMarket().suppressCondition("tectonic_activity");
                        industry.getMarket().suppressCondition("extreme_tectonic_activity");
                        industry.getMarket().suppressCondition("toxic_atmosphere");
                        industry.getMarket().suppressCondition("extreme_weather");
                        industry.getMarket().suppressCondition("inimical_biosphere");
                        industry.getMarket().suppressCondition("water_surface");
                    industry.getMarket().getAccessibilityMod().modifyFlat(VRI_Skyforge_Access_Bonus, VRI_SKYFORGE_ACCESS_BONUS);
                }

                public void unapply(Industry industry) {
                        industry.getMarket().unsuppressCondition("tectonic_activity");
                        industry.getMarket().unsuppressCondition("extreme_tectonic_activity");
                        industry.getMarket().unsuppressCondition("toxic_atmosphere");
                        industry.getMarket().unsuppressCondition("extreme_weather");
                        industry.getMarket().unsuppressCondition("inimical_biosphere");
                        industry.getMarket().unsuppressCondition("water_surface");
                    industry.getMarket().getAccessibilityMod().unmodifyFlat(VRI_Skyforge_Access_Bonus);
                }

                protected void addItemDescriptionImpl(Industry industry, TooltipMakerAPI text, SpecialItemData data,
                                                      InstallableIndustryItemPlugin.InstallableItemDescriptionMode mode, String pre, float pad) {

                    text.addPara(pre + "Counters the effects of Extreme Weather, Inmical Biosphere, Water Surface, Toxic Atmosphere, and Tectonic Activity." +
                                    " Increases Accessibility by " + VRI_SKYFORGE_ACCESS_BONUS,
                            pad, Misc.getHighlightColor(),
                            new String[]{VRI_SKYFORGE_ACCESS_BONUS + "Counters"});
                }
                @Override
                public String[] getSimpleReqs(Industry industry) {
                    return new String [] {NOT_HIGH_GRAVITY};
                }


            });
        }
    };
}
