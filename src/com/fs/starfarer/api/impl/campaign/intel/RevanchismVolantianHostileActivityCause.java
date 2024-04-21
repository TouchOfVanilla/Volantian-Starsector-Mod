package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.listeners.PlayerColonizationListener;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.events.*;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.MapParams;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import data.scripts.VRI_ModPlugin;
import exerelin.utilities.*;
import org.apache.log4j.Logger;

import java.awt.*;
import java.util.Iterator;

import static com.fs.starfarer.api.impl.campaign.intel.VolantianHostileActivityFactor.VolantianIncursionDefeated;

public class RevanchismVolantianHostileActivityCause extends BaseHostileActivityCause2 {

    private static Logger log = Global.getLogger(RevanchismVolantianHostileActivityCause.class);

    public static float VRI_ANGY_LY_DIST = 10f;

    public RevanchismVolantianHostileActivityCause(HostileActivityEventIntel intel) {
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
                        tooltip.addPara("Your colonial holdings in star systems near Volantian space are threatened by expansionist revanchism. " + "Hostile expeditionary fleets will patrol your systems as the Volantian polity prepares for " + "a grand offensive to assume control of the offending colonies.", opad, Misc.getHighlightColor(), new String[]{"a grand offensive to assume control of offending colonies."});
                        tooltip.addPara("The Volantian polity is unlikely to accept anything short of complete abandonment or the cession of your colonial holdings to the VRI.", opad, Misc.getHighlightColor(), new String[]{"complete abandonment or the cession of your colonial holdings to the VRI."});
                        tooltip.addPara("%s will likely result in large-scale conflict.", opad, Misc.getHighlightColor(), new String[]{"Enforcing your claim"});
                        tooltip.addPara("Colonies threatened by Volantian revanchism:", opad);

                        Iterator playermarketiter = NexUtilsFaction.getFactionMarkets(Factions.PLAYER).iterator();
                        while (playermarketiter.hasNext()) {
                            MarketAPI playermarket = (MarketAPI) playermarketiter.next();

                            StarSystemAPI system = playermarket.getStarSystem();


                            StarSystemAPI vrisystem = getClosestVRISystem(system);

                            float lydist = Misc.getDistanceLY(vrisystem.getHyperspaceAnchor(), system.getHyperspaceAnchor());
                            if (lydist <= VRI_ANGY_LY_DIST) {
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
        float progress = 10;
        // return total point contribution
        FactionAPI vri = Global.getSector().getFaction("vri");
        Iterator playermarketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
        while (playermarketiter.hasNext()) {
            MarketAPI playermarket = (MarketAPI) playermarketiter.next();
            if (playermarket.getFactionId().equals("player")){
            StarSystemAPI system = playermarket.getStarSystem();
            Iterator marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
            while (marketiter.hasNext()) {
                MarketAPI market = (MarketAPI) marketiter.next();
                if (market.getFaction().getId().equals("vri")) {
                    StarSystemAPI vrisystem = market.getStarSystem();
                    float lydist = Misc.getDistanceLY(vrisystem.getHyperspaceAnchor(), system.getHyperspaceAnchor());
                    if (lydist <= VRI_ANGY_LY_DIST) {
                        progress = progress + lydist;
                    }
                }
            }
            }
        }
        boolean IncursionDefeated = Global.getSector().getMemoryWithoutUpdate().getBoolean(VolantianIncursionDefeated);
        if (IncursionDefeated){
            progress = 0f;
        }
        return (int)progress;
    }


    public String getDesc() {
        return "Colonies near Volantian star systems";
    }

    public static StarSystemAPI getClosestVRISystem(StarSystemAPI system){
        StarSystemAPI closesys = NexUtils.getClosestMarket("vri").getStarSystem();
        float closedist = Misc.getDistanceLY(closesys.getHyperspaceAnchor(), system.getHyperspaceAnchor());
        Iterator marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
        while (marketiter.hasNext()){
            MarketAPI market = (MarketAPI)marketiter.next();
            if (market.getFaction().getId().equals("vri")){
                if (closedist > Misc.getDistanceLY(closesys.getHyperspaceAnchor(), market.getStarSystem().getHyperspaceAnchor()));
                    closesys = market.getStarSystem();
                    closedist = Misc.getDistanceLY(closesys.getHyperspaceAnchor(), system.getHyperspaceAnchor());
            }
        }
        return closesys;
    }

    public float getMagnitudeContribution(StarSystemAPI system) {
        //return a value less than 1
        return 0.7f;
    }

}
