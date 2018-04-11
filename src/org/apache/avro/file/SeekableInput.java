package org.apache.avro.file;

import java.io.*;

/** An InputStream that supports seek and tell. */
public interface SeekableInput {

    /** Set the position for the next {@link #read(byte[],int,int) read()}. */
    void seek(long p) throws IOException;

    /** Return the position of the next {@link #read(byte[],int,int) read()}. */
    long tell() throws IOException;

    /** Return the length of the file. */
    long length() throws IOException;

    /** Equivalent to {@link InputStream#read(byte[],int,int)}. */
    int read(byte b[], int off, int len) throws IOException;
}