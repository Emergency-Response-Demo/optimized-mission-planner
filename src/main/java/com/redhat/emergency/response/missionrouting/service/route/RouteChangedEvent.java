/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import com.redhat.emergency.response.missionrouting.domain.Distance;
// import org.springframework.context.ApplicationEvent;

/**
 * Event published when the routing plan has been updated either by discovering a better route or by a change
 * in the problem specification (vehicles, incidents).
 */
public class RouteChangedEvent { //extends ApplicationEvent {

    private final Distance distance;
    private final List<Long> vehicleIds;
    private final Long evacuationCenterId;
    private final List<Long> incidentIds;
    private final Collection<ShallowRoute> routes;

    /**
     * Create a new ApplicationEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     * @param distance total distance of all vehicle routes
     * @param vehicleIds vehicle IDs
     * @param evacuationCenterId evacuationCenter ID (may be {@code null} if there are no locations)
     * @param incidentIds IDs of incidents
     * @param routes vehicle routes
     */
    public RouteChangedEvent(
            Object source,
            Distance distance,
            List<Long> vehicleIds,
            Long evacuationCenterId,
            List<Long> incidentIds,
            Collection<ShallowRoute> routes) {
        // super(source);
        this.distance = Objects.requireNonNull(distance);
        this.vehicleIds = Objects.requireNonNull(vehicleIds);
        this.evacuationCenterId = evacuationCenterId; // may be null (no evacuationCenter)
        this.incidentIds = Objects.requireNonNull(incidentIds);
        this.routes = Objects.requireNonNull(routes);
    }

    /**
     * IDs of all vehicles.
     *
     * @return vehicle IDs
     */
    public List<Long> vehicleIds() {
        return vehicleIds;
    }

    /**
     * Routes of all vehicles.
     *
     * @return vehicle routes
     */
    public Collection<ShallowRoute> routes() {
        return routes;
    }

    /**
     * Routing plan distance.
     *
     * @return distance (never {@code null})
     */
    public Distance distance() {
        return distance;
    }

    /**
     * The evacuationCenter ID.
     *
     * @return evacuationCenter ID
     */
    public Optional<Long> evacuationCenterId() {
        return Optional.ofNullable(evacuationCenterId);
    }

    public List<Long> incidentIds() {
        return incidentIds;
    }
}
