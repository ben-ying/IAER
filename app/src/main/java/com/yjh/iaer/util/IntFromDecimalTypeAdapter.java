package com.yjh.iaer.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;

public class IntFromDecimalTypeAdapter extends TypeAdapter<Integer> {

    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value(value);
        }
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return 0;
        }
        if (in.peek() == JsonToken.STRING) {
            String value = in.nextString();
            try {
                return (int) Math.round(Double.parseDouble(value));
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return (int) Math.round(in.nextDouble());
    }
}
