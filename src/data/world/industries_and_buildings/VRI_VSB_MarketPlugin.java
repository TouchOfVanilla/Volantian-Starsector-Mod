package data.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.CommodityOnMarketAPI;
import com.fs.starfarer.api.campaign.econ.SubmarketAPI;
import com.fs.starfarer.api.campaign.impl.items.BlueprintProviderItem;
import com.fs.starfarer.api.impl.campaign.CoreCampaignPluginImpl;
import com.fs.starfarer.api.impl.campaign.DelayedBlueprintLearnScript;
import com.fs.starfarer.api.impl.campaign.submarkets.BaseSubmarketPlugin;
import com.fs.starfarer.api.impl.campaign.submarkets.BlackMarketPlugin;
import com.fs.starfarer.api.impl.campaign.submarkets.OpenMarketPlugin;
import com.fs.starfarer.api.util.Highlights;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;

public class VRI_VSB_MarketPlugin extends BaseSubmarketPlugin {
    public static Logger log = Global.getLogger(VRI_VSB_MarketPlugin.class);

    public VRI_VSB_MarketPlugin() {
    }

    public void init(SubmarketAPI submarket) {
        super.init(submarket);
    }

    public void updateCargoPrePlayerInteraction() {
        float seconds = Global.getSector().getClock().convertToSeconds(this.sinceLastCargoUpdate);
        this.addAndRemoveStockpiledResources(seconds, false, true, true);
        this.sinceLastCargoUpdate = 0.0F;
        if (this.okToUpdateShipsAndWeapons()) {
            this.sinceSWUpdate = 0.0F;
            float stability = this.market.getStabilityValue();
            this.pruneWeapons(0.0F);
            boolean military = Misc.isMilitary(this.market);
            FactionAPI playerfaction = Global.getSector().getPlayerFaction();
            WeightedRandomPicker<String> factionPicker = new WeightedRandomPicker();
            factionPicker.add(this.market.getFactionId(), 6.0F);
            factionPicker.add("independent", 7.0F);
            factionPicker.add("remnant", 4.0f);
            factionPicker.add(this.submarket.getFaction().getId(), 6.0F);

            //The only part of this script written by touchofvanilla
            ///We *salvage* shit, including the corpses of your enemies
            Iterator factionLooker = Global.getSector().getAllFactions().iterator();
            while (factionLooker.hasNext()){
                FactionAPI faction = (FactionAPI)factionLooker.next();
                if(faction.isHostileTo(playerfaction) && !faction.getFactionSpec().isShowInIntelTab()){
                    factionPicker.add(faction.getId(), 3f);
                    this.addShips(faction.getId(), 30f, 0, 0, 0, 0, 0, 0.6f, 1, (FactionAPI.ShipPickMode) null, (FactionDoctrineAPI)null, 5);

                }
            }


            int weapons = 6 + Math.max(0, this.market.getSize() - 1) + (military ? 5 : 0);
            int fighters = 2 + Math.max(0, (this.market.getSize() - 3) / 2) + (military ? 2 : 0);
            weapons = 6 + Math.max(0, this.market.getSize() - 1);
            fighters = 2 + Math.max(0, (this.market.getSize() - 3) / 2);
            this.addWeapons(weapons, weapons + 2, 3, factionPicker);
            this.addFighters(fighters, fighters + 2, 3, factionPicker);
            if (military) {
                weapons = this.market.getSize();
                fighters = Math.max(1, this.market.getSize() / 3);
                this.addWeapons(weapons, weapons + 2, 3, this.market.getFactionId(), false);
                this.addFighters(fighters, fighters + 2, 3, this.market.getFactionId());
            }

            float sMult = 0.5F + Math.max(0.0F, 1.0F - stability / 10.0F) * 0.5F;
            this.getCargo().getMothballedShips().clear();
            float pOther = 0.1F;
            FactionDoctrineAPI doctrine = this.market.getFaction().getDoctrine().clone();
            this.addShips(this.market.getFactionId(), 70.0F * sMult, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, (Float)null, 0.0F, (FactionAPI.ShipPickMode)null, doctrine);
            FactionDoctrineAPI doctrineOverride = this.submarket.getFaction().getDoctrine().clone();
            doctrineOverride.setWarships(4);
            doctrineOverride.setPhaseShips(1);
            doctrineOverride.setCarriers(2);
            doctrineOverride.setCombatFreighterProbability(0F);
            doctrineOverride.setShipSize(5);
            this.addShips(this.submarket.getFaction().getId(), 70.0F, 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, Math.min(1.0F, Misc.getShipQuality(this.market, this.market.getFactionId()) + 0.5F), 0.0F, (FactionAPI.ShipPickMode)null, doctrineOverride, 3);
            this.addShips("independent", 15.0F + 15.0F * sMult, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, this.itemGenRandom.nextFloat() > pOther ? 0.0F : 10.0F, Math.min(1.0F, Misc.getShipQuality(this.market, this.market.getFactionId()) + 0.5F), 0.0F, (FactionAPI.ShipPickMode)null, (FactionDoctrineAPI)null, 4);
            this.addHullMods(4, 1 + this.itemGenRandom.nextInt(3));
        }

        this.getCargo().sort();
    }

    protected Object writeReplace() {
        if (this.okToUpdateShipsAndWeapons()) {
            this.pruneWeapons(0.0F);
            this.getCargo().getMothballedShips().clear();
        }

        return this;
    }

    public int getStockpileLimit(CommodityOnMarketAPI com) {
        float limit = OpenMarketPlugin.getBaseStockpileLimit(com);
        Random random = new Random((long)(this.market.getId().hashCode() + this.submarket.getSpecId().hashCode() + Global.getSector().getClock().getMonth() * 170000));
        limit *= 0.9F + 0.2F * random.nextFloat();
        float sm = 1.0F - this.market.getStabilityValue() * 10.0F;
        limit *= 0.25F + 0.75F * sm;
        if (limit < 0.0F) {
            limit = 0.0F;
        }

        return (int)limit;
    }

    public SubmarketPlugin.PlayerEconomyImpactMode getPlayerEconomyImpactMode() {
        return SubmarketPlugin.PlayerEconomyImpactMode.NONE;
    }

    public float getDesiredCommodityQuantity(CommodityOnMarketAPI com) {
        boolean illegal = this.market.isIllegal(com.getId());
        if (illegal) {
            return com.getStockpile();
        } else {
            float blackMarketLegalFraction = 1.0F - 0.09F * this.market.getStabilityValue();
            return com.getStockpile() * blackMarketLegalFraction;
        }
    }

    public void reportPlayerMarketTransaction(PlayerMarketTransaction transaction) {
        super.reportPlayerMarketTransaction(transaction);
        FactionAPI faction = this.submarket.getFaction();
        delayedLearnBlueprintsFromTransaction(faction, this.getCargo(), transaction, 60.0F + 60.0F * (float)Math.random());
    }

    public static void delayedLearnBlueprintsFromTransaction(FactionAPI faction, CargoAPI cargo, PlayerMarketTransaction transaction) {
        delayedLearnBlueprintsFromTransaction(faction, cargo, transaction, 60.0F + 60.0F * (float)Math.random());
    }

    public static void delayedLearnBlueprintsFromTransaction(FactionAPI faction, CargoAPI cargo, PlayerMarketTransaction transaction, float daysDelay) {
        DelayedBlueprintLearnScript script = new DelayedBlueprintLearnScript(faction.getId(), daysDelay);
        Iterator var6 = transaction.getSold().getStacksCopy().iterator();

        while(true) {
            CargoStackAPI stack;
            SpecialItemPlugin plugin;
            do {
                if (!var6.hasNext()) {
                    if (!script.getFighters().isEmpty() || !script.getWeapons().isEmpty() || !script.getShips().isEmpty() || !script.getIndustries().isEmpty()) {
                        Global.getSector().addScript(script);
                        cargo.sort();
                    }

                    return;
                }

                stack = (CargoStackAPI)var6.next();
                plugin = stack.getPlugin();
            } while(!(plugin instanceof BlueprintProviderItem));

            BlueprintProviderItem bpi = (BlueprintProviderItem)plugin;
            boolean learnedSomething = false;
            String id;
            Iterator var11;
            if (bpi.getProvidedFighters() != null) {
                var11 = bpi.getProvidedFighters().iterator();

                while(var11.hasNext()) {
                    id = (String)var11.next();
                    if (!faction.knowsFighter(id)) {
                        script.getFighters().add(id);
                        learnedSomething = true;
                    }
                }
            }

            if (bpi.getProvidedWeapons() != null) {
                var11 = bpi.getProvidedWeapons().iterator();

                while(var11.hasNext()) {
                    id = (String)var11.next();
                    if (!faction.knowsWeapon(id)) {
                        script.getWeapons().add(id);
                        learnedSomething = true;
                    }
                }
            }

            if (bpi.getProvidedShips() != null) {
                var11 = bpi.getProvidedShips().iterator();

                while(var11.hasNext()) {
                    id = (String)var11.next();
                    if (!faction.knowsShip(id)) {
                        script.getShips().add(id);
                        learnedSomething = true;
                    }
                }
            }

            if (bpi.getProvidedIndustries() != null) {
                var11 = bpi.getProvidedIndustries().iterator();

                while(var11.hasNext()) {
                    id = (String)var11.next();
                    if (!faction.knowsIndustry(id)) {
                        script.getIndustries().add(id);
                        learnedSomething = true;
                    }
                }
            }

            if (learnedSomething) {
                cargo.removeItems(stack.getType(), stack.getData(), 1.0F);
            }
        }
    }

    public boolean isIllegalOnSubmarket(CargoStackAPI stack, SubmarketPlugin.TransferAction action) {
        return false;
    }

    public boolean isIllegalOnSubmarket(String commodityId, SubmarketPlugin.TransferAction action) {
        return false;
    }

    public float getTariff() {
        return 0.25F;
    }

    public boolean isBlackMarket() {
        return false;
    }

    public String getTooltipAppendix(CoreUIAPI ui) {
        if (this.isEnabled(ui)) {
            float p = CoreCampaignPluginImpl.computeSmugglingSuspicionLevel(this.market);
            if (p < 0.05F) {
                return "Suspicion level: none";
            } else if (p < 0.1F) {
                return "Suspicion level: minimal";
            } else if (p < 0.2F) {
                return "Suspicion level: medium";
            } else if (p < 0.3F) {
                return "Suspicion level: high";
            } else {
                return p < 0.5F ? "Suspicion level: very high" : "Suspicion level: extreme";
            }
        } else {
            return null;
        }
    }

    public Highlights getTooltipAppendixHighlights(CoreUIAPI ui) {
        String appendix = this.getTooltipAppendix(ui);
        if (appendix == null) {
            return null;
        } else {
            Highlights h = new Highlights();
            h.setText(new String[]{appendix});
            h.setColors(new Color[]{Misc.getNegativeHighlightColor()});
            return h;
        }
    }
}
