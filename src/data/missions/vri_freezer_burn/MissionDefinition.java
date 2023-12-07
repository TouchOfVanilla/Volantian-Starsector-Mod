package data.missions.vri_freezer_burn;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseEveryFrameCombatPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.fleet.FleetGoal;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.WeightedRandomPicker;

//The Gingus!

public class MissionDefinition implements MissionDefinitionPlugin {

	public void defineMission(MissionDefinitionAPI api) {
		// Set up the fleets
		api.initFleet(FleetSide.PLAYER, "DSS", FleetGoal.ATTACK, false, 5);
		api.initFleet(FleetSide.ENEMY, "DSS", FleetGoal.ATTACK, true, 5);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "130th Legion of the Sixth Battlegroup, led by Commander Xavier");
		api.setFleetTagline(FleetSide.ENEMY, "Domain Explorarium Defense System");

		// These show up as items in the bulleted list under
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("The unrelenting firepower of the drones will challenge your ship's defenses.");
		api.addBriefingItem("Furthermore, their point defense nets will play havoc to your fighter squadrons.");
		api.addBriefingItem("The DSS Hemisphere Dancer must survive.");

		// Set up the player's fleet
		FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "volantian_legion_vi_Standard", FleetMemberType.SHIP, "DSS Hemisphere Dancer", true);

		PersonAPI xavier = Global.getSector().getFaction("independent").createRandomPerson(FullName.Gender.FEMALE);
		xavier.setId("vrixavier_mission3");
		xavier.getName().setFirst("Julius");
		xavier.getName().setLast("Xavier");
		xavier.getName().setGender(FullName.Gender.MALE);
		xavier.setPersonality("steady");
		xavier.setPortraitSprite("graphics/portraits/portrait38.png");
		xavier.setFaction("independent");
		xavier.getStats().setSkillLevel(Skills.POLARIZED_ARMOR, 2);
		xavier.getStats().setSkillLevel(Skills.HELMSMANSHIP, 2);
		xavier.getStats().setSkillLevel(Skills.MISSILE_SPECIALIZATION, 1);
		xavier.getStats().setLevel(3);
		member.setCaptain(xavier);
		FleetMemberAPI member3 = api.addToFleet(FleetSide.PLAYER, "volantian_dominator_vi_Standard", FleetMemberType.SHIP, "DSS Eventide Daydreaming", false);
		FleetMemberAPI member4 = api.addToFleet(FleetSide.PLAYER, "volantian_eradicator_vi_Standard", FleetMemberType.SHIP, "DSS We Are The People COMSEC Warned Us About", false);
		FleetMemberAPI member8 = api.addToFleet(FleetSide.PLAYER, "volantian_manticore_vi_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member9 = api.addToFleet(FleetSide.PLAYER, "volantian_enforcer_vi_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member10 = api.addToFleet(FleetSide.PLAYER, "volantian_vanguard_vi_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member12 = api.addToFleet(FleetSide.PLAYER, "volantian_monitor_vi_Standard", FleetMemberType.SHIP, false);



		// Mark player flagship as essential
		api.defeatOnShipLoss("DSS Hemisphere Dancer");

		// Set up the enemy fleet.
		api.getDefaultCommander(FleetSide.ENEMY).getStats().setSkillLevel(Skills.FIGHTER_UPLINK, 1);
		api.addToFleet(FleetSide.ENEMY, "guardian_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "rampart_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "rampart_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "rampart_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "berserker_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "berserker_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "berserker_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "berserker_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "bastillon_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "bastillon_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "bastillon_Standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "defender_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "defender_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "defender_PD", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "picket_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "picket_Assault", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "picket_Assault", FleetMemberType.SHIP, false);
		// Set up the map.
		float width = 24000f;
		float height = 20500f;
		api.initMap((float)-width/2f, (float)width/2f, (float)-height/2f, (float)height/2f);

		float minX = -width/2;
		float minY = -height/2;
		api.addObjective(minX + width * 0.4f - 1000, minY + height * 0.5f, "nav_buoy");
		api.addObjective(minX + width * 0.8f - 1000, minY + height * 0.3f, "sensor_array");
		api.addObjective(minX + width * 0.5f - 1000, minY + height * 0.4f, "comm_relay");
		for (int i = 0; i < 5; i++) {
			float x = (float) Math.random() * width - width/2;
			float y = (float) Math.random() * height - height/2;
			float radius = 100f + (float) Math.random() * 900f;
			api.addNebula(x, y, radius);
		}
		api.addPlugin(new BaseEveryFrameCombatPlugin() {
			private boolean reallyStarted = false;
			private boolean started = false;
			private boolean finished = false;
			private IntervalUtil tracker = new IntervalUtil(1f, 1f);
			WeightedRandomPicker<String> AmongUs = new WeightedRandomPicker<String>();
			public void init(CombatEngineAPI engine) {
				engine.getContext().aiRetreatAllowed = false;
				engine.getContext().enemyDeployAll = true;
				engine.getContext().setInitialDeploymentBurnDuration(1f);
				engine.getContext().setNormalDeploymentBurnDuration(1f);

			}

		});
	}
}






