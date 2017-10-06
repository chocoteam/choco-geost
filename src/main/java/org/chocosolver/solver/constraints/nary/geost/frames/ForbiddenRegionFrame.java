/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.frames;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 4 fevr. 2009
 * Time: 10:41:36
 * To change this template use File | Settings | File Templates.
 */
public final class ForbiddenRegionFrame extends Frame {
    public int q, D, s1, s2, o1, o2;

    public ForbiddenRegionFrame(int q_, int D_, int s1_, int s2_, int o1_, int o2_) {
        super();
        q = q_;
        D = D_;
        s1 = s1_;
        s2 = s2_;
        o1 = o1_;
        o2 = o2_;
    }

    public String toString() {
        return "q:" + q + ";D:" + D + ";s1:" + s1 + ";s2:" + s2 + ";o1:" + o1 + ";o2:" + o2;

    }

}
