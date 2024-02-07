
//Author: lord_dalton with permission to be used by TouchOfVanilla for the mod VRI
package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.combat.listeners.DamageTakenModifier;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.loading.DamagingExplosionSpec;
import com.fs.starfarer.api.util.IntervalUtil;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicUI;

import java.awt.*;

public class VRI_shipSystem_phaseblink extends BaseShipSystemScript {
    public static Color JITTER = new Color(242, 85, 85, 160);
    private boolean runOnce = false;
    private final static float activediss = 0.2f;
    private final static float activetimeflow = 3f;
    //controls how much meter is lost when first diving
    private final static float initialdivecharge = 3f;

    protected void maintainStatus(ShipAPI playerShip, State state, float effectLevel) {

        ShipSystemAPI cloak = playerShip.getPhaseCloak();
        if (cloak == null) {
            cloak = playerShip.getSystem();
        }
        if (cloak == null) {
            return;
        }
    }

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

        if (player) {
            maintainStatus(ship, state, effectLevel);
        }

        if (Global.getCombatEngine().isPaused()) {
            return;
        }

        if (state == State.OUT || state == State.COOLDOWN || state == State.IDLE) {
            unapply(stats, id);
            return;
        }

        phaseblinklistener blistener = null;

        if(!ship.hasListenerOfClass(phaseblinklistener.class)) {

            blistener = new phaseblinklistener(ship);

            ship.addListener(blistener);
        }else{
            blistener = ship.getListeners(phaseblinklistener.class).get(0);
        }


        if (ship.getSystem().isOn()) {
            if(!runOnce){
                blistener.currcharge-=initialdivecharge;
                runOnce=true;
            }
            ship.setPhased(true);
            stats.getTimeMult().modifyMult(id,activetimeflow);
            stats.getFluxDissipation().modifyMult(id, activediss);
            ship.setJitterUnder(ship, JITTER, effectLevel, 7, 10f, 15f);
            ship.setExtraAlphaMult(MathUtils.clamp(1f - effectLevel,0.1f,1f));
            ship.setApplyExtraAlphaToEngines(true);
        } else {
            if(runOnce){
                ship.getSystem().forceState(ShipSystemAPI.SystemState.OUT,0f);
                ship.getSystem().deactivate();
                ship.getFluxTracker().beginOverloadWithTotalBaseDuration(0.01f);
            }
            ship.setPhased(false);
            unapply(stats, id);
            stats.getFluxDissipation().unmodifyMult(id);
            stats.getTimeMult().unmodifyMult(id);
            ship.setPhased(false);
            ship.setExtraAlphaMult(1f);
        }
    }

    public void unapply(MutableShipStatsAPI stats, String id) {
        runOnce = false;
        ShipAPI ship = null;
        boolean player = false;

        if (stats.getEntity() instanceof ShipAPI) {
            ship = (ShipAPI) stats.getEntity();
            player = ship == Global.getCombatEngine().getPlayerShip();
        } else {
            return;
        }

        phaseblinklistener blistener = null;

        if(!ship.hasListenerOfClass(phaseblinklistener.class)) {

            blistener = new phaseblinklistener(ship);

            ship.addListener(blistener);
        }else{
            blistener = ship.getListeners(phaseblinklistener.class).get(0);
        }

        stats.getFluxDissipation().unmodifyMult(id);
        stats.getTimeMult().unmodifyMult(id);
        

        ship.setPhased(false);
        ship.setExtraAlphaMult(1f);
    }

    public StatusData getStatusData(int index, State state, float effectLevel) {
        return null;
    }

    public static class phaseblinklistener implements AdvanceableListener, DamageTakenModifier {

        IntervalUtil interval = new IntervalUtil(0.1f, 0.1f);
        public final ShipAPI ship;
        public final static float maxcharge = 100f;
        public float currcharge = 100f;
        public final static float dtcratio = 0.001f;

        private final static float passivegainrate = 0.2f;
        private final static float activelossrate = 0.5f;

        public phaseblinklistener(ShipAPI ship) {
            this.ship = ship;
        }

        public float getcharge(){
            return currcharge/maxcharge;
        }

        @Override
        public void advance(float amount) {
            interval.advance(amount);

            MagicUI.drawSystemBar(ship, new Color(0.5f, 0.5f, 1f, 1f), getcharge(), 0);

            if (interval.intervalElapsed()){
                if(ship.getSystem().isActive()){
                    currcharge-=activelossrate;
                }else{
                    currcharge+=passivegainrate;
                }
            }

            if(currcharge>100f){
                currcharge = 100f;
            } else if (currcharge<0f){
                currcharge = 0f;
                ship.getFluxTracker().beginOverloadWithTotalBaseDuration(0.01f);
                ship.getSystem().forceState(ShipSystemAPI.SystemState.OUT,0f);
                ship.getSystem().deactivate();
            }
        }

        @Override
        public String modifyDamageTaken(Object param, CombatEntityAPI target, DamageAPI damage, Vector2f point, boolean shieldHit) {
            if(shieldHit){
                currcharge += dtcratio * damage.getDamage();
            }
            return null;
        }
    }
}
