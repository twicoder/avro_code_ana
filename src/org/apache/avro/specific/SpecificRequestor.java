package org.apache.avro.specific;

import java.io.IOException;
import java.lang.reflect.Proxy;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.ipc.Requestor;
import org.apache.avro.ipc.Transceiver;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectRequestor;

/** {@link Requestor} for generated interfaces. */
public class SpecificRequestor extends ReflectRequestor {
    private SpecificRequestor(Protocol protocol, Transceiver transceiver)
            throws IOException {
        super(protocol, transceiver);
    }

    protected DatumWriter<Object> getDatumWriter(Schema schema) {
        return new SpecificDatumWriter(schema);
    }

    protected DatumReader<Object> getDatumReader(Schema schema) {
        return new SpecificDatumReader(schema, packageName);
    }

    /** Create a proxy instance whose methods invoke RPCs. */
    public static Object getClient(Class<?> iface, Transceiver transciever)
            throws IOException {
        Protocol protocol = ReflectData.getProtocol(iface);
        return Proxy.newProxyInstance(iface.getClassLoader(),
                new Class[] { iface },
                new SpecificRequestor(protocol, transciever));
    }
}
