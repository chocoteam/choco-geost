/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.layers;


import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.Setup;
import org.chocosolver.solver.constraints.nary.geost.externalConstraints.*;
import org.chocosolver.solver.constraints.nary.geost.frames.DistLinearFrame;
import org.chocosolver.solver.constraints.nary.geost.frames.ForbiddenRegionFrame;
import org.chocosolver.solver.constraints.nary.geost.frames.Frame;
import org.chocosolver.solver.constraints.nary.geost.frames.NonOverlappingFrame;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Region;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.constraints.nary.geost.internalConstraints.*;
import org.chocosolver.solver.constraints.nary.geost.util.Pair;
import org.chocosolver.solver.exception.SolverException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;


/**
 * This is the external layer class. It implements the functionality that each external constraint should have. For every external constraint we
 * should be able to create the corresponding FRAME and generate the corresponding internal constraints.
 */
public final class ExternalLayer {

    Constants cst;
    Setup stp;

    /**
     * Creates an ExternalLayer instance for a specific constants class and a specific setup class
     *
     * @param c The constants class
     * @param s The Setup class
     */
    public ExternalLayer(Constants c, Setup s) {
        cst = c;
        stp = s;
    }

    /**
     * @param ectr An externalConstraint object
     * @param oIDs The list of object IDs
     * @return The frame that correspond to the external constraint ectr.
     */
    public Frame InitFrameExternalConstraint(ExternalConstraint ectr, int[] oIDs) {
        Frame result;
        switch (ectr.getEctrID()) {
            case Constants.COMPATIBLE:
                result = initFrameExternalConstraintForCompatible();
                break;
            case Constants.INCLUDED:
                result = initFrameExternalConstraintForIncluded();
                break;
            case Constants.NON_OVERLAPPING:
                result = initFrameExternalConstraintForNonOverlapping(oIDs);
                break;
            case Constants.VISIBLE:
                result = initFrameExternalConstraintForVisible();
                break;
            case Constants.DIST_LEQ:
                result = InitFrameExternalConstraintForDistLeq((DistLeq) ectr, oIDs);
                break;
            case Constants.DIST_GEQ:
                result = initFrameExternalConstraintForDistGeq((DistGeq) ectr);
                break;
            case Constants.DIST_LINEAR:
                result = InitFrameExternalConstraintForDistLinear((DistLinear) ectr, oIDs);
                break;
            case Constants.NON_OVERLAPPING_CIRCLE:
                result = initFrameExternalConstraintForNonOverlappingCircle();
                break;

            default:
                throw new SolverException("A call to InitFrameExternalConstraint with incorrect ectr parameter");
        }
        return result;
    }

    /**
     * @param ectr An externalConstraint object
     * @param o    An object
     * @return A vector containing all the internal constraints that are applied to o caused by ectr
     */
    public List<InternalConstraint> genInternalCtrs(ExternalConstraint ectr, GeostObject o) {
        List<InternalConstraint> result;
        switch (ectr.getEctrID()) {
            case Constants.COMPATIBLE:
                result = genInternalCtrsForCompatible();
                break;
            case Constants.INCLUDED:
                result = genInternalCtrsForIncluded();
                break;
            case Constants.NON_OVERLAPPING:
                result = genInternalCtrsForNonOverlapping((NonOverlapping) ectr, o);
                break;
            case Constants.VISIBLE:
                result = genInternalCtrsForVisible();
                break;
            case Constants.DIST_LEQ:
                result = genInternalCtrsForDistLeq((DistLeq) ectr);
                break;
            case Constants.DIST_GEQ:
                result = genInternalCtrsForDistGeq((DistGeq) ectr);
                break;
            case Constants.DIST_LINEAR:
                result = genInternalCtrsForDistLinear((DistLinear) ectr);
                break;
            default:
                throw new SolverException("A call to GenInternalCstrs with incorrect ectr parameter");
        }
        return result;
    }

    private Frame initFrameExternalConstraintForCompatible() {
        // Should be changed for Compatible Frame
        return new NonOverlappingFrame();
    }

    private Frame initFrameExternalConstraintForIncluded() {
        // Should be changed for Included Frame

        return new NonOverlappingFrame();
    }

    private Frame initFrameExternalConstraintForNonOverlapping(int[] oIDs) {
        NonOverlappingFrame f = new NonOverlappingFrame();
        for (int i = 0; i < oIDs.length; i++) {
            GeostObject o = stp.getObject(oIDs[i]);
            int m = o.getShapeId().getDomainSize();

            List<Region> regions = new ArrayList<>();

            int[][] set = new int[m][];
            int ivalue = 0;
            for (int sid = o.getShapeId().getLB(); sid <= o.getShapeId().getUB(); sid = o.getShapeId().nextValue(sid)) {
                int nbOfSbox = stp.getShape(sid).size();
                set[ivalue] = new int[nbOfSbox];
                for (int j = 0; j < nbOfSbox; j++) {
                    set[ivalue][j] = j;
                }
                ivalue++;
            }

            int[] pointer = new int[m];
            boolean print = true;
            while (true) {
                Region r = new Region(cst.getDIM(), o.getObjectId());
                for (int j = 0; j < cst.getDIM(); j++) {
                    int max = stp.getShape(o.getShapeId().getLB()).get(set[0][pointer[0]]).getOffset(j);
                    int min = stp.getShape(o.getShapeId().getLB()).get(set[0][pointer[0]]).getOffset(j) + stp.getShape(o.getShapeId().getLB()).get(set[0][pointer[0]]).getSize(j);
                    int curDomVal = o.getShapeId().nextValue(o.getShapeId().getLB());
                    for (int s = 1; s < m; s++) {
                        max = Math.max(max, stp.getShape(curDomVal).get(set[s][pointer[s]]).getOffset(j));
                        min = Math.min(min, stp.getShape(curDomVal).get(set[s][pointer[s]]).getOffset(j) + stp.getShape(curDomVal).get(set[s][pointer[s]]).getSize(j));
                        curDomVal = o.getShapeId().nextValue(curDomVal);
                    }
                    r.setMinimumBoundary(j, o.getCoord(j).getUB() + max + 1);
                    r.setMaximumBoundary(j, o.getCoord(j).getLB() + min - 1);
                }
                regions.add(r);
                for (int j = m - 1; j >= 0; j--) {
                    if (pointer[j] == set[j].length - 1) {
                        if (j == 0) {
                            print = false;
                        }
                        pointer[j] = 0;
                    } else {
                        pointer[j] += 1;
                        break;
                    }
                }
                if (!print) {
                    break;
                }
            }
            f.addForbidRegions(o.getObjectId(), regions);
        }
        return f;
    }


    private Frame InitFrameExternalConstraintForDistLeq(DistLeq ectr, int[] oIDs) {
        /*No ploymorphism for now*/
        int s1 = stp.getObject(ectr.o1).getShapeId().getValue();
        int s2 = stp.getObject(ectr.o2).getShapeId().getValue();
        ForbiddenRegionFrame f = new ForbiddenRegionFrame(ectr.q, ectr.D, s1, s2, ectr.o1, ectr.o2);
        for (int i = 0; i < oIDs.length; i++) {
            GeostObject o = stp.getObject(oIDs[i]);
            List<Region> regions = new ArrayList<>();
            f.addForbidRegions(o.getObjectId(), regions);
        }

        return f;
    }

    private Frame initFrameExternalConstraintForDistGeq(DistGeq ectr) {
        /*No ploymorphism for now*/
        int s1 = stp.getObject(ectr.o1).getShapeId().getValue();
        int s2 = stp.getObject(ectr.o2).getShapeId().getValue();
        //     for (int i = 0; i < oIDs.length; i++)
//     {
//         Obj o = stp.getObject(oIDs[i]);
//         List<Region> regions = new ArrayList<Region>();
//         f.addForbidRegions(o.getObjectId(), regions);
//     }

        return new ForbiddenRegionFrame(ectr.q, ectr.D, s1, s2, ectr.o1, ectr.o2);
    }

    private Frame InitFrameExternalConstraintForDistLinear(DistLinear ectr, int[] oIDs) {
        /*No ploymorphism for now*/
        DistLinearFrame f = new DistLinearFrame(ectr.a, ectr.o1, ectr.b);
        for (int i = 0; i < oIDs.length; i++) {
            GeostObject o = stp.getObject(oIDs[i]);
            List<Region> regions = new ArrayList<>();
            f.addForbidRegions(o.getObjectId(), regions);
        }

        return f;
    }

    private Frame initFrameExternalConstraintForVisible() {
        // Should be changed for Visible Frame

        return new NonOverlappingFrame();
    }

    private Frame initFrameExternalConstraintForNonOverlappingCircle() {
        // Should be changed for Visible Frame

        return new NonOverlappingFrame();
    }


    private List<InternalConstraint> genInternalCtrsForCompatible() {

        return new ArrayList<>();
    }

    private List<InternalConstraint> genInternalCtrsForIncluded() {

        return new ArrayList<>();
    }

    public Pair<Outbox, Boolean> mergeAdjacent(Outbox new_ob, Outbox last_ob) {
        //true if merging has occured

        //Check if the last outbox is adjacent on a single dimension with the last outbox

        int dim = new_ob.adjacent(last_ob);
        if ((dim != -1) && (!new_ob.sameSize(last_ob, dim))) dim = -1;
        if (dim != -1) new_ob.merge(last_ob, dim); //merge the two objects
//        if (dim!=-1) System.out.println("after merge:"+new_ob);

        return new Pair<>(new_ob, dim != -1);

    }


    private List<InternalConstraint> genInternalCtrsForNonOverlapping(NonOverlapping ectr, GeostObject o) {

        // Since non_overlapping constraint then we will generate outbox constraints
        List<InternalConstraint> ictrs = new ArrayList<InternalConstraint>();
        List<ShiftedBox> sb = stp.getShape(o.getShapeId().getLB());
        Iterator<Integer> itr;
        itr = ectr.getFrame().getRelForbidRegions().keySet().iterator();
        boolean printit = false;
        while (itr.hasNext()) {
            int i = itr.next();
            if (!(o.getObjectId() == i)) {
                for (int k = 0; k < sb.size(); k++) {
                    // We will generate an outbox constraint corresponding to each relative forbidden region we already generated
                    // for the shifted boxes of the shape corresponding to the Obj o

                    // here we go into the relative forbidden regions
                    loop:
                    for (int l = 0; l < ectr.getFrame().getRelForbidRegions(i).size(); l++) {
                        int[] t = new int[cst.getDIM()];
                        int[] s = new int[cst.getDIM()];
                        for (int j = 0; j < cst.getDIM(); j++) {
                            int min = ectr.getFrame().getRelForbidRegions(i).get(l).getMinimumBoundary(j) - sb.get(k).getOffset(j) - sb.get(k).getSize(j);
                            int max = ectr.getFrame().getRelForbidRegions(i).get(l).getMaximumBoundary(j) - sb.get(k).getOffset(j);

                            s[j] = max - min + 1; // length of the jth coordinate
                            if (s[j] <= 0) // since the length is negative
                                continue loop;
                            t[j] = min; // It is the offset. lower left corner.

                            if (printit) System.out.println(o.getObjectId() + " " + j + " " + o);
                            int supDom = o.getCoord(j).getUB();// + sb.get(k).getOffset(j) + sb.get(k).getSize(j);
                            int infDom = o.getCoord(j).getLB();// + sb.get(k).getOffset(j) ;
                            int maxObj = o.getCoord(j).getUB() + sb.get(k).getOffset(j) + sb.get(k).getSize(j) - 1;
                            if (maxObj > o.getCoord(j).getUB()) maxObj = o.getCoord(j).getUB();
                            int minObj = o.getCoord(j).getLB() + sb.get(k).getOffset(j);
                            if (minObj < o.getCoord(j).getLB()) minObj = o.getCoord(j).getLB();

                            if (printit) System.out.println("box: " + t[j] + " " + s[j]);
                            if (printit) System.out.println("dom: " + minObj + " " + maxObj);


                            if ((supDom < t[j]) || (infDom > t[j] + s[j])) {
                                // this means the intersection of dom(o.x) and the region forbidden region associated with Outbox(t,s) is empty. In the other words all
                                // the placement space is feasible for o.x according to the constraint Outbox(t,s)
                                if (printit) System.out.println("skip");
                                continue loop;
                            }
                            if ((maxObj < t[j]) || (minObj > t[j] + s[j])) {
                                // this means the intersection of dom(o.x) and the region forbidden region associated with Outbox(t,s) is empty. In the other words all
                                // the placement space is feasible for o.x according to the constraint Outbox(t,s)
                                if (printit) System.out.println("skip2");
                                continue loop;
                            }

                            //clipping
                            if (stp.opt.clipping) {
                                //   t[j] = Math.max(minObj, t[j]);
                                //     s[j] = Math.min(maxObj, t[j] + s[j]) - t[j]  ;
                            }

                            if (printit) System.out.println("result box: " + t[j] + " " + s[j]);


                        }

                        Outbox new_ob = new Outbox(t, s);


                        Pair<Outbox, Boolean> result;
                        if (ictrs.size() != 0) {
                            Outbox last_ob = (Outbox) ictrs.get(ictrs.size() - 1);
                            result = mergeAdjacent(new_ob, last_ob);

                            new_ob = result.fst;

                            if (result.snd) ictrs.remove(ictrs.size() - 1);

                        }

                        ictrs.add(new_ob);
                    }
                }
            }
        }
        return ictrs;
    }

    private boolean useful_absolute_fr(int min, int max, int[]s, int[]t, int j, int lb, int ub) {
        if(min < lb){
            min = lb;
        }
        if(max > ub){
            max = ub;
        }
        if(min > max){
            return false;
        }
        s[j] = max - min + 1; // length of the jth coordinate
        t[j] = min; // It is the offset. lower left corner.
        return true;
    }


    private List<InternalConstraint> genInternalCtrsForVisible() {

        return new ArrayList<>();
    }

    private List<InternalConstraint> genInternalCtrsForDistGeq(DistGeq ectr) {
        List<InternalConstraint> ictrs = new ArrayList<>();
        ForbiddenRegionFrame f = ((ForbiddenRegionFrame) ectr.getFrame());
        DistGeqIC ic = new DistGeqIC(stp, f.q, f.D, f.s1, f.s2, f.o1, f.o2, ectr.getDistanceVar());
        ictrs.add(ic);
        return ictrs;
    }

    private List<InternalConstraint> genInternalCtrsForDistLeq(DistLeq ectr) {
        List<InternalConstraint> ictrs = new ArrayList<>();
        ForbiddenRegionFrame f = ((ForbiddenRegionFrame) ectr.getFrame());
        DistLeqIC ic = new DistLeqIC(stp, f.q, f.D, f.s1, f.s2, f.o1, f.o2, ectr.getDistanceVar());
        ictrs.add(ic);
        return ictrs;
    }

    private List<InternalConstraint> genInternalCtrsForDistLinear(DistLinear ectr) {
        List<InternalConstraint> ictrs = new ArrayList<>();
        DistLinearFrame f = ((DistLinearFrame) ectr.getFrame());
        DistLinearIC ic = new DistLinearIC(stp, f.a, f.o1, f.b);
        ictrs.add(ic);
        return ictrs;
    }

}
