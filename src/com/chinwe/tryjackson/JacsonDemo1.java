package com.chinwe.tryjackson;

import org.apache.avro.Schema;
import org.apache.avro.SchemaParseException;
import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.lang.reflect.Method;

public class JacsonDemo1 {
    static final JsonFactory FACTORY = new JsonFactory();
    static final ObjectMapper MAPPER = new ObjectMapper(FACTORY);


    public static void main(String[] args) {



    }



    static void demo1(){
        printObjMethods(FACTORY);
        printObjMethods(MAPPER);
    }

    public static void printObjMethods(Object obj){
        Method[] methods = obj.getClass().getMethods();

        System.out.println("################################################");
        for(Method method : methods){
            System.out.println("method = " + method.getName());
        }
        System.out.println("################################################");
    }
}
