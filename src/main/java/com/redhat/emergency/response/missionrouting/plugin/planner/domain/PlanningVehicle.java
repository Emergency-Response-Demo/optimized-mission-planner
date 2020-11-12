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

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.domain.variable.PlanningVariable;

public class PlanningVehicle implements Standstill {

    @PlanningId
    private long id;
    private String description;
    private int capacity;
    @PlanningVariable(valueRangeProviderRefs = "evacuationCenter", nullable = false)
    private PlanningEvacuationCenter evacuationCenter;
    private PlanningLocation location;

    // Shadow variables
    private PlanningIncident nextIncident;

    PlanningVehicle() {
        // Hide public constructor in favor of the factory.
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getCapacity() {
        return capacity;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public PlanningEvacuationCenter getEvacuationCenter() {
        return evacuationCenter;
    }

    public void setEvacuationCenter(PlanningEvacuationCenter evacuationCenter) {
        this.evacuationCenter = evacuationCenter;
    }

    public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setLocation(PlanningLocation location) {
		this.location = location;
    }
    
    // @Override // ???
    // public PlanningLocation getLocation() {
    //     return evacuationCenter.getLocation();
    // }

    @Override
    public PlanningLocation getLocation() {
        return location;
    }

    @Override
    public PlanningIncident getNextIncident() {
        return nextIncident;
    }

    @Override
    public void setNextIncident(PlanningIncident nextIncident) {
        this.nextIncident = nextIncident;
    }

    public Iterable<PlanningIncident> getFutureIncidents() {
        return () -> new Iterator<PlanningIncident>() {
            PlanningIncident nextIncident = getNextIncident();

            @Override
            public boolean hasNext() {
                return nextIncident != null;
            }

            @Override
            public PlanningIncident next() {
                if (nextIncident == null) {
                    throw new NoSuchElementException();
                }
                PlanningIncident out = nextIncident;
                nextIncident = nextIncident.getNextIncident();
                return out;
            }
        };
    }

    @Override
    public String toString() {
        return "PlanningVehicle{" +
                "capacity=" + capacity +
                (location == null ? "" : ",location=[" + location.getId() + "]" + location.getDescription() ) +
                (evacuationCenter == null ? "" : ",evacuationCenter=[" + evacuationCenter.getLocation().getId() + "]" + evacuationCenter.getLocation().getDescription() ) +
                (nextIncident == null ? "" : ",nextIncident=" + nextIncident.getId()) +
                ",id=" + id +
                '}';
    }

}
