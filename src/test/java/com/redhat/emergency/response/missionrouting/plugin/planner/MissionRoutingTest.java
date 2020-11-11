package com.redhat.emergency.response.missionrouting.plugin.planner;

import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.ScoreManager;
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
                    new Location(1L, new Coordinates(BigDecimal.valueOf(1L), BigDecimal.valueOf(11L)), "Dracut, MA"), 
                    (pl) -> 200000L) ) );

        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(2L, new Coordinates(BigDecimal.valueOf(4L), BigDecimal.valueOf(8L)), "Greenfield, MA"), 
                    (pl) -> 300000L) ) );

        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(3L, new Coordinates(BigDecimal.valueOf(1L), BigDecimal.valueOf(3L)), "Springfield, MA"), 
                    (pl) -> 400000L) ) );
                    
        incidents.add( 
            PlanningIncidentFactory.fromLocation(
                PlanningLocationFactory.fromDomain(
                    new Location(4L, new Coordinates(BigDecimal.valueOf(4L), BigDecimal.valueOf(5L)), "Dennis, MA"), 
                    (pl) -> 500000) ) );
            
        boats.add( PlanningVehicleFactory.testVehicle(1L, 10) );

        shelters.add( new PlanningEvacuationCenter(PlanningLocationFactory.fromDomain(
            new Location(5L, new Coordinates(BigDecimal.valueOf(65L), BigDecimal.valueOf(25L)), "Shelter at LOGAN AIRPORT"), 
            (pl) -> 900000)) );

        solverManager.solve(2L, SolutionFactory.solutionFromIncidents(boats, shelters, incidents), (solution) -> {
            System.out.println(">>>>>> New solution found with Score: " + solution.getScore() + "<<<<<<");
            SolverStatus status = solverManager.getSolverStatus(1L);
            System.out.println("\t Status: " + status);
        });

        System.out.println("Finish test!");
        
    }

}