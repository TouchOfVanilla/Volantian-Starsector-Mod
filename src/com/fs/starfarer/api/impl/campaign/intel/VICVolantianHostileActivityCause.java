package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseFactorTooltip;
import com.fs.starfarer.api.impl.campaign.intel.events.BaseHostileActivityCause2;
import com.fs.starfarer.api.impl.campaign.intel.events.HostileActivityEventIntel;
import com.fs.starfarer.api.ui.MapParams;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.utilities.NexUtils;
import exerelin.utilities.NexUtilsFaction;
import org.apache.log4j.Logger;

import java.util.Iterator;

import static com.fs.starfarer.api.impl.campaign.intel.VolantianHostileActivityFactor.VolantianIncursionDefeated;

public class VICVolantianHostileActivityCause extends BaseHostileActivityCause2 {

    private static Logger log = Global.getLogger(VICVolantianHostileActivityCause.class);

    public static float VRI_ANGY_LY_DIST = 10f;

    public VICVolantianHostileActivityCause(HostileActivityEventIntel intel) {
        super(intel);
    }

    public TooltipMakerAPI.TooltipCreator getTooltip() {

        final StarSystemAPI system = NexUtilsFaction.getFactionMarkets("vri").get(0).getStarSystem();
        return new BaseFactorTooltip() {
            public void createTooltip(TooltipMakerAPI tooltip, boolean expanded, Object tooltipParam) {
                float opad = 10.0F;
                MapParams params = new MapParams();
                params.showSystem(system);
                float w = tooltip.getWidthSoFar();
                float h = (float) Math.round(w / 1.6F);
                params.positionToShowAllMarkersAndSystems(true, Math.min(w, h));
                UIPanelAPI map = tooltip.createSectorMap(w, h, params, system.getNameWithLowercaseType());
                tooltip.addPara("Your colonial holdings are caught in the crossfire of a corporate conflict between VolGen and the VIC. " + "Hostile Volantian fleets will patrol your systems as Volgen prepares to " + "strike against in-system Volkov assets.", opad, Misc.getHighlightColor(), new String[]{"strike against in-system Volkov assets."});
                tooltip.addPara("The VRI is largely uninterested in the well-being of your colonies and considers them acceptable collateral damage to reduce the VIC's presence in the sector.", opad, Misc.getHighlightColor(), new String[]{"reduce the VIC's presence."});
                tooltip.addPara("Your colonies will be existentially threatened if you do not remove their revitalization centers.", opad, Misc.getHighlightColor(), new String[]{"existentially threatened"});
                tooltip.addPara("Colonies threatened:", opad);

                Iterator playermarketiter = NexUtilsFaction.getFactionMarkets(Factions.PLAYER).iterator();
                while (playermarketiter.hasNext()) {
                    MarketAPI playermarket = (MarketAPI) playermarketiter.next();

                    if (playermarket.hasCondition("vic_revCenter")) {
                        tooltip.addPara(playermarket.getName() + " - " + system.getName(), opad, Misc.getHighlightColor(), new String[]{playermarket.getName()});
                    }
                }


                tooltip.addCustom(map, opad);
                log.info("tooltip created successfully");
            }
        };
    }

    public boolean shouldShow() {
        return this.getProgress() != 0;
    }

    public int getProgress() {
        float progress = 0;
        // return total point contribution
        FactionAPI vri = Global.getSector().getFaction("vri");
        Iterator playermarketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
        while (playermarketiter.hasNext()) {
            MarketAPI playermarket = (MarketAPI) playermarketiter.next();
            if (playermarket.getFactionId().equals("player")) {
                if (playermarket.hasIndustry("vic_revCenter")) {
                    progress++;
                }
            }
        }
        progress = progress*10;

        boolean IncursionDefeated = Global.getSector().getMemoryWithoutUpdate().getBoolean(VolantianIncursionDefeated);
        if (IncursionDefeated || !VolantianHostileActivityFactor.VICexists) {
            progress = progress *0f;
        }
        return (int) progress;
    }

    public String getDesc() {
        return "Colonies with VIC Revitalizaton Centers";
    }


    public float getMagnitudeContribution(StarSystemAPI system) {
        //return a value less than 1
        if (shouldShow()) {
            return 0.5f;
        }
        return 0;
    }

}
