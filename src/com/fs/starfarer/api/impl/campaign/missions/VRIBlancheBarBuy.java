package com.fs.starfarer.api.impl.campaign.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class VRIBlancheBarBuy extends HubMissionWithBarEvent {
    public static Logger log = Global.getLogger(VRIBlancheBarBuy.class);
    public static float PROB_PATROL_AFTER = 1f;
    public FleetMemberAPI VRIShip;
    public PersonAPI Blanche;


    @Override
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        log.info("wololo starting mission creation");
        Blanche = Global.getSector().getImportantPeople().getPerson("blanche");
        //log.info("Blanche exists: " + Blanche != null);

        if (!setPersonMissionRef(Blanche, "$vribar_ref")) {
            log.info("Failed to set person mission ref, hmm");
            return false;
        }
        Iterator shipIter = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().iterator();

        while (shipIter.hasNext()) {
            FleetMemberAPI member = (FleetMemberAPI) shipIter.next();
            log.info("Checking member " + member.getShipName() + ", has hull mods " + member.getHullSpec().getBuiltInMods());
            if (member.getHullSpec().getBuiltInMods().contains("VRI_FluxNetwork")) {
                VRIShip = member;
                log.info("wololo found member " + member.getShipName() + ", returning");
                return true;
            }
        }
        // failed to find any valid ships
        return false;
    }

    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params,
                                 Map<String, MemoryAPI> memoryMap) {
        if ("showShip".equals(action)) {
            dialog.getVisualPanel().showFleetMemberInfo(VRIShip, true);
            return true;
        } else if ("showPerson".equals(action)) {
            dialog.getVisualPanel().showPersonInfo(Blanche, true);
            return true;
        } else if ("spawnFuckFleet".equals(action)) {
            notifyEnding();
            return true;
        } else if ("YoinkHull".equals(action)) {
            Global.getSector().getPlayerFleet().getFleetData().removeFleetMember(VRIShip);
            CampaignFleetAPI otherfleet = (CampaignFleetAPI) dialog.getInteractionTarget();
            otherfleet.getFleetData().addFleetMember(VRIShip);
        }
        return true;
    }

    protected void updateInteractionDataImpl() {
        set("$blanchebuy_hullname", VRIShip.getShipName());
        set("$blanchebuy_hullclass", VRIShip.getHullSpec().getHullNameWithDashClass());
        set("$blanchebuy_hullsize", VRIShip.getHullSpec().getDesignation().toLowerCase());
        set("$blanchebuy_offerprice1", Misc.getWithDGS(VRIShip.getBaseValue()));
        set("$blanchebuy_offerprice2", Misc.getWithDGS(VRIShip.getBaseBuyValue()));
        set("$blanchebuy_ship", VRIShip);

    }

    protected void notifyEnding() {
        super.notifyEnding();
        log.info("The ship in question is" + VRIShip.getShipName());
        if (Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().contains(VRIShip)) {
            log.info("Creating fuck fleet, returning");

            Iterator shipIter = Global.getSector().getPlayerFleet().getFleetData().getMembersListCopy().iterator();

            while (shipIter.hasNext()) {
                FleetMemberAPI member = (FleetMemberAPI) shipIter.next();
                log.info("Checking member " + member.getShipName() + ", has hull mods " + member.getHullSpec().getBuiltInMods());
                if (member.getHullSpec().getBuiltInMods().contains("VRI_FluxNetwork")) {
                    VRIShip = member;
                    log.info("wololo found member for interception fleet, " + member.getShipName() + ", returning");
                }
            }


            FactionAPI patrolFaction = Global.getSector().getFaction("vri");

            DelayedFleetEncounter e = new DelayedFleetEncounter(this.genRandom, this.getMissionId());
            e.setDelayNone();
            e.setEncounterInHyper();
            e.setLocationInnerSector(true, "vri");
            e.beginCreate();
            e.triggerCreateFleet(FleetSize.LARGE, FleetQuality.DEFAULT, "vri", "patrolLarge", new Vector2f());
            e.setFleetWantsThing("vri", "the Volantian hull", "they", "the Volantian hull that you refused to sell " + "Blanche Star", 14, true, ComplicationRepImpact.FULL, DelayedFleetEncounter.TRIGGER_REP_LOSS_MINOR, Blanche);
            e.triggerSetAdjustStrengthBasedOnQuality(true, this.getQuality());
            e.triggerSetPatrol();
            e.triggerSetStandardAggroInterceptFlags();
            e.endCreate();
        }

    }
}
