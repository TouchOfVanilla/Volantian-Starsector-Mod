package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.SettingsAPI;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_shipSystem_temporaltransit extends BaseShipSystemScript {
	public static final Color JITTER_COLOR = new Color(163,185,216,25);
	public static final float MAX_TIME_MULT = 3f;
	public static final Color JITTER_UNDER_COLOR = new Color(163,185,216,45);
	private final int totalFlareCount = 8;
	private boolean activeOnce = false;
	private final IntervalUtil spawnSpriteInterval = new IntervalUtil(0.2f, 0.2f);
	
	public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
		
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}

		CombatEngineAPI engine = Global.getCombatEngine();

		float jitterLevel = effectLevel;
		float jitterRangeBonus = 0;
		float maxRangeBonus = 10f;
		if (state == State.IN) {
			jitterLevel = effectLevel / (1f / ship.getSystem().getChargeUpDur());
			if (jitterLevel > 1) {
				jitterLevel = 0.8f;
			}
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		} else if (state == State.ACTIVE) {
			jitterLevel = 1f;
			jitterRangeBonus = maxRangeBonus;
                       
		} else if (state == State.OUT) {
			jitterRangeBonus = jitterLevel * maxRangeBonus;
		}
		jitterLevel = (float) Math.sqrt(jitterLevel);
		effectLevel *= effectLevel;
		
		ship.setJitter(this, JITTER_COLOR, jitterLevel, 3, 0, 0 + jitterRangeBonus);
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, jitterLevel, 25, 0f, 7f + jitterRangeBonus);

		float shipTimeMult = 1f + (MAX_TIME_MULT - 1f) * effectLevel;
		stats.getTimeMult().modifyMult(id, shipTimeMult);
		if (player) {
			Global.getCombatEngine().getTimeMult().modifyMult(id, 1f / shipTimeMult);
		} else {
			Global.getCombatEngine().getTimeMult().unmodify(id);
		}
		if(!activeOnce) {
			createActivatedEffect(engine, ship, ship.getLocation());
			activeOnce = true;
		}
		createPhaseOutShip(engine, ship, ship.getLocation());
	}
	
	
	public void unapply(MutableShipStatsAPI stats, String id) {
		ShipAPI ship = null;
		boolean player = false;
		if (stats.getEntity() instanceof ShipAPI) {
			ship = (ShipAPI) stats.getEntity();
			player = ship == Global.getCombatEngine().getPlayerShip();
			id = id + "_" + ship.getId();
		} else {
			return;
		}
		activeOnce = false;
	}

	private void createActivatedEffect(CombatEngineAPI engine, ShipAPI ship, Vector2f point) {
		//flare
		int flareCounter = 0;
		while(flareCounter < totalFlareCount) {
			flareCounter += 1;
			MagicLensFlare.createSharpFlare(
					engine,
					ship,
					MathUtils.getRandomPointInCircle(
							point,
							ship.getCollisionRadius() + 50f
					),
					2,
					20,
					0,
					new Color(0, 255, 255,255),
					new Color(200,200,255)
			);
		}
		//ripple
		RippleDistortion ripple = new RippleDistortion(point, new Vector2f());
		ripple.setIntensity(12f);
		ripple.setSize(80f);
		ripple.setArc(0, 360);
		ripple.flip(true);
		ripple.setLifetime(0.35f);
		ripple.fadeOutIntensity(0.8f);
		ripple.setLocation(point);
		DistortionShader.addDistortion(ripple);
	}
	private void createPhaseOutShip(CombatEngineAPI engine, ShipAPI ship, Vector2f point) {
		SpriteAPI shipSprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
		spawnSpriteInterval.advance(Global.getCombatEngine().getElapsedInLastFrame());
		if (spawnSpriteInterval.intervalElapsed()) {
			MagicRender.battlespace(
					shipSprite,
					point,
					new Vector2f(),
					new Vector2f(shipSprite.getWidth(), shipSprite.getHeight()),
					new Vector2f(20, 20),
					ship.getFacing() - 90f,
					0f,
					new Color(0, 170, 200, 255),
					true,
					0.1f,
					0.2f,
					0.6f
			);
			MagicLensFlare.createSharpFlare(
					engine,
					ship,
					MathUtils.getRandomPointInCircle(
							point,
							ship.getCollisionRadius() + 50f
					),
					1,
					10,
					0,
					new Color(0, 255, 255,255),
					new Color(200,200,255)
			);
		}
	}
}