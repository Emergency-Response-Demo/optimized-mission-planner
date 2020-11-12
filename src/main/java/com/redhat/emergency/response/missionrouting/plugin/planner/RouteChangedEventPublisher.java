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

package com.redhat.emergency.response.missionrouting.plugin.planner;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import com.redhat.emergency.response.missionrouting.domain.Distance;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningEvacuationCenter;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningVehicle;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningIncident;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.MissionRoutingSolution;
import com.redhat.emergency.response.missionrouting.service.route.RouteChangedEvent;
// import com.redhat.emergency.response.missionrouting.service.route.RouteChangedEvent;
import com.redhat.emergency.response.missionrouting.service.route.ShallowRoute;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Converts planning solution to a {@link RouteChangedEvent} and publishes it so that it can be processed by other
 * components that listen for this type of event.
 */
@ApplicationScoped
class RouteChangedEventPublisher {

    private static final Logger logger = LoggerFactory.getLogger(RouteChangedEventPublisher.class);

    // private final ApplicationEventPublisher eventPublisher;

    // @Autowired
    // RouteChangedEventPublisher(ApplicationEventPublisher eventPublisher) {
    //     this.eventPublisher = eventPublisher;
    // }

    /**
     * Publish solution as a {@link RouteChangedEvent}.
     *
     * @param solution solution
     */
    void publishSolution(MissionRoutingSolution solution) {
        RouteChangedEvent event = solutionToEvent(solution, this);
        logger.info(
                "New solution with {} evacuationCenters, {} vehicles, {} visits, distance: {}, score: {}",
                solution.getEvacuationCenterList().size(),
                solution.getVehicleList().size(),
                solution.getIncidentList().size(),
                event.distance(),
                solution.getScore());

        logger.debug("Solution: {}", solution);
        logger.debug("Routes: {}", event.routes());
        // eventPublisher.publishEvent(event);
    }

    /**
     * Convert a planning domain solution to an event that can be published.
     *
     * @param solution solution
     * @param source source of the event
     * @return new event describing the solution
     */
    static RouteChangedEvent solutionToEvent(MissionRoutingSolution solution, Object source) {
        List<ShallowRoute> routes = routes(solution);
        return new RouteChangedEvent(
                source,
                // Turn negative soft score into a positive amount of time.
                Distance.ofMillis(-solution.getScore().getSoftScore()),
                vehicleIds(solution),
                evacuationCenterId(solution),
                incidentsIds(solution),
                routes);
    }

    private static List<Long> incidentsIds(MissionRoutingSolution solution) {
        return solution.getIncidentList().stream()
                .map(visit -> visit.getLocation().getId())
                .collect(toList());
    }

    /**
     * Extract routes from the solution. Includes empty routes of vehicles that stay in the evacuationCenter.
     *
     * @param solution solution
     * @return one route per vehicle
     */
    private static List<ShallowRoute> routes(MissionRoutingSolution solution) {
        // TODO include unconnected customers in the result
        if (solution.getEvacuationCenterList().isEmpty()) {
            return Collections.emptyList();
        }
        ArrayList<ShallowRoute> routes = new ArrayList<>();
        for (PlanningVehicle vehicle : solution.getVehicleList()) {
            PlanningEvacuationCenter evacuationCenter = vehicle.getEvacuationCenter();
            if (evacuationCenter == null) {
                throw new IllegalArgumentException(
                        "Vehicle (id=" + vehicle.getId() + ") is not in the evacuationCenter. That's not allowed");
            }
            List<Long> visits = new ArrayList<>();
            for (PlanningIncident incident : vehicle.getFutureIncidents()) {
                if (!solution.getIncidentList().contains(incident)) {
                    throw new IllegalArgumentException("Incident (" + incident + ") doesn't exist");
                }
                visits.add(incident.getLocation().getId());
            }
            routes.add(new ShallowRoute(vehicle.getId(), evacuationCenter.getId(), visits));
        }
        return routes;
    }

    /**
     * Get IDs of vehicles in the solution.
     *
     * @param solution the solution
     * @return vehicle IDs
     */
    private static List<Long> vehicleIds(MissionRoutingSolution solution) {
        return solution.getVehicleList().stream()
                .map(PlanningVehicle::getId)
                .collect(toList());
    }

    /**
     * Get solution's evacuationCenter ID.
     *
     * @param solution the solution in which to look for the evacuationCenter
     * @return first evacuationCenter ID from the solution or {@code null} if there are no evacuationCenters
     */
    private static Long evacuationCenterId(MissionRoutingSolution solution) {
        return solution.getEvacuationCenterList().isEmpty() ? null : solution.getEvacuationCenterList().get(0).getId();
    }
}
