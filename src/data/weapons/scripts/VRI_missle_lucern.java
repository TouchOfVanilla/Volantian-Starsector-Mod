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

public class VRI_missle_lucern implements OnHitEffectPlugin{

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(!projectile.isFading() && target instanceof ShipAPI) {

            RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
            ripple.setIntensity(2f);
            ripple.setSize(100f);
            ripple.setArc(0, 360);
            ripple.flip(true);
            ripple.setLifetime(0.35f);
            ripple.fadeOutIntensity(0.8f);
            ripple.setLocation(point);
            DistortionShader.addDistortion(ripple);

            MagicRender.battlespace(
                    Global.getSettings().getSprite("fx", "vol_lucern_explosion"),
                    point,
                    new Vector2f(),
                    new Vector2f(80, 80),
                    new Vector2f(120, 120),
                    //angle,
                    360 * (float) Math.random(),
                    0.2f,
                    new Color(170, 0, 210, 255),
                    true,
                    0,
                    0.3f,
                    0.8f
            );

            if (shieldHit) {
                engine.spawnEmpArcPierceShields(
                        projectile.getSource(),
                        point,
                        projectile.getDamageTarget(),
                        projectile.getDamageTarget(),
                        DamageType.ENERGY,
                        0,
                        200,
                        100000,
                        "tachyon_lance_emp_impact",
                        10f,
                        new Color(250, 0, 210, 255),
                        new Color(255, 255, 255, 255)
                );
            }
        }
    }
}
