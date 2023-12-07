package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.LabelAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.combat.AIUtils;

import java.awt.*;

public class VRI_FluxNetwork extends BaseHullMod {

	private static final float FLUXWPNEFF = 0.35f;
	private static final float FLUXSHLDEFF = 0.25f;
	private static final float makeshiftmult = 0.75f;
	private static final float THRESHOLD = 60f;
	private static final float THRESHOLDCOMM = 90f;
	private static final float hco = 1f;

	public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {
		if(stats.getFleetMember()!=null&&stats.getFleetMember().getFleetData()!=null&&stats.getFleetMember().getFleetData().getFleet()!=null&&stats.getFleetMember().getFleetData().getFleet().isPlayerFleet()) {
			float thresh = THRESHOLD;
			float fpused = 0f;
			if (Misc.getCommissionFactionId()!=null&&Misc.getCommissionFactionId().equals("vri")) {
				thresh = THRESHOLDCOMM;
			}
			if (stats.getFleetMember() != null && stats.getFleetMember().getFleetData() != null && stats.getFleetMember().getFleetData().getFleet() != null) {
				for (FleetMemberAPI f : stats.getFleetMember().getFleetData().getMembersListCopy()) {
					if (f.getVariant().hasHullMod("VRI_FluxNetwork")) {
						fpused = fpused + f.getFleetPointCost();
					}
				}
			}
			float degrade = Math.min(1f, thresh / (fpused * hco));
			stats.getMaxCombatReadiness().modifyMult(id, degrade, "Over VRI FP maximum.");
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
	public boolean shouldAddDescriptionToTooltip(ShipAPI.HullSize hullSize, ShipAPI ship, boolean isForModSpec) {
		return false;
	}
	public static Boolean anyNull(Object... objects) {
		if (objects.length == 0) return true;
		for (Object o : objects) {
			if (o == null) return false;
		}
		return true;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, ShipAPI.HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {

		float pad = 3f;
		float opad = 10f;
		Color m = Misc.getMissileMountColor();
		Color e = Misc.getEnergyMountColor();
		Color b = Misc.getHighlightColor();

		MutableShipStatsAPI stats = ship.getMutableStats();

		if(stats.getFleetMember()!=null&&stats.getFleetMember().getFleetData()!=null&&stats.getFleetMember().getFleetData().getFleet()!=null&&stats.getFleetMember().getFleetData().getFleet().isPlayerFleet()) {
			float thresh = THRESHOLD;
			float fpused = 0f;
			if (Misc.getCommissionFactionId()!=null&&Misc.getCommissionFactionId().equals("vri")) {
				thresh = THRESHOLDCOMM;
			}
			if (stats.getFleetMember() != null && stats.getFleetMember().getFleetData() != null && stats.getFleetMember().getFleetData().getFleet() != null) {
				for (FleetMemberAPI f : stats.getFleetMember().getFleetData().getMembersListCopy()) {
					if (f.getVariant().hasHullMod("VRI_FluxNetwork")) {
						fpused = fpused + f.getFleetPointCost();
					}
				}
			}
			float degrade = Math.min(1f, thresh / (fpused * hco));


			LabelAPI label = tooltip.addPara("VRI ships are outfitted with an advanced transponder array which allows them to share fleetwide computational loads. Increases weapon %s and %s based on the maximum flux of allied ships deployed with this hullmod. Ships with makeshift networks contribute a lower proportion of their flux maximum to the bonus. \n" +
							"Furthermore, the network has safety interlocks to prevent the damage of internal components and electricity grids caused by the long-term computational and mechanical strain of an abundance of Volantian hulls interlinked by the network. Volantian Officers are specially trained to minimize this effect, and to partially disable the interlocks. Makeshift Flux networks are not affected. Reduces maximum combat readiness based on fleet points of VRI hulls in fleet.", opad, b,
					"" + "flux efficiency",
					"" + "shield efficiency");
			label.setHighlight("" + "flux efficiency",
					"" + "additional bonuses",
					"" + "shield efficiency");
			label.setHighlightColors(b, b, b);

			tooltip.addSectionHeading("CR info:", Alignment.MID, opad);
			label = tooltip.addPara(" %s is at %s percent of the standard due to the number of VRI hulls; ", opad, b,
					"" + "Combat Readiness",
					"" + Math.round(degrade * 100f));
			label.setHighlight("" + "Combat Readiness",
					"" + Math.round(degrade * 100f));
			label.setHighlightColors(e, b);
		}
	}
}
