package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import exerelin.campaign.intel.specialforces.namer.SpecialForcesNamer;

import java.util.ArrayList;
import java.util.Collections;

public class VRISTGNamer implements SpecialForcesNamer {
    @Override
    public String getFleetName(CampaignFleetAPI fleet, MarketAPI origin, PersonAPI commander) {

        ArrayList<String> namelist = new ArrayList<String>();
        namelist.add("Blade of Volantis");
        namelist.add("Shield of Azor");
        namelist.add("Spear of Ontos");
        namelist.add("Geir's Lance");
        namelist.add("Desmond's Edge");

        Collections.shuffle(namelist);
        return namelist.get(0);
    }
}
