package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.VRI_ArkshipScript;
import lunalib.lunaSettings.LunaSettings;

public class VRILunaSettings {

    public static boolean HABoolean(){
        Boolean HostileActivity = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            HostileActivity = LunaSettings.getBoolean("TouchOfVanilla_vri","vri_HAtoggle");
        }
        return HostileActivity;
    }

    public static boolean ArkshipBoolean(){
        Boolean Movement = true;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
             Movement = LunaSettings.getBoolean("TouchOfVanilla_vri","vri_Arkshiptoggle");
        }
        return Movement;
    }
}
