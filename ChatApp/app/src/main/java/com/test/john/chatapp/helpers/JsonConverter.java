package com.test.john.chatapp.helpers;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

public class JsonConverter {

    public static <T> String convertToJson(T t) {
        StringWriter writer = new StringWriter();
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.writeValue(writer, t);
            return writer.toString();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("all")
    public static <T> T convertFromJson(String jsonObj, Class clazz) {
        StringReader reader = new StringReader(jsonObj);
        ObjectMapper mapper = new ObjectMapper();
        T t = null;
        try {
            return (T) mapper.readValue(reader, clazz);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
