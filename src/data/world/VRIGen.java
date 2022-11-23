package data.world;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.SectorGeneratorPlugin;
import com.fs.starfarer.api.campaign.econ.EconomyAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.shared.SharedData;
import data.world.systems.Gargantua;
import data.world.systems.Royce;
import data.world.systems.Uelyst;

import java.util.ArrayList;

public class VRIGen implements SectorGeneratorPlugin {

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
//        FactionAPI vri = sector.getFaction("vri");

        new Royce().generate(sector);
        new Uelyst().generate(sector);
        //new Gargantua().generate(sector);

        SharedData.getData().getPersonBountyEventData().addParticipatingFaction("vri");

//        vri.setRelationship(Factions.LUDDIC_CHURCH, -1f);
//        vri.setRelationship(Factions.LUDDIC_PATH, -1f);
//        vri.setRelationship(Factions.TRITACHYON, -0.7f);
//        vri.setRelationship(Factions.PERSEAN, 0.3f);
//        vri.setRelationship(Factions.PIRATES, 0f);
//        vri.setRelationship(Factions.INDEPENDENT, -0.2f);
//        vri.setRelationship(Factions.DIKTAT, -0.1f);
//        vri.setRelationship(Factions.LIONS_GUARD, -0.1f);
//        vri.setRelationship(Factions.HEGEMONY, 0.6f);
//        vri.setRelationship(Factions.REMNANTS, -1f);
    }
}
