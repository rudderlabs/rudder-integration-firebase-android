package com.rudderstack.android.integration.firebase;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.gson.Gson;
import com.rudderstack.android.sdk.core.MessageType;
import com.rudderstack.android.sdk.core.RudderClient;
import com.rudderstack.android.sdk.core.RudderConfig;
import com.rudderstack.android.sdk.core.RudderIntegration;
import com.rudderstack.android.sdk.core.RudderLogger;
import com.rudderstack.android.sdk.core.RudderMessage;
import com.rudderstack.android.sdk.core.ecomm.ECommerceEvents;
import com.rudderstack.android.sdk.core.ecomm.ECommerceParamNames;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class FirebaseIntegrationFactory extends RudderIntegration<FirebaseAnalytics> {
    private static final String FIREBASE_KEY = "Firebase";
    private static FirebaseAnalytics _firebaseAnalytics;

    private static final List<String> GOOGLE_RESERVED_KEYWORDS = Arrays.asList(
            "age", "gender", "interest"
    );

    private final Set<String> RESERVED_PARAM_NAMES = new HashSet<String>() {
        {
            add("product_id");
            add("name");
            add("category");
            add("quantity");
            add("price");
            add("currency");
            add("value");
            add("revenue");
            add("total");
            add("order_id");
            add("tax");
            add("shipping");
            add("coupon");
            add("cart_id");
            add("payment_method");
            add("query");
            add("list_id");
            add("promotion_id");
            add("creative");
            add("affiliation");
            add("share_via");
        }
    };

    private final Map<String, String> EVENTS_MAPPING = new HashMap<String, String>() {
        {
            put(ECommerceEvents.PAYMENT_INFO_ENTERED, FirebaseAnalytics.Event.ADD_PAYMENT_INFO);
            put(ECommerceEvents.PRODUCT_ADDED, FirebaseAnalytics.Event.ADD_TO_CART);
            put(ECommerceEvents.PRODUCT_ADDED_TO_WISH_LIST, FirebaseAnalytics.Event.ADD_TO_WISHLIST);
            put("Application Opened", FirebaseAnalytics.Event.APP_OPEN);
            put(ECommerceEvents.CHECKOUT_STARTED, FirebaseAnalytics.Event.BEGIN_CHECKOUT);
            put(ECommerceEvents.ORDER_COMPLETED, FirebaseAnalytics.Event.PURCHASE);
            put(ECommerceEvents.ORDER_REFUNDED, FirebaseAnalytics.Event.REFUND);
            put(ECommerceEvents.PRODUCTS_SEARCHED, FirebaseAnalytics.Event.SEARCH); // No Product
            put(ECommerceEvents.CART_SHARED, FirebaseAnalytics.Event.SHARE);
            put(ECommerceEvents.PRODUCT_SHARED, FirebaseAnalytics.Event.SHARE);
            put(ECommerceEvents.PRODUCT_VIEWED, FirebaseAnalytics.Event.VIEW_ITEM);
            put(ECommerceEvents.PRODUCT_LIST_VIEWED, FirebaseAnalytics.Event.VIEW_ITEM_LIST);
            put(ECommerceEvents.PRODUCT_REMOVED, FirebaseAnalytics.Event.REMOVE_FROM_CART);
            put(ECommerceEvents.PRODUCT_CLICKED, FirebaseAnalytics.Event.SELECT_CONTENT); // ROOT
            put(ECommerceEvents.PROMOTION_VIEWED, FirebaseAnalytics.Event.VIEW_PROMOTION);
            put(ECommerceEvents.PROMOTION_CLICKED, FirebaseAnalytics.Event.SELECT_PROMOTION);
            put(ECommerceEvents.CART_VIEWED, FirebaseAnalytics.Event.VIEW_CART);
        }
    };

    private final Map<String, String> PRODUCTS_MAPPING = new HashMap<String, String>() {
        {
            put("product_id", FirebaseAnalytics.Param.ITEM_ID);
            put("id", FirebaseAnalytics.Param.ITEM_ID);
            put("name", FirebaseAnalytics.Param.ITEM_NAME);
            put("category", FirebaseAnalytics.Param.ITEM_CATEGORY);
            put("quantity", FirebaseAnalytics.Param.QUANTITY);
            put("price", FirebaseAnalytics.Param.PRICE);
        }
    };

    private final Set<String> PRODUCT_EVENT = new HashSet<String>() {
        {
            add(FirebaseAnalytics.Event.ADD_PAYMENT_INFO); // No Product at the root level
            add(FirebaseAnalytics.Event.ADD_TO_CART); // ROOT
            add(FirebaseAnalytics.Event.ADD_TO_WISHLIST); // ROOT
            add(FirebaseAnalytics.Event.BEGIN_CHECKOUT); // PRODUCTS - ARRAY
            add(FirebaseAnalytics.Event.REMOVE_FROM_CART); // ROOT
            add(FirebaseAnalytics.Event.VIEW_ITEM); // ROOT
            add(FirebaseAnalytics.Event.VIEW_ITEM_LIST); // PRODUCTS - ARRAY
            add(FirebaseAnalytics.Event.PURCHASE); // PRODUCTS - ARRAY
            add(FirebaseAnalytics.Event.REFUND); // PRODUCTS - ARRAY
            add(FirebaseAnalytics.Event.VIEW_CART); // PRODUCTS - ARRAY
        }
    };

    public static Factory FACTORY = new Factory() {
        @Override
        public RudderIntegration<?> create(@Nullable Object settings, @NonNull RudderClient client, @NonNull RudderConfig rudderConfig) {
            RudderLogger.logDebug("Creating RudderIntegrationFactory");
            return new FirebaseIntegrationFactory(settings, client, rudderConfig);
        }

        @Override
        public String key() {
            return FIREBASE_KEY;
        }
    };

    private FirebaseIntegrationFactory(@Nullable Object config, @NonNull RudderClient client, @NonNull RudderConfig rudderConfig) {
        if (RudderClient.getApplication() != null) {
            RudderLogger.logDebug("Initializing Firebase SDK");
            _firebaseAnalytics = FirebaseAnalytics.getInstance(RudderClient.getApplication());
        }
    }

    private void processRudderEvent(@NonNull RudderMessage element) {
        if (element.getType() != null && _firebaseAnalytics != null) {
            Bundle params;
            Map<String, Object> properties;
            switch (element.getType()) {
                case MessageType.IDENTIFY:
                    if (!TextUtils.isEmpty(element.getUserId())) {
                        RudderLogger.logDebug("Setting userId to Firebase");
                        _firebaseAnalytics.setUserId(element.getUserId());
                    }
                    Map<String, String> traits = Utils.transformUserTraits(element.getTraits());
                    for (String key : traits.keySet()) {
                        if (key.equals("userId")) {
                            continue; // userId is already set
                        }
                        String firebaseKey = key.toLowerCase().trim().replace(" ", "_");
                        if (firebaseKey.length() > 40) {
                            firebaseKey = firebaseKey.substring(0, 40);
                        }
                        if (!GOOGLE_RESERVED_KEYWORDS.contains(firebaseKey)) {
                            RudderLogger.logDebug("Setting userProperties to Firebase");
                            _firebaseAnalytics.setUserProperty(firebaseKey, traits.get(key));
                        }
                    }
                    break;
                case MessageType.SCREEN:
                    params = new Bundle();
                    properties = element.getProperties();
                    if (properties != null) {
                        if (properties.containsKey(FirebaseAnalytics.Param.SCREEN_NAME)) {
                            params.putString(FirebaseAnalytics.Param.SCREEN_NAME, Utils.getString(properties.get(FirebaseAnalytics.Param.SCREEN_NAME)));
                        }
                        if (properties.containsKey(FirebaseAnalytics.Param.SCREEN_CLASS)) {
                            params.putString(FirebaseAnalytics.Param.SCREEN_CLASS, Utils.getString(properties.get(FirebaseAnalytics.Param.SCREEN_CLASS)));
                        }
                    }
                    attachAllCustomProperties(params, element.getProperties());
                    _firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
                    break;
                case MessageType.TRACK:
                    String eventName = element.getEventName();
                    if (eventName != null && !eventName.isEmpty()) {
                        String firebaseEvent;
                        properties = element.getProperties();
                        params = new Bundle();
                        // Handle E-Commerce event
                        if (EVENTS_MAPPING.containsKey(eventName)) {
                            firebaseEvent = EVENTS_MAPPING.get(eventName);

                            if (firebaseEvent != null && properties != null) {
                                if (firebaseEvent.equals(FirebaseAnalytics.Event.SHARE)) {
                                    if (properties.containsKey("cart_id")) {
                                        params.putString(FirebaseAnalytics.Param.ITEM_ID, (String) properties.get("cart_id"));
                                    } else if (properties.containsKey("product_id")) {
                                        params.putString(FirebaseAnalytics.Param.ITEM_ID, (String) properties.get("product_id"));
                                    }
                                }

                                if (firebaseEvent.equals(FirebaseAnalytics.Event.VIEW_PROMOTION) || firebaseEvent.equals(FirebaseAnalytics.Event.SELECT_PROMOTION)) {
                                    if (properties.containsKey("name")) {
                                        params.putString(FirebaseAnalytics.Param.PROMOTION_NAME, Utils.getString(properties.get("name")));
                                    }
                                }

                                if (firebaseEvent.equals(FirebaseAnalytics.Event.SELECT_CONTENT)) {
                                    if (properties.containsKey("product_id")) {
                                        params.putString(FirebaseAnalytics.Param.ITEM_ID, (String) properties.get("product_id"));
                                    }
                                }

                                if (eventName.equals(ECommerceEvents.PRODUCT_SHARED)) {
                                    params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product");
                                }

                                if (eventName.equals(ECommerceEvents.CART_SHARED)) {
                                    params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "cart");
                                }

                                handleECommerce(params, properties, firebaseEvent);
                            }
                        }
                        // Handle custom event
                        else {
                            firebaseEvent = eventName.toLowerCase().trim().replace(" ", "_");
                            if (firebaseEvent.length() > 40) {
                                firebaseEvent = firebaseEvent.substring(0, 40);
                            }
                        }

                        attachAllCustomProperties(params, properties);

                        if (!TextUtils.isEmpty(firebaseEvent)) {
                            this.attachAllCustomProperties(params, element.getProperties());
                            RudderLogger.logDebug("Logged \"" + firebaseEvent + "\" to Firebase");
                            _firebaseAnalytics.logEvent(firebaseEvent, params);
                        }
                    }
                    break;
                default:
                    RudderLogger.logInfo("MessageType is not supported through " + FIREBASE_KEY);
                    break;
            }
        }
    }

    private void handleECommerce(Bundle params, Map<String, Object> properties, String firebaseEvent) {
        if (properties != null) {
            if (properties.containsKey("revenue") && Utils.isCompatibleWithFloat(properties.get("revenue"))) {
                params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("revenue")));
            } else if (properties.containsKey("value") && Utils.isCompatibleWithFloat(properties.get("value"))) {
                params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("value")));
            } else if (properties.containsKey("total") && Utils.isCompatibleWithFloat(properties.get("total"))) {
                params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("total")));
            }

            // Handle Products array or Product at the root level for the allowed events
            if (PRODUCT_EVENT.contains(firebaseEvent)) {
                handleProducts(params, properties);
            }

            if (properties.containsKey("payment_method")) {
                params.putString(FirebaseAnalytics.Param.PAYMENT_TYPE, Utils.getString(properties.get("payment_method")));
            }
            if (properties.containsKey("coupon")) {
                params.putString(FirebaseAnalytics.Param.COUPON, Utils.getString(properties.get("coupon")));
            }
            // Set default Currency to USD, if it is not present in the payload
            if (properties.containsKey("currency")) {
                params.putString(FirebaseAnalytics.Param.CURRENCY, Utils.getString(properties.get("currency")));
            } else {
                params.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
            }
            if (properties.containsKey("query")) {
                params.putString(FirebaseAnalytics.Param.SEARCH_TERM, Utils.getString(properties.get("query")));
            }
            if (properties.containsKey("list_id")) {
                params.putString(FirebaseAnalytics.Param.ITEM_LIST_ID, Utils.getString(properties.get("list_id")));
            }
            if (properties.containsKey("promotion_id")) {
                params.putString(FirebaseAnalytics.Param.PROMOTION_ID, Utils.getString(properties.get("promotion_id")));
            }
            if (properties.containsKey("creative")) {
                params.putString(FirebaseAnalytics.Param.CREATIVE_NAME, Utils.getString(properties.get("creative")));
            }
            if (properties.containsKey("affiliation")) {
                params.putString(FirebaseAnalytics.Param.AFFILIATION, Utils.getString(properties.get("affiliation")));
            }
            if (properties.containsKey("shipping")) {
                params.putDouble(FirebaseAnalytics.Param.SHIPPING, Utils.getDouble(properties.get("shipping")));
            }
            if (properties.containsKey("tax")) {
                params.putDouble(FirebaseAnalytics.Param.TAX, Utils.getDouble(properties.get("tax")));
            }
            if (properties.containsKey("order_id")) {
                params.putString(FirebaseAnalytics.Param.TRANSACTION_ID, (String) properties.get("order_id"));
            }
            if (properties.containsKey("share_via")) {
                params.putString(FirebaseAnalytics.Param.METHOD, (String) properties.get("share_via"));
            }
        }
    }

    private void handleProducts(Bundle params, Map<String, Object> properties) {
        // If Products array is present
        if (properties.containsKey(ECommerceParamNames.PRODUCTS)) {
            JSONArray products = getJSONArray(properties.get(ECommerceParamNames.PRODUCTS));
            if (products != null && products.length() > 0) {
                ArrayList<Bundle> mappedProducts = new ArrayList<>();
                for (int i = 0; i < products.length(); i++) {
                    try {
                        JSONObject product = (JSONObject) products.get(i);
                        Bundle productBundle = new Bundle();
                        for (String key : PRODUCTS_MAPPING.keySet()) {
                            if (product.has(key)) {
                                putValue(productBundle, PRODUCTS_MAPPING.get(key), product.get(key));
                            }
                        }
                        mappedProducts.add(productBundle);
                    } catch (JSONException e) {
                        RudderLogger.logDebug("Error while getting Products: " + products);
                    }
                }
                params.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, mappedProducts);
            }
        }
        // If Product is present at the root level
        else {
            Bundle productBundle = null;
            for (String key : PRODUCTS_MAPPING.keySet()) {
                productBundle = new Bundle();
                if (properties.containsKey(key)) {
                    putValue(productBundle, PRODUCTS_MAPPING.get(key), properties.get(key));
                }
            }
            if (productBundle != null) {
                params.putParcelableArray(FirebaseAnalytics.Param.ITEMS, new Bundle[]{productBundle});
            }
        }

    }

    private void attachAllCustomProperties(Bundle params, Map<String, Object> properties) {
        if (properties != null) {
            for (String key : properties.keySet()) {
                String firebaseKey = key.toLowerCase().trim().replace(" ", "_");
                if (!RESERVED_PARAM_NAMES.contains(firebaseKey)) {
                    if (firebaseKey.length() > 40) {
                        firebaseKey = firebaseKey.substring(0, 40);
                    }
                    putValue(params, firebaseKey, properties.get(key));
                }
            }
        }
    }

    private static void putValue(Bundle params,  String firebaseKey, Object value) {
        if (value != null) {
            if (value instanceof Boolean) {
                params.putBoolean(firebaseKey, (Boolean) value);
            } else if (value instanceof Integer) {
                params.putInt(firebaseKey, (Integer) value);
            } else if (value instanceof Long) {
                params.putLong(firebaseKey, (Long) value);
            } else if (value instanceof Double) {
                params.putDouble(firebaseKey, (Double) value);
            } else if (value instanceof String) {
                String val = (String) value;
                if (val.length() > 100) val = val.substring(0, 100);
                params.putString(firebaseKey, val);
            } else {
                String val = new Gson().toJson(value);
                // if length exceeds 100, don't send the property
                if (!(val.length() > 100)) params.putString(firebaseKey, val);
            }
        }
    }

    // Handle product object of type JSONArray, ArrayList, JSOONObject and LinkedHashMap
    private JSONArray getJSONArray(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        if (object instanceof List){
            ArrayList<Object> arrayList = new ArrayList<>();
            arrayList.addAll((Collection<?>) object);
            return new JSONArray(arrayList);
        }
        if (object instanceof JSONObject) {
            return new JSONArray().put((JSONObject) object);
        }
        if (object instanceof LinkedHashMap) {
            LinkedHashMap product = (LinkedHashMap) object;
            JSONObject productJsonObject = new JSONObject();
            for (Object key: product.keySet()) {
                try {
                    productJsonObject.put((String) key, product.get(key));
                } catch (JSONException e) {
                    RudderLogger.logDebug("Error while converting the products: "+ object +" to JSONArray type");
                }
            }
            return new JSONArray().put(productJsonObject);
        }
        try {
            return new JSONArray((ArrayList) object);
        } catch (Exception e) {
            RudderLogger.logDebug("Error while converting the products: "+ object +" to JSONArray type");
        }
        return null;
    }

    @Override
    public void reset() {
        // Firebase doesn't support reset functionality
    }

    @Override
    public void dump(@Nullable RudderMessage element) {
        if (element != null) {
            processRudderEvent(element);
        }
    }

    @Override
    public FirebaseAnalytics getUnderlyingInstance() {
        return _firebaseAnalytics;
    }
}
