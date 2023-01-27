package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lazywizard.lazylib.combat.AIUtils;

import java.awt.*;

public class VRI_FluxNetwork extends BaseHullMod {

	private static final float FLUXWPNEFF = 0.35f;
	private static final float FLUXSHLDEFF = 0.25f;
	private static final float makeshiftmult = 0.75f;

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

	}

	public void advanceInCombat(ShipAPI ship, float amount){
		MutableShipStatsAPI stats = ship.getMutableStats();

		CombatEngineAPI engine = Global.getCombatEngine();

		float fleetfluxpool = 0f;
		ShipAPI fs = null;
		for(CombatEntityAPI f:AIUtils.getAlliesOnMap(ship)){
			if(f instanceof ShipAPI) fs = (ShipAPI) f;
			if(fs!= null){
				if(fs.getVariant().hasHullMod("VRI_FluxNetwork")){
					fleetfluxpool=fleetfluxpool+fs.getMaxFlux();
				}
				if(fs.getVariant().hasHullMod("VRI_MakeshiftFluxNetwork")){
					fleetfluxpool=fleetfluxpool+fs.getMaxFlux()*makeshiftmult;
				}
			}
		}
		fleetfluxpool = easeOutCirc(fleetfluxpool/200000);

		float wpneff = fleetfluxpool*FLUXWPNEFF;
		float shieldeff = fleetfluxpool*FLUXSHLDEFF;

		stats.getMissileWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);
		stats.getEnergyWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);
		stats.getBallisticWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);
		stats.getBallisticWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);

		stats.getShieldDamageTakenMult().modifyMult(spec.getId(), 1f-shieldeff);

		if (ship == Global.getCombatEngine().getPlayerShip()) {
			Global.getCombatEngine().maintainStatusForPlayerShip("fluxnetworkwpn", "graphics/icons/hullsys/fortress_shield.png", "Weapon Eff ", "+" + wpneff*100f + "%", false);
			Global.getCombatEngine().maintainStatusForPlayerShip("fluxnetworkshld", "graphics/icons/hullsys/fortress_shield.png", "Shield Eff ", "+" + shieldeff*100f + "%", false);
		}

		if(ship.getFleetMember().getFleetData().getFleet().getFaction().equals(Global.getSector().getFaction("vri"))){
			for(ShipAPI a:AIUtils.getAlliesOnMap(ship)){
				if(!a.getVariant().hasHullMod("VRI_MakeshiftFluxNetwork") && !a.getVariant().hasHullMod("VRI_FluxNetwork")){
					a.getVariant().addMod("VRI_MakeshiftFluxNetwork");
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
}