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

package com.redhat.emergency.response.missionrouting.service.route;

import static java.util.stream.Collectors.joining;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Stream;

import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningEvacuationCenter;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningVehicle;

// TODO maybe remove this once we fork planning domain from optaplanner-examples
// because then we can hold a reference to the original location

/**
 * Lightweight route description consisting of vehicle and location IDs instead of entities.
 * This makes it easier to quickly construct and share result of route optimization
 * without converting planning domain objects to business domain objects.
 * Specifically, some information may be lost when converting business domain objects to planning domain
 * because it's not needed for optimization (e.g. location address)
 * and so it's impossible to reconstruct the original business object without looking into the repository.
 */
public class ShallowRoute {

    /**
     * Vehicle.
     */
    public final PlanningVehicle vehicle;
    /**
     * EvacuationCenter.
     */
    public final PlanningEvacuationCenter evacuationCenter;
    /**
     * Visit IDs (immutable, never {@code null}).
     */
    // public final List<Long> incidentIds;
    public final List<String> incidentIds;

    /**
     * Create shallow route.
     *
     * @param vehicle vehicle
     * @param evacuationCenter evacuationCenter
     * @param incidentIds incident IDs
     */
    public ShallowRoute(PlanningVehicle vehicle, PlanningEvacuationCenter evacuationCenter, List<String> incidentIds) {
        this.vehicle = vehicle;
        this.evacuationCenter = evacuationCenter;
        this.incidentIds = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(incidentIds)));
    }

    @Override
    public String toString() {
        String vehicleLocation = vehicle.getLocation().getDescription();
        String route = Stream.concat(Stream.of(vehicleLocation), incidentIds.stream())
                .map(Object::toString)
                // add the evacuationCenterId as the last "incident" in the trip
                .collect(joining("->", "[", " --> " + evacuationCenter.getLocation().getDescription() + "("+ evacuationCenter.getId() +")" + "]"));
        return "[" + vehicle.getId() + "]" + vehicle.getLocation().getDescription() + ": " + route;
    }
}
