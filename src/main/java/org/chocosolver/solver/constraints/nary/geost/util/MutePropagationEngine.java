/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.util;

import org.chocosolver.solver.ICause;
import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.Propagator;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.exception.SolverException;
import org.chocosolver.solver.propagation.PropagationEngine;
import org.chocosolver.solver.variables.Variable;
import org.chocosolver.solver.variables.events.IEventType;
import org.chocosolver.solver.variables.events.PropagatorEventType;

/**
 * <br/>
 *
 * @author Charles Prud'homme
 * @since 30/06/2023
 */
public class MutePropagationEngine extends PropagationEngine {

    boolean mute = false;
    final PropagationEngine engine;

    /**
     * A seven-queue propagation engine.
     * Each of the seven queues deals with on priority.
     * When a propagator needs to be executed, it is scheduled in the queue corresponding to its priority.
     * The lowest priority queue is emptied before one element of the second lowest queue is popped, etc.
     *
     * @param model the declaring model
     */
    public MutePropagationEngine(Model model, PropagationEngine engine) {
        super(model);
        this.engine = engine;
        this.mute = false;
    }

    public void mute() {
        mute = true;
    }

    public void unmute() {
        mute = false;
    }

    // MAIN REASON WHY WE NEED THIS CLASS
    @Override
    public void onVariableUpdate(Variable variable, IEventType type, ICause cause) {
        if (!mute) engine.onVariableUpdate(variable, type, cause);
    }

    @Override
    public void schedule(Propagator<?> prop, int pindice, int mask) {
        if (!mute) engine.schedule(prop, pindice, mask);
    }

    // OTHER METHODS ARE JUST DELEGATED TO THE ENGINE

    @Override
    public void initialize() throws SolverException {
        engine.initialize();
    }

    @Override
    public boolean isInitialized() {
        return engine.isInitialized();
    }

    @Override
    public void propagate() throws ContradictionException {
        engine.propagate();
    }

    @Override
    public void execute(Propagator<?> propagator) throws ContradictionException {
        engine.execute(propagator);
    }

    @Override
    public void flush() {
        engine.flush();
    }

    @Override
    public void delayedPropagation(Propagator<?> propagator, PropagatorEventType type) {
        engine.delayedPropagation(propagator, type);
    }

    @Override
    public void onPropagatorExecution(Propagator<?> propagator) {
        engine.onPropagatorExecution(propagator);
    }

    @Override
    public void desactivatePropagator(Propagator<?> propagator) {
        engine.desactivatePropagator(propagator);
    }

    @Override
    public void reset() {
        engine.reset();
    }

    @Override
    public void clear() {
        engine.clear();
    }

    @Override
    public void ignoreModifications() {
        engine.ignoreModifications();
    }

    @Override
    public void dynamicAddition(boolean permanent, Propagator<?>... ps) throws SolverException {
        engine.dynamicAddition(permanent, ps);
    }

    @Override
    public void updateInvolvedVariables(Propagator<?> p) {
        engine.updateInvolvedVariables(p);
    }

    @Override
    public void propagateOnBacktrack(Propagator<?> propagator) {
        engine.propagateOnBacktrack(propagator);
    }

    @Override
    public void dynamicDeletion(Propagator<?>... ps) {
        engine.dynamicDeletion(ps);
    }
}
