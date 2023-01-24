package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.terrain.BaseRingTerrain;
import com.fs.starfarer.api.util.Misc;
import data.world.VRIGen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Uelyst {

    final float NocturneDist = 3800;
    final float GeirDist = 1000;
    final float SubA1Dist = 3000;
    final float ringDist = 5000;
    final float asteroids1Dist = 5050f;
    final float dust1Dist = 5000f;
    final float dust2Dist = 4000f;
    final float jumpFringeDist = 8000f;
    final float jumpCenterDist = 4000f;
    final float comDist = 8500;
    final float navDist = 2000;
    final float sensorDist = 4200;

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Uelyst");
        system.getLocation().set(22000, -20000);
        //system.setLightColor(new Color(31,247,182, 100));
        system.setEnteredByPlayer(true);
        Misc.setAllPlanetsSurveyed(system, true);

        PlanetAPI UelystStar = system.initStar("vri_star_uelyst", // unique id for this star
                "star_blue_supergiant", // id in planets.json
                500f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult

        //Sublimation Platform A1
        PlanetAPI SubA1 = system.addPlanet("vri_planet_suba1",
                UelystStar,
                "Sublimation Platform A1",
                "gas_giant",
                90f,
                400f,
                SubA1Dist,
                80f);

        MarketAPI SubA1_market = VRIGen.addMarketplace(
                "vri",
                SubA1,
                null,
                "Sublimation Platform A1",
                3,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.OUTPOST,
                                Conditions.VOLATILES_PLENTIFUL,
                                Conditions.TOXIC_ATMOSPHERE
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
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.WAYSTATION,
                                Industries.MINING,
                                Industries.ORBITALSTATION
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                false);
        SubA1_market.getIndustry(Industries.MINING).setSpecialItem(new SpecialItemData(Items.PLASMA_DYNAMO, null));
        SubA1_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);

        //Geir's Slab
        PlanetAPI Geir = system.addPlanet("vri_planet_Geir",
                SubA1,
                "Geir's Slab",
                "tundra",
                50f,
                100f,
                GeirDist,
                100f);

        SectorEntityToken Geir_mirror1 = system.addCustomEntity("Geir_mirror1", "Geir Stellar Mirror Alpha", "stellar_mirror", "vri");
        SectorEntityToken Geir_mirror2 = system.addCustomEntity("Geir_mirror2", "Geir Stellar Mirror Beta", "stellar_mirror", "vri");
        SectorEntityToken Geir_mirror3 = system.addCustomEntity("Geir_mirror3", "Geir Stellar Mirror Gamma", "stellar_mirror", "vri");
        Geir_mirror1.setCircularOrbitPointingDown(system.getEntityById("vri_planet_Geir"), 90 - 60, 200, 100);
        Geir_mirror2.setCircularOrbitPointingDown(system.getEntityById("vri_planet_Geir"), 90 - 30, 200, 100);
        Geir_mirror3.setCircularOrbitPointingDown(system.getEntityById("vri_planet_Geir"), 90 + 0, 200, 100);
        Geir_mirror1.setCustomDescriptionId("stellar_mirror");
        Geir_mirror2.setCustomDescriptionId("stellar_mirror");
        Geir_mirror3.setCustomDescriptionId("stellar_mirror");

        MarketAPI Geir_market = VRIGen.addMarketplace(
                "vri",
                Geir,
                new ArrayList<SectorEntityToken>(
                        Arrays.asList(
                            Geir_mirror1,
                            Geir_mirror2,
                            Geir_mirror3
                        )
                ),
                "Geir's Slab",
                3,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_5,
                                Conditions.COLD,
                                Conditions.SOLAR_ARRAY,
                                Conditions.RUINS_VAST,
                                Conditions.FREE_PORT,
                                Conditions.HABITABLE
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
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.WAYSTATION,
                                Industries.FUELPROD,
                                "cryorevival"
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                true,
                //junk and chatter
                true);
        Geir_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);

        //Nocturne
        SectorEntityToken NocturneStation = system.addCustomEntity("vri_nocturne", "Nocturne", "vri_remnant_station", "vri");
        NocturneStation.setCircularOrbitPointingDown(UelystStar, 360f * (float) Math.random(), NocturneDist, 200);

        MarketAPI Nocturne_market = VRIGen.addMarketplace(
                "vri",
                NocturneStation,
                null,
                "Nocturne Station",
                3,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.STEALTH_MINEFIELDS,
                                Conditions.OUTPOST
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
                                Industries.POPULATION,
                                Industries.SPACEPORT,
                                Industries.HEAVYBATTERIES,
                                Industries.MILITARYBASE
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                false);
        Nocturne_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);

        //Asteroid field
        SectorEntityToken UelystAF1 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldTerrainPlugin.AsteroidFieldParams(
                        200f, // min radius
                        300f, // max radius
                        8, // min asteroid count
                        16, // max asteroid count
                        4f, // min asteroid radius
                        16f, // max asteroid radius
                        "Asteroids Field")); // null for default name
        UelystAF1.setCircularOrbit(UelystStar, 180, asteroids1Dist, 280);

        //Dust belt
        system.addRingBand(UelystStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust1Dist, 330f);
        system.addRingBand(UelystStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust1Dist, 310f);

        //Dust belt
        system.addRingBand(UelystStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust2Dist, 330f);
        system.addRingBand(UelystStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust2Dist, 310f);

        //Ring
        SectorEntityToken ring = system.addTerrain(Terrain.RING, new BaseRingTerrain.RingParams(200 + 256, ringDist, null, "Uelyst's Disk"));
        ring.setCircularOrbit(UelystStar, 0, 0, 100);

        //add Comm relay
        SectorEntityToken MakeshiftRelay = system.addCustomEntity("vri_comm_relay_makeshift", // unique id
                "Outer Uelyst Comm Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        MakeshiftRelay.setCircularOrbitPointingDown(UelystStar, 180f, comDist, 265);

        // Nav beacon
        SectorEntityToken NavBeacon = system.addCustomEntity("vri_nav_buoy_makeshift", // unique id
                "Outer Uelyst Nav Beacon", // name - if null, defaultName from custom_entities.json will be used
                "nav_buoy_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        NavBeacon.setCircularOrbitPointingDown(UelystStar, 90f, navDist, 305);

        // Sensor relay
        SectorEntityToken SensorRelay = system.addCustomEntity("vri_sensor_array", // unique id
                "Uelyst Sensor Relay", // name - if null, defaultName from custom_entities.json will be used
                "sensor_array", // type of object, defined in custom_entities.json
                "vri"); // faction
        SensorRelay.setCircularOrbitPointingDown(UelystStar, 70f, sensorDist, 200);

        //Jump point
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "Center_jump",
                "Center System Jump");

        jumpPoint1.setCircularOrbit(system.getEntityById("vri_star_uelyst"), 10, jumpCenterDist, 100f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(
                "fringe_jump",
                "Fringe System Jump");

        jumpPoint2.setCircularOrbit(system.getEntityById("vri_star_uelyst"), 2, jumpFringeDist, 500f);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint1);
        system.addEntity(jumpPoint2);

        system.autogenerateHyperspaceJumpPoints(true, false);
    }
}
