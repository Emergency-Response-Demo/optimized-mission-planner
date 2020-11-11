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
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.SolutionFactory;

@QuarkusTest
public class MissionRoutingTest {

    @Inject
    SolverManager<MissionRoutingSolution, Long> solverManager;
    @Inject
    ScoreManager<MissionRoutingSolution> scoreManager;

    @Test
    public void testMissionRoutingPlanning() {

        List<PlanningIncident> incidents = new ArrayList<>();
        List<PlanningVehicle> boats = new ArrayList<>();
        List<PlanningEvacuationCenter> shelters = new ArrayList<>();

        // PlanningIncidentFactory.fromLocation(
        //     PlanningLocationFactory.fromDomain(
        //     new Location(1L, new Coordinates(BigDecimal.valueOf(1L), BigDecimal.valueOf(1L)), "Test"));

        solverManager.solve(1L, SolutionFactory.solutionFromIncidents(boats, shelters, incidents), (solution) -> {
            System.out.println(">>>>>> New solution found with Score: " + solution.getScore() + "<<<<<<");
            SolverStatus status = solverManager.getSolverStatus(1L);
            System.out.println("\t Status: " + status);
        });
        
    }

}