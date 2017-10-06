/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.util;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public final class RandomProblemGenerator {
    private List<GeostObject> objects;
    private List<ShiftedBox> sBoxes;
    private Model m;
    private int nbOfObjects;
    private int nbOfShapes;
    private int nbOfShiftedBoxes;
    private int maxLength;
    private int dim;
    private long seed;

    public RandomProblemGenerator(int k, int nbOfObjects, int nbOfShapes, int nbOfShiftedBoxes, int maxLength, long seed) {
        objects = new ArrayList<>();
        sBoxes = new ArrayList<>();
        m = new Model();
        this.nbOfObjects = nbOfObjects;
        this.nbOfShapes = nbOfShapes;
        this.nbOfShiftedBoxes = nbOfShiftedBoxes;
        this.maxLength = maxLength;
        this.dim = k;
        this.seed = seed;
    }

    public void generateProb() {
        generateRandomProblem(this.nbOfObjects, this.nbOfShapes, this.nbOfShiftedBoxes, this.maxLength, this.seed);
    }

    private void generateRandomProblem(int nbOfObjects, int nbOfShapes, int nbOfShiftedBoxes, int maxLength, long seed) {
        if (nbOfShapes > nbOfShiftedBoxes) {
            System.out.println("The number of shifted boxes should be greater or equal to the number of shapes");
            return;
        }


        Random rnd = new Random(seed);
        int[] maxDomain = new int[dim]; //maximum value of o.x in each dimension

        //first generate the shape IDs
        List<Integer> shapeIDS = new ArrayList<>();
        for (int i = 0; i < nbOfShapes; i++) {
            shapeIDS.add(i, i);
        }

        //generate the objects
        for (int i = 0; i < nbOfObjects; i++) {
            //create the shape id randomly from the list of shape ids
            int index = rnd.nextInt(shapeIDS.size());
            int sid = shapeIDS.get(index);
            shapeIDS.remove(index);


            IntVar shapeId = getModel().intVar("sid", sid);
            IntVar[] coords = new IntVar[dim];
            for (int j = 0; j < dim; j++) {

                int max = rnd.nextInt(maxLength);
                while (max == 0) {
                    max = rnd.nextInt(maxLength);
                }

                int min = rnd.nextInt(max);
                coords[j] = getModel().intVar("x" + j, min, max, false);
            }
            IntVar start = getModel().intVar("start", 1);
            IntVar duration = getModel().intVar("duration", 1);
            IntVar end = getModel().intVar("end", 1);
            objects.add(new GeostObject(this.dim, i, shapeId, coords, start, duration, end));
        }


        for (int i = 0; i < dim; i++) {
            int max = 0;
            for (int j = 0; j < objects.size(); j++) {
                if (max < objects.get(j).getCoordinates()[i].getUB())
                    max = objects.get(j).getCoordinates()[i].getUB();
            }

            maxDomain[i] = max;
        }


        //create the shifted boxes

        //create a shifted box for each shape at least
        //then the remaining shifted boxes create them in a correct manner
        //respecting the offset and the previous shifted box created

        for (int j = 0; j < nbOfShapes; j++) {
            int[] t = new int[dim];
            int[] s = new int[dim];
            for (int k = 0; k < dim; k++) {
                t[k] = 0; //for the time being all the shappes have no offset from the origin so the offset of this box should be 0 so that
                //any other box we add is above or to the right. this way we know that the origin of the shape is the bottom left corner
                s[k] = rnd.nextInt(maxLength);
                while (s[k] == 0) {
                    s[k] = rnd.nextInt(maxLength); //All boxes has a minimum size of in every dimension
                }
            }
            sBoxes.add(new ShiftedBox(j, t, s));
        }

        //repopulate the ShapeIDS Vector
        for (int j = 0; j < nbOfShapes; j++) {
            shapeIDS.add(j, j);
        }

        int remainingSBtoCreate = nbOfShiftedBoxes - nbOfShapes;
        while (remainingSBtoCreate > 0) {
            //pick a shape
            int index = rnd.nextInt(shapeIDS.size());
            int sid = shapeIDS.get(index);

            //get an already created shifted box for that shape
            for (int i = 0; i < sBoxes.size(); i++) {
                if (sBoxes.get(i).getShapeId() == sid) {
                    index = i;
                    break;
                }
            }

            int[] t = new int[dim];
            int[] s = new int[dim];
            for (int k = 0; k < dim; k++) {
                t[k] = rnd.nextInt(sBoxes.get(index).getSize(k)); //so that it stays touching the other box
                s[k] = rnd.nextInt(maxLength);
                while (s[k] == 0) {
                    s[k] = rnd.nextInt(maxLength);//All boxes has a minimum size of in every dimension
                }
            }
            sBoxes.add(new ShiftedBox(sBoxes.get(index).getShapeId(), t, s));

            remainingSBtoCreate--;
        }
    }


    public List<GeostObject> getObjects() {
        return objects;
    }

    public List<ShiftedBox> getSBoxes() {
        return sBoxes;
    }

    public Model getModel() {
        return m;
    }

}
