package data.hullmods;

import java.util.HashMap;
import java.util.Map;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class VolantianRemnantConversion extends BaseHullMod {

	private static final float SUPPLY_USE_MULT = 0.9f;
	private static final float PEAK_PERFORMANCE_MULT = 1.1f;

	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		

		// lower supply use!
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);

		//Higher PPT
		stats.getPeakCRDuration().modifyMult(id, PEAK_PERFORMANCE_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 2) return "" + (int) ((SUPPLY_USE_MULT - 1f) * 100f);
		if (index == 3) return "" + (int) ((PEAK_PERFORMANCE_MULT - 1f) * 100f);
		
		return null;
	}


}