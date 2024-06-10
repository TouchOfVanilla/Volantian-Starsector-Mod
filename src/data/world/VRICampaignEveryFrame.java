package data.world;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.impl.campaign.intel.RevanchismVolantianHostileActivityCause;
import com.fs.starfarer.api.impl.campaign.intel.VolantianHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import data.scripts.VRI_ModPlugin;
import exerelin.utilities.NexUtils;
import exerelin.utilities.NexUtilsFaction;

public class VRICampaignEveryFrame implements EveryFrameScript {

    @Override
    public boolean isDone() {
        if (HostileActivityEventIntel.get() == null){return false;}
        if (!VolantianHostileActivityFactor.doesVRICrisisNeedInit()){return true;}
        return false;
    }

    @Override
    public boolean runWhilePaused() {
        return false;
    }

    @Override
    public void advance(float amount) {
        if (VolantianHostileActivityFactor.doesVRICrisisNeedInit()){
            VRI_ModPlugin.addVolantianColonyCrisis();
        }
    }
}
