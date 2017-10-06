/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.internalConstraints;


import org.chocosolver.solver.constraints.nary.geost.Constants;

public class AvoidHoles extends InternalConstraint {

    public AvoidHoles() {
        super(Constants.AVOID_HOLES);
    }
}
