package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.plugins.ShipSystemStatsScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.VectorUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.CombatUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class VRI_shipSystem_guardianarccore extends BaseShipSystemScript {
	private boolean activeOnce = false;
	private final float randomPointOffsetMin = 50f;
	private final float randomPointOffsetMax = 150f;
	private final float randomThiccMax = 10f;
	private final float randomThiccMin = 5f;
	private final float damage = 100f;
	private final float emp = 200f;
	private final float arcTargetsRange = 300f;
	private final IntervalUtil spawnArcInterval = new IntervalUtil(0.1f, 0.1f);
	private final IntervalUtil spawnAfterImageInterval = new IntervalUtil(0.02f, 0.02f);

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

		if (state == ShipSystemStatsScript.State.OUT) {
			stats.getMaxSpeed().unmodify(id); // to slow down ship to its regular top speed while powering drive down
			stats.getMaxTurnRate().unmodify(id);
		} else {
			stats.getMaxSpeed().modifyFlat(id, 20f);
			stats.getAcceleration().modifyPercent(id, 200f * effectLevel);
			stats.getDeceleration().modifyPercent(id, 200f * effectLevel);
			stats.getTurnAcceleration().modifyFlat(id, 30f * effectLevel);
			stats.getTurnAcceleration().modifyPercent(id, 200f * effectLevel);
			stats.getMaxTurnRate().modifyFlat(id, 15f);
			stats.getMaxTurnRate().modifyPercent(id, 100f);
		}

		HashSet<CombatEntityAPI> listTargets = new HashSet<>();
		for (CombatEntityAPI entity: CombatUtils.getEntitiesWithinRange(ship.getLocation(), arcTargetsRange)) {
			if( (entity instanceof ShipAPI || entity instanceof MissileAPI || entity instanceof FighterWingAPI)
					&& entity.getOwner() == 1) {
				listTargets.add(entity);
			}
		}
		spawnArcInterval.advance(Global.getCombatEngine().getElapsedInLastFrame());
		spawnAfterImageInterval.advance(Global.getCombatEngine().getElapsedInLastFrame());
		if(spawnAfterImageInterval.intervalElapsed()) {
			ship.addAfterimage(new Color(30, 70, 70, 255),
					0f,
					0f,
					ship.getVelocity().x * -0.8f,
					ship.getVelocity().y * -0.8f,
					0,
					0f,
					0f,
					0.3f,
					true,
					true,
					false);
		}
		if(spawnArcInterval.intervalElapsed()) {
			if(listTargets.isEmpty()) {
				float startArcArea = (float) (ship.getCollisionRadius() + Math.random() * MathUtils.getRandomNumberInRange(randomPointOffsetMin, randomPointOffsetMax));
				float angle = (float) (Math.random() * 360);
				Vector2f startArcPoint = MathUtils.getPointOnCircumference(ship.getLocation(), startArcArea, angle);
				Vector2f endArcPoint = MathUtils.getPointOnCircumference(ship.getLocation(), startArcArea * 0.5f , angle);

				engine.spawnEmpArc(
						ship,
						startArcPoint,
						new SimpleEntity(startArcPoint),
						new SimpleEntity(endArcPoint),
						DamageType.ENERGY,
						damage,
						emp,
						10000,
						null,
						MathUtils.getRandomNumberInRange(randomThiccMin, randomThiccMax),
						new Color(0, 255,120, 255),
						new Color(255, 255,255, 255)
				);
				createSmokeEffect(engine, startArcPoint, new Vector2f(20,20));

			} else {
				for (CombatEntityAPI target: listTargets) {
					if(spawnArcInterval.intervalElapsed()) {
						float startArcArea = (float) (ship.getCollisionRadius() + Math.random() * MathUtils.getRandomNumberInRange(randomPointOffsetMin, randomPointOffsetMax));
						float arcAngle = VectorUtils.getFacing(Vector2f.sub(target.getLocation(), ship.getLocation(), new Vector2f()));
						Vector2f startArcPoint = MathUtils.getPointOnCircumference(ship.getLocation(), startArcArea, arcAngle);

						engine.spawnEmpArc(
								ship,
								startArcPoint,
								new SimpleEntity(startArcPoint),
								target,
								DamageType.ENERGY,
								damage,
								emp,
								10000,
								null,
								MathUtils.getRandomNumberInRange(randomThiccMin, randomThiccMax),
								new Color(0, 255,120, 255),
								new Color(255, 255,255, 255)
						);
						createSmokeEffect(engine, startArcPoint, new Vector2f(20,20));
					}
					if(!engine.isInPlay(target)) {
						listTargets.remove(target);
					}
				}
			}
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
	}

	private void createSmokeEffect(CombatEngineAPI engine, Vector2f loc, Vector2f vol) {
		engine.addSwirlyNebulaParticle(
				loc,
				vol,
				6f,
				12f,
				0.1f,
				0.2f,
				0.33f,
				new Color(0,255, 200, 255),
				true
		);
	}
}