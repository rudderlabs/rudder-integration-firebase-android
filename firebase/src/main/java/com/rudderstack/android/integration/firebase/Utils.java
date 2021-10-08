package com.rudderstack.android.integration.firebase;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.rudderstack.android.sdk.core.RudderLogger;

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
        if (object == null) {
            return null;
        }
        switch (getType(object)) {
            case "Byte":
            case "Short":
            case "Integer":
            case "Long":
            case "Float":
            case "Double":
            case "Boolean":
            case "Character":
            case "ArrayList":
            case "HashMap":
                return object.toString();
            case "String":
                return (String) object;
            case "LinkedTreeMap":
                return getStringFromLinkedTreeMap((LinkedTreeMap<String, Object>) object);
            case "Array":
                return new Gson().toJson(object);
            default:
                return null;
        }
    }

    static String getType(Object object) {
        if (object.getClass().isArray()) {
            return "Array";
        }
        return object.getClass().getSimpleName();
    }

    static boolean isCompatibleWithFloat(Object value) {
        if (value == null || value instanceof Number) {
            return true;
        }
        if (value instanceof String) {
            try {
                Float.parseFloat((String) value);
                return true;
            } catch (NumberFormatException ignored) {
                return false;
            }
        }
        return false;
    }

    static float getFloat(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).floatValue();
        }
        if (value instanceof String) {
            try {
                return Float.parseFloat((String) value);
            } catch (NumberFormatException ignored) {
                RudderLogger.logDebug("Unable to convert the value: " + value +
                        " to Float, using the defaultValue: " + (float) 0);
            }
        }
        return (float) 0;
    }

    static long getLong(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            try {
                return Long.parseLong((String) value);
            } catch (NumberFormatException ignored) {
                RudderLogger.logDebug("Unable to convert the value: " + value +
                        " to Long, using the defaultValue: " + (long) 0);
            }
        }
        return 0;
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

    public static boolean isEmpty(Map<String, Object> properties) {
        return (properties == null || properties.size() == 0);
    }
}
