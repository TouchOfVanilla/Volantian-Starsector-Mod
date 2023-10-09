package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.AICoreAdminPlugin;
import com.fs.starfarer.api.characters.FullName;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.impl.campaign.ids.Ranks;

public class VRICoreAdminPlugin extends AICoreAdminPluginImpl implements AICoreAdminPlugin {

    public VRICoreAdminPlugin(){
    }

    public PersonAPI createPerson(String aiCoreId, String factionId, long seed) {
        PersonAPI person = Global.getFactory().createPerson();
        person.setFaction(factionId);
        person.setAICoreId(aiCoreId);
        person.setName(new FullName("Azorian Core", "", FullName.Gender.ANY));
        person.setPortraitSprite("graphics/portraits/volcore.png");
        person.setRankId((String)null);
        person.setPostId(Ranks.POST_ADMINISTRATOR);
        person.getStats().setSkillLevel("volantian_contingency_planning", 2.0F);
        person.getStats().setSkillLevel("hypercognition", 1.0F);
        return person;
    }

}
