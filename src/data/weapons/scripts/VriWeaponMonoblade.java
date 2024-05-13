package data.weapons.scripts;

import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.Color;
import java.util.Iterator;
import java.util.Random;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.EveryFrameWeaponEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;

//added new spawnShapedExplosion class, charge light/firing plume to advance class, needs new imports
public class VriWeaponMonoblade implements OnHitEffectPlugin, EveryFrameWeaponEffectPlugin{

    private int count = 4;
    private boolean wantSetReload = false;
    private int start = 0;
    private int arcOffsetMin = -50;
    private int arcOffsetMax = 50;

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

	public static void spawnShapedExplosion(Vector2f loc, float angle, float objSpeed, Color pc) {
		if (Global.getCombatEngine().getViewport().isNearViewport(loc, 800f)) {
			
			int numParticles = 150;
			float minSize = 15;
			float maxSize = 20;
			
			float minDur = 0.3f;
			float maxDur = 0.5f;
			
			float arc = 40;
			float scatter = 40f;
			float minVel = -300f + objSpeed;
			float maxVel = 100f + objSpeed;
			
			float endSizeMin = 1f;
			float endSizeMax = 2f;
			
			Vector2f spawnPoint = new Vector2f(loc);
			for (int i = 0; i < numParticles; i++) {
				float angleOffset = (float) Math.random();
				if (angleOffset > 0.2f) {
					angleOffset *= angleOffset;
				}
				float speedMult = 1f - angleOffset;
				speedMult = 0.5f + speedMult * 0.5f;
				angleOffset *= Math.signum((float) Math.random() - 0.5f);
				angleOffset *= arc/2f;
				float theta = (float) Math.toRadians(angle + angleOffset);
				float r = (float) (Math.random() * Math.random() * scatter);
				float x = (float) Math.cos(theta) * r;
				float y = (float) Math.sin(theta) * r;
				Vector2f pLoc = new Vector2f(spawnPoint.x + x, spawnPoint.y + y);
				
				float speed = minVel + (maxVel - minVel) * (float) Math.random();
				speed *= speedMult;
				
				Vector2f pVel = Misc.getUnitVectorAtDegreeAngle((float) Math.toDegrees(theta));
				pVel.scale(speed);
				
				float pSize = minSize + (maxSize - minSize) * (float) Math.random();
				float pDur = minDur + (maxDur - minDur) * (float) Math.random();
				float endSize = endSizeMin + (endSizeMax - endSizeMin) * (float) Math.random();
				Global.getCombatEngine().addNebulaParticle(pLoc, pVel, pSize, endSize, 0.1f, 0.5f, pDur, pc);
			}
		}
	}
	
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        //create charge light
		if (weapon.getChargeLevel() > 0 && weapon.getCooldownRemaining() == 0) {
			ShipAPI ship = weapon.getShip();
			
			//fringe
			engine.addHitParticle(
					weapon.getLocation(),
					ship.getVelocity(),
					30.0f, //size
					0.3f, //brightness
					0.1f, //duration
					new Color(180, 0, 50, 255));
		
			//core
			engine.addHitParticle(
					weapon.getLocation(),
					ship.getVelocity(),
					20.0f, //size
					0.3f, //brightness
					0.1f, //duration
					new Color(255, 150, 200, 255));
		}
		
		//create firing plume
		if (weapon.getChargeLevel() == 1f) {
			//Vector2f loc = MathUtils.getPointOnCircumference(weapon.getLocation(), 50f, weapon.getCurrAngle());
			
			spawnShapedExplosion(weapon.getLocation(), 
								weapon.getCurrAngle(), 
								1200f, 
								new Color(200, 50, 180, 200));
			
		}
		
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
