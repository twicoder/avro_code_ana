package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.reflect.ReflectDatumWriter;

/** {@link DatumWriter} for generated Java classes. */
public class SpecificDatumWriter extends ReflectDatumWriter {
    public SpecificDatumWriter() {}

    public SpecificDatumWriter(Schema root) {
        super(root);
    }

    protected Object getField(Object record, String name, int position) {
        return ((SpecificRecord)record).get(position);
    }

}
