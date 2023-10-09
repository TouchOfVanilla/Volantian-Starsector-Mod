package data.weapons.scripts;

import com.fs.starfarer.api.combat.BeamAPI;
import com.fs.starfarer.api.combat.BeamEffectPlugin;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;

public class VRI_weapon_azorian_graviton implements BeamEffectPlugin {
    @Override
    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        if (target == null){
            return;
        } else if(target.equals(target.getShield())){

        }

    }
}
