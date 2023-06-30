/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.internalConstraints;

import java.io.Serializable;

/**
 * A class that all internal constraints should extend. It contains info and functionality common to all internal constraints.
 */
public class InternalConstraint implements Serializable {
	
	private int ictrID = 0;
	
	public InternalConstraint(int id)
	{
		this.ictrID = id;
	}
	
	public final int getIctrID()
	{
		return this.ictrID;
	}

	public final void setIctrID(int id)
	{
		this.ictrID=id;
	}
	
}
