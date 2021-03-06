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

package org.optaplanner.constraint.streams.bavet.uni;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintSession;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiNode;
import org.optaplanner.constraint.streams.bavet.bi.BavetGroupBiTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.core.api.score.stream.uni.UniConstraintCollector;

public final class BavetGroupBridgeUniNode<A, NewA, ResultContainer_, NewB> extends BavetAbstractUniNode<A> {

    private final BavetAbstractUniNode<A> parentNode;
    private final Function<A, NewA> groupKeyMapping;
    private final UniConstraintCollector<A, ResultContainer_, NewB> collector;
    private final Map<NewA, BavetGroupBiTuple<NewA, ResultContainer_, NewB>> tupleMap;
    private BavetGroupBiNode<NewA, ResultContainer_, NewB> groupNode;

    public BavetGroupBridgeUniNode(BavetConstraintSession session, int nodeIndex, BavetAbstractUniNode<A> parentNode,
            Function<A, NewA> groupKeyMapping, UniConstraintCollector<A, ResultContainer_, NewB> collector) {
        super(session, nodeIndex);
        this.parentNode = parentNode;
        this.groupKeyMapping = groupKeyMapping;
        this.collector = collector;
        tupleMap = new HashMap<>();
    }

    @Override
    public List<BavetAbstractUniNode<A>> getChildNodeList() {
        return Collections.emptyList();
    }

    @Override
    public BavetGroupBridgeUniTuple<A, NewA, ResultContainer_, NewB> createTuple(BavetAbstractUniTuple<A> parentTuple) {
        return new BavetGroupBridgeUniTuple<>(this, parentTuple);
    }

    public void setGroupNode(BavetGroupBiNode<NewA, ResultContainer_, NewB> groupNode) {
        this.groupNode = groupNode;
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        if (groupNode == null) {
            throw new IllegalStateException("Impossible state: GroupBridgeNode (" + this +
                    ") has no child GroupNode (" + groupNode + ").");
        }
        BavetGroupBridgeUniTuple<A, NewA, ResultContainer_, NewB> tuple =
                (BavetGroupBridgeUniTuple<A, NewA, ResultContainer_, NewB>) uncastTuple;
        if (tuple.getChildTuple() != null) {
            BavetGroupBiTuple<NewA, ResultContainer_, NewB> childTuple = tuple.getChildTuple();
            NewA oldGroupKey = childTuple.getGroupKey();
            int parentCount = childTuple.decreaseParentCount();
            tuple.getUndoAccumulator().run();
            childTuple.clearResult();
            tuple.setChildTuple(null);
            tuple.setUndoAccumulator(null);
            if (parentCount == 0) {
                // Clean up tupleMap
                tupleMap.remove(oldGroupKey);
                session.transitionTuple(childTuple, BavetTupleState.DYING);
            } else {
                session.transitionTuple(childTuple, BavetTupleState.UPDATING);
            }
        }
        if (tuple.isActive()) {
            A a = tuple.getFactA();
            NewA groupKey = groupKeyMapping.apply(a);
            BavetGroupBiTuple<NewA, ResultContainer_, NewB> childTuple = tupleMap.computeIfAbsent(groupKey,
                    k -> groupNode.createTuple(groupKey, collector.supplier().get()));
            int parentCount = childTuple.increaseParentCount();

            Runnable undoAccumulator = collector.accumulator().apply(childTuple.getResultContainer(), a);
            tuple.setUndoAccumulator(undoAccumulator);
            childTuple.clearResult();
            tuple.setChildTuple(childTuple);
            if (parentCount == 1) {
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            } else {
                // It might have just been created by an earlier tuple in the same nodeIndex
                if (childTuple.getState() != BavetTupleState.CREATING) {
                    session.transitionTuple(childTuple, BavetTupleState.UPDATING);
                }
            }
        }
    }

    @Override
    public String toString() {
        return "GroupBridge()";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
