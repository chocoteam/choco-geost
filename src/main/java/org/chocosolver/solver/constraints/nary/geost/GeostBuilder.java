/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Constraint;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.DistGeq;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.DistLeq;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.constraints.nary.geost.util.MutePropagationEngine;
import org.chocosolver.solver.propagation.PropagationEngine;
import org.chocosolver.solver.variables.IntVar;

import java.util.ArrayList;
import java.util.List;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 30/06/2023
 */
public class GeostBuilder {


    public static Constraint geost(int dim, List<GeostObject> objects, List<ShiftedBox> shiftedBoxes, List<ExternalConstraint> eCtrs) {
        return geost(dim, objects, shiftedBoxes, eCtrs, null);
    }

    public static Constraint geost(int dim, List<GeostObject> objects, List<ShiftedBox> shiftedBoxes, List<ExternalConstraint> eCtrs, List<int[]> ctrlVs) {
        return geost(dim, objects, shiftedBoxes, eCtrs, ctrlVs, null);
    }


    public static Constraint geost(int dim, List<GeostObject> objects, List<ShiftedBox> shiftedBoxes, List<ExternalConstraint> eCtrs, List<int[]> ctrlVs, GeostOptions opt) {
        int originOfObjects = objects.size() * dim; //Number of domain variables to represent the origin of all objects
        int otherVariables = objects.size() * 4; //each object has 4 other variables: shapeId, start, duration; end

        /*Collect distance variales due to ditance constraints*/
        List<Integer> distVars = new ArrayList<>(eCtrs.size());
        for (int i = 0; i < eCtrs.size(); i++) {
            ExternalConstraint ectr = eCtrs.get(i);
            if ((ectr instanceof DistLeq) && (((DistLeq) ectr).hasDistanceVar()))
                distVars.add(i);
            if ((ectr instanceof DistGeq) && (((DistGeq) ectr).hasDistanceVar()))
                distVars.add(i);
        }
        //vars will be stored as follows: object 1 coords(so k coordinates), sid, start, duration, end,
        //                                object 2 coords(so k coordinates), sid, start, duration, end and so on ........
        //To retrieve the index of a certain variable, the formula is (nb of the object in question = objId assuming objIds are consecutive and start from 0) * (k + 4) + number of the variable wanted
        //the number of the variable wanted is decided as follows: 0 ... k-1 (the coords), k (the sid), k+1 (start), k+2 (duration), k+3 (end)
        IntVar[] vars = new IntVar[originOfObjects + otherVariables + distVars.size()];
        for (int i = 0; i < objects.size(); i++) {
            for (int j = 0; j < dim; j++) {
                vars[(i * (dim + 4)) + j] = objects.get(i).getCoordinates()[j];
            }
            vars[(i * (dim + 4)) + dim] = objects.get(i).getShapeId();
            vars[(i * (dim + 4)) + dim + 1] = objects.get(i).getStart();
            vars[(i * (dim + 4)) + dim + 2] = objects.get(i).getDuration();
            vars[(i * (dim + 4)) + dim + 3] = objects.get(i).getEnd();
        }
        Model model = vars[0].getModel();
        int ind = 0;
        for (int i : distVars) {
            ExternalConstraint ectr = eCtrs.get(i);
            if (ectr instanceof DistLeq) {
                vars[originOfObjects + otherVariables + ind] = ((DistLeq) ectr).getDistanceVar();
            }
            if (ectr instanceof DistGeq) {
                vars[originOfObjects + otherVariables + ind] = ((DistGeq) ectr).getDistanceVar();
            }

            ind++;
        }

        if (opt == null) {
            opt = new GeostOptions();
        }
        // this should be checked before creating the constraint
        PropagationEngine engine = model.getSolver().getEngine();
        if (!(engine instanceof MutePropagationEngine)) {
            MutePropagationEngine muteEngine = new MutePropagationEngine(model, engine);
            model.getSolver().setEngine(muteEngine);
        }
        PropGeost propgeost;
        if (ctrlVs == null) {
            propgeost = new PropGeost(vars/*model variables*/, dim, objects, shiftedBoxes, eCtrs, false, opt.included, model.getSolver());
        } else {
            propgeost = new PropGeost(vars, dim, objects, shiftedBoxes, eCtrs, ctrlVs, opt.memoisation, opt.included, opt.increment, model.getSolver());
        }

        return new Constraint("Geost", propgeost);
    }
}
