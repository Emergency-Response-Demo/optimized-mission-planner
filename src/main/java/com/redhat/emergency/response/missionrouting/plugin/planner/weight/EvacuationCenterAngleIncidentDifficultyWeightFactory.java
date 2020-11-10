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

package com.redhat.emergency.response.missionrouting.plugin.planner.weight;

import static java.util.Comparator.comparingDouble;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionSorterWeightFactory;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningEvacuationCenter;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningLocation;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.PlanningIncident;
import com.redhat.emergency.response.missionrouting.plugin.planner.domain.MissionRoutingSolution;

/**
 * On large data sets, the constructed solution looks like pizza slices.
 * The order of the slices depends on the {@link PlanningLocation#angleTo} implementation.
 */
public class EvacuationCenterAngleIncidentDifficultyWeightFactory
        implements SelectionSorterWeightFactory<MissionRoutingSolution, PlanningIncident> {

    @Override
    public EvacuationCenterAngleIncidentDifficultyWeight createSorterWeight(MissionRoutingSolution solution, PlanningIncident incident) {
        PlanningEvacuationCenter evacuationCenter = solution.getEvacuationCenterList().get(0);
        return new EvacuationCenterAngleIncidentDifficultyWeight(
                incident,
                // angle of the line from incident to evacuationCenter relative to incident→east
                incident.getLocation().angleTo(evacuationCenter.getLocation()),
                incident.getLocation().distanceTo(evacuationCenter.getLocation())
                        + evacuationCenter.getLocation().distanceTo(incident.getLocation()));
    }

    static class EvacuationCenterAngleIncidentDifficultyWeight implements Comparable<EvacuationCenterAngleIncidentDifficultyWeight> {

        private static final Comparator<EvacuationCenterAngleIncidentDifficultyWeight> COMPARATOR =
                comparingDouble((EvacuationCenterAngleIncidentDifficultyWeight weight) -> weight.evacuationCenterAngle)
                        // Ascending (further from the evacuationCenter are more difficult)
                        .thenComparingLong(weight -> weight.evacuationCenterRoundTripDistance)
                        .thenComparing(weight -> weight.incident, comparingLong(PlanningIncident::getId));

        private final PlanningIncident incident;
        private final double evacuationCenterAngle;
        private final long evacuationCenterRoundTripDistance;

        EvacuationCenterAngleIncidentDifficultyWeight(PlanningIncident incident, double evacuationCenterAngle, long evacuationCenterRoundTripDistance) {
            this.incident = incident;
            this.evacuationCenterAngle = evacuationCenterAngle;
            this.evacuationCenterRoundTripDistance = evacuationCenterRoundTripDistance;
        }

        @Override
        public int compareTo(EvacuationCenterAngleIncidentDifficultyWeight other) {
            return COMPARATOR.compare(this, other);
        }

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof EvacuationCenterAngleIncidentDifficultyWeight)) {
                return false;
            }
            return compareTo((EvacuationCenterAngleIncidentDifficultyWeight) o) == 0;
        }

        @Override
        public int hashCode() {
            return Objects.hash(incident, evacuationCenterAngle, evacuationCenterRoundTripDistance);
        }

        @Override
        public String toString() {
            return "EvacuationCenterAngleIncidentDifficultyWeight{" +
                    "incident=" + incident +
                    ", evacuationCenterAngle=" + evacuationCenterAngle +
                    ", evacuationCenterRoundTripDistance=" + evacuationCenterRoundTripDistance +
                    '}';
        }
    }
}
