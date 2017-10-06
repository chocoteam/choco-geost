/**
 * This file is part of choco-geost, https://github.com/chocoteam/choco-geost
 *
 * Copyright (c) 2017-10-06T08:40:34Z, IMT Atlantique. All rights reserved.
 *
 * Licensed under the BSD 4-clause license.
 * See LICENSE file in the project root for full license information.
 */
package org.chocosolver.solver.constraints.nary.geost.dataStructures;

import java.util.NoSuchElementException;

public class LinkedList {
  ListItem head;
 /*
  * creates an empty list
  */
  public LinkedList() {
    head = new ListItem(null);
    head.next = head.previous = head;
  }

 /*
  * remove all elements in the list
  */
  public final synchronized void clear() {
    head.next = head.previous = head;
  }

 /*
  * returns true if this container is empty.
  */
  public final boolean isEmpty() {
    return head.next == head;
  }

 /*
  * insert element after current position
  */
  public final synchronized void insertAfter(Object obj, ListIterator cursor) {
    ListItem newItem = new ListItem(cursor.pos, obj, cursor.pos.next);
    newItem.next.previous = newItem;
    cursor.pos.next = newItem;
  }

 /*
  * insert element before current position
  */
  public final synchronized void insertBefore(Object obj, ListIterator cursor) {
    ListItem newItem = new ListItem(cursor.pos.previous, obj, cursor.pos);
    newItem.previous.next = newItem;
    cursor.pos.previous = newItem;
  }

 /*
  * remove the element at current position
  */
  public final synchronized void remove(ListIterator cursor) {
    if (isEmpty()) {
      throw new IndexOutOfBoundsException("empty list.");
    }
    if (cursor.pos == head) {
      throw new NoSuchElementException("cannot remove the head");
    }
    cursor.pos.previous.next = cursor.pos.next;
    cursor.pos.next.previous = cursor.pos.previous;
  }

 /*
  * Return an iterator positioned at the head.
  */
  public final ListIterator head() {
    return new ListIterator(this, head);
  }

 /*
  * find the first occurrence of the object in a list
  */
  public final synchronized ListIterator find(Object obj) {
    if (isEmpty()) {
      throw new IndexOutOfBoundsException("empty list.");
    }
    ListItem pos = head;
    while (pos.next != head) {  // There are still elements to be inspected
      pos = pos.next;
      if (pos.obj == obj) {
        return new ListIterator(this, pos);
      }
    }
    throw new NoSuchElementException("no such object found");
  }
}
