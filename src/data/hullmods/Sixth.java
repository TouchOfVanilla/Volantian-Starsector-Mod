package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.api.combat.ShieldAPI;
import org.lazywizard.lazylib.combat.DefenseType;

import java.util.Iterator;

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
	private static final float SPEED_MULT = .95f;
	private static final float ARMORDMG_MULT = .9f;


	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {
		stats.getFluxDissipation().modifyMult(id,DISSIPATION_MULT);
		stats.getMaxSpeed().modifyMult(id,SPEED_MULT);

	}

	@Override
	public void advanceInCombat(ShipAPI ship, float amount) {
		Iterator wingiter = ship.getAllWings().iterator();
		while (wingiter.hasNext()){
			FighterWingAPI wing = (FighterWingAPI) wingiter.next();
			Iterator fighteriter = wing.getWingMembers().iterator();
			while (fighteriter.hasNext()){
				ShipAPI fighter = (ShipAPI) fighteriter.next();
				if (fighter.getFluxTracker().isOverloaded()){
					fighter.setDefenseDisabled(true);
					fighter.getFluxTracker().stopOverload();
				}

			}
		}
	}

	@Override
	public void applyEffectsToFighterSpawnedByShip(ShipAPI fighter, ShipAPI ship, String id) {
		if(fighter.getShield() == null){
			fighter.getMutableStats().getArmorDamageTakenMult().modifyMult(id, ARMORDMG_MULT);
		}
	}

	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) Math.round((DISSIPATION_MULT - 1f) * 100f) + "%"; // + Strings.X;
		if (index == 1) return "" + (int) Math.round((1f - SPEED_MULT) * 100f) + "%";
		if (index == 2) return "" + (int) Math.round((1f - ARMORDMG_MULT) * 100f) + "%";
		if (index == 3) return "any";
		if (index == 4) return "stops";

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

