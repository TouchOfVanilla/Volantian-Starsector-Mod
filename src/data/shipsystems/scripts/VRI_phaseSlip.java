package data.shipsystems.scripts;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.combat.listeners.AdvanceableListener;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.impl.campaign.ids.Stats;
import com.fs.starfarer.api.impl.combat.BaseShipSystemScript;
import com.fs.starfarer.api.util.IntervalUtil;
import com.fs.starfarer.api.util.JitterUtil;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.renderers.JitterRenderer;
import org.lazywizard.lazylib.LazyLib;
import org.lwjgl.util.vector.Vector2f;
import org.magiclib.util.MagicRender;

import java.awt.*;

public class VRI_phaseSlip extends BaseShipSystemScript {

    boolean CreatedCopy = false;
    boolean CopyActive = false;
    public static Color JITTER = new Color(70, 70, 70, 160);

    Vector2f initloc;
    boolean fading;
    @Override
    public void apply(MutableShipStatsAPI stats, String id, State state, float effectLevel) {
        fading = false;
            initloc = stats.getEntity().getLocation();

            if (state == State.OUT){
                stats.getEntity().setCollisionClass(CollisionClass.SHIP);
            } else {
                stats.getEntity().setCollisionClass(CollisionClass.NONE);
            }

        if (!CreatedCopy){
            ShipAPI copy = spawnShipOrWingDirectly(stats.getVariant(), FleetMemberType.SHIP, stats.getFleetMember().getOwner(), stats.getFleetMember().getRepairTracker().getCR(), initloc, stats.getEntity().getFacing(),true);
            copy.addListener(new CopyListener(copy, fading, stats.getEntity()));
            copy.setCurrentCR((stats.getFleetMember()).getRepairTracker().getCR());
            copy.getFleetMember().getRepairTracker().setCR((stats.getFleetMember()).getRepairTracker().getCR());
            copy.getFleetMember().setShipName(stats.getFleetMember().getShipName());
            copy.setCaptain(stats.getFleetMember().getCaptain());
            CreatedCopy = true;
            CopyActive = true;
        }

    }

    @Override
    public void unapply(MutableShipStatsAPI stats, String id) {
        CreatedCopy = false;
    }

    @Override
    public boolean isUsable(ShipSystemAPI system, ShipAPI ship) {
        if (ship.hasListenerOfClass(CopyListener.class)){
            return false;
        }
        return true;
    }
    public static class CopyListener implements AdvanceableListener{
        public final CombatEntityAPI copy;
        public boolean fading;
        public CombatEntityAPI original;
        public final IntervalUtil PhaseOutTimer = new IntervalUtil(20f, 20f);
        public final IntervalUtil FadeTimer = new IntervalUtil( 2.f,2.5f);
        public CopyListener(CombatEntityAPI copy, boolean fading, CombatEntityAPI original){
            this.copy = copy;
            this.fading = fading;
            this.original = original;
        }
        @Override
        public void advance(float amount) {
            CombatEngineAPI engine = Global.getCombatEngine();
            PhaseOutTimer.advance(0.1f);
            float intensity = Misc.interpolate(0.2f,1f, PhaseOutTimer.getElapsed()/PhaseOutTimer.getIntervalDuration());
            float fade = Misc.interpolate(1f,0, FadeTimer.getElapsed()/FadeTimer.getIntervalDuration());
            ((ShipAPI) copy).setJitter(copy, JITTER, intensity, 5, 20);
            ((ShipAPI) copy).setAlphaMult(fade);
            ((ShipAPI) copy).getFluxTracker().setCurrFlux(((ShipAPI)original).getCurrFlux());
            ((ShipAPI) copy).getFluxTracker().setHardFlux(((ShipAPI)original).getHardFluxLevel());
                if (PhaseOutTimer.intervalElapsed()){
                    fading = true;
                    ((ShipAPI) copy).setHoldFire(true);
                    Global.getSoundPlayer().playSound("system_phase_skimmer", 1,1, copy.getLocation(), copy.getVelocity());
                }
                if (fading){
                    FadeTimer.advance(0.1f);
                    ((ShipAPI) copy).setJitter(copy, JITTER, 1, 7, 30);
                }

            if (FadeTimer.intervalElapsed()){
                engine.removeEntity(copy);
                engine.getFleetManager(copy.getOwner()).removeDeployed(((ShipAPI)copy), false);
                ((ShipAPI)copy).removeListener(this);
            }
        }
    }

   /// public FleetMemberAPI getShipCopy(MutableShipStatsAPI stats, CombatEngineAPI engine){
    ///    FleetMemberAPI copy = Global.getFactory().createFleetMember(FleetMemberType.SHIP, stats.getVariant());
    ///    copy.setShipName(stats.getFleetMember().getShipName());
    ///    copy.setCaptain(stats.getFleetMember().getCaptain());
  ///      copy.getRepairTracker().setCR(stats.getFleetMember().getRepairTracker().getCR());
  ///      return copy;
 ///   }

    //thank u ruddy
    public static ShipAPI spawnShipOrWingDirectly(ShipVariantAPI variant, FleetMemberType type, int owner, float combatReadiness, Vector2f location, float facing, boolean noDP) {
        // Create the ship, set its stats and spawn it on the combat map
        FleetMemberAPI member = Global.getFactory().createFleetMember(type, variant);
        if (noDP && type == FleetMemberType.SHIP) {member.getStats().getDynamic().getMod(Stats.DEPLOYMENT_POINTS_MOD).modifyMult("SSCSpawnShipOrWingDirectly", 0f);}
        member.setOwner(owner);
        member.getCrewComposition().addCrew(member.getNeededCrew());
        ShipAPI ship = Global.getCombatEngine().getFleetManager(owner).spawnFleetMember(member, location, facing, 0f);
        ship.setCRAtDeployment(combatReadiness);
        ship.setCurrentCR(combatReadiness);
        ship.setOwner(owner);
        ship.getShipAI().forceCircumstanceEvaluation();
        return ship;
    }
}
