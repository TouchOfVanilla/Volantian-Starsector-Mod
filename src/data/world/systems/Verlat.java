package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.ids.Conditions;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BaseThemeGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.RemnantThemeGenerator;
import com.fs.starfarer.api.util.Misc;
import org.lazywizard.lazylib.MathUtils;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;

import java.util.Iterator;
public class Verlat {

    final float Verlatdisty = MathUtils.getRandomNumberInRange(-24000, -52000);
    final float Verlatdistx = MathUtils.getRandomNumberInRange(24000, 62000);

    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Verlat");
        system.getLocation().set(Verlatdistx, Verlatdisty);
        //system.setLightColor(new Color(31,247,182, 100));

        PlanetAPI VerlatStar = system.initStar("vri_star_Verlat", // unique id for this star
                "star_orange", // id in planets.json
                600f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult
        SectorEntityToken AKCenter = system.addCustomEntity("AK_Center", "null", "AKCenter", "neutral");
        AKCenter.setCircularOrbitPointingDown(VerlatStar, 45, 3500, 90);

        PlanetAPI Aomia = system.addPlanet("vri_Aomia",
                AKCenter,
                "Aomia",
                "terran-eccentric",
                0f,
                120f,
                200f,
                90f);
        Misc.initConditionMarket(Aomia);
        MarketAPI AomiaMarket = Aomia.getMarket();
        AomiaMarket.setPlanetConditionMarketOnly(true);
        AomiaMarket.addCondition(Conditions.HABITABLE);
        AomiaMarket.addCondition(Conditions.ORE_RICH);
        AomiaMarket.addCondition(Conditions.ORGANICS_ABUNDANT);
        AomiaMarket.addCondition(Conditions.DECIVILIZED);
        AomiaMarket.addCondition(Conditions.FARMLAND_POOR);
        AomiaMarket.addCondition(Conditions.EXTREME_WEATHER);
        AomiaMarket.addCondition(Conditions.RUINS_EXTENSIVE);
        AomiaMarket.addCondition(Conditions.VOLATILES_ABUNDANT);
        AomiaMarket.setPrimaryEntity(Aomia);
        Aomia.setMarket(AomiaMarket);
        Aomia.setCustomDescriptionId("vri_planet_Aomia"); //reference descriptions.csv
        PlanetAPI Kedranov = system.addPlanet("vri_Kedranov",
                AKCenter,
                "Kedranov",
                "terran",
                180f,
                120f,
                200f,
                90f);
        Misc.initConditionMarket(Kedranov);
        MarketAPI KedranovMarket = Kedranov.getMarket();
        KedranovMarket.setPlanetConditionMarketOnly(true);
        KedranovMarket.addCondition(Conditions.HABITABLE);
        KedranovMarket.addCondition(Conditions.DECIVILIZED);
        KedranovMarket.addCondition(Conditions.ORE_RICH);
        KedranovMarket.addCondition(Conditions.ORGANICS_ABUNDANT);
        KedranovMarket.addCondition(Conditions.FARMLAND_BOUNTIFUL);
        KedranovMarket.addCondition(Conditions.MILD_CLIMATE);
        KedranovMarket.addCondition(Conditions.RUINS_VAST);
        KedranovMarket.setPrimaryEntity(Kedranov);
        Kedranov.setMarket(KedranovMarket);
        Kedranov.setCustomDescriptionId("vri_planet_Kedranov"); //reference descriptions.csv

        SectorEntityToken AKTether = system.addCustomEntity("AK_Tether", "A-K Tether Station", "AKSpaceElevator", "neutral");
        AKTether.setCircularOrbitPointingDown(Aomia, 180, 200, 90);
        Misc.setAbandonedStationMarket("ak_tether_market", AKTether);

        AKTether.setInteractionImage("illustrations", "abandoned_station3");
        CargoAPI cargo = AKTether.getMarket().getSubmarket("storage").getCargo();
        cargo.initMothballedShips("independent");
        CampaignFleetAPI temp = Global.getFactory().createEmptyFleet("independent", (String)null, true);
        temp.getFleetData().addFleetMember("odyssey_Balanced");
        temp.getFleetData().addFleetMember("apogee_Balanced");
        temp.getFleetData().addFleetMember("apogee_Balanced");
        temp.getFleetData().addFleetMember("crig_Standard");
        temp.getFleetData().addFleetMember("crig_Standard");
        temp.getFleetData().addFleetMember("shepherd_Frontier");
        temp.getFleetData().addFleetMember("ox_Standard");
        DefaultFleetInflaterParams p = new DefaultFleetInflaterParams();
        p.quality = -1;
        temp.setInflater(new DefaultFleetInflater(p)); //scuff it up
        temp.inflateIfNeeded();
        temp.setInflater(null);

        if (Global.getSettings().getModManager().isModEnabled("aotd_lost_glory")){
            Kedranov.getMarket().addCondition("planetarytether");
            AomiaMarket.addCondition("planetarytether");
        }


        for(Iterator var25 = temp.getFleetData().getMembersListCopy().iterator(); var25.hasNext();) {
            FleetMemberAPI member = (FleetMemberAPI)var25.next();
            Iterator var27 = member.getVariant().getFittedWeaponSlots().iterator();
                cargo.getMothballedShips().addFleetMember(member);
        }
        system.autogenerateHyperspaceJumpPoints(true, true);
        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, VerlatStar, StarAge.OLD,
                2, 4, // min/max entities to add
                4000, // radius to start adding at
                2, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                true); // whether to allow habitable worlds
        StarSystemGenerator.addOrbitingEntities(system, VerlatStar, StarAge.OLD,
                3, 5, // min/max entities to add
                4000, // radius to start adding at
                2, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                true); // whether to allow habitable worlds

        RemnantThemeGenerator.addBeacon(system, RemnantThemeGenerator.RemnantSystemType.RESURGENT);

        if (Global.getSettings().getModManager().isModEnabled("aotd_lost_glory")){
            AomiaMarket.addCondition("planetarytether");
            KedranovMarket.addCondition("planetarytether");
        }

    }

}
