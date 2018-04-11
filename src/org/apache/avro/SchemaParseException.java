package org.apache.avro;

public class SchemaParseException extends AvroRuntimeException {
    public SchemaParseException(Throwable cause) { super(cause); }
    public SchemaParseException(String message) { super(message); }
}
