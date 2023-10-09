package com.fs.starfarer.api.impl.campaign.skills;

import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

public class VRIAzorianCoreOfficerSkill {
    public static float PPT_MOD_FLAT = 90f;
    public static float SUPPLY_MOD_MULT = .75f;
    public static float REPAIR_RATE_MULT = 1.5f;

    public VRIAzorianCoreOfficerSkill() {
    }

    public static class Level1 implements ShipSkillEffect {
        public Level1() {
        }

        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            stats.getSuppliesPerMonth().modifyMult(id, SUPPLY_MOD_MULT);
            stats.getPeakCRDuration().modifyFlat(id, PPT_MOD_FLAT);
        }

        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getSuppliesPerMonth().unmodify(id);
            stats.getPeakCRDuration().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return (int)(100 - (100 * SUPPLY_MOD_MULT)) + "% less supply upkeeep";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
    public static class Level2 implements ShipSkillEffect {
        public Level2() {
        }

        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
        }

        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
        }

        public String getEffectDescription(float level) {
            return "+" + (int)PPT_MOD_FLAT + " seconds of peak performance time";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
}
