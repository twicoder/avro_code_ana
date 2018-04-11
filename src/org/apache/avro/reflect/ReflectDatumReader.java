package org.apache.avro.reflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.io.DatumReader;

/** {@link DatumReader} for existing classes via Java reflection. */
public class ReflectDatumReader extends GenericDatumReader<Object> {
    protected String packageName;

    public ReflectDatumReader(String packageName) {
        this.packageName = packageName;
    }

    public ReflectDatumReader(Schema root, String packageName) {
        this(packageName);
        setSchema(root);
    }

    protected Object newRecord(Object old, Schema schema) {
        Class c = getClass(schema);
        return (c.isInstance(old) ? old : newInstance(c));
    }

    protected void addField(Object record, String name, int position, Object o) {
        try {
            Field field = record.getClass().getField(name);
            field.setAccessible(true);
            field.set(record, o);
        } catch (Exception e) {
            throw new AvroRuntimeException(e);
        }
    }
    protected Object getField(Object record, String name, int position) {
        try {
            Field field = record.getClass().getField(name);
            field.setAccessible(true);
            return field.get(record);
        } catch (Exception e) {
            throw new AvroRuntimeException(e);
        }
    }
    protected void removeField(Object record, String name, int position) {
        addField(record, name, position, null);
    }

    @SuppressWarnings("unchecked")
    protected Object createEnum(String symbol, Schema schema) {
        return Enum.valueOf(getClass(schema), symbol);
    }

    protected Object createFixed(Object old, Schema schema) {
        Class c = getClass(schema);
        return c.isInstance(old) ? old : newInstance(c);
    }

    private static final Class<?>[] EMPTY_ARRAY = new Class[]{};
    private static final Map<Class,Constructor> CTOR_CACHE =
            new ConcurrentHashMap<Class,Constructor>();

    private Map<String,Class> classCache = new ConcurrentHashMap<String,Class>();

    private Class getClass(Schema schema) {
        String name = schema.getName();
        Class c = classCache.get(name);
        if (c == null) {
            try {
                c = Class.forName(packageName + name);
                classCache.put(name, c);
            } catch (ClassNotFoundException e) {
                throw new AvroRuntimeException(e);
            }
        }
        return c;
    }

    /** Create a new instance of the named class. */
    @SuppressWarnings("unchecked")
    protected static Object newInstance(Class c) {
        Object result;
        try {
            Constructor meth = (Constructor)CTOR_CACHE.get(c);
            if (meth == null) {
                meth = c.getDeclaredConstructor(EMPTY_ARRAY);
                meth.setAccessible(true);
                CTOR_CACHE.put(c, meth);
            }
            result = meth.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }

}
