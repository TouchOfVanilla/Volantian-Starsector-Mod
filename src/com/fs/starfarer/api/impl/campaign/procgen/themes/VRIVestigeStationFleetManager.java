package com.fs.starfarer.api.impl.campaign.procgen.themes;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.impl.campaign.enc.EncounterManager;
import com.fs.starfarer.api.impl.campaign.enc.EncounterPoint;
import com.fs.starfarer.api.impl.campaign.enc.EncounterPointProvider;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.SourceBasedFleetManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class VRIVestigeStationFleetManager extends SourceBasedFleetManager {
///Also taken directly from remnants
    protected int minPts;
    protected int maxPts;
    protected int totalLost;
    protected transient VRIVestigeStationFleetManager.VestigeSystemEPGenerator epGen;
    protected transient boolean addedListener = false;

    public VRIVestigeStationFleetManager(SectorEntityToken source, float thresholdLY, int minFleets, int maxFleets, float respawnDelay, int minPts, int maxPts) {
        super(source, thresholdLY, minFleets, maxFleets, respawnDelay);
        this.minPts = minPts;
        this.maxPts = maxPts;
    }

    protected Object readResolve() {
        return this;
    }

    public void advance(float amount) {
        if (!this.addedListener) {
            this.epGen = new VestigeSystemEPGenerator();
            Global.getSector().getListenerManager().addListener(this.epGen, true);
            this.addedListener = true;
        }

        super.advance(amount);
    }

    protected CampaignFleetAPI spawnFleet() {
        if (this.source == null) {
            return null;
        } else {
            Random random = new Random();
            int combatPoints = this.minPts + random.nextInt(this.maxPts - this.minPts + 1);
            int bonus = this.totalLost * 4;
            if (bonus > this.maxPts) {
                bonus = this.maxPts;
            }

            combatPoints += bonus;
            String type = "patrolSmall";
            if (combatPoints > 8) {
                type = "patrolMedium";
            }

            if (combatPoints > 16) {
                type = "patrolLarge";
            }

            combatPoints = (int)((float)combatPoints * 8.0F);
            FleetParamsV3 params = new FleetParamsV3(this.source.getMarket(), this.source.getLocationInHyperspace(), "vestige", 1.0F, type, (float)combatPoints, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
            params.random = random;
            CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
            if (fleet == null) {
                return null;
            } else {
                LocationAPI location = this.source.getContainingLocation();
                location.addEntity(fleet);
                VRIVestigeSeededFleetManager.initVestigeFleetProperties(random, fleet, false);
                fleet.setLocation(this.source.getLocation().x, this.source.getLocation().y);
                fleet.setFacing(random.nextFloat() * 360.0F);
                fleet.addScript(new VRIVestigeAssignmentAI(fleet, (StarSystemAPI)this.source.getContainingLocation(), this.source));
                fleet.getMemoryWithoutUpdate().set("$sourceId", this.source.getId());
                VRIVestigeSeededFleetManager.addVestCoresToFleet(fleet);
                return fleet;
            }
        }
    }

    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        super.reportFleetDespawnedToListener(fleet, reason, param);
        if (reason == CampaignEventListener.FleetDespawnReason.DESTROYED_BY_BATTLE) {
            String sid = fleet.getMemoryWithoutUpdate().getString("$sourceId");
            if (sid != null && this.source != null && sid.equals(this.source.getId())) {
                ++this.totalLost;
            }
        }

    }

    public int getTotalLost() {
        return this.totalLost;
    }

    public class VestigeSystemEPGenerator implements EncounterPointProvider {
        public VestigeSystemEPGenerator() {
        }

        public List<EncounterPoint> generateEncounterPoints(LocationAPI where) {
            if (!where.isHyperspace()) {
                return null;
            } else if (VRIVestigeStationFleetManager.this.totalLost > 0 && VRIVestigeStationFleetManager.this.source != null) {
                String id = "ep_" + VRIVestigeStationFleetManager.this.source.getId();
                EncounterPoint ep = new EncounterPoint(id, where, VRIVestigeStationFleetManager.this.source.getLocationInHyperspace(), EncounterManager.EP_TYPE_OUTSIDE_SYSTEM);
                ep.custom = this;
                List<EncounterPoint> result = new ArrayList();
                result.add(ep);
                return result;
            } else {
                return null;
            }
        }
    }
}
