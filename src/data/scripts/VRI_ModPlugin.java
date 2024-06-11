package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.ListenerManagerAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import com.fs.starfarer.api.impl.campaign.VRICampaignPluginImpl;
import com.fs.starfarer.api.impl.campaign.VRI_ArkshipScript;
import com.fs.starfarer.api.impl.campaign.econ.impl.VRItemEffectsRepo;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.intel.RevanchismVolantianHostileActivityCause;
import com.fs.starfarer.api.impl.campaign.intel.VICVolantianHostileActivityCause;
import com.fs.starfarer.api.impl.campaign.intel.VRIColonyListener;
import com.fs.starfarer.api.impl.campaign.intel.VolantianHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import data.hullmods.ReAutoRefitButton;
import data.scripts.util.MagicSettings;
import data.world.VRICampaignEveryFrame;
import data.world.VRIGen;
import exerelin.campaign.SectorManager;

import java.util.Iterator;
import java.util.Map;

import lunalib.lunaRefit.LunaRefitManager;
import org.apache.log4j.Logger;
import org.dark.shaders.light.LightData;
import org.dark.shaders.util.ShaderLib;
import org.dark.shaders.util.TextureData;

import static org.lazywizard.lazylib.MathUtils.getRandomNumberInRange;

public class VRI_ModPlugin extends BaseModPlugin implements PlayerColonizationListener {
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
        if (!l.hasListenerOfClass(VRI_ArkshipScript.class)) {
            l.addListener(new VRI_ArkshipScript());
        log.info("Added VRI Arkship Listener");
            }
        if (!l.hasListenerOfClass(VRIColonyListener.class)) {
            l.addListener(new VRIColonyListener());
            log.info("Added VRI Colony Listener");
        }
        }
        public void setScriptsIfNeeded(){
        if (!Global.getSector().hasScript(VRICampaignEveryFrame.class)){
            Global.getSector().addScript(new VRICampaignEveryFrame());
            log.info("Added VRI campaign everyframe script");
            }
        }

        public static void addVolantianColonyCrisis(){
            HostileActivityEventIntel intel = HostileActivityEventIntel.get();
            if (intel != null && intel.getActivityCause(VolantianHostileActivityFactor.class,RevanchismVolantianHostileActivityCause.class) == null) {
                intel.addActivity(new VolantianHostileActivityFactor(intel), new RevanchismVolantianHostileActivityCause(intel));
                log.info("added regular colony crisis");
            }
            if (VRI_CrossmodPlugins.isVICEnabled) {
                if (intel != null && intel.getActivityCause(VolantianHostileActivityFactor.class, VICVolantianHostileActivityCause.class) == null) {
                    intel.addActivity(new VolantianHostileActivityFactor(intel), new VICVolantianHostileActivityCause(intel));
                    log.info("added vic/vri colony crisis");
                }
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

        VRI_ModPlugin.addVolantianColonyCrisis();

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
        setScriptsIfNeeded();
    }

    public void onNewGameAfterEconomyLoad() {
        setListenersIfNeeded();
        setScriptsIfNeeded();
    }

    public void onNewGameAfterTimePass(){
        initPlanetConditions();
        setListenersIfNeeded();
        setScriptsIfNeeded();
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

        LunaRefitManager.addRefitButton(new ReAutoRefitButton());

        Boolean hasGlib = Global.getSettings().getModManager().isModEnabled("shaderLib");
        if (hasGlib){
            ShaderLib.init();
            TextureData.readTextureDataCSV("data/config/vri_texture_data.csv");
            LightData.readLightDataCSV("data/config/vri_light_data.csv");
        }
    }
    public void initPlanetConditions(){
        Iterator<StarSystemAPI> sysiter = Global.getSector().getStarSystems().iterator();
        while (sysiter.hasNext()){
            StarSystemAPI system = sysiter.next();
            Iterator<PlanetAPI> planetiter = system.getPlanets().iterator();
            while (planetiter.hasNext()){
                PlanetAPI planet = planetiter.next();
                if (planet.getTypeId().equals("vri_" +
                        "storm")){
                    if (!planet.hasCondition(Conditions.EXTREME_WEATHER)){
                        planet.getMarket().addCondition(Conditions.EXTREME_WEATHER);
                    }
                }
            }
        }
    }

    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
            VRI_ModPlugin.addVolantianColonyCrisis();
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {

    }
}
