package org.apache.avro.specific;

import org.apache.avro.Schema;

/** Implemented by generated record classes. Permits efficient access to
 * fields.*/
public interface SpecificRecord {
    Schema schema();
    Object get(int field);
    void set(int field, Object value);
}
