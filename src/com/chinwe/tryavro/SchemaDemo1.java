package com.chinwe.tryavro;

import org.apache.avro.Schema;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;

public class SchemaDemo1 {
    public static void main(String[] args) {
        demo2TestParseAvscFile();
    }

    static void demo2TestParseAvscFile(){
        try{
            Schema demoSchema = Schema.parse(new File("user.avsc"));
            System.out.println(demoSchema);
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    static void demo2TestSchemaEqual(){
        Schema stringSchema1 = Schema.create(Schema.Type.STRING);
        Schema stringSchema2 = Schema.create(Schema.Type.STRING);
        System.out.println(stringSchema1.equals(stringSchema2));
    }

    static void demo1() {
        Schema stringSchema = Schema.create(Schema.Type.STRING);
        System.out.println(stringSchema.getClass());

        Method[] methods = stringSchema.getClass().getMethods();

        for(Method method : methods){
            System.out.println("method = " + method.getName());
        }
    }
}
