package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.econ.MarketConditionAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.terrain.AsteroidFieldTerrainPlugin;
import com.fs.starfarer.api.util.Misc;
import data.world.VRIGen;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Royce {

    final float azorDist = 300;
    final float desmondDist = 4000;
    final float cryosleeperDist = 7200;
    final float volantisDist = 3000;
    final float asteroids1Dist = 2750f;
    final float asteroids2Dist = 3500f;
    final float asteroids3Dist = 6000f;
    final float asteroids4Dist = 8000f;
    final float dustDist = 5000f;
    final float jumpFringeDist = 10000f;
    final float jumpCenterDist = 2000f;
    final float comDist = 8500;
    final float navDist = 9000;
    final float sensorDist = 7000;

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Royce");
        system.getLocation().set(23000, -17000);
        system.setBackgroundTextureFilename("graphics/backgrounds/vribg1.jpg");
        //system.setLightColor(new Color(31,247,182, 100));
        system.setEnteredByPlayer(true);
        Misc.setAllPlanetsSurveyed(system, true);

        SectorEntityToken royce_nebula = Misc.addNebulaFromPNG("data/campaign/terrain/royce_nebula.png",
                0, 0, // center of nebula
                system, // location to add to
                "terrain", "nebula_blue", // "nebula_blue", // texture to use, uses xxx_map for map
                4, 4, StarAge.YOUNG); // number of cells in texture


        PlanetAPI RoyceStar = system.initStar("vri_star_royce", // unique id for this star
                "star_yellow", // id in planets.json
                500f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult

        //Volantis
        PlanetAPI Volantis = system.addPlanet("vri_planet_Volantis",
                RoyceStar,
                "Volantis",
                "terran-eccentric",
                90f,
                130f,
                volantisDist,
                80f);
        Volantis.setCustomDescriptionId("vri_planet_Volantis"); //reference descriptions.csv

        MarketAPI Volantis_market = VRIGen.addMarketplace(
                "vri",
                Volantis,
                null,
                "Volantis",
                5,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_5,
                                Conditions.LARGE_REFUGEE_POPULATION,
                                Conditions.HABITABLE,
                                Conditions.LOW_GRAVITY,
                                Conditions.MILD_CLIMATE,
                                Conditions.FARMLAND_RICH,
                                Conditions.VOLATILES_TRACE
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
                                Industries.FARMING,
                                Industries.LIGHTINDUSTRY,
                                Industries.WAYSTATION,
                                Industries.MILITARYBASE,
                                Industries.STARFORTRESS_HIGH,
                                "VRI_VolGen_Office",
                                "cryorevival"
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                true,
                //junk and chatter
                true);
        Volantis_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        PersonAPI blanche = Global.getFactory().createPerson();
        blanche.setId("blanche");
        blanche.setFaction("vri");
        blanche.setGender(FullName.Gender.FEMALE);
        blanche.setRankId(Ranks.SPACE_COMMANDER);
        blanche.setPostId(Ranks.POST_BASE_COMMANDER);
        blanche.setImportance(PersonImportance.HIGH);
        blanche.getName().setFirst("Blanche");
        blanche.getName().setLast("Star");
        Global.getSector().getImportantPeople().addPerson(blanche);
        Global.getSector().getImportantPeople().getPerson("blanche").addTag("military");
        blanche.setPortraitSprite(Global.getSettings().getSpriteName("characters", blanche.getId()));

        Volantis_market.getCommDirectory().addPerson(blanche);
        Volantis_market.getCommDirectory().getEntryForPerson(blanche).setHidden(false);
        Volantis_market.addPerson(blanche);

        PersonAPI delilah = Global.getFactory().createPerson();
        delilah.setId("delilah");
        delilah.setFaction("vri");
        delilah.setGender(FullName.Gender.FEMALE);
        delilah.setRankId(Ranks.FACTION_LEADER);
        delilah.setPostId(Ranks.POST_ADMINISTRATOR);
        delilah.setImportance(PersonImportance.VERY_HIGH);
        delilah.getName().setFirst("November-Echo-Delta");
        delilah.getName().setLast("'Delilah'");
        delilah.setPortraitSprite(Global.getSettings().getSpriteName("characters", delilah.getId()));

        Volantis_market.getCommDirectory().addPerson(delilah);
        Volantis_market.getCommDirectory().getEntryForPerson(delilah).setHidden(false);
        Volantis_market.addPerson(delilah);


        //Azor
        PlanetAPI Azor = system.addPlanet("vri_planet_Azor",
                Volantis,
                "Azor",
                "barren",
                30f,
                50f,
                200f,
                90f);
        Azor.setCircularOrbit(Volantis, 100, azorDist,100);
        Misc.initConditionMarket(Azor);
        MarketAPI Azor_market = Azor.getMarket();
        Azor_market.setPlanetConditionMarketOnly(true);
        Azor_market.addCondition(Conditions.NO_ATMOSPHERE);
        Azor_market.addCondition(Conditions.LOW_GRAVITY);
        Azor_market.addCondition(Conditions.ORE_SPARSE);
        Azor_market.addCondition(Conditions.RARE_ORE_MODERATE);
        Azor_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        Azor_market.setPrimaryEntity(Azor);
        Azor.setMarket(Azor_market);
        for (MarketConditionAPI mc : Azor_market.getConditions())
        {
            mc.setSurveyed(true);
        }

        //Cryosleeper Station "Ontos-117"
        SectorEntityToken CyrosleeperStructure = system.addCustomEntity("vri_cryosleeper", "Cryosleeper Station \"Ontos-117\"", "vri_cryosleeper_station", "vri");
        CyrosleeperStructure.setCircularOrbitPointingDown(RoyceStar, 360f * (float) Math.random(), cryosleeperDist, 200);

        //Desmond's Landing
        PlanetAPI Desmond = system.addPlanet("vri_planet_Desmond",
                RoyceStar,
                "Desmond's Landing",
                "barren-bombarded",
                50f,
                130f,
                desmondDist,
                100f);

        MarketAPI Desmond_market = VRIGen.addMarketplace(
                "vri",
                Desmond,
                null,
                "Desmond's Landing",
                4,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.NO_ATMOSPHERE,
                                Conditions.RARE_ORE_ULTRARICH,
                                Conditions.IRRADIATED,
                                Conditions.ORE_RICH,
                                Conditions.RUINS_EXTENSIVE
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
                                Industries.HEAVYINDUSTRY,
                                Industries.MINING,
                                "VRI_ConvertedNexusStation",
                                "VRI_VolGen_Office"
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                true);
        Desmond_market.getIndustry(Industries.HEAVYINDUSTRY).setSpecialItem(new SpecialItemData(Items.CORRUPTED_NANOFORGE, null));
        Desmond_market.getIndustry(Industries.MINING).setSpecialItem(new SpecialItemData(Items.MANTLE_BORE, null));
        Desmond_market.getIndustry(Industries.MEGAPORT).setSpecialItem(new SpecialItemData(Items.FULLERENE_SPOOL, null));

        Desmond_market.setSurveyLevel(MarketAPI.SurveyLevel.FULL);
        Desmond.setCustomDescriptionId("vri_planet_Desmond"); //reference descriptions.csv
        //Asteroid field
        SectorEntityToken RoyceAF1 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldTerrainPlugin.AsteroidFieldParams(
                        200f, // min radius
                        300f, // max radius
                        8, // min asteroid count
                        16, // max asteroid count
                        4f, // min asteroid radius
                        16f, // max asteroid radius
                        "Asteroids Field")); // null for default name
        RoyceAF1.setCircularOrbit(RoyceStar, 130, asteroids1Dist, 240);

        //Asteroid field
        SectorEntityToken RoyceAF2 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldTerrainPlugin.AsteroidFieldParams(
                        200f, // min radius
                        300f, // max radius
                        8, // min asteroid count
                        16, // max asteroid count
                        4f, // min asteroid radius
                        16f, // max asteroid radius
                        "Asteroids Field")); // null for default name
        RoyceAF2.setCircularOrbit(RoyceStar, 180, asteroids2Dist, 280);

        //Asteroid field big
        SectorEntityToken RoyceAF3 = system.addTerrain(Terrain.ASTEROID_FIELD,
                new AsteroidFieldTerrainPlugin.AsteroidFieldParams(
                        800f, // min radius
                        900f, // max radius
                        24, // min asteroid count
                        48, // max asteroid count
                        8f, // min asteroid radius
                        16f, // max asteroid radius
                        "Big Asteroids Field")); // null for default name
        RoyceAF3.setCircularOrbit(RoyceStar, 300, asteroids3Dist, 350);

        //Dust belt
        system.addRingBand(RoyceStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dustDist, 330f);
        system.addRingBand(RoyceStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dustDist, 310f);

        //Asteroid belt thin
        system.addAsteroidBelt(RoyceStar, 1000, asteroids4Dist, 800, 250, 400, Terrain.ASTEROID_BELT, "Inner Band");

        //add Comm relay
        SectorEntityToken MakeshiftRelay = system.addCustomEntity("vri_comm_relay_makeshift", // unique id
                "Outer Royce Comm Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        MakeshiftRelay.setCircularOrbitPointingDown(RoyceStar, 180f, comDist, 265);

        // Nav beacon
        SectorEntityToken NavBeacon = system.addCustomEntity("vri_nav_buoy_makeshift", // unique id
                "Outer Royce Nav Beacon", // name - if null, defaultName from custom_entities.json will be used
                "nav_buoy_makeshift", // type of object, defined in custom_entities.json
                "vri"); // faction
        NavBeacon.setCircularOrbitPointingDown(RoyceStar, 90f, navDist, 305);

        // Sensor relay
        SectorEntityToken SensorRelay = system.addCustomEntity("vri_sensor_array", // unique id
                "Royce Sensor Relay", // name - if null, defaultName from custom_entities.json will be used
                "sensor_array", // type of object, defined in custom_entities.json
                "vri"); // faction
        SensorRelay.setCircularOrbitPointingDown(RoyceStar, 70f, sensorDist, 200);

        //Jump point
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "Center_jump",
                "Center System Jump");

        jumpPoint1.setCircularOrbit(system.getEntityById("vri_star_royce"), 10, jumpCenterDist, 200f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(
                "fringe_jump",
                "Fringe System Jump");

        jumpPoint2.setCircularOrbit(system.getEntityById("vri_star_royce"), 2, jumpFringeDist, 1000f);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint1);
        system.addEntity(jumpPoint2);

        system.autogenerateHyperspaceJumpPoints(true, false);
    }

}
