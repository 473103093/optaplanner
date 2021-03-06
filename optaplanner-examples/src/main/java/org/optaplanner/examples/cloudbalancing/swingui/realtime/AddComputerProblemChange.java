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

package org.optaplanner.examples.cloudbalancing.swingui.realtime;

import java.util.ArrayList;

import org.optaplanner.core.api.solver.change.ProblemChange;
import org.optaplanner.core.api.solver.change.ProblemChangeDirector;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;

public class AddComputerProblemChange implements ProblemChange<CloudBalance> {

    private final CloudComputer computer;

    public AddComputerProblemChange(CloudComputer computer) {
        this.computer = computer;
    }

    @Override
    public void doChange(CloudBalance cloudBalance, ProblemChangeDirector problemChangeDirector) {
        // Set a unique id on the new computer
        long nextComputerId = 0L;
        for (CloudComputer otherComputer : cloudBalance.getComputerList()) {
            if (nextComputerId <= otherComputer.getId()) {
                nextComputerId = otherComputer.getId() + 1L;
            }
        }
        computer.setId(nextComputerId);
        // A SolutionCloner does not clone problem fact lists (such as computerList)
        // Shallow clone the computerList so only workingSolution is affected, not bestSolution or guiSolution
        cloudBalance.setComputerList(new ArrayList<>(cloudBalance.getComputerList()));
        // Add the problem fact itself
        problemChangeDirector.addProblemFact(computer, cloudBalance.getComputerList()::add);
    }

}
