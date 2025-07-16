package data.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.procgen.NebulaEditor;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.ThemeGenContext;
import com.fs.starfarer.api.impl.campaign.procgen.themes.Themes;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import com.fs.starfarer.api.impl.campaign.terrain.HyperspaceTerrainPlugin;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_CrossmodPlugins;
import data.world.systems.*;
import exerelin.utilities.NexUtilsAstro;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

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
        new Uelyst().generate(sector);
        new Royce().generate(sector);
        new Avery().generate(sector);
        new Espoz().generate(sector);


        StarSystemAPI uelyst = Global.getSector().getStarSystem("Uelyst");
        StarSystemAPI royce = Global.getSector().getStarSystem("Royce");
        StarSystemAPI avery = Global.getSector().getStarSystem("Avery");
        StarSystemAPI espoz = Global.getSector().getStarSystem("Espoz");


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

        if (Global.getSettings().getModManager().isModEnabled("vic")){
            VRI_CrossmodPlugins.initVICCrossmod();
        }
    }
    public static void addTransShips(SectorAPI sector){
        Iterator<StarSystemAPI> stariter = sector.getStarSystems().iterator();
        ArrayList<StarSystemAPI> validstars = new ArrayList<StarSystemAPI>();
        while (stariter.hasNext()){
            StarSystemAPI star = stariter.next();
            if (star.isProcgen()){
                validstars.add(star);
            }
        }
        Collections.shuffle(validstars);
        StarSystemAPI targetstar = validstars.get(0);
        SectorEntityToken entity = targetstar.getPlanets().get(0);
        TTBlackSite.addDerelict(targetstar, entity, "estragon_volantian_Elite", "TTS Trogen", "v0lantian_paragon_trAns", ShipRecoverySpecial.ShipCondition.PRISTINE, entity.getRadius() +300, true);
        //log.info("estrogen paragon is in" + targetstar.getName());
        Collections.shuffle(validstars);
        targetstar = validstars.get(0);
        entity = targetstar.getPlanets().get(0);
        TTBlackSite.addDerelict(targetstar, entity, "volantian_testostral_Elite", "TTS Tosterone", "trans_volantian_astral", ShipRecoverySpecial.ShipCondition.PRISTINE, entity.getRadius() +300, true);
        //log.info("testosterone astral is in" + targetstar.getName());
    }
    public static void cleanup(StarSystemAPI system){
        HyperspaceTerrainPlugin plugin = (HyperspaceTerrainPlugin) Misc.getHyperspaceTerrain().getPlugin();
        NebulaEditor editor = new NebulaEditor(plugin);
        float minRadius = plugin.getTileSize() * 2f;

        float radius = system.getMaxRadiusInHyperspace();
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius * 0.5f, 0f, 360f);
        editor.clearArc(system.getLocation().x, system.getLocation().y, 0, radius + minRadius, 0f, 360f, 0.25f);
    }
}
