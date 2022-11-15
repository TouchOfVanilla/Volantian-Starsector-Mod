package data.weapons.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.WaveDistortion;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_weapon_clover implements OnHitEffectPlugin{

    private static int counter = 0;

    final Vector2f vector = new Vector2f();
    private final DamagingExplosionSpec explosion = new DamagingExplosionSpec(0.05f,
            100,
            50f,
            500,
            250,
            CollisionClass.PROJECTILE_FF,
            CollisionClass.PROJECTILE_FIGHTER,
            1,
            100,
            0.1f,
            200,
            new Color(33, 255, 122, 255),
            new Color(255, 150, 35, 255)
    );

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(!projectile.isFading() && target instanceof ShipAPI) {
            engine.spawnExplosion(
                    point,
                    new Vector2f(0, 0),
                    new Color(255, 0, 0, 255),
                    100f,
                    0.3f);
            if(counter == 9) {
                counter = 0;
                explosion.setDamageType(DamageType.FRAGMENTATION);
                explosion.setMinDamage(300);
                explosion.setMaxDamage(300);
                engine.spawnDamagingExplosion(explosion, projectile.getSource(), point);
            }
            else {
                counter+=1;
            }
        }
    }
}
