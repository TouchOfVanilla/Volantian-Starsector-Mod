package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import data.world.VRIGen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Gargantua {

    final float StalosDist = 6400;
    final float EmineneceDist = 5000;
    final float dust1Dist = 3000f;
    final float dust2Dist = 4000f;
    final float dust3Dist = 6000f;
    final float jumpFringeDist = 10000f;
    final float jumpCenterDist = 5500f;
    final float comDist = 9000;
    final float navDist = 7000;
    final float sensorDist = 5200;

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Gargantua");
        system.getLocation().set(22000, -25000);
        //system.setLightColor(new Color(31,247,182, 100));

        PlanetAPI GargantuaStar = system.initStar("vri_star_Gargantua", // unique id for this star
                "black_hole", // id in planets.json
                50f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult

        //Eminenece
        PlanetAPI Eminenece = system.addPlanet("vri_planet_eminenece",
                GargantuaStar,
                "Eminenece",
                "rocky_metallic",
                90f,
                130f,
                EmineneceDist,
                80f);
        //Gargantua_Star.setCustomDescriptionId("vri_planet_Eminenece"); //reference descriptions.csv

        MarketAPI eminenece_market = VRIGen.addMarketplace(
                "vri",
                Eminenece,
                null,
                "Eminenece",
                4,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.RARE_ORE_ABUNDANT,
                                Conditions.ORE_ULTRARICH,
                                Conditions.METEOR_IMPACTS,
                                Conditions.THIN_ATMOSPHERE,
                                Conditions.DARK,
                                Conditions.VERY_COLD
                        )
                ),

                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE,
                                Submarkets.SUBMARKET_BLACK
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                false);

        //Orbital Station "Stalos Outpost"
        SectorEntityToken StalosStation = system.addCustomEntity("vri_Stalos", "Stalos", "vri_remnant_station", "vri");
        StalosStation.setCircularOrbitPointingDown(GargantuaStar, 360f * (float) Math.random(), StalosDist, 200);

        MarketAPI Stalos_market = VRIGen.addMarketplace(
                "vri",
                StalosStation,
                null,
                "Stalos Station",
                3,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_3
                        )
                ),

                new ArrayList<>(
                        Arrays.asList(
                                Submarkets.GENERIC_MILITARY,
                                Submarkets.SUBMARKET_OPEN,
                                Submarkets.SUBMARKET_STORAGE,
                                Submarkets.SUBMARKET_BLACK
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                false);

        //Dust belt
        system.addRingBand(GargantuaStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust1Dist, 330f);
        system.addRingBand(GargantuaStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust1Dist, 310f);

        //Dust belt
        system.addRingBand(GargantuaStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust2Dist, 330f);
        system.addRingBand(GargantuaStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust2Dist, 310f);

        //Dust belt
        system.addRingBand(GargantuaStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust3Dist, 330f);
        system.addRingBand(GargantuaStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust3Dist, 310f);

        //add Comm relay
        SectorEntityToken MakeshiftRelay = system.addCustomEntity("vri_comm_relay_makeshift", // unique id
                "Outer Gargantua Comm Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        MakeshiftRelay.setCircularOrbitPointingDown(GargantuaStar, 180f, comDist, 265);

        // Nav beacon
        SectorEntityToken NavBeacon = system.addCustomEntity("vri_nav_buoy_makeshift", // unique id
                "Outer Gargantua Nav Beacon", // name - if null, defaultName from custom_entities.json will be used
                "nav_buoy_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        NavBeacon.setCircularOrbitPointingDown(GargantuaStar, 90f, navDist, 305);

        // Sensor relay
        SectorEntityToken SensorRelay = system.addCustomEntity("vri_sensor_array", // unique id
                "Gargantua Sensor Relay", // name - if null, defaultName from custom_entities.json will be used
                "sensor_array", // type of object, defined in custom_entities.json
                "vri"); // faction
        SensorRelay.setCircularOrbitPointingDown(GargantuaStar, 70f, sensorDist, 200);

        //Jump point
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "Center_jump",
                "Center System Jump");

        jumpPoint1.setCircularOrbit(system.getEntityById("vri_star_Gargantua"), 10, jumpCenterDist, 200f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(
                "fringe_jump",
                "Fringe System Jump");

        jumpPoint2.setCircularOrbit(system.getEntityById("vri_star_Gargantua"), 2, jumpFringeDist, 1000f);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint1);
        system.addEntity(jumpPoint2);

        system.autogenerateHyperspaceJumpPoints(true, false);
    }
}
