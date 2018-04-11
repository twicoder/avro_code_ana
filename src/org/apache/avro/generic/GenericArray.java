package org.apache.avro.generic;

/** An array of objects. */
public interface GenericArray<T> extends Iterable<T> {
    /** The number of elements contained in this array. */
    long size();

    /** Reset the size of the array to zero. */
    void clear();

    /** Add an element to this array. */
    void add(T element);

    /** The current content of the location where {@link #add(Object)} would next
     * store an element, if any.  This permits reuse of arrays and their elements
     * without allocating new objects. */
    T peek();
}