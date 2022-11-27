package data.weapons.scripts;

import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.util.Random;

public class VRI_weapon_opalescent implements OnFireEffectPlugin{

    private final float Damage = 80f;
    private final float range = 700f;
    private final Color zapCore = new Color(255, 255,255, 255);
    private final Color zapFringe = new Color(0, 255,190, 255);
    private final float maxThicc = 30;
    private final float minThicc = 10;
    private final Random r = new Random();

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        //Not working currently
        CombatEntityAPI target = projectile.getDamageTarget(); //return null, need fix

        if(target instanceof ShipAPI || target instanceof MissileAPI || target instanceof FighterWingAPI) {
            Vector2f hitLocation = target.getShield().getLocation();
            if(target.getShield() == null || target.getShield().isOff()) {
                hitLocation = target.getLocation();
            }
            float randomThicc = minThicc + r.nextFloat() * (maxThicc - minThicc);
            //zap arc
            EmpArcEntityAPI emp = engine.spawnEmpArcVisual(
                    projectile.getSpawnLocation(),
                    projectile.getSource(),
                    hitLocation,
                    target,
                    randomThicc,
                    zapFringe,
                    zapCore
            );
        }
    }

}
