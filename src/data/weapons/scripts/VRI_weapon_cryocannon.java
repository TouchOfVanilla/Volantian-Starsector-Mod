package data.weapons.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_weapon_cryocannon implements OnHitEffectPlugin, OnFireEffectPlugin, EveryFrameWeaponEffectPlugin {
    private final DamagingExplosionSpec explosion = new DamagingExplosionSpec(0.1f,
            5,
            10f,
            500,
            250,
            CollisionClass.PROJECTILE_FF,
            CollisionClass.PROJECTILE_FIGHTER,
            1,
            3,
            0.1f,
            3,
            new Color(33, 255, 122, 255),
            new Color(35, 248, 255, 255)
    );

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (projectile.getWeapon().getSlot().getWeaponType().equals(WeaponAPI.WeaponType.ENERGY)) {
            projectile.setDamageAmount(325);
        }
        if (projectile.getWeapon().getSlot().getWeaponType().equals(WeaponAPI.WeaponType.HYBRID)) {
            projectile.getDamageType().setShieldMult(2);
        }

    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (!projectile.isFading() && target instanceof ShipAPI && !shieldHit && projectile.getWeapon().getSlot().getWeaponType().equals(WeaponAPI.WeaponType.SYNERGY)) {
            explosion.setDamageType(DamageType.HIGH_EXPLOSIVE);
            explosion.setMinDamage(100);
            explosion.setMaxDamage(100);
            engine.spawnDamagingExplosion(explosion, projectile.getSource(), point);
            }
        }
    }