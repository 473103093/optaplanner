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

package org.optaplanner.constraint.streams.bavet.bi;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.constraint.streams.bavet.BavetConstraintSession;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;

public final class BavetGroupBiNode<GroupKey_, ResultContainer_, Result_> extends BavetAbstractBiNode<GroupKey_, Result_> {

    private final Function<ResultContainer_, Result_> finisher;

    private final List<BavetAbstractBiNode<GroupKey_, Result_>> childNodeList = new ArrayList<>();

    public BavetGroupBiNode(BavetConstraintSession session, int nodeIndex,
            Function<ResultContainer_, Result_> finisher) {
        super(session, nodeIndex);
        this.finisher = finisher;
    }

    @Override
    public void addChildNode(BavetAbstractBiNode<GroupKey_, Result_> childNode) {
        childNodeList.add(childNode);
    }

    @Override
    public List<BavetAbstractBiNode<GroupKey_, Result_>> getChildNodeList() {
        return childNodeList;
    }

    // ************************************************************************
    // Equality for node sharing
    // ************************************************************************

    // TODO

    // ************************************************************************
    // Runtime
    // ************************************************************************

    @Override
    public BavetGroupBiTuple<GroupKey_, ResultContainer_, Result_> createTuple(
            BavetAbstractBiTuple<GroupKey_, Result_> parentTuple) {
        throw new IllegalStateException("The Grouped node (" + getClass().getSimpleName()
                + ") can't have a parentTuple (" + parentTuple + ");");
    }

    public BavetGroupBiTuple<GroupKey_, ResultContainer_, Result_> createTuple(
            GroupKey_ groupKey, ResultContainer_ resultContainer) {
        return new BavetGroupBiTuple<>(this, groupKey, resultContainer);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetGroupBiTuple<GroupKey_, ResultContainer_, Result_> tuple =
                (BavetGroupBiTuple<GroupKey_, ResultContainer_, Result_>) uncastTuple;
        List<BavetAbstractTuple> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractTuple childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            tuple.updateResult(finisher);
            for (BavetAbstractBiNode<GroupKey_, Result_> childNode : childNodeList) {
                BavetAbstractBiTuple<GroupKey_, Result_> childTuple = childNode.createTuple(tuple);
                childTupleList.add(childTuple);
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            }
        }
    }

    @Override
    public String toString() {
        return "Group() with " + childNodeList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
