package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicRender;
import org.lazywizard.lazylib.LazyLib;
import org.lazywizard.lazylib.MathUtils;
import org.lazywizard.lazylib.combat.AIUtils;
import org.lazywizard.lazylib.combat.entities.SimpleEntity;
import org.lwjgl.util.vector.Vector2f;
import sound.L;

import java.awt.*;

public class VRI_shipSystem_rapturepulse extends BaseShipSystemScript {
	public static final float AOE = 1600f;
	
	public static final Color JITTER_COLOR = new Color(163,185,216,25);
	public static final Color JITTER_UNDER_COLOR = new Color(163,185,216,45);

	public static final Color VRI_COLOR = new Color(31,247,182,255);
	public static final Color VRI_COLOR_DARK = new Color(31,207,152,255);
	private static final int NUM_OF_POINTS = 12;
	private static final int NUM_OF_MARK_POINTS = 5;
	private static float SPIN_OFFSET = 0f;
	private static final float SPIN_STEP = 0.02f;
	private static final float SHIP_MARK_OFFSET = 120f;
	private final IntervalUtil SPAWN_PARTICLES_INTERVAL = new IntervalUtil(0.03f, 0.03f);
	private boolean ACTIVATED_EXPLOSION_MARK = false;
	private boolean PLAY_SOUND_ONCE = false;
	private static final float ARC_OFFSET_MAX = 100f;
	private static final float ARC_OFFSET_MIN = 10f;
	private static final float OVERLOAD_DURATION = 3f;

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
		float maxRangeBonus = 20f;
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

		SPAWN_PARTICLES_INTERVAL.advance(Global.getCombatEngine().getElapsedInLastFrame());
		spawnCircleAroundShip(ship, engine);

		if(effectLevel >= 0.99 && ship.getSystem().isChargeup()){
			if(!PLAY_SOUND_ONCE) {
				Global.getSoundPlayer().playSound("system_emp_emitter_impact", 0.8f, 1.1f, ship.getLocation(), new Vector2f(20, 20));
				PLAY_SOUND_ONCE = true;
			}
			if(AIUtils.getNearbyEnemies(ship,AOE)!=null){
//				float d = (ship.getSystem().getEffectLevel() - 0.7f) * 3.3f * AOE;
				for(ShipAPI enemy : AIUtils.getNearbyEnemies(ship,AOE)){
					if(!enemy.getFluxTracker().isOverloaded() && !ship.isFighter()) {
						enemy.getFluxTracker().beginOverloadWithTotalBaseDuration(OVERLOAD_DURATION);
						//engine.applyDamage(enemy,enemy.getLocation(),500f,DamageType.ENERGY,500f,true,false,ship);
						//engine.spawnEmpArc(enemy, enemy.getLocation(),enemy,enemy, DamageType.ENERGY,750f,750f,10000f,null,10,hot,hothot);
					}

					//ship emp arcs
					Vector2f firstLocation = MathUtils.getRandomPointOnCircumference(ship.getLocation(), ship.getCollisionRadius() + ARC_OFFSET_MAX);
					Vector2f secondLocation = MathUtils.getRandomPointOnCircumference(ship.getLocation(), ship.getCollisionRadius() + ARC_OFFSET_MIN);
					engine.spawnEmpArcVisual(
							firstLocation,
							new SimpleEntity(firstLocation),
							secondLocation,
							new SimpleEntity(secondLocation),
							MathUtils.getRandomNumberInRange(20,10),
							VRI_COLOR,
							new Color(255, 255,255, 255)
					);

					//Unmark explosion
					SpriteAPI shipSprite = Global.getSettings().getSprite(enemy.getHullSpec().getSpriteName());
					if(!ACTIVATED_EXPLOSION_MARK) {
						MagicRender.battlespace(
								Global.getSettings().getSprite("fx", "vol_rapture_mark"),
								enemy.getLocation(),
								new Vector2f(0, 0),
								new Vector2f(
										shipSprite.getWidth() + SHIP_MARK_OFFSET + 150f,
										shipSprite.getHeight() + SHIP_MARK_OFFSET + 150f
								),
								new Vector2f(-150, -150),
								0,
								0f,
								VRI_COLOR_DARK,
								true,
								0,
								0.2f,
								0.5f
						);
					}

				}
				ACTIVATED_EXPLOSION_MARK = true;
			}

		} else {
			if(!ACTIVATED_EXPLOSION_MARK) {
				markEnemyShip(ship, engine);
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
		ACTIVATED_EXPLOSION_MARK = false;
		PLAY_SOUND_ONCE = false;

	}

	public StatusData getStatusData(int index, State state, float effectLevel) {

		return null;
                
	}

	private void spawnCircleAroundShip(ShipAPI ship, CombatEngineAPI engine) {
		int i = 0;
		if(SPAWN_PARTICLES_INTERVAL.intervalElapsed()) {
			while(i < NUM_OF_POINTS) {
				i++;
				SPIN_OFFSET += SPIN_STEP;
				Float angle = (float) ((360 / NUM_OF_POINTS * i) + SPIN_OFFSET);
				Vector2f spawnNebulaPoint = MathUtils.getPointOnCircumference(ship.getLocation(), AOE, angle);
				engine.addNebulaParticle(
						spawnNebulaPoint,
						new Vector2f(10, 10),
						10f,
						5f,
						1f,
						1f,
						0.5f,
						VRI_COLOR
				);
			}
		}
	}

	private void markEnemyShip(ShipAPI ship, CombatEngineAPI engine) {
		for(ShipAPI enemy : AIUtils.getNearbyEnemies(ship,AOE)){
			int i = 0;
			while(i < NUM_OF_MARK_POINTS) {
				i++;
				SPIN_OFFSET += SPIN_STEP;
				Float angle = (float) ((360 / NUM_OF_MARK_POINTS * i) + SPIN_OFFSET);
				Vector2f spawnNebulaPoint = MathUtils.getPointOnCircumference(enemy.getLocation(), ship.getCollisionRadius() + 100f, angle);
				engine.addSmoothParticle(
						spawnNebulaPoint,
						new Vector2f(0, 0),
						15f,
						1f,
						0.2f,
						VRI_COLOR
				);
			}

		}
	}
        
}