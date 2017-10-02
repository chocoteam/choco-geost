/*
 * Copyright (c) 1999-2015, Ecole des Mines de Nantes
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Ecole des Mines de Nantes nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE REGENTS AND CONTRIBUTORS ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE REGENTS AND CONTRIBUTORS BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.chocosolver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.NonOverlapping;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cprudhom on 20/07/15.
 * Project: choco-geost.
 */
public class Tetris {

    List<Shape> SHAPES = new ArrayList<>();

    public static void main(String[] args) {
//        new Tetris(new int[]{3, 2, 4, 1, 1, 2, 3}, 7, 7);
        new Tetris(new int[]{0, 0, 0, 0, 0, 1, 1}, 2, 1);
    }


    public Tetris(int[] qts, int X, int Y) {
        SHAPES.add(new I1());
        SHAPES.add(new I2());
        SHAPES.add(new O());
        SHAPES.add(new T1());
        SHAPES.add(new T2());
        SHAPES.add(new T3());
        SHAPES.add(new T4());
        SHAPES.add(new S1());
        SHAPES.add(new S2());
        SHAPES.add(new Z1());
        SHAPES.add(new Z2());
        SHAPES.add(new J1());
        SHAPES.add(new J2());
        SHAPES.add(new J3());
        SHAPES.add(new J4());
        SHAPES.add(new L1());
        SHAPES.add(new L2());
        SHAPES.add(new L3());
        SHAPES.add(new L4());


        List<ShiftedBox> shapes = new ArrayList<>();
        int sid = 0;
        for (Shape s : SHAPES) {
            sid++;
            for (int i = 0; i < s.boxes.size(); i++) {
                shapes.add(new ShiftedBox(sid, s.offsets.get(i), s.boxes.get(i)));
            }
        }


        System.out.printf("%d\n", SHAPES.size());
        System.out.printf("rect_size = \n[");
        for (Shape s : SHAPES) {
            for (int i = 0; i < s.boxes.size(); i++) {
                System.out.printf("| %d, %d \n", s.boxes.get(i)[0], s.boxes.get(i)[1]);
            }
        }
        System.out.printf("|];\n\n");

        System.out.printf("rect_offset = \n[");
        for (Shape s : SHAPES) {
            for (int i = 0; i < s.boxes.size(); i++) {
                System.out.printf("| %d, %d \n", s.offsets.get(i)[0], s.offsets.get(i)[1]);
            }
        }
        System.out.printf("|];\n\n");

        List<int[]> shapeIds = new ArrayList<>();
        shapeIds.add(new int[]{1, 2}); // I
        shapeIds.add(new int[]{3, 3}); // O
        shapeIds.add(new int[]{4, 5, 6, 7}); // T
        shapeIds.add(new int[]{8, 9}); // S
        shapeIds.add(new int[]{/*10, */11}); // Z
        shapeIds.add(new int[]{/*12, 13,*/ 14/*, 15*/}); // J
        shapeIds.add(new int[]{/*16, 17, */18/*, 19*/}); // L




        Model model = new Model();

        int dim = 2;
        List<GeostObject> objects = new ArrayList<>();

        List<IntVar> coordinates = new ArrayList<>();
        List<IntVar> theShapes = new ArrayList<>();
        int k = 1;
        for (int j = 0; j < qts.length; j++) {
            for (int i = 0; i < qts[j]; i++) {
                IntVar shapeId = model.intVar("sid_" + k, shapeIds.get(j));
                theShapes.add(shapeId);
                IntVar[] coords = new IntVar[2];
                coords[0] = model.intVar("X_" + k, 1, X, false);
                coords[1] = model.intVar("Y_" + k, 1, Y, false);
                coordinates.add(coords[0]);
                coordinates.add(coords[1]);
                objects.add(new GeostObject(dim, k++, shapeId, coords, model.intVar(1), model.intVar(1), model.intVar(1)));
            }
        }

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.array(1,dim), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(dim, objects, shapes, ectr2);
        model.post(geost);

        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(coordinates.toArray(new IntVar[coordinates.size()])),
                Search.inputOrderLBSearch(theShapes.toArray(new IntVar[theShapes.size()])));
        solver.findAllSolutions();

    }

    private class Shape {

        List<int[]> boxes;
        List<int[]> offsets;

        public Shape() {
            boxes = new ArrayList<>();
            offsets = new ArrayList<>();
        }
    }

    private class I1 extends Shape {
        public I1() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{4, 1});
        }
    }

    private class I2 extends Shape {
        public I2() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 4});
        }
    }

    private class O extends Shape {
        public O() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{2, 2});
        }
    }

    private class L1 extends Shape {
        public L1() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{1, 0});
            boxes.add(new int[]{1, 1});
        }
    }

    private class L2 extends Shape {
        public L2() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 1});
            offsets.add(new int[]{0, 1});
            boxes.add(new int[]{3, 1});
        }
    }

    private class L3 extends Shape {
        public L3() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{-1, 2});
            boxes.add(new int[]{1, 1});
        }
    }

    private class L4 extends Shape {
        public L4() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{3, 1});
            offsets.add(new int[]{2, 1});
            boxes.add(new int[]{1, 1});
        }
    }

    private class J1 extends Shape {
        public J1() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 1});
            offsets.add(new int[]{1, 0});
            boxes.add(new int[]{1, 3});
        }
    }

    private class J2 extends Shape {
        public J2() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{0, 1});
            boxes.add(new int[]{1, 1});
        }
    }

    private class J3 extends Shape {
        public J3() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{1, 2});
            boxes.add(new int[]{1, 1});
        }
    }

    private class J4 extends Shape {
        public J4() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{2, -1});
            boxes.add(new int[]{1, 1});
        }
    }

    private class T1 extends Shape {
        public T1() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 1});
            offsets.add(new int[]{-1, 1});
            boxes.add(new int[]{3, 1});
        }
    }

    private class T2 extends Shape {
        public T2() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{3, 1});
            offsets.add(new int[]{1, 1});
            boxes.add(new int[]{1, 1});
        }
    }

    private class T3 extends Shape {
        public T3() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{1, 1});
            boxes.add(new int[]{1, 1});
        }
    }

    private class T4 extends Shape {
        public T4() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 3});
            offsets.add(new int[]{-1, 1});
            boxes.add(new int[]{1, 1});
        }
    }

    private class S1 extends Shape {
        public S1() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{2, 1});
            offsets.add(new int[]{1, 1});
            boxes.add(new int[]{2, 1});
        }
    }

    private class S2 extends Shape {
        public S2() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 2});
            offsets.add(new int[]{-1, 1});
            boxes.add(new int[]{1, 2});
        }
    }

    private class Z1 extends Shape {
        public Z1() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{2, 1});
            offsets.add(new int[]{-1, 1});
            boxes.add(new int[]{2, 1});
        }
    }

    private class Z2 extends Shape {
        public Z2() {
            offsets.add(new int[]{0, 0});
            boxes.add(new int[]{1, 2});
            offsets.add(new int[]{1, 1});
            boxes.add(new int[]{1, 2});
        }
    }
}
