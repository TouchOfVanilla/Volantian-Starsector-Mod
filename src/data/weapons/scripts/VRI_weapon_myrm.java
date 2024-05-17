package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;
import java.util.Map;

public class VRI_weapon_myrm implements EveryFrameWeaponEffectPlugin, OnHitEffectPlugin, OnFireEffectPlugin{

    IntervalUtil timer = new IntervalUtil(1f, 1f);
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
        ShipAPI ship = weapon.getShip();
        Vector2f firepoint = weapon.getFirePoint(0);
        float oppositeangle = weapon.getCurrAngle() - 180;

        Vector2f arcdest = MathUtils.getRandomPointInCone(firepoint, 70, oppositeangle - 3, oppositeangle + 3);
        //time between arcs first min second max

        if (weapon.isFiring()) {
            timer.advance(weapon.getChargeLevel());
        }


        if (timer.intervalElapsed()){
                engine.spawnEmpArcVisual(firepoint, ship, arcdest, ship, 1f, new Color(25,25,25,125), new Color(225,225,225,225));
        }

    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {

    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {

    }
}
