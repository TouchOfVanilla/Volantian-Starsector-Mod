package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.econ.Market;
import data.world.VRIGen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.world.TTBlackSite.addDerelict;


public class Fevali {
    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Fevali");
        system.getLocation().set(24500, -21000);
        //system.setLightColor(new Color(31,247,182, 100));

        PlanetAPI FevaliStar = system.initStar("vri_star_Fevali", // unique id for this star
                "star_yellow", // id in planets.json
                650f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult
    }
}