/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.util;


import org.chocosolver.solver.constraints.nary.geost.Constants;
import org.chocosolver.solver.constraints.nary.geost.Setup;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;

import java.util.Iterator;
import java.util.List;

/**
 * After solving and finding a solution, this class provides a function that tests whether a given solution is a valid solution. 
 * It is only for the non_overlapping constraint. 
 */
public final class SolutionTester {
	Setup stp;
	Constants cst;
	public SolutionTester(Setup s, Constants c)
	{
		this.stp = s;
		this.cst = c; 
	}
	
	public boolean testSolution()
	{
		
		Iterator objItr1;
		Iterator objItr2;
		
		objItr1 = stp.getObjectKeySet().iterator();
		while(objItr1.hasNext())
		{
			int oid1 = (Integer) objItr1.next();
			GeostObject o1 = stp.getObject(oid1);
			objItr2 = stp.getObjectKeySet().iterator();
			while(objItr2.hasNext())
			{
				int oid2 = (Integer) objItr2.next();
                GeostObject o2 = stp.getObject(oid2);
				if(oid1 != oid2)
				{
					//check for intersection: Two objects do not intersect if there exist at least on dim where they do not intersect
					List<ShiftedBox> sb1 = stp.getShape(o1.getShapeId().getLB());
					List<ShiftedBox> sb2 = stp.getShape(o2.getShapeId().getLB());
					
					for(int i = 0; i < sb1.size(); i++)
					{
						for(int j = 0; j < sb2.size(); j++)
						{
							boolean intersect = true;
							for(int k = 0; k < cst.getDIM(); k++)
							{
								if(!(
										((sb2.get(j).getOffset(k) + o2.getCoord(k).getLB() >= sb1.get(i).getOffset(k) + o1.getCoord(k).getLB()) &&
										(sb2.get(j).getOffset(k) + o2.getCoord(k).getLB() < sb1.get(i).getOffset(k) + o1.getCoord(k).getLB()  + sb1.get(i).getSize(k)))
										||
										((sb2.get(j).getOffset(k) + o2.getCoord(k).getLB() + sb2.get(j).getSize(k) > sb1.get(i).getOffset(k) + o1.getCoord(k).getLB()) && 
										(sb2.get(j).getOffset(k) + o2.getCoord(k).getLB() + sb2.get(j).getSize(k) <= sb1.get(i).getOffset(k) + o1.getCoord(k).getLB() + sb1.get(i).getSize(k)))
									)
								   )
								{
									intersect = false;
									break;
								}
							}
							if(intersect)
								return false;
						}
					}
				}
			}
		}
		return true;
	}

}
