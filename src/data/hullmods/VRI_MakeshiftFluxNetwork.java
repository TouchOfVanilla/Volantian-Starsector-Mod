package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import org.lazywizard.lazylib.combat.AIUtils;

import java.awt.*;

public class VRI_MakeshiftFluxNetwork extends BaseHullMod {

	private static final float FLUXWPNEFF = 0.35f;
	private static final float FLUXSHLDEFF = 0.25f;
	private static final float makeshiftmult = 0.75f;

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

	}
	public void advanceInCombat(ShipAPI ship, float amount) {
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

		float wpneff = fleetfluxpool*FLUXWPNEFF*makeshiftmult;
		float shieldeff = fleetfluxpool*FLUXSHLDEFF*makeshiftmult;

		stats.getMissileWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);
		stats.getEnergyWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);
		stats.getBallisticWeaponFluxCostMod().modifyMult(spec.getId(), 1f-wpneff);

		stats.getShieldDamageTakenMult().modifyMult(spec.getId(), 1f-shieldeff);

		//Change color of shield and vent
		if(ship.getShield() != null) {
			ship.getShield().setInnerColor(new Color(31,247,182,75));
		}
		ship.setVentCoreColor(new Color(31,247,182,255));

		if (ship == Global.getCombatEngine().getPlayerShip()) {
			Global.getCombatEngine().maintainStatusForPlayerShip("fluxnetworkwpn", "graphics/icons/hullsys/fortress_shield.png", "Weapon Eff ", "+" + wpneff*100f + "%", false);
			Global.getCombatEngine().maintainStatusForPlayerShip("fluxnetworkshld", "graphics/icons/hullsys/fortress_shield.png", "Shield Eff ", "+" + shieldeff*100f + "%", false);
		}
	}

	private float easeOutCirc(float x) {
		return (float)Math.sqrt(1 - Math.pow(x - 1, 2));
	}

	@Override
	public boolean isApplicableToShip(ShipAPI ship) {

		return !ship.getVariant().hasHullMod("VRI_FluxNetwork");
	}

	public String getUnapplicableReason(ShipAPI ship) {

		if(ship.getVariant().hasHullMod("VRI_FluxNetwork")) return "incompatible with a built in VRI Flux Network";
		return null;
	}

	public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {
	}
}