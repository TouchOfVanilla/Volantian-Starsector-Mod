package data.missions.vri_rude_awakening;

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
		api.initFleet(FleetSide.ENEMY, "TTS", FleetGoal.ATTACK, true, 5);

		// Set a blurb for each fleet
		api.setFleetTagline(FleetSide.PLAYER, "130th Legion of the Sixth Battlegroup, with elements of salvaged hulls.");
		api.setFleetTagline(FleetSide.ENEMY, "Tri-Tachyon Asset Deployment Taskforce with TTDS Reinforcements");

		// These show up as items in the bulleted list under
		// "Tactical Objectives" on the mission detail screen
		api.addBriefingItem("The Tri-Tachyon ships will challenge your air superiority while the automated ships will test your resilience.");
		api.addBriefingItem("The automated ships will compose the front line of the battle- plan accordingly..");
		api.addBriefingItem("The DSS Hemisphere Dancer must survive.");

		// Set up the player's fleet
		FleetMemberAPI member = api.addToFleet(FleetSide.PLAYER, "volantian_legion_vi_standard", FleetMemberType.SHIP, "DSS Hemisphere Dancer", true);
		FleetMemberAPI member1 = api.addToFleet(FleetSide.PLAYER, "volantian_dominator_vi_standard", FleetMemberType.SHIP, "DSS Eventide Daydreaming", false);
		FleetMemberAPI member2 = api.addToFleet(FleetSide.PLAYER, "volantian_eradicator_vi_standard", FleetMemberType.SHIP, "DSS We Are The People COMSEC Warned Us About", false);
		FleetMemberAPI member3 = api.addToFleet(FleetSide.PLAYER, "rampart_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member4 = api.addToFleet(FleetSide.PLAYER, "rampart_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member5 = api.addToFleet(FleetSide.ENEMY, "bastillon_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member6 = api.addToFleet(FleetSide.ENEMY, "bastillon_Standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member7 = api.addToFleet(FleetSide.PLAYER, "volantian_manticore_vi_standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member8 = api.addToFleet(FleetSide.PLAYER, "volantian_manticore_vi_standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member10 = api.addToFleet(FleetSide.PLAYER, "volantian_vanguard_vi_standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member11 = api.addToFleet(FleetSide.PLAYER, "volantian_vanguard_vi_standard", FleetMemberType.SHIP, false);
		FleetMemberAPI member12 = api.addToFleet(FleetSide.PLAYER, "legion_xiv_Elite", FleetMemberType.SHIP, false);
		FleetMemberAPI member13 = api.addToFleet(FleetSide.PLAYER, "dominator_XIV_Elite", FleetMemberType.SHIP, false);
		FleetMemberAPI member14 = api.addToFleet(FleetSide.PLAYER, "dominator_XIV_Elite", FleetMemberType.SHIP, false);
		FleetMemberAPI member15 = api.addToFleet(FleetSide.PLAYER, "enforcer_XIV_Elite", FleetMemberType.SHIP, false);
		FleetMemberAPI member16 = api.addToFleet(FleetSide.PLAYER, "enforcer_XIV_Elite", FleetMemberType.SHIP, false);
		FleetMemberAPI member17 = api.addToFleet(FleetSide.PLAYER, "enforcer_XIV_Elite", FleetMemberType.SHIP, false);

		member17.setAlly(true);
		member16.setAlly(true);
		member15.setAlly(true);
		member14.setAlly(true);
		member13.setAlly(true);
		member12.setAlly(true);
		member.getVariant().addPermaMod("automated");
		member1.getVariant().addPermaMod("automated");
		member2.getVariant().addPermaMod("automated");
		member7.getVariant().addPermaMod("automated");
		member8.getVariant().addPermaMod("automated");
		member10.getVariant().addPermaMod("automated");
		member11.getVariant().addPermaMod("automated");
		member.getCaptain().setAICoreId("alpha_core");
		member.setFlagship(true);
		member1.getCaptain().setAICoreId("beta_core");
		member2.getCaptain().setAICoreId("beta_core");
		member3.getCaptain().setAICoreId("beta_core");
		member7.getCaptain().setAICoreId("beta_core");


		// Mark player flagship as essential
		api.defeatOnShipLoss("DSS Hemisphere Dancer");

		// Set up the enemy fleet.
		api.getDefaultCommander(FleetSide.ENEMY).getStats().setSkillLevel(Skills.FIGHTER_UPLINK, 1);
		api.addToFleet(FleetSide.ENEMY, "astral_attack", FleetMemberType.SHIP, true);
		api.addToFleet(FleetSide.ENEMY, "astral_attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "brilliant_Standard", FleetMemberType.SHIP, "TTDS Silverlight", false);
		api.addToFleet(FleetSide.ENEMY, "brilliant_Standard", FleetMemberType.SHIP, "TTDS Star Strider", false);
		api.addToFleet(FleetSide.ENEMY, "apex_Assault", FleetMemberType.SHIP, "TTDS High Roller", false);
		api.addToFleet(FleetSide.ENEMY, "apex_Assault", FleetMemberType.SHIP, "TTDS Last Call", false);
		api.addToFleet(FleetSide.ENEMY, "heron_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "heron_Attack", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "volantian_gauntlet_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "volantian_gauntlet_standard", FleetMemberType.SHIP, false);
		api.addToFleet(FleetSide.ENEMY, "fulgent_Assault", FleetMemberType.SHIP, "TTDS Circumference", false);
		api.addToFleet(FleetSide.ENEMY, "fulgent_Standard", FleetMemberType.SHIP, "TTDS Blood Moon", false);
		api.addToFleet(FleetSide.ENEMY, "volantian_prismatic_brawler", FleetMemberType.SHIP, "TTDS Irreplaceable", false);
		api.addToFleet(FleetSide.ENEMY, "volantian_prismatic_brawler", FleetMemberType.SHIP, "TTDS Echoes and Dust", false);
		api.addToFleet(FleetSide.ENEMY, "volantian_prismatic_brawler", FleetMemberType.SHIP, "TTDS Golden Shroud", false);
		api.addToFleet(FleetSide.ENEMY, "lumen_Standard", FleetMemberType.SHIP, "TTDS Noble", false);
		api.addToFleet(FleetSide.ENEMY, "lumen_Standard", FleetMemberType.SHIP, "TTDS Blue", false);
		api.addToFleet(FleetSide.ENEMY, "lumen_Standard", FleetMemberType.SHIP, "TTDS Ferret", false);
		api.addToFleet(FleetSide.ENEMY, "lumen_Standard", FleetMemberType.SHIP, "TTDS Silver", false);


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






