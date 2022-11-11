package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import org.lazywizard.lazylib.combat.AIUtils;

import java.awt.*;

public class VRI_shipSystem_rapturepulse extends BaseShipSystemScript {
	public static final float AOE = 3500f;
	
	public static final Color JITTER_COLOR = new Color(163,185,216,25);
	public static final Color JITTER_UNDER_COLOR = new Color(163,185,216,45);

	public static final Color hot = new Color(240,163,163,255);
	public static final Color hothot = new Color(240,53,53,255);

	
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
	
		if(effectLevel>=0.7 && ship.getSystem().isChargeup()){
			if(AIUtils.getNearbyEnemies(ship,AOE)!=null){
				float d = (ship.getSystem().getEffectLevel() - 0.7f) * 3.3f * AOE;
				for(ShipAPI enemy : AIUtils.getNearbyEnemies(ship,d)){
					if(!enemy.getFluxTracker().isOverloaded()) {
						enemy.getFluxTracker().beginOverloadWithTotalBaseDuration(7f);
						//engine.applyDamage(enemy,enemy.getLocation(),500f,DamageType.ENERGY,500f,true,false,ship);
						//engine.spawnEmpArc(enemy, enemy.getLocation(),enemy,enemy, DamageType.ENERGY,750f,750f,10000f,null,10,hot,hothot);
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

	public StatusData getStatusData(int index, State state, float effectLevel) {

		return null;
                
	}
        
}