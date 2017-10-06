/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:47Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

/**
 * An interface for keys in the heap.  Allows the heap to make
 * comparisons to upheap or downheap.
 *
 * @see Heap
 * @version 1.0 2/23/96
 * @author <A HREF="http://www.radwin.org/michael/">Michael J. Radwin</A>
 */
public
interface Heapable {
    /**
     * Determines if this key is greater than the other key.
     * For example, to compare keys that are subclasses of
     * Integer:
     * <pre>
     *     return (intValue() > ((Integer)other).intValue());
     * </pre>
     *
     * @return true if this key is greater than the other key
     * @param other the key to compare this key to.
     */
    boolean greaterThan(Object other);

    /**
     * Determines if this key is less than the other key.
     * For example, to compare keys that are subclasses of
     * Integer:
     * <pre>
     *     return (intValue() < ((Integer)other).intValue());
     * </pre>
     *
     * @return true if this key is less than the other key
     * @param other the key to compare this key to.
     */
    boolean lessThan(Object other);

    /**
     * Determines if this key is equal to the other key.
     * For example, to compare keys that are subclasses of
     * Integer:
     * <pre>
     *     return (intValue() == ((Integer)other).intValue());
     * </pre>
     *
     * @return true if this key is equal to the other key
     * @param other the key to compare this key to.
     */
    boolean equalTo(Object other);
}
