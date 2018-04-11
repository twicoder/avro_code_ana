package org.apache.avro.specific;

import java.io.File;
import java.io.IOException;

/** Ant task to generate Java interface and classes for a protocol. */
public class SchemaTask extends ProtocolTask {
    protected SpecificCompiler doCompile(File file) throws IOException {
        return SpecificCompiler.compileSchema(file);
    }
}