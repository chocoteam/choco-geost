/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

import org.chocosolver.solver.constraints.nary.geost.internalConstraints.InternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Point;


public final class HeapKey implements Heapable {

    private Point p;
    private int d; //the internal dimension
    private InternalConstraint ictr;
    private int k;//the total dimension of the problem

    public HeapKey(Point p, int d, int dim, InternalConstraint ictr) {
        this.p = p;
        this.d = d;
        this.ictr = ictr;
        this.k = dim;
    }


    public int getD() {
        return d;
    }


    public InternalConstraint getIctr() {
        return ictr;
    }


    public Point getP() {
        return p;
    }


    public void setD(int d) {
        this.d = d;
    }


    public void setIctr(InternalConstraint ictr) {
        this.ictr = ictr;
    }


    public void setP(Point p) {
        this.p = p;
    }


    public boolean equalTo(Object other) {
        int jPrime = 0;

        for (int j = 0; j < k; j++) {
            jPrime = (j + this.d) % k;
            if (p.getCoord(jPrime) != (((HeapKey) other).getP()).getCoord(jPrime)) {
                return false;
            }
        }
        return true;
    }

    public boolean greaterThan(Object other) {
        int jPrime = 0;

        for (int j = 0; j < k; j++) {
            jPrime = (j + d) % k;
            if (this.p.getCoord(jPrime) != (((HeapKey) other).getP()).getCoord(jPrime)) {
                return this.p.getCoord(jPrime) >= (((HeapKey) other).getP()).getCoord(jPrime);
            }
        }
        return false; //since they are equal
    }


    public boolean lessThan(Object other) {
        int jPrime = 0;

        for (int j = 0; j < k; j++) {
            jPrime = (j + d) % k;
            if (p.getCoord(jPrime) != (((HeapKey) other).getP()).getCoord(jPrime)) {
                return p.getCoord(jPrime) <= (((HeapKey) other).getP()).getCoord(jPrime);
            }
        }
        return false; //since they are equal
    }

}
