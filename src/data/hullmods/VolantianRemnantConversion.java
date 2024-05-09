package data.hullmods;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.MutableFleetStatsAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import lunalib.lunaRefit.LunaRefitManager;

public class VolantianRemnantConversion extends BaseHullMod {

	private static final float SUPPLY_USE_MULT = 0.9f;
	private static final float PEAK_PERFORMANCE_FLAT = 60f;

	private static final float FLUX_STAT_MULT = 0.9f;

	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		

		// lower supply use!
		stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_USE_MULT);

		//worse PPT
		stats.getPeakCRDuration().modifyFlat(id,PEAK_PERFORMANCE_FLAT);

		//worse flux stats
		stats.getFluxDissipation().modifyMult(id, FLUX_STAT_MULT);
		stats.getFluxCapacity().modifyMult(id, FLUX_STAT_MULT);
	}

	@Override
	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((1f - SUPPLY_USE_MULT) * 100f) + "%";
		if (index == 1) return "" + (int) (PEAK_PERFORMANCE_FLAT) + " seconds";
		if (index == 2) return "" + (int) ((1f - FLUX_STAT_MULT)* 100f) + "%";
		
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
