package org.apache.avro.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.AvroTypeException;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.Protocol.Message;
import org.apache.avro.Schema.Type;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.util.Utf8;

import com.thoughtworks.paranamer.CachingParanamer;
import com.thoughtworks.paranamer.Paranamer;

/** Utilities to use existing Java classes and interfaces via reflection. */
public class ReflectData {
    private ReflectData() {}

    /** Returns true if an object matches a schema. */
    public static boolean validate(Schema schema, Object datum) {
        switch (schema.getType()) {
            case RECORD:
                Class recordClass = datum.getClass();
                if (!(datum instanceof Object)) return false;
                for (Map.Entry<String, Schema> entry : schema.getFieldSchemas()) {
                    try {
                        if (!validate(entry.getValue(),
                                recordClass.getField(entry.getKey()).get(datum)))
                            return false;
                    } catch (NoSuchFieldException e) {
                        return false;
                    } catch (IllegalAccessException e) {
                        throw new AvroRuntimeException(e);
                    }
                }
                return true;
            case ENUM:
                return datum instanceof Enum
                        && schema.getEnumSymbols().contains(((Enum)datum).name());
            case ARRAY:
                if (!(datum instanceof GenericArray)) return false;
                for (Object element : (GenericArray)datum)
                    if (!validate(schema.getElementType(), element))
                        return false;
                return true;
            case UNION:
                for (Schema type : schema.getTypes())
                    if (validate(type, datum))
                        return true;
                return false;
            case FIXED:   return datum instanceof GenericFixed;
            case STRING:  return datum instanceof Utf8;
            case BYTES:   return datum instanceof ByteBuffer;
            case INT:     return datum instanceof Integer;
            case LONG:    return datum instanceof Long;
            case FLOAT:   return datum instanceof Float;
            case DOUBLE:  return datum instanceof Double;
            case BOOLEAN: return datum instanceof Boolean;
            case NULL:    return datum == null;
            default: return false;
        }
    }

    private static final WeakHashMap<java.lang.reflect.Type,Schema> SCHEMA_CACHE =
            new WeakHashMap<java.lang.reflect.Type,Schema>();

    /** Generate a schema for a Java type.
     * <p>For records, {@link Class#getDeclaredFields() declared fields} (not
     * inherited) which are not static or transient are used.</p>
     * <p>Note that unions cannot be automatically generated by this method,
     * since Java provides no representation for unions.</p>
     */
    public static Schema getSchema(java.lang.reflect.Type type) {
        Schema schema = SCHEMA_CACHE.get(type);
        if (schema == null) {
            schema = createSchema(type, new LinkedHashMap<String,Schema>());
            SCHEMA_CACHE.put(type, schema);
        }
        return schema;
    }

    @SuppressWarnings(value="unchecked")
    private static Schema createSchema(java.lang.reflect.Type type,
                                       Map<String,Schema> names) {
        if (type == Utf8.class)
            return Schema.create(Type.STRING);
        else if (type == ByteBuffer.class)
            return Schema.create(Type.BYTES);
        else if ((type == Integer.class) || (type == Integer.TYPE))
            return Schema.create(Type.INT);
        else if ((type == Long.class) || (type == Long.TYPE))
            return Schema.create(Type.LONG);
        else if ((type == Float.class) || (type == Float.TYPE))
            return Schema.create(Type.FLOAT);
        else if ((type == Double.class) || (type == Double.TYPE))
            return Schema.create(Type.DOUBLE);
        else if ((type == Boolean.class) || (type == Boolean.TYPE))
            return Schema.create(Type.BOOLEAN);
        else if ((type == Void.class) || (type == Void.TYPE))
            return Schema.create(Type.NULL);
        else if (type instanceof ParameterizedType) {
            ParameterizedType ptype = (ParameterizedType)type;
            Class raw = (Class)ptype.getRawType();
            System.out.println("ptype = "+ptype+" raw = "+raw);
            java.lang.reflect.Type[] params = ptype.getActualTypeArguments();
            for (int i = 0; i < params.length; i++)
                System.out.println("param ="+params[i]);
            if (GenericArray.class.isAssignableFrom(raw)) { // array
                if (params.length != 1)
                    throw new AvroTypeException("No array type specified.");
                return Schema.createArray(createSchema(params[0], names));
            } else if (Map.class.isAssignableFrom(raw)) { // map
                java.lang.reflect.Type key = params[0];
                java.lang.reflect.Type value = params[1];
                if (!(key == Utf8.class))
                    throw new AvroTypeException("Map key class not Utf8: "+key);
                return Schema.createMap(createSchema(value, names));
            }
        } else if (type instanceof Class) {
            Class c = (Class)type;
            String name = c.getSimpleName();
            String space = c.getPackage().getName();

            Schema schema = names.get(name);
            if (schema == null) {

                if (c.isEnum()) {                         // enum
                    List<String> symbols = new ArrayList<String>();
                    Enum[] constants = (Enum[])c.getEnumConstants();
                    for (int i = 0; i < constants.length; i++)
                        symbols.add(constants[i].name());
                    schema = Schema.createEnum(name, space, symbols);
                    names.put(name, schema);
                    return schema;
                }
                // fixed
                if (GenericFixed.class.isAssignableFrom(c)) {
                    int size = ((FixedSize)c.getAnnotation(FixedSize.class)).value();
                    schema = Schema.createFixed(name, space, size);
                    names.put(name, schema);
                    return schema;
                }
                // record
                LinkedHashMap<String,Schema.Field> fields =
                        new LinkedHashMap<String,Schema.Field>();
                schema = Schema.createRecord(name, space,
                        Throwable.class.isAssignableFrom(c));
                if (!names.containsKey(name))
                    names.put(name, schema);
                for (Field field : c.getDeclaredFields())
                    if ((field.getModifiers()&(Modifier.TRANSIENT|Modifier.STATIC))==0) {
                        Schema fieldSchema = createSchema(field.getGenericType(), names);
                        fields.put(field.getName(), new Schema.Field(fieldSchema, null));
                    }
                schema.setFields(fields);
            }
            return schema;
        }
        throw new AvroTypeException("Unknown type: "+type);
    }

    /** Generate a protocol for a Java interface.
     * <p>Note that this requires that <a
     * href="http://paranamer.codehaus.org/">Paranamer</a> is run over compiled
     * interface declarations, since Java 6 reflection does not provide access to
     * method parameter names.  See Avro's build.xml for an example. </p>
     */
    public static Protocol getProtocol(Class iface) {
        Protocol protocol =
                new Protocol(iface.getSimpleName(), iface.getPackage().getName());
        for (Method method : iface.getDeclaredMethods())
            if ((method.getModifiers() & Modifier.STATIC) == 0)
                protocol.getMessages().put(method.getName(),
                        getMessage(method, protocol));

        // reverse types, since they were defined in reference order
        List<Map.Entry<String,Schema>> names =
                new ArrayList<Map.Entry<String,Schema>>();
        names.addAll(protocol.getTypes().entrySet());
        Collections.reverse(names);
        protocol.getTypes().clear();
        for (Map.Entry<String,Schema> name : names)
            protocol.getTypes().put(name.getKey(), name.getValue());

        return protocol;
    }

    private static Paranamer PARANAMER = new CachingParanamer();

    private static Message getMessage(Method method, Protocol protocol) {
        Map<String,Schema> names = protocol.getTypes();
        LinkedHashMap<String,Schema.Field> fields =
                new LinkedHashMap<String,Schema.Field>();
        String[] paramNames = PARANAMER.lookupParameterNames(method);
        java.lang.reflect.Type[] paramTypes = method.getGenericParameterTypes();
        for (int i = 0; i < paramTypes.length; i++)
            fields.put(paramNames[i],
                    new Schema.Field(createSchema(paramTypes[i], names), null));
        Schema request = Schema.createRecord(fields);

        Schema response = createSchema(method.getGenericReturnType(), names);

        List<Schema> errs = new ArrayList<Schema>();
        errs.add(Protocol.SYSTEM_ERROR);              // every method can throw
        for (java.lang.reflect.Type err : method.getGenericExceptionTypes())
            if (err != AvroRemoteException.class)
                errs.add(createSchema(err, names));
        Schema errors = Schema.createUnion(errs);

        return protocol.createMessage(method.getName(), request, response, errors);
    }

}
