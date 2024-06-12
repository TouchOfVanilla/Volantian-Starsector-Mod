package data.weapons.scripts;

import java.awt.Color;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;

public class VolMyrmOnHit implements OnHitEffectPlugin {

	public void onHit(DamagingProjectileAPI proj, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
		if (!shieldHit) {
			MagicRender.battlespace(
					Global.getSettings().getSprite("fx", "vol_myrmidon_explosion"), //sprite
					proj.getLocation(),        //loc
					new Vector2f(),            //vel
					new Vector2f(100, 100),        //size
					new Vector2f(600, 600),        //growth
					proj.getFacing() + 90f,    //angle
					0f,                            //spin
					Color.WHITE,                //color
					true,                        //additive
					0.2f,                        //fadein
					0.05f,                        //full
					0.3f);                        //fadeout

			Global.getSoundPlayer().playSound("vol_myrm_hit", 1f, 1f, point, new Vector2f());
		}
	}
}