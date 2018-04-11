package org.apache.avro.reflect;

import org.apache.avro.generic.GenericFixed;
import java.lang.annotation.*;

/** Declares the size of implementations of {@link GenericFixed}. */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
@Documented
public @interface FixedSize {
    /** The declared size of instances of classes with this annotation. */
    int value();
}