package data.weapons.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Random;

public class VRI_weapon_kastane implements OnHitEffectPlugin{

    private int count = 5;
    private int start = 0;
    private int maxExplosionRadius = 50;
    private int minExplosionRadius = -20;
    private int maxSize = 40;
    private int minSize = 20;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(!projectile.isFading() && target instanceof ShipAPI) {
            engine.spawnExplosion(
                    point,
                    new Vector2f(0, 0),
                    new Color(0, 220, 200, 255),
                    120f,
                    0.2f
            );

            while(start < count) {
                engine.spawnExplosion(
                        point,
                        new Vector2f(
                            getRandomNumberInRange(maxExplosionRadius, minExplosionRadius),
                            getRandomNumberInRange(maxExplosionRadius, minExplosionRadius)
                        ),
                        new Color(0, 100, 100, 255),
                        getRandomNumberInRange(maxSize, minSize),
                        0.8f
                );

                start += 1;
            }
        }
    }

    private float getRandomNumberInRange(int max, int min) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
