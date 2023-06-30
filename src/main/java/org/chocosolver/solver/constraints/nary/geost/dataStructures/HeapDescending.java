/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2023, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

import java.util.ArrayList;
import java.util.NoSuchElementException;

/**
 * An implementation of a priority queue according to Cormen,
 * Leiserson and Rivest.  Sorts in descending order.
 *
 * @version 1.0 2/23/96
 * @author <A HREF="http://www.radwin.org/michael/">Michael J. Radwin</A> */
public final class HeapDescending extends ArrayList<Heapable> implements HeapImpl {
    /**
	 *
	 */
	private static final long serialVersionUID = -1098658235927216892L;

	/**
     * Constructs the heap in O(N) time, using a technique similar to
     * bottom-up construction.
     */
    public HeapDescending(Heapable anArray[])
    {
	super(anArray.length);
	ensureCapacity(anArray.length);

	for (int i = 0; i < anArray.length; i++) {
		set(i, anArray[i]);
	}

	for (int i = (int)Math.floor(size() / 2) - 1; i >= 0; i--) {
		heapify(i);
	}
    }

    /**
     * Constructs a heap with no elements.
     */
    public HeapDescending()
    {
	super(0);
    }

    /**
     * Returns the Vector index of the left child
     */
    protected int left(int i)
    {
	return ((i + 1) << 1) - 1;
//	return (2 * (i + 1)) - 1;
    }

    /**
     * Returns the Vector index of the right child
     */
    protected int right(int i)
    {
	return ((i + 1) << 1);
//	return (2 * (i + 1));
    }

    /**
     * Returns the Vector index of the parent
     */
    protected int parent(int i)
    {
	return ((i + 1) >> 1) - 1;
//	return (int)Math.floor((i + 1) / 2) - 1;
    }

    /**
     * Exchanges the elements stored at the two locations
     */
    protected synchronized void exchange(int i, int j)
    {
	Heapable temp = get(j);
	set(j, get(i));
	set(i, temp);
    }

    /**
     * Also known as downheap, restores the heap condition
     * starting at node i and working its way down.
     */
    protected synchronized void heapify(int i)
    {
	int l = left(i);
	int r = right(i);
	int largest;

	if (l < size() &&
	    ((Heapable)get(l)).greaterThan(get(i))) {
		largest = l;
	} else {
		largest = i;
	}

	if (r < size() &&
	    ((Heapable)get(r)).greaterThan(get(largest))) {
		largest = r;
	}

	if (largest != i) {
	    exchange(i, largest);
	    heapify(largest);
	}
    }

    /**
     * Removes the maximum (top) element from the Heap, decreases the
     * size of the heap by one, and returns the maximum element.
     */
    public synchronized Heapable extractMax() throws NoSuchElementException
    {
	if (size() == 0) {
		throw new NoSuchElementException();
	}

	Object max = get(0);

	// move the last key to the top, decrease size, and downheap
	set(0, get(size()-1));
	remove(size() - 1);
	heapify(0);

	return (Heapable)max;
    }

    /**
     * Returns the maximum (top) element from the Heap
     */
    public synchronized Heapable getMax() throws NoSuchElementException
    {
	if (size() == 0) {
		throw new NoSuchElementException();
	}

	Object max = get(0);

	return (Heapable)max;
    }


    /**
     * Removes an element from the heap.
     */
    public Heapable remove() throws NoSuchElementException
    {
	return extractMax();
    }

    /**
     * Inserts key into the heap, and then upheaps that key to a
     * position where the heap property is satisfied.
     */
    public synchronized void insert(Heapable key)
    {
	int i = size();
	ensureCapacity(size() + 1);

	// upheap if necessary
	while (i > 0 && ((Heapable)get(parent(i))).lessThan(key)) {
	    set(i, get(parent(i)));
	    i = parent(i);
	}

	set(i, key);
    }

    @Override
    public void removeAllElements() {
        clear();
    }

    /**
     * Performs a preorder traversal of the heap, calling
     * tg.DrawInternal on every key and tg.DrawLeaf for every child
     * that exceeds the length of the heap (and is therefore a "leaf")
     */
//    public void preorder(int i, TreeGraphics tg)
//    {
//	if (i >= size()) {
//	    tg.DrawLeaf("red");
//	    return;
//	}
//
//	tg.DrawInternal(get(i).toString(), "blue");
//	preorder(left(i), tg);
//	preorder(right(i), tg);
//    }
}
