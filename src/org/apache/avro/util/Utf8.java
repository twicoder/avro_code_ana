package org.apache.avro.util;

import java.io.*;

/** A Utf8 string. */
public class Utf8 {
    private static byte[] EMPTY = new byte[0];

    byte[] bytes = EMPTY;
    int length;

    public Utf8() {}

    public Utf8(String string) {
        try {
            this.bytes = string.getBytes("UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        this.length = bytes.length;
    }

    public byte[] getBytes() { return bytes; }
    public int getLength() { return length; }

    public Utf8 setLength(int newLength) {
        if (this.length < newLength) {
            byte[] newBytes = new byte[newLength];
            System.arraycopy(bytes, 0, newBytes, 0, this.length);
            this.bytes = newBytes;
        }
        this.length = newLength;
        return this;
    }

    public String toString() {
        try {
            return new String(bytes, 0, length, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Object o) {
        if (o == this) return true;
        if (!(o instanceof Utf8)) return false;
        Utf8 that = (Utf8)o;
        if (!(this.length == that.length)) return false;
        byte[] thatBytes = that.bytes;
        for (int i = 0; i < this.length; i++)
            if (bytes[i] != thatBytes[i])
                return false;
        return true;
    }

    public int hashCode() {
        int hash = length;
        for (int i = 0; i < this.length; i++)
            hash += bytes[i] & 0xFF;
        return hash;
    }

}
