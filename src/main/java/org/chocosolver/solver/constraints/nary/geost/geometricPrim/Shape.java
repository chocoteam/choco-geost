/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.geometricPrim;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the class that represents a Shape. Each shape has a shape Id and a set of shifted boxes.
 */
public final class Shape {

    private int shapeId;

    private List<ShiftedBox> sb;

    public Shape() {
        this.sb = new ArrayList<>();

    }

    public Shape(int id) {
        this.shapeId = id;
        this.sb = new ArrayList<>();
    }

    public List<ShiftedBox> getShiftedBoxes() {
        return this.sb;
    }

    public void setShiftedBoxes(List<ShiftedBox> sb) {
        this.sb = sb;
    }

    public void addShiftedBox(ShiftedBox sb) {
        this.sb.add(sb);
    }

    public ShiftedBox getShiftedBox(int index) {
        return this.sb.get(index);
    }

    public void removeShiftedBox(int index) {
        this.sb.remove(index);
    }

    public void removeShiftedBox(ShiftedBox sb) {
        this.sb.remove(sb);

    }

    public int getShapeId() {
        return this.shapeId;
    }

    public void setShapeId(int shapeId) {
        this.shapeId = shapeId;
    }


}
