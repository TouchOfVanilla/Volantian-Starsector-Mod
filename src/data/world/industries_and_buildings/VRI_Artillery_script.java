package data.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.campaign.ids.Strings;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class VRI_Artillery_script extends BaseIndustry {

    private static final float DEFENSE_BONUS = 4f;
    private static final float POP_GROWTH = 10f;

    public static List<String> SUPPRESSED_CONDITIONS = new ArrayList();

    static {
        SUPPRESSED_CONDITIONS.add("meteor_impacts");
    }

    @Override
    public boolean isFunctional() {
        return super.isFunctional();
    }

    @Override
    public void apply() {
        super.apply(true);

        for(String s:SUPPRESSED_CONDITIONS){
            if(market.hasCondition(s)){
                market.suppressCondition(s);
            }
        }

        int size = market.getSize();

        this.demand(Commodities.CREW, size);
        this.demand(Commodities.HEAVY_MACHINERY, size);
        this.demand(Commodities.VOLATILES, size);

        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult("Aegis Artillery Network",1.5f);
    }
    @Override
    public void unapply() {
        super.unapply();

        for(String s:SUPPRESSED_CONDITIONS){
            if(market.hasCondition(s)){
                market.unsuppressCondition(s);
            }
        }
    }
    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            Color h = Misc.getHighlightColor();
            float opad = 10f;

            tooltip.addPara("Increases colony ground defenses by %s",opad, h, "1.5x");
            tooltip.addPara("Counters the effects of %s.",opad, h, "Meteor Impacts");
        }
    }
    @Override
    protected int getBaseStabilityMod() {
        return 3;
    }

    public String getNameForModifier() {

        return "Aegis Artillery Network";
    }
    @Override
    public String getCurrentImage() {
        return super.getCurrentImage();
    }
    public boolean isDemandLegal(CommodityOnMarketAPI com) {
        return true;
    }
    public boolean isSupplyLegal(CommodityOnMarketAPI com) {
        return true;
    }
    @Override
    protected void buildingFinished() {
        super.buildingFinished();
    }
    @Override
    protected void upgradeFinished(Industry previous) {
        super.upgradeFinished(previous);
    }
    @Override
    public void advance(float amount) {
        super.advance(amount);

    }
    public static float ALPHA_CORE_BONUS = 0.5f;
    @Override
    protected void applyAlphaCoreModifiers() {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).modifyMult(
                getModId(1), 1f + ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
    }
    @Override
    protected void applyNoAICoreModifiers() {
        market.getStats().getDynamic().getMod(Stats.GROUND_DEFENSES_MOD).unmodifyMult(getModId(1));
    }

    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {
        demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
    }

    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }
        float a = ALPHA_CORE_BONUS;
        //String str = Strings.X + (int)Math.round(a * 100f) + "%";
        String str = Strings.X + (1f + a) + "";

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                            "Increases ground defenses by %s.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                    str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                        "Increases ground defenses by %s.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                str);

    }
    @Override
    public boolean isAvailableToBuild() {
        if (!super.isAvailableToBuild()) {
            return false;
        } else {
            return (Global.getSector().getPlayerFaction().knowsIndustry(getId()));
        }

    }
    public boolean showWhenUnavailable() {
        return false;
    }
    @Override
    public boolean canImprove() {
        return false;
    }
    @Override
    public MarketCMD.RaidDangerLevel adjustCommodityDangerLevel(String commodityId, MarketCMD.RaidDangerLevel level) {
        return level.next();
    }
    @Override
    public MarketCMD.RaidDangerLevel adjustItemDangerLevel(String itemId, String data, MarketCMD.RaidDangerLevel level) {
        return level.next();
    }

}