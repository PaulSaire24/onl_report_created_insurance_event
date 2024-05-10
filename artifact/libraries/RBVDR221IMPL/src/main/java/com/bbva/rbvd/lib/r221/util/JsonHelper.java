package com.bbva.rbvd.lib.r221.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class JsonHelper {
    private static final JsonHelper INSTANCE = new JsonHelper();
    private final Gson gson;
    private static final String DATE = "yyyy-MM-dd";

    private JsonHelper() {
        gson = new GsonBuilder()
                .setDateFormat(DATE)
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .registerTypeHierarchyAdapter(Calendar.class, new CalendarAdapter())
                .create();
    }

    public static JsonHelper getInstance() { return INSTANCE; }
    public String toJsonString(Object o) { return this.gson.toJson(o); }
}

class LocalDateAdapter implements JsonSerializer<LocalDate> {

    @Override
    public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
        return new JsonPrimitive(src.toString());
    }

}

class CalendarAdapter implements JsonSerializer<Calendar> {

    @Override
    public JsonElement serialize(Calendar src, Type typeOfSrc, JsonSerializationContext context) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        return new JsonPrimitive(dateFormat.format(src.getTime()));
    }
}
