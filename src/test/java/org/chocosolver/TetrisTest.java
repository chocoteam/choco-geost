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

import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.NonOverlapping;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.search.strategy.ISF;
import org.chocosolver.solver.trace.Chatterbox;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.VF;
import org.chocosolver.util.tools.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cprudhom on 21/07/15.
 * Project: choco-geost.
 */
public class TetrisTest {

    @Test(groups = "1s")
    public void test1a() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 2}, new int[]{2, 1}));

        Solver solver = new Solver();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = VF.enumerated("sid_1", 1, 1, solver);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = VF.enumerated("X_1", 2, 2, solver);
        coords1[1] = VF.enumerated("Y_1", 1, 1, solver);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, solver.ONE(), solver.ONE(), solver.ONE()));

        IntVar shapeId2 = VF.enumerated("sid_2", 2, 2, solver);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = VF.enumerated("X_2", 1, 1, solver);
        coords2[1] = VF.enumerated("Y_2", 1, 1, solver);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, solver.ONE(), solver.ONE(), solver.ONE()));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.oneToN(2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(2, objects, shapes, ectr2);
        solver.post(geost);
        solver.set(ISF.lexico_LB(coords1[0], coords2[0]));
        Chatterbox.showDecisions(solver);
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 0);

    }

    @Test(groups = "1s")
    public void test1b() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{2, 1}));

        Solver solver = new Solver();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = VF.enumerated("sid_1", 1, 1, solver);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = VF.enumerated("X_1", 2, 2, solver);
        coords1[1] = VF.enumerated("Y_1", 1, 1, solver);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, solver.ONE(), solver.ONE(), solver.ONE()));

        IntVar shapeId2 = VF.enumerated("sid_2", 2, 2, solver);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = VF.enumerated("X_2", 1, 1, solver);
        coords2[1] = VF.enumerated("Y_2", 3, 3, solver);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, solver.ONE(), solver.ONE(), solver.ONE()));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.oneToN(2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(2, objects, shapes, ectr2);
        solver.post(geost);
        solver.set(ISF.lexico_LB(coords1[0], coords2[0]));
        Chatterbox.showDecisions(solver);
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 0);

    }

    @Test(groups = "1s")
    public void test1c() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(2, new int[]{0, 2}, new int[]{2, 1}));

        Solver solver = new Solver();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = VF.enumerated("sid_1", 1, 1, solver);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = VF.enumerated("X_1", 1, 2, solver);
        coords1[1] = VF.enumerated("Y_1", 1, 1, solver);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, solver.ONE(), solver.ONE(), solver.ONE()));

        IntVar shapeId2 = VF.enumerated("sid_2", 2, 2, solver);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = VF.enumerated("X_2", 1, 2, solver);
        coords2[1] = VF.enumerated("Y_2", 1, 1, solver);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, solver.ONE(), solver.ONE(), solver.ONE()));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.oneToN(2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(2, objects, shapes, ectr2);
        solver.post(geost);
        solver.set(ISF.lexico_LB(coords1[0], coords2[0]));
        Chatterbox.showDecisions(solver);
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 1);

    }

    @Test(groups = "1s")
    public void test2() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 3}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{1, 1}));
//        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{2, 1}));

        Solver solver = new Solver();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = VF.enumerated("sid_1", 1, 1, solver);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = VF.enumerated("X_1", 2, 2, solver);
        coords1[1] = VF.enumerated("Y_1", 1, 1, solver);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, solver.ONE(), solver.ONE(), solver.ONE()));

        IntVar shapeId2 = VF.enumerated("sid_2", 2, 2, solver);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = VF.enumerated("X_2", 1, 1, solver);
        coords2[1] = VF.enumerated("Y_2", 3, 3, solver);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, solver.ONE(), solver.ONE(), solver.ONE()));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.oneToN(2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(2, objects, shapes, ectr2);
        solver.post(geost);
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 1);

    }


    @Test(groups = "1s")
    public void test3() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{1, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{0, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(2, new int[]{0, 2}, new int[]{2, 1}));

        Solver solver = new Solver();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = VF.enumerated("sid_1", 1, 1, solver);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = VF.enumerated("X_1", 1, 2, solver);
        coords1[1] = VF.enumerated("Y_1", 1, 1, solver);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, solver.ONE(), solver.ONE(), solver.ONE()));

        IntVar shapeId2 = VF.enumerated("sid_2", 2, 2, solver);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = VF.enumerated("X_2", 1, 2, solver);
        coords2[1] = VF.enumerated("Y_2", 1, 1, solver);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, solver.ONE(), solver.ONE(), solver.ONE()));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.oneToN(2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(2, objects, shapes, ectr2);
        solver.post(geost);
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 1);

    }

    @Test(groups = "1s")
    public void test4() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{1, 0}, new int[]{1, 3}));
        shapes.add(new ShiftedBox(1, new int[]{0, 2}, new int[]{1, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(2, new int[]{0, 2}, new int[]{2, 1}));

        Solver solver = new Solver();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = VF.enumerated("sid_1", 1, 1, solver);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = VF.enumerated("X_1", 1, 2, solver);
        coords1[1] = VF.enumerated("Y_1", 1, 1, solver);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, solver.ONE(), solver.ONE(), solver.ONE()));

        IntVar shapeId2 = VF.enumerated("sid_2", 2, 2, solver);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = VF.enumerated("X_2", 1, 2, solver);
        coords2[1] = VF.enumerated("Y_2", 1, 1, solver);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, solver.ONE(), solver.ONE(), solver.ONE()));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.oneToN(2), objOfEctr2);
//        ectr2.add(new DistGeq(Constants.DIST_GEQ));
        ectr2.add(n2);

        Constraint geost = Tutorial.geost(2, objects, shapes, ectr2);
        solver.post(geost);
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 1);

    }
}