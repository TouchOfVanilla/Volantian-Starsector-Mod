package data.world.industries_and_buildings;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.campaign.econ.Industry;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.econ.impl.BaseIndustry;
import com.fs.starfarer.api.impl.campaign.econ.impl.OrbitalStation;
import com.fs.starfarer.api.impl.campaign.events.OfficerManagerEvent;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantOfficerGeneratorPlugin;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.MarketCMD;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.Pair;
import org.json.JSONException;
import org.json.JSONObject;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;

public class NexusStationPlugin extends BaseIndustry implements FleetEventListener{
        public static float DEFENSE_BONUS_BASE = 0.5F;
        public static float DEFENSE_BONUS_BATTLESTATION = 1.0F;
        public static float DEFENSE_BONUS_FORTRESS = 2.0F;
        public static float IMPROVE_STABILITY_BONUS = 1.0F;
        protected CampaignFleetAPI stationFleet = null;
        protected boolean usingExistingStation = false;
        protected SectorEntityToken stationEntity = null;

        public NexusStationPlugin() {
        }

        public void apply() {
            super.apply(false);
            int size = 3;
            boolean battlestation = this.getSpec().hasTag("battlestation");
            boolean starfortress = this.getSpec().hasTag("starfortress");
            if (battlestation) {
                size = 5;
            } else if (starfortress) {
                size = 7;
            }

            this.modifyStabilityWithBaseMod();
            this.applyIncomeAndUpkeep((float)size);
            this.demand("crew", size);
            this.demand("supplies", size);
            float bonus = DEFENSE_BONUS_BASE;
            if (battlestation) {
                bonus = DEFENSE_BONUS_BATTLESTATION;
            } else if (starfortress) {
                bonus = DEFENSE_BONUS_FORTRESS;
            }

            this.market.getStats().getDynamic().getMod("ground_defenses_mod").modifyMult(this.getModId(), 1.0F + bonus, this.getNameForModifier());
            this.matchCommanderToAICore(this.aiCoreId);
            if (!this.isFunctional()) {
                this.supply.clear();
                this.unapply();
            } else {
                this.applyCRToStation();
            }

        }

        public void unapply() {
            super.unapply();
            this.unmodifyStabilityWithBaseMod();
            this.matchCommanderToAICore((String)null);
            this.market.getStats().getDynamic().getMod("ground_defenses_mod").unmodifyMult(this.getModId());
        }

        protected void applyCRToStation() {
            if (this.stationFleet != null) {
                float cr = this.getCR();
                Iterator var3 = this.stationFleet.getFleetData().getMembersListCopy().iterator();

                while(var3.hasNext()) {
                    FleetMemberAPI member = (FleetMemberAPI)var3.next();
                    member.getRepairTracker().setCR(cr);
                }

                FleetInflater inflater = this.stationFleet.getInflater();
                if (inflater != null) {
                    if (this.stationFleet.isInflated()) {
                        this.stationFleet.deflate();
                    }

                    inflater.setQuality(Misc.getShipQuality(this.market));
                    if (inflater instanceof DefaultFleetInflater) {
                        DefaultFleetInflater dfi = (DefaultFleetInflater)inflater;
                        ((DefaultFleetInflaterParams)dfi.getParams()).allWeapons = true;
                    }
                }
            }

        }

        protected float getCR() {
            float deficit = (float)(Integer)this.getMaxDeficit(new String[]{"crew", "supplies"}).two;
            float demand = (float)Math.max(this.getDemand("crew").getQuantity().getModifiedInt(), this.getDemand("supplies").getQuantity().getModifiedInt());
            if (deficit < 0.0F) {
                deficit = 0.0F;
            }

            if (demand < 1.0F) {
                demand = 1.0F;
                deficit = 0.0F;
            }

            float q = Misc.getShipQuality(this.market);
            if (q < 0.0F) {
                q = 0.0F;
            }

            if (q > 1.0F) {
                q = 1.0F;
            }

            float d = (demand - deficit) / demand;
            if (d < 0.0F) {
                d = 0.0F;
            }

            if (d > 1.0F) {
                d = 1.0F;
            }

            float cr = 0.5F + 0.5F * Math.min(d, q);
            if (cr > 1.0F) {
                cr = 1.0F;
            }

            return cr;
        }

        protected boolean hasPostDemandSection(boolean hasDemand, Industry.IndustryTooltipMode mode) {
            return mode != IndustryTooltipMode.NORMAL || this.isFunctional();
        }

        protected void addPostDemandSection(TooltipMakerAPI tooltip, boolean hasDemand, Industry.IndustryTooltipMode mode) {
            if (mode != IndustryTooltipMode.NORMAL || this.isFunctional()) {
                Color h = Misc.getHighlightColor();
                float opad = 10.0F;
                float cr = this.getCR();
                tooltip.addPara("Station combat readiness: %s", opad, h, new String[]{Math.round(cr * 100.0F) + "%"});
                this.addStabilityPostDemandSection(tooltip, hasDemand, mode);
                boolean battlestation = this.getSpec().hasTag("battlestation");
                boolean starfortress = this.getSpec().hasTag("starfortress");
                float bonus = DEFENSE_BONUS_BASE;
                if (battlestation) {
                    bonus = DEFENSE_BONUS_BATTLESTATION;
                } else if (starfortress) {
                    bonus = DEFENSE_BONUS_FORTRESS;
                }

                this.addGroundDefensesImpactSection(tooltip, bonus, new String[]{"supplies"});
            }

        }

        protected Object readResolve() {
            super.readResolve();
            return this;
        }

        public void advance(float amount) {
            super.advance(amount);
            if (!Global.getSector().getEconomy().isSimMode()) {
                if (this.stationEntity == null) {
                    this.spawnStation();
                }

                if (this.stationFleet != null) {
                    this.stationFleet.setAI((CampaignFleetAIAPI)null);
                    if (this.stationFleet.getOrbit() == null && this.stationEntity != null) {
                        this.stationFleet.setCircularOrbit(this.stationEntity, 0.0F, 0.0F, 100.0F);
                    }
                }

            }
        }

        protected void buildingFinished() {
            super.buildingFinished();
            if (this.stationEntity != null && this.stationFleet != null) {
                this.matchStationAndCommanderToCurrentIndustry();
            } else {
                this.spawnStation();
            }

        }

        public void notifyBeingRemoved(MarketAPI.MarketInteractionMode mode, boolean forUpgrade) {
            super.notifyBeingRemoved(mode, forUpgrade);
            if (!forUpgrade) {
                this.removeStationEntityAndFleetIfNeeded();
            }

        }

        protected void upgradeFinished(Industry previous) {

        }

        protected void removeStationEntityAndFleetIfNeeded() {
            if (this.stationEntity != null) {
                this.stationEntity.getMemoryWithoutUpdate().unset("$stationFleet");
                this.stationEntity.getMemoryWithoutUpdate().unset("$stationBaseFleet");
                this.stationEntity.getContainingLocation().removeEntity(this.stationFleet);
                if (this.stationEntity.getContainingLocation() != null && !this.usingExistingStation) {
                    this.stationEntity.getContainingLocation().removeEntity(this.stationEntity);
                    this.market.getConnectedEntities().remove(this.stationEntity);
                } else if (this.stationEntity.hasTag("use_station_visual")) {
                    ((CustomCampaignEntityAPI)this.stationEntity).setFleetForVisual((CampaignFleetAPI)null);
                    float origRadius = ((CustomCampaignEntityAPI)this.stationEntity).getCustomEntitySpec().getDefaultRadius();
                    ((CustomCampaignEntityAPI)this.stationEntity).setRadius(origRadius);
                }

                if (this.stationFleet != null) {
                    this.stationFleet.getMemoryWithoutUpdate().unset("$stationMarket");
                    this.stationFleet.removeEventListener(this);
                }

                this.stationEntity = null;
                this.stationFleet = null;
            }

        }

        public void notifyColonyRenamed() {
            super.notifyColonyRenamed();
            if (!this.usingExistingStation) {
                this.stationFleet.setName(this.market.getName() + " Station");
                this.stationEntity.setName(this.market.getName() + " Station");
            }

        }

        protected void spawnStation() {
            FleetParamsV3 fParams = new FleetParamsV3((MarketAPI)null, (Vector2f)null, this.market.getFactionId(), 1.0F, "patrolSmall", 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            fParams.allWeapons = true;
            this.removeStationEntityAndFleetIfNeeded();
            this.stationFleet = FleetFactoryV3.createFleet(fParams);
            this.stationFleet.setNoFactionInName(true);
            this.stationFleet.setStationMode(true);
            this.stationFleet.clearAbilities();
            this.stationFleet.addAbility("transponder");
            this.stationFleet.getAbility("transponder").activate();
            this.stationFleet.getDetectedRangeMod().modifyFlat("gen", 10000.0F);
            this.stationFleet.setAI((CampaignFleetAIAPI)null);
            this.stationFleet.addEventListener(this);
            this.ensureStationEntityIsSetOrCreated();
            if (this.stationEntity instanceof CustomCampaignEntityAPI && (!this.usingExistingStation || this.stationEntity.hasTag("use_station_visual"))) {
                ((CustomCampaignEntityAPI)this.stationEntity).setFleetForVisual(this.stationFleet);
            }

            this.stationFleet.setCircularOrbit(this.stationEntity, 0.0F, 0.0F, 100.0F);
            this.stationFleet.getMemoryWithoutUpdate().set("$stationMarket", this.market);
            this.stationFleet.setHidden(true);
            this.matchStationAndCommanderToCurrentIndustry();
        }

        protected void ensureStationEntityIsSetOrCreated() {
            SectorEntityToken entity;
            if (this.stationEntity == null) {
                Iterator var2 = this.market.getConnectedEntities().iterator();

                while(var2.hasNext()) {
                    entity = (SectorEntityToken)var2.next();
                    if (entity.hasTag("station") && !entity.hasTag("NO_ORBITAL_STATION")) {
                        this.stationEntity = entity;
                        this.usingExistingStation = true;
                        break;
                    }
                }
            }

            if (this.stationEntity == null) {
                this.stationEntity = this.market.getContainingLocation().addCustomEntity((String)null, this.market.getName() + " Station", "station_built_from_industry", this.market.getFactionId());
                entity = this.market.getPrimaryEntity();
                float orbitRadius = entity.getRadius() + 150.0F;
                this.stationEntity.setCircularOrbitWithSpin(entity, (float)Math.random() * 360.0F, orbitRadius, orbitRadius / 10.0F, 5.0F, 5.0F);
                this.market.getConnectedEntities().add(this.stationEntity);
                this.stationEntity.setMarket(this.market);
            }

        }

        protected void matchStationAndCommanderToCurrentIndustry() {
            this.stationFleet.getFleetData().clear();
            String fleetName = null;
            String variantId = null;
            float radius = 60.0F;

            try {
                JSONObject json = new JSONObject(this.getSpec().getData());
                variantId = json.getString("variant");
                radius = (float)json.getDouble("radius");
                fleetName = json.getString("fleetName");
            } catch (JSONException var10) {
                throw new RuntimeException(var10);
            }

            if (this.stationEntity != null) {
                fleetName = this.stationEntity.getName();
            }

            this.stationFleet.setName(fleetName);
            FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, variantId);
            member.setShipName(fleetName);
            this.stationFleet.getFleetData().addFleetMember(member);
            this.applyCRToStation();
            if (!this.usingExistingStation && this.stationEntity instanceof CustomCampaignEntityAPI) {
                ((CustomCampaignEntityAPI)this.stationEntity).setRadius(radius);
            } else if (this.stationEntity.hasTag("use_station_visual")) {
                ((CustomCampaignEntityAPI)this.stationEntity).setRadius(radius);
            }

            boolean skeletonMode = !this.isFunctional();
            if (skeletonMode) {
                this.stationEntity.getMemoryWithoutUpdate().unset("$stationFleet");
                this.stationEntity.getMemoryWithoutUpdate().set("$stationBaseFleet", this.stationFleet);
                this.stationEntity.getContainingLocation().removeEntity(this.stationFleet);

                for(int i = 1; i < member.getStatus().getNumStatuses(); ++i) {
                    ShipVariantAPI variant = member.getVariant();
                    if (i > 0) {
                        String slotId = (String)member.getVariant().getModuleSlots().get(i - 1);
                        variant = variant.getModuleVariant(slotId);
                        if (!variant.hasHullMod("vastbulk")) {
                            member.getStatus().setDetached(i, true);
                            member.getStatus().setPermaDetached(i, true);
                            member.getStatus().setHullFraction(i, 0.0F);
                        }
                    }
                }
            } else {
                this.stationEntity.getMemoryWithoutUpdate().unset("$stationBaseFleet");
                this.stationEntity.getMemoryWithoutUpdate().set("$stationFleet", this.stationFleet);
                this.stationEntity.getContainingLocation().removeEntity(this.stationFleet);
                this.stationFleet.setExpired(false);
                this.stationEntity.getContainingLocation().addEntity(this.stationFleet);
            }

        }

        protected int getHumanCommanderLevel() {
            boolean battlestation = this.getSpec().hasTag("battlestation");
            boolean starfortress = this.getSpec().hasTag("starfortress");
            if (starfortress) {
                return Global.getSettings().getInt("tier3StationOfficerLevel");
            } else {
                return battlestation ? Global.getSettings().getInt("tier2StationOfficerLevel") : Global.getSettings().getInt("tier1StationOfficerLevel");
            }
        }

        protected void matchCommanderToAICore(String aiCore) {
            if (this.stationFleet != null) {
                PersonAPI commander = null;
                if ("alpha_core".equals(aiCore)) {
                    AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin("alpha_core");
                    commander = plugin.createPerson("alpha_core", "remnant", (Random)null);
                    if (this.stationFleet.getFlagship() != null) {
                        RemnantOfficerGeneratorPlugin.integrateAndAdaptCoreForAIFleet(this.stationFleet.getFlagship());
                    }
                } else if (this.stationFleet.getFlagship() != null) {
                    int level = this.getHumanCommanderLevel();
                    PersonAPI current = this.stationFleet.getFlagship().getCaptain();
                    if (level > 0) {
                        if (current.isAICore() || current.getStats().getLevel() != level) {
                            commander = OfficerManagerEvent.createOfficer(Global.getSector().getFaction(this.market.getFactionId()), level, true);
                        }
                    } else if (this.stationFleet.getFlagship() == null || this.stationFleet.getFlagship().getCaptain() == null || !this.stationFleet.getFlagship().getCaptain().isDefault()) {
                        commander = Global.getFactory().createPerson();
                    }
                }

                if (commander != null && this.stationFleet.getFlagship() != null) {
                    this.stationFleet.getFlagship().setCaptain(commander);
                    this.stationFleet.getFlagship().setFlagship(false);
                }

            }
        }

        public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
        }

        protected void disruptionFinished() {
            super.disruptionFinished();
            this.matchStationAndCommanderToCurrentIndustry();
        }

        protected void notifyDisrupted() {
            super.notifyDisrupted();
            this.matchStationAndCommanderToCurrentIndustry();
        }

        public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
            if (fleet == this.stationFleet) {
                disrupt(this);
                if (this.stationFleet.getMembersWithFightersCopy().isEmpty()) {
                    this.matchStationAndCommanderToCurrentIndustry();
                }

                this.stationFleet.setAbortDespawn(true);
            }
        }

        public static void disrupt(Industry station) {
            station.setDisrupted(station.getSpec().getBuildTime() * 0.5F, true);
        }

        public boolean isAvailableToBuild() {
        return false;
        }
        public boolean showWhenUnavailable() {
        return false;
    }
        public String getUnavailableReason() {
            return "Requires a functional spaceport";
        }

        protected int getBaseStabilityMod() {
            boolean battlestation = this.getSpec().hasTag("battlestation");
            boolean starfortress = this.getSpec().hasTag("starfortress");
            int stabilityMod = 1;
            if (battlestation) {
                stabilityMod = 2;
            } else if (starfortress) {
                stabilityMod = 3;
            }

            return stabilityMod;
        }

        protected Pair<String, Integer> getStabilityAffectingDeficit() {
            return this.getMaxDeficit(new String[]{"supplies", "crew"});
        }

        protected void applyAlphaCoreModifiers() {
        }

        protected void applyNoAICoreModifiers() {
        }

        protected void applyAlphaCoreSupplyAndDemandModifiers() {
            this.demandReduction.modifyFlat(this.getModId(0), (float)DEMAND_REDUCTION, "Alpha core");
        }

        protected void addAlphaCoreDescription(TooltipMakerAPI tooltip, Industry.AICoreDescriptionMode mode) {
            float opad = 10.0F;
            Color highlight = Misc.getHighlightColor();
            String pre = "Alpha-level AI core currently assigned. ";
            if (mode == AICoreDescriptionMode.MANAGE_CORE_DIALOG_LIST || mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
                pre = "Alpha-level AI core. ";
            }

            if (mode == AICoreDescriptionMode.INDUSTRY_TOOLTIP) {
                CommoditySpecAPI coreSpec = Global.getSettings().getCommoditySpec(this.aiCoreId);
                TooltipMakerAPI text = tooltip.beginImageWithText(coreSpec.getIconName(), 48.0F);
                text.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " + "Increases station combat effectiveness.", 0.0F, highlight, new String[]{(int)((1.0F - UPKEEP_MULT) * 100.0F) + "%", "" + DEMAND_REDUCTION});
                tooltip.addImageWithText(opad);
            } else {
                tooltip.addPara(pre + "Reduces upkeep cost by %s. Reduces demand by %s unit. " + "Increases station combat effectiveness.", opad, highlight, new String[]{(int)((1.0F - UPKEEP_MULT) * 100.0F) + "%", "" + DEMAND_REDUCTION});
            }
        }

        public boolean canImprove() {
            return true;
        }

        protected void applyImproveModifiers() {
            if (this.isImproved()) {
                this.market.getStability().modifyFlat("orbital_station_improve", IMPROVE_STABILITY_BONUS, this.getImprovementsDescForModifiers() + " (" + this.getNameForModifier() + ")");
            } else {
                this.market.getStability().unmodifyFlat("orbital_station_improve");
            }

        }

        public void addImproveDesc(TooltipMakerAPI info, Industry.ImprovementDescriptionMode mode) {
            float opad = 10.0F;
            Color highlight = Misc.getHighlightColor();
            if (mode == ImprovementDescriptionMode.INDUSTRY_TOOLTIP) {
                info.addPara("Stability increased by %s.", 0.0F, highlight, new String[]{"" + (int)IMPROVE_STABILITY_BONUS});
            } else {
                info.addPara("Increases stability by %s.", 0.0F, highlight, new String[]{"" + (int)IMPROVE_STABILITY_BONUS});
            }

            info.addSpacer(opad);
            super.addImproveDesc(info, mode);
        }

        protected boolean isMiltiarized() {
            boolean battlestation = this.getSpec().hasTag("battlestation");
            boolean starfortress = this.getSpec().hasTag("starfortress");
            return battlestation || starfortress;
        }

        public MarketCMD.RaidDangerLevel adjustCommodityDangerLevel(String commodityId, MarketCMD.RaidDangerLevel level) {
            return !this.isMiltiarized() ? level : level.next();
        }

        public MarketCMD.RaidDangerLevel adjustItemDangerLevel(String itemId, String data, MarketCMD.RaidDangerLevel level) {
            return !this.isMiltiarized() ? level : level.next();
        }
    }
