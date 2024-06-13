package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class VRI_weapon_shardlance implements EveryFrameWeaponEffectPlugin, OnHitEffectPlugin {
    IntervalUtil timer = new IntervalUtil(1f, 1f);
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        boolean fired = weapon.getCooldownRemaining() > 0;
        SpriteAPI CenterLatch = Global.getSettings().getSprite("fx", "vol_shardlance_recoil");
        float FacingAngle = weapon.getCurrAngle() - 90;
        Vector2f loc = getLatchLoc(weapon);
        ShipAPI ship = weapon.getShip();
        Vector2f latchsize = new Vector2f(CenterLatch.getWidth(), CenterLatch.getHeight());
        java.awt.Color colore = new java.awt.Color(255,255,255);
        MagicRender.singleframe(CenterLatch,loc, latchsize, FacingAngle, colore, false, CombatEngineLayers.ABOVE_SHIPS_AND_MISSILES_LAYER);

        Vector2f fromright = MathUtils.getPoint(weapon.getLocation(), 35, weapon.getCurrAngle()-140);
        Vector2f fromleft = MathUtils.getPoint(weapon.getLocation(), 35, weapon.getCurrAngle()+140);
        Vector2f toright = MathUtils.getPoint(weapon.getLocation(), 35, weapon.getCurrAngle()-75);
        Vector2f toleft = MathUtils.getPoint(weapon.getLocation(), 35, weapon.getCurrAngle()+75);

        Vector2f rightmin = MathUtils.getPoint(weapon.getLocation(), 35, weapon.getCurrAngle()-80);
        Vector2f leftmin = MathUtils.getPoint(weapon.getLocation(), 35, weapon.getCurrAngle()+80);

        if (fired){
            toright = Misc.interpolateVector(toright, rightmin, weapon.getCooldownRemaining());
            toleft = Misc.interpolateVector(toleft, leftmin, weapon.getCooldownRemaining());
        }

        timer.advance(0.1f);
            if (timer.intervalElapsed()) {
                    engine.spawnEmpArcVisual(toright, ship, fromright, ship, 7f, new java.awt.Color(25, 250, 50, 0), new Color(0, 225, 175, 225));
                engine.spawnEmpArcVisual(toleft, ship, fromleft, ship, 7f, new java.awt.Color(25, 250, 50, 0), new Color(0, 225, 175, 225));
        }
    }
    public Vector2f getLatchLoc(WeaponAPI weapon){
        boolean fired = weapon.getCooldownRemaining() > 0;
        Vector2f loc = new Vector2f();
        Vector2f weaponloc = weapon.getLocation();
        Vector2f targetloc = MathUtils.getPoint(weapon.getLocation(), 50,weapon.getCurrAngle());

        float y = weaponloc.y;
        float x = weaponloc.x;

        if (weapon.isFiring()){
            loc.set(weaponloc);
        }
        if (fired){
            loc.set(Misc.interpolateVector(weaponloc, targetloc, weapon.getChargeLevel()));
        }
        if (!weapon.isFiring() && !fired){
            loc.set(weaponloc);
        }
        return loc;
    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
    }
}
