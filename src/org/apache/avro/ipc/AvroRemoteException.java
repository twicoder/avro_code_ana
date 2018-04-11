package org.apache.avro.ipc;

import java.io.IOException;

/** Base class for exceptions thrown to client by server. */
public class AvroRemoteException extends IOException {
    private Object value;

    protected AvroRemoteException() {}

    public AvroRemoteException(Throwable value) {
        this(value.toString());
        initCause(value);
    }

    public AvroRemoteException(Object value) {
        super(value != null ? value.toString() : null);
        this.value = value;
    }

    public Object getValue() { return value; }
}
