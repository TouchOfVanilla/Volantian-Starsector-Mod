package data.hullmods;

import java.awt.*;
import com.fs.starfarer.api.GameState;
import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipAPI.HullSize;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import static com.fs.starfarer.api.impl.campaign.RepairGantry.getAdjustedGantryModifier;
import static com.fs.starfarer.api.impl.campaign.RepairGantry.getAdjustedGantryModifierForPostCombatSalvage;


public class VolantianReclaimers extends BaseHullMod {
	
	public void applyEffectsBeforeShipCreation(HullSize hullSize, MutableShipStatsAPI stats, String id) {

		stats.getDynamic().getMod(Stats.SALVAGE_VALUE_MULT_MOD).modifyFlat(id, 0.03f);
	}
	
	public String getDescriptionParam(int index, HullSize hullSize) {
		if (index == 0) return "" + (int) ((0.03) * 100f) + "%";
		
		return null;
	}

	@Override
	public void addPostDescriptionSection(TooltipMakerAPI tooltip, HullSize hullSize, ShipAPI ship, float width, boolean isForModSpec) {
		float opad = 10f;
		Color h = Misc.getHighlightColor();

		tooltip.addPara("Each additional ship provides diminishing returns.", opad);

		if (isForModSpec || ship == null) return;
		if (Global.getSettings().getCurrentState() == GameState.TITLE) return;

		CampaignFleetAPI fleet = Global.getSector().getPlayerFleet();
		float fleetMod = getAdjustedGantryModifier(fleet, null, 0f);

		tooltip.addPara("The total resource recovery bonus for your fleet is %s.", opad, h,
				"" + (int) Math.round(fleetMod * 100f) + "%");

		tooltip.addPara("The fleetwide post-battle salvage bonus is %s.", opad, h,
				"" + (int) Math.round(getAdjustedGantryModifierForPostCombatSalvage(fleet) * 100f) + "%");
	}
}
