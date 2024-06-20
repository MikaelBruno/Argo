package com.vem.util;
import java.time.Instant;
import java.lang.reflect.Type;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

public class Iso8601Formatter {
     public Instant formatter(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        try {
            System.out.println(json);
            long epochMillis = json.getAsJsonPrimitive().getAsLong();
            return Instant.ofEpochMilli(epochMillis);
        } catch (NumberFormatException e) {
            throw new JsonParseException("Non è possibile convertire il valore in Instant", e);
        }
    }
}
