package com.rudderstack.android.integration.firebase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class TestUtils {
    public static String jsonArrayToString(JSONArray jsonArray) {
        List<String> list = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                Object object = jsonArray.get(i);
                list.add(jsonObjectToString((JSONObject) object));
            }
            Collections.sort(list);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return "[" + String.join(",", list) + "]";
    }

    private static String jsonObjectToString(JSONObject jsonObject) {
        List<String> list = new ArrayList<>();
        try {
            for (Iterator<String> it = jsonObject.keys(); it.hasNext(); ) {
                String key = it.next();
                Object value = jsonObject.get(key);
                list.add("\"" + key + "\":" + "\"" + value + "\"");
            }
            Collections.sort(list);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return "{" + String.join(",", list) + "}";
    }
}
