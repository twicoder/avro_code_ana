package org.apache.avro.specific;

import org.apache.avro.generic.GenericData;
import org.apache.avro.reflect.FixedSize;

/** Base class for generated fixed-sized data classes. */
public abstract class SpecificFixed extends GenericData.Fixed {
    public SpecificFixed() {
        bytes(new byte[getClass().getAnnotation(FixedSize.class).value()]);
    }
}
