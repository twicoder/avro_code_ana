package org.apache.avro.generic;

import java.io.IOException;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.ipc.Requestor;
import org.apache.avro.ipc.Transceiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/** {@link Requestor} implementation for generic Java data. */
public class GenericRequestor extends Requestor {
    private static final Logger LOG
            = LoggerFactory.getLogger(GenericRequestor.class);

    public GenericRequestor(Protocol protocol, Transceiver transceiver)
            throws IOException {
        super(protocol, transceiver);
    }

    public void writeRequest(Schema schema, Object request, Encoder out)
            throws IOException {
        new GenericDatumWriter<Object>(schema).write(request, out);
    }

    public Object readResponse(Schema schema, Decoder in) throws IOException {
        return new GenericDatumReader<Object>(schema).read(null, in);
    }

    public AvroRemoteException readError(Schema schema, Decoder in)
            throws IOException {
        return new AvroRemoteException(new GenericDatumReader<Object>(schema).read(null,in));
    }

}
