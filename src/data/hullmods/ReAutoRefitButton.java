package data.hullmods;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.combat.WeaponAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.HullMods;
import com.fs.starfarer.api.impl.campaign.ids.Skills;
import com.fs.starfarer.api.input.InputEventAPI;
import com.fs.starfarer.api.loading.WeaponSlotAPI;
import com.fs.starfarer.api.loading.WeaponSpecAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import lunalib.lunaRefit.BaseRefitButton;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReAutoRefitButton extends BaseRefitButton {

    public WeaponSlotAPI SLOT;
    public WeaponSpecAPI WEAPON;
    @Override
    public String getButtonName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "VRC Re-Automation";
    }

    @Override
    public String getIconName(FleetMemberAPI member, ShipVariantAPI variant) {
        return "graphics/hullmods/integrated_targeting_unit.png";
    }

    @Override
    public int getOrder(FleetMemberAPI member, ShipVariantAPI variant) {
        return 9999;
    }

    @Override
    public boolean hasTooltip(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        return true;
    }

    public void addTooltip(TooltipMakerAPI tooltip, FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {

        if (variant.hasHullMod(HullMods.AUTOMATED)) tooltip.addPara("Converts this hull in to a crewed ship. ", 0f);
        else tooltip.addPara("Converts this hull in to an automated ship.", 0f);

        if (!isClickable(member, variant, market)) {
            tooltip.addSpacer(10f);
            if (Global.getSector().getPlayerStats().getSkillLevel(Skills.AUTOMATED_SHIPS) > 1) {
                tooltip.addPara("You lack the necessary skills in automated ships.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
                return;
            }
            if (!member.getCaptain().isDefault()) {
                tooltip.addPara("Can not converted while an officer is assigned to the ship.", 0f, Misc.getNegativeHighlightColor(), Misc.getNegativeHighlightColor());
            }
        }
    }

    @Override
    public void onClick(FleetMemberAPI member, ShipVariantAPI variant, InputEventAPI event, MarketAPI market) {
        super.onClick(member, variant, event, market);

        Iterator weaponiter = (member.getHullSpec().getAllWeaponSlotsCopy().iterator());
        while (weaponiter.hasNext()){
            WeaponSlotAPI weaponslot = (WeaponSlotAPI) weaponiter.next();
            if (weaponslot.getWeaponType().equals(WeaponAPI.WeaponType.DECORATIVE)){
                SLOT = weaponslot;
            }
        }
        String HULLID = variant.getHullSpec().getHullId();

        if (decoMap.containsKey(HULLID)){
            WEAPON = Global.getSettings().getWeaponSpec(decoMap.get(HULLID));
        }

        if (!variant.hasHullMod(HullMods.AUTOMATED)) {
            //Always use the provided ShipVariantAPI instead of calling member.getVariant(), as modifying the variant assigned to the member causes issues while in the refit screen.
            variant.addPermaMod(HullMods.AUTOMATED);
            variant.addWeapon(SLOT.getId(), WEAPON.getWeaponId());
            //Has to be called after modifying the variant
            refreshVariant();
            //We want the button to dissapear after installing the hullmod, so refresh the list to make shouldShow() run again.
            refreshButtonList();
            return;
        }
        if (variant.hasHullMod(HullMods.AUTOMATED)) {
            //Always use the provided ShipVariantAPI instead of calling member.getVariant(), as modifying the variant assigned to the member causes issues while in the refit screen.
            variant.removePermaMod(HullMods.AUTOMATED);
            variant.clearSlot(SLOT.getId());
            //Has to be called after modifying the variant
            refreshVariant();
            //We want the button to dissapear after installing the hullmod, so refresh the list to make shouldShow() run again.
            refreshButtonList();
        }
    }
    @Override
    public boolean isClickable(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        if (!member.getCaptain().isDefault()) return false;
        if (Global.getSector().getPlayerStats().getSkillLevel(Skills.AUTOMATED_SHIPS) > 1) return false;
        return true;
    }
    @Override
    public boolean shouldShow(FleetMemberAPI member, ShipVariantAPI variant, MarketAPI market) {
        if (member.getVariant().hasHullMod("volremconversion")) return true;
        return false;
    }
    public static final Map<String, String> decoMap = new HashMap<String, String>();
    static {
        decoMap.put("volantian_radiant_vri", "vol_radiant_vri_corebridge");
        decoMap.put("volantian_lumen_vri", "vol_lumen_vri_corebridge");
        decoMap.put("volantian_glimmer_vri", "vol_glimmer_vri_corebridge");
        decoMap.put("volantian_pyralis_vri", "vol_pyralis_vri_corebridge");
        decoMap.put("volantian_fulgent_vri", "vol_fulgent_vri_corebridge");
        decoMap.put("volantian_chromatic_vri", "vol_chromatic_vri_corebridge");
        decoMap.put("volantian_scintilla_vri", "vol_scintilla_vri_corebridge");
        decoMap.put("volantian_prismatic_vri", "vol_prismatic_vri_corebridge");
    }
}
