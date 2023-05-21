package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lazywizard.lazylib.combat.AIUtils;

import java.awt.*;

public class VRI_FluxNetwork extends BaseHullMod {

	private static final float FLUXWPNEFF = 0.35f;
	private static final float FLUXSHLDEFF = 0.25f;
	private static final float makeshiftmult = 0.75f;
	private static final float THRESHOLD = 120f;
	private static final float THRESHOLDCOMM = 180f;
	private static final float hco = 1f;

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		float thresh = THRESHOLD;
		float fpused = 0f;
		if(stats.getFleetMember()!=null&&stats.getFleetMember().getFleetData()!=null&&stats.getFleetMember().getFleetData().getFleet()!=null&&stats.getFleetMember().getFleetData().getFleet().getFaction().getFactionSpec().equals(Global.getSettings().getFactionSpec("vri"))){
			thresh = THRESHOLDCOMM;
		}
		if(stats.getFleetMember()!=null&&stats.getFleetMember().getFleetData()!=null&&stats.getFleetMember().getFleetData().getFleet()!=null){
			for(FleetMemberAPI f:stats.getFleetMember().getFleetData().getMembersListCopy()){
				if(f.getVariant().hasHullMod("VRI_FluxNetwork")){
					fpused = fpused+f.getFleetPointCost();
				}
			}
		}
		float degrade = Math.min(1f,thresh/(fpused*hco));
		stats.getMaxCombatReadiness().modifyMult(id,degrade,"Over VRI FP maximum.");

		if(stats.getFleetMember()!=null&&stats.getFleetMember().getFleetData()!=null&&stats.getFleetMember().getFleetData().getFleet()!=null){
			for(FleetMemberAPI f:stats.getFleetMember().getFleetData().getMembersListCopy()){
				if(f.getVariant().hasHullMod("[put whatever the id for flux network is here]")){
					fpused = fpused+f.getFleetPointCost();
				}
			}
		}
	}

	public void advanceInCombat(ShipAPI ship, float amount) {
		MutableShipStatsAPI stats = ship.getMutableStats();

		CombatEngineAPI engine = Global.getCombatEngine();

		float fleetfluxpool = 0f;
		ShipAPI fs = null;
		for (CombatEntityAPI f : AIUtils.getAlliesOnMap(ship)) {
			if (f instanceof ShipAPI) fs = (ShipAPI) f;
			if (fs != null) {
				if (fs.getVariant().hasHullMod("VRI_FluxNetwork")) {
					fleetfluxpool = fleetfluxpool + fs.getMaxFlux();
				}
				if (fs.getVariant().hasHullMod("VRI_MakeshiftFluxNetwork")) {
					fleetfluxpool = fleetfluxpool + fs.getMaxFlux() * makeshiftmult;
				}
			}
		}
		fleetfluxpool = easeOutCirc(fleetfluxpool / 200000);

		float wpneff = fleetfluxpool * FLUXWPNEFF;
		float shieldeff = fleetfluxpool * FLUXSHLDEFF;

		stats.getMissileWeaponFluxCostMod().modifyMult(spec.getId(), 1f - wpneff);
		stats.getEnergyWeaponFluxCostMod().modifyMult(spec.getId(), 1f - wpneff);
		stats.getBallisticWeaponFluxCostMod().modifyMult(spec.getId(), 1f - wpneff);
		stats.getBallisticWeaponFluxCostMod().modifyMult(spec.getId(), 1f - wpneff);

		stats.getShieldDamageTakenMult().modifyMult(spec.getId(), 1f - shieldeff);

		if (ship == Global.getCombatEngine().getPlayerShip()) {
			Global.getCombatEngine().maintainStatusForPlayerShip("fluxnetworkwpn", "graphics/icons/hullsys/fortress_shield.png", "Weapon Eff ", "+" + wpneff * 100f + "%", false);
			Global.getCombatEngine().maintainStatusForPlayerShip("fluxnetworkshld", "graphics/icons/hullsys/fortress_shield.png", "Shield Eff ", "+" + shieldeff * 100f + "%", false);
		}

		if (ship.getFleetMember() != null && ship.getFleetMember().getFleetData() != null && ship.getFleetMember().getFleetData().getFleet() != null && ship.getFleetMember().getFleetData().getFleet().getFaction() != null) {
			if (ship.getFleetMember().getFleetData().getFleet().getFaction().equals(Global.getSector().getFaction("vri"))) {
				for (ShipAPI a : AIUtils.getAlliesOnMap(ship)) {
					if (!a.getVariant().hasHullMod("VRI_MakeshiftFluxNetwork") && !a.getVariant().hasHullMod("VRI_FluxNetwork")) {
						a.getVariant().addMod("VRI_MakeshiftFluxNetwork");
					}
				}
			}
		}
	}

	private float easeOutCirc(float x) {
		return (float)Math.sqrt(1 - Math.pow(x - 1, 2));
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {
		return true;
	}

	public String getUnapplicableReason(ShipAPI ship) {

		return null;
	}
	@Override
	public int getDisplaySortOrder() {
		return 1;
	}
	public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {

		if (index == 0) return "" + (int) ((SUPPLY_USE_MULT - 1f) * 100f) + "%";
		if (index == 1) return "" + (int) ((HANDLING_MULT - 1f) * 100f) + "%";
		if (index == 2) return "" + (int) ((CAPACITY_MULT - 1f) * 100f) + "%";
		if (index == 3) return "" + (int) ((PEAK_PERFORMANCE_MULT - 1f) * 100f) + "%";

		return null;
	}
	@Override
	public int getDisplayCategoryIndex() {
		return 1;
	}
	@Override
	public boolean shouldAddDescriptionToTooltip (HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}
}