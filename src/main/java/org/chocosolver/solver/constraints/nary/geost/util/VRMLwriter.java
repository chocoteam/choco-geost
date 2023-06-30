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
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.GeostObject;
import org.chocosolver.solver.constraints.nary.geost.geometricPrim.ShiftedBox;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * This class provides a function that writes a certain solution (after solving and getting a solution)
 * to a wrl file in the Virtual Reality Modeling Language (VRML). This file can later be visualized using the VRMLviewer tool.
 * The ouput file is written in a folder specified in the VRML_OUTPUT_FOLDER variable in the global.Constants class.
 */
public final class VRMLwriter {

    private VRMLwriter() {
    }

    public static final String VRML_OUTPUT_FOLDER = "";


    public static boolean printVRML3D(Setup s, Constants c, String name) {
        return printVRML3D(s, c, VRML_OUTPUT_FOLDER, name);
    }

    public static boolean printVRML3D(Setup s, Constants c, String outPut, String name) {
        String str;
        str = "" + outPut + name + ".wrl";
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(str));
            System.out.println("writing the VRML to : "+ str);
            Iterator itr;
            itr = s.objects.keySet().iterator();

            int kdim = 0;
            if (c.getDIM() == 2)
                kdim = 2;
            else if (c.getDIM() == 3)
                kdim = 3;
            else
                kdim = 0;

            out.write("#VRML V2.0 utf8" + '\n');

            if (kdim > 1 && kdim < 4) {
                while (itr.hasNext()) {
                    int id = (Integer) itr.next();
                    //Obj o = s.objects.get(new Integer(id));
                    GeostObject o = s.getObject(id);

                    StringBuilder temp = new StringBuilder();
                    for (int i = 0; i < c.getDIM(); i++) {
                        temp.append(o.getCoord(i).getLB()).append(" ");
                    }

                    if (kdim == 2)
                        temp.append(" 0.0 ");


                    out.write("Transform { translation " + temp.toString() + '\n');
                    out.write("children [ " + '\n');
                    List<ShiftedBox> sb = s.shapes.get(Integer.valueOf(o.getShapeId().getLB()));

                    Random rnd = new Random();
                    float fDiff1 = rnd.nextFloat();
                    float fDiff2 = rnd.nextFloat();
                    float fDiff3 = rnd.nextFloat();
                    float fSpec1 = rnd.nextFloat();
                    float fSpec2 = rnd.nextFloat();
                    float fSpec3 = rnd.nextFloat();
                    float shine = rnd.nextFloat();
                    String appearance = "appearance  Appearance {" +
                            "material  Material { " +
                            " ambientIntensity  0.25 " +
                            " diffuseColor  " + fDiff1 + " " + fDiff2 + " " + fDiff3 + " " +
                            " specularColor  " + fSpec1 + " " + fSpec2 + " " + fSpec3 + " " +
                            " emissiveColor  0 0 0 " +
                            " shininess  " + shine + " " +
                            " transparency  0.40 }} ";

                    double sizeOnZOfFirstShiftedBoxOfObject = 0.0;
                    for (int i = 0; i < sb.size(); i++) {

                        //out.write("Shape { geometry Box { size ");
                        temp.setLength(0);
                        for (int j = 0; j < c.getDIM(); j++) {
                            double k = (sb.get(i).getOffset(j) + (sb.get(i).getSize(j) / 2.0f));
                            temp.append("").append(k).append(" ");
                        }

//						the translation (on the z-axis) for the text label on the box
                        sizeOnZOfFirstShiftedBoxOfObject = sb.get(0).getSize(c.getDIM() - 1);

                        if (kdim == 2)
                            temp.append(" 0.0 ");

                        out.write("Transform { translation " + temp.toString() + '\n');
                        out.write("children [ ");
                        temp.setLength(0);
                        for (int j = 0; j < c.getDIM(); j++) {
                            int k = sb.get(i).getSize(j);
                            temp.append("").append(k).append(" ");
                        }

                        if (kdim == 2)
                            temp.append(" 0.0 ");

                        out.write("Shape { " + appearance);

                        out.write(" geometry Box { size " + temp.toString() + "}}]}" + '\n');
//						out.write(" geometry Box { size " + temp + "}}");


                    }
                    out.write(" Transform { translation .0 .0 " + sizeOnZOfFirstShiftedBoxOfObject + " \n");
                    out.write("children [ ");
                    out.write("   Shape {appearance Appearance { material Material { diffuseColor .0 .0 .0  transparency  0}} " +
                            "geometry Text { string [ \" " + o.getObjectId() + "\" ");
                    out.write("]  fontStyle FontStyle { style \"BOLD\" size 2.0 }}}" + '\n');
                    out.write(" ]} " + '\n');
                    out.write(" ]} " + '\n');
                }

                StringBuilder temp = new StringBuilder();

                if (kdim == 3) {
                    temp.append(" 25.0 0.1 0.1 ");
//					the x-axis
                    out.write("Transform { translation " + temp.toString() + '\n');
                    out.write("children [ ");
                    out.write("Shape { " + "appearance  Appearance {" +
                            "material  Material { " +
                            " diffuseColor  1.0 0.0 0.0 }} ");
                    out.write(" geometry Box { size 50.0 0.2 0.2 }}]}" + '\n');

                    temp.setLength(0);
                    temp.append(" 0.1 25.0 0.1 ");
//					the y-axis
                    out.write("Transform { translation " + temp.toString() + '\n');
                    out.write("children [ ");
                    out.write("Shape { " + "appearance  Appearance {" +
                            "material  Material { " +
                            " diffuseColor  0.0 1.0 0.0 }} ");
                    out.write(" geometry Box { size 0.2 50.0 0.2 }}]}" + '\n');

                    temp.setLength(0);
                    temp.append(" 0.1 0.1 25.0 ");
//					the z-axis
                    out.write("Transform { translation " + temp.toString() + '\n');
                    out.write("children [ ");
                    out.write("Shape { " + "appearance  Appearance {" +
                            "material  Material { " +
                            " diffuseColor  0.0 0.0 1.0 }} ");
                    out.write(" geometry Box { size 0.2 0.2 50.0 }}]}" + '\n');
                } else if (kdim == 2) {
                    temp.setLength(0);
                    temp.append(" 25.0 0.1 0.0 ");
//					the x-axis
                    out.write("Transform { translation " + temp.toString() + '\n');
                    out.write("children [ ");
                    out.write("Shape { " + "appearance  Appearance {" +
                            "material  Material { " +
                            " diffuseColor  1.0 0.0 0.0 }} ");
                    out.write(" geometry Box { size 50.0 0.2 0.0 }}]}" + '\n');

                    temp.setLength(0);
                    temp.append(" 0.1 25.0 0.0 ");
//					the y-axis
                    out.write("Transform { translation " + temp.toString() + '\n');
                    out.write("children [ ");
                    out.write("Shape { " + "appearance  Appearance {" +
                            "material  Material { " +
                            " diffuseColor  0.0 1.0 0.0 }} ");
                    out.write(" geometry Box { size 0.2 50.0 0.0 }}]}" + '\n');
                }

                out.close();
            } else
                System.err.println("Dimension is not 2 nor 3 therefore no VRML file was written.");


        } catch (IOException e) {
            System.err.println("ERROR; Couldn't write VRML file");
        }


        return true;
    }

    public static boolean printVRML3D(Setup s, Constants c, String prefix, int solNb) {

        String str = MessageFormat.format("{0}_{1}", prefix, solNb);

        return printVRML3D(s, c, str);
    }

    public static boolean printVRML3D(Setup s, Constants c, String output, String prefix, int solNb) {

        String str = MessageFormat.format("{0}_{1}", prefix, solNb);

        return printVRML3D(s, c, output, str);
    }


}
