package org.apache.avro.ipc;

import java.nio.ByteBuffer;
import java.util.Map;
import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.AvroRuntimeException;
import org.apache.avro.Protocol;
import org.apache.avro.util.Utf8;
import org.apache.avro.ipc.AvroRemoteException;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.specific.SpecificExceptionBase;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.avro.specific.SpecificRecord;
import org.apache.avro.specific.SpecificFixed;
import org.apache.avro.reflect.FixedSize;


public class HandshakeRequest extends SpecificRecordBase implements SpecificRecord {
    public static final Schema _SCHEMA = Schema.parse("{\"type\":\"record\",\"name\":\"HandshakeRequest\",\"namespace\":\"org.apache.avro.ipc\",\"fields\":[{\"name\":\"clientHash\",\"type\":{\"type\":\"fixed\",\"name\":\"MD5\",\"size\":16}},{\"name\":\"clientProtocol\",\"type\":[\"null\",\"string\"]},{\"name\":\"serverHash\",\"type\":\"MD5\"},{\"name\":\"meta\",\"type\":[\"null\",{\"type\":\"map\",\"values\":\"bytes\"}]}]}");
    public MD5 clientHash;
    public Object clientProtocol;
    public MD5 serverHash;
    public Object meta;
    public Schema schema() { return _SCHEMA; }
    public Object get(int _field) {
        switch (_field) {
            case 0: return clientHash;
            case 1: return clientProtocol;
            case 2: return serverHash;
            case 3: return meta;
            default: throw new AvroRuntimeException("Bad index");
        }
    }
    @SuppressWarnings(value="unchecked")
    public void set(int _field, Object _value) {
        switch (_field) {
            case 0: clientHash = (MD5)_value; break;
            case 1: clientProtocol = (Object)_value; break;
            case 2: serverHash = (MD5)_value; break;
            case 3: meta = (Object)_value; break;
            default: throw new AvroRuntimeException("Bad index");
        }
    }
}

@FixedSize(16)
class MD5 extends SpecificFixed {}