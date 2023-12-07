package com.fs.starfarer.api.impl.campaign.procgen.themes;

import com.fs.starfarer.api.campaign.AICoreOfficerPlugin;
import com.fs.starfarer.api.campaign.CampaignFleetAPI;
import com.fs.starfarer.api.campaign.FleetInflater;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.characters.PersonAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.BaseGenericPlugin;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Tags;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed;
import com.fs.starfarer.api.loading.VariantSource;
import com.fs.starfarer.api.util.Misc;
import data.world.systems.Fevali;

import java.util.Iterator;
import java.util.Random;

public class BluesteelDefenderPluginImpl extends BaseGenericPlugin implements SalvageGenFromSeed.SalvageDefenderModificationPlugin {
    public BluesteelDefenderPluginImpl() {
    }

    public float getStrength(SalvageGenFromSeed.SDMParams p, float strength, Random random, boolean withOverride) {
        return strength;
    }

    public float getMinSize(SalvageGenFromSeed.SDMParams p, float minSize, Random random, boolean withOverride) {
        return minSize;
    }

    public float getMaxSize(SalvageGenFromSeed.SDMParams p, float maxSize, Random random, boolean withOverride) {
        return maxSize;
    }

    public float getProbability(SalvageGenFromSeed.SDMParams p, float probability, Random random, boolean withOverride) {
        return probability;
    }

    public void reportDefeated(SalvageGenFromSeed.SDMParams p, SectorEntityToken entity, CampaignFleetAPI fleet) {
    }

    public void modifyFleet(SalvageGenFromSeed.SDMParams p, CampaignFleetAPI fleet, Random random, boolean withOverride) {
        fleet.setNoFactionInName(true);
        fleet.setName("130th Legion of the Domain Sixth Battlegroup");
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin("alpha_core");
        fleet.getFleetData().clear();
        fleet.getFleetData().setShipNameRandom(random);
        FleetMemberAPI member = fleet.getFleetData().addFleetMember("volantian_ventalse_vi_Standard");
        member.setId("xivtf_" + random.nextLong());
        PersonAPI person = plugin.createPerson("alpha_core", fleet.getFaction().getId(), random);
        person.getStats().setSkipRefresh(true);
        person.getStats().setSkillLevel("carrier_group", 1.0F);
        person.getStats().setSkillLevel("fighter_uplink", 1.0F);
        person.getStats().setSkipRefresh(false);
        member.setCaptain(person);
        ShipVariantAPI v = member.getVariant().clone();
        v.setSource(VariantSource.REFIT);
        v.addTag("no_autofit");
        v.addTag("no_auto_penalty");
        member.setVariant(v, false, true);
        fleet.setCommander(person);
        addAutomated(fleet, "volantian_legion_vi_Standard", (String)null, "alpha_core", random);
        addAutomated(fleet, "volantian_dominator_vi_Standard", (String)null, "beta_core", random);
        addAutomated(fleet, "volantian_dominator_vi_Standard", (String)null, "beta_core", random);
        addAutomated(fleet, "volantian_eradicator_vi_Standard", (String)null, "beta_core", random);
        addAutomated(fleet, "volantian_heron_vi_Standard", (String)null, "beta_core", random);
        addAutomated(fleet, "volantian_enforcer_vi_Standard", (String)null, "beta_core", random);
        addAutomated(fleet, "volantian_condor_vi_Standard", (String)null, "gamma_core", random);
        addAutomated(fleet, "volantian_condor_vi_Standard", (String)null, "gamma_core", random);
        addAutomated(fleet, "volantian_manticore_vi_Standard", (String)null, "gamma_core", random);
        addAutomated(fleet, "volantian_monitor_vi_Standard", (String)null, "gamma_core", random);
        addAutomated(fleet, "volantian_monitor_vi_Standard", (String)null, "gamma_core", random);


        Iterator var10 = fleet.getFleetData().getMembersListCopy().iterator();

        FleetMemberAPI curr;
        while(var10.hasNext()) {
            curr = (FleetMemberAPI)var10.next();
            makeAICoreSkillsGoodForLowTech(curr, true);
            curr.getRepairTracker().setCR(curr.getRepairTracker().getMaxCR());
        }

        var10 = fleet.getFleetData().getMembersListCopy().iterator();

        while(var10.hasNext()) {
            curr = (FleetMemberAPI)var10.next();
            v = curr.getVariant().clone();
            v.setSource(VariantSource.REFIT);
            curr.setVariant(v, false, false);
        }

        if (fleet.getInflater() instanceof DefaultFleetInflater) {
            DefaultFleetInflater dfi = (DefaultFleetInflater)fleet.getInflater();
            DefaultFleetInflaterParams dfip = (DefaultFleetInflaterParams)dfi.getParams();
            dfip.allWeapons = true;
            dfip.averageSMods = 3;
            fleet.inflateIfNeeded();
            fleet.setInflater((FleetInflater)null);
        }

        var10 = fleet.getFleetData().getMembersListCopy().iterator();

        while(var10.hasNext()) {
            curr = (FleetMemberAPI)var10.next();
            curr.getVariant().addPermaMod("automated");
            curr.getVariant().setVariantDisplayName("Automated");
            curr.getVariant().addTag("no_auto_penalty");
            curr.getVariant().addTag(Tags.UNRECOVERABLE);
        }

    }

    public static void addAutomated(CampaignFleetAPI fleet, String variantId, String shipName, String aiCore, Random random) {
        AICoreOfficerPlugin plugin = Misc.getAICoreOfficerPlugin("alpha_core");
        FleetMemberAPI member = fleet.getFleetData().addFleetMember(variantId);
        member.setId("xivtf_" + random.nextLong());
        if (shipName != null) {
            member.setShipName(shipName);
        }

        if (aiCore != null) {
            PersonAPI person = plugin.createPerson(aiCore, fleet.getFaction().getId(), random);
            member.setCaptain(person);
        }

    }

    public static void makeAICoreSkillsGoodForLowTech(FleetMemberAPI member, boolean integrate) {
        if (member.getCaptain() != null && member.getCaptain().isAICore()) {
            PersonAPI person = member.getCaptain();
            person.getStats().setSkipRefresh(true);
            String aiCoreId = person.getAICoreId();
            if (integrate) {
                person.getStats().setLevel(person.getStats().getLevel() + 1);
                person.getStats().setSkillLevel("ballistic_mastery", 2.0F);
            }

            boolean alpha = "alpha_core".equals(aiCoreId);
            boolean beta = "beta_core".equals(aiCoreId);
            boolean gamma = "gamma_core".equals(aiCoreId);
            if (member.isCapital() || member.isCruiser()) {
                person.getStats().setSkillLevel("combat_endurance", 0.0F);
                person.getStats().setSkillLevel("missile_specialization", 2.0F);
            }

            person.getStats().setSkipRefresh(false);
        }
    }

    @Override
    public int getHandlingPriority(Object params) {
        if (!(params instanceof SalvageGenFromSeed.SDMParams)) return 0;
        SalvageGenFromSeed.SDMParams p = (SalvageGenFromSeed.SDMParams) params;

        if (p.entity != null && p.entity.getMemoryWithoutUpdate().contains(
                Fevali.VRI_BLUESTEEL)) {
            return 2;
        }
        return -1;
    }

    public float getQuality(SalvageGenFromSeed.SDMParams p, float quality, Random random, boolean withOverride) {
        return quality;
    }
}