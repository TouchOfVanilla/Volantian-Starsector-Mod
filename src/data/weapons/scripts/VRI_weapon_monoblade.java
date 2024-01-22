package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.util.A;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Random;

public class VRI_weapon_monoblade implements OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

    private final int count = 4;
    private boolean wantSetReload = false;
    private int start = 0;
    private final int arcOffsetMin = -50;
    private final int arcOffsetMax = 50;

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
        if(!projectile.isFading() && target instanceof ShipAPI) {

            RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
            ripple.setIntensity(5f);
            ripple.setSize(120f);
            ripple.setArc(0, 360);
            ripple.flip(true);
            ripple.setLifetime(0.75f);
            ripple.fadeOutIntensity(0.8f);
            ripple.setLocation(point);
            DistortionShader.addDistortion(ripple);

            engine.spawnExplosion(
                    point,
                    new Vector2f(0, 0),
                    new Color(255, 0, 100, 255),
                    200f,
                    0.2f
            );

            while(start < count) {
                engine.spawnExplosion(
                        point,
                        new Vector2f(0, 0),
                        new Color(100, 0, 0, 255),
                        getRandomNumberInRange(160, 140),
                        0.5f
                );

                engine.spawnExplosion(
                        point,
                        new Vector2f(0, 0),
                        new Color(180, 0, 150, 255),
                        getRandomNumberInRange(70, 50),
                        0.7f
                );

                start += 1;
            }
            Global.getSoundPlayer().playSound("vol_monoblade_impact", 1, 1f, point, new Vector2f(20, 20));
        }

    }

    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        //create EMP arcs
        Iterator projiter = engine.getProjectiles().iterator();
        while (projiter.hasNext()) {
            DamagingProjectileAPI proj = (DamagingProjectileAPI) projiter.next();
            if (proj.getWeapon() == weapon) {
                Vector2f empSourceLocation = new Vector2f(
                        proj.getLocation().x + getRandomNumberInRange(arcOffsetMax, arcOffsetMin),
                        proj.getLocation().y + getRandomNumberInRange(arcOffsetMax, arcOffsetMin)
                );
                Vector2f projSourceLocation = new Vector2f(
                        proj.getLocation().x + getRandomNumberInRange(arcOffsetMax, arcOffsetMin),
                        proj.getLocation().y + getRandomNumberInRange(arcOffsetMax, arcOffsetMin)
                );
                engine.spawnEmpArcVisual(
                        projSourceLocation,
                        new SimpleEntity(projSourceLocation),
                        empSourceLocation,
                        new SimpleEntity(empSourceLocation),
                        getRandomNumberInRange(20, 10),
                        new Color(255, 0, 150, 255),
                        new Color(255, 255, 255, 255)
                );
            }
        }
        if (wantSetReload) {
            weapon.setRemainingCooldownTo(.75f);
            wantSetReload = false;
        }
    }

    private float getRandomNumberInRange(int max, int min) {
        Random r = new Random();
        return r.nextInt((max - min) + 1) + min;
    }
}
