package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.plugins.MagicTrailPlugin;
import org.lazywizard.lazylib.VectorUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_weapon_trident implements OnHitEffectPlugin{
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
        if(!projectile.isFading() && target instanceof ShipAPI && !shieldHit) {
            explosion.setDamageType(DamageType.HIGH_EXPLOSIVE);
            explosion.setMinDamage(75);
            explosion.setMaxDamage(75);
            engine.spawnDamagingExplosion(explosion, projectile.getSource(), point);
        }
    }
}
