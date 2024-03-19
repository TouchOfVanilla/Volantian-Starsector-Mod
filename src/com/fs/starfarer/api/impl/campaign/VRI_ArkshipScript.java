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
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_ModPlugin;
import exerelin.campaign.intel.invasion.InvasionIntel;
import exerelin.plugins.ExerelinCampaignPlugin;
import exerelin.utilities.*;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;

public abstract class VRI_ArkshipScript implements EconomyTickListener {

    //0 == Patrol
    //1 == Offensive
    //2 == Defensive
    public static Boolean isPatrol = false;

    private static Logger log = Global.getLogger(VRI_ArkshipScript.class);


    public static void ArkshipMoveScript(MarketAPI market, SectorEntityToken ark, Boolean patrol){
        isPatrol = patrol;
        StarSystemAPI destination = getArkshipDestination(market);
        log.info("arkship destination found, " + destination.getName());

        Iterator planetiter = destination.getPlanets().iterator();
        while (planetiter.hasNext()){
            PlanetAPI planet = (PlanetAPI) planetiter.next();
            if (planet.getMarket().getFaction() == null){
                MarketAPI ontos = ark.getMarket();
                ontos.getConnectedEntities().remove(ark);
                ark.getStarSystem().removeEntity(ark);

                SectorEntityToken CyrosleeperStructure = destination.addCustomEntity("vri_cryosleeper", "Arkship \"Ontos-117\"", "vri_cryosleeper_station", "vri");
                CyrosleeperStructure.setCircularOrbitPointingDown(planet, 360f * (float) Math.random(), planet.getRadius() + 200, 200);
                ontos.getConnectedEntities().add(CyrosleeperStructure);

                NexUtils.addExpiringIntel(ArkshipMoveNotif(market.getStarSystem()));
                return;
            }
        }
    }
    public static MessageIntel ArkshipMoveNotif(StarSystemAPI system){

        MessageIntel intel = new MessageIntel("Mothership Relocation - " + system.getName(), Global.getSector().getFaction("vri").getBaseUIColor());
        intel.addLine(BaseIntelPlugin.BULLET + "Arkship Ontos has relocated to the " + system.getName(),
                Misc.getTextColor(),
                new String[] {"" + system.getName()},
                Misc.getHighlightColor());
        if(!isPatrol) {
            intel.addLine(BaseIntelPlugin.BULLET + "This is likely due to Volantian military operations in the system.",
                    Misc.getTextColor(),
                    new String[]{"military operations"},
                    Misc.getHighlightColor());

            intel.setIcon(Global.getSector().getFaction("vri").getCrest());
            intel.setSound(BaseIntelPlugin.getSoundMajorPosting());
        }
        return intel;
    }

    public static StarSystemAPI getArkshipDestination(MarketAPI market) {
        ArrayList<StarSystemAPI> validsyslist = new ArrayList<>();
        if (isPatrol) {
            Iterator marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
            while (marketiter.hasNext()) {
                MarketAPI schmarket = (MarketAPI) marketiter.next();
                if (schmarket.getFaction() == Global.getSector().getFaction("vri")) {
                    if (schmarket.getStarSystem().getEntityById("vri_cryosleeper") != null) {
                        validsyslist.add(schmarket.getStarSystem());
                    }
                }
            }
            return validsyslist.get(validsyslist.size() - 1);
        }
        if (!isPatrol) {
            return market.getStarSystem();
        }
        return null;
    }

    @Override
    public void reportEconomyMonthEnd() {
        log.info("Month End, arkship check");
        float patrolprob = 100f;
        float prob = MathUtils.getRandomNumberInRange(1, 100);
        IntelManagerAPI manager = Global.getSector().getIntelManager();
        Iterator inteliter = manager.getIntel().iterator();
        while (inteliter.hasNext()){
            IntelInfoPlugin intel = (IntelInfoPlugin) inteliter.next();
            if (intel instanceof InvasionIntel){
                if (((InvasionIntel) intel).getTargetFaction() == Global.getSector().getFaction("vri")){
                    ArkshipMoveScript(((InvasionIntel) intel).getTarget(), Global.getSector().getEntityById("vri_cryosleeper"), false);
                    return;
                }
                if (((InvasionIntel) intel).getFaction() == Global.getSector().getFaction("vri")){
                    ArkshipMoveScript(((InvasionIntel) intel).getTarget(), Global.getSector().getEntityById("vri_cryosleeper"), false);
                    return;
                }
            }
        }
        if (patrolprob > prob){
            ArkshipMoveScript(null, Global.getSector().getEntityById("vri_cryosleeper"), true);
        }
    }
}