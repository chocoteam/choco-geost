/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.externalConstraints;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 3 fevr. 2009
 * Time: 16:29:15
 * To change this template use File | Settings | File Templates.
 */
public class DistLinear extends ExternalConstraint{

    public int o1;
    public int[] a;
    public int b;

	public DistLinear(int ectrID, int[] dimensions, int[] objectIdentifiers, int[] a, int b)
	{
        super(ectrID, dimensions, null);
        int[] oids = new int[1];
        oids[0] = objectIdentifiers[0];
        setObjectIds(oids); //Prune only the first object!
        o1=objectIdentifiers[0];
        this.a=a; this.b=b;       
	}

}
