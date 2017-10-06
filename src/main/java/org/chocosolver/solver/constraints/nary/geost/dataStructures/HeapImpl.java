/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

import java.util.NoSuchElementException;

/**
 * An interface for a heap implementation.  Can either be descending or
 * ascending.
 *
 * @see HeapDescending
 * @see HeapAscending
 * @version 1.0 3/9/96
 * @author <A HREF="http://www.radwin.org/michael/">Michael John Radwin</A>
 */
public
interface HeapImpl {
    Heapable remove() throws NoSuchElementException;
    void insert(Heapable key);
   // void preorder(int i, TreeGraphics tg);
    boolean isEmpty();
    int size();
    void removeAllElements();
}
