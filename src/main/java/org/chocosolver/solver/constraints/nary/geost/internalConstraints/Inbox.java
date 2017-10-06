/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.internalConstraints;

import org.chocosolver.solver.constraints.nary.geost.Constants;

/**
 * A class that represent the Inbox internal constraint. If this constraint is applied to an object, it forces the origin of the object to be inside 
 * its box defined by an offset and a size in each dimension
 */
public final class Inbox extends InternalConstraint {
	
	private int[] t;
	private int[] l;
		
	public Inbox(int[] t, int[] l)
	{
		super(Constants.INBOX);
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

}
