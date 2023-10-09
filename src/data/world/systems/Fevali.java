package data.world.systems;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.*;
import com.fs.starfarer.api.campaign.econ.MarketAPI;
import com.fs.starfarer.api.campaign.rules.MemoryAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.DModManager;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflater;
import com.fs.starfarer.api.impl.campaign.fleets.DefaultFleetInflaterParams;
import com.fs.starfarer.api.impl.campaign.fleets.FleetFactoryV3;
import com.fs.starfarer.api.impl.campaign.fleets.FleetParamsV3;
import com.fs.starfarer.api.impl.campaign.ids.*;
import com.fs.starfarer.api.impl.campaign.procgen.DefenderDataOverride;
import com.fs.starfarer.api.impl.campaign.procgen.SalvageEntityGenDataSpec;
import com.fs.starfarer.api.impl.campaign.procgen.StarAge;
import com.fs.starfarer.api.impl.campaign.procgen.StarSystemGenerator;
import com.fs.starfarer.api.impl.campaign.procgen.themes.BluesteelDefenderPluginImpl;
import com.fs.starfarer.api.impl.campaign.procgen.themes.PKDefenderPluginImpl;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.SalvageGenFromSeed;
import com.fs.starfarer.api.impl.campaign.rulecmd.salvage.special.ShipRecoverySpecial;
import com.fs.starfarer.api.impl.campaign.terrain.BaseTiledTerrain;
import com.fs.starfarer.api.impl.campaign.terrain.DebrisFieldTerrainPlugin;
import com.fs.starfarer.api.ui.SectorMapAPI;
import com.fs.starfarer.api.util.Misc;
import com.fs.starfarer.campaign.econ.Market;
import com.fs.starfarer.loading.ShipNameStore;
import data.world.VRIGen;
import com.fs.starfarer.api.util.WeightedRandomPicker;
import org.magiclib.campaign.MagicFleetBuilder;

import java.awt.*;
import java.util.*;

import static com.fs.starfarer.api.impl.campaign.world.TTBlackSite.addDerelict;


public class Fevali {

    final float jumpCenterDist = 5500f;
    final float dust3Dist = 6000f;
    final float jumpFringeDist = 10000f;
    private java.util.Random random;
    public String VENTALSE_VI = "volantian_ventalse_vi_standard";
    public String LEGION_VI = "volantian_legion_vi_standard";
    public String DOMINATOR_VI = "volantian_dominator_vi_standard";
    public String HERON_VI = "volantian_heron_vi_standard";
    public String ERADICATOR_VI = "volantian_eradicator_vi_standard";
    public String ENFORCER_VI = "volantian_enforcer_vi_standard";
    public String MANTICORE_VI = "volantian_manticore_vi_standard";
    public String CONDOR_VI = "volantian_condor_vi_standard";
    public String MONITOR_VI = "volantian_monitor_vi_standard";
    public String VANGUARD_VI = "volantian_vanguard_vi_standard";
    public String LASHER_VI = "volantian_lasher_vi_standard";
    public static String VRI_BLUESTEEL = "$bluesteel_bastion";


    public void generate(SectorAPI sector) {
        StarSystemAPI system = sector.createStarSystem("Fevali");
        system.getLocation().set(46500, -46000);
        //system.setLightColor(new Color(31,247,182, 100));

        PlanetAPI FevaliStar = system.initStar("vri_star_Fevali", // unique id for this star
                "star_yellow", // id in planets.json
                650f,        // radius (in pixels at default zoom)
                600, // corona radius, from star edge
                10f, // solar wind burn level
                0.5f, // flare probability
                5f); // cr loss mult

    PlanetAPI Kaizmera = system.addPlanet("vri_planet_Kaizmera",
            FevaliStar,
            "Kaizmera",
            "terran",
            90f,
            200f,
            5000,
            120f);
        Misc.initConditionMarket(Kaizmera);
        MarketAPI Kaizmera_market = Kaizmera.getMarket();
        Kaizmera_market.setPlanetConditionMarketOnly(true);
        Kaizmera_market.addCondition(Conditions.HABITABLE);
        Kaizmera_market.addCondition(Conditions.FARMLAND_BOUNTIFUL);
        Kaizmera_market.addCondition(Conditions.ORE_ULTRARICH);
        Kaizmera_market.addCondition(Conditions.ORGANICS_ABUNDANT);
        Kaizmera_market.addCondition(Conditions.LOW_GRAVITY);
        Kaizmera_market.addCondition(Conditions.RUINS_EXTENSIVE);
        Kaizmera_market.addCondition(Conditions.DECIVILIZED);
        Kaizmera_market.setPrimaryEntity(Kaizmera);
        Kaizmera.setMarket(Kaizmera_market);

        SectorEntityToken BluesteelBastion = system.addCustomEntity("bluesteel_bastion",
                "Bluesteel Bastion", "bluesteel_bastion", "neutral");

        BluesteelBastion.setCircularOrbitPointingDown(system.getEntityById("vri_planet_Kaizmera"), 45, 600, 50);

        BluesteelBastion.setInteractionImage("illustrations", "abandoned_station3");
        SalvageGenFromSeed.SDMParams p = new SalvageGenFromSeed.SDMParams();
        Misc.setDefenderOverride(BluesteelBastion, new DefenderDataOverride("vi", 1, 30, 50));
        BluesteelBastion.getMemoryWithoutUpdate().set(VRI_BLUESTEEL, true);
        SectorEntityToken fevali_nebula = system.addTerrain(Terrain.NEBULA, new BaseTiledTerrain.TileParams(
                "          " +
                        " x   xxxx " +
                        "   xxx    " +
                        "  xx  xx  " +
                        "  xxxxx   " +
                        "  xxxxx x " +
                        "   xxxx   " +
                        "x  xxxxx  " +
                        "  xxxxxxx " +
                        "    xxx   ",
                10, 10, // size of the nebula grid, should match above string
                "terrain", "nebula_blue", 4, 4, null));
        fevali_nebula.setCircularOrbit(FevaliStar, 60 + 60, 11500, 820);
        JumpPointAPI jumpPoint1 = Global.getFactory().createJumpPoint(
                "Center_jump",
                "Center System Jump");

        jumpPoint1.setCircularOrbit(system.getEntityById("vri_star_Fevali"), 10, jumpCenterDist, 200f);
        jumpPoint1.setStandardWormholeToHyperspaceVisual();

        JumpPointAPI jumpPoint2 = Global.getFactory().createJumpPoint(
                "fringe_jump",
                "Fringe System Jump");

        jumpPoint2.setCircularOrbit(system.getEntityById("vri_star_Fevali"), 2, jumpFringeDist, 1000f);
        jumpPoint2.setStandardWormholeToHyperspaceVisual();

        system.addEntity(jumpPoint1);
        system.addEntity(jumpPoint2);

        system.autogenerateHyperspaceJumpPoints(true, false);

        system.addRingBand(FevaliStar, "misc", "rings_dust0", 256f, 0, Color.gray, 256f, dust3Dist, 330f);
        system.addRingBand(FevaliStar, "misc", "rings_dust0", 256f, 0, Color.white, 256f, dust3Dist, 310f);

        float radiusAfter = StarSystemGenerator.addOrbitingEntities(system, FevaliStar, StarAge.OLD,
                3, 5, // min/max entities to add
                5500, // radius to start adding at
                1, // name offset - next planet will be <system name> <roman numeral of this parameter + 1>
                true, // whether to use custom or system-name based names
                true); // whether to allow habitable worlds

    }
}
