package data.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;

public class VRI_CrossmodPlugins {
    public static boolean isVICEnabled = false;


    public static void initVICCrossmod(){
        FactionAPI vri = Global.getSector().getFaction("vri");
        FactionAPI vic = Global.getSector().getFaction("vic");

        vri.makeCommodityIllegal("vic_genetech");
        isVICEnabled = true;

    }
}
