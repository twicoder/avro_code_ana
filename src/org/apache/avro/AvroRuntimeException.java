package org.apache.avro;

public class AvroRuntimeException extends RuntimeException {
    public AvroRuntimeException(Throwable cause) { super(cause); }
    public AvroRuntimeException(String message) { super(message); }
}
