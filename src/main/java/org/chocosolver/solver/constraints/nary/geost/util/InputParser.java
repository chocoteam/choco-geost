/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:47Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.util;

import org.chocosolver.solver.Model;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.Shape;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;
import org.chocosolver.solver.variables.IntVar;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * This class parses a text file that describes the problem to be solved. While parsing it creates the choco problem, the objects, shapes and shifted boxes and stores them locally to this class.
 * Then to create the environment that the constraint uses all we need to do is call global.Setup.createEnvironment(parser) and give it this object as an argument.
 */
public final class InputParser {

    public static class GeostProblem {
        public int[][] objects;
        public int[] shapes;
        public int[][] shiftedBoxes;

        public GeostProblem(int[][] objects, int[] shapes, int[][] shiftedBoxes) {
            this.objects = objects;
            this.shapes = shapes;
            this.shiftedBoxes = shiftedBoxes;
        }
    }


    List<GeostObject> obj;
    List<Shape> sh;
    List<ShiftedBox> sb;
    String path;
    int dim;
    GeostProblem gp;

    public InputParser() {
    }

    public InputParser(String path, int dim) {
        this.path = path;
        this.dim = dim;
        obj = new ArrayList<>();
        sh = new ArrayList<>();
        sb = new ArrayList<>();
    }

    public InputParser(GeostProblem gp, int dim) {
        this.dim = dim;
        this.gp = gp;
        obj = new ArrayList<>();
        sh = new ArrayList<>();
        sb = new ArrayList<>();
    }


    public List<GeostObject> getObjects() {
        return this.obj;
    }

    public List<Shape> getShapes() {
        return this.sh;
    }

    public List<ShiftedBox> getShiftedBoxes() {
        return this.sb;
    }

    public boolean parse(Model model) throws IOException {
        if (path != null) {
            return this.parseFile(model);
        } else if (gp != null) {
            return this.parseGP(model);
        }
        return false;
    }

    /**
     * This is the essential function of this class it. It is the function that executes the parsing. The file to be parsed is read
     * from the local variable path. The value of path is given to the constructor as parameter.
     *
     * @return The function returns false if there was an error during the parsing otherwise it returns true.
     */
    public boolean parseFile(Model model) throws IOException {

		BufferedReader bin = null;
        try{
			//URL inputFileLocation = new URL(constants.INPUT_FILE_PATH);
			bin = new BufferedReader(new FileReader(this.path));
			//bin = new BufferedReader(new InputStreamReader(inputFileLocation.openStream()));
		}
		catch(Exception e)
		{
			System.err.println("Unable to open data file: "+this.path+"\n"+ e);
			e.printStackTrace();
			return false;
		}

		String str = "";
		String temp = "";
		String mode = "";
		while((str = bin.readLine()) != null) 
		{
			if (str.equals("")) continue;
			if (str.startsWith("#")) continue;
			
			if (str.equals("Objects") || str.equals("Shapes") || str.equals("ShiftedBoxes"))
			{
				mode = str;
				str = bin.readLine();
			}
 
			StringTokenizer st = new StringTokenizer(str, " ");
			int tokenNb = 0;
			
			int indice = 0;
			if (mode.equals("Objects"))
			{
				int id = 0;
                IntVar shape = null;
                IntVar[] coord = new IntVar[this.dim];
                IntVar start = null;
                IntVar duration= null;
                IntVar end= null;
                while (st.hasMoreTokens())
				{
			        temp = st.nextToken();
			        tokenNb++; 
			        switch (tokenNb)
			        {
			        	case 1:
			        		id =Integer.valueOf(temp);
			        		break;
			        	case 2:
			        		String temp2 = st.nextToken();
			        		tokenNb++;
			        		shape = model.intVar("sid_" + indice, Integer.valueOf(temp), Integer.valueOf(temp2), false);
			        		break;
			        }
			        
			        if (tokenNb > 3 && tokenNb <= (3 + ( 2 * this.dim)))
			        {
			        	for(int i = 0; i < this.dim; i++)
			        	{
			        		int lowerBound = -1, upperBound = -1;
			        		for (int j = 0; j < 2; j++)
			        		{
			        			if(j==0)
			        			{
			        				lowerBound = Integer.valueOf(temp);
                                }
			        			if(j==1)
			        			{
			        				upperBound = Integer.valueOf(temp);
                                }
			        			if(st.hasMoreTokens())
			        			{
			        				temp = st.nextToken();
				        			tokenNb++;
			        			}
			        		}
			        		coord[i] = model.intVar("x"+"_"+indice+"_" + i, lowerBound, upperBound, true);
			        	}
			        }
			        else if(tokenNb > (3 + ( 2 * this.dim)))
			        {
			        	for(int i = 0; i < 3; i++)
			        	{
			        		int lowerBound = -1, upperBound = -1;
			        		for (int j = 0; j < 2; j++)
			        		{
			        			if(j==0)
			        			{
			        				lowerBound = Integer.valueOf(temp);
			        			}
			        			if(j==1)
			        			{
			        				upperBound = Integer.valueOf(temp);
			        			}
			        			if(st.hasMoreTokens())
			        			{
			        				temp = st.nextToken();
				        			tokenNb++;
			        			}
			        		}
			        		if(i == 0)
			        		{
			        			start = model.intVar("start_"+indice, lowerBound, upperBound, true);
			        		}
			        		else if(i == 1)
			        		{
			        			duration = model.intVar("duration_"+indice, lowerBound, upperBound, true);
			        		}	
			        		else if(i == 2)
			        		{
			        			end = model.intVar("end_"+(indice++), lowerBound, upperBound, true);
			        		}
			        	}
			        }
			        
			    }
                GeostObject o = new GeostObject(this.dim, id, shape, coord, start, duration, end);
	        	obj.add(o);	
			}
			
			if (mode.equals("Shapes"))
			{
				Shape s = new Shape();
				temp = st.nextToken();
			    s.setShapeId(Integer.valueOf(temp));
			    sh.add(s);
			}
			
			if (mode.equals("ShiftedBoxes"))
			{
				ShiftedBox s = new ShiftedBox();
				while (st.hasMoreTokens()) 
				{
			        temp = st.nextToken();
			        tokenNb++; 
			        if (tokenNb == 1)
			        {
			        	s.setShapeId(Integer.valueOf(temp));
			        }
			        
			        if (tokenNb > 1 && tokenNb <= this.dim + 1)
			        {
			        	int[] off = new int[this.dim];
			        	for(int i = 0; i < this.dim; i++)
			        	{
			        		off[i] = Integer.valueOf(temp);
			        		if (i != this.dim - 1)
			        		{
			        			temp = st.nextToken();
				        		tokenNb++;
			        		}
			        		
			        	}
			        	s.setOffset(off);
		        	}
			        
			        if(tokenNb > 1 && tokenNb > this.dim + 1)
			        {
			        	int[] size = new int[this.dim];
			        	for(int i = 0; i < this.dim; i++)
			        	{
			        		size[i] = Integer.valueOf(temp);
			        		if (i != this.dim - 1)
			        		{
			        			temp = st.nextToken();
				        		tokenNb++;
			        		}
			        		
			        	}
			        	s.setSize(size);
			        }
			    }
			    sb.add(s);
			    
			    }
			}
		
		//add the shiftedboxes to their corresponding shapes linked lists
		for(int i = 0; i < sb.size(); i++)
		{
			int index = -1;
			for (int j = 0; j < sh.size(); j++)
			{
				if(sh.get(j).getShapeId() == sb.get(i).getShapeId()) 
					{
						index = j;
						break;
					}
		
			}
			if (index != -1)
				sh.get(index).addShiftedBox(sb.get(i));
		}

//		//Calculate the origin domain sizes
//		for (int i = 0; i < obj.size(); i++)
//			obj.get(i).setOriginDomainSize(obj.get(i).calculateOriginDomainSize());
        return true;
    }

    /**
     * This is the essential function of this class it. It is the function that executes the parsing. Lists to be parsed are read.
     *
     * @return The function returns false if there was an error during the parsing otherwise it returns true.
     */
    public boolean parseGP(Model model) {

		// Objects:
        for(int i =0; i < gp.objects.length; i++){
            int j =0;
            int id = gp.objects[i][j++];
            IntVar shape = model.intVar("sid_" + i, gp.objects[i][j++], gp.objects[i][j++], false);
            IntVar[] coord = new IntVar[this.dim];
            for(int k = 0; k < this.dim; k++){
                coord[k] = model.intVar("x_" + i + "_" + k, gp.objects[i][j++], gp.objects[i][j++], true);
            }

            IntVar start = model.intVar("start_" + i, gp.objects[i][j++], gp.objects[i][j++], false);
            IntVar duration= model.intVar("duration_" + i, gp.objects[i][j++], gp.objects[i][j++], false);
            IntVar end= model.intVar("end_" + i, gp.objects[i][j++], gp.objects[i][j], false);
            GeostObject o = new GeostObject(this.dim, id, shape, coord, start, duration, end);
	        obj.add(o);
        }
        // Shapes
        for(int i = 0; i < gp.shapes.length; i++){
            Shape s = new Shape();
            s.setShapeId(gp.shapes[i]);
            sh.add(s);
        }
        // Shifted boxes
        for(int i = 0; i < gp.shiftedBoxes.length; i++){
            int j = 0;
            ShiftedBox s = new ShiftedBox();
            s.setShapeId(gp.shiftedBoxes[i][j++]);
            int[] off = new int[this.dim];
            for(int k = 0; k < this.dim; k++){
                off[k] = gp.shiftedBoxes[i][j++];
            }
            s.setOffset(off);
            int[] size = new int[this.dim];
            for(int k = 0; k < this.dim; k++)
            {
                size[k] = gp.shiftedBoxes[i][j++];
            }
            s.setSize(size);
            sb.add(s);
        }
        //add the shiftedboxes to their corresponding shapes linked lists
		for(int i = 0; i < sb.size(); i++)
		{
			int index = -1;
			for (int j = 0; j < sh.size(); j++)
			{
				if(sh.get(j).getShapeId() == sb.get(i).getShapeId())
					{
						index = j;
						break;
					}

			}
			if (index != -1)
				sh.get(index).addShiftedBox(sb.get(i));
		}
        return true;
    }

}
