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
import java.util.Set;

import org.optaplanner.constraint.streams.bavet.BavetConstraintSession;
import org.optaplanner.constraint.streams.bavet.common.BavetAbstractTuple;
import org.optaplanner.constraint.streams.bavet.common.BavetJoinNode;
import org.optaplanner.constraint.streams.bavet.common.BavetTupleState;
import org.optaplanner.constraint.streams.bavet.common.index.BavetIndex;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniNode;
import org.optaplanner.constraint.streams.bavet.uni.BavetJoinBridgeUniTuple;

public final class BavetJoinBiNode<A, B> extends BavetAbstractBiNode<A, B> implements BavetJoinNode {

    private final BavetJoinBridgeUniNode<A> leftParentNode;
    private final BavetJoinBridgeUniNode<B> rightParentNode;

    private final List<BavetAbstractBiNode<A, B>> childNodeList = new ArrayList<>();

    public BavetJoinBiNode(BavetConstraintSession session, int nodeIndex,
            BavetJoinBridgeUniNode<A> leftParentNode, BavetJoinBridgeUniNode<B> rightParentNode) {
        super(session, nodeIndex);
        this.leftParentNode = leftParentNode;
        this.rightParentNode = rightParentNode;
    }

    @Override
    public void addChildNode(BavetAbstractBiNode<A, B> childNode) {
        childNodeList.add(childNode);
    }

    @Override
    public List<BavetAbstractBiNode<A, B>> getChildNodeList() {
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
    public BavetJoinBiTuple<A, B> createTuple(BavetAbstractBiTuple<A, B> parentTuple) {
        throw new IllegalStateException("The join node (" + getClass().getSimpleName()
                + ") can't have a parentTuple (" + parentTuple + ");");
    }

    public BavetJoinBiTuple<A, B> createTuple(
            BavetJoinBridgeUniTuple<A> aTuple, BavetJoinBridgeUniTuple<B> bTuple) {
        return new BavetJoinBiTuple<>(this, aTuple, bTuple);
    }

    @Override
    public void refresh(BavetAbstractTuple uncastTuple) {
        BavetJoinBiTuple<A, B> tuple = (BavetJoinBiTuple<A, B>) uncastTuple;
        List<BavetAbstractTuple> childTupleList = tuple.getChildTupleList();
        for (BavetAbstractTuple childTuple : childTupleList) {
            session.transitionTuple(childTuple, BavetTupleState.DYING);
        }
        childTupleList.clear();
        if (tuple.isActive()) {
            for (BavetAbstractBiNode<A, B> childNode : childNodeList) {
                BavetAbstractBiTuple<A, B> childTuple = childNode.createTuple(tuple);
                childTupleList.add(childTuple);
                session.transitionTuple(childTuple, BavetTupleState.CREATING);
            }
        }
    }

    public void refreshChildTuplesLeft(BavetJoinBridgeUniTuple<A> leftParentTuple) {
        List<BavetAbstractTuple> leftTupleSet = leftParentTuple.getChildTupleList();
        for (BavetAbstractTuple tuple_ : leftTupleSet) {
            BavetJoinBiTuple<A, B> tuple = (BavetJoinBiTuple<A, B>) tuple_;
            boolean removed = tuple.getBTuple().getChildTupleList().remove(tuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactA()
                        + ")'s tuple cannot be removed from the other fact (" + tuple.getFactB()
                        + ")'s join bridge.");
            }
            session.transitionTuple(tuple, BavetTupleState.DYING);
        }
        leftTupleSet.clear();
        if (leftParentTuple.isActive()) {
            Set<BavetJoinBridgeUniTuple<B>> rightParentTupleList = getRightIndex().get(leftParentTuple.getIndexProperties());
            for (BavetJoinBridgeUniTuple<B> rightParentTuple : rightParentTupleList) {
                if (!rightParentTuple.isDirty()) {
                    BavetJoinBiTuple<A, B> childTuple = createTuple(leftParentTuple, rightParentTuple);
                    leftTupleSet.add(childTuple);
                    rightParentTuple.getChildTupleList().add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    public void refreshChildTuplesRight(BavetJoinBridgeUniTuple<B> rightParentTuple) {
        List<BavetAbstractTuple> rightTupleSet = rightParentTuple.getChildTupleList();
        for (BavetAbstractTuple uncastTuple : rightTupleSet) {
            BavetJoinBiTuple<A, B> tuple = (BavetJoinBiTuple<A, B>) uncastTuple;
            boolean removed = tuple.getATuple().getChildTupleList().remove(tuple);
            if (!removed) {
                throw new IllegalStateException("Impossible state: the fact (" + tuple.getFactB()
                        + ")'s tuple cannot be removed from the other fact (" + tuple.getFactA()
                        + ")'s join bridge.");
            }
            session.transitionTuple(tuple, BavetTupleState.DYING);
        }
        rightTupleSet.clear();
        if (rightParentTuple.isActive()) {
            Set<BavetJoinBridgeUniTuple<A>> leftParentTupleList = getLeftIndex().get(rightParentTuple.getIndexProperties());
            for (BavetJoinBridgeUniTuple<A> leftParentTuple : leftParentTupleList) {
                if (!leftParentTuple.isDirty()) {
                    BavetJoinBiTuple<A, B> childTuple = createTuple(leftParentTuple, rightParentTuple);
                    leftParentTuple.getChildTupleList().add(childTuple);
                    rightTupleSet.add(childTuple);
                    session.transitionTuple(childTuple, BavetTupleState.CREATING);
                }
            }
        }
    }

    public BavetIndex<BavetJoinBridgeUniTuple<A>> getLeftIndex() {
        return leftParentNode.getIndex();
    }

    public BavetIndex<BavetJoinBridgeUniTuple<B>> getRightIndex() {
        return rightParentNode.getIndex();
    }

    @Override
    public String toString() {
        return "Join() with " + childNodeList.size() + " children";
    }

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

}
