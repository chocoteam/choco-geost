/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.geometricPrim;

import org.chocosolver.memory.IStateInt;

import static java.lang.System.arraycopy;


public final class Witness {

    private IStateInt[] coords;
    private int dim;

    public Witness(int dim) {    //creates a point at the origin of the coordinate base.
        this.dim = dim;
        coords = new IStateInt[this.dim];
        for (int i = 0; i < this.dim; i++) {
            this.coords[i].set(0);
        }
    }

    public Witness(Witness w) {
        //creates a point from another point.
        coords = new IStateInt[this.dim];
        for (int i = 0; i < w.getCoords().length; i++) {
            this.coords[i].set(w.getCoord(i));
        }
    }

    public IStateInt[] getCoords() {
        return this.coords;
    }

    public void setCoords(IStateInt coordinates[]) {
        arraycopy(coordinates, 0, this.coords, 0, coordinates.length);
    }

    public int getCoord(int index) {
        return this.coords[index].get();
    }

    public void setCoord(int index, int value) {
        this.coords[index].set(value);
    }

}
