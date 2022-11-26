package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import data.scripts.util.MagicSettings;
import data.world.VRIGen;
import exerelin.campaign.SectorManager;
import java.util.Map;
import org.apache.log4j.Logger;

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

    public VRI_ModPlugin() {
    }

    public void onNewGame() {
        Map<String, Object> data = Global.getSector().getPersistentData();
        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
            new VRIGen().generate(Global.getSector());
            data.put("VRI_generated", "Version 1.0");
        }

    }

    public void onGameLoad(boolean wasEnabledBefore) {
        boolean loadIntoExistingSave = MagicSettings.getBoolean("TouchOfVanilla_vri", "loadIntoExistingSave");
        if (loadIntoExistingSave) {

            boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
            Map<String, Object> data = Global.getSector().getPersistentData();

            if ((!haveNexerelin || SectorManager.getManager().isCorvusMode()) && !data.containsKey("VRI_generated")) {
                new VRIGen().generate(Global.getSector());
                data.put("VRI_generated", "Version 1.0");
            }
        }

    }

    public void onNewGameAfterEconomyLoad() {
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
