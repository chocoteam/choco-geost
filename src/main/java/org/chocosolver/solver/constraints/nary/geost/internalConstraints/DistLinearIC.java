/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:34Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.internalConstraints;

import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.Setup;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Point;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Region;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.util.iterators.DisposableValueIterator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 4 f�vr. 2009
 * Time: 10:11:25
 * To change this template use File | Settings | File Templates.
 */
public final class DistLinearIC extends ForbiddenRegion {

    public int o1;
    public int[] a;
    public int b;
    public Setup stp;
    public int D;

    public DistLinearIC(final Setup stp_, final int[] a, final int o1, final int b) {
        this.setIctrID(Constants.DIST_LINEAR_FR);
        stp = stp_;
        this.a = a;
        this.b = b;
        this.o1 = o1;
        this.D = b;
    }

    /* sweep.tex r108 Chapter 7 page 277 - Algorithm 177 'isFeasible'
     * false if p notin F, otherwise computes the forbidden box f */
    public List isFeasible(final boolean min, final int dim, final int k, final GeostObject o, final Point p, final Point jump) {
        System.out.println("-- ENTERING DistLinearIC.isFeasible;p:" + p + ";jump:" + jump);

        D = compute_D(k); /*line 1*/
        if (D < 0) D = 0;

        final Region f = new Region(k, o.getObjectId());
        final List<Object> result = new ArrayList<>();

        if (!insideForbidden(p)) { /*line 1*/
            System.out.println("Not Inside forbidden");
            result.add(0, true);
            result.add(1, p);
            return result; /*line 2:return (true,p)*/
        }

        System.out.println("Inside forbidden");
        for (int i = 0; i < k; i++) {
            final int v = p.getCoord(i);
            f.setMinimumBoundary(i, v);
            f.setMaximumBoundary(i, v);
        }  /*line 5:f.min<-f.max<-p*/

        System.out.println("min:" + min);
        final int[] m = new int[k];
        int sum = 0; /*line 6*/
        for (int i = 0; i < k; i++) { /*line 7*/
            if (a[i] > 0) /*line 8*/
                m[i] = a[i] * f.getMinimumBoundary(i); /*line 9*/
            else
                m[i] = a[i] * f.getMaximumBoundary(i); /*line 11*/
            sum += m[i]; /*line 13*/
        }
        System.out.println("m1:" + m[0] + ";m2:" + m[1]);

        if (min) { /*line 6:if min then*/
            for (int j = k - 1; j >= 0; j--) { /*line 16:for j<-k-1 downto 0 do*/
                final int j_prime = (j + dim) % k;   /*line 17:j'<-(j+dim) mod k*/
                System.out.println("j_prime:" + j_prime);
                if (a[j_prime] >= 0) /*line 18*/
                    f.setMaximumBoundary(j_prime, stp.getObject(o1).getCoord(j_prime).getUB()); /*line 19*/
                else {
                    final double sup = (double) (D - (sum - m[j_prime]));
                    final double inf = (double) a[j_prime];
                    final int term = (int) Math.ceil(sup / inf);

                    System.out.println("D:" + D + ";sum:" + sum + ";m:" + m[j_prime] + ";a:" + a[j_prime] + ";term:" + term);
                    f.setMaximumBoundary(j_prime, Math.min(jump.getCoord(j_prime) - 1, term - 1)); /*line 21*/
                    m[j_prime] = a[j_prime] * f.getMaximumBoundary(j_prime); /*line 22*/
                }
            }
        } else {
            for (int j = k - 1; j >= 0; j--) { /*line 26:for j<-k-1 downto 0 do*/
                final int j_prime = (j + dim) % k;   /*line 27:j'<-(j+dim) mod k*/
                System.out.println("j_prime:" + j_prime);
                if (a[j_prime] <= 0)
                    f.setMinimumBoundary(j_prime, stp.getObject(o1).getCoord(j_prime).getLB());
                else {
                    final double sup = (double) (D - (sum - m[j_prime]));
                    final double inf = (double) a[j_prime];
                    final int term = (int) Math.floor(sup / inf);
                    System.out.println("D:" + D + ";sum:" + sum + ";m:" + m[j_prime] + ";a:" + a[j_prime] + ";term:" + term);
                    f.setMinimumBoundary(j_prime, Math.max(jump.getCoord(j_prime) + 1, term + 1)); /*line 31*/
                    m[j_prime] = a[j_prime] * f.getMinimumBoundary(j_prime); /*line 32*/
                }

            }
        }

        System.out.println("RETURNING forbidden box:" + f);
        result.add(0, false);
        result.add(1, f);
        System.out.println("-- EXITING ForbiddenRegion.isFeasible");
        return result; /*line 19:return (false,f)*/
    }

    int compute_D(final int k) /*computes line 1 of Algorithm 177*/ {
        final DisposableValueIterator it = stp.getObject(o1).getShapeId().getValueIterator(true);
        int max = 0;
        boolean max_not_assigned = true;

        while (it.hasNext()) {
            final int sid = it.next();
            boolean min_sid_not_assigned = true;
            int min_sid = -1;
            for (final ShiftedBox sb : stp.getShape(sid)) {

                //r=a*s.t
                int r = 0;
                for (int i = 0; i < k; i++)
                    r += a[i] * sb.getOffset(i);
                // sum max(a[i],0)*s.l[i]
                int sum = 0;
                for (int i = 0; i < k; i++) {
                    final int s_l_i = sb.getSize(i);
                    final int max_a_i = Math.max(a[i], 0);
                    sum += max_a_i * s_l_i;
                }
                if ((min_sid_not_assigned) || (min_sid > b - r - sum)) min_sid = b - r - sum;
                min_sid_not_assigned = false;
            }
            if ((max_not_assigned) || (max < min_sid)) max = min_sid;
            max_not_assigned = false;
        }
        it.dispose();
        return max;
    }

    /* sweep.tex r108 Chapter 7 - Algorithm 174 'InsideForbidden'
     * returns true if p belongs to the forbidden region F and false otherwise */
    public boolean insideForbidden(final Point p) {
//           System.out.println("-- ENTERING DistLinear.segInsideForbidden");
//           System.out.println("DistLinear.segInsideForbidden("+p+") call");
//           System.out.println("o1:"+stp.getObject(o1));
//           System.out.println("-- EXITING DistLinear.segInsideForbidden : "+a+" "+p+"product(a,p):"+product(a,p)+":"+(product(a,p)>D));
        return (product(a, p) > D);
    }

    public int product(final int[] v, final Point p) {
        int result = 0;
        for (int i = 0; i < v.length; i++) {
            result += v[i] * p.getCoord(i);
        }
        return result;
    }

    //Algorithm 177 p.280 r204
    @Override
    public int maximizeSizeOfFBox(final boolean min, final int d, final int k, final Region f) {
        final int[] m = new int[a.length];
        //1: for i = 0 to k - 1 do
        for (int i = 0; i < k - 1; i++) {
            //2: if a[i] > 0 then
            if (a[i] > 0) {
                //3: m[i] <- a[i] . f.min [i]
                m[i] = a[i] * f.getMinimumBoundary(i);

            } else //4:else
            {
                //5: m[i] <- a[i] . f.max [i]
                m[i] = a[i] * f.getMaximumBoundary(i);
            }
        }


        //8:if min then
        if (min) {
            //9:if a[d]>=0 then
            if (a[d] >= 0) {
                //10:return +inf
                return stp.getObject(o1).getCoord(d).getUB();
            } else //11:else
            {
                //12:return ...
                D = b;
                int r = 0;
                for (int j = 0; j < k; j++) {
                    if (k != d) {
                        r += m[j];
                    }
                }
                final double num = (double) (D - r);
                final double den = (double) a[d];
                return ((int) Math.ceil(num / den)) - 1;
            }
        }
        //14:else
        else {
            //15:if a[d]<=0 then
            if (a[d] <= 0) {
                //16:return -inf
                return stp.getObject(o1).getCoord(d).getLB();
            } else { //17:else
                //18: return ...
                D = b;
                int r = 0;
                for (int j = 0; j < k; j++) {
                    if (k != d) {
                        r += m[j];
                    }
                }

                final double num = (double) (D - r);
                final double den = (double) a[d];
                return ((int) Math.floor(num / den)) + 1;
            }
        }
    }

}
