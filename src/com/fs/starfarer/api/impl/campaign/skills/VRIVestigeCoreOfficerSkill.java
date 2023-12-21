package com.fs.starfarer.api.impl.campaign.skills;

import com.fs.starfarer.api.characters.LevelBasedEffect;
import com.fs.starfarer.api.characters.ShipSkillEffect;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.campaign.ids.Stats;

public class VRIVestigeCoreOfficerSkill {

    public VRIVestigeCoreOfficerSkill(){
    }

    public static class Level1 implements ShipSkillEffect {
        public Level1() {

        }

        public void apply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id, float level) {

        }

        public void unapply(MutableShipStatsAPI stats, ShipAPI.HullSize hullSize, String id) {

        }

        public String getEffectDescription(float level) {
            return "r";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return ScopeDescription.PILOTED_SHIP;
        }
    }
}
