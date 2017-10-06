/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.geometricPrim;

import java.io.Serializable;
import java.util.Arrays;

/**
 * This is the class that represents a Shifted Box. Each shifted box belongs to a shape (therefore the shape id variable) and has two lists
 * that specify its offset (basically origin, lower left corner) and its size in every dimension.
 */
public class ShiftedBox implements Serializable {

    private int sid; //shape Id
    private int[] t; //the offset
    private int[] l; //the size

    public ShiftedBox(int shapeId, int[] offset, int[] size) {
        this.sid = shapeId;
        this.t = offset;
        this.l = size;
    }

    public ShiftedBox() {
    }

    public void setOffset(int index, int value) {
        this.t[index] = value;
    }

    public void setOffset(int[] off) {
        this.t = off;
    }

    public int getOffset(int index) {
        return this.t[index];
    }

    public int[] getOffset() {
        return this.t;
    }


    public void setSize(int index, int value) {
        this.l[index] = value;
    }

    public void setSize(int[] s) {
        this.l = s;
    }

    public int getSize(int index) {
        return this.l[index];
    }

    public int[] getSize() {
        return this.l;
    }

    public int getShapeId() {
        return this.sid;
    }

    public void setShapeId(int id) {
        this.sid = id;
    }

    public void print() {
        System.out.print("sid=" + sid + " ");
        for (int i = 0; i < t.length; i++) System.out.print("t[" + i + "]:" + t[i] + " ");
        for (int i = 0; i < l.length; i++) System.out.print("l[" + i + "]:" + l[i] + " ");
        System.out.println("");
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final ShiftedBox other = (ShiftedBox) obj;
        if (!Arrays.equals(l, other.l))
            return false;
        if (sid != other.sid)
            return false;
        if (!Arrays.equals(t, other.t))
            return false;
        return true;
    }


}
