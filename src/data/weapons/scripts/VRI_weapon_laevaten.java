package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.ApplyDamageResultAPI;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import data.scripts.util.MagicFakeBeam;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_weapon_laevaten implements OnFireEffectPlugin{

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
    public void onFire(DamagingProjectileAPI projectile, WeaponAPI weapon, CombatEngineAPI engine) {
        MagicFakeBeam.spawnAdvancedFakeBeam(
                engine,
                projectile.getLocation(),
                weapon.getRange(),
                projectile.getFacing(),
                20,
                30,
                10f,
                "vol_laevaten_beam",
                "vol_laevaten_beam",
                120,
                1f,
                50f,
                0.2f,
                0.5f,
                0.7f,
                40,
                new Color(255, 255, 255, 255),
                new Color(135, 125, 255, 255),
                projectile.getDamageAmount(),
                projectile.getDamageType(),
                projectile.getEmpAmount(),
                projectile.getSource()
        );
        engine.removeEntity(projectile);
    }
}
