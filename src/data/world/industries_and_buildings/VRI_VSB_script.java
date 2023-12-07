package data.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BlueprintProviderItem;
import com.fs.starfarer.api.campaign.impl.items.ModSpecItemPlugin;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.ids.Commodities;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageEntity;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.econ.Submarket;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class VRI_VSB_script extends BaseIndustry {


    public float CalculateConstellationRuins(){
        Constellation constellation = this.market.getStarSystem().getConstellation();
        Float numruins = 0f;
        Iterator starcounter = constellation.getSystems().iterator();
        while (starcounter.hasNext()){
            StarSystemAPI star = (StarSystemAPI) starcounter.next();
            Iterator planetes = star.getPlanets().iterator();
            while (planetes.hasNext()){
                PlanetAPI plant = (PlanetAPI) planetes.next();
                if (plant.hasCondition(Conditions.RUINS_SCATTERED)){
                    numruins = numruins + 1;
                } else if (plant.hasCondition(Conditions.RUINS_WIDESPREAD)){
                    numruins = numruins + 2;
                } else if (plant.hasCondition(Conditions.RUINS_EXTENSIVE)){
                    numruins = numruins + 3;
                } else if (plant.hasCondition(Conditions.RUINS_VAST)){
                    numruins = numruins + 4;
                }
            }
        }
        return numruins;
    }
    public boolean IsAlreadyInConstellation() {
        Iterator starcounter = this.market.getStarSystem().getConstellation().getSystems().iterator();
        while (starcounter.hasNext()) {
            StarSystemAPI star = (StarSystemAPI) starcounter.next();
            Iterator planetes = star.getPlanets().iterator();
            while (planetes.hasNext()) {
                PlanetAPI plant = (PlanetAPI) planetes.next();
                if (plant.getMarket().hasIndustry(this.id)){
                    return true;
                }
            }
        }
        return false;
    }


    @Override
    public void apply() {

        super.apply(true);
        if(!canUpgrade()) {
            this.demand(Commodities.CREW, market.getSize());
            this.demand(Commodities.SHIPS, market.getSize() - 1); ///You find all this stuff salvaging, so reduced demand
            this.demand(Commodities.HEAVY_MACHINERY, market.getSize() - 1);
            this.demand(Commodities.FUEL, market.getSize() - 1);
            this.demand(Commodities.SUPPLIES, market.getSize() - 1);

            this.market.addSubmarket("vsb_market");
        }
    }

    @Override
    public void unapply() {
        super.unapply();
    }

    protected boolean hasPostDemandSection(boolean hasDemand, IndustryTooltipMode mode) {
        return mode != IndustryTooltipMode.NORMAL || isFunctional();
    }
    @Override
    protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, IndustryTooltipMode mode) {
        Constellation constellation = this.market.getStarSystem().getConstellation();
        if (mode != IndustryTooltipMode.NORMAL || isFunctional()) {
            Color h = Misc.getHighlightColor();
            float opad = 10f;

            tooltip.addPara("Performing salvage and techmining operations in the %s constellation.",opad, h, constellation.getName());
            tooltip.addPara("Creates a special submarket selling %s.",opad, h, "rare ships and weapons");
        }
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
    ///Implement this at some point you fucker
    Boolean VanillaIsLazy = true;
    public CargoAPI generateCargoForGatheringPoint(Random random) {
        if (VanillaIsLazy) {
            return null;
        } else {
            float mult = 1;
            float base = this.CalculateConstellationRuins();
            java.util.List<SalvageEntityGenDataSpec.DropData> dropRandom = new ArrayList();
            java.util.List<SalvageEntityGenDataSpec.DropData> dropValue = new ArrayList();
            SalvageEntityGenDataSpec.DropData d = new SalvageEntityGenDataSpec.DropData();
            d.chances = 5;
            d.group = "blueprints_low";
            dropRandom.add(d);
            d = new SalvageEntityGenDataSpec.DropData();
            d.chances = 5;
            d.group = "rare_tech_low";
            d.valueMult = 30F;
            dropRandom.add(d);
            d = new SalvageEntityGenDataSpec.DropData();
            d.chances = 2;
            d.group = "ai_cores3";
            dropRandom.add(d);
            d = new SalvageEntityGenDataSpec.DropData();
            d.chances = 3;
            d.group = "any_hullmod_low";
            dropRandom.add(d);
            d = new SalvageEntityGenDataSpec.DropData();
            d.chances = 17;
            d.group = "weapons2";
            dropRandom.add(d);
            d = new SalvageEntityGenDataSpec.DropData();
            d.group = "basic";
            d.value = 500;
            dropValue.add(d);
            if (mult >= 1.0F) {
                float num = base * (5.0F + random.nextFloat() * 2.0F);
                if (num < 1.0F) {
                    num = 1.0F;
                }

                d = new SalvageEntityGenDataSpec.DropData();
                d.chances = Math.round(num);
                d.group = "techmining_first_find";
                dropRandom.add(d);
            }

            CargoAPI result = SalvageEntity.generateSalvage(random, 1.0F, 1.0F, base * mult, 1.0F, dropValue, dropRandom);
            FactionAPI pf = Global.getSector().getPlayerFaction();
            Iterator var11 = result.getStacksCopy().iterator();

            while(true) {
                CargoStackAPI stack;
                label92:
                while(true) {
                    List list;
                    String id;
                    Iterator var15;
                    BlueprintProviderItem bp;
                    label82:
                    while(true) {
                        label72:
                        while(true) {
                            label62:
                            while(true) {
                                while(var11.hasNext()) {
                                    stack = (CargoStackAPI)var11.next();
                                    if (stack.getPlugin() instanceof BlueprintProviderItem) {
                                        bp = (BlueprintProviderItem)stack.getPlugin();
                                        list = bp.getProvidedShips();
                                        if (list == null) {
                                            break label62;
                                        }

                                        var15 = list.iterator();

                                        while(true) {
                                            if (!var15.hasNext()) {
                                                break label62;
                                            }

                                            id = (String)var15.next();
                                            if (!pf.knowsShip(id)) {
                                                break;
                                            }
                                        }
                                    } else if (stack.getPlugin() instanceof ModSpecItemPlugin) {
                                        ModSpecItemPlugin mod = (ModSpecItemPlugin)stack.getPlugin();
                                        if (pf.knowsHullMod(mod.getModId())) {
                                            result.removeStack(stack);
                                        }
                                    }
                                }

                                return result;
                            }

                            list = bp.getProvidedWeapons();
                            if (list == null) {
                                break;
                            }

                            var15 = list.iterator();

                            while(true) {
                                if (!var15.hasNext()) {
                                    break label72;
                                }

                                id = (String)var15.next();
                                if (!pf.knowsWeapon(id)) {
                                    break;
                                }
                            }
                        }

                        list = bp.getProvidedFighters();
                        if (list == null) {
                            break;
                        }

                        var15 = list.iterator();

                        while(true) {
                            if (!var15.hasNext()) {
                                break label82;
                            }

                            id = (String)var15.next();
                            if (!pf.knowsFighter(id)) {
                                break;
                            }
                        }
                    }

                    list = bp.getProvidedIndustries();
                    if (list == null) {
                        break;
                    }

                    var15 = list.iterator();

                    while(true) {
                        if (!var15.hasNext()) {
                            break label92;
                        }

                        id = (String)var15.next();
                        if (!pf.knowsIndustry(id)) {
                            break;
                        }
                    }
                }

                result.removeStack(stack);
            }
        }
    }
    public boolean isAvailableToBuild() {
        return (Global.getSector().getPlayerFaction().knowsIndustry(getId()) && !IsAlreadyInConstellation());

    }
    public boolean showWhenUnavailable() {
        return true;
    }

    public boolean canImprove() {
        return false;
    }
}