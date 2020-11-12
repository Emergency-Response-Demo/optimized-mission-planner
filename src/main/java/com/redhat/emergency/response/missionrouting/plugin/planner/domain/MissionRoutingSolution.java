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

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;

@PlanningSolution
public class MissionRoutingSolution {

    @ProblemFactCollectionProperty
    @ValueRangeProvider(id = "evacuationCenter")
    private List<PlanningEvacuationCenter> evacuationCenterList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "vehicleRange")
    private List<PlanningVehicle> vehicleList;

    @PlanningEntityCollectionProperty
    @ValueRangeProvider(id = "incidentRange")
    private List<PlanningIncident> incidentList;
    
    @PlanningScore
    private HardSoftLongScore score;

    MissionRoutingSolution() {
        // Hide public constructor in favor of the factory.
    }

    public List<PlanningEvacuationCenter> getEvacuationCenterList() {
        return this.evacuationCenterList;
    }

    public void setEvacuationCenterList(List<PlanningEvacuationCenter> evacuationCenterList) {
        this.evacuationCenterList = evacuationCenterList;
    }

    public List<PlanningVehicle> getVehicleList() {
        return this.vehicleList;
    }

    public void setVehicleList(List<PlanningVehicle> vehicleList) {
        this.vehicleList = vehicleList;
    }

    public List<PlanningIncident> getIncidentList() {
        return this.incidentList;
    }

    public void setIncidentList(List<PlanningIncident> incidentList) {
        this.incidentList = incidentList;
    }

    public HardSoftLongScore getScore() {
        return this.score;
    }

    public void setScore(HardSoftLongScore score) {
        this.score = score;
    }

    @Override
    public String toString() {
        return "MissionRoutingSolution{" +
                "evacuationCenterList=" + evacuationCenterList +
                ", vehicleList=" + vehicleList +
                ", incidentList=" + incidentList +
                ", score=" + score +
                '}';
    }
}
