package org.apache.avro.reflect;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.Map;

import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Schema;
import org.apache.avro.Protocol.Message;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.Decoder;
import org.apache.avro.io.Encoder;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.ipc.Responder;
import org.apache.avro.util.Utf8;

/** {@link Responder} for existing interfaces via Java reflection.*/
public class ReflectResponder extends Responder {
    private Object impl;
    protected String packageName;

    public ReflectResponder(Class iface, Object impl) {
        super(ReflectData.getProtocol(iface));
        this.impl = impl;
        this.packageName = getLocal().getNamespace()+"."+getLocal().getName()+"$";
    }

    protected DatumWriter<Object> getDatumWriter(Schema schema) {
        return new ReflectDatumWriter(schema);
    }

    protected DatumReader<Object> getDatumReader(Schema schema) {
        return new ReflectDatumReader(schema, packageName);
    }

    /** Reads a request message. */
    public Object readRequest(Schema schema, Decoder in) throws IOException {
        Object[] args = new Object[schema.getFields().size()];
        int i = 0;
        for (Map.Entry<String, Schema> param : schema.getFieldSchemas())
            args[i++] = getDatumReader(param.getValue()).read(null, in);
        return args;
    }

    /** Writes a response message. */
    public void writeResponse(Schema schema, Object response, Encoder out)
            throws IOException {
        getDatumWriter(schema).write(response, out);
    }

    /** Writes an error message. */
    public void writeError(Schema schema, AvroRemoteException error,
                           Encoder out) throws IOException {
        getDatumWriter(schema).write(error, out);
    }

    public Object respond(Message message, Object request)
            throws AvroRemoteException {
        Class[] paramTypes = new Class[message.getRequest().getFields().size()];
        int i = 0;
        try {
            for (Map.Entry<String,Schema> param: message.getRequest().getFieldSchemas())
                paramTypes[i++] = paramType(param.getValue());
            Method method = impl.getClass().getMethod(message.getName(), paramTypes);
            return method.invoke(impl, (Object[])request);
        } catch (InvocationTargetException e) {
            Throwable target = e.getTargetException();
            if (target instanceof AvroRemoteException)
                throw (AvroRemoteException)target;
            else throw new AvroRuntimeException(e);
        } catch (ClassNotFoundException e) {
            throw new AvroRuntimeException(e);
        } catch (NoSuchMethodException e) {
            throw new AvroRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new AvroRuntimeException(e);
        }
    }

    private Class paramType(Schema schema) throws ClassNotFoundException {
        switch (schema.getType()) {
            case FIXED:
            case RECORD:
            case ENUM:    return Class.forName(packageName+schema.getName());
            case ARRAY:   return GenericArray.class;
            case MAP:     return Map.class;
            case UNION:   return Object.class;
            case STRING:  return Utf8.class;
            case BYTES:   return ByteBuffer.class;
            case INT:     return Integer.TYPE;
            case LONG:    return Long.TYPE;
            case FLOAT:   return Float.TYPE;
            case DOUBLE:  return Double.TYPE;
            case BOOLEAN: return Boolean.TYPE;
            case NULL:    return Void.TYPE;
            default: throw new AvroRuntimeException("Unknown type: "+schema);
        }

    }


}
