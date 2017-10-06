/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost;

import org.chocosolver.solver.constraints.nary.geost.dataStructures.HeapAscending;
import org.chocosolver.solver.constraints.nary.geost.dataStructures.HeapDescending;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.propagation.IPropagationEngine;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * <br/>
 * <p/>
 * This is a very important class. It contains all the variables and objects the constraint needs.
 * Also it contains functions that the user and the constraint use to access the shapes, objects as well as the external constraints in the Geost.
 *
 * @author Charles Prud'homme
 * @since 17/01/2014
 */

public final class Setup {
    
    private final Constants cst;

    public GeostOptions opt = new GeostOptions();

    public final IPropagationEngine propagationEngine;

    public final PropGeost g_constraint;

    /**
     * Creates a Setup instance for a given Constants class
     *
     * @param c                 An instance of the constants class
     * @param propagationEngine
     * @param constraint
     */
    public Setup(Constants c, IPropagationEngine propagationEngine, PropGeost constraint) {
        cst = c;
        this.propagationEngine = propagationEngine;
        this.g_constraint = constraint;
    }

    /**
     * A hashtable where the key is a shape_id. And for every shape_id there is a pointer to the set of shifted_boxes that belong to this shape.
     * This hashtable contains all the shapes (and their shifted boxes) of all the objects in the geost constraint.
     */
    public final Hashtable<Integer, List<ShiftedBox>> shapes = new Hashtable<>();
    /**
     * A hashtable where the key is an object_id. And for every object_id there is a pointer to the actual object.
     * This hashtable contains all the objects that goest needs to place.
     */
    public final Hashtable<Integer, GeostObject> objects = new Hashtable<>();
    /**
     * A Vector containing ExternalConstraint objects. This vector constains all the external constraints that geost needs to deal with.
     */
    private final List<ExternalConstraint> constraints = new ArrayList<>();
    /**
     * A heap data structure containting elements in ascending order (lexicographically).
     * This is not used anymore.
     * It was used inside that pruneMin function and we used to store in it the internal constraints.
     * This way we coulld extract the active internal constraints at a specific position
     */
    private final transient HeapAscending ictrMinHeap = new HeapAscending();
    /**
     * A heap data structure containting elements in descending order (lexicographically).
     * This is not used anymore.
     * It was used inside that pruneMax function and we used to store in it the internal constraints.
     * This way we coulld extract the active internal constraints at a specific position
     */
    private final transient HeapDescending ictrMaxHeap = new HeapDescending();


    public void insertShape(int sid, List<ShiftedBox> shiftedBoxes) {
        shapes.put(sid, shiftedBoxes);
    }

    public void insertObject(int oid, GeostObject o) {
        objects.put(oid, o);
    }


    public List<ShiftedBox> getShape(int sid) {
        return shapes.get(sid);
    }

    public GeostObject getObject(int oid) {
        return objects.get(oid);
    }

    public int getNbOfObjects() {
        return objects.size();
    }


    public int getNbOfShapes() {
        return shapes.size();
    }

    /**
     * This function calculates the number of the domain variables in our problem.
     */
    public int getNbOfDomainVariables() {
        int originOfObjects = getNbOfObjects() * cst.getDIM(); //Number of domain variables to represent the origin of all objects
        int otherVariables = getNbOfObjects() * 4; //each object has 4 other variables: shapeId, start, duration; end
        return originOfObjects + otherVariables;
    }

    /**
     * Creates the environment and sets up the problem for the geost constraint given a parser object.
     */
//	public void createEnvironment(InputParser parser)
//	{
//		for(int i = 0; i < parser.getObjects().size(); i++)
//		{
//			insertObject(parser.getObjects().elementAt(i).getObjectId(), parser.getObjects().elementAt(i));
//		}
//
//		for(int i = 0; i < parser.getShapes().size(); i++)
//		{
//			insertShape(parser.getShapes().elementAt(i).getShapeId(), parser.getShapes().elementAt(i).getShiftedBoxes());
//		}
//	}

//	public void SetupTheProblem(Vector<Obj> objects, Vector<ShiftedBox> shiftedBoxes)
//	{
//		for(int i = 0; i < objects.size(); i++)
//		{
//			addObject(objects.elementAt(i));
//		}
//
//		for(int i = 0; i < shiftedBoxes.size(); i++)
//		{
//			addShiftedBox(shiftedBoxes.elementAt(i));
//		}
//
//	}

    /**
     * Given a Vector of Objects and a Vector of shiftedBoxes and a Vector of ExternalConstraints it sets up the problem for the geost constraint.
     */
    public void SetupTheProblem(List<GeostObject> objects, List<ShiftedBox> shiftedBoxes, List<ExternalConstraint> ectr) {
        for (int i = 0; i < objects.size(); i++) {
            addObject(objects.get(i));
        }

        for (int i = 0; i < shiftedBoxes.size(); i++) {
            addShiftedBox(shiftedBoxes.get(i));
        }

        for (int i = 0; i < ectr.size(); i++) {
            addConstraint(ectr.get(i));
            for (int j = 0; j < ectr.get(i).getObjectIds().length; j++) {
                getObject(ectr.get(i).getObjectIds()[j]).addRelatedExternalConstraint(ectr.get(i));
            }
        }

    }

    void addConstraint(ExternalConstraint ectr) {
        constraints.add(ectr);
    }

    public List<ExternalConstraint> getConstraints() {
        return constraints;
    }

    public HeapAscending getIctrMinHeap() {
        return ictrMinHeap;
    }

    public HeapDescending getIctrMaxHeap() {
        return ictrMaxHeap;
    }

    void addShiftedBox(ShiftedBox sb) {
        if (shapes.containsKey(sb.getShapeId())) {
            shapes.get(sb.getShapeId()).add(sb);
        } else {
            List<ShiftedBox> v = new ArrayList<>();
            v.add(sb);
            shapes.put(sb.getShapeId(), v);
        }
    }

    void addObject(GeostObject o) {
        if (objects.containsKey(o.getObjectId())) {
            System.out.println("Trying to add an already existing object. In addObject in Setup");
        } else {
            objects.put(o.getObjectId(), o);
        }
    }

    public Enumeration<Integer> getObjectKeys() {
        return objects.keys();
    }

    public Enumeration<Integer> getShapeKeys() {
        return shapes.keys();
    }

    public Set<Integer> getObjectKeySet() {
        return objects.keySet();
    }

    public Set<Integer> getShapeKeySet() {
        return shapes.keySet();
    }

    /**
     * Prints to the output console the objects and the shapes of the problem.
     */
    public void print() {
        Iterator<Integer> itr;
        itr = objects.keySet().iterator();
        while (itr.hasNext()) {
            int id = itr.next();
            GeostObject o = objects.get(id);
            System.out.println("object id: " + id);
            System.out.println("    shape id: " + o.getShapeId().getLB());
            for (int i = 0; i < cst.getDIM(); i++) {
                System.out.println("    Coords x" + i + " : " + o.getCoord(i).getLB() + "    " + o.getCoord(i).getUB());
            }
        }

        itr = shapes.keySet().iterator();
        while (itr.hasNext()) {
            int sid = itr.next();
            List<ShiftedBox> sb = shapes.get(sid);
            System.out.println("shape id: " + sid);
            for (int i = 0; i < sb.size(); i++) {
                StringBuilder offset = new StringBuilder();
                StringBuilder size = new StringBuilder();
                for (int j = 0; j < cst.getDIM(); j++) {
                    offset.append(sb.get(i).getOffset(j)).append("  ");
                    size.append(sb.get(i).getSize(j)).append("  ");
                }
                System.out.println("    sb" + i + ": ");
                System.out.println("       Offset: " + offset.toString());
                System.out.println("       Size: " + size.toString());
            }
        }
    }

    /**
     * Prints to a file that can be easily read by a person the objects and the shapes of the problem.
     * The file to be written to is specified in the global variable OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS,
     * present in the global.Constants class.
     */
    public boolean printToFileHumanFormat(String path) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));

            Iterator<Integer> itr;
            itr = objects.keySet().iterator();
            while (itr.hasNext()) {
                int id = itr.next();
                GeostObject o = objects.get(id);
                out.write("object id: " + id + '\n');
                out.write("    shape id: " + o.getShapeId().getLB() + '\n');
                for (int i = 0; i < cst.getDIM(); i++) {
                    out.write("    Coords x" + i + " : " + o.getCoord(i).getLB() + "    " + o.getCoord(i).getUB() + '\n');
                }
            }

            itr = shapes.keySet().iterator();
            while (itr.hasNext()) {
                int sid = itr.next();
                List<ShiftedBox> sb = shapes.get(sid);
                out.write("shape id: " + sid + '\n');
                for (int i = 0; i < sb.size(); i++) {
                    StringBuilder offset = new StringBuilder();
                    StringBuilder size = new StringBuilder();
                    for (int j = 0; j < cst.getDIM(); j++) {
                        offset.append(sb.get(i).getOffset(j)).append("  ");
                        size.append(sb.get(i).getSize(j)).append("  ");
                    }
                    out.write("    sb" + i + ": " + '\n');
                    out.write("       Offset: " + offset.toString() + '\n');
                    out.write("       Size: " + size.toString() + '\n');
                }
            }
            out.close();
        } catch (IOException ignored) {
        }


        return true;
    }

    /**
     * Prints to a file  the objects and the shapes of the problem. The written file can be read by the InputParser class.
     * The file to be written to is specified in the global variable OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT,
     * present in the global.Constants class.
     */
    public boolean printToFileInputFormat(String path) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(path));

            Iterator<Integer> itr;
            itr = objects.keySet().iterator();
            out.write("Objects" + '\n');
            while (itr.hasNext()) {
                int id = itr.next();
                GeostObject o = objects.get(id);
                out.write(id + " ");
                out.write(o.getShapeId().getLB() + " " + o.getShapeId().getUB() + " ");
                for (int i = 0; i < cst.getDIM(); i++) {
                    out.write(o.getCoord(i).getLB() + " " + o.getCoord(i).getUB() + " ");
                }
                //now write the time things
                out.write("1 1 1 1 1 1" + '\n');
            }

            itr = shapes.keySet().iterator();
            out.write("Shapes" + '\n');
            while (itr.hasNext()) {
                int sid = itr.next();
                out.write(sid + "" + '\n');
            }


            itr = shapes.keySet().iterator();
            out.write("ShiftedBoxes" + '\n');
            while (itr.hasNext()) {
                int sid = itr.next();
                List<ShiftedBox> sb = shapes.get(sid);

                for (int i = 0; i < sb.size(); i++) {
                    StringBuilder offset = new StringBuilder();
                    StringBuilder size = new StringBuilder();
                    for (int j = 0; j < cst.getDIM(); j++) {
                        offset.append(sb.get(i).getOffset(j)).append(" ");
                        size.append(sb.get(i).getSize(j)).append(" ");
                    }
                    out.write(sid + " ");
                    out.write(offset.toString() + size.toString() + '\n');
                }
            }
            out.close();
        } catch (IOException ignored) {
        }


        return true;
    }

    /**
     * Clears the Setup object. So basically it removes all the shapes, objects and constraints from the problem.
     */
    public void clear() {
        shapes.clear();
        objects.clear();
        constraints.clear();
        ictrMinHeap.clear();
        ictrMaxHeap.clear();
    }
//	public static void setIctrHeap(HeapAscending ictrHeap) {
//		Setup.ictrHeap = ictrHeap;
//	}

}
