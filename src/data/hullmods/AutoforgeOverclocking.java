package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShieldAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class AutoforgeOverclocking extends BaseHullMod {

	private static final float CAPACITY_MULT = 0.8f;
	private static final float DISSIPATION_MULT = 0.8f;
	private static final float REPLACEMENT_TIME_MULT = .75f;
	public static final float SHIELD_ARC_MULT = .5f;
	private static final float SHIELD_EFF_MULT = 1.5f;

	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		
		// worse flux!
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		
		// worse shields!
		stats.getShieldArcBonus().modifyMult(id, SHIELD_ARC_MULT);
		stats.getShieldDamageTakenMult().modifyMult(id, SHIELD_EFF_MULT);
		
		// better replacement time!
		stats.getFighterRefitTimeMult().modifyMult(id, REPLACEMENT_TIME_MULT);

		// +1 to fighter bays!
		stats.getNumFighterBays().modifyFlat(id, 1f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((1f - REPLACEMENT_TIME_MULT) * 100f) + "%";
		if (index == 1) return "" + (int) ((1f - CAPACITY_MULT) * 100f) + "%";
		if (index == 2) return "" + (int) ((1f - SHIELD_ARC_MULT) * 100f) + "%";
		if (index == 3) return "" + (int) ((SHIELD_EFF_MULT - 1f) * 100f) + "%";
		
		return null;
	}


}
