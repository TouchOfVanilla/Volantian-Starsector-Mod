package com.fs.starfarer.api.impl.campaign.rulecmd;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.InteractionDialogAPI;
import com.fs.starfarer.api.campaign.TextPanelAPI;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.fleet.FleetMemberType;
import com.fs.starfarer.api.util.Misc;

import java.util.List;
import java.util.Map;
//This is for avery only, do not use this without swapping out id's first
public class VRIAddSuperSpecialShipAndOfficer extends BaseCommandPlugin {

    public boolean execute(String ruleId, InteractionDialogAPI dialog, List<Misc.Token> params, Map<String, MemoryAPI> memoryMap) {

        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().getVariant("volantian_chromatic_vri_strike"));
        member.setCaptain(Global.getSector().getImportantPeople().getPerson("avery")); //optionally, also add your officer to the fleetMember
        Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member);
        addShipGainText(member, dialog.getTextPanel());
        PersonAPI avery = dialog.getInteractionTarget().getActivePerson();
        MarketAPI market = dialog.getInteractionTarget().getMarket();
        market.getCommDirectory().removePerson(avery);
        market.removePerson(avery);
        return true;
    }

    public static void addShipGainText(FleetMemberAPI member, TextPanelAPI text) {
        text.setFontSmallInsignia();
        text.addParagraph("Gained " + member.getVariant().getFullDesignationWithHullNameForShip(), Misc.getPositiveHighlightColor());
        text.highlightInLastPara(Misc.getHighlightColor(), member.getVariant().getFullDesignationWithHullNameForShip());
        text.setFontInsignia();
    }
}