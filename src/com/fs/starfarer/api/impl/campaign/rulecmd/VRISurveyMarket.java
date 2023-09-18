package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;

public class VRISurveyMarket extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        MarketAPI market = Global.getSector().getPlayerFleet().getInteractionTarget().getMarket();
        market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        Global.getSector().getPlayerFleet().getCargo().addCommodity("survey_data_5", 1);
        return false;
    }
}
