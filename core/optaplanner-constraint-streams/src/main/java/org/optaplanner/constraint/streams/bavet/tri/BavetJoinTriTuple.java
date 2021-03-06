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

package org.optaplanner.constraint.streams.bavet.tri;

import java.util.ArrayList;
import java.util.List;

import org.optaplanner.constraint.streams.bavet.bi.BavetJoinBridgeBiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinTuple;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniTuple;

public final class BavetJoinTriTuple<A, B, C> extends BavetAbstractTriTuple<A, B, C>
        implements BavetJoinTuple {

    private final BavetJoinTriNode<A, B, C> node;
    private final BavetJoinBridgeBiTuple<A, B> abTuple;
    private final BavetJoinBridgeUniTuple<C> cTuple;
    private final List<BavetAbstractTuple> childTupleList = new ArrayList<>(1);

    public BavetJoinTriTuple(BavetJoinTriNode<A, B, C> node,
            BavetJoinBridgeBiTuple<A, B> abTuple, BavetJoinBridgeUniTuple<C> cTuple) {
        this.node = node;
        this.abTuple = abTuple;
        this.cTuple = cTuple;
    }

    @Override
    public String toString() {
        return "Join(" + getFactsString() + ")";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public BavetJoinTriNode<A, B, C> getNode() {
        return node;
    }

    @Override
    public List<BavetAbstractTuple> getChildTupleList() {
        return childTupleList;
    }

    @Override
    public A getFactA() {
        return abTuple.getFactA();
    }

    @Override
    public B getFactB() {
        return abTuple.getFactB();
    }

    @Override
    public C getFactC() {
        return cTuple.getFactA();
    }

    public BavetJoinBridgeBiTuple<A, B> getAbTuple() {
        return abTuple;
    }

    public BavetJoinBridgeUniTuple<C> getCTuple() {
        return cTuple;
    }

}
