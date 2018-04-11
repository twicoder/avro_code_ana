package org.apache.avro.file;

import java.io.*;

/** A {@link FileInputStream} that implements {@link SeekableInput}. */
public class SeekableFileInput
        extends FileInputStream implements SeekableInput {

    public SeekableFileInput(File file) throws IOException { super(file); }

    public void seek(long p) throws IOException { getChannel().position(p); }
    public long tell() throws IOException { return getChannel().position(); }
    public long length() throws IOException { return getChannel().size(); }

}
