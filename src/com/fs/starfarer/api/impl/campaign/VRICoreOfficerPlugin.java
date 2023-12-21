package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.econ.CommoditySpecAPI;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Personalities;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;
import com.fs.starfarer.api.ui.Alignment;
import com.fs.starfarer.api.ui.TooltipMakerAPI;

import java.awt.*;
import java.util.Random;

import static com.fs.starfarer.api.impl.campaign.AICoreOfficerPluginImpl.*;

public class VRICoreOfficerPlugin extends BaseAICoreOfficerPluginImpl implements AICoreOfficerPlugin{

    public PersonAPI createPerson(String aiCoreId, String factionId, Random random) {
        if (random == null) {
            new Random();
        }
        PersonAPI person = Global.getFactory().createPerson();
        person.setFaction(factionId);
        person.setAICoreId(aiCoreId);
        boolean VolCore = "volantian_core".equals(aiCoreId);
        boolean VestCore = "vestige_core".equals(aiCoreId);
        CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(aiCoreId);
        person.getStats().setSkipRefresh(true);
        person.setName(new FullName(spec.getName(), "", FullName.Gender.ANY));
        int points = 0;
        float mult = 1.0F;
        if (VolCore) {
            person.getStats().setLevel(6);
            person.getStats().setSkillLevel("helmsmanship", 2.0F);
            person.getStats().setSkillLevel("target_analysis", 2.0F);
            person.getStats().setSkillLevel("impact_mitigation", 2.0F);
            person.getStats().setSkillLevel("field_modulation", 2.0F);
            person.getStats().setSkillLevel("gunnery_implants", 2.0F);
            person.getStats().setSkillLevel("combat_endurance", 2.0F);
            person.getStats().setSkillLevel("volantian_custodian_protocols", 1.0F);
            person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "volcore"));

            points = BETA_POINTS;
            mult = BETA_MULT;
        }
        if (VestCore) {
            person.getStats().setLevel(3);
            person.getStats().setSkillLevel("helmsmanship", 2.0F);
            person.getStats().setSkillLevel("target_analysis", 2.0F);
            person.getStats().setSkillLevel("impact_mitigation", 2.0F);
            person.getStats().setSkillLevel("vestigial_augmentation", 1.0F);
            person.setPortraitSprite(Global.getSettings().getSpriteName("characters", "vestcore"));

            points = ALPHA_POINTS;
            mult = ALPHA_MULT;
        }
        person.getMemoryWithoutUpdate().set("$autoPointsMult", mult);
        if (VolCore) {
            person.setPersonality(Personalities.CAUTIOUS);
        }
        if (!VolCore){
            person.setPersonality(Personalities.RECKLESS);
        }
        person.setRankId(Ranks.SPACE_CAPTAIN);
        person.setPostId((String)null);
        person.getStats().setSkipRefresh(false);
        return person;
    }
    @Override
    public void createPersonalitySection(PersonAPI person, TooltipMakerAPI tooltip) {
        float opad = 10.0F;
        Color text = person.getFaction().getBaseUIColor();
        Color bg = person.getFaction().getDarkUIColor();
        CommoditySpecAPI spec = Global.getSettings().getCommoditySpec(person.getAICoreId());
        if (spec.getId().equals("volantian_core")) {
            tooltip.addSectionHeading("Personality: steady", text, bg, Alignment.MID, 20.0F);
            tooltip.addPara("In combat, the " + spec.getName() + " is effective, yet self-restrained, unlike other cores. " + "This remarkable demonstration of self-preservation may prolong the operational lifespan of your droneships.", opad);
        }
        if (spec.getId().equals("vestige_core")){
            tooltip.addSectionHeading("Personality: Fearless", text, bg, Alignment.MID, 20.0F);
            tooltip.addPara("In combat, the " + spec.getName() + " is single-minded and determined. " + "In a human captain, its traits might be considered reckless. In a machine, they're terrifying.", opad);        }
    }
}

