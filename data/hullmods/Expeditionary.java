package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class Expeditionary extends BaseHullMod {

	private static final float CAPACITY_MULT = 1.1f;
	private static final float DISSIPATION_MULT = 1.1f;
	private static final float HANDLING_MULT = 1.25f;
	private static final float SUPPLY_USE_MULT = 1.1f;
	private static final float CORONA_EFFECT_MULT = 0.5f;
	//private static final int BURN_LEVEL_BONUS = 1;	
	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		
		// 10% better flux stats
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		
		// 10% better handling all around!
		stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats. getTurnAcceleration().modifyMult(id, HANDLING_MULT);
		
		// higher supply use
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
		
		//Burn Bonus
		//stats.getMaxBurnLevel().modifyFlat(id, BURN_LEVEL_BONUS);
		
		stats.getDynamic().getStat(Stats.CORONA_EFFECT_MULT).modifyMult(id, CORONA_EFFECT_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((SUPPLY_USE_MULT - 1f) * 100f);
		if (index == 1) return "" + (int) ((HANDLING_MULT - 1f) * 100f); // int) ((1f - CORONA_EFFECT_REDUCTION) * 100f);
		if (index == 2) return "" + (int) ((CAPACITY_MULT - 1f) * 100f);
//		if (index == 3) return "" + (int) ((DISSIPATION_MULT - 1f) * 100f);
		if (index == 3) return "" + (int) ((1f - CORONA_EFFECT_MULT ) * 100f);
		
		return null;
		//if (index == 0) return "" + ((Float) mag.get(hullSize)).intValue();
		//return null;
	}


}
