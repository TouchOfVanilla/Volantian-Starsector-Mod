package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.econ.WorldWater;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import data.world.VRIGen;
import org.lazywizard.lazylib.MathUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Espoz {

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Espoz");
        system.getLocation().set(17000, -15000);
        system.setBackgroundTextureFilename("graphics/backgrounds/vribg1.jpg");
        PlanetAPI espoz = system.initStar(
                "espoz_star",
                "star_yellow",
                550,
                300f,
                10f,
                0.5f,
                5f
        );
        StarSystemGenerator.addOrbitingEntities(system, espoz, StarAge.OLD,
                3, 5, // min/max entities to add
                4200, // radius to start adding at
                2, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                true); // whether to allow habitable worlds
        PlanetAPI Aberdeen = system.addPlanet(
                "aberdeen",
                espoz,
                "Aberdeen",
                "storm",
                90f,
                130f,
                3000f,
                120f
        );
        Misc.initConditionMarket(Aberdeen);
        MarketAPI aberdeenmarket = Aberdeen.getMarket();
        aberdeenmarket.addCondition(Conditions.HABITABLE);
        aberdeenmarket.addCondition(Conditions.FARMLAND_ADEQUATE);
        aberdeenmarket.addCondition(Conditions.ORE_MODERATE);
        aberdeenmarket.addCondition(Conditions.ORGANICS_TRACE);
        aberdeenmarket.addCondition(Conditions.EXTREME_WEATHER);
        aberdeenmarket.addCondition(Conditions.RUINS_EXTENSIVE);
        aberdeenmarket.addCondition(Conditions.DECIVILIZED);
        aberdeenmarket.setPrimaryEntity(Aberdeen);
        Aberdeen.setMarket(aberdeenmarket);
        Aberdeen.setCustomDescriptionId("vri_aberdeen"); //reference descriptions.csv

        PlanetAPI Visegrad = system.addPlanet(
                "visegrad",
                espoz,
                "Visegrad",
                "desert",
                45f,
                90f,
                2000f,
                90f
        );
        Misc.initConditionMarket(Visegrad);
        MarketAPI visegradmarket = Visegrad.getMarket();
        visegradmarket.setPrimaryEntity(Visegrad);
        Visegrad.setMarket(visegradmarket);
        MarketAPI visegradcolony = VRIGen.addMarketplace(
                "hegemony",
                Visegrad,
                null,
                "Visegrad",
                4,

                new ArrayList<String>(
                        Arrays.asList(
                                Conditions.POPULATION_4,
                                Conditions.OUTPOST,
                                Conditions.HABITABLE,
                                Conditions.FARMLAND_POOR,
                                Conditions.ORE_MODERATE,
                                Conditions.RARE_ORE_SPARSE,
                                Conditions.DARK
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
                                Industries.HEAVYBATTERIES,
                                Industries.ORBITALSTATION,
                                Industries.MILITARYBASE,
                                Industries.FUELPROD
                        )
                ),
                //tariffs
                0.3f,
                //freeport
                false,
                //junk and chatter
                true);
        Visegrad.setCustomDescriptionId("vri_visegrad"); //reference descriptions.csv
        StarSystemGenerator.addOrbitingEntities(system, espoz, StarAge.OLD,
                3, 5, // min/max entities to add
                3500, // radius to start adding at
                2, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                true); // whether to allow habitable worlds
        system.autogenerateHyperspaceJumpPoints(true, false);
        SectorEntityToken MakeshiftRelay = system.addCustomEntity("vri_comm_relay_makeshift", // unique id
                "Visegrad Relay", // name - if null, defaultName from custom_entities.json will be used
                "comm_relay_makeshift", // type of object, defined in custom_entities.json
                "hegemony"); // faction
        MakeshiftRelay.setCircularOrbitPointingDown(Visegrad, 180f, 600, 265);
        system.autogenerateHyperspaceJumpPoints(true, true);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "espoz_center_jump",
                "Inner System Jump Point");

        jumpPoint1.setCircularOrbit(espoz, 10, 1250, 100f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        PlanetAPI Aether = system.addPlanet(
                "aether",
                espoz,
                "Aether",
                "barren-desert",
                45f,
                120f,
                1500f,
                90f
        );
        MarketAPI aethermarket = Aether.getMarket();
        aethermarket.addCondition(Conditions.THIN_ATMOSPHERE);
        aethermarket.addCondition(Conditions.HOT);
        aethermarket.addCondition(Conditions.ORE_MODERATE);

    PlanetAPI Chesapeake = system.addPlanet(
            "chesapeake",
            espoz,
            "Chesapeake",
            Planets.PLANET_WATER,
            145f,
            110f,
            3500f,
            120f
    );
    MarketAPI chesapeakemarket = VRIGen.addMarketplace(
            Factions.INDEPENDENT,
            Chesapeake,
            null,
            "Chesapeake",
            5,
            new ArrayList<String>(Arrays.asList(
                    Conditions.POPULATION_5,
                    Conditions.HABITABLE,
                    Conditions.ORGANICS_ABUNDANT,
                    Conditions.WATER_SURFACE,
                    Conditions.WATER
            )),
            new ArrayList<String>(Arrays.asList(
                    Submarkets.SUBMARKET_OPEN,
                    Submarkets.SUBMARKET_BLACK,
                    Submarkets.SUBMARKET_STORAGE
            )),
            new ArrayList<String>(Arrays.asList(
                    Industries.POPULATION,
                    Industries.MEGAPORT,
                    Industries.MINING,
                    Industries.AQUACULTURE,
                    Industries.HEAVYBATTERIES,
                    Industries.MILITARYBASE,
                    Industries.WAYSTATION,
                    Industries.STARFORTRESS_MID
            )),
            0.3f,
            true,
            true
    );
        Chesapeake.setCustomDescriptionId("vri_chesapeake"); //reference descriptions.csv

    }
}
