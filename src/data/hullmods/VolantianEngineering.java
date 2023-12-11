package data.hullmods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.mission.FleetSide;



public class VolantianEngineering extends BaseHullMod {

	private static final float CAPACITY_MULT = 1.1f;
	private static final float DISSIPATION_MULT = 1.1f;
	private static final float HANDLING_MULT = 1.25f;
	private static final float SUPPLY_USE_MULT = 1.1f;
	private static final float PEAK_PERFORMANCE_MULT = 0.9f;


	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		//uhhhhhh fuckinnn flux
		stats.getFluxDissipation().modifyMult(id,DISSIPATION_MULT);
		stats.getFluxCapacity().modifyMult(id,DISSIPATION_MULT);
		// higher supply use
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);
		//Lower PPT
		stats.getPeakCRDuration().modifyMult(id, PEAK_PERFORMANCE_MULT);
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		if (index == 1) return "" + (int) ((DISSIPATION_MULT - 1f) * 100f) + "%";
		if (index == 2) return "" + (int) ((1f - PEAK_PERFORMANCE_MULT) * 100f) + "%";
		
		return null;
	}
	@Override
	public int getDisplaySortOrder() {
		return 0;
	}

	@Override
	public int getDisplayCategoryIndex() {
		return 0;
	}

}
