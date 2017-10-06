/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:47Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost;

import org.chocosolver.memory.IStateInt;
import org.chocosolver.solver.Solution;
import org.chocosolver.solver.Solver;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.constraints.PropagatorPriority;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.ExternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.constraints.nary.geost.internalConstraints.InternalConstraint;
import org.chocosolver.solver.constraints.nary.geost.layers.ExternalLayer;
import org.chocosolver.solver.constraints.nary.geost.layers.GeometricKernel;
import org.chocosolver.solver.constraints.nary.geost.layers.IntermediateLayer;
import org.chocosolver.solver.constraints.nary.geost.util.Pair;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.propagation.IPropagationEngine;
import org.chocosolver.solver.propagation.NoPropagationEngine;
import org.chocosolver.solver.variables.IntVar;
import org.chocosolver.solver.variables.events.IntEventType;
import org.chocosolver.util.ESat;

import java.util.HashMap;
import java.util.List;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 17/01/2014
 */
public class PropGeost extends Propagator<IntVar> {
    /**
     * Array of objects ids.
     * initial order is not preserved in greedy mode, due to iteration over fixed objects.
     */
    int[] oIDs;
    /**
     * Index of the last non fixed object id (used with greedy mode)
     */
    IStateInt lastNonFixedO;

    Constants cst;
    Setup stp;
    ExternalLayer externalLayer;
    GeometricKernel geometricKernel;
    IntermediateLayer intermediateLayer;
    protected Solver s;
    private int greedyMode = 0;
    boolean increment = false;
    List<int[]> ctrlVs;

    /**
     * Creates a geost constraint with the given parameters.
     *
     * @param vars         Array of Variables for choco
     * @param k            Dimension of the problem we are working with
     * @param objects      A vector containing the objects (obj)
     * @param shiftedBoxes A vector containing the shifted boxes
     * @param ectr         A vector containing the External Constraints in our problem
     * @param ctrlVs       A list of controlling vectors used in the greedy mode
     * @param solver
     */

    public PropGeost(IntVar[] vars, int k, List<GeostObject> objects, List<ShiftedBox> shiftedBoxes,
                     List<ExternalConstraint> ectr, List<int[]> ctrlVs, boolean memo_active,
                     HashMap<Pair<Integer, Integer>, Boolean> included,
                     boolean increment_, Solver solver) {

        super(vars, PropagatorPriority.VERY_SLOW, false);

        cst = new Constants();
        stp = new Setup(cst, solver.getEngine(), this);
        intermediateLayer = new IntermediateLayer();
        externalLayer = new ExternalLayer(cst, stp);
        geometricKernel = new GeometricKernel(cst, stp, externalLayer, intermediateLayer, memo_active, included, solver, this);

        cst.setDIM(k);
        this.ctrlVs = ctrlVs;

        stp.SetupTheProblem(objects, shiftedBoxes, ectr);

        //this should be changed and be provided globally to the system
        oIDs = new int[stp.getNbOfObjects()];
        for (int i = 0; i < stp.getNbOfObjects(); i++) {
            oIDs[i] = objects.get(i).getObjectId();
        }
        lastNonFixedO = solver.getEnvironment().makeInt(oIDs.length);

        this.s = solver;
        this.greedyMode = 1;
        this.increment = increment_;


//        IntVar D = VariableFactory.bounded("D", 0, 100, s);


    }


    /**
     * Creates a geost constraint with the given parameters.
     *
     * @param vars         Array of Variables for choco
     * @param k            Dimension of the problem we are working with
     * @param objects      A vector containing the objects (obj)
     * @param shiftedBoxes A vector containing the shifted boxes
     * @param ectr         A vector containing the External Constraints in our problem
     * @param solver
     */

    public PropGeost(IntVar[] vars, int k, List<GeostObject> objects, List<ShiftedBox> shiftedBoxes,
                     List<ExternalConstraint> ectr, boolean memo, HashMap<Pair<Integer, Integer>,
            Boolean> included, Solver solver) {
        super(vars, PropagatorPriority.VERY_SLOW, false);

        cst = new Constants();
        stp = new Setup(cst, solver.getEngine(), this);
        intermediateLayer = new IntermediateLayer();
        externalLayer = new ExternalLayer(cst, stp);
        geometricKernel = new GeometricKernel(cst, stp, externalLayer, intermediateLayer, memo, included, solver, this);

        cst.setDIM(k);

        stp.SetupTheProblem(objects, shiftedBoxes, ectr);

        //this should be changed and be provided globally to the system
        oIDs = new int[stp.getNbOfObjects()];
        for (int i = 0; i < stp.getNbOfObjects(); i++) {
            oIDs[i] = objects.get(i).getObjectId();
        }
        lastNonFixedO = solver.getEnvironment().makeInt(oIDs.length);

        this.s = solver;

    }

    @Override
    public int getPropagationConditions(int vIdx) {
        return IntEventType.ALL_EVENTS;
    }

    @Override
    public void propagate(int evtmask) throws ContradictionException {
        //        int l=vars.length;
        //        for (int i=0; i<l; i++)
        //            LOGGER.info("Geost_Constraint:propagate():vars["+i+"]:"+vars[i]+","+vars[i].getInf()+","+vars[i].getSup());
        //        LOGGER.info("----propagate");          ^
        if (stp.opt.debug) System.out.println("GeostConstraint:propagate()");
        filter();
    }

    @Override
    public ESat isEntailed() {
        if (isCompletelyInstantiated()) {
            boolean b = false;
            Solver solver = model.getSolver();
            IPropagationEngine cengine = solver.getEngine();
            solver.setEngine(NoPropagationEngine.SINGLETON);
            solver.getEnvironment().worldPush();
            try {
                b = geometricKernel.filterCtrs(cst.getDIM(), oIDs,
                        stp.getConstraints());
            } catch (ContradictionException e) {
                b = false;
            }
            solver.getEnvironment().worldPop();
            solver.setEngine(cengine);
            return ESat.eval(b);
        }
        return ESat.UNDEFINED;
    }


    public void filter() throws ContradictionException {
        if (this.greedyMode == 0) {
            filterWithoutGreedyMode();
        } else {
//            long tmpTime = (System.nanoTime() / 1000000);
            filterWithGreedyMode();
//            stp.opt.timefilterWithGreedyMode += ((System.nanoTime() / 1000000) - tmpTime);
        }
    }

    private void filterWithGreedyMode() throws ContradictionException {
        if (stp.opt.debug) System.out.println("Geost_Constraint:filterWithGreedyMode()");
        s.getEnvironment().worldPush();    //Starts a new branch in the search tree
        boolean result = false;

        if (!increment) {
            long tmpTimeFixAllObj = System.nanoTime() / 1000000;
            result = geometricKernel.fixAllObjs(cst.getDIM(), oIDs, stp.getConstraints(), this.ctrlVs, lastNonFixedO);
            stp.opt.timeFixAllObj += ((System.nanoTime() / 1000000) - tmpTimeFixAllObj);
        } else {
            long tmpTimeFixAllObj = System.nanoTime() / 1000000;
            result = geometricKernel.fixAllObjs_incr(cst.getDIM(), oIDs, stp.getConstraints(), this.ctrlVs, lastNonFixedO);
            stp.opt.timeFixAllObj += ((System.nanoTime() / 1000000) - tmpTimeFixAllObj);
        }
        if (!result) {
            s.getEnvironment().worldPop();
            long tmpTime = (System.nanoTime() / 1000000);
            filterWithoutGreedyMode();
            stp.opt.timefilterWithoutGreedyMode += ((System.nanoTime() / 1000000) - tmpTime);

        } else {

            long tmpTime = (System.nanoTime() / 1000000);
            //s.getSearchStrategy().recordSolution();
            Solution sol = new Solution(model, model.retrieveIntVars(true));
            sol.record();
            stp.opt.handleSolution1 += ((System.nanoTime() / 1000000) - tmpTime);
            tmpTime = (System.nanoTime() / 1000000);
            s.getEnvironment().worldPop();  //Come back to the state before propagation
            stp.opt.handleSolution2 += ((System.nanoTime() / 1000000) - tmpTime);
            tmpTime = (System.nanoTime() / 1000000);
            //s.getSearchStrategy().restoreBestSolution();
            for(int i = 0; i < model.retrieveIntVars(true).length; i++){
                vars[i].instantiateTo(sol.getIntVal(vars[i]), this);
            }
            stp.opt.handleSolution3 += ((System.nanoTime() / 1000000) - tmpTime);
        }
    }


    private void filterWithoutGreedyMode() throws ContradictionException {
        if (stp.opt.debug) System.out.println("Geost_Constraint:filterWithoutGreedyMode()");
        if (!geometricKernel.filterCtrs(cst.getDIM(), oIDs, stp.getConstraints()))
            this.fails();
    }

    public Constants getCst() {
        return cst;
    }

    public Setup getStp() {
        return stp;
    }

    public void setCst(Constants cst) {
        this.cst = cst;
    }

    public void setStp(Setup stp) {
        this.stp = stp;
    }

    public ExternalLayer getExternalLayer() {
        return externalLayer;
    }

    public List<InternalConstraint> getForbiddenRegions(GeostObject o) {

        //Should be set up only once during a single fixpoint
        List<ExternalConstraint> ectrs = stp.getConstraints();
        for (int i = 0; i < ectrs.size(); i++) {
            ectrs.get(i).setFrame(externalLayer.InitFrameExternalConstraint(ectrs.get(i), oIDs));
        }

        //TODO: Holes should be generated here

        for (int i = 0; i < o.getRelatedExternalConstraints().size(); i++) {
            List<InternalConstraint> v = externalLayer.genInternalCtrs(o.getRelatedExternalConstraints().get(i), o);
            for (int j = 0; j < v.size(); j++) {
                o.addRelatedInternalConstraint(v.get(j));
            }
        }

        return o.getRelatedInternalConstraints();
    }

    public void setGreedy(boolean greedy) {
        this.greedyMode = greedy ? 1 : 0;
    }

    public boolean isGreedy() {
        return greedyMode != 0;
    }
}
