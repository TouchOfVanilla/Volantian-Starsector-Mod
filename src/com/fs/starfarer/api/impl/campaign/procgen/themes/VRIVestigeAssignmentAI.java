package com.fs.starfarer.api.impl.campaign.procgen.themes;

import com.fs.starfarer.api.EveryFrameScript;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetAssignment;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.util.Misc;
import org.lwjgl.util.vector.Vector2f;

import java.util.Random;

public class VRIVestigeAssignmentAI implements EveryFrameScript {
    protected StarSystemAPI homeSystem;
    protected CampaignFleetAPI fleet;
    protected SectorEntityToken source;

    public VRIVestigeAssignmentAI(CampaignFleetAPI fleet, StarSystemAPI homeSystem, SectorEntityToken source) {
        this.fleet = fleet;
        this.homeSystem = homeSystem;
        this.source = source;
        this.giveInitialAssignments();
    }

    protected void giveInitialAssignments() {
        boolean playerInSameLocation = this.fleet.getContainingLocation() == Global.getSector().getCurrentLocation();
        if ((playerInSameLocation || (float)Math.random() < 0.1F) && this.source != null) {
            this.fleet.setLocation(this.source.getLocation().x, this.source.getLocation().y);
            this.fleet.addAssignment(FleetAssignment.ORBIT_AGGRESSIVE, this.source, 3.0F + (float)Math.random() * 2.0F);
        } else {
            SectorEntityToken target = VRIVestigeSeededFleetManager.pickEntityToGuard(new Random(), this.homeSystem, this.fleet);
            Vector2f loc;
            if (target != null) {
                loc = Misc.getPointAtRadius(target.getLocation(), target.getRadius() + 100.0F);
                this.fleet.setLocation(loc.x, loc.y);
            } else {
                loc = Misc.getPointAtRadius(new Vector2f(), 5000.0F);
                this.fleet.setLocation(loc.x, loc.y);
            }

            this.pickNext();
        }

    }

    protected void pickNext() {
        boolean standDown = this.source != null && (float)Math.random() < 0.2F;
        if (!standDown) {
            SectorEntityToken target = VRIVestigeSeededFleetManager.pickEntityToGuard(new Random(), this.homeSystem, this.fleet);
            float days;
            if (target != null) {
                days = Misc.getSpeedForBurnLevel(8.0F);
                float dist = Misc.getDistance(this.fleet.getLocation(), target.getLocation());
                float seconds = dist / days;
                days = seconds / Global.getSector().getClock().getSecondsPerDay();
                days += 5.0F + 5.0F * (float)Math.random();
                this.fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, target, days, "patrolling");
                return;
            }

            days = 5.0F + 5.0F * (float)Math.random();
            this.fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, (SectorEntityToken)null, days, "patrolling");
        }

        if (this.source != null) {
            float dist = Misc.getDistance(this.fleet.getLocation(), this.source.getLocation());
            if (dist > 1000.0F) {
                this.fleet.addAssignment(FleetAssignment.PATROL_SYSTEM, this.source, 3.0F, "returning from patrol");
            } else {
                this.fleet.addAssignment(FleetAssignment.ORBIT_PASSIVE, this.source, 3.0F + (float)Math.random() * 2.0F, "standing down");
                this.fleet.addAssignment(FleetAssignment.GO_TO_LOCATION_AND_DESPAWN, this.source, 5.0F);
            }
        }

    }

    public void advance(float amount) {
        if (this.fleet.getCurrentAssignment() == null) {
            this.pickNext();
        }

    }

    public boolean isDone() {
        return false;
    }

    public boolean runWhilePaused() {
        return false;
    }
}

