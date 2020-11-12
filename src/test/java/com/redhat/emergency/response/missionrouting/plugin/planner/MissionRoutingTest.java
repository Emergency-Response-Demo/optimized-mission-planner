package com.redhat.emergency.response.missionrouting.plugin.planner;

import io.quarkus.test.junit.QuarkusTest;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.is;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import com.redhat.emergency.response.missionrouting.domain.Coordinates;
import com.redhat.emergency.response.missionrouting.domain.Location;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.DistanceMap;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.MissionRoutingSolution;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningEvacuationCenter;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningIncident;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningIncidentFactory;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningLocation;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningLocationFactory;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningVehicle;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningVehicleFactory;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.SolutionFactory;
import com.redhat.emergency.response.missionrouting.plugin.routing.RoutingProperties;
import com.redhat.emergency.response.missionrouting.service.location.DistanceMatrix;
import com.redhat.emergency.response.missionrouting.service.location.DistanceMatrixRow;

@QuarkusTest
public class MissionRoutingTest {
    private static final Logger LOG = LoggerFactory.getLogger(MissionRoutingTest.class);
    
    @Inject
    SolverFactory<MissionRoutingSolution> solverFactory;
    @Inject
    SolverManager<MissionRoutingSolution, Long> solverManager;
    @Inject
    ScoreManager<MissionRoutingSolution> scoreManager;
    @Inject 
    DistanceMatrix distanceMatrix;
    @Inject
    RouteChangedEventPublisher routeChangedEventPublisher;

    @Test
    public void testMissionRoutingPlanning() {
        LOG.info("Init test!");

        List<PlanningIncident> incidents = new ArrayList<>();
        List<PlanningVehicle> boats = new ArrayList<>();
        List<PlanningEvacuationCenter> shelters = new ArrayList<>();

        try {
            // Evacuation centers
            PlanningLocation pLoganAirport = createPlannigLocation(10L, BigDecimal.valueOf(42.3663473), BigDecimal.valueOf(-71.0202646), "Shelter at LOGAN AIRPORT");
            PlanningEvacuationCenter loganshelter = new PlanningEvacuationCenter(pLoganAirport);
            shelters.add(loganshelter);

            PlanningLocation pPittsfield = createPlannigLocation(20L, BigDecimal.valueOf(42.4507686), BigDecimal.valueOf(-73.3304686), "Shelter at PITTSFIELD");
            PlanningEvacuationCenter pittisfieldShelter = new PlanningEvacuationCenter(pPittsfield);
            shelters.add(pittisfieldShelter);

            // Boats
            PlanningLocation pWesternMass = createPlannigLocation(30L, BigDecimal.valueOf(42.3711232), BigDecimal.valueOf(-73.2721461), "Lenox boat");
            PlanningVehicle lenoxBoat = PlanningVehicleFactory.testVehicle(1L, 10);
            lenoxBoat.setLocation(pWesternMass);
            boats.add(lenoxBoat);

            PlanningLocation pEasternMass = createPlannigLocation(40L, BigDecimal.valueOf(42.6159286), BigDecimal.valueOf(-70.6619888), "Gloucester boat");
            PlanningVehicle gloucesterBoat = PlanningVehicleFactory.testVehicle(2L, 10);
            gloucesterBoat.setLocation(pEasternMass);
            boats.add(gloucesterBoat);

            // Incidents
            PlanningLocation pDracut = createPlannigLocation(50L, BigDecimal.valueOf(42.6818928), BigDecimal.valueOf(-71.2963726), "Dracut");
            incidents.add(PlanningIncidentFactory.fromLocation(pDracut, 5));

            PlanningLocation pGreenfield = createPlannigLocation(60L, BigDecimal.valueOf(42.6097476), BigDecimal.valueOf(-72.5979752), "Greenfield");
            incidents.add(PlanningIncidentFactory.fromLocation(pGreenfield, 3));

            PlanningLocation pSpringfield = createPlannigLocation(70L, BigDecimal.valueOf(42.1014831), BigDecimal.valueOf(-72.589811), "Springfield");
            incidents.add(PlanningIncidentFactory.fromLocation(pSpringfield, 2));

            PlanningLocation pDennis = createPlannigLocation(80L, BigDecimal.valueOf(41.7353872), BigDecimal.valueOf(-70.1939087), "Dennis");
            incidents.add(PlanningIncidentFactory.fromLocation(pDennis, 10));
            
            //print the current distance matrix
            LOG.info(distanceMatrix.toString());

            // Solve the problem using the current Thread
            MissionRoutingSolution solution = solverFactory.buildSolver().solve(SolutionFactory.solutionFromIncidents(boats, shelters, incidents));
            routeChangedEventPublisher.publishSolution(solution);
        } catch (Exception e) {
            e.printStackTrace();
        }        

        LOG.info("Finish test!");        
    }

    private PlanningLocation createPlannigLocation(long id, BigDecimal lat, BigDecimal lon, String description) {
        Location location = new Location(id, new Coordinates(lat, lon), description);
        DistanceMatrixRow locationDMR = distanceMatrix.addLocation(location);
        return PlanningLocationFactory.fromDomain(location, new DistanceMapImpl(locationDMR));
    }

}