/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.constraint;

import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.impl.testdata.util.PlannerAssert;

class ConstraintMatchTest {

    @Test
    void equalsAndHashCode() { // No CM should equal any other.
        ConstraintMatch<SimpleScore> constraintMatch =
                new ConstraintMatch<>("a.b", "c", Arrays.asList("e1"), SimpleScore.ZERO);
        PlannerAssert.assertObjectsAreEqual(constraintMatch, constraintMatch);
        ConstraintMatch<SimpleScore> constraintMatch2 =
                new ConstraintMatch<>("a.b", "c", Arrays.asList("e1"), SimpleScore.ZERO);
        // Cast do avoid Comparable checks.
        PlannerAssert.assertObjectsAreNotEqual(constraintMatch, (Object) constraintMatch2);
    }

    @Test
    void compareTo() {
        PlannerAssert.assertCompareToOrder(
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "aa"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "aa", "a"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "aa", "b"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "ab"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "a", Arrays.asList("a", "c"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "b", Arrays.asList("a", "aa"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "b", Arrays.asList("a", "ab"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.b", "b", Arrays.asList("a", "c"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.c", "a", Arrays.asList("a", "aa"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.c", "a", Arrays.asList("a", "ab"), SimpleScore.ZERO),
                new ConstraintMatch<>("a.c", "a", Arrays.asList("a", "c"), SimpleScore.ZERO));
    }

}
