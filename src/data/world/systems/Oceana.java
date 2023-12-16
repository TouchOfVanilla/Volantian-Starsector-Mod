package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.SectorProcGen;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import data.world.VRIGen;

import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Oceana {
    public void generate(SectorAPI sector){
        StarSystemAPI system = sector.createStarSystem("Oceana");
        system.getLocation().set(16000, -18000);
        system.setBackgroundTextureFilename("graphics/backgrounds/vribg1.jpg");
        PlanetAPI oceanastar = system.initStar("oceana",StarTypes.BLUE_GIANT, 550f, 700, 10f, 0.7f, 5f);

        PlanetAPI sylvana = system.addPlanet("vri_planet_sylvana",
                oceanastar,
                "Sylvana",
                "gas_giant",
                90f,
                400f,
                3000f,
                80f);

        system.addRingBand(sylvana, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, 600f, 330f);
        CustomCampaignEntityAPI elsetterminal = system.addCustomEntity("elset_terminal", "Elset Terminal", "station_side03", Factions.INDEPENDENT);
        elsetterminal.setCircularOrbitPointingDown(sylvana, 90f, 600f, 90f);
        system.addAsteroidBelt(oceanastar, 500, 4100f, 300, 90,60);
        MarketAPI elsetterminalmarket = VRIGen.addMarketplace(
                Factions.INDEPENDENT,
                elsetterminal,
                null,
                "Elset Terminal",
                4,
                new ArrayList<String>(Arrays.asList(
                        Conditions.POPULATION_4,
                        Conditions.OUTPOST
                )),
                new ArrayList<String>(Arrays.asList(
                        Submarkets.SUBMARKET_OPEN,
                        Submarkets.SUBMARKET_BLACK,
                        Submarkets.SUBMARKET_STORAGE,
                        "midline_market"
                )),
                new ArrayList<String>(Arrays.asList(
                        Industries.POPULATION,
                        Industries.MEGAPORT,
                        Industries.FUELPROD,
                        Industries.REFINING,
                        Industries.HEAVYBATTERIES,
                        Industries.PATROLHQ,
                        Industries.WAYSTATION,
                        Industries.STARFORTRESS_MID

                )),
                0.3f,
                true,
                true
        );
        elsetterminal.setMarket(elsetterminalmarket);

        elsetterminal.setCustomDescriptionId("vri_elsetterminal");

        PlanetAPI Elsatia = system.addPlanet(
                "elsatia",
                oceanastar,
                "Elsatia",
                "cryovolcanic",
                145f,
                130f,
                3750f,
                120f
        );
        MarketAPI elsatiamarket = VRIGen.addMarketplace(
                Factions.INDEPENDENT,
                Elsatia,
                null,
                "Elsatia",
                5,
                new ArrayList<String>(Arrays.asList(
                        Conditions.POPULATION_5,
                        Conditions.OUTPOST
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
                        Industries.REFINING,
                        Industries.HEAVYBATTERIES,
                        Industries.PATROLHQ,
                        Industries.WAYSTATION,
                        Industries.STARFORTRESS_MID
                )),
                0.3f,
                true,
                true
        );
Elsatia.setCustomDescriptionId("vri_elsatia"); //reference descriptions.csv
        SectorEntityToken oceana_gate = system.addCustomEntity("oceana_gate", // unique id
                "Oceana Gate", // name - if null, defaultName from custom_entities.json will be used
                "inactive_gate", // type of object, defined in custom_entities.json
                null); // faction
        oceana_gate.setCircularOrbit(Elsatia, 230-180, 500, 90);

        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, oceanastar, StarAge.OLD,
                3, 5, // min/max entities to add
                5500, // radius to start adding at
                1, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                true); // whether to allow habitable worlds
        system.autogenerateHyperspaceJumpPoints(true, true);
        Misc.setAllPlanetsSurveyed(system, true);
        system.setEnteredByPlayer(true);


    }

    }
