package org.apache.avro.reflect;

import java.io.IOException;
import java.lang.reflect.Field;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Encoder;

/** {@link DatumWriter} for existing classes via Java reflection. */
public class ReflectDatumWriter extends GenericDatumWriter<Object> {
    public ReflectDatumWriter() {}

    public ReflectDatumWriter(Schema root) {
        super(root);
    }

    protected Object getField(Object record, String name, int position) {
        try {
            Field field = record.getClass().getField(name);
            return field.get(record);
        } catch (Exception e) {
            throw new AvroRuntimeException(e);
        }
    }

    protected void writeEnum(Schema schema, Object datum, Encoder out)
            throws IOException {
        out.writeInt(((Enum)datum).ordinal());
    }

    protected boolean isEnum(Object datum) {
        return datum instanceof Enum;
    }

    @Override
    protected boolean isRecord(Object datum) {
        return ReflectData.getSchema(datum.getClass()).getType() == Type.RECORD;
    }

    protected boolean instanceOf(Schema schema, Object datum) {
        return (schema.getType() == Type.RECORD)
                ? ReflectData.getSchema(datum.getClass()).getType() == Type.RECORD
                : super.instanceOf(schema, datum);
    }

}
