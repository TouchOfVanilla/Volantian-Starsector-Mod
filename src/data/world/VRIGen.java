package data.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.procgen.themes.VRIVestigeThemeGenerator;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.themes.SectorThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.VRIVestigeThemeGenerator;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.util.Misc;
import data.world.systems.*;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import static com.fs.starfarer.api.impl.campaign.ids.Tags.THEME_RUINS;
import static org.lazywizard.lazylib.MathUtils.getRandomNumberInRange;

public class VRIGen implements SectorGeneratorPlugin {
    public static Logger log = Global.getLogger(VRIGen.class);
    public static float TETHER_SYSTEM_Y_COORDS = 2f;
    public static float TETHER_SYSTEM_X_COORDS = 2f;
    StarSystemAPI tetherSystem;
    public StarSystemAPI Tether_System;
    public PlanetAPI tetherplanet;

    public static MarketAPI addMarketplace(String factionID, SectorEntityToken primaryEntity, ArrayList<SectorEntityToken> connectedEntities, String name,
                                           int size, ArrayList<String> marketConditions, ArrayList<String> submarkets, ArrayList<String> industries, float tarrif,
                                           boolean freePort, boolean withJunkAndChatter) {
        EconomyAPI globalEconomy = Global.getSector().getEconomy();
        String planetID = primaryEntity.getId();
        String marketID = planetID + "_market";

        MarketAPI newMarket = Global.getFactory().createMarket(marketID, name, size);
        newMarket.setFactionId(factionID);
        newMarket.setPrimaryEntity(primaryEntity);
        newMarket.getTariff().modifyFlat("generator", tarrif);

        //Adds submarkets
        if (null != submarkets) {
            for (String market : submarkets) {
                newMarket.addSubmarket(market);
            }
        }

        //Adds market conditions
        for (String condition : marketConditions) {
            newMarket.addCondition(condition);
        }

        //Add market industries
        for (String industry : industries) {
            newMarket.addIndustry(industry);
        }

        //Sets us to a free port, if we should
        newMarket.setFreePort(freePort);

        //Adds our connected entities, if any
        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                newMarket.getConnectedEntities().add(entity);
            }
        }

        globalEconomy.addMarket(newMarket, withJunkAndChatter);
        primaryEntity.setMarket(newMarket);
        primaryEntity.setFaction(factionID);

        if (null != connectedEntities) {
            for (SectorEntityToken entity : connectedEntities) {
                entity.setMarket(newMarket);
                entity.setFaction(factionID);
            }
        }

        //Finally, return the newly-generated market
        return newMarket;
    }

    @Override
    public void generate(SectorAPI sector) {
        FactionAPI vri = sector.getFaction("vri");
        FactionAPI vesties = sector.getFaction("vestige");
        new Uelyst().generate(sector);
        new Royce().generate(sector);
        new Avery().generate(sector);
        new Fevali().generate(sector);
        new Verlat().generate(sector);
        new Espoz().generate(sector);
        new Oceana().generate(sector);

        StarSystemAPI uelyst = Global.getSector().getStarSystem("Uelyst");
        StarSystemAPI royce = Global.getSector().getStarSystem("Royce");
        StarSystemAPI avery = Global.getSector().getStarSystem("Avery");
        StarSystemAPI fevali = Global.getSector().getStarSystem("Fevali");
        StarSystemAPI verlat = Global.getSector().getStarSystem("Verlat");
        StarSystemAPI espoz = Global.getSector().getStarSystem("Espoz");

        SectorThemeGenerator.generators.add(1, new VRIVestigeThemeGenerator());

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("vri");
//Vanilla Factions
        vri.setRelationship(Factions.LUDDIC_CHURCH, -1f); //Hates us
        vri.setRelationship(Factions.LUDDIC_PATH, -1f); //Hates us squared
        vri.setRelationship(Factions.TRITACHYON, -.025f); //We use their ships whether they like it or not
        vri.setRelationship(Factions.PERSEAN, 0.5f); //You hate the hegemony? welcome!
        vri.setRelationship(Factions.PIRATES, -0.5f); //Only WE are allowed to be pirates
        vri.setRelationship(Factions.INDEPENDENT, 0f); //Thx 4 the ships :3
        vri.setRelationship(Factions.DIKTAT, 0f); //Who are you people?!
        vri.setRelationship(Factions.LIONS_GUARD, 0f); //Who are you people?!
        vri.setRelationship(Factions.HEGEMONY, -1f); //Stole our lunch money (and our former glory)
        vri.setRelationship(Factions.REMNANTS, 0f); //Officer, the radiant is not in my fleet, it is chasing me
        vesties.setRelationship(Factions.REMNANTS, -1f); //we are different colors and therefore we must fight


    }
}
