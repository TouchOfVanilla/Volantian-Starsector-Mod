package com.fs.starfarer.api.impl.campaign.intel;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.intel.deciv.DecivIntel;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;
import exerelin.utilities.NexUtilsFaction;

import java.awt.*;
import java.util.Set;


public class EncasedIntel extends BaseIntelPlugin {

    protected SectorEntityToken primary;
    protected String name;
    protected FactionAPI faction;
    protected boolean destroyed;
    protected boolean warning;


    public EncasedIntel(MarketAPI market, SectorEntityToken primary, boolean destroyed, boolean warning) {
        //this.market = market;
        this.primary = primary;
        this.destroyed = destroyed;
        this.warning = warning;
        this.faction = market.getFaction();
        name = market.getName();
    }

    protected void addBulletPoints(TooltipMakerAPI info, ListInfoMode mode) {

        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        float pad = 3f;
        float opad = 10f;

        float initPad = pad;
        if (mode == ListInfoMode.IN_DESC) initPad = opad;

        Color tc = getBulletColorForMode(mode);

        bullet(info);
        boolean isUpdate = getListInfoParam() != null;

//			info.addPara("Danger level: " + danger, initPad, tc, dangerColor, danger);
//			initPad = 0f;

        unindent(info);
    }


    @Override
    public void createIntelInfo(TooltipMakerAPI info, ListInfoMode mode) {
        Color c = getTitleColor(mode);
        info.addPara(getName(), c, 0f);
        addBulletPoints(info, mode);
    }

    @Override
    public void createSmallDescription(TooltipMakerAPI info, float width, float height) {
        Color h = Misc.getHighlightColor();
        Color g = Misc.getGrayColor();
        Color tc = Misc.getTextColor();
        float pad = 3f;
        float opad = 10f;

        String YouOrFaction = faction.getDisplayNameWithArticle();

        if (NexUtilsFaction.getFactionMarkets(Global.getSector().getPlayerFaction().getId()).isEmpty()) {
            YouOrFaction = "you";
        }

        info.addImage(getFactionForUIColors().getLogo(), width, 128, opad);
            info.addPara(name + ", a colony formerly belonging to " +
                            YouOrFaction + ", has been encased in an orbital shell of mines, rendering it completely inaccessible. " +
                            "Central authority has collapsed completely and irreversibly, with the remaining " +
                            "population scrambling to survive. " +
                            "All planetary assets are considered lost.",
                    opad, faction.getBaseUIColor(), YouOrFaction);
        addBulletPoints(info, ListInfoMode.IN_DESC);
    }

    @Override
    public String getIcon() {
        return Global.getSettings().getSpriteName("intel", "deciv");
    }

    @Override
    public Set<String> getIntelTags(SectorMapAPI map) {
        Set<String> tags = super.getIntelTags(map);
        tags.add(Tags.INTEL_DECIVILIZED);
        return tags;
    }

    public String getSortString() {
        return "Encased " + name;
    }

    public String getName() {

        return name + " - Encased";
    }

    @Override
    public FactionAPI getFactionForUIColors() {
        return faction;
    }

    public String getSmallDescriptionTitle() {
        return getName();
    }

    @Override
    public SectorEntityToken getMapLocation(SectorMapAPI map) {
        return primary;
    }

    @Override
    public boolean shouldRemoveIntel() {
        return false;
    }

    @Override
    public String getCommMessageSound() {
        return super.getCommMessageSound();
    }
}
