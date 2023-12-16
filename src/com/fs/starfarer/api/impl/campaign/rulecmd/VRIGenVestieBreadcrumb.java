package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.impl.campaign.intel.misc.BreadcrumbIntel;
import com.fs.starfarer.api.util.Misc;

import java.awt.*;
import java.util.List;
import java.util.Map;

public class VRIGenVestieBreadcrumb extends BaseCommandPlugin{
    @Override
    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {
        SectorEntityToken entity = dialog.getInteractionTarget();
        SectorEntityToken target = Global.getSector().getEntityById("warning_beacon");
        for (SectorEntityToken beacon : Global.getSector().getHyperspace().getAllEntities()){
            if (beacon.getFaction().getId().equals("vestige")){
                target = beacon;
            }
        }
        BreadcrumbIntel intel = new BreadcrumbIntel(entity, target);
        TextPanelAPI text = dialog.getTextPanel();
        text.addParagraph("While exploring the " + entity.getName() + ", your crew found navigational data regarding a notable location in seemingly empty space.", Color.WHITE);
        intel.setTitle("Point of Interest- Empty Space");
        intel.setText("While exploring the " + entity.getName() + ", your crew found some stored navigational data regarding a notable location in seemingly empty space.");
        Global.getSector().getIntelManager().addIntel(intel, false, text);
        return false;
    }
}
