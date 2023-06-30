/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.GeostBuilder;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.NonOverlapping;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.search.strategy.Search;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.util.tools.ArrayUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by cprudhom on 21/07/15. Project: choco-geost.
 */
public class TetrisTest {

    @Test(groups = "1s")
    public void test1a() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 2}, new int[]{2, 1}));

        Model model = new Model();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = model.intVar("sid_1", 1, 1, false);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = model.intVar("X_1", 2, 2, false);
        coords1[1] = model.intVar("Y_1", 1, 1, false);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, model.intVar(1), model.intVar(1), model.intVar(1)));

        IntVar shapeId2 = model.intVar("sid_2", 2, 2, false);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = model.intVar("X_2", 1, 1, false);
        coords2[1] = model.intVar("Y_2", 1, 1, false);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, model.intVar(1), model.intVar(1), model.intVar(1)));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.array(1, 2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = GeostBuilder.geost(2, objects, shapes, ectr2);
        model.post(geost);
        Solver solver = model.getSolver();
        solver.showDecisions();
        solver.setSearch(Search.inputOrderLBSearch(coords1[0], coords2[0]));
        solver.findAllSolutions();
        Assert.assertEquals(solver.getSolutionCount(), 0);

    }

    @Test(groups = "1s")
    public void test1b() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{2, 1}));

        Model model = new Model();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = model.intVar("sid_1", 1, 1, false);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = model.intVar("X_1", 2, 2, false);
        coords1[1] = model.intVar("Y_1", 1, 1, false);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, model.intVar(1), model.intVar(1), model.intVar(1)));

        IntVar shapeId2 = model.intVar("sid_2", 2, 2, false);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = model.intVar("X_2", 1, 1, false);
        coords2[1] = model.intVar("Y_2", 3, 3, false);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, model.intVar(1), model.intVar(1), model.intVar(1)));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.array(1, 2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = GeostBuilder.geost(2, objects, shapes, ectr2);
        model.post(geost);
        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(coords1[0], coords2[0]));
        solver.findAllSolutions();
        Assert.assertEquals(solver.getSolutionCount(), 0);

    }

    @Test(groups = "1s")
    public void test1c() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{2, 1}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(2, new int[]{0, 2}, new int[]{2, 1}));

        Model model = new Model();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = model.intVar("sid_1", 1, 1, false);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = model.intVar("X_1", 1, 2, false);
        coords1[1] = model.intVar("Y_1", 1, 1, false);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, model.intVar(1), model.intVar(1), model.intVar(1)));

        IntVar shapeId2 = model.intVar("sid_2", 2, 2, false);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = model.intVar("X_2", 1, 2, false);
        coords2[1] = model.intVar("Y_2", 1, 1, false);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, model.intVar(1), model.intVar(1), model.intVar(1)));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.array(1, 2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = GeostBuilder.geost(2, objects, shapes, ectr2);
        model.post(geost);
        Solver solver = model.getSolver();
        solver.setSearch(Search.inputOrderLBSearch(coords1[0], coords2[0]));
        solver.findAllSolutions();
        Assert.assertEquals(solver.getSolutionCount(), 1);

    }

    @Test(groups = "1s")
    public void test2() {
        List<ShiftedBox> shapes = new ArrayList<>();
        shapes.add(new ShiftedBox(1, new int[]{0, 0}, new int[]{1, 3}));
        shapes.add(new ShiftedBox(1, new int[]{-1, 2}, new int[]{1, 1}));
//        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{1, 2}));
        shapes.add(new ShiftedBox(2, new int[]{0, 0}, new int[]{2, 1}));

        Model model = new Model();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = model.intVar("sid_1", 1, 1, false);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = model.intVar("X_1", 2, 2, false);
        coords1[1] = model.intVar("Y_1", 1, 1, false);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, model.intVar(1), model.intVar(1), model.intVar(1)));

        IntVar shapeId2 = model.intVar("sid_2", 2, 2, false);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = model.intVar("X_2", 1, 1, false);
        coords2[1] = model.intVar("Y_2", 3, 3, false);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, model.intVar(1), model.intVar(1), model.intVar(1)));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.array(1, 2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = GeostBuilder.geost(2, objects, shapes, ectr2);
        model.post(geost);
        Solver solver = model.getSolver();
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

        Model model = new Model();
        List<GeostObject> objects = new ArrayList<>();
        IntVar shapeId1 = model.intVar("sid_1", 1, 1, false);
        IntVar[] coords1 = new IntVar[2];
        coords1[0] = model.intVar("X_1", 1, 2, false);
        coords1[1] = model.intVar("Y_1", 1, 1, false);
        objects.add(new GeostObject(2, 1, shapeId1, coords1, model.intVar(1), model.intVar(1), model.intVar(1)));

        IntVar shapeId2 = model.intVar("sid_2", 2, 2, false);
        IntVar[] coords2 = new IntVar[2];
        coords2[0] = model.intVar("X_2", 1, 2, false);
        coords2[1] = model.intVar("Y_2", 1, 1, false);
        objects.add(new GeostObject(2, 2, shapeId2, coords2, model.intVar(1), model.intVar(1), model.intVar(1)));

        List<ExternalConstraint> ectr2 = new ArrayList<>();

        int[] objOfEctr2 = new int[objects.size()];
        for (int d = 0; d < objects.size(); d++) {
            objOfEctr2[d] = objects.get(d).getObjectId();
        }

        NonOverlapping n2 = new NonOverlapping(Constants.NON_OVERLAPPING, ArrayUtils.array(1, 2), objOfEctr2);
        ectr2.add(n2);

        Constraint geost = GeostBuilder.geost(2, objects, shapes, ectr2);
        model.post(geost);
        Solver solver = model.getSolver();
        solver.findAllSolutions();
        Assert.assertEquals(solver.getMeasures().getSolutionCount(), 1);

    }
}
