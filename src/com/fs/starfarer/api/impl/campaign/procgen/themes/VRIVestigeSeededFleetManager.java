package com.fs.starfarer.api.impl.campaign.procgen.themes;

import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.fleets.SeededFleetManager;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.lwjgl.util.vector.Vector2f;

import java.util.Iterator;
import java.util.Random;

///Thank you alex
///Ripped directly from RemnantSeededFleetManager with tweaks

public class VRIVestigeSeededFleetManager extends SeededFleetManager {
    protected int minPts;
    protected int maxPts;
    protected float activeChance;

    public VRIVestigeSeededFleetManager(StarSystemAPI system, int minFleets, int maxFleets, int minPts, int maxPts, float activeChance) {
        super(system, 1.0F);
        this.minPts = minPts;
        this.maxPts = maxPts;
        this.activeChance = activeChance;
        int num = minFleets + StarSystemGenerator.random.nextInt(maxFleets - minFleets + 1);

        for(int i = 0; i < num; ++i) {
            long seed = StarSystemGenerator.random.nextLong();
            this.addSeed(seed);
        }

    }

    protected CampaignFleetAPI spawnFleet(long seed) {
        Random random = new Random(seed);
        int combatPoints = this.minPts + random.nextInt(this.maxPts - this.minPts + 1);
        String type = "patrolSmall";
        if (combatPoints > 8) {
            type = "patrolMedium";
        }

        if (combatPoints > 16) {
            type = "patrolLarge";
        }

        combatPoints = (int)((float)combatPoints * 8.0F);
        FleetParamsV3 params = new FleetParamsV3(this.system.getLocation(), "vestige", 1.0F, type, (float)combatPoints, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F);
        params.withOfficers = false;
        params.random = random;
        CampaignFleetAPI fleet = FleetFactoryV3.createFleet(params);
        if (fleet == null) {
            return null;
        } else {
            this.system.addEntity(fleet);
            fleet.setFacing(random.nextFloat() * 360.0F);
            boolean dormant = random.nextFloat() >= this.activeChance;
            int numActive = 0;
            Iterator var11 = this.fleets.iterator();

            while(var11.hasNext()) {
                SeededFleetManager.SeededFleet f = (SeededFleetManager.SeededFleet)var11.next();
                if (f.fleet != null) {
                    ++numActive;
                }
            }

            if (numActive == 0 && this.activeChance > 0.0F) {
                dormant = false;
            }

            initVestigeFleetProperties(random, fleet, dormant);
            if (dormant) {
                SectorEntityToken target = pickEntityToGuard(random, this.system, fleet);
                if (target != null) {
                    fleet.setCircularOrbit(target, random.nextFloat() * 360.0F, fleet.getRadius() + target.getRadius() + 100.0F + 100.0F * random.nextFloat(), 25.0F + 5.0F * random.nextFloat());
                } else {
                    Vector2f loc = Misc.getPointAtRadius(new Vector2f(), 4000.0F, random);
                    fleet.setLocation(loc.x, loc.y);
                }
            } else {
                fleet.addScript(new VRIVestigeAssignmentAI(fleet, this.system, (SectorEntityToken)null));
            }

            return fleet;
        }
    }

    public static SectorEntityToken pickEntityToGuard(Random random, StarSystemAPI system, CampaignFleetAPI fleet) {
        WeightedRandomPicker<SectorEntityToken> picker = new WeightedRandomPicker(random);

        SectorEntityToken entity;
        Iterator var5;
        float w;
        for(var5 = system.getEntitiesWithTag("salvageable").iterator(); var5.hasNext(); picker.add(entity, w)) {
            entity = (SectorEntityToken)var5.next();
            w = 1.0F;
            if (entity.hasTag("neutrino_high")) {
                w = 3.0F;
            }

            if (entity.hasTag("neutrino_low")) {
                w = 0.33F;
            }
        }

        var5 = system.getJumpPoints().iterator();

        while(var5.hasNext()) {
            entity = (SectorEntityToken)var5.next();
            picker.add(entity, 1.0F);
        }

        return (SectorEntityToken)picker.pick();
    }

    public static void initVestigeFleetProperties(Random random, CampaignFleetAPI fleet, boolean dormant) {
        if (random == null) {
            random = new Random();
        }

        fleet.removeAbility("emergency_burn");
        fleet.removeAbility("sensor_burst");
        fleet.removeAbility("go_dark");
        fleet.getMemoryWithoutUpdate().set("$sawPlayerTransponderOn", true);
        fleet.getMemoryWithoutUpdate().set("$isPatrol", true);
        fleet.getMemoryWithoutUpdate().set("$cfai_longPursuit", true);
        fleet.getMemoryWithoutUpdate().set("$cfai_holdVsStronger", true);
        fleet.getMemoryWithoutUpdate().set("$cfai_noJump", true);
        if (dormant) {
            fleet.setTransponderOn(false);
            fleet.getMemoryWithoutUpdate().set("$cfai_makeAllowDisengage", true);
            fleet.getMemoryWithoutUpdate().set("$cfai_makeAggressive", true);
            fleet.setAI((CampaignFleetAIAPI)null);
            fleet.setNullAIActionText("dormant");
        }

        addVestigeInteractionConfig(fleet);
        long salvageSeed = random.nextLong();
        fleet.getMemoryWithoutUpdate().set("$salvageSeed", salvageSeed);
    }

    public static void addVestigeInteractionConfig(CampaignFleetAPI fleet) {
        fleet.getMemoryWithoutUpdate().set("$fidConifgGen", new VestigeFleetInteractionConfigGen());
    }

    public static class VestigeFleetInteractionConfigGen implements FleetInteractionDialogPluginImpl.FIDConfigGen {
        public VestigeFleetInteractionConfigGen() {
        }

        public FleetInteractionDialogPluginImpl.FIDConfig createConfig() {
            FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
            config.showTransponderStatus = false;
            config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
                public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                    bcc.aiRetreatAllowed = false;
                }
            };
            return config;
        }
    }
}