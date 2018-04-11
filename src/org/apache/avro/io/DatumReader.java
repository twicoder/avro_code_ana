package org.apache.avro.io;

import java.io.IOException;

import org.apache.avro.Schema;

/** Read data of a schema.
 * <p>Determines the in-memory data representation.
 */
public interface DatumReader<D> {

    /** Set the schema. */
    void setSchema(Schema schema);

    /** Read a datum.  Traverse the schema, depth-first, reading all leaf values
     * in the schema into a datum that is returned.  If the provided datum is
     * non-null it may be reused and returned. */
    D read(D reuse, Decoder in) throws IOException;

}
