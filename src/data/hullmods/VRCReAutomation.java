package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CargoStackAPI;
import com.fs.starfarer.api.combat.BaseHullMod;
import com.fs.starfarer.api.combat.MutableShipStatsAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.util.Misc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class VRCReAutomation extends BaseHullMod {

    public WeaponSlotAPI SLOT;
    public WeaponSpecAPI WEAPON;

    @Override
    public void applyEffectsBeforeShipCreation(ShipAPI.HullSize hullSize, MutableShipStatsAPI stats, String id) {

        if (stats.getEntity() == null) return;

        Iterator weaponiter = ((ShipAPI)stats.getEntity()).getHullSpec().getAllWeaponSlotsCopy().iterator();
        while (weaponiter.hasNext()){
            WeaponSlotAPI weaponslot = (WeaponSlotAPI) weaponiter.next();
            if (weaponslot.getWeaponType().equals(WeaponAPI.WeaponType.DECORATIVE)){
                SLOT = weaponslot;
            }
        }

        String HULLID = stats.getVariant().getHullSpec().getHullId();

        if (decoMap.containsKey(HULLID)){
            WEAPON = Global.getSettings().getWeaponSpec(decoMap.get(HULLID));
        }
        stats.getVariant().addWeapon(SLOT.getId(), WEAPON.getWeaponId());
    }

    @Override
    public void applyEffectsAfterShipCreation(ShipAPI ship, String id) {

        if(ship.getOriginalOwner()<0){
            //undo fix for weapons put in cargo
            if(
                    Global.getSector()!=null &&
                            Global.getSector().getPlayerFleet()!=null &&
                            Global.getSector().getPlayerFleet().getCargo()!=null &&
                            Global.getSector().getPlayerFleet().getCargo().getStacksCopy()!=null &&
                            !Global.getSector().getPlayerFleet().getCargo().getStacksCopy().isEmpty()
            ){
                for (CargoStackAPI s : Global.getSector().getPlayerFleet().getCargo().getStacksCopy()){
                    if(
                            s.isWeaponStack()
                                    && s.getWeaponSpecIfWeapon().getWeaponId().endsWith("_corebridge")
                    ){
                        Global.getSector().getPlayerFleet().getCargo().removeStack(s);
                    }
                }
            }
        }
    }


    public boolean isApplicableToShip(ShipAPI ship) {
        if (ship == null) return false;


        if (ship.getVariant().hasHullMod("automated") && ship.getVariant().hasHullMod("volremconversion") && !ship.getVariant().hasHullMod("vrc_reauto")){
            return false;
        }

        if (Misc.isUnremovable(ship.getCaptain())) return false;

        if (!ship.getCaptain().isDefault()){
            return false;
        }

        if (Global.getSector().getPlayerStats().getSkillLevel(Skills.AUTOMATED_SHIPS) < 1) return false;

        if (ship.getVariant().hasHullMod("volremconversion")) {
            return true;
        }

        return false;
    }
    public String getUnapplicableReason(ShipAPI ship) {

            if (Misc.isUnremovable(ship.getCaptain())) return "This ship's captain cannot be removed.";
            if (Global.getSector().getPlayerStats().getSkillLevel(Skills.AUTOMATED_SHIPS) < 1)
                return "You do not have the necessary knowledge and expertise in automated ships.";
            if (!ship.getCaptain().isDefault()) return "This ship cannot be automated while a captain is present.";
        if (!ship.getVariant().hasHullMod("vrc_reauto")) {
            if (ship.getVariant().hasHullMod("volremconversion") && ship.getVariant().hasHullMod("automated")) {
                return "This hull is already automated, dumbass. You didn't expect me to plan for this, huh, fucker?";
            }
        }
        return "this hull is not a Volantian remnant conversion.";
    }

    public String getDescriptionParam(int index, ShipAPI.HullSize hullSize) {
        if (index == 0) {
            return "All";
        }
        return null;
    }

    public static final Map<String, String> decoMap = new HashMap<String, String>();
    static {
        decoMap.put("volantian_radiant_vri", "vol_radiant_vri_corebridge");
        decoMap.put("volantian_lumen_vri", "vol_lumen_vri_corebridge");
        decoMap.put("volantian_glimmer_vri", "vol_glimmer_vri_corebridge");
        decoMap.put("volantian_pyralis_vri", "vol_pyralis_vri_corebridge");
    }
}
