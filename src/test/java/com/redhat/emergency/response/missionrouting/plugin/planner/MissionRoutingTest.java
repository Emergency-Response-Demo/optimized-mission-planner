package com.redhat.emergency.response.missionrouting.plugin.planner;

import io.quarkus.test.junit.QuarkusTest;

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
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningLocationFactory;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningVehicle;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningVehicleFactory;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.SolutionFactory;

@QuarkusTest
public class MissionRoutingTest {

    @Inject
    SolverFactory solverFactory;
    @Inject
    SolverManager<MissionRoutingSolution, Long> solverManager;
    @Inject
    ScoreManager<MissionRoutingSolution> scoreManager;

    @Test
    public void testMissionRoutingPlanning() {
        System.out.println("Init test!");

        List<PlanningIncident> incidents = new ArrayList<>();
        List<PlanningVehicle> boats = new ArrayList<>();
        List<PlanningEvacuationCenter> shelters = new ArrayList<>();

        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(1L, new Coordinates(BigDecimal.valueOf(42.6818928), BigDecimal.valueOf(-71.2963726)), "Dracut, MA"), 
                    l -> 200000L) ) );

        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(2L, new Coordinates(BigDecimal.valueOf(42.6097476), BigDecimal.valueOf(-72.5979752)), "Greenfield, MA"), 
                    l -> 300000L) ) );

        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(3L, new Coordinates(BigDecimal.valueOf(42.1014831), BigDecimal.valueOf(-72.589811)), "Springfield, MA"), 
                    l -> 400000L) ) );
                    
        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(4L, new Coordinates(BigDecimal.valueOf(41.7353872), BigDecimal.valueOf(-70.1939087)), "Dennis, MA"), 
                    l -> 500000) ) );
        
        PlanningVehicle boat1 = PlanningVehicleFactory.testVehicle(1L, 10);
        PlanningEvacuationCenter shelter1 = 
            new PlanningEvacuationCenter(PlanningLocationFactory.fromDomain(
                new Location(5L, new Coordinates(BigDecimal.valueOf(42.3663473), BigDecimal.valueOf(-710202646)), "Shelter at LOGAN AIRPORT"), 
                    l -> 900000));
        boat1.setEvacuationCenter( shelter1 );        
        boats.add( boat1 );
        shelters.add( shelter1 );

        // Solve the problem using the current Thread
        solverFactory.buildSolver().solve(SolutionFactory.solutionFromIncidents(boats, shelters, incidents));

        // Use a separate Thread
        // solverManager.solve(2L, SolutionFactory.solutionFromIncidents(boats, shelters, incidents), (solution) -> {
        //     System.out.println(">>>>>> New solution found with Score: " + solution.getScore() + "<<<<<<");
        //     SolverStatus status = solverManager.getSolverStatus(1L);
        //     System.out.println("\t Status: " + status);
        // });

        System.out.println("Finish test!");
        
    }

}