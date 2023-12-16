package com.fs.starfarer.api.impl.campaign;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FactionAPI;
import com.fs.starfarer.api.campaign.PlanetAPI;
import com.fs.starfarer.api.campaign.SectorEntityToken;
import com.fs.starfarer.api.campaign.StarSystemAPI;
import com.fs.starfarer.api.combat.ShipAPI;
import com.fs.starfarer.api.combat.ShipVariantAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.world.TTBlackSite;
import com.fs.starfarer.api.util.Misc;
import org.apache.log4j.Logger;
import org.lazywizard.lazylib.MathUtils;

import java.util.ArrayList;
import java.util.Iterator;

public class VRIDerelictSpawner {
    private static Logger log = Global.getLogger(VRIDerelictSpawner.class);

public VRIDerelictSpawner(){
}

    public static void spawnDerelicts(){
        ArrayList gudeplanets = new ArrayList<PlanetAPI>();
        Iterator stariter = Global.getSector().getStarSystems().iterator();
        while (stariter.hasNext()){
            StarSystemAPI star = (StarSystemAPI) stariter.next();
            if(star.isProcgen()){
                Iterator planetiter = star.getPlanets().iterator();
                while (planetiter.hasNext()){
                    PlanetAPI planet = (PlanetAPI) planetiter.next();
                    Float prob = MathUtils.getRandomNumberInRange(0f,100f);
                    if (prob > 90 && (planet.hasCondition(Conditions.RUINS_VAST) || planet.hasCondition(Conditions.RUINS_EXTENSIVE) || planet.hasCondition(Conditions.RUINS_WIDESPREAD) || planet.hasCondition(Conditions.RUINS_SCATTERED))){
                        gudeplanets.add(planet);
                    }
                }
            }
        }
        ArrayList variants = new ArrayList<String>();
        variants.add("volantian_eradicator_vi_Standard");
        variants.add("volantian_manticore_vi_Standard");
        variants.add("volantian_monitor_vi_Standard");
        variants.add("volantian_lasher_vi_Standard");
        variants.add("volantian_dominator_vi_Standard");
        variants.add("volantian_heron_vi_Standard");
        variants.add("volantian_enforcer_vi_Standard");
        variants.add("volantian_condor_vi_Standard");
        variants.add("volantian_vanguard_vi_Standard");
        Iterator gudeplanetiter = gudeplanets.iterator();
        while (gudeplanetiter.hasNext()){
            PlanetAPI chosenplanet = (PlanetAPI)gudeplanetiter.next();
            Integer arraynumber = MathUtils.getRandomNumberInRange(0,8);
            String variant = (String) variants.get(arraynumber);
            ShipVariantAPI botevar = Global.getSettings().getVariant(variant);
            String bote = botevar.getHullVariantId();
            Float orbitradius = chosenplanet.getRadius() + MathUtils.getRandomNumberInRange(50,300);
            FactionAPI faction = Global.getSector().getFaction("independent");
            String name = faction.pickRandomShipName();
            SectorEntityToken boat = TTBlackSite.addDerelict(chosenplanet.getStarSystem(), chosenplanet, variant, name, bote, ShipRecoverySpecial.ShipCondition.AVERAGE, orbitradius, true );
            log.info("Generated a " + bote + " in orbit of " + chosenplanet.getName() + " in the " + chosenplanet.getStarSystem().getName() + " system.");
        }

    }


}
