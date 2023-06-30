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
 * Leiserson and Rivest.  Sorts in ascending order.
 *
 * @version 1.0 2/23/96
 * @author <A HREF="http://www.radwin.org/michael/">Michael J. Radwin</A> */
public final class HeapAscending extends ArrayList<Heapable>
        implements HeapImpl {
    /**
	 *
	 */
	private static final long serialVersionUID = -6129722552080498898L;


	/**
     * Constructs the heap in O(N) time, using a technique similar to
     * bottom-up construction.
     */
    public HeapAscending(Heapable anArray[])
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
    public HeapAscending()
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
	int smallest;

	if (l < size() &&
	    get(l).lessThan(get(i))) {
		smallest = l;
	} else {
		smallest = i;
	}

	if (r < size() &&
	    get(r).lessThan(get(smallest))) {
		smallest = r;
	}

	if (smallest != i) {
	    exchange(i, smallest);
	    heapify(smallest);
	}
    }

    /**
     * Removes the minimum (top) element from the Heap, decreases the
     * size of the heap by one, and returns the minimum element.
     */
    public synchronized Heapable extractMin() throws NoSuchElementException
    {
	if (size() == 0) {
		throw new NoSuchElementException();
	}

	    Object min = get(0);

	    // move the last key to the top, decrease size, and downheap
        set(0, get(size()-1));
	    remove(size() - 1);
	    heapify(0);

	    return (Heapable)min;
    }

    /**
     * Returns the minimum (top) element from the Heap.
     */
    public synchronized Heapable getMin() throws NoSuchElementException
    {
	if (size() == 0) {
		throw new NoSuchElementException();
	}

	Object min = get(0);

	return (Heapable)min;
    }

    /**
     * Removes an element from the heap.
     */
    public Heapable remove() throws NoSuchElementException
    {
	return extractMin();
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
	while (i > 0 && get(parent(i)).greaterThan(key)) {
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
