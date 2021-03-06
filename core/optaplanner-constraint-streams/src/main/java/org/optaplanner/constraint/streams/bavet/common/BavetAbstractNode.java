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

package org.optaplanner.constraint.streams.bavet.common;

import org.optaplanner.constraint.streams.bavet.BavetConstraintSession;

public abstract class BavetAbstractNode implements BavetNode {

    protected final BavetConstraintSession session;
    protected final int nodeIndex;

    public BavetAbstractNode(BavetConstraintSession session, int nodeIndex) {
        this.session = session;
        this.nodeIndex = nodeIndex;
    }

    public abstract void refresh(BavetAbstractTuple tuple);

    // ************************************************************************
    // Getters/setters
    // ************************************************************************

    @Override
    public int getNodeIndex() {
        return nodeIndex;
    }

}
