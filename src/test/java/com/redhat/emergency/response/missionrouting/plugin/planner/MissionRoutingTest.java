package com.redhat.emergency.response.missionrouting.plugin.planner;

import io.quarkus.test.junit.QuarkusTest;

import org.jboss.logging.Logger;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.api.solver.SolverManager;
import org.optaplanner.core.api.solver.SolverStatus;

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
    private static final Logger LOG = Logger.getLogger(MissionRoutingTest.class);
    
    @Inject
    SolverFactory<MissionRoutingSolution> solverFactory;
    @Inject
    SolverManager<MissionRoutingSolution, Long> solverManager;
    @Inject
    ScoreManager<MissionRoutingSolution> scoreManager;
    @Inject 
    DistanceMatrix distanceMatrix;

    @Test
    public void testMissionRoutingPlanning() {
        LOG.info("Init test!");

        List<PlanningIncident> incidents = new ArrayList<>();
        List<PlanningVehicle> boats = new ArrayList<>();
        List<PlanningEvacuationCenter> shelters = new ArrayList<>();

        try {
            Location loganAirport = 
                new Location(1L, new Coordinates(BigDecimal.valueOf(42.3663473), BigDecimal.valueOf(-71.0202646)), "Shelter at LOGAN AIRPORT");
            DistanceMatrixRow loganAirportDMR = distanceMatrix.addLocation(loganAirport);
            PlanningLocation pLoganAirport = PlanningLocationFactory.fromDomain(loganAirport, new DistanceMapImpl(loganAirportDMR));

            PlanningVehicle boat1 = PlanningVehicleFactory.testVehicle(1L, 10);
            PlanningEvacuationCenter loganshelter = new PlanningEvacuationCenter( pLoganAirport );
            shelters.add(loganshelter);
            boat1.setEvacuationCenter( loganshelter );
            boats.add( boat1 );

            Location pittsfield = 
                new Location(2L, new Coordinates(BigDecimal.valueOf(42.4507686), BigDecimal.valueOf(-73.3304686)), "Pittsfield, MA");
            DistanceMatrixRow pittsfieldDMR = distanceMatrix.addLocation(pittsfield);
            PlanningLocation pPittsfield = PlanningLocationFactory.fromDomain(pittsfield, new DistanceMapImpl(pittsfieldDMR));

            PlanningVehicle boat2 = PlanningVehicleFactory.testVehicle(2L, 10);
            PlanningEvacuationCenter pittisfieldShelter = new PlanningEvacuationCenter( pPittsfield );
            shelters.add(pittisfieldShelter);
            boat2.setEvacuationCenter( pittisfieldShelter );
            boats.add( boat2 );

            Location dracut = 
                new Location(3L, new Coordinates(BigDecimal.valueOf(42.6818928), BigDecimal.valueOf(-71.2963726)), "Dracut, MA");
            DistanceMatrixRow dracutDMR = distanceMatrix.addLocation(dracut);
            PlanningLocation pDracut = PlanningLocationFactory.fromDomain(dracut, new DistanceMapImpl(dracutDMR));
            incidents.add(PlanningIncidentFactory.fromLocation(pDracut, 5));

            Location greefield = 
                new Location(4L, new Coordinates(BigDecimal.valueOf(42.6097476), BigDecimal.valueOf(-72.5979752)), "Greenfield, MA");
            DistanceMatrixRow greenfieldDMR = distanceMatrix.addLocation(greefield);
            PlanningLocation pGreenfield = PlanningLocationFactory.fromDomain(greefield, new DistanceMapImpl(greenfieldDMR));
            incidents.add(PlanningIncidentFactory.fromLocation(pGreenfield, 7));

            Location springfield = 
                new Location(5L, new Coordinates(BigDecimal.valueOf(42.1014831), BigDecimal.valueOf(-72.589811)), "Springfield, MA");
            DistanceMatrixRow springfieldDMR = distanceMatrix.addLocation(springfield);
            PlanningLocation pSpringfield = PlanningLocationFactory.fromDomain(springfield, new DistanceMapImpl(springfieldDMR));
            incidents.add(PlanningIncidentFactory.fromLocation(pSpringfield, 5));

            Location dennis = 
                new Location(6L, new Coordinates(BigDecimal.valueOf(41.7353872), BigDecimal.valueOf(-70.1939087)), "Dennis, MA");
            DistanceMatrixRow dennisDMR = distanceMatrix.addLocation(dennis);
            PlanningLocation pDennis = PlanningLocationFactory.fromDomain(dennis, new DistanceMapImpl(dennisDMR));
            incidents.add(PlanningIncidentFactory.fromLocation(pDennis, 3));
            
            //print the current distance matrix
            LOG.info(distanceMatrix.toString());

            // Solve the problem using the current Thread
            solverFactory.buildSolver().solve(SolutionFactory.solutionFromIncidents(boats, shelters, incidents));
        } catch (Exception e) {
            e.printStackTrace();
        }        

        LOG.info("Finish test!");        
    }

}