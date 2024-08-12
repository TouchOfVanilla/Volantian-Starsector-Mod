package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.EveryFrameScriptWithCleanup;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.comm.CommMessageAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.comm.IntelManagerAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.EconomyTickListener;
import com.fs.starfarer.api.impl.campaign.ids.Industries;
import com.fs.starfarer.api.impl.campaign.intel.BaseIntelPlugin;
import com.fs.starfarer.api.impl.campaign.intel.MessageIntel;
import com.fs.starfarer.api.impl.campaign.intel.misc.FleetLogIntel;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.Nex_MarketCMD;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRILunaSettings;
import data.scripts.VRI_ModPlugin;
import data.world.systems.Royce;
import exerelin.campaign.InvasionRound;
import exerelin.campaign.intel.invasion.InvasionIntel;
import exerelin.plugins.ExerelinCampaignPlugin;
import exerelin.utilities.*;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.fs.starfarer.api.Global.getSector;

public class VRI_ArkshipScript implements EconomyTickListener, InvasionListener {
    public static Boolean isPatrol = false;

    private static Logger log = Global.getLogger(VRI_ArkshipScript.class);


    public static void ArkshipMoveScript(MarketAPI market, SectorEntityToken ark, Boolean patrol) {
        isPatrol = patrol;
        float maxdist = 0f;
        float maxradius = 0f;
        StarSystemAPI destination = market.getStarSystem();
        log.info("arkship destination found, " + destination.getName());

        if (ark == null){
            log.info("arkship entity does not exist, player is in non-standard sector");
            return;
        }

        MarketAPI ontos = Global.getSector().getEconomy().getMarket(ark.getMarket().getId());

        if (VRILunaSettings.ArkshipBoolean() == false){
            log.info("arkship movement disabled via lunasettings, aborting");
            return;
        }
        if (ontos == null) {
            log.info("arkship market no longer exists, aborting");
            return;
        }
        if (!ontos.getFactionId().equals("vri")) {
            log.info("arkship no longer belongs to vri, aborting");
            return;
        }
        if (ontos.getStarSystem() == market.getStarSystem()){
            log.info("arkship destination same as current location, aborting");
            return;
        }
        if (ontos.getStarSystem() == Global.getSector().getPlayerFleet().getStarSystem()){
            log.info("arkship destination same as player location, aborting");
            return;
        }
        if (!isSystemSafe(market.getStarSystem())){
            log.info("target system is not safe");
            return;
        }
        if (ontos.getIndustry("VRI_slipspace_drive") == null){
            log.info("arkship does not have a slipspace drive");
            return;
        }
        if (ontos.getIndustry("VRI_slipspace_drive").isDisrupted()){
            log.info("arkship slipspace drive is charging");
            return;
        }
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
        Global.getSector().getCampaignUI().addMessage((ArkshipMoveNotif(destination)), CommMessageAPI.MessageClickAction.INTEL_TAB);
        ontos.setPrimaryEntity(CyrosleeperStructure);
        ontos.addIndustry(Industries.STARFORTRESS_HIGH);
        ontos.getIndustry("VRI_slipspace_drive").setDisrupted(90);
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
    public static boolean isSystemSafe(StarSystemAPI system){
        FactionAPI vri = Global.getSector().getFaction("vri");
        FactionAPI faction = NexUtilsFaction.getSystemOwner(system);
            float facsyssize = 0f;
            Iterator<MarketAPI> marketiter = Misc.getMarketsInLocation(system).iterator();
            while (marketiter.hasNext()){
                MarketAPI market = marketiter.next();
                if (market.getFaction().isHostileTo(vri)) {
                    facsyssize = facsyssize + market.getSize();
                }
            }
            if (facsyssize >= 8){
                return false;
            }
        return true;
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

    @Override
    public void reportInvadeLoot(InteractionDialogAPI dialog, MarketAPI market, Nex_MarketCMD.TempDataInvasion actionData, CargoAPI cargo) {

    }

    @Override
    public void reportInvasionRound(InvasionRound.InvasionRoundResult result, CampaignFleetAPI fleet, MarketAPI defender, float atkStr, float defStr) {

    }

    @Override
    public void reportInvasionFinished(CampaignFleetAPI fleet, FactionAPI attackerFaction, MarketAPI market, float numRounds, boolean success) {

    }

    @Override
    public void reportMarketTransfered(MarketAPI market, FactionAPI newOwner, FactionAPI oldOwner, boolean playerInvolved, boolean isCapture, List<String> factionsToNotify, float repChangeStrength) {
        if (newOwner != Global.getSector().getFaction("vri")) {
            if (market.hasIndustry("VRI_slipspace_drive")) {
                market.removeIndustry("VRI_slipspace_drive", null, false);
                market.addIndustry("VRI_slipspace_drive_ruined");
            }
        }
        if (newOwner == Global.getSector().getFaction("vri")) {
            if (market.hasIndustry("VRI_slipspace_drive_ruined")) {
                market.removeIndustry("VRI_slipspace_drive_ruined", null, false);
                market.addIndustry("VRI_slipspace_drive");
            }
        }
    }
}