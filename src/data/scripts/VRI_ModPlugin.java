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
            boolean isVRIEnabled = MagicSettings.getBoolean("TouchOfVanilla_vri", "enableVRIFaction");
            boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
            Map<String, Object> data = Global.getSector().getPersistentData();

            if ((!haveNexerelin || SectorManager.getManager().isCorvusMode()) && isVRIEnabled && !data.containsKey("VRI_generated")) {
                new VRIGen().generate(Global.getSector());
                data.put("VRI_generated", "Version 1.0");
            }
        }

    }

    public void onNewGameAfterEconomyLoad() {
    }

    public void onApplicationLoad() {
    }
}
