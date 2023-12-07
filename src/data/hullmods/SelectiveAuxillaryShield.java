package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.combat.*;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.loading.specs.HullVariantSpec;
import org.lazywizard.lazylib.MathUtils;
import org.lwjgl.util.vector.Vector2f;

import java.awt.*;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.Iterator;
import java.util.List;

public class SelectiveAuxillaryShield extends BaseHullMod {

    //Lyravega is amazing and awesome, tsym

    private static Class<?> zfieldClass;
    private static MethodHandle zsetAccessible;
    private static MethodHandle zset;

    static {
        try {
            zfieldClass = Class.forName("java.lang.reflect.Field", false, Class.class.getClassLoader());
            zsetAccessible = MethodHandles.lookup().findVirtual(zfieldClass, "setAccessible", MethodType.methodType(void.class, boolean.class));
            zset = MethodHandles.lookup().findVirtual(zfieldClass, "set", MethodType.methodType(void.class, Object.class, Object.class));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException e) {
            // TODO: handle exception
        }
    }

    public static void hurr(FleetMemberAPI member) {
        if (member.getStatus().getNumStatuses() != member.getVariant().getStationModules().size() + 1) {    // check if status needs to be refreshed; status array includes parent's, so check with module amount + 1
            try {
                Object statusField = member.getClass().getDeclaredField("status");
                zsetAccessible.invoke(statusField, true);
                zset.invoke(statusField, member, null);    // setting this field to null will cause the getter to repopulate
                member.getStatus();    // as the status field is null, this getter will repopulate the status array
            } catch (Throwable t) {

            }
        }
    }

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        HullVariantSpec variant = (HullVariantSpec) stats.getVariant();

        String newSlotId = "derp";
        String newNodeId = "derpNode";

        Vector2f newNodeLocation = new Vector2f((variant.getHullSpec().getShieldSpec().getCenterX()), variant.getHullSpec().getShieldSpec().getCenterY());

        if (!newSlotId.equals(variant.getHullSpec().getAllWeaponSlots().get(variant.getHullSpec().getAllWeaponSlotsCopy().size()-1).getId())) {
            variant.getHullSpec().addWeaponSlot(variant.getHullSpec().getAllWeaponSlots().iterator().next().clone());
            variant.getHullSpec().getAllWeaponSlots().get(variant.getHullSpec().getAllWeaponSlotsCopy().size()-1).setId(newSlotId);
            variant.getHullSpec().getWeaponSlot(newSlotId).setNode(newNodeId, newNodeLocation);
            variant.getHullSpec().getWeaponSlot(newSlotId).setWeaponType(WeaponAPI.WeaponType.STATION_MODULE);
            variant.getHullSpec().getWeaponSlot(newSlotId).setAngle(360);
        }

        ShipVariantAPI shieldcore = Global.getSettings().getVariant("volantian_shieldcore_Hull");
        stats.getVariant().setModuleVariant(newSlotId, shieldcore);

        if (stats.getFleetMember() != null) {
            hurr(stats.getFleetMember());
        }
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return "missiles";
        } else if (index == 1) {
            return "other projectiles";
        } else
            return index == 2 ? "50%" : null;
        }
}
