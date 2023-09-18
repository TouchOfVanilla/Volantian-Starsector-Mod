package data.weapons.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import data.scripts.util.MagicLensFlare;
import data.scripts.util.MagicRender;
import org.dark.shaders.distortion.DistortionShader;
import org.dark.shaders.distortion.RippleDistortion;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;

public class VRI_weapon_laevaten implements BeamEffectPlugin{

    private IntervalUtil fireInterval = new IntervalUtil(0.1f, 0.1f);
    private final float pierceChance = 0.1f;


    public void advance(float amount, CombatEngineAPI engine, BeamAPI beam) {
        CombatEntityAPI target = beam.getDamageTarget();
        if (target instanceof ShipAPI && beam.getBrightness() >= 1f) {
            Vector2f point = beam.getRayEndPrevFrame();
            float emp = beam.getDamage().getFluxComponent() * 0.5f;
            float dam = beam.getDamage().getDamage() * 0.25f;

            float dur = beam.getDamage().getDpsDuration();
            fireInterval.advance(dur);

            boolean piercedShield = (float) Math.random() < pierceChance;

            if (fireInterval.intervalElapsed()) {

                if(piercedShield) {
                    engine.spawnEmpArcPierceShields(
                            beam.getSource(),
                            point,
                            target,
                            target,
                            DamageType.FRAGMENTATION,
                            dam, // damage
                            emp, // emp
                            100000f, // max range
                            "tachyon_lance_emp_impact",
                            beam.getWidth() + 5f,
                            beam.getFringeColor(),
                            beam.getCoreColor()
                    );
                }
            }
        }
    }
}
