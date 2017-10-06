/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.externalConstraints;

public class NonOverlapping extends ExternalConstraint {

    public NonOverlapping(int ectrID, int[] dimensions, int[] objectIdentifiers) {
        super(ectrID, dimensions, objectIdentifiers);
    }

}
