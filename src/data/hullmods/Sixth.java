package data.hullmods;

import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.util.Misc;

public class Sixth extends BaseHullMod {

	/* A Ship of the 6th Domain Battlegroup
	 * well-maintained survivor of the original battlegroup which did not found the Hegemony
	 * Sterling example of the Domain Navy's traditional "steel wings" doctrine
	 * focus on superior armour and strke craft to destroy the enemy
	 * - slightly better flux handling
	 * - slightly better armour
	 * - slightly worse speed/maneuver
	 * - 
	 */
	
	//private static final float ARMOR_BONUS_MULT = 1.1f;
	private static final float ARMOR_BONUS = 100;
	private static final float CAPACITY_MULT = 1.10f;
	private static final float DISSIPATION_MULT = 1.10f;
	private static final float HANDLING_MULT = 1.08f;
	private static final float SPEED_MULT = 1.20f;
	
	/*private static Map mag = new HashMap();
	static {
		mag.put(HullSize.FIGHTER, 0.0f);
		mag.put(HullSize.FRIGATE, 0.25f);
		mag.put(HullSize.DESTROYER, 0.15f);
		mag.put(HullSize.CRUISER, 0.10f);
		mag.put(HullSize.CAPITAL_SHIP, 0.05f);
	}*/	
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		
		// just generally better armour - and structure!
		//stats.getArmorBonus().modifyMult(id, (Float) mag.get(hullSize) + 1.00f); // * ARMOR_BONUS_MULT); 
		//stats.getHullBonus().modifyPercent(id, (Float) mag.get(hullSize) * 0.5f); // some hull. 
		stats.getArmorBonus().modifyFlat(id, (Float) ARMOR_BONUS);
		
		// 5% better flux stats
		stats.getFluxCapacity().modifyMult(id, CAPACITY_MULT);
		stats.getFluxDissipation().modifyMult(id, DISSIPATION_MULT);
		
		// 8% worse handling
		stats.getMaxSpeed().modifyMult(id, SPEED_MULT);
		stats.getAcceleration().modifyMult(id, HANDLING_MULT);
		stats.getDeceleration().modifyMult(id, HANDLING_MULT);
		stats.getMaxTurnRate().modifyMult(id, HANDLING_MULT);
		stats.getTurnAcceleration().modifyMult(id, HANDLING_MULT);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 2) return Misc.getRoundedValue(ARMOR_BONUS);
		if (index == 0) return "" + (int) Math.round((HANDLING_MULT - 1f) * 100f) + "%"; // + Strings.X;
		if (index == 1) return "" + (int) Math.round((CAPACITY_MULT - 1f) * 100f) + "%";
		return null;
	}

	/*public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + ((Float) mag.get(HullSize.FRIGATE)).intValue();
		if (index == 1) return "" + ((Float) mag.get(Hullize.DESTROYER)).intValue();
		if (index == 2) return "" + ((Float) mag.get(HullSize.CRUISER)).intValue();
		if (index == 3) return "" + ((Float) mag.get(HullSize.CAPITAL_SHIP)).intValue();
		if (index == 4) return "" + (int) ACCELERATION_BONUS;
		//if (index == 5) return "four times";
		if (index == 5) return "4" + Strings.X;
		if (index == 6) return "" + BURN_LEVEL_BONUS;
		return null;
	}*/
	@Override
	public int getDisplaySortOrder() {
		return 0;
	}

	@Override
	public int getDisplayCategoryIndex() {
		return 0;
	}

}

