package data.scripts.campaign.intel.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.FleetEventListener;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.DerelictShipEntityPlugin.DerelictType;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.missions.hub.HubMissionWithBarEvent;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.fs.starfarer.api.impl.campaign.ids.FleetTypes.PATROL_MEDIUM;

public class InternalAffairs extends HubMissionWithBarEvent implements FleetEventListener {

    // chance of also spawning a Remnant fleet
    public static float PROB_REMNANT = 0.5f;
    // time we have to complete the mission
    public static float MISSION_DAYS = 120f;

    // mission stages
    public static enum Stage {
        FIND_CLUE,
        KILL_FLEET,
        COMPLETED,
        FAILED,
    }

    // important objects, systems and people
    protected SectorEntityToken derelict;
    protected SectorEntityToken cache;
    protected CampaignFleetAPI target;
    protected PersonAPI executive;
    protected StarSystemAPI system;
    protected StarSystemAPI system2;

    // whether the bar event show should at any given market
    public boolean shouldShowAtMarket(MarketAPI market) {
        return market.getFactionId().equals(Factions.TRITACHYON);
    }

    // run when the bar event starts / when we ask a contact about the mission
    protected boolean create(MarketAPI createdAt, boolean barEvent) {
        // a simple boolean we'll sometimes flip on and do extra stuff
        boolean remnant = false;

        // if bar event, let's create a person to actually give it to us
        if (barEvent) {
            setGiverRank(Ranks.AGENT);
            setGiverPost(Ranks.POST_EXECUTIVE);
            setGiverImportance(PersonImportance.HIGH);
            setGiverFaction(Factions.TRITACHYON);
            setGiverTags(Tags.CONTACT_MILITARY);
            setGiverVoice(Voices.BUSINESS);
            findOrCreateGiver(createdAt, false, false);
        }

        PersonAPI person = getPerson();
        if (person == null) return false;

        // setting the mission ref allows us to use the Call rulecommand in their dialogues, so that we can make this script do things
        if (!setPersonMissionRef(person, "$intaff_ref")) {
            return false;
        }

        // when we complete the mission from a bar event, always allow us to add the questgiver as a contact
        if (barEvent) {
            setGiverIsPotentialContactOnSuccess(1f);
        }

        // set up the disgraced executive
        executive = Global.getSector().getFaction(Factions.TRITACHYON).createRandomPerson();
        executive.setRankId(Ranks.SPACE_ADMIRAL);
        executive.setPostId(Ranks.POST_SENIOR_EXECUTIVE);
        executive.getMemoryWithoutUpdate().set("$intaff_exec", true);

        // pick the system with the clues inside
        requireSystemInterestingAndNotUnsafeOrCore();
        preferSystemInInnerSector();
        preferSystemUnexplored();
        preferSystemInDirectionOfOtherMissions();

        system = pickSystem(true);
        if (system == null) return false;

        // pick the target fleet's system
        requireSystemInterestingAndNotUnsafeOrCore();
        preferSystemWithinRangeOf(system.getLocation(), 3f);
        preferSystemUnexplored();
        requireSystemNot(system);

        system2 = pickSystem(true);
        if (system2 == null) return false;

        // roll to see if we're adding Remnants
        if (rollProbability(PROB_REMNANT)) {
            remnant = true;
        }

        // determine the faction and ship type of the derelict
        String derelict_faction = Factions.TRITACHYON;
        DerelictType derelict_type = DerelictType.CIVILIAN;
        if (remnant) {
            derelict_faction = Factions.REMNANTS;
            derelict_type = DerelictType.SMALL;
        }

        // spawn a supply cache and derelict ship, both serving as clues. They have memory flags that are checked for in rules.csv
        cache = spawnEntity(Entities.SUPPLY_CACHE, new LocData(EntityLocationType.HIDDEN, null, system));
        derelict = spawnDerelict(derelict_faction, derelict_type, new LocData(EntityLocationType.HIDDEN, null, system));
        cache.getMemoryWithoutUpdate().set("$intaff_clue", true);
        setEntityMissionRef(cache, "$intaff_ref");
        derelict.getMemoryWithoutUpdate().set("$intaff_clue", true);
        setEntityMissionRef(derelict, "$intaff_ref");

        // set up the target fleet. I've done this using the old style, because the trigger-system doesn't support event listeners by default,
        // and we need to know when this fleet dies or despawns
        FleetParamsV3 params = new FleetParamsV3(
                null,
                null,
                Factions.TRITACHYON,
                null,
                PATROL_MEDIUM,
                50f, // combatPts
                10f, // freighterPts
                10f, // tankerPts
                0f, // transportPts
                0f, // linerPts
                0f, // utilityPts
                -0.25f // qualityMod
        );
        // toughen them up, in exchange for the shoddy ship quality we set
        params.averageSMods = 2;
        target = FleetFactoryV3.createFleet(params);

        target.setName(executive.getNameString() + "'s Fleet");
        target.setNoFactionInName(true);

        target.setCommander(executive);
        target.getFlagship().setCaptain(executive);

        Misc.makeHostile(target);
        Misc.makeNoRepImpact(target, "$intaff");
        Misc.makeImportant(target, "$intaff");

        target.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE, "$intaff");
        target.getMemoryWithoutUpdate().set(MemFlags.MEMORY_KEY_MAKE_AGGRESSIVE_ONE_BATTLE_ONLY, "$intaff");
        target.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORED_BY_OTHER_FLEETS, "$intaff");
        target.getMemoryWithoutUpdate().set(MemFlags.FLEET_IGNORES_OTHER_FLEETS, "$intaff");

        target.getMemoryWithoutUpdate().set("$intaff_execfleet", true);
        target.getAI().addAssignment(FleetAssignment.PATROL_SYSTEM, system2.getCenter(), 200f, null);
        target.addEventListener(this);
        system2.addEntity(target);

        // optionally spawn a Remnant fleet when we close in on the executive's system
        if (remnant) {
            beginWithinHyperspaceRangeTrigger(system2,1f, false, Stage.FIND_CLUE, Stage.KILL_FLEET);
            triggerCreateFleet(FleetSize.LARGE, FleetQuality.VERY_HIGH, Factions.REMNANTS, PATROL_MEDIUM, system2);
            triggerMakeHostileAndAggressive();
            triggerAutoAdjustFleetStrengthMajor();
            triggerSetRemnantConfig();
            //triggerMakeFleetIgnoredByOtherFleets();
            triggerPickLocationAtInSystemJumpPoint(system2);
            triggerSpawnFleetAtPickedLocation(null, null);
            triggerOrderFleetPatrol(system2, true, Tags.JUMP_POINT, Tags.SALVAGEABLE, Tags.PLANET);
            endTrigger();
        }

        // set a global reference we can use, useful for once-off missions.
        if (!setGlobalReference("$intaff_ref")) return false;

        // set our starting, success and failure stages
        setStartingStage(Stage.FIND_CLUE);
        setSuccessStage(Stage.COMPLETED);
        setFailureStage(Stage.FAILED);

        // set stage transitions when certain global flags are set, and when certain flags are set on the questgiver
        setStageOnGlobalFlag(Stage.KILL_FLEET, "$intaff_foundclue");
        setStageOnMemoryFlag(Stage.COMPLETED, person, "$intaff_completed");
        setStageOnMemoryFlag(Stage.FAILED, person, "$intaff_failed" );
        // set time limit and credit reward
        setTimeLimit(Stage.FAILED, MISSION_DAYS, system2);
        setCreditReward(CreditReward.HIGH);

        return true;
    }

    // when Call-ing something that isn't a default option for a mission, it'll try and run this method with "action" being the first parameter
    // e.g Call $global.intaff_ref unsetClues
    @Override
    protected boolean callAction(String action, String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        if (action.equals("unsetClues")){
            // make the other clue no longer a clue
            if (cache != null) {
                cache.getMemoryWithoutUpdate().unset("$intaff_clue");
            }
            if (derelict != null) {
                derelict.getMemoryWithoutUpdate().unset("$intaff_clue");
            }
            return true;
        }
        return false;
    }

    // during the initial dialogue and in any dialogue where we use "Call $intaff_ref updateData", these values will be put in memory
    // here, used so we can, say, type $intaff_execName and automatically insert the disgraced executive's name
    protected void updateInteractionDataImpl() {
        set("$intaff_barEvent", isBarEvent());
        set("$intaff_manOrWoman", getPerson().getManOrWoman());
        set("$intaff_heOrShe", getPerson().getHeOrShe());
        set("$intaff_reward", Misc.getWithDGS(getCreditsReward()));

        set("$intaff_personName", getPerson().getNameString());
        set("$intaff_execName", executive.getNameString());
        set("$intaff_systemName", system.getNameWithLowercaseTypeShort());
        set("$intaff_system2Name", system2.getNameWithLowercaseTypeShort());
        set("$intaff_dist", getDistanceLY(system));
    }

    // used to detect when the executive's fleet is destroyed and complete the mission
    public void reportBattleOccurred(CampaignFleetAPI fleet, CampaignFleetAPI primaryWinner, BattleAPI battle) {
        if (isDone() || result != null) return;

        // also credit the player if they're in the same location as the fleet and nearby
        float distToPlayer = Misc.getDistance(fleet, Global.getSector().getPlayerFleet());
        boolean playerInvolved = battle.isPlayerInvolved() || (fleet.isInCurrentLocation() && distToPlayer < 2000f);

        if (battle.isInvolved(fleet) && !playerInvolved) {
            if (fleet.getFlagship() == null || fleet.getFlagship().getCaptain() != target) {
                fleet.setCommander(fleet.getFaction().createRandomPerson());
                getPerson().getMemoryWithoutUpdate().set("$intaff_completed", true);
                return;
            }
        }

        if (!playerInvolved || !battle.isInvolved(fleet) || battle.onPlayerSide(fleet)) {
            return;
        }

        // didn't destroy the original flagship
        if (fleet.getFlagship() != null && fleet.getFlagship().getCaptain() == target) return;

        getPerson().getMemoryWithoutUpdate().set("$intaff_completed", true);

    }

    // if the fleet despawns for whatever reason, fail the mission
    public void reportFleetDespawnedToListener(CampaignFleetAPI fleet, CampaignEventListener.FleetDespawnReason reason, Object param) {
        if (isDone() || result != null) return;

        if (fleet.getMemoryWithoutUpdate().contains("$intaff_execfleet")) {
            getPerson().getMemoryWithoutUpdate().set("$intaff_failed", true);
        }
    }

    // description when selected in intel screen
    @Override
    public void addDescriptionForNonEndStage(TooltipMakerAPI info, float width, float height) {
        float opad = 10f;
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.FIND_CLUE) {
            info.addPara("Look for clues as to the disgraced executive's location in the " +
                    system.getNameWithLowercaseTypeShort() + ".", opad);
        } else if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Hunt down and eliminate the disgraced executive in the " +
                    system2.getNameWithLowercaseTypeShort() + ".", opad);
        }
        if (isDevMode()) {
            info.addPara("DEVMODE: EXECUTIVE IS LOCATED IN THE " +
                    system2.getNameWithLowercaseTypeShort() + ".", opad);
        }
    }

    // short description in popups and the intel entry
    @Override
    public boolean addNextStepText(TooltipMakerAPI info, Color tc, float pad) {
        Color h = Misc.getHighlightColor();
        if (currentStage == Stage.FIND_CLUE) {
            info.addPara("Look for clues in the " +
                    system.getNameWithLowercaseTypeShort(), tc, pad);
            return true;
        } else if (currentStage == Stage.KILL_FLEET) {
            info.addPara("Hunt down the disgraced executive in the " +
                    system2.getNameWithLowercaseTypeShort(), tc, pad);
            return true;
        }
        return false;
    }

    // where on the map the intel screen tells us to go
    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        if (currentStage == Stage.FIND_CLUE) {
            return getMapLocationFor(system.getCenter());
        } else if (currentStage == Stage.KILL_FLEET) {
            return getMapLocationFor(system2.getCenter());
        }
        return null;
    }

    // mission name
    @Override
    public String getBaseName() {
        return "Internal Affairs";
    }
}
