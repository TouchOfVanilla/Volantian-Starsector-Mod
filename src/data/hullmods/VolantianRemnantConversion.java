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
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import lunalib.lunaRefit.LunaRefitManager;

public class VolantianRemnantConversion extends BaseHullMod {

	private static final float SUPPLY_USE_MULT = 0.9f;
	private static final float PEAK_PERFORMANCE_FLAT = 60f;

	private static final float FLUX_STAT_MULT = 0.9f;
	public WeaponSlotAPI SLOT;
	public WeaponSpecAPI WEAPON;

	
	
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

		Iterator weaponiter = (ship.getHullSpec().getAllWeaponSlotsCopy().iterator());
		while (weaponiter.hasNext()){
			WeaponSlotAPI weaponslot = (WeaponSlotAPI) weaponiter.next();
			if (weaponslot.getWeaponType().equals(WeaponAPI.WeaponType.DECORATIVE)){
				SLOT = weaponslot;
			}
		}
		String HULLID = ship.getHullSpec().getHullId();

		if (decoMap.containsKey(HULLID)){
			WEAPON = Global.getSettings().getWeaponSpec(decoMap.get(HULLID));
		}
		if (ship.getVariant().hasHullMod(HullMods.AUTOMATED)){
			ship.getVariant().addWeapon(SLOT.getId(), WEAPON.getWeaponId());
		} else {
			ship.getVariant().clearSlot(SLOT.getId());
		}
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
	public static final Map<String, String> decoMap = new HashMap<String, String>();
	static {
		decoMap.put("volantian_radiant_vri", "vol_radiant_vri_corebridge");
		decoMap.put("volantian_lumen_vri", "vol_lumen_vri_corebridge");
		decoMap.put("volantian_glimmer_vri", "vol_glimmer_vri_corebridge");
		decoMap.put("volantian_pyralis_vri", "vol_pyralis_vri_corebridge");
		decoMap.put("volantian_fulgent_vri", "vol_fulgent_vri_corebridge");
		decoMap.put("volantian_chromatic_vri", "vol_chromatic_vri_corebridge");
		decoMap.put("volantian_scintilla_vri", "vol_scintilla_vri_corebridge");
		decoMap.put("volantian_prismatic_vri", "vol_prismatic_vri_corebridge");
	}

}
