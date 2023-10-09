package com.fs.starfarer.api.impl.campaign.skills;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.characters.*;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.util.Misc;

import static com.fs.starfarer.api.impl.campaign.econ.impl.ItemEffectsRepo.PRISTINE_NANOFORGE_QUALITY_BONUS;

public class VRIAzorianCoreAdminSkill {
    public static float FLEET_QUALITY_FLAT = 0.25f;
    public static float DEMAND_FLAT = 1f;

    public VRIAzorianCoreAdminSkill(){
    }

    public static class Level1 implements CharacterStatsSkillEffect {
        public Level1() {
        }

        public void apply(MutableCharacterStatsAPI stats, String id, float level) {
            stats.getDynamic().getMod("demand_reduction").modifyFlat(id, (float)DEMAND_FLAT);
        }

        public void unapply(MutableCharacterStatsAPI stats, String id) {
            stats.getDynamic().getMod("demand_reduction").unmodifyFlat(id);
        }

        public String getEffectDescription(float level) {
            return "All industries demand " + (int)DEMAND_FLAT + " less unit of all the commodities demanded";
        }

        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }

    public static class Level2 implements MarketSkillEffect {
        public Level2() {
        }

        public void apply(MarketAPI market, String id, float level) {
            market.getStats().getDynamic().getMod("fleet_quality_mod")
                    .modifyFlat(id, PRISTINE_NANOFORGE_QUALITY_BONUS/2, Misc.ucFirst("Volantian Contingency Planning"));
        }

        public void unapply(MarketAPI market, String id) {
            market.getStats().getDynamic().getMod("fleet_quality_mod").unmodify(id);
        }

        public String getEffectDescription(float level) {
            return "+" + (int)(100*FLEET_QUALITY_FLAT) + "% fleet quality";
        }
        public String getEffectPerLevelDescription() {
            return null;
        }

        public LevelBasedEffect.ScopeDescription getScopeDescription() {
            return ScopeDescription.GOVERNED_OUTPOST;
        }
    }
}
