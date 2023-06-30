/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.internalConstraints;

import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.Setup;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Point;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Region;
import org.chocosolver.solver.exception.ContradictionException;
import org.chocosolver.solver.exception.SolverException;
import org.chocosolver.solver.variables.IntVar;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 4 fevr. 2009
 * Time: 10:11:25
 * To change this template use File | Settings | File Templates.
 */
public final class DistGeqIC extends ForbiddenRegion {

    public int q, D, s1, s2, o1, o2;
    IntVar DVar = null; //DVar is the distance variable; valid only if (D_init_lower_bnd!=null && D_init_upper_bnd!=null)

    public Setup stp;

    public DistGeqIC(Setup stp_, int q_, int D_, int s1_, int s2_, int o1_, int o2_) {
        this.setIctrID(Constants.DIST_GEQ_FR);

        stp = stp_;
        q = q_;
        D = D_;
        s1 = s1_;
        s2 = s2_;
        o1 = o1_;
        o2 = o2_;
        if (q != 2) {
            throw new SolverException("DistGeqIC:Only norm 2 is supported for now.");
        }
    }

    public DistGeqIC(Setup stp_, int q_, int D_, int s1_, int s2_, int o1_, int o2_, IntVar DVar_) {
        this.setIctrID(Constants.DIST_GEQ_FR);

        stp = stp_;
        q = q_;
        D = D_;
        s1 = s1_;
        s2 = s2_;
        o1 = o1_;
        o2 = o2_;
        if (q != 2) {
            throw new SolverException("DistGeqIC:Only norm 2 is supported for now.");
        }
        DVar = DVar_;
    }


    /* sweep.tex r108 Chapter 7 - Algorithm 161 'InsideForbidden'
     * returns true if p belongs to the forbidden region F and false otherwise */
    public boolean insideForbidden(Point p) {
        //LOGGER.info("-- ENTERING segInsideForbidden");
        //LOGGER.info("segInsideForbidden("+p+") call");
        //LOGGER.info("o1:"+stp.getObject(o1));
        //LOGGER.info("o2:"+stp.getObject(o2));
        if (DVar != null) D = DVar.getLB();
        int k = p.getCoords().length;
        Point m = new Point(k);
        for (int i = 0; i < k; i++) { /*line 1*/
            int p_i = p.getCoord(i);
            int s1_t_i = stp.getShape(s1).get(0).getOffset(i); //suppose there is only one shifted box
            int s2_t_i = stp.getShape(s2).get(0).getOffset(i); //suppose there is only one shifted box
            int o2_x_i_lb = stp.getObject(o2).getCoord(i).getLB();
            int o2_x_i_ub = stp.getObject(o2).getCoord(i).getUB();
            int s1_l_i = stp.getShape(s1).get(0).getSize(i); //suppose there is only one shifted box
            int s2_l_i = stp.getShape(s2).get(0).getSize(i); //suppose there is only one shifted box

            int N1 = Math.max(p_i + s1_t_i, o2_x_i_ub + s2_t_i); /*line 33*/
            int N2 = Math.min(p_i + s1_t_i + s1_l_i, o2_x_i_ub + s1_t_i + s2_l_i); /*line 4*/
            int M1 = Math.max(0, N1 - N2); /*line 5*/
            int N1_ = Math.max(p_i + s1_t_i, o2_x_i_lb + s2_t_i); /*line 6*/
            int N2_ = Math.min(p_i + s1_t_i + s1_l_i, o2_x_i_lb + s2_t_i + s2_l_i); /*line 7*/
            int M2 = Math.max(0, N1_ - N2_); /*line 8*/
            m.setCoord(i, Math.max(M1, M2)); /*line 9:M[i]<-...*/
        }

        double norm = norm(m); //norm computes the q-norm

        //LOGGER.info("-- EXITING segInsideForbidden : "+m+" "+norm+"<"+D+":"+(norm<D));

        return (norm < D); /*line 11-15*/
    }

    //Algorithm 162 p.259 sweep.pdf r108
    public int maximizeSizeOfFBox(boolean min, int d, int k, Region f) {
        //LOGGER.info("ENTERING maximizeSizeOfFBox DistGeqIC");
        if (stp.opt.debug) {
            if (!insideForbidden(f.pointMin())) {
                throw new SolverException("Error precondition pointMin in DistGeqIC");
            }
            if (!insideForbidden(f.pointMax())) {
                throw new SolverException("Error precondition pointMax in DistGeqIC");
            }
        }
        if (DVar != null) D = DVar.getLB();
        Point m = new Point(k);
        for (int i = 0; i < k; i++) { /*line 1*/
            int f_min_i = f.getMinimumBoundary(i);
            int f_max_i = f.getMaximumBoundary(i);
            int s1_t_i = stp.getShape(s1).get(0).getOffset(i); //suppose there is only one shifted box
            int s2_t_i = stp.getShape(s2).get(0).getOffset(i); //suppose there is only one shifted box
            int o2_x_i_lb = stp.getObject(o2).getCoord(i).getLB();
            int o2_x_i_ub = stp.getObject(o2).getCoord(i).getUB();
            int s1_l_i = stp.getShape(s1).get(0).getSize(i); //suppose there is only one shifted box
            int s2_l_i = stp.getShape(s2).get(0).getSize(i); //suppose there is only one shifted box

            int N1 = Math.max(f_min_i + s1_t_i, o2_x_i_ub + s2_t_i); /*line 3*/
            int N2 = Math.min(f_min_i + s1_t_i + s1_l_i, o2_x_i_ub + s1_t_i + s2_l_i); /*line 4*/
            int M1 = Math.max(0, N1 - N2); /*line 5*/
            int N1_ = Math.max(f_max_i + s1_t_i, o2_x_i_lb + s2_t_i); /*line 6*/
            int N2_ = Math.min(f_max_i + s1_t_i + s1_l_i, o2_x_i_lb + s2_t_i + s2_l_i); /*line 7*/
            int M2 = Math.max(0, N1_ - N2_); /*line 8*/
            m.setCoord(i, Math.max(M1, M2)); /*line 9:M[i]<-...*/
        }

        int s1_t_d = stp.getShape(s1).get(0).getOffset(d); //suppose there is only one shifted box
        int s2_t_d = stp.getShape(s2).get(0).getOffset(d); //suppose there is only one shifted box
        int o2_x_d_lb = stp.getObject(o2).getCoord(d).getLB();
        int o2_x_d_ub = stp.getObject(o2).getCoord(d).getUB();
        int s1_l_d = stp.getShape(s1).get(0).getSize(d); //suppose there is only one shifted box
        int s2_l_d = stp.getShape(s2).get(0).getSize(d); //suppose there is only one shifted box

        double q_sum = q_sum(m, d);
        //double norm = sqrt(q_sum,q);
        ///double norm = q_sum;

        //checkSqrt(q_sum,norm);
        double term = sqrt(Math.pow(D, q) - q_sum);
        if (term >= 0) checkSqrt(Math.pow(D, q) - q_sum, term);
        //int term_down = (int) Math.floor(term);
        //int term_up=term_down;
        //if (((double)term_down) != term) term_up++;

        //LOGGER.info("f:"+f+";m:"+m+";d:"+d+";D:"+D+";q_sum:"+q_sum+";term:"+term+";plus_inf:"+plus_infinity+";minus_inf:"+minus_infinity);
        //LOGGER.info("term:"+term+";o2_x_d_lb:"+o2_x_d_lb+";s2_t_d:"+s2_t_d+";s2_l_d:"+s2_l_d+";s1_t_d:"+s1_t_d);

        if (min) /*line 12*/
            return ((int) Math.ceil(term + o2_x_d_lb + s2_t_d + s2_l_d)) - 1 - s1_t_d; /*line 13*/

        return ((int) Math.floor(o2_x_d_ub + s2_t_d + s1_l_d - term)) + 1 - s1_t_d; /*line 15*/
    }

    private double q_sum(Point m, int d) {
        int k = m.getCoords().length;
        //LOGGER.info("m:"+m+",d:"+d);
        double sum = 0;
        for (int i = k - 1; i >= 0; i--) {
            if (i != d) {
                double r = 1;
                for (int j = 0; j < q; j++) {
                    r *= Math.abs(m.getCoord(i));
                    if (r == Double.POSITIVE_INFINITY) {
                        throw new SolverException("DestGeqIC:q_sum():r:double limit reached");
                    }
                }

                sum += r;
                if (sum == Double.POSITIVE_INFINITY) {
                    throw new SolverException("DestGeqIC:q_sum():sum:double limit reached");
                }
            }
        }

        return sum;

    }

    private double sqrt(double sum) {
        return Math.sqrt(sum);
    }

    private double norm(Point m) {
        return sqrt(q_sum(m, -1));

    }

    private void checkSqrt(double value, double result) {
        //First check if the value to be square rooted is an integer
        if (value < 0.0) {
            throw new SolverException("DistGeqIC:checkSqrt(): value is negative:" + value);
        }

        double ivalue = Math.floor(value);

        if (ivalue != value) {
            throw new SolverException("DistGeqIC:checkSqrt(): sqrt value is not an integer double value:" + value + " integer value:" + ivalue);
        }
        long lb = (int) Math.floor(result);
        long ub = (int) Math.ceil(result);
        if (lb * lb > ivalue) {
            throw new SolverException("DistGeqIC:checkSqrt(): lb is wrong:" + value + "," + result);
        }
        if (ub * ub < ivalue) {
            throw new SolverException("DistGeqIC:checkSqrt(): ub is wrong:ub:" + ub + " value:" + value + "," + result + " ub*ub:" + ub * ub + "<ivalue:" + ivalue);
        }
    }


    public boolean insideForbidden(Point c0, Point c1) {
        if (DVar != null) D = DVar.getLB();
        return (insideForbidden(c0) && insideForbidden(c1));
    }

    public String toString() {
        StringBuilder r = new StringBuilder();
        if (DVar == null) {
            r.append("GeqIC(D=").append(D).append(",q=").append(q).append(",o1=")
                    .append(o1).append(",o2=").append(o2).append(")");
        } else {
            r.append("GeqIC(D=[").append(DVar.getLB()).append(",").append(DVar.getUB())
                    .append("],q=").append(q).append(",o1=").append(o1).append(",o2=").append(o2).append(")");
        }

        return r.toString();
    }

    public int EvaluateMaximumDistance(int k) {
        double dist = 0.0;
        for (int d = 0; d < k; d++) {
            int o1_inf = stp.getObject(o1).getCoord(d).getLB();
            int o2_inf = stp.getObject(o2).getCoord(d).getLB();
            int o1_sup = stp.getObject(o1).getCoord(d).getUB();
            int o2_sup = stp.getObject(o2).getCoord(d).getUB();

            double m1 = Math.max(o1_inf, o2_sup) - Math.min(o1_inf, o2_sup);
            double m2 = Math.max(o1_sup, o2_inf) - Math.min(o1_sup, o2_inf);
            double max = Math.max(m1, m2);
            dist += max * max;
        }

        return (int) Math.floor(Math.sqrt(dist));

    }

    public boolean updateDistance(int k) throws ContradictionException {
        //returns true if disntace has been updated
        if (DVar != null) {
            int oldSup = DVar.getUB();
            int newSup = EvaluateMaximumDistance(k);
            if (oldSup <= newSup) return false;
            DVar.updateUpperBound(newSup, this.stp.g_constraint);
            if (stp.opt.debug) {
                System.out.println("DistGeqIC:" + this + " updateDistance:[" + DVar.getLB() + "," + DVar.getUB() + "]");
            }
            if ((DVar.getLB() > DVar.getUB()) || (DVar.getUB() < DVar.getLB())) {
                stp.g_constraint.fails();
            }
            return true;
        }
        return false;
    }

    public boolean hasDistanceVar() {
        return (DVar != null);
    }

    public IntVar getDistanceVar() {
        return DVar;
    }


}
