package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.plugins.MagicTrailPlugin;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

import static data.weapons.scripts.VRI_weapon_harpe.BALLISTIC_SLOT_RANGE_FLAT;

public class VRI_weapon_trident implements OnHitEffectPlugin, OnFireEffectPlugin, EveryFrameWeaponEffectPlugin{
    private boolean wantSetReload = false;
    private boolean firstRun = true;
    private final DamagingExplosionSpec explosion = new DamagingExplosionSpec(0.05f,
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
            new Color(255, 150, 35, 255)
    );

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(!projectile.isFading() && target instanceof ShipAPI && !shieldHit && projectile.getWeapon().getSlot().getWeaponType().equals(WeaponAPI.WeaponType.COMPOSITE)) {
            explosion.setDamageType(DamageType.HIGH_EXPLOSIVE);
            explosion.setMinDamage(250);
            explosion.setMaxDamage(250);
            engine.spawnDamagingExplosion(explosion, projectile.getSource(), point);
        }
    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        if (firstRun && !weapon.getShip().hasListenerOfClass(VRI_weapon_trident.VolantianTridentListener.class)) {
            weapon.getShip().addListener(new VRI_weapon_trident.VolantianTridentListener());
            firstRun = false;
        }
        if (wantSetReload) {
            weapon.setRemainingCooldownTo(.75f);
            wantSetReload = false;
        }
    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        if (weapon.getSlot().getWeaponType().equals(WeaponAPI.WeaponType.MISSILE)){
            wantSetReload = true;
        }

    }
    private class VolantianTridentListener implements WeaponBaseRangeModifier{

        @Override
        public float getWeaponBaseRangePercentMod(ShipAPI ship, WeaponAPI weapon) {
            return 0;
        }

        @Override
        public float getWeaponBaseRangeMultMod(ShipAPI ship, WeaponAPI weapon) {
            return 1f;
        }

        @Override
        public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon) {
            if ((weapon.getId().equals("vol_trident")) && (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.BALLISTIC))
                return BALLISTIC_SLOT_RANGE_FLAT;
            return 1;
        }
        }
    }

