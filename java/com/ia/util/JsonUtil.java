package com.ia.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class JsonUtil {

    public static String buildJsonString(final String... args) {
        try {
            if (args.length > 0 && args.length % 2 == 0) {
                final JsonObject jsonObject = new JsonObject();
                for (int i = 0; i < args.length;) {
                    jsonObject.addProperty(args[i], args[i + 1]);
                    i += 2;
                }
                return jsonObject.toString();
            }
            else {
                return null;
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> T fromJson(final Class<T> obj, final String json) {
        return new Gson().fromJson(json, obj);
    }

    public static String toJson(final Object o) {
        final GsonBuilder gb = new GsonBuilder().serializeNulls();
        final Gson gson = gb.create();
        return gson.toJson(o);
    }
}
