package org.apache.avro;

/** Thrown when an illegal type is used. */
public class AvroTypeException extends AvroRuntimeException {
    public AvroTypeException(String message) { super(message); }
}
