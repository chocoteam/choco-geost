/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:11:10Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

final class ListItem {
    Object obj;
    ListItem previous, next;

    public ListItem(Object obj) {
        this(null, obj, null);
    }

    public ListItem(ListItem previous, Object obj, ListItem next) {
        this.previous = previous;
        this.obj = obj;
        this.next = next;
    }
}
