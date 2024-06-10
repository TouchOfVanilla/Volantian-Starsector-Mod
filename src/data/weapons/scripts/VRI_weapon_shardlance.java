package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.graphics.SpriteAPI;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.Color;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

public class VRI_weapon_shardlance implements EveryFrameWeaponEffectPlugin {
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
    }
