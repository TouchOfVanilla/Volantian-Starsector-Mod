package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicRender;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_missle_katti_warhead implements OnHitEffectPlugin{

    private final DamagingExplosionSpec explosion = new DamagingExplosionSpec(0.05f,
            50,
            20f,
            500,
            250,
            CollisionClass.PROJECTILE_FF,
            CollisionClass.PROJECTILE_FIGHTER,
            1,
            30,
            0.1f,
            30,
            new Color(33, 255, 122, 255),
            new Color(0, 255, 255, 255)
    );

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(!projectile.isFading() && target instanceof ShipAPI && !shieldHit) {

            explosion.setDamageType(DamageType.HIGH_EXPLOSIVE);
            explosion.setMinDamage(700);
            explosion.setMaxDamage(700);
            engine.spawnDamagingExplosion(explosion, projectile.getSource(), point);

            RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
            ripple.setIntensity(5f);
            ripple.setSize(150f);
            ripple.setArc(0, 360);
            ripple.flip(true);
            ripple.setLifetime(0.55f);
            ripple.fadeOutIntensity(0.8f);
            ripple.setLocation(point);
            DistortionShader.addDistortion(ripple);

            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "vol_katti_explosion"),
                    point,
                    new Vector2f(),
                    new Vector2f(100, 100),
                    new Vector2f(150, 150),
                    //angle,
                    360 * (float) Math.random(),
                    0.5f,
                    new Color(100,255,230, 255),
                    true,
                    0,
                    0.3f,
                    0.8f
            );
            Global.getSoundPlayer().playSound("hit_heavy_energy", 1, 1f, point, new Vector2f(20, 20));
        }
    }
}
