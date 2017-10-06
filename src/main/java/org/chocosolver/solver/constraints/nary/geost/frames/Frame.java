/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.frames;


import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Region;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;

/**
 * A class that all Frames should extend. It contains info and functionality common to all frames.
 */
public class Frame implements Externalizable {
	
	/**
	 * Integer for the object id and the vector is the relative Forbidden Regions of every shifted box of the shapes of the object
	 */
	private HashMap<Integer, List<Region>> RelForbidRegions;
	
	public Frame()
	{
		RelForbidRegions = new HashMap<>();
	}

	/**
	 * Gets the Relative forbidden regions of this frame. It return a hash table where the key is an Integer object representing the shape id and the value a vector of Region object.
	 */
	public final HashMap<Integer, List<Region>> getRelForbidRegions()
	{
		return RelForbidRegions;
	}
	
	/**
	 * Adds a given shape id and a Vector of regions to the Frame.
	 */
	public final void addForbidRegions(int oid, List<Region> regions)
	{
		this.RelForbidRegions.put(oid, regions);
	}
	
	/**
	 * Gets the Relative forbidden regions of a certain shape id. It returns Vector of Region object.
	 */
	public final List<Region> getRelForbidRegions(int oid)
	{
		return this.RelForbidRegions.get(oid);
	}
	
	/**
	 * Returns the size of the frame.
	 */
	public final int size()
	{
		return RelForbidRegions.size();
	}


    public final void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(RelForbidRegions);

    }

    @SuppressWarnings({"unchecked"})
    public final void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        //To change body of implemented methods use File | Settings | File Templates.
        RelForbidRegions=(HashMap<Integer, List<Region>>) in.readObject();        
    }
}
