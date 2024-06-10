package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import data.scripts.VRI_ModPlugin;

public class VRIColonyListener implements PlayerColonizationListener {
    @Override
    public void reportPlayerColonizedPlanet(PlanetAPI planet) {
        VRI_ModPlugin.addVolantianColonyCrisis();
    }

    @Override
    public void reportPlayerAbandonedColony(MarketAPI colony) {

    }
}
