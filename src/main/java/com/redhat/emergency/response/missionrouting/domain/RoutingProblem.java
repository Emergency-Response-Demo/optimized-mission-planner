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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Definition of the vehicle routing problem instance.
 */
public class RoutingProblem {

    private final String name;
    private final List<VehicleData> vehicles;
    private final LocationData evacuationCenter;
    private final List<LocationData> incidents;

    /**
     * Create routing problem instance.
     *
     * @param name the instance name
     * @param vehicles list of vehicles (not {@code null})
     * @param evacuationCenter the evacuationCenter (may be {@code null} if there is no evacuationCenter)
     * @param incidents list of incidents (not {@code null})
     */
    public RoutingProblem(
            String name,
            List<? extends VehicleData> vehicles,
            LocationData evacuationCenter,
            List<? extends LocationData> incidents) {
        this.name = Objects.requireNonNull(name);
        this.vehicles = new ArrayList<>(Objects.requireNonNull(vehicles));
        this.evacuationCenter = evacuationCenter;
        this.incidents = new ArrayList<>(Objects.requireNonNull(incidents));
    }

    /**
     * Get routing problem instance name.
     *
     * @return routing problem instance name
     */
    public String name() {
        return name;
    }

    /**
     * Get the evacuationCenter.
     *
     * @return evacuationCenter (never {@code null})
     */
    public Optional<LocationData> evacuationCenter() {
        return Optional.ofNullable(evacuationCenter);
    }

    /**
     * Get locations that should be visited.
     *
     * @return incidents
     */
    public List<LocationData> incidents() {
        return incidents;
    }

    /**
     * Vehicles that are part of the problem definition.
     *
     * @return vehicles
     */
    public List<VehicleData> vehicles() {
        return vehicles;
    }
}
