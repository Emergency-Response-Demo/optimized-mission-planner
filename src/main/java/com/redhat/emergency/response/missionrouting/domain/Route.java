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

package com.redhat.emergency.response.missionrouting.domain;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Vehicle's itinerary (sequence of incidents) and its evacuationCenter. This entity cannot exist without the vehicle and the evacuationCenter
 * but it's allowed to have no incidents when the vehicle hasn't been assigned any (it's idle).
 * <p>
 * This entity describes part of a {@link RoutingPlan solution} of the vehicle routing problem
 * (assignment of a subset of incidents to one of the vehicles).
 * It doesn't carry the data about physical tracks between adjacent incidents.
 * Geographical data is held by {@link RouteWithTrack}.
 */
public class Route {

    private final Vehicle vehicle;
    private final Location evacuationCenter;
    private final List<Location> incidents;

    /**
     * Create a vehicle route.
     *
     * @param vehicle the vehicle assigned to this route (not {@code null})
     * @param evacuationCenter vehicle's evacuationCenter (not {@code null})
     * @param incidents list of incidents (not {@code null})
     */
    public Route(Vehicle vehicle, Location evacuationCenter, List<Location> incidents) {
        this.vehicle = Objects.requireNonNull(vehicle);
        this.evacuationCenter = Objects.requireNonNull(evacuationCenter);
        this.incidents = new ArrayList<>(Objects.requireNonNull(incidents));
        // TODO Probably remove this check when we have more types: new Route(Depot evacuationCenter, List<Visit> incidents).
        //      Then incidents obviously cannot contain the evacuationCenter. But will we still require that no visit has the same
        //      location as the evacuationCenter? (I don't think so).
        if (incidents.contains(evacuationCenter)) {
            throw new IllegalArgumentException("Depot (" + evacuationCenter + ") must not be one of the incidents (" + incidents + ")");
        }
        long uniqueVisits = incidents.stream().distinct().count();
        if (uniqueVisits < incidents.size()) {
            long duplicates = incidents.size() - uniqueVisits;
            throw new IllegalArgumentException("Some incidents have been visited multiple times (" + duplicates + ")");
        }
    }

    /**
     * The vehicle assigned to this route.
     *
     * @return route's vehicle (never {@code null})
     */
    public Vehicle vehicle() {
        return vehicle;
    }

    /**
     * Depot in which the route starts and ends.
     *
     * @return route's evacuationCenter (never {@code null})
     */
    public Location evacuationCenter() {
        return evacuationCenter;
    }

    /**
     * List of vehicle's incidents (not including the evacuationCenter).
     *
     * @return list of incidents
     */
    public List<Location> incidents() {
        return Collections.unmodifiableList(incidents);
    }

    @Override
    public String toString() {
        return "Route{" +
                "vehicle=" + vehicle +
                ", evacuationCenter=" + evacuationCenter.id() +
                ", incidents=" + incidents.stream().map(Location::id).collect(toList()) +
                '}';
    }
}
