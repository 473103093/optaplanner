/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.cheaptime.optional.score;

import static java.util.Comparator.comparing;
import static java.util.Comparator.comparingLong;

import java.util.Comparator;
import java.util.Objects;

import org.optaplanner.examples.cheaptime.domain.Machine;

public class IdleCost implements Comparable<IdleCost> {

    private static final Comparator<IdleCost> COMPARATOR = comparing(IdleCost::getMachine, comparingLong(Machine::getId))
            .thenComparingInt(IdleCost::getActivePeriodAfterIdle)
            .thenComparingLong(IdleCost::getCost);

    private final Machine machine;
    private final int activePeriodAfterIdle;
    private final long cost;

    public IdleCost(Machine machine, int activePeriodAfterIdle, long cost) {
        this.machine = machine;
        this.activePeriodAfterIdle = activePeriodAfterIdle;
        this.cost = cost;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getActivePeriodAfterIdle() {
        return activePeriodAfterIdle;
    }

    public long getCost() {
        return cost;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IdleCost other = (IdleCost) o;
        return Objects.equals(machine, other.machine) &&
                activePeriodAfterIdle == other.activePeriodAfterIdle &&
                cost == other.cost;
    }

    @Override
    public int hashCode() {
        return Objects.hash(machine, activePeriodAfterIdle, cost);
    }

    @Override
    public int compareTo(IdleCost other) {
        return COMPARATOR.compare(this, other);
    }

    @Override
    public String toString() {
        return "machine = " + machine + ", activePeriodAfterIdle = " + activePeriodAfterIdle + ", cost = " + cost;
    }
}
