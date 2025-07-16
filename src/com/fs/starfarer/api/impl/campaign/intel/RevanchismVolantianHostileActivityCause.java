package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.impl.campaign.intel.events.*;
import com.fs.starfarer.api.ui.MapParams;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.ui.UIPanelAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.campaign.AllianceManager;
import exerelin.utilities.*;
import org.apache.log4j.Logger;

import java.util.Iterator;

import static com.fs.starfarer.api.Global.getSector;
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
                tooltip.addPara("Your colonial holdings in star systems near Volantian space are threatened by expansionist revanchism. " + "Hostile expeditionary fleets will patrol your systems as the Volantian polity prepares for " + "a grand offensive to neutralize your colonies.", opad, Misc.getHighlightColor(), new String[]{"a grand offensive to neutralize your colonies."});
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
        float progress = 0;
        // return total point contribution
        FactionAPI vri = Global.getSector().getFaction("vri");
        SectorEntityToken ontosent = Global.getSector().getEntityById("vri_cryosleeper");
        Iterator playermarketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
        while (playermarketiter.hasNext()) {
            MarketAPI playermarket = (MarketAPI) playermarketiter.next();
            if (playermarket.getFactionId().equals("player")) {
                StarSystemAPI system = playermarket.getStarSystem();
                Iterator marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
                while (marketiter.hasNext()) {
                    MarketAPI market = (MarketAPI) marketiter.next();
                    if (market.getFaction().getId().equals("vri")) {
                        StarSystemAPI vrisystem = market.getStarSystem();
                        if (!isValidStar(vrisystem)) continue;
                        if (vrisystem.getHyperspaceAnchor() == null)continue;

                            float lydist = Misc.getDistanceLY(vrisystem.getHyperspaceAnchor(), system.getHyperspaceAnchor());
                        if (ontosent != null) {
                            if (ontosent.getMarket() != null) {
                                if (market == ontosent.getMarket()) {
                                    lydist = 100;
                                }
                            }
                        }
                        if (lydist <= VRI_ANGY_LY_DIST) {
                            progress = progress + lydist;
                        }
                    }
                }
            }
        }
        boolean IncursionDefeated = Global.getSector().getMemoryWithoutUpdate().getBoolean(VolantianIncursionDefeated);
        if (IncursionDefeated || isPlayerVRICommissioned() || doesPlayerShareAllianceWithVRI()) {
            progress = 0f;
        }
        return (int) progress;
    }
    public boolean isValidStar(StarSystemAPI star){
        if (star == Global.getSector().getStarSystem("Royce")) return true;
        if (star == Global.getSector().getStarSystem("Uelyst")) return true;
        if (star == Global.getSector().getStarSystem("Avery")) return true;
        return false;
    }


    public String getDesc() {
        return "Colonies near Volantian star systems";
    }

    public static StarSystemAPI getClosestVRISystem(StarSystemAPI system) {
        StarSystemAPI closesys = NexUtils.getClosestMarket("vri").getStarSystem();
        float closedist = Misc.getDistanceLY(closesys.getHyperspaceAnchor(), system.getHyperspaceAnchor());
        Iterator marketiter = Global.getSector().getEconomy().getMarketsCopy().iterator();
        while (marketiter.hasNext()) {
            MarketAPI market = (MarketAPI) marketiter.next();
            if (market.getFaction().getId().equals("vri")) {
                if (closedist > Misc.getDistanceLY(closesys.getHyperspaceAnchor(), market.getStarSystem().getHyperspaceAnchor()))
                    ;
                closesys = market.getStarSystem();
                closedist = Misc.getDistanceLY(closesys.getHyperspaceAnchor(), system.getHyperspaceAnchor());
            }
        }
        return closesys;
    }

    public float getMagnitudeContribution(StarSystemAPI system) {
        //return a value less than 1
        if (this.shouldShow()) {
            return 0.2f;
        }
        return 0;
    }

    public static boolean isPlayerVRICommissioned() {
//        if (Global.getSector().getIntelManager().hasIntelOfClass(FactionCommissionIntel.class)){
//           Iterator<IntelInfoPlugin> inteliter = Global.getSector().getIntelManager().getIntel().iterator();
//                IntelInfoPlugin intel = inteliter.next();
//                if (intel instanceof FactionCommissionIntel){
//                    if (((FactionCommissionIntel)intel).faction == Global.getSector().getFaction("vri")){
        //fuck me
        if (Misc.getCommissionFactionId() == "vri") {
            return true;
        }
        return false;
    }

    public static boolean doesPlayerShareAllianceWithVRI() {
        return AllianceManager.areFactionsAllied("player", "vri");
    }
}
