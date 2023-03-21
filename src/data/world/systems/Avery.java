package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.world.VRIGen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;



public class Avery {

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
        StarSystemAPI system = sector.createStarSystem("Avery");
        system.getLocation().set(22000, -25000);
        //system.setLightColor(new Color(31,247,182, 100));

        PlanetAPI AveryStar = system.initStar("vri_star_Avery", // unique id for this star
                "star_orange", // id in planets.json
                500f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult

        //Eminence
        PlanetAPI Eminence = system.addPlanet("vri_planet_eminence",
                AveryStar,
                "Eminence",
                "terran_adapted",
                90f,
                130f,
                EmineneceDist,
                80f);
        Misc.initConditionMarket(Eminence);
        MarketAPI Tally_market = Eminence.getMarket();
        Tally_market.setPlanetConditionMarketOnly(true);
        Tally_market.addCondition(Conditions.HABITABLE);
        Tally_market.addCondition(Conditions.POLLUTION);
        Tally_market.addCondition(Conditions.ORE_RICH);
        Tally_market.addCondition(Conditions.RARE_ORE_RICH);
        Tally_market.addCondition(Conditions.ORGANICS_ABUNDANT);
        Tally_market.addCondition(Conditions.FARMLAND_BOUNTIFUL);
        Tally_market.addCondition(Conditions.MILD_CLIMATE);
        Tally_market.addCondition(Conditions.HOT);
        Tally_market.addCondition(Conditions.RUINS_VAST);
        Tally_market.setPrimaryEntity(Eminence);
        Eminence.setMarket(Tally_market);
        //Avery_Star.setCustomDescriptionId("vri_planet_Eminence"); //reference descriptions.csv

        //Orbital Station "Stalos Outpost"
        SectorEntityToken StalosStation = system.addCustomEntity("vri_Stalos", "Stalos", "vri_remnant_station", "vri");
        StalosStation.setCircularOrbitPointingDown(AveryStar, 360f * (float) Math.random(), StalosDist, 200);

        MarketAPI Stalos_market = VRIGen.addMarketplace(
                "vri",
                StalosStation,
                null,
                "Stalos Station",
                3,

                new ArrayList<>(
                        Arrays.asList(
                                Conditions.POPULATION_3,
                                Conditions.OUTPOST,
                                Conditions.STEALTH_MINEFIELDS
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
                                Industries.MEGAPORT,
                                Industries.MILITARYBASE,
                                Industries.WAYSTATION,
                                Industries.HEAVYBATTERIES,
                                Industries.STARFORTRESS_HIGH
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                false);

        //Tally
        PlanetAPI Tally = system.addPlanet("vri_planet_tally",
                AveryStar,
                "Tally",
                "cryovolcanic",
                90f,
                130f,
                StalosDist,
                80f);
        Misc.initConditionMarket(Tally);
        Tally_market = Tally.getMarket();
        Tally_market.setPlanetConditionMarketOnly(true);
        Tally_market.addCondition(Conditions.VERY_COLD);
        Tally_market.addCondition(Conditions.RARE_ORE_ULTRARICH);
        Tally_market.addCondition(Conditions.ORE_ULTRARICH);
        Tally_market.addCondition(Conditions.VOLATILES_PLENTIFUL);
        Tally_market.addCondition(Conditions.ORGANICS_ABUNDANT);
        Tally_market.addCondition(Conditions.LOW_GRAVITY);
        Tally_market.addCondition(Conditions.RUINS_EXTENSIVE);
        Tally_market.setPrimaryEntity(Tally);
        Tally.setMarket(Tally_market);
        //Avery_Star.setCustomDescriptionId("vri_planet_Tally"); //reference descriptions.csv
        //Derelict Ships

        BaseThemeGenerator.addSalvageEntity(system, AveryStar, ),

        addDerelict(system, AveryStar, "volantian_sandalphon_vri_standard", ShipRecoverySpecial.ShipCondition.BATTERED, 1500f,  (Math.random()<0.4));
        addDerelict(system, AveryStar, "volantian_chromatic_vri_standard", ShipRecoverySpecial.ShipCondition.BATTERED, 1500f,  (Math.random()<0.4));
        addDerelict(system, AveryStar, "volantian_lumen_vri_standard", ShipRecoverySpecial.ShipCondition.BATTERED, 1500f,  (Math.random()<0.2));
        addDerelict(system, AveryStar, "volantian_sunder_vri_standard", ShipRecoverySpecial.ShipCondition.BATTERED, 1500f,  (Math.random()<0.2));
        addDerelict(system, AveryStar, "volantian_naegling_vri_assault", ShipRecoverySpecial.ShipCondition.BATTERED, 1500f,  (Math.random()<0.4));
        addDerelict(system, AveryStar, "volantian_lancet_vri_assault", ShipRecoverySpecial.ShipCondition.BATTERED, 1500f,  (Math.random()<0.2));

// Debris fields!
        DebrisFieldTerrainPlugin.DebrisFieldParams params_ring1 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                225f, // field radius - should not go above 1000 for performance reasons
                0.7f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params_ring1.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params_ring1.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken avery_debris = Misc.addDebrisField(system, params_ring1, StarSystemGenerator.random);
        avery_debris.setSensorProfile(1000f);
        avery_debris.setDiscoverable(true);
        avery_debris.setCircularOrbit(AveryStar, 30f, 5000, 160f);
        avery_debris.setId("avery_debris");

        //Dust belt
        system.addRingBand(AveryStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust1Dist, 330f);
        system.addRingBand(AveryStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust1Dist, 310f);

        //Dust belt
        system.addRingBand(AveryStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust2Dist, 330f);
        system.addRingBand(AveryStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust2Dist, 310f);

        //Dust belt
        system.addRingBand(AveryStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust3Dist, 330f);
        system.addRingBand(AveryStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust3Dist, 310f);

        //add Comm relay
        SectorEntityToken MakeshiftRelay = system.addCustomEntity("vri_comm_relay_makeshift", // unique id
                "Outer Gargantua Comm Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        MakeshiftRelay.setCircularOrbitPointingDown(AveryStar, 180f, comDist, 265);

        // Nav beacon
        SectorEntityToken NavBeacon = system.addCustomEntity("vri_nav_buoy_makeshift", // unique id
                "Outer Gargantua Nav Beacon", // name - if null, defaultName from custom_entities.json will be used
                "nav_buoy_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        NavBeacon.setCircularOrbitPointingDown(AveryStar, 90f, navDist, 305);

        // Sensor relay
        SectorEntityToken SensorRelay = system.addCustomEntity("vri_sensor_array", // unique id
                "Gargantua Sensor Relay", // name - if null, defaultName from custom_entities.json will be used
                "sensor_array", // type of object, defined in custom_entities.json
                "vri"); // faction
        SensorRelay.setCircularOrbitPointingDown(AveryStar, 70f, sensorDist, 200);

        //Jump point
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "Center_jump",
                "Center System Jump");

        jumpPoint1.setCircularOrbit(system.getEntityById("vri_star_Avery"), 10, jumpCenterDist, 200f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(
                "fringe_jump",
                "Fringe System Jump");

        jumpPoint2.setCircularOrbit(system.getEntityById("vri_star_Avery"), 2, jumpFringeDist, 1000f);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint1);
        system.addEntity(jumpPoint2);

        system.autogenerateHyperspaceJumpPoints(true, false);
    }

    private void addDerelict(StarSystemAPI system, PlanetAPI averyStar, String wayfarerStandard, ShipRecoverySpecial.ShipCondition battered, float v, boolean b) {
    }
}
