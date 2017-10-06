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

/**
 * A class that represent the Outbox internal constraint. If this constraint is
 * applied to an object, it forces the origin of the object to be outside its
 * box defined by an offset and a size in each dimension
 */
public final class Outbox extends InternalConstraint {

	private int[] t;
	private int[] l;
	
	
	public Outbox(int[] t, int[] l)
	{
		super(Constants.OUTBOX);
		this.t = t;
		this.l = l;
	}


	public int[] getL() {
		return l;
	}
	
	public int getL(int index) {
		return this.l[index];
	}


	public void setL(int[] l) {
		this.l = l;
	}


	public int[] getT() {
		return t;
	}
	
	public int getT(int index)
	{
		return this.t[index];
	}


	public void setT(int[] t) {
		this.t = t;
	}

    public String toString() {
        StringBuilder res = new StringBuilder();
        for (int aT : t){
            res.append("[").append(aT).append("],");
        }
        for (int i=0; i<t.length; i++){
            res.append("[").append(l[i]).append("],");
        }

        return res.toString();
    }

    public int adjacent(Outbox ob)
        //returns the dimensions along which ob is adjacent to the object
        //otherwise returns -1
        //Condition for adjacency, regarding the origin:
        //1) adjacent in at most one dimension
        //2) the other dimensions must be equal
    {
        int already_found=-1;
        for (int i=0; i<t.length; i++) {
            if ((t[i]+l[i]==ob.getT(i)) || (ob.getT(i)+ob.getL(i)==t[i] ))
                if (already_found!=-1) return -1; else already_found=i;
            if ((i!=already_found) && (t[i]!=ob.getT(i))) return -1;
        }
        return already_found;
    }

    public void merge(Outbox ob, int dim)
    //PRE: dim=adjacent(ob) and sameSize(ob,dim)
    {
//        System.out.println("before computation dim:"+dim+" l:"+l[dim]);
//        System.out.println("before computation dim:"+dim+" ob.l:"+ob.getL(dim));

       if (ob.getT(dim)<t[dim]) {
           t[dim]=ob.getT(dim);
           l[dim]+=ob.getL(dim);
       }
       else {
           l[dim]+=ob.getL(dim);
       }
//        System.out.println("after computation dim:"+dim+" l:"+l[dim]);
        
    }

    public boolean sameSize(Outbox ob, int dim)
            //ob has the same size except for the dimension dim
    {
     for (int i=0; i<t.length; i++) {
         if (i==dim) continue;
         if (l[i]!=ob.getL(i)) return false;
     }
     return true;
    }

}
