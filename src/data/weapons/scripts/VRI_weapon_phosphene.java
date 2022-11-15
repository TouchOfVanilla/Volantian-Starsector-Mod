package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.dark.shaders.distortion.RippleDistortion;
import org.dark.shaders.distortion.DistortionShader;

import java.awt.*;

public class VRI_weapon_phosphene implements BeamEffectPlugin{

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
            new Color(35, 255, 235, 255),
            new Color(55, 85, 255, 255)
    );

    private IntervalUtil fireInterval = new IntervalUtil(0.6f, 0.7f);
    private int flareCounter = 0;
    private int totalFlareCount = 16;

    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
            Vector2f point = beam.getRayEndPrevFrame();
            ShipAPI ship = (ShipAPI) target;
            float dur = beam.getDamage().getDpsDuration();
            fireInterval.advance(dur);

            while(flareCounter < totalFlareCount) {
                flareCounter += 1;
                MagicLensFlare.createSharpFlare(
                        engine,
                        ship,
                        MathUtils.getRandomPointInCircle(
                                point,
                                140f
                        ),
                        3,
                        10,
                        0,
                        new Color(70, 60, 255,255),
                        new Color(200,200,255)
                );
            }

            if (fireInterval.intervalElapsed()) {
                if(Global.getSettings().getModManager().isModEnabled("shaderLib")){
                    RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
                    ripple.setIntensity(15f);
                    ripple.setSize(250f);
                    ripple.setArc(0, 360);
                    ripple.flip(true);
                    ripple.setLifetime(0.75f);
                    ripple.fadeOutIntensity(0.8f);
                    ripple.setLocation(point);
                    DistortionShader.addDistortion(ripple);
                }

                MagicRender.battlespace(
                        Global.getSettings().getSprite("fx", "vol_phosphene_explosion"),
                        point,
                        new Vector2f(),
                        new Vector2f(100, 100),
                        new Vector2f(230, 230),
                        //angle,
                        360 * (float) Math.random(),
                        0.2f,
                        new Color(70, 0, 210, 255),
                        true,
                        0,
                        0.5f,
                        0.8f
                );
                explosion.setDamageType(DamageType.FRAGMENTATION);
                explosion.setMinDamage(1000);
                explosion.setMaxDamage(1000);
                engine.spawnDamagingExplosion(explosion, ship, point);

                engine.spawnExplosion(point,
                        new Vector2f(0,0),
                        new Color(255, 255, 255,255),
                        2.5f,
                        0.2f);

                Global.getSoundPlayer().playSound("riftcascade_rift", 1, 1f, point, new Vector2f(20, 20));
            }
        }
    }
}
