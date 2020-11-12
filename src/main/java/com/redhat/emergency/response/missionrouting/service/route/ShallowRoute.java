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
     * Vehicle ID.
     */
    public final long vehicleId;
    /**
     * Depot ID.
     */
    public final long evacuationCenterId;
    /**
     * Visit IDs (immutable, never {@code null}).
     */
    // public final List<Long> incidentIds;
    public final List<String> incidentIds;

    /**
     * Create shallow route.
     *
     * @param vehicleId vehicle ID
     * @param evacuationCenterId evacuationCenter ID
     * @param incidentIds incident IDs
     */
    public ShallowRoute(long vehicleId, long evacuationCenterId, List<String> incidentIds) {
        this.vehicleId = vehicleId;
        this.evacuationCenterId = evacuationCenterId;
        this.incidentIds = Collections.unmodifiableList(new ArrayList<>(Objects.requireNonNull(incidentIds)));
    }

    @Override
    public String toString() {
        String route = Stream.concat(Stream.of(evacuationCenterId), incidentIds.stream())
                .map(Object::toString)
                // add the evacuationCenterId as the last "incident" in the trip
                .collect(joining("->", "[", " --> " + evacuationCenterId + "]"));
        return vehicleId + ": " + route;
    }
}
