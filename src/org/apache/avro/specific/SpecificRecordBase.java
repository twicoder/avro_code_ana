package org.apache.avro.specific;

import org.apache.avro.Schema;

/** Base class for generated record classes. */
public abstract class SpecificRecordBase implements SpecificRecord {
    public abstract Schema schema();
    public abstract Object get(int field);
    public abstract void set(int field, Object value);

    public boolean equals(Object o) {
        return SpecificRecordBase.equals(this, o);
    }

    static boolean equals(SpecificRecord r1, Object o) {
        if (r1 == o) return true;
        if (!(o instanceof SpecificRecord)) return false;

        SpecificRecord r2 = (SpecificRecord)o;
        if (!r1.schema().equals(r2.schema())) return false;

        int end = r1.schema().getFields().size();
        for (int i = 0; i < end; i++) {
            Object v1 = r1.get(i);
            Object v2 = r2.get(i);
            if (v1 == null) {
                if (v2 != null) return false;
            } else {
                if (!v1.equals(v2)) return false;
            }
        }
        return true;
    }

    public int hashCode() {
        return SpecificRecordBase.hashCode(this);
    }

    static int hashCode(SpecificRecord r) {
        int result = 0;
        int end = r.schema().getFields().size();
        for (int i = 0; i < end; i++)
            result += r.get(i).hashCode();
        return result;
    }

}
