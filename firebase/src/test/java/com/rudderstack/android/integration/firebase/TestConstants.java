package com.rudderstack.android.integration.firebase;

import com.rudderstack.android.sdk.core.RudderMessage;
import com.rudderstack.android.sdk.core.RudderMessageBuilder;
import com.rudderstack.android.sdk.core.RudderProperty;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestConstants {
    public static JSONArray getProductSingleJSONArray() {
        JSONArray products = new JSONArray();
        try {
            JSONObject product1 = new JSONObject();
            product1.put("product_id", "RSPro1");
            product1.put("name", "RSMonopoly1");
            product1.put("price", 1000.2);
            product1.put("quantity", "100");
            product1.put("category", "RSCat1");

            products.put(product1);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public static JSONArray getProductsJSONArray() {
        JSONArray products = new JSONArray();
        try {
            JSONObject product1 = new JSONObject();
            product1.put("product_id", "RSPro1");
            product1.put("name", "RSMonopoly1");
            product1.put("price", 1000.2);
            product1.put("quantity", "100");
            product1.put("category", "RSCat1");

            JSONObject product2 = new JSONObject();
            product2.put("product_id", "Pro2");
            product2.put("name", "Games2");
            product2.put("price", "2000.20");
            product2.put("quantity", 200);
            product2.put("category", "RSCat2");

            products.put(product1);
            products.put(product2);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
        return products;
    }

    public static List<Map<String, Object>> getProductsList() {
        Map<String, Object> product1 = new HashMap<>();
        product1.put("product_id", "RSPro1");
        product1.put("name", "RSMonopoly1");
        product1.put("price", 1000.2);
        product1.put("quantity", "100");
        product1.put("category", "RSCat1");

        Map<String, Object> product2 = new HashMap<>();
        product2.put("product_id", "Pro2");
        product2.put("name", "Games2");
        product2.put("price", "2000.20");
        product2.put("quantity", 200);
        product2.put("category", "RSCat2");

        // Creating a List of Maps
        List<Map<String, Object>> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        return products;
    }

    public static Map<String, Object> getProductMap() {
        Map<String, Object> product1 = new HashMap<>();
        product1.put("product_id", "RSPro1");
        product1.put("name", "RSMonopoly1");
        product1.put("price", 1000.2);
        product1.put("quantity", "100");
        product1.put("category", "RSCat1");

        return product1;
    }

    public static List<LinkedHashMap<String, Object>> getProductsLinkedHashMap() {
        LinkedHashMap<String, Object> product1 = new LinkedHashMap<>();
        product1.put("product_id", "RSPro1");
        product1.put("name", "RSMonopoly1");
        product1.put("price", 1000.2);
        product1.put("quantity", "100");
        product1.put("category", "RSCat1");

        LinkedHashMap<String, Object> product2 = new LinkedHashMap<>();
        product2.put("product_id", "Pro2");
        product2.put("name", "Games2");
        product2.put("price", "2000.20");
        product2.put("quantity", 200);
        product2.put("category", "RSCat2");

        // Creating a List of Maps
        List<LinkedHashMap<String, Object>> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        return products;
    }

    public static LinkedHashMap<String, Object> getProductSingleLinkedHashMap() {
        LinkedHashMap<String, Object> product1 = new LinkedHashMap<>();
        product1.put("product_id", "RSPro1");
        product1.put("name", "RSMonopoly1");
        product1.put("price", 1000.2);
        product1.put("quantity", "100");
        product1.put("category", "RSCat1");

        return product1;
    }

    public static ArrayList<Map<String, Object>> getProductsArrayList() {
        Map<String, Object> product1 = new HashMap<>();
        product1.put("product_id", "RSPro1");
        product1.put("name", "RSMonopoly1");
        product1.put("price", 1000.2);
        product1.put("quantity", "100");
        product1.put("category", "RSCat1");

        Map<String, Object> product2 = new HashMap<>();
        product2.put("product_id", "Pro2");
        product2.put("name", "Games2");
        product2.put("price", "2000.20");
        product2.put("quantity", 200);
        product2.put("category", "RSCat2");

        ArrayList<Map<String, Object>> products = new ArrayList<>();
        products.add(product1);
        products.add(product2);

        return products;
    }
}
