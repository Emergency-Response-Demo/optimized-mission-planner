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

/**
 * Creates {@link PlanningIncident} instances.
 */
public class PlanningIncidentFactory {

    static final int DEFAULT_VISIT_DEMAND = 1;

    private PlanningIncidentFactory() {
        throw new AssertionError("Utility class");
    }

    /**
     * Create incident with {@link #DEFAULT_VISIT_DEMAND}.
     *
     * @param location incident's location
     * @return new incident with the default demand
     */
    public static PlanningIncident fromLocation(PlanningLocation location) {
        return fromLocation(location, DEFAULT_VISIT_DEMAND);
    }

    /**
     * Create incident of a location with the given demand.
     *
     * @param location incident's location
     * @param demand incident's demand
     * @return incident with demand at the given location
     */
    public static PlanningIncident fromLocation(PlanningLocation location, int demand) {
        PlanningIncident incident = new PlanningIncident();
        incident.setId(location.getId());
        incident.setLocation(location);
        incident.setDemand(demand);
        return incident;
    }

    /**
     * Create a test incident with the given ID.
     *
     * @param id ID of the incident and its location
     * @return incident with an ID only
     */
    public static PlanningIncident testVisit(long id) {
        return fromLocation(PlanningLocationFactory.testLocation(id));
    }
}
