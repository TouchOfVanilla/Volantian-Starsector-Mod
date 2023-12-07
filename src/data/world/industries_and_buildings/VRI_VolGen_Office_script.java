package data.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.*;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.MilitaryBase;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.*;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.population.PopulationComposition;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import data.world.systems.Avery;
import data.world.systems.Royce;
import data.world.systems.Uelyst;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VRI_VolGen_Office_script extends BaseIndustry implements MarketImmigrationModifier {

    private static final float DEFENSE_BONUS = 4f;
    private static final float POP_GROWTH = 10f;

    public static List<String> SUPPRESSED_CONDITIONS = new ArrayList();

    static {
        SUPPRESSED_CONDITIONS.add("hot");
        SUPPRESSED_CONDITIONS.add("very_hot");
        SUPPRESSED_CONDITIONS.add("cold");
        SUPPRESSED_CONDITIONS.add("very_cold");
        SUPPRESSED_CONDITIONS.add("irradiated");
        SUPPRESSED_CONDITIONS.add("toxic_atmosphere");
        SUPPRESSED_CONDITIONS.add("inimical_biosphere");
        SUPPRESSED_CONDITIONS.add("pollution");
        SUPPRESSED_CONDITIONS.add("dark");
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

        this.demand(Commodities.ORGANICS, size+1);
        this.demand(Commodities.HEAVY_MACHINERY, size+1);
        this.demand(Commodities.METALS, size+1);

        this.income.modifyMult(this.id,size,"Market Size");
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

            tooltip.addPara("Increases colony growth by %s",opad, h, String.valueOf(POP_GROWTH));
            tooltip.addPara("Counters the effects of %s, %s, %s, %s, %s, %s, and %s.",opad, h, "Heat","Cold","Irradiated","Toxic Atmosphere", "Pollution", "Inimical Biosphere","Dark");
        }
    }
    @Override
    protected int getBaseStabilityMod() {
        return 0;
    }

    public String getNameForModifier() {

        return "VolGen Office";
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
    @Override
    protected void applyAlphaCoreModifiers() {
        market.getIncomeMult().modifyPercent(getModId(1), ALPHA_CORE_BONUS, "Alpha core (" + getNameForModifier() + ")");
    }

    @Override
    protected void applyNoAICoreModifiers() {
        market.getIncomeMult().unmodifyPercent(getModId(1));
    }

    @Override
    protected void applyAlphaCoreSupplyAndDemandModifiers() {
        demandReduction.modifyFlat(getModId(0), DEMAND_REDUCTION, "Alpha core");
    }
    public static float ALPHA_CORE_BONUS = 25f;
    protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, AICoreDescriptionMode mode) {
        float opad = 10f;
        Color highlight = Misc.getHighlightColor();

        String pre = "Alpha-level AI core currently assigned. ";
        if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            pre = "Alpha-level AI core. ";
        }
        float a = ALPHA_CORE_BONUS;
        String str = "" + (int) Math.round(a) + "%";

        if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
            CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(aiCoreId);
            TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48);
            text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                            "Increases colony income by %s.", 0f, highlight,
                    "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                    str);
            tooltip.addImageWithText(opad);
            return;
        }

        tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " +
                        "Increases colony income by %s.", opad, highlight,
                "" + (int)((1f - UPKEEP_MULT) * 100f) + "%", "" + DEMAND_REDUCTION,
                str);

    }
    Boolean PlayerHasBP = false;
    Boolean VRIOnPlanetAlready = false;

    public boolean isAvailableToBuild() {
        if (Global.getSector().getPlayerFaction().knowsIndustry(getId())){
            PlayerHasBP = true;
        }
        if (this.market.hasIndustry("VRI_VSB_ReclaimerBranch") || this.market.hasIndustry("VRI_VISOC_BlackSite")){
            VRIOnPlanetAlready = true;
        }
        if (!PlayerHasBP){
            return false;
        }
        if (VRIOnPlanetAlready){
            return false;
        }
        return true;
    }

    public boolean showWhenUnavailable() {
        return false;
    }

    public String getUnavailableReason() {
        if (!PlayerHasBP){
            return "You do not have this industry's blueprint.";
        }
        if(VRIOnPlanetAlready){
            return "There are already Volantian assets on this planet";
        }
        return "Congrats you aren't supposed to see this";
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

    @Override
    public void modifyIncoming(MarketAPI market, PopulationComposition incoming) {
        incoming.getWeight().modifyFlat(this.getModId(), POP_GROWTH, "VolGen Branch");
    }
}