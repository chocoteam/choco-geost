/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:47Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.layers;

import org.chocosolver.solver.constraints.nary.geost.Setup;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.DistGeq;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.DistLeq;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.DistLinear;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Region;
import org.chocosolver.solver.constraints.nary.geost.internalConstraints.InternalConstraint;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.exception.SolverException;
import org.chocosolver.solver.variables.IntVar;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.text.MessageFormat.format;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 14 ao�t 2009
 * Time: 16:27:20
 * To change this template use File | Settings | File Templates.
 */
public final class GeostNumeric {

    private Setup stp = null;
    private int paramid = 0; //each detected new param gets a param id which is the id of the param in the extengine
    private int varid = 0;    //each detected new variable gets a variable id which is the id of the var in the extengine
    // (extengine=external engine=numeric solver here)
    private int ctrid = 0; //each added new constraint gets a cstr id
    private String listVars = "";
    private String listParams = "";
    private String listCstrs = "";
    private String listContractors = "";


    private Map<GeostObject, String> contractorName = new HashMap<>();
    //list of var ids in the ext engine corresponding to domain variable
    private Map<IntVar, List<Integer>> VarVarId = new HashMap<>();
    //list of param ids in the ext engine corresponding to domain variable
    private Map<IntVar, List<Integer>> varParamId = new HashMap<>();
    //list of domain variables that exist in the external engine
    private Map<IntVar, Boolean> listOfVars = new HashMap<>();

    private Map<IntVar, HashMap<ExternalConstraint, Integer>> VarParamId = new HashMap<>();
    private Map<GeostObject, HashMap<ExternalConstraint, String>> ObjParamIdText = new HashMap<>();
    private Map<GeostObject, HashMap<ExternalConstraint, String>> ObjCstrName = new HashMap<>();


    private long cr = -1; //conversion rate between domain variable and external engine
    private double isThick = 0.0;

    public GeostNumeric(Setup stp_, int maxNbrOfBoxes) {
        stp = stp_;

        cr = computeConversionRate();

        isThick = computeIsThick(maxNbrOfBoxes);


        for (int oid = 0; oid < stp.getObjectKeySet().size(); oid++) { //fixed order iteration
            GeostObject o = stp.getObject(oid);
            addObj(o);
        }

        for (int oid = 0; oid < stp.getObjectKeySet().size(); oid++) { //fixed order iteration
            GeostObject o = stp.getObject(oid);
            for (ExternalConstraint ectr : o.getRelatedExternalConstraints()) {
                addCstr(o, ectr); //write constraint and params at the same time
            }
            writeContractor(o);
        }

    }

    private void addObj(GeostObject o) {
        listVars += strObj(o);
        int k = o.getCoordinates().length;
        for (int d = 0; d < k; d++) {
            IntVar v = o.getCoord(d);
            addVar(v);
        }
    }

    private void addVar(IntVar v) {
        if (!VarVarId.containsKey(v)) {
            VarVarId.put(v, new ArrayList<>());
        }
        VarVarId.get(v).add(varid++);
        listOfVars.put(v, true);
    }


    private String strObj(GeostObject o) {
        StringBuilder r = new StringBuilder();
        int k = o.getCoordinates().length;
        r.append("o").append(o.getObjectId()).append("[").append(k).append("] in [");
        for (int d = 0; d < k; d++) {
            r.append("[");
            r.append(coordToExtEngine(o.getCoord(d).getLB())).append("/*").append(o.getCoord(d).getLB()).append("*/,");
            r.append(coordToExtEngine(o.getCoord(d).getUB())).append("/*").append(o.getCoord(d).getUB()).append("*/");
            if (d == k - 1) {
                r.append("]");
            } else {
                r.append("];");
            }
        }
        r.append("];\n");
        return r.toString();
    }


    private String strParam(GeostObject o, ExternalConstraint ectr) {
        StringBuilder r = new StringBuilder();
        int k = o.getCoordinates().length;
        r.append(ObjParamIdText.get(o).get(ectr)).append("[").append(k).append("] in [");
        for (int d = 0; d < k; d++) {
            r.append("[");
            r.append(coordToExtEngine(o.getCoord(d).getLB())).append("/*").append(o.getCoord(d).getLB()).append("*/" + ",");
            r.append(coordToExtEngine(o.getCoord(d).getUB())).append("/*").append(o.getCoord(d).getUB()).append("*/");
            if (d == k - 1) {
                r.append("]");
            } else {
                r.append("];");
            }
        }
        r.append("];\n");
        return r.toString();
    }


    private void addParam(GeostObject o, ExternalConstraint ectr) {
        //Add all obj to the system which are different from o and included in ectr
        if (ectr instanceof DistLeq) {
            DistLeq dl = (DistLeq) ectr;
            int oid = o.getObjectId();
            int toAdd = oid;
            if (dl.o1 == oid) {
                toAdd = dl.o2;
            } else {
                toAdd = dl.o1;
            }
            addObjParamText(stp.getObject(toAdd), ectr);
            listParams += strParam(stp.getObject(toAdd), ectr);
            if (dl.hasDistanceVar()) {
                addVarParam(dl.getDistanceVar(), ectr);
                listParams += strParam(dl.getDistanceVar(), ectr);
            }
        } else if (ectr instanceof DistGeq) {
            DistGeq dl = (DistGeq) ectr;
            int oid = o.getObjectId();
            int toAdd = oid;
            if (dl.o1 == oid) {
                toAdd = dl.o2;
            } else {
                toAdd = dl.o1;
            }
            addObjParamText(stp.getObject(toAdd), ectr);
            listParams += strParam(stp.getObject(toAdd), ectr);
            if (dl.hasDistanceVar()) {
                addVarParam(dl.getDistanceVar(), ectr);
                listParams += strParam(dl.getDistanceVar(), ectr);
            }
        } else if (ectr instanceof DistLinear) {
            //no parameter
        } else {
            throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:addParam():External Constraint " + ectr + " not supported yet.");
        }
        ctrid++;
    }

    private String strParam(IntVar v, ExternalConstraint ectr) {
        String name = format("p{0}", VarParamId.get(v).get(ectr));
        StringBuilder r = new StringBuilder(format("{0} in ", name));
        r.append("[");
        r.append(coordToExtEngine(v.getLB())).append(",");
        r.append(coordToExtEngine(v.getUB()));
        r.append("];\n");
        return r.toString();
    }


    private void addObjParamText(GeostObject o, ExternalConstraint ectr) {
        if (ObjParamIdText.get(o) == null) {
            ObjParamIdText.put(o, new HashMap<>());
        }
        ObjParamIdText.get(o).put(ectr, strObjName(o));

        for (IntVar v : o.getCoordinates()) {
            addVarParam(v, ectr);
            paramid++;
        }
    }

    private String strObjName(GeostObject o) {
        //return the name of the param of obj o and constraint ectr
        return "o" + o.getObjectId() + "_ctr" + ctrid;
    }

    private void addVarParam(IntVar v, ExternalConstraint ectr) {
        if (VarParamId.get(v) == null) {
            VarParamId.put(v, new HashMap<>());
        }
        VarParamId.get(v).put(ectr, paramid);
        if (varParamId.get(v) == null) {
            varParamId.put(v, new ArrayList<>());
        }
        varParamId.get(v).add(paramid);
        listOfVars.put(v, true);
        paramid++;
    }


    private void addCstr(GeostObject o, ExternalConstraint ectr) {
        addParam(o, ectr);
        listCstrs += strCstr(o, ectr); //return the cstr string and associate o with the name of the constraint
        addCstrName(o, ectr); //asociate o and the constraint name
        ctrid++;
    }

    private void addCstrName(GeostObject o, ExternalConstraint ectr) {
        String name = strCstrName(o);
        if (ObjCstrName.get(o) == null) {
            ObjCstrName.put(o, new HashMap<>());
        }
        ObjCstrName.get(o).put(ectr, name);
    }

    private String strCstr(GeostObject o, ExternalConstraint ectr) {
        StringBuilder r = new StringBuilder();
        if (ectr instanceof DistLeq) {
            DistLeq dl = (DistLeq) ectr;
            int oid = o.getObjectId();
            r.append(format("constraint {0}\n", strCstrName(o)));
            if (dl.hasDistanceVar()) {
                r.append(" distance(o").append(oid).append(",").append(getObjectParamIdText(stp.getObject(dl.o2), ectr))
                        .append(")<=").append(getVarParamIdText(dl.getDistanceVar(), ectr)).append(";\n");
            } else {
                r.append(" distance(o").append(oid).append(",")
                        .append(getObjectParamIdText(stp.getObject(dl.o2), ectr)).append(")<=")
                        .append(coordToExtEngine(dl.D / 2)).append("/*").append(dl.D).append("*/" + ";\n");
            }
            r.append("end\n");
        } else if (ectr instanceof DistGeq) {
            DistGeq dl = (DistGeq) ectr;
            int oid = o.getObjectId();
            r.append("constraint ").append(strCstrName(o)).append("\n");
            if (dl.hasDistanceVar()) {
                r.append(" distance(o").append(oid).append(",")
                        .append(getObjectParamIdText(stp.getObject(dl.o2), ectr)).append(")>=")
                        .append(getVarParamIdText(dl.getDistanceVar(), ectr)).append(";\n");
            } else {
                r.append(" distance(o").append(oid).append(",")
                        .append(getObjectParamIdText(stp.getObject(dl.o2), ectr)).append(")>=")
                        .append(coordToExtEngine(dl.D)).append("/*").append(dl.D).append("*/" + ";\n");
            }
            r.append("end\n");
        } else if (ectr instanceof DistLinear) {
            throw new SolverException("GeostNumeric:strCstr():External Constraint " + ectr + " not supported yet.");
        }

        return r.toString();
    }

    private String getObjectParamIdText(GeostObject o, ExternalConstraint ectr) {
        return ObjParamIdText.get(o).get(ectr);
    }

    private String strCstrName(GeostObject o) {
        return "obj" + o.getObjectId() + "_c" + ctrid;
    }

    private String getVarParamIdText(IntVar v, ExternalConstraint ectr) {
        return "p" + VarParamId.get(v).get(ectr);
    }


    private double computeIsThick(int maxNbrOfBoxes) {
        //The goal of 'computeIsThick' is to compute the parameter isThick such that
        //the max. nbr of boxes generated by the numeric engine os 'maxNbrOfBoxes'.
        //PRE: cr has been computed

        if (cr == -1) {
            throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:computeIsThick:conversion ratio cr must be computed before isThick can be compured");
        }

        double maxNbrOfBoxesDouble = (double) maxNbrOfBoxes;

        int k = stp.getObject(0).getCoordinates().length;
        double volume = (double) volume();
        System.out.println("volume:" + String.format("%f", volume));

        isThick = 1.0;
        double inverse_k = 1.0 / ((double) k);
        if (inverse_k <= 0) {
            throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:computeIsThick:unable to compute isThick because of 1/k:" + inverse_k);
        }
        return Math.pow((volume / maxNbrOfBoxesDouble), inverse_k) / cr;
    }

    private long volume() {
        int k = stp.getObject(0).getCoordinates().length;
        long volume = 1;

        for (int dim = 0; dim < k; dim++) {
            //determine min and max for dim i
            int min = stp.getObject(0).getCoord(dim).getLB();
            int max = stp.getObject(0).getCoord(dim).getUB();
            for (int i : stp.getObjectKeySet()) {
                GeostObject o = stp.getObject(i);
                min = Math.min(min, o.getCoord(dim).getLB());
                max = Math.max(max, o.getCoord(dim).getUB());
            }

            volume *= Math.abs(max - min);
        }

        return volume;

    }


    private long computeConversionRate() {
        //compute conversion rate (of the form 10^k) on min and max
        //determine min and max
        int min = stp.getObject(0).getCoord(0).getLB();
        int max = stp.getObject(0).getCoord(0).getUB();
        for (int i : stp.getObjectKeySet()) {
            GeostObject o = stp.getObject(i);
            for (int j = 0; j < o.getCoordinates().length; j++) {
                min = Math.min(min, o.getCoord(j).getLB());
                max = Math.max(max, o.getCoord(j).getUB());
            }
        }
        //determine conversion rate of min
        long cr_min = 1;
        while ((min / cr_min) > 0) {
            cr_min *= 10;
            if (cr_min < 0) {
                throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:computeConversionRate:long limit cr_min exceeded");
            }
        }
        //determine conversion rate of max
        long cr_max = 1;
        while ((max / cr_max) > 0) {
            cr_max *= 10;
            if (cr_max < 0) {
                throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:computeConversionRate:long limit cr_max exceeded");
            }
        }

        //return the max of the two
        return Math.max(cr_min, cr_max);
    }

    private double coordToExtEngine(int v) {
        double vd = (double) v;
        return (vd / cr);
    }

    private void writeContractor(GeostObject o) {
        List<ExternalConstraint> vectr = o.getRelatedExternalConstraints();
        if (vectr.size() > 0) {
            listContractors += "contractor object" + o.getObjectId() + "\npropag(";

            for (int i = 0; i < vectr.size(); i++) {
                String ctrname = ObjCstrName.get(o).get(vectr.get(i));
                if (i == vectr.size() - 1) {
                    listContractors += ctrname + ");";
                } else {
                    listContractors += ctrname + ";";
                }
            }
            listContractors += "\nend/*listContractors*/\n";
            contractorName.put(o, "object" + o.getObjectId());
        }


    }

    private void writeFile(String filename) {
        try {
            File f = new File(filename);
            PrintWriter pw = new PrintWriter(new FileWriter(f));
            pw.println("Variables");
            pw.println(listVars);
            pw.println("Parameters");
            pw.println(listParams);
            pw.println("function d=distance(x[2],y[2])\n\td=sqrt((x[1]-y[1])^2+(x[2]-y[2])^2);\nend");
            pw.println(listCstrs);
            pw.println(listContractors);
            //DecimalFormat df = new DecimalFormat("###.#######################");
            // df.format(isThick)
            pw.println("contractor isthick\nmaxdiamGT(" + String.format("%f", isThick).replace(",", ".") + ");\nend\n");

            pw.close();
        } catch (Exception e) {
            throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:writeFile():could not write file");
        }
    }

    public void synchronize() {
//        for (int i : stp.getObjectKeySet()) {
//            Obj o=stp.getObject(i);
//            synchronize(o);
//        }

        for (IntVar v : listOfVars.keySet()) {
            synchronize(v);
        }
    }

    public void synchronize(GeostObject o) {
        /*for (int i = 0; i < o.getCoordinates().length; i++) {
            IntVar v = o.getCoord(i);
            for (int id : VarVarId.get(v)) {
                double inf = coordToExtEngine(v.getLB());
                double sup = coordToExtEngine(v.getUB());
                engine.set_var_domain(id, inf, sup);
            }
        }*/
        throw new UnsupportedOperationException("geost does not support complex form");
    }

    public void synchronize(IntVar v) {

        /*List<Integer> a = VarVarId.get(v);
        List<Integer> b = varParamId.get(v);
        if (a != null) {
            for (int id : VarVarId.get(v)) {
                double inf = coordToExtEngine(v.getLB());
                double sup = coordToExtEngine(v.getUB());
                engine.set_var_domain(id, inf, sup);
            }
        } else if (b != null) {
            List<Integer> vint = varParamId.get(v);
            for (int i = 0; i < vint.size(); i++) {
                int id = vint.get(i);
                double inf = coordToExtEngine(v.getLB());
                double sup = coordToExtEngine(v.getUB());
                engine.set_var_domain(id, inf, sup);
            }

        } else {
            throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:synchronize:IntVar not found");

        }*/
        throw new UnsupportedOperationException("geost does not support complex form");


    }

    public void synchronize(ExternalConstraint ectr) {
        throw new SolverException("choco.cp.solver.constraints.global.geost.layers.GeostNumeric:synchronize:External Constraints are not supported yet.");

    }

    public Region propagate(GeostObject o) {
        //Returns a region /*containing*/ the solution
        System.out.println("--Entering GeostNumeric:propagate()");

        int k = o.getCoordinates().length;
        if (contractorName.get(o) == null) {
            return new Region(k, o);
        }
        System.out.println("calling contract(object" + o.getObjectId() + ") because of contractorName.get(" + o + ")=" + contractorName.get(o));

        /*engine.contract("object" + o.getObjectId());
        Region r = new Region(k, o.getObjectId());
        for (int i = 0; i < k; i++) {
            //Probleme de conversion inverse ici
            String id = format("o{0}[{1}]", o.getObjectId(), i);
            double lb = engine.get_lb(id);
            double ub = engine.get_ub(id);
            int lb_int = (int) Math.floor(lb * cr);
            int ub_int = (int) Math.ceil(ub * cr);
            r.setMinimumBoundary(i, lb_int);
            r.setMaximumBoundary(i, ub_int);
        }
        System.out.println("--Exiting GeostNumeric:propagate()");
        return r;
        */
        throw new UnsupportedOperationException("geost does not support complex form");
    }


    public void prune(GeostObject o, int k, List<InternalConstraint> ictrs) throws ContradictionException {
        System.out.println("Entering Prune:" + o + "," + k + "," + ictrs);
        //returns no value, but throws a contradiction exception if failure occurs
        //call engine to propagate
        synchronize();
        Region box = propagate(o);
        //update o with box; set b to true if o is modified.
        for (int i = 0; i < k; i++) {
            int min = box.getMinimumBoundary(i);
            int max = box.getMaximumBoundary(i);
            int min_ori = o.getCoord(i).getLB();
            int max_ori = o.getCoord(i).getUB();
            System.out.println("Prune():" + o + "[" + i + "] updated to [" + min + "," + max + "]");

            boolean var_was_modified = false;
            if (min > min_ori) {
                var_was_modified = true;
                //Detect failure to update b
                o.getCoord(i).updateLowerBound(min, stp.g_constraint);
            }
            if (max < max_ori) {
                var_was_modified = true;
                o.getCoord(i).updateUpperBound(max, stp.g_constraint);
            }

            //TODO: synchronize variables only when they are modified
            if (var_was_modified) {
                synchronize(o.getCoord(i)); //(A)
            }
            System.out.println("Prune():synchronize o[" + i + "]=[" + o.getCoord(i).getLB() + "," + o.getCoord(i).getUB() + "]");
        }
        //The following line is an alternative to the synchronization in line (A)
        //if (b) synchronize(o); //(B)
        //(B) is simply more computational-expensive than (A)
        System.out.println("Exiting Prune()");
    }


}


//        for (ExternalConstraint ectr : stp.getConstraints()) {
//        }
//
//
//
//
//        //new ComponentConstraint(ConstraintType.GEOST, new Object[]{dim, shiftedBoxes, eCtrs, objects, ctrlVs, opt}, vars);
//            Object[] params = (Object[])parameters;
//            int dim = (Integer)params[0];
//            Vector<ShiftedBox> shiftedBoxes = (Vector<ShiftedBox>)params[1];
//            Vector<IExternalConstraint> ectr = (Vector<IExternalConstraint>)params[2];
//            Vector<GeostObject> vgo = (Vector<GeostObject>)params[3];
//            Vector<int[]> ctrlVs = (Vector<int[]>)params[4];
//            GeostOptions opt = (GeostOptions) params[5];
//            if (opt==null) { opt=new GeostOptions(); }
//
//            CPSolver solver = (CPSolver) stp.getObject(0).getCoord(0).getSolver();
//
//            //Transformation of Geost Objects (model) to interval geost object (constraint)
//            Vector<Obj> vo = new Vector<Obj>(vgo.size());
//            for (int i = 0; i < vgo.size(); i++) {
//                GeostObject g = vgo.elementAt(i);
//                vo.add(i, new Obj(g.getDim(),
//                        g.getObjectId(),
//                        solver.getVar(g.getShapeId()),
//                        solver.getVar(g.getCoordinates()),
//                        solver.getVar(g.getStartTime()),
//                        solver.getVar(g.getDurationTime()),
//                        solver.getVar(g.getEndTime()),
//                        g.getRadius())
//                        );
//            }
//
//
//        String vars="";
//        int k=stp.getObject(0).getCoordinates().length;
//        int index=0;
//        //for each object i
//        for (int i=0; i<stp.getObjectKeySet().size(); i++) {
//            Obj o = stp.getObject(i);
//            //declare variable domain
//            cvars.put(i,index);
//            vars+="o"+i+"["+k+"] in ";
//            vars+="[";
//            for (int d=0; d<k; d++) {
//                vars+="[";
//                vars+=o.getCoord(d).getLB()+",";
//                vars+=o.getCoord(d).getUB();
//                if (d==k-1) vars+="]"; else vars+="];";
//            }
//            vars+="];\n";
//            index+=2;
//
//
//            //Assign a parameter for all variables related to o_i
//           for (ExternalConstraint ect : stp.getConstraints() ) {
//                if (ect instanceof DistLeq) {
//                    DistLeq dl = (DistLeq) ect;
//                    //pars[i][dl.o2]=
//
//                }
//
//            }
//
//            String constraints="";
//            int nextCstr=0;
//            //for each constraint attached to object i
//            for (ExternalConstraint ect : stp.getConstraints() ) {
//                if (ect instanceof DistLeq) {
//                    DistLeq dl = (DistLeq) ect;
//                    constraints+="constraint obj"+i+"_c"+nextCstr+"\n";
//                    constraints+=" distance(o"+i+",p"+pars.get(dl.getObjectIds()[1])+")<=p"+pars.getVarCstr(dl.getCstrId())+";\n";
//                    constraints+="end\n";
//
//                }
//            }
//
//
//
//
//
//        }
//

