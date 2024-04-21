package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import com.fs.starfarer.api.impl.campaign.NPCHassler;
import com.fs.starfarer.api.impl.campaign.VRI_ArkshipScript;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.FleetTypes;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.group.*;
import com.fs.starfarer.api.impl.campaign.intel.raid.RaidIntel;
import com.fs.starfarer.api.impl.campaign.missions.FleetCreatorMission;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_ModPlugin;
import exerelin.campaign.intel.fleets.RaidListener;
import exerelin.utilities.NexUtilsFaction;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Iterator;
import java.util.Random;

public class VolantianHostileActivityFactor extends BaseHostileActivityFactor implements PlayerColonizationListener, FleetGroupIntel.FGIEventListener, RaidListener {

    public static float VRI_ANGY_LY_DIST = 10f;
    public static String VolantianIncursionDefeated = "$vriIncursion_defeated";

    public VolantianHostileActivityFactor(HostileActivityEventIntel intel) {
        super(intel);
        Global.getSector().getListenerManager().addListener(this);
    }

    public String getProgressStr(BaseEventIntel intel) {
        return "";
    }

    public int getProgress(BaseEventIntel intel) {
        return !checkFactionExists("vri", true) ? 0 : super.getProgress(intel);
    }

    public String getDesc(BaseEventIntel intel) {
        return "Volantian Reclamation";
    }

    public String getNameForThreatList(boolean first) {
        return "Volantian Reclamation";
    }

    public Color getDescColor(BaseEventIntel intel) {
        return this.getProgress(intel) <= 0 ? Misc.getGrayColor() : Global.getSector().getFaction("vri").getBaseUIColor();
    }

    public TooltipMakerAPI.TooltipCreator getMainRowTooltip(BaseEventIntel intel) {
        return new BaseFactorTooltip() {
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                float opad = 10.0F;
                tooltip.addPara("The Volantian Reclamation Initiative seeks enforce its revanchistic claims on worlds in former Volantian systems. Volantian fleets will probe your defenses as the polity prepares for further hostile action.", 0.0F);
            }
        };
    }

    public boolean shouldShow(BaseEventIntel intel) {
        return this.getProgress(intel) > 0;
    }

    public Color getNameColor(float mag) {
        return mag <= 0.0F ? Misc.getGrayColor() : Global.getSector().getFaction("vri").getBaseUIColor();
    }

    public float getSpawnInHyperProbability(StarSystemAPI system) {
        return 0.0F;
    }

    public CampaignFleetAPI createFleet(StarSystemAPI system, Random random) {
        float f = this.intel.getMarketPresenceFactor(system);
        int difficulty = 0 + (int) Math.max(1.0F, (float) Math.round(f * 4.0F));
        difficulty += random.nextInt(6);
        FleetCreatorMission m = new FleetCreatorMission(random);
        m.beginFleet();
        Vector2f loc = system.getLocation();
        String factionId = "vri";
        m.createStandardFleet(difficulty, factionId, loc);
        m.triggerSetFleetType(FleetTypes.PATROL_LARGE);
        m.triggerSetPatrol();
        m.triggerFleetAllowLongPursuit();
        m.triggerMakeLowRepImpact();
        CampaignFleetAPI fleet = m.createFleet();

        return fleet;
    }

    public void addBulletPointForEvent(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage, TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        Color c = Global.getSector().getFaction("vri").getBaseUIColor();
        info.addPara("Imminent Volantian Incursion", initPad, tc, c, new String[]{"Volantian"});
    }

    public void addBulletPointForEventReset(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage, TooltipMakerAPI info, IntelInfoPlugin.ListInfoMode mode, boolean isUpdate, Color tc, float initPad) {
        info.addPara("Volantian Incursion averted", tc, initPad);
    }

    public void addStageDescriptionForEvent(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage, TooltipMakerAPI info) {
        float small = 0.0F;
        float opad = 10.0F;
        small = 8.0F;
        info.addPara("You've received intel that the VRI is planning an incursion towards your colonies encroaching on former Volantian territory. Their goal is to encase your colony in an impenetrable shell of mines, effectively cutting it off from the sector at large.", small, Misc.getNegativeHighlightColor(), new String[]{"cutting it off from the sector at large."});
        LabelAPI label = info.addPara("If the incursion force is defeated, you can expect the Volantian polity to recognize your colonial holdings and tolerate further expansion.", opad);
        Color c = Global.getSector().getFaction("vri").getBaseUIColor();
        stage.beginResetReqList(info, true, "crisis", opad);
        info.addPara("You go to %s and cede ownership of your offending colonies", 0.0F, c, new String[]{"Ontos"});
        info.addPara("You %s your colonies within 10 light years of Volantian star systems", 0.0F, c, new String[]{"abandon", "10 light years"});
        stage.endResetReqList(info, false, "crisis", -1, -1);
        this.addBorder(info, Global.getSector().getFaction("vri").getBaseUIColor());
    }

    public String getEventStageIcon(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        return Global.getSector().getFaction("vri").getCrest();
    }

    public TooltipMakerAPI.TooltipCreator getStageTooltipImpl(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        return stage.id == HostileActivityEventIntel.Stage.HA_EVENT ? this.getDefaultEventTooltip("Volantian Incursion", intel, stage) : null;
    }

    public float getEventFrequency(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        if (stage.id == HostileActivityEventIntel.Stage.HA_EVENT) {

        }

        return 10.0F;
    }

    public void rollEvent(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        HostileActivityEventIntel.HAERandomEventData data = new HostileActivityEventIntel.HAERandomEventData(this, stage);
        stage.rollData = data;
        intel.sendUpdateIfPlayerHasIntel(data, false);
    }

    public boolean fireEvent(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        StarSystemAPI target = findIncursionTarget(intel, stage);
        MarketAPI source = this.getIncursionSource(intel, stage, target);
        if (source != null && target != null) {
            stage.rollData = null;
            return this.startIncursion(source, target, stage, this.getRandomizedStageRandom(3));
        } else {
            return false;
        }
    }

    public static StarSystemAPI findIncursionTarget(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage) {
        Iterator var5 = Misc.getPlayerSystems(false).iterator();
        while (var5.hasNext()) {
            StarSystemAPI system = (StarSystemAPI) var5.next();
            StarSystemAPI vri = RevanchismVolantianHostileActivityCause.getClosestVRISystem(system);
            float dist = Misc.getDistanceLY(vri.getHyperspaceAnchor(), system.getHyperspaceAnchor());
            if (dist <= VRI_ANGY_LY_DIST) {
                return system;
            }
        }
        return null;
    }

    public MarketAPI getIncursionSource(HostileActivityEventIntel intel, BaseEventIntel.EventStageData stage, StarSystemAPI target) {
        FactionAPI vri = Global.getSector().getFaction("vri");
        MarketAPI schmarket = null;

        Iterator<MarketAPI> marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
        while (marketiter.hasNext()) {
            MarketAPI marker = marketiter.next();
            if (marker.getFaction().equals(vri)) {
                if (marker.hasIndustry(Industries.HIGHCOMMAND)) {
                    if (!marker.getIndustry(Industries.HIGHCOMMAND).isDisrupted()) {
                        if (!marker.getName().equals("Arkship Ontos")) return marker;
                    }
                }
                if (marker.hasIndustry(Industries.MILITARYBASE)) {
                    if (!marker.getIndustry(Industries.MILITARYBASE).isDisrupted()) {
                        return marker;
                    }
                }
            }
        }
        return null;
    }

    public boolean startIncursion(MarketAPI source, StarSystemAPI target, BaseEventIntel.EventStageData stage, Random random) {
        GenericRaidFGI.GenericRaidParams rParams = new GenericRaidFGI.GenericRaidParams(new Random(), true);
        rParams.factionId = "vri";
        rParams.source = source;
        rParams.makeFleetsHostile = true;
        rParams.prepDays = 14f + random.nextFloat() * 14f;
        rParams.payloadDays = 27f + 7f * random.nextFloat();
        rParams.raidParams.where = target;
        rParams.noun = "Incursion";

        Iterator<MarketAPI> marketiter = Global.getSector().getEconomy().getMarkets(target).iterator();
        while (marketiter.hasNext()) {
            MarketAPI market = marketiter.next();
            if (!rParams.raidParams.allowedTargets.contains(market) && market.getFaction().equals(Global.getSector().getPlayerFaction())) {
                rParams.raidParams.allowedTargets.add(market);
            }
        }
        int num = NexUtilsFaction.getFactionMarketSizeSum(Global.getSector().getPlayerFaction().getId());
        if (num < 15) num = 15;

        while (marketiter.hasNext()) {
            MarketAPI market = marketiter.next();
            if (market.getFaction().equals(Global.getSector().getPlayerFaction())) {
                rParams.fleetSizes.add(num);
            }
        }
        rParams.fleetSizes.add(num);

        VolantianIncursion incursion = new VolantianIncursion(rParams);
        incursion.setListener(this);
        Global.getSector().getIntelManager().addIntel(incursion);
        source.getFaction().setRelationship(Global.getSector().getPlayerFaction().getId(), -1.0f);
        return true;
    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {

    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {
        StarSystemAPI vri = RevanchismVolantianHostileActivityCause.getClosestVRISystem(colony.getStarSystem());
        float dist = Misc.getDistanceLY(vri.getHyperspaceAnchor(), colony.getStarSystem().getHyperspaceAnchor());
        if (dist <= VRI_ANGY_LY_DIST) {
            this.intel.resetHA_EVENT();
        }
    }

    public void reportFGIAborted(FleetGroupIntel intel) {
        setDefeatedIncursion(true);
        Misc.adjustRep("hegemony", 0.4f, (TextPanelAPI) null);
        Misc.adjustRep("tritachyon", 0.2f, (TextPanelAPI) null);
    }

    public void notifyFactorRemoved() {
        Global.getSector().getListenerManager().removeListener(this);
    }

    public void notifyEventEnding() {
        this.notifyFactorRemoved();
    }

    @Override
    public void reportRaidEnded(RaidIntel intel, FactionAPI attacker, FactionAPI defender, MarketAPI target, boolean success) {

    }
    public static void setDefeatedIncursion(boolean value) {
        Global.getSector().getMemoryWithoutUpdate().set(VolantianIncursionDefeated, value);
        if (!value) {
            Global.getSector().getMemoryWithoutUpdate().unset(VolantianIncursionDefeated);
        }
    }
}
