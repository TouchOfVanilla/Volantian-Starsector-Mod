package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.impl.campaign.VRICampaignPluginImpl;
import com.fs.starfarer.api.impl.campaign.VRIDerelictSpawner;
import com.fs.starfarer.api.impl.campaign.VRIFleetInflationListener;
import com.fs.starfarer.api.impl.campaign.econ.impl.VRItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.ids.Planets;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BluesteelDefenderPluginImpl;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicSettings;
import data.world.VRIGen;
import data.world.industries_and_buildings.VRI_MidlineMarketPlugin;
import exerelin.campaign.SectorManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import exerelin.utilities.LunaConfigHelper;
import org.apache.log4j.Logger;

import static org.lazywizard.lazylib.MathUtils.getRandomNumberInRange;

public class VRI_ModPlugin extends BaseModPlugin {
    private static Logger log = Global.getLogger(VRI_ModPlugin.class);

    public static boolean blackrockExists = false;
    public static boolean borkenExists = false;
    public static boolean checkMemory = false;
    public static boolean diableExists = false;
    public static boolean exigencyExists = false;
    public static boolean hasDynaSector = false;
    public static boolean hasGraphicsLib = false;
    public static boolean hasMagicLib = false;
    public static boolean hasUnderworld = false;
    public static boolean iceExists = false;
    public static boolean imperiumExists = false;
    public static boolean isExerelin = false;
    public static boolean junkPiratesExists = false;
    public static boolean oraExists = false;
    public static boolean scalarTechExists = false;
    public static boolean scyExists = false;
    public static boolean shadowyardsExists = false;
    public static boolean templarsExists = false;
    public static boolean tiandongExists = false;
    public static boolean tyradorExists = false;
    public static boolean dmeExists = false;
    public static boolean arkgneisisExists = false;

    public static float TETHER_SYSTEM_Y_COORDS = 2f;
    public static float TETHER_SYSTEM_X_COORDS = 2f;

    public VRI_ModPlugin() {
    }

    public void setListenersIfNeeded() {
        ListenerManagerAPI l = Global.getSector().getListenerManager();
        if (!l.hasListenerOfClass(VRIFleetInflationListener.class)) {
            l.addListener(new VRIFleetInflationListener(), true);
            log.info("Added VRI Fleet Listener");
        }
    }

    public void onNewGame() {
        Map<String, Object> data = Global.getSector().getPersistentData();
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            new VRIGen().generate(Global.getSector());
            data.put("VRI_generated", "Version 69.4.20");
        }
    }

    public void onGameLoad(boolean wasEnabledBefore) {

        VRItemEffectsRepo.addItemEffectsToVanillaRepo();

        boolean loadIntoExistingSave = MagicSettings.getBoolean("TouchOfVanilla_vri", "loadIntoExistingSave");
        if (loadIntoExistingSave) {

            boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
            Map<String, Object> data = Global.getSector().getPersistentData();

            if ((!haveNexerelin || SectorManager.getManager().isCorvusMode()) && !data.containsKey("VRI_generated")) {
                new VRIGen().generate(Global.getSector());
                data.put("VRI_generated", "Version 1.0");
            }
        }
            Global.getSector().registerPlugin(new VRICampaignPluginImpl());

        setListenersIfNeeded();
        ListenerManagerAPI l = Global.getSector().getListenerManager();
        if (!l.hasListenerOfClass(VRIFleetInflationListener.class)) {
            l.addListener(new VRIFleetInflationListener());
        }
        VRI_MidlineMarketPlugin.addHullsToMidlineMarket();
    }

    public void onNewGameAfterEconomyLoad() {
        Global.getSector().getGenericPlugins().addPlugin(new BluesteelDefenderPluginImpl());
    }

    public void onNewGameAfterTimePass(){
        ///Planetary tether generation
        ///picking le system
        log.info("Collecting all systems that are valid targets for putting tether in.");
        ArrayList<StarSystemAPI> validSystems = new ArrayList<>();
        for (StarSystemAPI system : Global.getSector().getStarSystems()) {
            if (system.isProcgen() & !system.hasBlackHole() && !system.hasPulsar() && !system.hasSystemwideNebula() && !system.getPlanets().isEmpty()) {
                validSystems.add(system);
            }
        }
        Collections.shuffle(validSystems);
        PlanetAPI tetherPlanet = null;
        StarSystemAPI tetherSystem = null;
        log.info("Searching for planet to attach tether to.");
        for (StarSystemAPI system : validSystems) {
            for (PlanetAPI planet : system.getPlanets()) {
                if ((planet.hasCondition(Conditions.RUINS_SCATTERED) || planet.hasCondition(Conditions.RUINS_WIDESPREAD) || planet.hasCondition(Conditions.RUINS_EXTENSIVE) ||
                        planet.hasCondition(Conditions.RUINS_VAST)) && planet.hasCondition(Conditions.HABITABLE) && !planet.isStar() && !planet.isGasGiant() && !planet.isMoon()) {
                    tetherPlanet = planet;
                    tetherSystem = system;
                    log.info("Found a planet that satisfies conditions for tether: " + tetherPlanet.getName() + "in " + tetherSystem.getName());
                }
            }
            if (tetherPlanet != null) {
                log.info("Tether planet has been found, exiting loop early.");
                break;
            }
        }
        if (tetherPlanet == null) return;
        tetherPlanet.setRadius(120f);
        MarketAPI tetherplanetmarket = tetherPlanet.getMarket();
        tetherplanetmarket.addCondition(Conditions.DECIVILIZED);
        PlanetAPI tetherPlanet2 = tetherSystem.addPlanet(
                "vri_azariaum",
                tetherPlanet,
                "Azariaum",
                Planets.PLANET_WATER,
                120f,
                60f,
                360f,
                90f
        );
        MarketAPI Vanguard = tetherPlanet2.getMarket();
        Vanguard.addCondition(Conditions.HABITABLE);
        Vanguard.addCondition(Conditions.DECIVILIZED);
        Vanguard.addCondition(Conditions.ORGANICS_COMMON);
        Vanguard.addCondition(Conditions.WATER_SURFACE);
        Vanguard.addCondition(Conditions.STEALTH_MINEFIELDS);
        Vanguard.addCondition(Conditions.VOLTURNIAN_LOBSTER_PENS);
        Vanguard.addCondition(Conditions.RUINS_WIDESPREAD);
        tetherPlanet2.setMarket(Vanguard);
        SectorEntityToken tether = tetherSystem.addCustomEntity("AOTD_Tether", "Planetary Tether", "AKSpaceElevator", "neutral");
        tether.setCircularOrbitPointingDown(tetherPlanet, 120f, 200, 90);
        Misc.setAbandonedStationMarket("aotd_tether_market", tether);
        log.info("Silly Tether created!");

        if (Global.getSettings().getModManager().isModEnabled("aotd_lost_glory")){
            tetherPlanet.getMarket().addCondition("planetarytether");
            Vanguard.addCondition("planetarytether");
        }

        VRIDerelictSpawner.spawnDerelicts();

    }


    public void onApplicationLoad() {
        isExerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        hasUnderworld = Global.getSettings().getModManager().isModEnabled("underworld");
        hasDynaSector = Global.getSettings().getModManager().isModEnabled("dynasector");

        borkenExists = Global.getSettings().getModManager().isModEnabled("fob");
        iceExists = Global.getSettings().getModManager().isModEnabled("nbj_ice");
        imperiumExists = Global.getSettings().getModManager().isModEnabled("Imperium");
        templarsExists = Global.getSettings().getModManager().isModEnabled("Templars");
        blackrockExists = Global.getSettings().getModManager().isModEnabled("blackrock_driveyards");
        exigencyExists = Global.getSettings().getModManager().isModEnabled("exigency");
        shadowyardsExists = Global.getSettings().getModManager().isModEnabled("shadow_ships");
        junkPiratesExists = Global.getSettings().getModManager().isModEnabled("junk_pirates_release");
        scyExists = Global.getSettings().getModManager().isModEnabled("SCY");
        tiandongExists = Global.getSettings().getModManager().isModEnabled("THI");
        diableExists = Global.getSettings().getModManager().isModEnabled("diableavionics");
        oraExists = Global.getSettings().getModManager().isModEnabled("ORA");
        tyradorExists = Global.getSettings().getModManager().isModEnabled("TS_Coalition");
        dmeExists = Global.getSettings().getModManager().isModEnabled("istl_dam");
        scalarTechExists = Global.getSettings().getModManager().isModEnabled("tahlan_scalartech");
        arkgneisisExists = Global.getSettings().getModManager().isModEnabled("ArkLeg");
    }
}
