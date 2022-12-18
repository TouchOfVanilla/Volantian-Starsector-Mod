package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.HashSet;

public class VRI_shipSystem_transfixshift extends BaseShipSystemScript {
	public static float SPEED_BONUS = 300f;
	public static float TURN_BONUS = 100f;
	private final IntervalUtil spawnSpriteInterval = new IntervalUtil(0.2f, 0.2f);
	private boolean activeSpawnSpriteOnce = false;
	public static final Color JITTER_COLOR = new Color(100,255,200,25);
	public static final Color JITTER_UNDER_COLOR = new Color(100,255,200,45);
	private static final float DAMAGE_NEGATED = 0.2f;
	private static final float HARD_FLUX_DISSIPATION_PERCENT = 25f;

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

		stats.getMaxSpeed().modifyFlat(id, SPEED_BONUS);
		stats.getTurnAcceleration().modifyFlat(id, TURN_BONUS);
		stats.getTurnAcceleration().modifyPercent(id, TURN_BONUS * 4f);
		stats.getMaxTurnRate().modifyFlat(id, 60f);
		stats.getMaxTurnRate().modifyPercent(id, 100f);
		stats.getArmorDamageTakenMult().modifyMult(id, DAMAGE_NEGATED);

		ship.setJitter(this, JITTER_COLOR, 1f, 3, 0, 5f);
		ship.setJitterUnder(this, JITTER_UNDER_COLOR, 1f, 25, 0f, 10f);

		if (state == State.IN) {
			stats.getAcceleration().modifyMult(id, 50f);
			stats.getDeceleration().modifyMult(id, 50f);
		}
		if (state == State.ACTIVE) {
			if (ship.getVelocity().length() < ship.getMaxSpeed())
			{
				ship.getVelocity().scale(200f);
				VectorUtils.clampLength(ship.getVelocity(), ship.getMaxSpeed(), ship.getVelocity());
			}
			stats.getAcceleration().modifyMult(id, 0f);
			stats.getDeceleration().modifyMult(id, 0f);
			stats.getHardFluxDissipationFraction().modifyFlat(id, 120f);
			if(ship.isPhased()) {
				stats.getPhaseCloakUpkeepCostBonus().modifyMult(id, 0);
			}
		}
		if (state == State.OUT) {
			stats.getMaxSpeed().unmodify(id);
			stats.getAcceleration().modifyMult(id, 50f);
			stats.getDeceleration().modifyMult(id, 50f);
			stats.getMaxTurnRate().unmodify(id);
		}

		if(!activeSpawnSpriteOnce) {
			activeSpawnSpriteOnce = true;
			SpriteAPI shipSprite = Global.getSettings().getSprite(ship.getHullSpec().getSpriteName());
			MagicRender.battlespace(
					shipSprite,
					ship.getLocation(),
					new Vector2f(),
					new Vector2f(shipSprite.getWidth(), shipSprite.getHeight()),
					new Vector2f(MathUtils.getRandomNumberInRange(20, 100), MathUtils.getRandomNumberInRange(20, 100)),
					ship.getFacing() - 90f,
					0f,
					new Color(200, 255, 240, 200),
					false,
					0.1f,
					0f,
					1.2f
			);
		}

		spawnSpriteInterval.advance(Global.getCombatEngine().getElapsedInLastFrame());
		if(spawnSpriteInterval.intervalElapsed()) {
			ship.addAfterimage(new Color(30, 70, 70, 100),
					0f,
					0f,
					ship.getVelocity().x * -0.8f,
					ship.getVelocity().y * -0.8f,
					0,
					0f,
					0f,
					0.8f,
					true,
					true,
					false);
		}
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

		activeSpawnSpriteOnce = false;
		stats.getMaxSpeed().unmodify(id);
		stats.getMaxTurnRate().unmodify(id);
		stats.getTurnAcceleration().unmodify(id);
		stats.getAcceleration().unmodify(id);
		stats.getDeceleration().unmodify(id);
		stats.getArmorDamageTakenMult().unmodify(id);
		stats.getHardFluxDissipationFraction().unmodify(id);
		stats.getPhaseCloakUpkeepCostBonus().unmodify(id);
	}
}