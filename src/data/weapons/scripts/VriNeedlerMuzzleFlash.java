package data.weapons.scripts;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;

import org.lazywizard.lazylib.MathUtils;

public class VriNeedlerMuzzleFlash implements OnHitEffectPlugin, OnFireEffectPlugin {
	
	private static Color CORE_COLOR = new Color(150,255,200,255);
	private static Color FRINGE_COLOR = new Color(75,255,185,180);
		
	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		Global.getSoundPlayer().playSound("vol_needler_hit", 1f, 1f, point, new Vector2f());
	}
	
    public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine) {
		ShipAPI ship = weapon.getShip();
		Vector2f projLoc = MathUtils.getPointOnCircumference(proj.getLocation(), 15f, weapon.getCurrAngle());
				
        engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				50.0f, //size
				0.8f, //brightness
				0.15f, //duration
				FRINGE_COLOR);
		
		engine.addHitParticle(
				projLoc,
				ship.getVelocity(),
				20.0f, //size
				1.0f, //brightness
				0.1f, //duration
				CORE_COLOR);
		
    }
}