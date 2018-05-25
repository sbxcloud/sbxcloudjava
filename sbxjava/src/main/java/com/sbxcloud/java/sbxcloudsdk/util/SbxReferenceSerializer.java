package com.sbxcloud.java.sbxcloudsdk.util;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.sbxcloud.java.sbxcloudsdk.query.annotation.SbxKey;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;

public  class SbxReferenceSerializer<T> extends StdSerializer<T> {



    protected SbxReferenceSerializer(Class<T> t) {
        super(t);
    }

    protected SbxReferenceSerializer(JavaType type) {
        super(type);
    }

    protected SbxReferenceSerializer(Class<?> t, boolean dummy) {
        super(t, dummy);
    }

    protected SbxReferenceSerializer(StdSerializer<?> src) {
        super(src);
    }

    @Override
    public void serialize(T value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        Class<T> clazz =  (Class<T>) ((ParameterizedType)getClass()
                .getGenericSuperclass()).getActualTypeArguments()[0];
        final Field[] variables = clazz.getDeclaredFields();
        try{
            for (final Field variable : variables) {
                boolean isAccessible = variable.isAccessible();
                variable.setAccessible(true);
                final Annotation annotation = variable.getAnnotation(SbxKey.class);
                if(annotation!=null){
                    Object os = variable.get(value);
                    if (os != null) {
                        String data = (String) os;
                        gen.writeObject(variable.get(data));
                    }
                }
                variable.setAccessible(isAccessible);
            }
        } catch (IllegalArgumentException | IllegalAccessException e) {

        }

    }

}
