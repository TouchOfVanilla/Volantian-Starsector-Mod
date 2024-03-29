package data.missions;

import com.fs.starfarer.api.Global;
import com.fs.starfarer.api.campaign.FleetDataAPI;
import com.fs.starfarer.api.campaign.PlanetSpecAPI;
import com.fs.starfarer.api.fleet.FleetMemberAPI;
import com.fs.starfarer.api.impl.campaign.ids.Factions;
import com.fs.starfarer.api.mission.FleetSide;
import com.fs.starfarer.api.mission.MissionDefinitionAPI;
import com.fs.starfarer.api.mission.MissionDefinitionPlugin;
import com.fs.starfarer.api.util.WeightedRandomPicker;

import java.awt.*;
import java.util.List;
import java.util.*;

public class VRI_BaseRandomBattle implements MissionDefinitionPlugin {
    public static final Comparator<FleetMemberAPI> PRIORITY = new Comparator<FleetMemberAPI>() {
        // -1 means member1 is first, 1 means member2 is first
        @Override
        public int compare(FleetMemberAPI member1, FleetMemberAPI member2) {
            if (member1.isFlagship()) {
                if (!member2.isFlagship()) {
                    return -1;
                }
            } else {
                if (member2.isFlagship()) {
                    return 1;
                }
            }
            if (!member1.isCivilian()) {
                if (member2.isCivilian()) {
                    return -1;
                }
            } else {
                if (!member2.isCivilian()) {
                    return 1;
                }
            }
            if (!member1.isFighterWing()) {
                if (member2.isFighterWing()) {
                    return -1;
                }
            } else {
                if (!member2.isFighterWing()) {
                    return 1;
                }
            }
            if (member1.getFleetPointCost() > member2.getFleetPointCost()) {
                return -1;
            } else if (member1.getFleetPointCost() < member2.getFleetPointCost()) {
                return 1;
            }
            if (member1.getCaptain() != null) {
                if (member2.getCaptain() == null) {
                    return -1;
                }
            } else {
                if (member2.getCaptain() != null) {
                    return 1;
                }
            }
            if (!member1.isFrigate()) {
                if (member2.isFrigate()) {
                    return -1;
                }
            } else {
                if (!member2.isFrigate()) {
                    return 1;
                }
            }
            if (!member1.isDestroyer()) {
                if (member2.isDestroyer()) {
                    return -1;
                }
            } else {
                if (!member2.isDestroyer()) {
                    return 1;
                }
            }
            if (!member1.isCruiser()) {
                if (member2.isCruiser()) {
                    return -1;
                }
            } else {
                if (!member2.isCruiser()) {
                    return 1;
                }
            }
            if (!member1.isCapital()) {
                if (member2.isCapital()) {
                    return -1;
                }
            } else {
                if (!member2.isCapital()) {
                    return 1;
                }
            }
            return member1.getSpecId().compareTo(member2.getSpecId());
        }
    };

    private static boolean first = true;

    protected static final WeightedRandomPicker<String> FACTIONS = new WeightedRandomPicker<String>();
    protected static final String[] OBJECTIVE_TYPES = {
            "sensor_array", "sensor_array", "nav_buoy", "nav_buoy", "comm_relay"
    };
    protected static final List<String> PLANETS = new ArrayList<String>(35);
    protected static final List<Color> STAR_COLORS = new ArrayList<Color>(13);

    protected static final Random rand = new Random();

    public static void inflateFleet(List<FleetMemberAPI> fleet, FleetSide side, MissionDefinitionAPI api) {
        boolean flagship = true;
        for (FleetMemberAPI fleetMember : fleet) {
            if (!fleetMember.isFighterWing()) {
                fleetMember.setFlagship(flagship);
                api.addFleetMember(side, fleetMember);

                flagship = false;
            }

            //member.getCrewComposition().addCrew(member.getMaxCrew() - member.getCrewComposition().getCrew());
            //member.getRepairTracker().setCR(0.7f);
        }
    }

    protected static FleetDataAPI generateFleet(int maxPts, float qualityFactor, float opBonus, int avgSMods, FleetSide side,
                                                String faction, String fleetType, MissionDefinitionAPI api, long seed, boolean autoshit) {
        VRI_FleetGenerator type;
        switch (fleetType) {
            case "raiders":
                type = VRI_FleetGenerator.GeneratorFleetTypes.RAIDERS;
                break;
            case "patrol fleet":
                type = VRI_FleetGenerator.GeneratorFleetTypes.PATROL;
                break;
            case "hunter-killers":
                type = VRI_FleetGenerator.GeneratorFleetTypes.HUNTERS;
                break;
            case "war fleet":
                type = VRI_FleetGenerator.GeneratorFleetTypes.WAR;
                break;
            case "defense fleet":
                type = VRI_FleetGenerator.GeneratorFleetTypes.DEFENSE;
                break;
            case "convoy":
                type = VRI_FleetGenerator.GeneratorFleetTypes.CONVOY;
                break;
            case "blockade-runners":
                type = VRI_FleetGenerator.GeneratorFleetTypes.BLOCKADE;
                break;
            case "invasion fleet":
                type = VRI_FleetGenerator.GeneratorFleetTypes.INVASION;
                break;
            default:
                return null;
        }

        return type.generate(api, side, faction, qualityFactor, opBonus, avgSMods, maxPts, seed, autoshit);
    }

    static FleetDataAPI finishFleet(FleetDataAPI fleet, FleetSide side, String faction, MissionDefinitionAPI api) {
        List<FleetMemberAPI> memberList = fleet.getMembersInPriorityOrder();
        Collections.sort(memberList, PRIORITY);

        inflateFleet(memberList, side, api);

        return fleet;
    }

    @Override
    public void defineMission(MissionDefinitionAPI api) {
        if (first) {
            first = false;
            init();
        }
    }

    private void init() {
        for (PlanetSpecAPI spec : Global.getSettings().getAllPlanetSpecs()) {
            PLANETS.add(spec.getPlanetType());
            if (spec.isStar()) {
                STAR_COLORS.add(spec.getCoronaColor());
            }
        }
        FACTIONS.add(Factions.PIRATES, 1f);
        FACTIONS.add(Factions.HEGEMONY, 1f);
        FACTIONS.add(Factions.DIKTAT, 0.5f);
        FACTIONS.add(Factions.LIONS_GUARD, 0.25f);
        FACTIONS.add(Factions.INDEPENDENT, 1f);
        FACTIONS.add(Factions.SCAVENGERS, 0.25f);
        FACTIONS.add(Factions.TRITACHYON, 1f);
        FACTIONS.add(Factions.PERSEAN, 1f);
        FACTIONS.add(Factions.LUDDIC_PATH, 0.5f);
        FACTIONS.add(Factions.LUDDIC_CHURCH, 1f);
        FACTIONS.add(Factions.DERELICT, 0.25f);
        FACTIONS.add(Factions.REMNANTS, 0.5f);
        if (Global.getSettings().isDevMode()) {
            FACTIONS.add(Factions.OMEGA, 0.1f);
        }
        FACTIONS.add("domain", 0.25f);
        FACTIONS.add("sector", 0.25f);
        FACTIONS.add("everything", 0.25f);

        FACTIONS.add("vri", 0.25f);
    }

    private static float prevXDir = 0;
    private static float prevYDir = 0;

    protected static void addObjectiveAt(float xMult, float yMult, float xOff, float yOff, float width, float height, MissionDefinitionAPI api, List<String> objs) {
        String type = pickAny();
        if (objs != null && objs.size() > 0) {
            int index = (int) (Math.random() * objs.size());
            type = objs.remove(index);
        }

        float xPad = 2000f;
        float yPad = 3000f;

        float minX = -width / 2 + xPad;
        float minY = -height / 2 + yPad;

        float x = (width - xPad * 2f) * xMult + minX;
        float y = (height - yPad * 2f) * yMult + minY;

        x = ((int) x / 1000) * 1000f;
        y = ((int) y / 1000) * 1000f;

        float offsetX = Math.round((Math.random() - 0.5f) * xOff * 2f) * 1000f;
        float offsetY = Math.round((Math.random() - 0.5f) * yOff * 2f) * 1000f;

        float xDir = (float) Math.signum(offsetX);
        float yDir = (float) Math.signum(offsetY);

        if (xDir == prevXDir && xOff > 0) {
            xDir = -xDir;
            offsetX = Math.abs(offsetX) * -prevXDir;
        }

        if (yDir == prevYDir && yOff > 0) {
            yDir = -yDir;
            offsetY = Math.abs(offsetY) * -prevYDir;
        }

        prevXDir = xDir;
        prevYDir = yDir;

        x += offsetX;
        y += offsetY;

        api.addObjective(x, y, type);

        if ((float) Math.random() > 0.6f) {
            float nebulaSize = (float) Math.random() * 1500f + 500f;
            api.addNebula(x, y, nebulaSize);
        }
    }

    private static String pickAny() {
        float r = (float) Math.random();
        if (r < 0.33f) {
            return "nav_buoy";
        } else if (r < 0.67f) {
            return "sensor_array";
        } else {
            return "comm_relay";
        }
    }
}
