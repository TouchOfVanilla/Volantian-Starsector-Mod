package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class VRI_laidlawaccelerator implements OnHitEffectPlugin, EveryFrameWeaponEffectPlugin {

    private boolean light = false;

    final Vector2f ZERO = new Vector2f();
    final float damagePercent = 0.25f;

    private final DamagingExplosionSpec explosion = new DamagingExplosionSpec(0.05f,
            125,
            67f,
            500,
            250,
            CollisionClass.PROJECTILE_FF,
            CollisionClass.PROJECTILE_FIGHTER,
            3,
            3,
            0.5f,
            10,
            new Color(33, 255, 122, 255),
            new Color(255, 150, 35, 255)
    );


    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if (!projectile.isFading() && target instanceof ShipAPI && !((ShipAPI) target).isFighter() && !(target instanceof MissileAPI)) {
            explosion.setDamageType(DamageType.FRAGMENTATION);
            explosion.setShowGraphic(false);
            explosion.setMinDamage(projectile.getDamageAmount() * damagePercent * 0.5f);
            explosion.setMaxDamage(projectile.getDamageAmount() * damagePercent);
            engine.spawnDamagingExplosion(explosion, projectile.getSource(), point);
            float num = 0;
            for (num = 0; num < 4; num++){
                engine.spawnEmpArc(projectile.getSource(), point, null, target, DamageType.FRAGMENTATION, 100, 0, 100000f, "tachyon_lance_emp_impact", 50f,
                        new Color(0, 225, 207, 125),
                        new Color(0, 255, 207, 165));
            }






            if (Global.getSettings().getModManager().isModEnabled("shaderLib")) {
            light = true;
        }

            WaveDistortion wave = new WaveDistortion(point, ZERO);
            wave.setIntensity(1.5f);
            wave.setSize(75f);
            wave.flip(true);
            wave.setLifetime(0f);
            wave.fadeOutIntensity(0.66f);
            wave.setLocation(projectile.getLocation());
            DistortionShader.addDistortion(wave);


            engine.spawnExplosion(
                    point,
                    new Vector2f(0, 0),
                    new Color(255, 255, 255, 25),
                    25f,
                    0.3f);

            engine.spawnExplosion(
                    point,
                    new Vector2f(0, 0),
                    new Color(0, 255, 225, 15),
                    50f,
                    0.75f);

            engine.addSmoothParticle(
                    point,
                    new Vector2f(),
                    200,
                    2f,
                    0.15f,
                    new Color(100, 255, 255, 255));


            float angle = 360 * (float) Math.random();

            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "vol_lucern_explosion"),
                    point,
                    new Vector2f(),
                    new Vector2f(72, 72),
                    new Vector2f(300, 300),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    new Color(255, 200, 200, 25),
                    true,
                    0,
                    0.1f,
                    0.15f
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "vol_lucern_explosion"),
                    point,
                    new Vector2f(),
                    new Vector2f(96, 96),
                    new Vector2f(150, 150),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    new Color(225, 225, 225, 25),
                    true,
                    0.2f,
                    0.0f,
                    0.3f
            );
            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "vol_lucern_explosion"),
                    point,
                    new Vector2f(),
                    new Vector2f(150, 150),
                    new Vector2f(75, 75),
                    //angle,
                    360 * (float) Math.random(),
                    0,
                    new Color(255, 255, 255, 20),
                    true,
                    0.4f,
                    0.0f,
                    0.9f
            );
        }

    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

    }
}
