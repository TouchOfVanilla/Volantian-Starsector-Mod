package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
//Hiya fellas just got back from the mint paint store
public class VolantianAuxillary extends BaseHullMod {

	private static final float CAPACITY_MULT = 1f;
	private static final float DISSIPATION_MULT = 1f;
	private static final float HANDLING_MULT = 1f;
	private static final float SUPPLY_USE_MULT = 1f;

	private static final float PEAK_PERFORMANCE_MULT = 1f;


	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		
		// better flux!
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		
		// better handling!
		stats.getMaxSpeed().modifyMult(id, HANDLING_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats. getTurnAcceleration().modifyMult(id, HANDLING_MULT);
		
		// higher supply use
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);

		//Higher PPT
		stats.getPeakCRDuration().modifyMult(id, PEAK_PERFORMANCE_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		if (index == 1) return "" + (int) ((HANDLING_MULT - 1f) * 100f) + "%";
		if (index == 2) return "" + (int) ((CAPACITY_MULT - 1f) * 100f) + "%";
		if (index == 3) return "" + (int) ((PEAK_PERFORMANCE_MULT - 1f) * 100f) + "%";
		
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
