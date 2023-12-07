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

        FleetMemberAPI member = Global.getFactory().createFleetMember(FleetMemberType.SHIP, Global.getSettings().getVariant("volantian_chromatic_vri_Standard")); //Creates a ship for the game to use, defines it as 'member'
        member.setCaptain(Global.getSector().getImportantPeople().getPerson("avery")); //sets the captain of this ship to the person in question
        Global.getSector().getPlayerFleet().getFleetData().addFleetMember(member); //adds this ship to the player fleet
        Global.getSector().getPlayerFleet().getFleetData().addOfficer(Global.getSector().getImportantPeople().getPerson("avery")); //adds the person as an officer in the player fleet
        addShipGainText(member, dialog.getTextPanel()); //triggers the text below
        PersonAPI avery = dialog.getInteractionTarget().getActivePerson(); //defines the person the player is interacting with as a variable
        MarketAPI market = dialog.getInteractionTarget().getMarket(); //defines the person's market as a variable
        market.getCommDirectory().removePerson(avery); //removes the person from the comm directory
        market.removePerson(avery); //removes the person from the market
        return true;
    }

    public static void addShipGainText(FleetMemberAPI member, TextPanelAPI text) {
        text.setFontSmallInsignia();
        text.addParagraph("Gained " + member.getVariant().getFullDesignationWithHullNameForShip(), Misc.getPositiveHighlightColor());
        text.highlightInLastPara(Misc.getHighlightColor(), member.getVariant().getFullDesignationWithHullNameForShip());
        text.setFontInsignia();
    }
}