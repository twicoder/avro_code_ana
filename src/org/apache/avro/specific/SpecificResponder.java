package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.ipc.Responder;
import org.apache.avro.reflect.ReflectResponder;

/** {@link Responder} for generated interfaces.*/
public class SpecificResponder extends ReflectResponder {
    public SpecificResponder(Class iface, Object impl) {
        super(iface, impl);
    }

    protected DatumWriter<Object> getDatumWriter(Schema schema) {
        return new SpecificDatumWriter(schema);
    }

    protected DatumReader<Object> getDatumReader(Schema schema) {
        return new SpecificDatumReader(schema, packageName);
    }

}
