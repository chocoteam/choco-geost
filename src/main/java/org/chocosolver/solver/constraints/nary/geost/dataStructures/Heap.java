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
 * A priority queue according to Cormen, Leiserson and Rivest.
 * <p>
 * The heap can be constructed in O(N) time by copying an array, or
 * O(N log N) time by staring with an empty heap and performing N
 * insertions.
 * <p>
 * @version 1.0 2/23/96
 * @author <A HREF="http://www.radwin.org/michael/">Michael J. Radwin</A>
 */

// * The heap is visualized with a call to TreeGraphics.
// * @see TreeGraphics
public  final class Heap {
    HeapImpl theHeap;

    /**
     * Constructs the heap by copying an unordered array.  Sorts keys
     * in descending order if descending is true, ascending order
     * otherwise.  Takes O(N) time.
     */
    public Heap(Heapable anArray[], boolean descending)
    {
	super();
	if (descending) {
		theHeap = new HeapDescending(anArray);
	} else {
		theHeap = new HeapAscending(anArray);
	}
    }

    /**
     * Constructs the heap by copying an unordered array.  Sorts keys
     * in descending order. Takes O(N) time.
     */
    public Heap(Heapable anArray[])
    {
	this(anArray, true);
    }


    /**
     * Constructs a heap with the given sorting order. Takes O(N) time.
     *
     * @param descending true if keys should be sorted in descending order.
     */
    public Heap(boolean descending)
    {
	super();
	if (descending) {
		theHeap = new HeapDescending();
	} else {
		theHeap = new HeapAscending();
	}
    }

    /**
     * Constructs a heap with keys sorted in descending order.
     * Takes O(N) time.
     */
    public Heap()
    {
	this(true);
    }

    /**
     * Returns true if there are no keys in the heap, false otherwise.
     * Takes O(1) time.
     */
    public boolean isEmpty()
    {
 	return theHeap.isEmpty();
    }

    /**
     * Returns the number of keys in the heap.
     * Takes O(1) time.
     */
    public int size()
    {
	return theHeap.size();
    }

    /**
     * Removes all keys from the heap.
     * Takes O(N) time.
     */
    public synchronized void clear()
    {
	theHeap.removeAllElements();
    }

    /**
     * Removes the top key from the heap.
     * Takes O(N log N) time.
     */
    public synchronized Heapable remove() throws NoSuchElementException
    {
	return theHeap.remove();
    }

    /**
     * Inserts a key into the heap.
     * Takes O(N log N) time.
     */
    public synchronized void insert(Heapable key)
    {
	theHeap.insert(key);
    }

    /**
     * Visualizes every key in the heap using calls to TreeGraphics.
     * Takes O(N) time.
     */
//    public void visualize(TreeGraphics tg)
//    {
//	theHeap.preorder(0, tg);
//    }
}
