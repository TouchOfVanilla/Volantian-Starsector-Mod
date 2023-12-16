package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
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
        SectorEntityToken avery_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/avery_nebula.png",
                0, 0, // center of nebula
                system, // location to add to
                "terrain", "nebula_blue", // "nebula_blue", // texture to use, uses xxx_map for map
                4, 4, StarAge.YOUNG); // number of cells in texture


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
                80f,
                EmineneceDist,
                80f);
        Misc.initConditionMarket(Eminence);
        MarketAPI Eminence_market = Eminence.getMarket();
        Eminence_market.setPlanetConditionMarketOnly(true);
        Eminence_market.addCondition(Conditions.HABITABLE);
        Eminence_market.addCondition(Conditions.POLLUTION);
        Eminence_market.addCondition(Conditions.ORE_RICH);
        Eminence_market.addCondition(Conditions.RARE_ORE_RICH);
        Eminence_market.addCondition(Conditions.ORGANICS_ABUNDANT);
        Eminence_market.addCondition(Conditions.FARMLAND_BOUNTIFUL);
        Eminence_market.addCondition(Conditions.MILD_CLIMATE);
        Eminence_market.addCondition(Conditions.HOT);
        Eminence_market.addCondition(Conditions.IRRADIATED);
        Eminence_market.addCondition(Conditions.RUINS_VAST);
        Eminence_market.setPrimaryEntity(Eminence);
        Eminence.setMarket(Eminence_market);
        Eminence.setCustomDescriptionId("vri_planet_Eminence"); //reference descriptions.csv

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
                                Submarkets.SUBMARKET_BLACK,
                                "vsb_market"
                        )
                ),
                new ArrayList<>(
                        Arrays.asList(
                                Industries.POPULATION,
                                Industries.MEGAPORT,
                                Industries.MILITARYBASE,
                                Industries.WAYSTATION,
                                Industries.HEAVYBATTERIES,
                                "VRI_ConvertedNexusStation",
                                "VRI_VolGen_Office"

                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                false);
        StalosStation.setCustomDescriptionId("vri_Stalos");
        PersonAPI iris = Global.getFactory().createPerson();
        iris.setId("iris");
        iris.setFaction("vri");
        iris.setGender(FullName.Gender.FEMALE);
        iris.setRankId(Ranks.SPACE_COMMANDER);
        iris.setPostId(Ranks.POST_BASE_COMMANDER);
        iris.setImportance(PersonImportance.HIGH);
        iris.getName().setFirst("Iris");
        iris.getName().setLast("Star");
        Global.getSector().getImportantPeople().addPerson(iris);
        Global.getSector().getImportantPeople().getPerson("iris").addTag("trade");
        iris.setPortraitSprite(Global.getSettings().getSpriteName("characters", iris.getId()));
        Stalos_market.getCommDirectory().addPerson(iris);
        //Tally
        PlanetAPI Tally = system.addPlanet("vri_planet_tally",
                AveryStar,
                "Tally",
                "cryovolcanic",
                90f,
                130f,
                3500,
                120f);
        Misc.initConditionMarket(Tally);
        MarketAPI Tally_market = Tally.getMarket();
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
        Tally.setCustomDescriptionId("vri_planet_Tally"); //reference descriptions.csv

        //Abandoned Station around
        SectorEntityToken daedalusstation = system.addCustomEntity("daedalus_station ",
                "Daedalus Station", "station_side06", "neutral");

        daedalusstation.setCircularOrbitPointingDown(system.getEntityById("vri_star_avery"), 45, 2000, 50);

        Misc.setAbandonedStationMarket("daedalus_station_market", daedalusstation);

        daedalusstation.setCustomDescriptionId("daedalus_station");
        daedalusstation.setInteractionImage("illustrations", "abandoned_station3");



        //Derelict Ships + Debris for Daedalus

        DebrisFieldTerrainPlugin.DebrisFieldParams params2 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                250f, // field radius - should not go above 1000 for performance reasons
                1.2f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params2.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params2.baseSalvageXP = 1000; // base XP for scavenging in field
        SectorEntityToken debrisstation = Misc.addDebrisField(system, params2, StarSystemGenerator.random);
        debrisstation.setSensorProfile(650f);
        debrisstation.setDiscoverable(true);
        debrisstation.setCircularOrbit(daedalusstation, 20f, 20, 900f);
        debrisstation.setId("deadalus_debris");

        addDerelict(system, daedalusstation, "volantian_gauntlet_vri_Standard", Global.getSector().getFaction("vri").pickRandomShipName(), Misc.genUID(),ShipRecoverySpecial.ShipCondition.BATTERED, 270f, (Math.random() < 0.5));
        addDerelict(system, daedalusstation, "volantian_sunder_vri_Standard", Global.getSector().getFaction("vri").pickRandomShipName(), Misc.genUID(),ShipRecoverySpecial.ShipCondition.BATTERED, 270f, (Math.random() < 0.5));



        // Debris fields for the star!

        Random rand = new Random();
        int random_radius1 = 500 + rand.nextInt(4400);
        int random_radius2 = 500 + rand.nextInt(4400);
        int random_radius3 = 500 + rand.nextInt(4400);
        int random_radius4 = 500 + rand.nextInt(4400);
        int random_radius5 = 500 + rand.nextInt(4400);

        DebrisFieldTerrainPlugin.DebrisFieldParams params_ring1 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                225f, // field radius - should not go above 1000 for performance reasons
                0.7f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params_ring1.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params_ring1.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken avery_debris1 = Misc.addDebrisField(system, params_ring1, StarSystemGenerator.random);
        avery_debris1.setSensorProfile(1000f);
        avery_debris1.setDiscoverable(true);
        avery_debris1.setCircularOrbit(AveryStar, 30f, random_radius1, 160f);
        avery_debris1.setId("avery_debris1");

        DebrisFieldTerrainPlugin.DebrisFieldParams params_ring2 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                225f, // field radius - should not go above 1000 for performance reasons
                0.7f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params_ring2.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params_ring2.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken avery_debris2 = Misc.addDebrisField(system, params_ring2, StarSystemGenerator.random);
        avery_debris2.setSensorProfile(1000f);
        avery_debris2.setDiscoverable(true);
        avery_debris2.setCircularOrbit(AveryStar, 90f, random_radius2, 160f);
        avery_debris2.setId("avery_debris2");

        DebrisFieldTerrainPlugin.DebrisFieldParams params_ring3 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                225f, // field radius - should not go above 1000 for performance reasons
                0.7f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params_ring3.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params_ring3.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken avery_debris3 = Misc.addDebrisField(system, params_ring3, StarSystemGenerator.random);
        avery_debris3.setSensorProfile(1000f);
        avery_debris3.setDiscoverable(true);
        avery_debris3.setCircularOrbit(AveryStar, 60f, random_radius3, 160f);
        avery_debris3.setId("avery_debris3");

        DebrisFieldTerrainPlugin.DebrisFieldParams params_ring4 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                225f, // field radius - should not go above 1000 for performance reasons
                0.7f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params_ring4.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params_ring4.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken avery_debris4 = Misc.addDebrisField(system, params_ring4, StarSystemGenerator.random);
        avery_debris4.setSensorProfile(1000f);
        avery_debris4.setDiscoverable(true);
        avery_debris4.setCircularOrbit(AveryStar, 120f, random_radius4, 90f);
        avery_debris4.setId("avery_debris4");

        DebrisFieldTerrainPlugin.DebrisFieldParams params_ring5 = new DebrisFieldTerrainPlugin.DebrisFieldParams(
                225f, // field radius - should not go above 1000 for performance reasons
                0.7f, // density, visual - affects number of debris pieces
                10000000f, // duration in days
                0f); // days the field will keep generating glowing pieces
        params_ring5.source = DebrisFieldTerrainPlugin.DebrisFieldSource.MIXED;
        params_ring5.baseSalvageXP = 500; // base XP for scavenging in field
        SectorEntityToken avery_debris5 = Misc.addDebrisField(system, params_ring5, StarSystemGenerator.random);
        avery_debris5.setSensorProfile(1000f);
        avery_debris5.setDiscoverable(true);
        avery_debris5.setCircularOrbit(AveryStar, 180f, random_radius5, 140f);
        avery_debris5.setId("avery_debris5");
// Salvage Entities


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
                "Avery Comm Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        MakeshiftRelay.setCircularOrbitPointingDown(AveryStar, 180f, comDist, 265);

        // Nav beacon
        SectorEntityToken NavBeacon = system.addCustomEntity("vri_nav_buoy_makeshift", // unique id
                "Avery Nav Beacon", // name - if null, defaultName from custom_entities.json will be used
                "nav_buoy_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        NavBeacon.setCircularOrbitPointingDown(AveryStar, 90f, navDist, 305);

        // Sensor relay
        SectorEntityToken SensorRelay = system.addCustomEntity("vri_sensor_array", // unique id
                "Avery Sensor Relay", // name - if null, defaultName from custom_entities.json will be used
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
        system.setBackgroundTextureFilename("graphics/backgrounds/vribg1.jpg");

        system.autogenerateHyperspaceJumpPoints(true, false);
    }
}

//Nosy little fuck, aren't you?