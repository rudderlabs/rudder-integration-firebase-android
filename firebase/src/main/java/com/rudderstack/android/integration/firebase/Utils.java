package com.rudderstack.android.integration.firebase;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class Utils {

    static Map<String, String> transformUserTraits(Map<String, Object> userTraits) {
        Map<String, String> transformedUserTraits = new HashMap<>();
        for (String key : userTraits.keySet()) {
            String value = getString(userTraits.get(key));
            if (value != null) {
                transformedUserTraits.put(key, value);
            }
        }
        return transformedUserTraits;
    }

    static String getString(Object object) {
        if (object instanceof Byte || object instanceof Short || object instanceof Integer || object instanceof Long || object instanceof Float || object instanceof Double || object instanceof Boolean || object instanceof Character) {
            return object.toString();
        }
        if (object instanceof String) {
            return (String) object;
        }
        if (object instanceof ArrayList || object instanceof HashMap) {
            return object.toString();
        }
        if (object instanceof LinkedTreeMap) {
            return getStringFromLinkedTreeMap((LinkedTreeMap<String, Object>) object);
        }
        if (object.getClass().isArray()) {
            return new Gson().toJson(object);
        }
        return null;
    }

    static String getStringFromLinkedTreeMap(LinkedTreeMap<String, Object> linkedTreeMap) {
        if (linkedTreeMap.containsKey("values")) {
            ArrayList<Object> arrayList = new ArrayList<>();
            ArrayList<LinkedTreeMap> tempArrayList = (ArrayList<LinkedTreeMap>) linkedTreeMap.get("values");
            for (LinkedTreeMap ltMap : tempArrayList) {
                arrayList.add(ltMap.get("nameValuePairs"));
            }
            return arrayList.toString();
        }
        if (linkedTreeMap.containsKey("nameValuePairs")) {
            return linkedTreeMap.get("nameValuePairs").toString();
        }
        return linkedTreeMap.toString();
    }
}
