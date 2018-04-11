package org.apache.avro.generic;

import java.io.IOException;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.ipc.Responder;

/** {@link Responder} implementation for generic Java data. */
public abstract class GenericResponder extends Responder {

    public GenericResponder(Protocol local) {
        super(local);
    }

    /** Reads a request message. */
    public Object readRequest(Schema schema, Decoder in) throws IOException {
        return new GenericDatumReader<Object>(schema).read(null, in);
    }

    /** Writes a response message. */
    public void writeResponse(Schema schema, Object response, Encoder out)
            throws IOException {
        new GenericDatumWriter<Object>(schema).write(response, out);
    }

    /** Writes an error message. */
    public void writeError(Schema schema, AvroRemoteException error,
                           Encoder out) throws IOException {
        new GenericDatumWriter<Object>(schema).write(error.getValue(), out);
    }

}
