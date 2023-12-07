package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.WeaponBaseRangeModifier;
import com.fs.starfarer.api.combat.CombatEngineAPI;
import com.fs.starfarer.api.combat.CombatEntityAPI;
import com.fs.starfarer.api.combat.DamageType;
import com.fs.starfarer.api.combat.DamagingProjectileAPI;
import com.fs.starfarer.api.combat.OnHitEffectPlugin;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.combat.listeners.WeaponRangeModifier;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_ModPlugin;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.combat.OnFireEffectPlugin;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;


    public class VRI_weapon_harpe implements EveryFrameWeaponEffectPlugin, OnHitEffectPlugin, OnFireEffectPlugin
    {
        private static Logger log = Global.getLogger(VRI_weapon_harpe.class);
        public static final float BALLISTIC_SLOT_RANGE_FLAT = 400f;
        public static final float ENERGY_SLOT_FIRE_RATE_MULT = 1f;
        public static final float HYBRID_SLOT_EMP_ARC_CHANCE = 0.6f;
        public static WeaponAPI weapon;
private boolean wantSetReload = false;
        private boolean firstRun = true;

        @Override
        public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {
            if (firstRun && !weapon.getShip().hasListenerOfClass(VolantianHarpeListener.class)) {
                weapon.getShip().addListener(new VolantianHarpeListener());
                firstRun = false;
            }
            if (wantSetReload) {
                weapon.setRemainingCooldownTo(.75f);
                wantSetReload = false;
            }
        }
        @Override
        public void onFire(DamagingProjectileAPI proj, WeaponAPI weapon, CombatEngineAPI engine){
        if (weapon.getSlot().getWeaponType().equals(WeaponAPI.WeaponType.ENERGY)){
            log.info(weapon.getCooldown());
            wantSetReload = true;
            }
        }

        @Override
        public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
            if ((float)Math.random() > HYBRID_SLOT_EMP_ARC_CHANCE && target instanceof ShipAPI && !shieldHit && projectile.getWeapon().getSlot().getWeaponType().equals(WeaponAPI.WeaponType.HYBRID)) {
                float emp = 40;
                float dam = projectile.getDamageAmount();
                engine.spawnEmpArc(projectile.getSource(), point, target, target, DamageType.ENERGY, dam, emp, 100000.0F, "tachyon_lance_emp_impact", 20.0F, new Color(25, 100, 155, 255), new Color(255, 255, 255, 255));
                engine.spawnEmpArc(projectile.getSource(), point, target, target, DamageType.ENERGY, dam, emp, 100000.0F, "tachyon_lance_emp_impact", 20.0F, new Color(25, 100, 155, 255), new Color(255, 255, 255, 255));
                engine.spawnEmpArc(projectile.getSource(), point, target, target, DamageType.ENERGY, dam, emp, 100000.0F, "tachyon_lance_emp_impact", 20.0F, new Color(25, 100, 155, 255), new Color(255, 255, 255, 255));

            }
        }

        private class VolantianHarpeListener implements WeaponBaseRangeModifier
        {

            public float getWeaponBaseRangePercentMod(ShipAPI shipAPI, WeaponAPI weaponAPI)
            {
                return 0;
            }

            @Override
            public float getWeaponBaseRangeMultMod(ShipAPI shipAPI, WeaponAPI weaponAPI)
            {
                return 1f;
            }

            @Override
            public float getWeaponBaseRangeFlatMod(ShipAPI ship, WeaponAPI weapon)
            {
                if ((weapon.getId().equals("vol_harpecannon")) && (weapon.getSlot().getWeaponType() == WeaponAPI.WeaponType.BALLISTIC)){
                    return BALLISTIC_SLOT_RANGE_FLAT;
                }
                    return 0f;
            }
        }
    }

