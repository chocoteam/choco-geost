/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:34Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.externalConstraints;


import org.chocosolver.solver.constraints.nary.geost.frames.Frame;

/**
 * A class that all external constraints should extend. It contains info and functionality common to all external constraints.
 */
public class ExternalConstraint {

    protected int ectrID;
    protected int[] dim;
    protected int[] objectIds;
    protected Frame frame;
    protected static int maxId = 0;


    public ExternalConstraint() {
    }

    public ExternalConstraint(int ectrID, int[] dimensions, int[] objectIdentifiers) {
        this.ectrID = ectrID;
        this.dim = dimensions;
        this.objectIds = objectIdentifiers;
        this.frame = new Frame();
        //UpdateObjectsRelatedConstraintInfo();
    }

    /**
     * Gets the list of dimensions that an external constraint is active for.
     */
    public final int[] getDim() {
        return dim;
    }

    /**
     * Gets the external constraint ID
     */
    public final int getEctrID() {
        return ectrID;
    }

    /**
     * Gets the list of object IDs that this external constraint affects.
     */
    public final int[] getObjectIds() {
        return objectIds;
    }

    /**
     * Sets the list of dimensions that an external constraint is active for.
     */
    public final void setDim(int[] dim) {
        this.dim = dim;
    }

//	public void setEctrID(int ectrID) {
//		this.ectrID = ectrID;
//	}

    /**
     * Sets the list of object IDs that this external constraint affects.
     */
    public final void setObjectIds(int[] objectIds) {
        this.objectIds = objectIds;
    }

    /**
     * Gets the frame related to an external constraint
     */
    public final Frame getFrame() {
        return frame;
    }

    /**
     * Sets the frame related to an external constraint
     */
    public final void setFrame(Frame frame) {
        this.frame = frame;
    }


}
