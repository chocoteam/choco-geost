/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

final class ListIterator {
    LinkedList owner;
    ListItem pos;

    ListIterator(LinkedList owner, ListItem pos) {
        this.owner = owner;
        this.pos = pos;
    }

    /*
     * check whether object owns the iterator
     */
    public boolean belongsTo(Object owner) {
        return this.owner == owner;
    }

    /*
     * move to head position
     */
    public void head() {
        pos = owner.head;
    }

    /*
     * move to next position
     */
    public void next() {
        pos = pos.next;
    }

    /*
     * move to previous position
     */
    public void previous() {
        pos = pos.previous;
    }
}
