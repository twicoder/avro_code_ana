package org.apache.avro.specific;

import org.apache.avro.Schema;
import org.apache.avro.io.DatumReader;
import org.apache.avro.reflect.ReflectDatumReader;

/** {@link DatumReader} for generated Java classes. */
public class SpecificDatumReader extends ReflectDatumReader {
    public SpecificDatumReader(String packageName) {
        super(packageName);
    }

    public SpecificDatumReader(Schema root, String packageName) {
        super(root, packageName);
    }

    public SpecificDatumReader(Schema root) {
        super(root, root.getNamespace()+".");
    }

    protected void addField(Object record, String name, int position, Object o) {
        ((SpecificRecord)record).set(position, o);
    }
    protected Object getField(Object record, String name, int position) {
        return ((SpecificRecord)record).get(position);
    }
    protected void removeField(Object record, String field, int position) {
        ((SpecificRecord)record).set(position, null);
    }

}
