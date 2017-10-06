/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:34Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost;

import org.chocosolver.solver.constraints.nary.geost.util.Pair;

import java.io.FileOutputStream;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.List;

/**
 * Created by IntelliJ IDEA.
 * User: szampelli
 * Date: 10 ao�t 2009
 * Time: 13:35:09
 * To change this template use File | Settings | File Templates.
 */
public class GeostOptions {

    public static long timeFixObj=0L;
    public static long timeFixAllObj=0L;
    public static long timePruneFix=0L;
    public static long timeGetFR=0L;
    public static long timeIsFeasible=0L;

    public static long GetFRCalled=0L;
    public static long PruneFixCalled=0L;
    public static long timefilterWithGreedyMode=0L;
    public static long timefilterWithoutGreedyMode=0L;
    public static long handleSolution1 = 0L;
    public static long handleSolution2 = 0L;
    public static long handleSolution3 = 0L;
    public static boolean[][] memo_objects;
    public static boolean clipping=false;
    public static int interval_size=-1;//size of the interval in IntervalBranching
//    public static long sum_jumps=0;
    public static long GetFRCalls=0;
    public static long deltaOne=0;
    public static long deltaZero=0;
    public static HashMap<Integer, HashMap<Integer,Integer>> delta= new HashMap<>();
    public static HashMap<Integer,HashMap<Integer, List<Integer>>> succDelta= new HashMap<>();

    public static ObjectOutput serial=null;
    public static FileOutputStream fileSerial=null;
    public static double[][] prop={{0.66,0.66,0.66}};
    //public static double[][] prop={{0.25,0.75,1.0},{0.75,0.25,1.0},{0.33,0.66,1.0},{0.66,0.33,1.0},{0.33,0.33},{0.66,0.66},{0.25,0.25},{0.75,0.75},{0.2,0.8},{0.8,0.2},{1.0,0.5},{0.5,1.0},{1.0,0.2},{0.2,1.0},{1.0,0.8},{0.8,1.0}};
    //public static double[][] prop={{1.0,0.8}};

    //public static double[] prop={0.1,0.2,0.33,0.4,0.5,0.66,0.75,0.8,0.9};
    //public static double[] prop={0.1,0.2,0.33,0.4,0.5,0.66,0.75,0.8,0.9};
    //public static double[] prop={0.001,0.002,0.003,0.004,0.005,0.006,0.007,0.008,0.009};
    //public static double[] prop={0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09};

    public static boolean boxModeOnly=true;
    public static boolean propModeOnly=false;
    public static boolean deltaModeOnly=false;
    public static boolean circleRandom=false;
    public static boolean vizuRandom=false;
    public static boolean firstTimeGetDeltaFR=false;
    public static boolean debug=false;
    public static boolean findboxinterout =false;
    public static boolean findboxinteroutonly =false;
    public static boolean findboxtriangle=false;
    public static boolean singleboxonly=false;
    public static boolean mixmode=false;
    public static boolean intersection=false;
    public static boolean deltasucc=false;
    public static boolean usevectorbox=false;
    public static boolean useinterbox=false;
    public static boolean processing=false;
    public static boolean unaryCirclePackingHeuristic=false;
    public static boolean viewsol=false;


    public static int phase=1;
    public static int nbr_jumps=0;
    public static int max_nbr_jumps=0; //maximum nbr of jumps for all propagations steps
    public static int sum_jumps=0; //maximum nbr of jumps for all propagations steps
    public static long sum_square_jumps=0; //maximum nbr of jumps for all propagations steps
    public static int nbr_propagations=0;

    public static boolean worst_increase=true; //associated with 'worst_point'; indicates wether increase was true or false when worst point was detected
    public static int state_FR=0;
    public static int nbr_steps=0;

    public static boolean memoisation=false;
    public static boolean increment=false;
    public HashMap<Pair<Integer,Integer>, Boolean> included=null;

    public boolean propag_failed=false;//Used in CirclePackingHeuristics and GeometricKernel only
    public boolean try_propagation=false;
    public int tried_propagation=0;
    public boolean useNumericEngine=false;

    /*
      public static long timeFixObj=0L;
      public static long timeFixAllObj=0L;
      public static long timePruneFix=0L;
      public static long timeGetFR=0L;
      public static long timeIsFeasible=0L;

      public static long GetFRCalled=0L;
      public static long PruneFixCalled=0L;
      public static long timefilterWithGreedyMode=0L;
      public static long timefilterWithoutGreedyMode=0L;
      public static long handleSolution1 = 0L;
      public static long handleSolution2 = 0L;
      public static long handleSolution3 = 0L;
      public static boolean[][] memo_objects;
      public static boolean clipping=false;
      public static int interval_size=-1;//size of the interval in IntervalBranching
  //    public static long sum_jumps=0;
      public static long GetFRCalls=0;
      public static long deltaOne=0;
      public static long deltaZero=0;
      public static HashMap<Integer,HashMap<Integer,Integer>> delta= new HashMap<Integer, HashMap<Integer,Integer>>();
      public static HashMap<Integer,HashMap<Integer,Vector<Integer>>> succDelta= new HashMap<Integer, HashMap<Integer,Vector<Integer>>>();

      public static ObjectOutput serial=null;
      public static FileOutputStream fileSerial=null;
      public static double[][] prop={{0.66,0.66,0.66}};
      //public static double[][] prop={{0.25,0.75,1.0},{0.75,0.25,1.0},{0.33,0.66,1.0},{0.66,0.33,1.0},{0.33,0.33},{0.66,0.66},{0.25,0.25},{0.75,0.75},{0.2,0.8},{0.8,0.2},{1.0,0.5},{0.5,1.0},{1.0,0.2},{0.2,1.0},{1.0,0.8},{0.8,1.0}};
      //public static double[][] prop={{1.0,0.8}};

      //public static double[] prop={0.1,0.2,0.33,0.4,0.5,0.66,0.75,0.8,0.9};
      //public static double[] prop={0.1,0.2,0.33,0.4,0.5,0.66,0.75,0.8,0.9};
      //public static double[] prop={0.001,0.002,0.003,0.004,0.005,0.006,0.007,0.008,0.009};
      //public static double[] prop={0.01,0.02,0.03,0.04,0.05,0.06,0.07,0.08,0.09};

      public static boolean boxModeOnly=false;
      public static boolean propModeOnly=false;
      public static boolean deltaModeOnly=true;
      public static boolean circleRandom=false;
      public static boolean vizuRandom=false;
      public static boolean firstTimeGetDeltaFR=true;
      public static boolean debug=true;
      public static boolean findboxinterout =false;
      public static boolean findboxinteroutonly =false;
      public static boolean findboxtriangle=false;
      public static boolean singleboxonly=false;
      public static boolean mixmode=false;
      public static boolean intersection=true;
      public static boolean deltasucc=false;
      public static boolean usevectorbox=true;
      public static boolean useinterbox=true;
      public static boolean processing=false;
      public static boolean unaryCirclePackingHeuristic=false;
      public static boolean viewsol=false;


      public static int phase=1;
      public static int nbr_jumps=0;
      public static int max_nbr_jumps=0; //maximum nbr of jumps for all propagations steps
      public static int sum_jumps=0; //maximum nbr of jumps for all propagations steps
      public static long sum_square_jumps=0; //maximum nbr of jumps for all propagations steps
      public static int nbr_propagations=0;

      public static Point worst_point=null; //sweep point where the maximum nbr of jumps has been observed
      public static boolean worst_increase=true; //associated with 'worst_point'; indicates wether increase was true or false when worst point was detected

      public static int state_FR=0;
      public static int nbr_steps=0;
  */
    
}
