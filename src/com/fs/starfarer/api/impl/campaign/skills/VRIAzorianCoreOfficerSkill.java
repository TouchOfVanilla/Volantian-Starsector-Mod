package com.fs.starfarer.api.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.MutableCharacterStatsAPI;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.characters.SkillSpecAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.loading.HullModSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.Iterator;

public class VRIAzorianCoreOfficerSkill {
    public static float PPT_MOD_FLAT = 90f;
    public static float SUPPLY_MOD_MULT = .75f;
    public static float REPAIR_RATE_MULT = 1.5f;

    public static float dmodcount = 0f;
    public static float smodcount = 0f;

    public VRIAzorianCoreOfficerSkill() {
    }

    public static class Level1 implements ShipSkillEffect {
        public Level1() {
        }

        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {
            dmodcount = DModManager.getNumDMods(stats.getVariant());

            smodcount = stats.getVariant().getSMods().size();

            float smodmult = 1 - (smodcount * 0.05f);
            float dmodmult = 1 - (dmodcount * 0.05f);

            if (smodmult < 0.75f){
                smodmult = 0.75f;
            }
            if (dmodmult < 0.75f){
                dmodmult = 0.75f;
            }

            stats.getShieldDamageTakenMult().modifyMult(id, smodmult);
            stats.getArmorDamageTakenMult().modifyMult(id, dmodmult);

        }

        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
            stats.getShieldDamageTakenMult().unmodify(id);
            stats.getArmorDamageTakenMult().unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (5) + "% armor damage taken per D-Mod, current bonus is " + (int) (dmodcount * 5) + "%";
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
            ShipAPI ship = (ShipAPI) stats.getEntity();
        }

        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {
        }

        public String getEffectDescription(float level) {
            return "-" + (int) (5) + "% shield damage taken per s-mod, current bonus is " + (int) (smodcount * 5) + "%";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
    public static class Level3 implements ShipSkillEffect{
        public Level3(){
        }

        @Override
        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        @Override
        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }

        @Override
        public String getEffectDescription(float level) {
            return "Bonuses are capped at 25% damage reduction.";
        }

        @Override
        public String getEffectPerLevelDescription() {
            return null;
        }

        @Override
        public ScopeDescription getScopeDescription() {
            return null;
        }
    }
}
