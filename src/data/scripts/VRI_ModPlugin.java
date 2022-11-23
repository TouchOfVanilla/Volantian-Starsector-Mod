package data.scripts;

import com.fs.starfarer.api.BaseModPlugin;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import data.world.VRIGen;
import exerelin.campaign.SectorManager;

import java.util.ArrayList;

public class VRI_ModPlugin extends BaseModPlugin {

    @Override
    public void onNewGame() {
        //Nex compatibility setting, if there is no nex or corvus mode(Nex), just generate the system
//        boolean haveNexerelin = Global.getSettings().getModManager().isModEnabled("nexerelin");
//        if (!haveNexerelin || SectorManager.getManager().isCorvusMode()) {
//
//        }
        new VRIGen().generate(Global.getSector());
    }
}
