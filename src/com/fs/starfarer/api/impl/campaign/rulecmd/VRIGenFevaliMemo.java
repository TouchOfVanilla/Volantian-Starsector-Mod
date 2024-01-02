package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.IntelDataAPI;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.comm.IntelInfoPlugin;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.ui.TooltipMakerAPI;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class VRIGenFevaliMemo extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        SectorEntityToken entity = dialog.getInteractionTarget();
        SectorEntityToken target = Global.getSector().getStarSystem("Fevali").getHyperspaceAnchor();
        BreadcrumbIntel intel = new BreadcrumbIntel(entity, target);
        TextPanelAPI text = dialog.getTextPanel();
        text.addParagraph("While exploring the " + entity.getName() + ", your crew found some stored navigational data indicating this ship was headed for the Fevali star system.", Color.WHITE);
        intel.setTitle("Point of Interest- Fevali Star System");
        intel.setText("While exploring the " + entity.getName() + ", your crew found some stored navigational data indicating this ship was headed for the Fevali star system.");
        Global.getSector().getIntelManager().addIntel(intel, false, text);
        return false;
    }
}
