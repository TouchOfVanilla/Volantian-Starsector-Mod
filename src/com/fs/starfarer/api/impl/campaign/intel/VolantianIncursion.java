package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.IntelDataAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.MutableStatWithTempMods;
import com.fs.starfarer.api.impl.campaign.NPCHassler;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivTracker;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.impl.campaign.intel.events.PerseanLeagueHostileActivityFactor;
import com.fs.starfarer.api.impl.campaign.intel.group.*;
import com.fs.starfarer.api.impl.campaign.missions.FleetCreatorMission;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithTriggers;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.utilities.NexUtilsFaction;
import org.lwjgl.util.vector.Vector2f;

import java.util.Iterator;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.intel.group.PerseanLeagueBlockade.*;

public class VolantianIncursion extends GenericRaidFGI {
    public static String KEY = "$vriIncursion_ref";

    public VolantianIncursion(GenericRaidParams raidParams) {
        super(raidParams);
        Global.getSector().getMemoryWithoutUpdate().set(KEY, this);
    }

    protected void notifyEnding() {
        super.notifyEnding();
        Global.getSector().getMemoryWithoutUpdate().unset(KEY);
    }

    protected void notifyEnded() {
        super.notifyEnded();
    }


    @Override
    protected CampaignFleetAPI createFleet(int size, float damage) {

        Random r = getRandom();

        Vector2f loc = origin.getLocationInHyperspace();

        FleetCreatorMission m = new FleetCreatorMission(r);
        m.beginFleet();

            m.triggerCreateFleet(HubMissionWithTriggers.FleetSize.MAXIMUM, HubMissionWithTriggers.FleetQuality.SMOD_2, params.factionId, FleetTypes.MERC_ARMADA, loc);
            m.triggerSetFleetOfficers(HubMissionWithTriggers.OfficerNum.MORE, HubMissionWithTriggers.OfficerQuality.HIGHER);
            m.triggerSetFleetFlag(ARMADA);
            m.setName("Grand Incursion");

            m.triggerSetFleetType(FleetTypes.MERC_ARMADA);
            m.triggerSetFleetDoctrineQuality(5, 5, 5);
            m.triggerSetFleetDoctrineOther(5, 0);
            m.triggerSetFleetComposition(0f, 0f, 0f, 0f, 0f);
            m.triggerFleetMakeFaster(true, 1, false);

            m.triggerFleetAddCommanderSkill(Skills.CREW_TRAINING, 1);
            m.triggerFleetAddCommanderSkill(Skills.COORDINATED_MANEUVERS, 1);
            m.triggerFleetAddCommanderSkill(Skills.TACTICAL_DRILLS, 1);
            m.triggerFleetAddCommanderSkill(Skills.CARRIER_GROUP, 1);


        m.setFleetSource(params.source);
        m.setFleetDamageTaken(damage);

        m.triggerSetPatrol();
        m.triggerMakeLowRepImpact();
        m.triggerMakeAlwaysSpreadTOffHostility();


        CampaignFleetAPI fleet = m.createFleet();



        if (fleet != null) {
            fleet.getCommander().setRankId(Ranks.SPACE_ADMIRAL);
            setNeverStraggler(fleet);
        }

        return fleet;
    }

//	@Override
//	public void abort() {
//		if (!isAborted()) {
//			for (CampaignFleetAPI curr : getFleets()) {
//				curr.getMemoryWithoutUpdate().set(ABORTED_OR_ENDING, true);
//			}
//		}
//		super.abort();
//	}


    @Override
    public void advance(float amount) {
        super.advance(amount);
    }


    @Override
    protected void addPostAssessmentSection(TooltipMakerAPI info, float width, float height, float opad) {
        info.addPara("The incursion forces will encase your colonies in mines if they succeed.", opad, Misc.getNegativeHighlightColor(), "encase the colonies in mines");
//		bullet(info);
//		info.addPara("Forcing the Grand Armada to withdraw will defeat the blockade", opad);
//		info.addPara("Forcing both supply fleets to withdraw will defeat the blockade", 0f);
//		unindent(info);
    }
    public boolean hasCustomRaidAction() {
        return true;
    }

    public void doCustomRaidAction(CampaignFleetAPI fleet, MarketAPI market, float raidStr) {
        market.addCondition(Conditions.POLLUTION);
        DecivTracker.decivilize(market, false, false);
        EncasedIntel intel = new EncasedIntel(market, market.getPrimaryEntity(), false, false);
        Global.getSector().getIntelManager().addIntel(intel);
        market.getStarSystem().addOrbitalJunk(market.getPrimaryEntity(), "orbital_mines",140, 12, 20, market.getPrimaryEntity().getRadius(), market.getPrimaryEntity().getRadius()/3, market.getPrimaryEntity().getRadius()/2, market.getPrimaryEntity().getRadius() - 10, 60f, 360f);
    }
}