/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:34Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.frames;

import java.util.Arrays;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 4 fevr. 2009
 * Time: 10:41:36
 * To change this template use File | Settings | File Templates.
 */
public final class DistLinearFrame extends Frame {
    public int[] a;
    public int o1, b;


    public DistLinearFrame(int[] a, int o1, int b) {
        super();
        this.a = a;
        this.o1 = o1;
        this.b = b;
    }

    public String toString() {
        return "a:" + Arrays.toString(a) + ";o1:" + o1 + ";b:" + b;
    }

}
