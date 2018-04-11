package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.ipc.AvroRemoteException;

/** Base class for specific exceptions. */
public abstract class SpecificExceptionBase extends AvroRemoteException
        implements SpecificRecord {

    public abstract Schema schema();
    public abstract Object get(int field);
    public abstract void set(int field, Object value);

    public boolean equals(Object o) {
        return SpecificRecordBase.equals(this, o);
    }

    public int hashCode() {
        return SpecificRecordBase.hashCode(this);
    }

}
