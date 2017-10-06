/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.internalConstraints;

import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Region;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 f�vr. 2009
 * Time: 15:24:07
 * To change this template use File | Settings | File Templates.
 */

/*Implementation of Chapter 7 rev 108
        "A Characterisation of a Class of Forbidden Sets with Applications to distance and linear constraints"*/

/*
 * ForbiddenRegion is a general name for an internal constraint.
 * It contains all the needed services to jump from a sweep point p to a feasible position.
 */

public abstract class ForbiddenRegion extends InternalConstraint {

    public ForbiddenRegion() {
        super(Constants.FORBID_REGION);
    }


    /* sweep.tex r108 Chapter 7 - Algorithm 149 'isFeasible'
     * false if p notin F, otherwise computes the forbidden box f */
    public List isFeasible(boolean min, int dim, int k, GeostObject o, Point p, Point jump) {
        //System.out.println("-- ENTERING ForbiddenRegion.isFeasible;p:"+p+";jump:"+jump);
        Region f = new Region(k, o.getObjectId());
        List<Object> result = new ArrayList<>();
        if (!insideForbidden(p)) { /*line 1*/
            //System.out.println("Not Inside forbidden");
            result.add(0, true);
            result.add(1, p);
            return result; /*line 2:return (true,p)*/
        }
        //System.out.println("Inside forbidden");
        for (int i = 0; i < k; i++) {
            int v = p.getCoord(i);
            f.setMinimumBoundary(i, v);
            f.setMaximumBoundary(i, v);
        }  /*line 5:f.min<-f.max<-p*/

        //System.out.println("min:"+min);
        if (min) { /*line 6:if min then*/
            for (int j = k - 1; j >= 0; j--) { /*line 8:for j<-k-1 downto 0 do*/
                int j_prime = (j + dim) % k;   /*line 9:j'<-(j+dim) mod k*/
                //System.out.println("j_prime:"+j_prime);
                /*line 10:f.max[j']<-min(jump[j']-1,MaximizeSizeOfFBox(F,true,j',k,f)*/
                //System.out.println("max:"+Math.min(jump.getCoord(j_prime)-1,maximizeSizeOfFBox(true,j_prime,k,f))+",jump.getCoord(j_prime)-1:"+(jump.getCoord(j_prime)-1)+",maximizeSizeOfFBox(true,j_prime,k,f):"+maximizeSizeOfFBox(true,j_prime,k,f));
                //System.out.println("max:"+Math.min(jump.getCoord(j_prime)-1,maximizeSizeOfFBox(true,j_prime,k,f))+",jump.getCoord(j_prime)-1:"+(jump.getCoord(j_prime)-1)+",maximizeSizeOfFBox(true,j_prime,k,f):"+maximizeSizeOfFBox(true,j_prime,k,f));

                f.setMaximumBoundary(j_prime, Math.min(jump.getCoord(j_prime) - 1, maximizeSizeOfFBox(true, j_prime, k, f)));
                //System.out.println("f:"+f);
            }
        } else {
            for (int j = k - 1; j >= 0; j--) { /*line 14:for j<-k-1 downto 0 do*/
                int j_prime = (j + dim) % k;   /*line 15:j'<-(j+dim) mod k*/
                /*line 16:f.min[j']<-max(jump[j']+1,MaximizeSizeOfFBox(F,false,j',k,f)*/
                f.setMinimumBoundary(j_prime, Math.max(jump.getCoord(j_prime) + 1, maximizeSizeOfFBox(false, j_prime, k, f)));
            }
        }


        //System.out.println("RETURNING forbidden box:"+f);
        result.add(0, false);
        result.add(1, f);
        //System.out.println("-- EXITING ForbiddenRegion.isFeasible");
        return result; /*line 19:return (false,f)*/
    }

    /* sweep.tex r108 Chapter 7 - Algorithm 150 'InsideForbidden'
     * returns true if p belongs to the forbidden region F and false otherwise */
    public abstract boolean insideForbidden(Point p);

    /* sweep.tex r108 Chapter 7 - Algorithm 'MaximizeSizeOfFBox'
     * computes the largest extended f d/e box remains included in F if min=true
     * computes the smallest extended f d/e box remains included in F if min=false */
    public abstract int maximizeSizeOfFBox(boolean min, int d, int k, Region f);


}
