package org.apache.avro.generic;

import java.util.*;

import org.apache.avro.Schema;

/** A set of fields, each a name/value pair.*/
public interface GenericRecord extends Map<String,Object> {
    /** The schema of this instance. */
    Schema getSchema();
}
