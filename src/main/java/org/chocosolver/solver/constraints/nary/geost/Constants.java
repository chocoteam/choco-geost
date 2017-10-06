/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost;

import java.io.Serializable;

/**
 * This class contains 2 important types of data. The first is a set of variables and access methods that belong to a certain instance of this class
 * like the DIM variable representing the dimension. So to know the dimension of our problem we can just call the constants instance and ask to getDIM().
 * The other type of information is static information that are INTERNAL CONSTRAINTS CONTANTS REPRESENTING THE ICTR ID (Internal Constraint ID)
 * and EXTERNAL CONSTRAINTS CONSTANTS REPRESENTING THE ECTR ID (External Constraint ID).
 *
 * @author Charles Prud'homme
 * @since 17/01/2014
 */
public final class Constants implements Serializable {

    public Constants() {
    }

    //Just for tests
    public int nbOfUpdates = 0;


    //GLOBAL SETTING CONSTANTS
    /**
     * DIM id a constants indicating the dimension of the space we are working in globally
     */
    private int DIM = 2;
    //public static final String INPUT_FILE_PATH = "./geost/input.txt";
    //public static final String INPUT_FILE_PATH = "./geost/PerfectSquareProb1.txt";
    private String INPUT_FILE_PATH = "./geost/randomInputGenProb.txt";
    //public static final String OUTPUT_FILE_PATH = "./geost/output.txt";
    private String VRML_OUTPUT_FOLDER = "./geost/VRMLfiles/";
    private String OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT = "./geost/randomInputGenProb.txt";
    private String OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS = "./geost/randomHumanGenProb.txt";

    //0: all solutions
    //1: first solution
    /**
     * RUN_MODE is a constant that indicates Whether we want to search for all solutions or a first solution.
     * value 0 is for all solutions, value 1 is for one solution.
     */
    private int RUN_MODE = 0;

    //INTERNAL CONSTRAINTS CONTANTS REPRESENTING THE ICTR ID
    /**
     * INBOX is a constant that specifies the id of the inbox constraint. So when declaring an internal constraint that is an inbox constraint
     * we provide the value Constants.INBOX as ictrID.
     */
    public static final int INBOX = 1;
    /**
     * OUTBOX is a constant that specifies the id of the outbox constraint. So when declaring an internal constraint that is an outbox constraint
     * we provide the value Constants.OUTBOX as ictrID.
     */
    public static final int OUTBOX = 2;
    /**
     * AVOID_HOLES is a constant that specifies the id of the avoid_holes constraint. So when declaring an internal constraint that is an avoid_holes constraint
     * we provide the value Constants.AVOID_HOLES as ictrID.
     */
    public static final int AVOID_HOLES = 3;
    /**
     * FORBID_REGION is a constant that specifies the id of a forbidden region internal constraint.
     * So when declaring an internal constraint corresponding to a constraint using a forbidden region,
     * we provide the value Constants.FORBID_REGION as ictrID.
     */
    public static final int FORBID_REGION = 4;
    /**
     * DIST_LEQ_FR is a constant that specifies the id of a dist_leq internal constraint.
     * So when declaring an internal constraint corresponding to a constraint using a dist_leq forbidden region,
     * we provide the value Constants.DIST_LEQ_FR as ictrID.
     */
    public static final int DIST_LEQ_FR = 5;
    /**
     * DIST_GEQ_FR is a constant that specifies the id of a dist_leq internal constraint.
     * So when declaring an internal constraint corresponding to a constraint using a dist_leq forbidden region,
     * we provide the value Constants.DIST_GEQ_FR as ictrID.
     */
    public static final int DIST_GEQ_FR = 6;
    /**
     * DIST_LINEAR_FR is a constant that specifies the id of a linear internal constraint.
     * So when declaring an internal constraint corresponding to a constraint using a lienar forbidden region,
     * we provide the value Constants.LINEAR_FR as ictrID.
     */
    public static final int DIST_LINEAR_FR = 7;


    //EXTERNAL CONSTRAINTS CONSTANTS REPRESENTING THE ECTR ID
    /**
     * COMPATIBLE is a constant that specifies the id of the compatible constraint. So when declaring an external constraint that is a compatible constraint
     * we provide the value Constants.COMPATIBLE as ectrID.
     */
    public static final int COMPATIBLE = 1;
    /**
     * INCLUDED is a constant that specifies the id of the included constraint. So when declaring an external constraint that is an included constraint
     * we provide the value Constants.INCLUDED as ectrID.
     */
    public static final int INCLUDED = 2;
    /**
     * NON_OVERLAPPING is a constant that specifies the id of the non_overlapping constraint. So when declaring an external constraint that is a non_overlapping constraint
     * we provide the value Constants.NON_OVERLAPPING as ectrID.
     */
    public static final int NON_OVERLAPPING = 3;
    /**
     * VISIBLE is a constant that specifies the id of the visible constraint. So when declaring an external constraint that is a visible constraint
     * we provide the value Constants.VISIBLE as ectrID.
     */
    public static final int VISIBLE = 4;
    /**
     * DIST_LEQ is a constant that specifies the id of the distance constraint. So when declaring
     * an external constraint that is a <= distance constraint
     * we provide the value Constants.DIST_LEQ as ectrID.
     */
    public static final int DIST_LEQ = 5;
    /**
     * DIST_GEQ is a constant that specifies the id of the distance constraint. So when declaring
     * an external constraint that is a <= distance constraint
     * we provide the value Constants.DIST_LEQ as ectrID.
     */
    public static final int DIST_GEQ = 6;
    /**
     * DIST_LINEAR is a constant that specifies the id of the linear constraint. So when declaring
     * an external constraint that is a linear distance constraint
     * we provide the value Constants.DIST_LINEAR as ectrID.
     */
    public static final int DIST_LINEAR = 7;
    /**
     * NON_OVERLAPPING_CIRCLE is a constant that specifies the id of the linear constraint. So when declaring
     * an external constraint that is a linear distance constraint
     * we provide the value Constants.DIST_LINEAR as ectrID.
     */
    public static final int NON_OVERLAPPING_CIRCLE = 8;


    public void setDIM(int d) {
        DIM = d;
    }

    public int getDIM() {
        return DIM;
    }

    public String getINPUT_FILE_PATH() {
        return INPUT_FILE_PATH;
    }

    public int getRUN_MODE() {
        return RUN_MODE;
    }

    public void setINPUT_FILE_PATH(String input_file_path) {
        INPUT_FILE_PATH = input_file_path;
    }

    public void setRUN_MODE(int run_mode) {
        RUN_MODE = run_mode;
    }

    public String getVRML_OUTPUT_FOLDER() {
        return VRML_OUTPUT_FOLDER;
    }

    public void setVRML_OUTPUT_FOLDER(String vrml_output_folder) {
        VRML_OUTPUT_FOLDER = vrml_output_folder;
    }

    public String getOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS() {
        return OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS;
    }

    public String getOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT() {
        return OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT;
    }

    public void setOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS(
            String output_of_random_gen_prob_to_be_read_by_humans) {
        OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_READ_BY_HUMANS = output_of_random_gen_prob_to_be_read_by_humans;
    }

    public void setOUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT(
            String output_of_random_gen_prob_to_be_used_as_input) {
        OUTPUT_OF_RANDOM_GEN_PROB_TO_BE_USED_AS_INPUT = output_of_random_gen_prob_to_be_used_as_input;
    }

}
