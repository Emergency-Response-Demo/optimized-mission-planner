/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.redhat.emergency.response.missionrouting.plugin.planner.domain;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

/**
 * Creates {@link MissionRoutingSolution} instances.
 */
public class SolutionFactory {

    private SolutionFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create an empty solution. Empty solution has zero locations, evacuationCenters, incidents and vehicles and a zero score.
     *
     * @return empty solution
     */
    public static MissionRoutingSolution emptySolution() {
        MissionRoutingSolution solution = new MissionRoutingSolution();
        solution.setIncidentList(new ArrayList<>());
        solution.setEvacuationCenterList(new ArrayList<>());
        solution.setVehicleList(new ArrayList<>());
        solution.setScore(HardSoftLongScore.ZERO);
        return solution;
    }

    /**
     * Create a new solution from given vehicles, evacuationCenter and incidents.
     * All vehicles will be placed in the evacuationCenter.
     * <p>
     * The returned solution's vehicles and locations are new collections so modifying the solution
     * won't affect the collections given as arguments.
     * <p>
     * <strong><em>Elements of the argument collections are NOT cloned.</em></strong>
     *
     * @param vehicles vehicles
     * @param evacuationCenter evacuationCenter
     * @param incidents incidents
     * @return solution containing the given vehicles, evacuationCenter, incidents and their locations
     */
    public static MissionRoutingSolution solutionFromIncidents(
            List<PlanningVehicle> vehicles,
            List<PlanningEvacuationCenter> evacuationCenters,
            List<PlanningIncident> incidents) {

        MissionRoutingSolution solution = new MissionRoutingSolution();
        solution.setVehicleList(new ArrayList<>(vehicles));
        solution.setEvacuationCenterList(new ArrayList<>(evacuationCenters));
        solution.setIncidentList(new ArrayList<>(incidents));
        solution.setScore(HardSoftLongScore.ZERO);
        
        return solution;
    }

    private static void moveAllVehiclesToEvacuationCenter(List<PlanningVehicle> vehicles, PlanningEvacuationCenter evacuationCenter) {
        vehicles.forEach(vehicle -> vehicle.setEvacuationCenter(evacuationCenter));
    }
}
