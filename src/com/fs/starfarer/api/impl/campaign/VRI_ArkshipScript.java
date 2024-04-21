package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.EveryFrameScriptWithCleanup;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignEventListener;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_ModPlugin;
import data.world.systems.Royce;
import exerelin.campaign.intel.invasion.InvasionIntel;
import exerelin.plugins.ExerelinCampaignPlugin;
import exerelin.utilities.*;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

import static com.fs.starfarer.api.Global.getSector;

public class VRI_ArkshipScript implements EconomyTickListener {
    public static Boolean isPatrol = false;

    private static Logger log = Global.getLogger(VRI_ArkshipScript.class);


    public static void ArkshipMoveScript(MarketAPI market, SectorEntityToken ark, Boolean patrol) {
        isPatrol = patrol;
        float maxdist = 0f;
        float maxradius = 0f;
        StarSystemAPI destination = market.getStarSystem();
        log.info("arkship destination found, " + destination.getName());
        MarketAPI ontos = Global.getSector().getEconomy().getMarket(ark.getMarket().getId());
        ontos.removeIndustry(Industries.STARFORTRESS_HIGH, null, false);
        ontos.getConnectedEntities().clear();
        ark.getStarSystem().removeEntity(ark);
        SectorEntityToken CyrosleeperStructure = destination.addCustomEntity("vri_cryosleeper", "Arkship \"Ontos-117\"", "vri_cryosleeper_station", "vri");

        Iterator planetiter = market.getStarSystem().getPlanets().iterator();
        while (planetiter.hasNext()) {
            PlanetAPI planet = (PlanetAPI) planetiter.next();
            if (planet.getCircularOrbitRadius() > maxdist) {
                maxdist = planet.getCircularOrbitRadius();
            }
            if (planet.getRadius() > maxradius){
                maxradius = planet.getRadius();
            }
        }

        float sleeperdist = maxdist + maxradius + 500;
        CyrosleeperStructure.setCircularOrbitPointingDown(market.getStarSystem().getStar(), 360f * (float) Math.random(), sleeperdist, 120);
        CyrosleeperStructure.setMarket(ontos);
        NexUtils.addExpiringIntel(ArkshipMoveNotif(destination));
        ontos.setPrimaryEntity(CyrosleeperStructure);
        ontos.addIndustry(Industries.STARFORTRESS_HIGH);
    }

    public static MessageIntel ArkshipMoveNotif(StarSystemAPI system) {

        MessageIntel intel = new MessageIntel("Mothership Relocation - " + system.getName(), Global.getSector().getFaction("vri").getBaseUIColor());
        intel.addLine(BaseIntelPlugin.BULLET + "Arkship Ontos has relocated to the " + system.getName(),
                Misc.getTextColor(),
                new String[]{"" + system.getName()},
                Misc.getHighlightColor());
        if (!isPatrol) {
            intel.addLine(BaseIntelPlugin.BULLET + "Responding to in-system military operations",
                    Misc.getTextColor(),
                    new String[]{"military operations"},
                    Misc.getHighlightColor());
        }
        intel.setIcon(Global.getSector().getFaction("vri").getCrest());
        intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        return intel;
    }

    public static StarSystemAPI getArkshipDestination(MarketAPI market) {
        ArrayList<StarSystemAPI> validsyslist = new ArrayList<>();
        if (isPatrol) {
            Iterator marketiter = getSector().getEconomy().getMarketsCopy().iterator();
            while (marketiter.hasNext()) {
                MarketAPI schmarket = (MarketAPI) marketiter.next();
                if (schmarket.getFaction() == getSector().getFaction("vri")) {
                    if (schmarket.getStarSystem().getEntityById("vri_cryosleeper") == null) {
                        validsyslist.add(schmarket.getStarSystem());
                    }
                }
            }
            return validsyslist.get(MathUtils.getRandomNumberInRange(0, validsyslist.size()) - 1);
        }
        if (!isPatrol) {
            return market.getStarSystem();
        }
        return null;
    }

    @Override
    public void reportEconomyTick(int iterIndex) {

    }

    @Override
    public void reportEconomyMonthEnd() {
        log.info("Month End, arkship check");
        float patrolprob = 25F;
        float prob = MathUtils.getRandomNumberInRange(1, 100);
        IntelManagerAPI manager = getSector().getIntelManager();
        Iterator inteliter = manager.getIntel().iterator();
        while (inteliter.hasNext()) {
            IntelInfoPlugin intel = (IntelInfoPlugin) inteliter.next();
            if (intel instanceof InvasionIntel) {
                if (((InvasionIntel) intel).getTargetFaction() == getSector().getFaction("vri")) {
                    ArkshipMoveScript(((InvasionIntel) intel).getTarget(), getSector().getEntityById("vri_cryosleeper"), false);
                    return;
                }
                if (((InvasionIntel) intel).getFaction() == getSector().getFaction("vri")) {
                    ArkshipMoveScript(((InvasionIntel) intel).getTarget(), getSector().getEntityById("vri_cryosleeper"), false);
                    return;
                }
            }
        }
        if (patrolprob > prob) {
            Iterator marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
            while (marketiter.hasNext()) {
                MarketAPI market = (MarketAPI) marketiter.next();
                if (market.getFaction().getId().equals("vri") && MathUtils.getRandomNumberInRange(1, 100) > 75) {
                    ArkshipMoveScript(market, getSector().getEntityById("vri_cryosleeper"), true);
                    return;
                }

            }
        }
    }
}