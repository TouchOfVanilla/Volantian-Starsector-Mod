package data.scripts;

import com.fs.starfarer.api.Global;
import lunalib.lunaSettings.LunaSettings;

public class VRILunaSettings {

    public static boolean BlancheBoolean(){
        Boolean blanche = false;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
            blanche = LunaSettings.getBoolean("TouchOfVanilla_vri","vri_blanchetoggle");
        }
        return blanche;
    }
    public static boolean VestigeBoolean(){
        Boolean vesties = false;
        if (Global.getSettings().getModManager().isModEnabled("lunalib"))
        {
             vesties = LunaSettings.getBoolean("TouchOfVanilla_vri","vri_vestietoggle");
        }
        return vesties;
    }
}
