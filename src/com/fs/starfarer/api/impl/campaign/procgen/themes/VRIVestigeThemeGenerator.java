package com.fs.starfarer.api.impl.campaign.procgen.themes;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.ai.CampaignFleetAIAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BattleCreationContext;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.FleetEncounterContext;
import com.fs.starfarer.api.impl.campaign.FleetInteractionDialogPluginImpl;
import com.fs.starfarer.api.impl.campaign.VRIDerelictSpawner;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.procgen.Constellation;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.ThemeGenContext;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.*;
import java.util.List;

public class VRIVestigeThemeGenerator extends BaseThemeGenerator {

    Boolean ConstellationHasNoDeciv = false;
    Constellation PickedConstellation = null;
    private static Logger log = Global.getLogger(VRIVestigeThemeGenerator.class);


    @Override
    public int getOrder() {
        return 5;
    }

    @Override
    public String getThemeId() {
        return VRIThemes.VESTIGES;
    }

    @Override
    public void generateForSector(ThemeGenContext context, float allowedSectorFraction) {
        Constellation VestigeConstellation = this.pickVestigeConstellation(context);
        Iterator<StarSystemAPI> VestSysIter = VestigeConstellation.getSystems().iterator();
        float numsys = (float)VestigeConstellation.getSystems().size();
        float x = 1;
        float stationchance = x/numsys;
        while (VestSysIter.hasNext()){
            StarSystemAPI system = VestSysIter.next();
            if (stationchance < (float)Math.random()){
                this.PopulateVestiges(system, false);
                x++;
            } else{
                this.PopulateVestiges(system, true);
                log.info("Put a Vestige Nexus in the " + system.getName() + " star system");
            }
            addBeacon(system);
        }

        log.info("Finished generating vestiges in the " + VestigeConstellation.getName() + " constellation");
    }
    public Constellation pickVestigeConstellation(ThemeGenContext context){
        ArrayList ValidConstellations = new ArrayList<Constellation>();
        List<Constellation> constellations = this.getSortedAvailableConstellations(context, false, new Vector2f(), (List)null);
        Collections.reverse(constellations);

        ListIterator<Constellation> constellationIter = constellations.listIterator();

        while(constellationIter.hasNext()){
            Constellation constellation = constellationIter.next();
            ListIterator<StarSystemAPI> starIter = constellation.getSystems().listIterator();
            while (starIter.hasNext()){
                StarSystemAPI star = starIter.next();
                ///Conditions for stars go here
                if (star.isProcgen() && !star.hasTag("theme_remnant")){
                    ListIterator<PlanetAPI> planetIter = star.getPlanets().listIterator();
                    while (planetIter.hasNext()){
                        PlanetAPI planet = planetIter.next();
                        ///Conditions for planets go here
                        if (planet.hasCondition(Conditions.DECIVILIZED_SUBPOP)){
                            ValidConstellations.add(constellation);

                        }

                    }
                }

            }
        }
        if (ValidConstellations.isEmpty()){
            ConstellationHasNoDeciv = true;
            PickedConstellation = constellations.get(MathUtils.getRandomNumberInRange(0, constellations.size() - 1));
        } else if (!ValidConstellations.isEmpty()){
            PickedConstellation = (Constellation) ValidConstellations.get(MathUtils.getRandomNumberInRange(0, ValidConstellations.size()));
        }
        return PickedConstellation;
    }
    public void PopulateVestiges(StarSystemAPI system, boolean IsStation){
        VRIVestigeSeededFleetManager fleets;
        fleets = new VRIVestigeSeededFleetManager(system, 6, 15, 8, 16, 0.5f);
        system.addScript(fleets);
        BaseThemeGenerator.StarSystemData data = computeSystemData(system);

        if (IsStation){
            List<CampaignFleetAPI> stations = this.addBattlestations(data, 1.0F, 1, 1, this.createStringPicker(new Object[]{"remnant_station2_Standard", 10.0F}));
            Iterator var25 = stations.iterator();

            while(var25.hasNext()) {
                CampaignFleetAPI station = (CampaignFleetAPI)var25.next();
                int maxFleets = 8 + this.random.nextInt(5);
                VRIVestigeStationFleetManager activeFleets = new VRIVestigeStationFleetManager(station, 1.0F, 0, maxFleets, 15.0F, 8, 24);
                system.addScript(activeFleets);
            }
            this.addInactiveGate(data, 1, 0.5F, 0.5F, this.createStringPicker(new Object[]{"hegemony", 10.0F, "remnant", 7.0F, "derelict", 3.0F}));

        }
        this.addObjectives(data, 1);
        this.addShipGraveyard(data, 0.25F, 1, 3, this.createStringPicker(new Object[]{"mercenary", 10.0F, "remnant", 7.0F, "independent", 10.0F}));
        this.addMiningStations(data, 1, 1, 1, this.createStringPicker(new Object[]{"station_mining_remnant", 10.0F}));
        this.addResearchStations(data, 1, 1, 2, this.createStringPicker(new Object[]{"station_research_remnant", 10.0F}));
        this.addDebrisFields(data, 1F, 3, 7);
        this.addDerelictShips(data, 1F, 4, 8, this.createStringPicker(new Object[]{"mercenary", 10.0F, "remnant", 7.0F, "independent", 3.0F}));
        this.addCaches(data, 1F, 0, 3, this.createStringPicker(new Object[]{"weapons_cache_remnant", 10.0F, "weapons_cache_small_remnant", 10.0F, "supply_cache", 10.0F, "supply_cache_small", 10.0F, "equipment_cache", 10.0F, "equipment_cache_small", 10.0F}));
        }
    public List<CampaignFleetAPI> addBattlestations(BaseThemeGenerator.StarSystemData data, float chanceToAddAny, int min, int max, WeightedRandomPicker<String> stationTypes) {
        List<CampaignFleetAPI> result = new ArrayList();
        if (this.random.nextFloat() >= chanceToAddAny) {
            return result;
        } else {
            int num = min + this.random.nextInt(max - min + 1);
            if (DEBUG) {
                System.out.println("    Adding " + num + " battlestations");
            }

            for(int i = 0; i < num; ++i) {
                BaseThemeGenerator.EntityLocation loc = pickCommonLocation(this.random, data.system, 200.0F, true, (Set)null);
                String type = (String)stationTypes.pick();
                if (loc != null) {
                    CampaignFleetAPI fleet = FleetFactoryV3.createEmptyFleet("vestige", "battlestation", (MarketAPI)null);
                    FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, type);
                    fleet.getFleetData().addFleetMember(member);
                    fleet.getMemoryWithoutUpdate().set("$cfai_makeAggressive", true);
                    fleet.getMemoryWithoutUpdate().set("$cfai_noJump", true);
                    fleet.getMemoryWithoutUpdate().set("$cfai_makeAllowDisengage", true);
                    fleet.addTag("neutrino_high");
                    fleet.setStationMode(true);
                    addVestigeStationInteractionConfig(fleet);
                    data.system.addEntity(fleet);
                    fleet.clearAbilities();
                    fleet.addAbility("transponder");
                    fleet.getAbility("transponder").activate();
                    fleet.getDetectedRangeMod().modifyFlat("gen", 1000.0F);
                    fleet.setAI((CampaignFleetAIAPI)null);
                    setEntityLocation(fleet, loc, (String)null);
                    convertOrbitWithSpin(fleet, 5.0F);
                    boolean damaged = type.toLowerCase().contains("damaged");
                    String coreId = "alpha_core";
                    if (damaged) {
                        fleet.getMemoryWithoutUpdate().set("$damagedStation", true);
                        fleet.setName(fleet.getName() + " (Damaged)");
                    }

                    AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin(coreId);
                    PersonAPI commander = plugin.createPerson(coreId, fleet.getFaction().getId(), this.random);
                    fleet.setCommander(commander);
                    fleet.getFlagship().setCaptain(commander);
                    if (!damaged) {
                        RemnantOfficerGeneratorPlugin.integrateAndAdaptCoreForAIFleet(fleet.getFlagship());
                        RemnantOfficerGeneratorPlugin.addCommanderSkills(commander, fleet, (FleetParamsV3)null, 3, this.random);
                    }

                    member.getRepairTracker().setCR(member.getRepairTracker().getMaxCR());
                    result.add(fleet);
                }
            }

            return result;
        }
    }
    public static void addVestigeStationInteractionConfig(CampaignFleetAPI fleet) {
        fleet.getMemoryWithoutUpdate().set("$fidConifgGen", new VRIVestigeThemeGenerator.VestigeStationInteractionConfigGen());
    }
    public static class VestigeStationInteractionConfigGen implements FleetInteractionDialogPluginImpl.FIDConfigGen {
        public VestigeStationInteractionConfigGen() {
        }

        public FleetInteractionDialogPluginImpl.FIDConfig createConfig() {
            FleetInteractionDialogPluginImpl.FIDConfig config = new FleetInteractionDialogPluginImpl.FIDConfig();
            config.alwaysAttackVsAttack = true;
            config.leaveAlwaysAvailable = true;
            config.showFleetAttitude = false;
            config.showTransponderStatus = false;
            config.showEngageText = false;
            config.delegate = new FleetInteractionDialogPluginImpl.BaseFIDDelegate() {
                public void postPlayerSalvageGeneration(InteractionDialogAPI dialog, FleetEncounterContext context, CargoAPI salvage) {
                    (new RemnantSeededFleetManager.RemnantFleetInteractionConfigGen()).createConfig().delegate.postPlayerSalvageGeneration(dialog, context, salvage);
                }

                public void notifyLeave(InteractionDialogAPI dialog) {
                }

                public void battleContextCreated(InteractionDialogAPI dialog, BattleCreationContext bcc) {
                    bcc.aiRetreatAllowed = false;
                    bcc.objectivesAllowed = false;
                }
            };
            return config;
        }
    }

    public static CustomCampaignEntityAPI addBeacon(StarSystemAPI system) {
        SectorEntityToken anchor = system.getHyperspaceAnchor();
        List<SectorEntityToken> points = Global.getSector().getHyperspace().getEntities(JumpPointAPI.class);
        float minRange = 600.0F;
        float closestRange = Float.MAX_VALUE;
        JumpPointAPI closestPoint = null;
        Iterator var8 = points.iterator();

        while(var8.hasNext()) {
            SectorEntityToken entity = (SectorEntityToken)var8.next();
            JumpPointAPI point = (JumpPointAPI)entity;
            if (!point.getDestinations().isEmpty()) {
                JumpPointAPI.JumpDestination dest = (JumpPointAPI.JumpDestination)point.getDestinations().get(0);
                if (dest.getDestination().getContainingLocation() == system) {
                    float dist = Misc.getDistance(anchor.getLocation(), point.getLocation());
                    if (!(dist < minRange + point.getRadius()) && dist < closestRange) {
                        closestPoint = point;
                        closestRange = dist;
                    }
                }
            }
        }

        CustomCampaignEntityAPI beacon = Global.getSector().getHyperspace().addCustomEntity((String)null, (String)null, "warning_beacon", "vestige");

        float orbitDays;
        if (closestPoint == null) {
            orbitDays = minRange / (10.0F + StarSystemGenerator.random.nextFloat() * 5.0F);
            beacon.setCircularOrbitPointingDown(anchor, StarSystemGenerator.random.nextFloat() * 360.0F, minRange, orbitDays);
        } else {
            orbitDays = 20.0F + StarSystemGenerator.random.nextFloat() * 20.0F;
            float angle = Misc.getAngleInDegrees(anchor.getLocation(), closestPoint.getLocation()) + orbitDays;
            if (closestPoint.getOrbit() != null) {
                OrbitAPI orbit = Global.getFactory().createCircularOrbitPointingDown(anchor, angle, closestRange, closestPoint.getOrbit().getOrbitalPeriod());
                beacon.setOrbit(orbit);
            } else {
                Vector2f beaconLoc = Misc.getUnitVectorAtDegreeAngle(angle);
                beaconLoc.scale(closestRange);
                Vector2f.add(beaconLoc, anchor.getLocation(), beaconLoc);
                beacon.getLocation().set(beaconLoc);
            }
        }

        Color glowColor = new Color(12, 200, 234, 255);
        Color pingColor = new Color(255, 255, 255, 255);
        Misc.setWarningBeaconColors(beacon, glowColor, pingColor);
        return beacon;
    }
}