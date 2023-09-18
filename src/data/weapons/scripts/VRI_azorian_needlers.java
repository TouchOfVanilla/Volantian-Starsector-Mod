package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.VRI_ModPlugin;
import org.apache.log4j.Logger;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_azorian_needlers implements EveryFrameWeaponEffectPlugin, OnHitEffectPlugin, OnFireEffectPlugin {
    private int counter = 0;
    private static Logger log = Global.getLogger(VRI_azorian_needlers.class);

    final Vector2f vector = new Vector2f();
    private final DamagingExplosionSpec explosion = new DamagingExplosionSpec(0.05f,
            100,
            50f,
            500,
            250,
            CollisionClass.PROJECTILE_FF,
            CollisionClass.PROJECTILE_FIGHTER,
            1,
            100,
            0.1f,
            200,
            new Color(33, 255, 122, 255),
            new Color(255, 150, 35, 255)
    );
    @Override
    public void advance(float amount, CombatEngineAPI engine, WeaponAPI weapon) {

    }

    @Override
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        projectile.getDamageType().setShieldMult(2.5f);

    }

    @Override
    public void onHit(DamagingProjectileAPI projectile, CombatEntityAPI target, Vector2f point, boolean shieldHit, ApplyDamageResultAPI damageResult, CombatEngineAPI engine) {
    }
}
