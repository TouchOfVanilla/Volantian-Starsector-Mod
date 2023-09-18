package data.weapons.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

public class VRI_weapon_flechette implements OnHitEffectPlugin, OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    {
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {


    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        projectile.getVelocity().scale(MathUtils.getRandomNumberInRange(0.70f, 1.30f));

    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

    }
}
