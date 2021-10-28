package com.rudderstack.android.integration.firebase;

import static com.rudderstack.android.integration.firebase.Utils.ECOMMERCE_PROPERTY_MAPPING;
import static com.rudderstack.android.integration.firebase.Utils.ECOMMERCE_EVENTS_MAPPING;
import static com.rudderstack.android.integration.firebase.Utils.GOOGLE_RESERVED_KEYWORDS;
import static com.rudderstack.android.integration.firebase.Utils.PRODUCT_PROPERTIES_MAPPING;
import static com.rudderstack.android.integration.firebase.Utils.EVENT_WITH_PRODUCTS;
import static com.rudderstack.android.integration.firebase.Utils.RESERVED_PARAM_NAMES;

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
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirebaseIntegrationFactory extends RudderIntegration<FirebaseAnalytics> {
    private static final String FIREBASE_KEY = "Firebase";
    private static FirebaseAnalytics _firebaseAnalytics;

    public static Factory FACTORY = new Factory() {
        @Override
        public RudderIntegration<?> create(@Nullable Object settings, @NonNull RudderClient client, @NonNull RudderConfig rudderConfig) {
            RudderLogger.logDebug("Creating RudderIntegrationFactory");
            return new FirebaseIntegrationFactory();
        }

        @Override
        public String key() {
            return FIREBASE_KEY;
        }
    };

    private FirebaseIntegrationFactory() {
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
                        String firebaseKey = Utils.getTrimKey(key);
                        if (!GOOGLE_RESERVED_KEYWORDS.contains(firebaseKey)) {
                            RudderLogger.logDebug("Setting userProperties to Firebase");
                            _firebaseAnalytics.setUserProperty(firebaseKey, traits.get(key));
                        }
                    }
                    break;
                case MessageType.SCREEN:
                    String screenName = element.getEventName();
                    if(Utils.isEmpty(screenName))
                        return;
                    params = new Bundle();
                    params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
                    attachAllCustomProperties(params, element.getProperties());
                    _firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
                    break;
                case MessageType.TRACK:
                    String eventName = element.getEventName();
                    if (!Utils.isEmpty(eventName)) {
                        String firebaseEvent;
                        properties = element.getProperties();
                        params = new Bundle();
                        if (eventName.equals("Application Opened")) {
                            firebaseEvent = FirebaseAnalytics.Event.APP_OPEN;
                        }
                        // Handle E-Commerce event
                        else if (ECOMMERCE_EVENTS_MAPPING.containsKey(eventName)) {
                            firebaseEvent = ECOMMERCE_EVENTS_MAPPING.get(eventName);
                            if (!Utils.isEmpty(firebaseEvent) && !Utils.isEmpty(properties)) {
                                if (firebaseEvent.equals(FirebaseAnalytics.Event.SHARE)) {
                                    if (properties.containsKey("cart_id") && !Utils.isEmpty(properties.get("cart_id"))) {
                                        params.putString(FirebaseAnalytics.Param.ITEM_ID, Utils.getString(properties.get("cart_id")));
                                    } else if (properties.containsKey("product_id") && !Utils.isEmpty(properties.get("product_id"))) {
                                        params.putString(FirebaseAnalytics.Param.ITEM_ID, Utils.getString(properties.get("product_id")));
                                    }
                                }
                                if (firebaseEvent.equals(FirebaseAnalytics.Event.VIEW_PROMOTION) || firebaseEvent.equals(FirebaseAnalytics.Event.SELECT_PROMOTION)) {
                                    if (properties.containsKey("name") && !Utils.isEmpty(properties.get("name"))) {
                                        params.putString(FirebaseAnalytics.Param.PROMOTION_NAME, Utils.getString(properties.get("name")));
                                    }
                                }
                                if (eventName.equals(ECommerceEvents.PRODUCT_SHARED) || firebaseEvent.equals(FirebaseAnalytics.Event.SELECT_CONTENT)) {
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
                            firebaseEvent = Utils.getTrimKey(eventName);
                        }
                        if (firebaseEvent != null) {
                            attachAllCustomProperties(params, properties);
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
        if (properties.containsKey("revenue") && !Utils.isEmpty(properties.get("revenue")) && Utils.isDouble(properties.get("revenue"))) {
            params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("revenue")));
        } else if (properties.containsKey("value") && !Utils.isEmpty(properties.get("value")) && Utils.isDouble(properties.get("value"))) {
            params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("value")));
        } else if (properties.containsKey("total") && !Utils.isEmpty(properties.get("total")) && Utils.isDouble(properties.get("total"))) {
            params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("total")));
        }
        // Handle Products array or Product at the root level for the allowed events
        if (EVENT_WITH_PRODUCTS.contains(firebaseEvent)) {
            handleProducts(params, properties);
        }
        for (String propertyKey : properties.keySet()) {
            if (ECOMMERCE_PROPERTY_MAPPING.containsKey(propertyKey) && !Utils.isEmpty(properties.get(propertyKey))) {
                params.putString(ECOMMERCE_PROPERTY_MAPPING.get(propertyKey), Utils.getString(properties.get(propertyKey)));
            }
        }
        // Set default Currency to USD, if it is not present in the payload
        if (properties.containsKey("currency") && !Utils.isEmpty(properties.get("currency"))) {
            params.putString(FirebaseAnalytics.Param.CURRENCY, Utils.getString(properties.get("currency")));
        } else {
            params.putString(FirebaseAnalytics.Param.CURRENCY, "USD");
        }
        if (properties.containsKey("shipping") && !Utils.isEmpty(properties.get("shipping")) && Utils.isDouble(properties.get("shipping"))) {
            params.putDouble(FirebaseAnalytics.Param.SHIPPING, Utils.getDouble(properties.get("shipping")));
        }
        if (properties.containsKey("tax") && !Utils.isEmpty(properties.get("tax")) && Utils.isDouble(properties.get("tax"))) {
            params.putDouble(FirebaseAnalytics.Param.TAX, Utils.getDouble(properties.get("tax")));
        }
    }

    private void handleProducts(Bundle params, Map<String, Object> properties) {
        // If Products array is present
        if (properties.containsKey(ECommerceParamNames.PRODUCTS)) {
            JSONArray products = getProductsJSONArray(properties.get(ECommerceParamNames.PRODUCTS));
            if (!Utils.isEmpty(products)) {
                ArrayList<Bundle> mappedProducts = new ArrayList<>();
                for (int i = 0; i < products.length(); i++) {
                    try {
                        JSONObject product = (JSONObject) products.get(i);
                        Bundle productBundle = new Bundle();
                        for (String key : PRODUCT_PROPERTIES_MAPPING.keySet()) {
                            if (product.has(key)) {
                                putProductValue(productBundle, PRODUCT_PROPERTIES_MAPPING.get(key), product.get(key));
                            }
                        }
                        if (!productBundle.isEmpty()) {
                            mappedProducts.add(productBundle);
                        }
                    } catch (JSONException e) {
                        RudderLogger.logDebug("Error while getting Products: " + products);
                    } catch (ClassCastException e) {
                        // If products contains list of null value
                        RudderLogger.logDebug("Error while getting Products: " + products);
                    }
                }
                if (!mappedProducts.isEmpty()) {
                    params.putParcelableArrayList(FirebaseAnalytics.Param.ITEMS, mappedProducts);
                }
            }
        }
        // If Product is present at the root level
        else {
            Bundle productBundle = new Bundle();
            for (String key : PRODUCT_PROPERTIES_MAPPING.keySet()) {
                if (properties.containsKey(key)) {
                    putProductValue(productBundle, PRODUCT_PROPERTIES_MAPPING.get(key), properties.get(key));
                }
            }
            if (!productBundle.isEmpty()) {
                params.putParcelableArray(FirebaseAnalytics.Param.ITEMS, new Bundle[]{productBundle});
            }
        }
    }

    private void attachAllCustomProperties(Bundle params, Map<String, Object> properties) {
        if (!Utils.isEmpty(properties)) {
            for (String key : properties.keySet()) {
                String firebaseKey = Utils.getTrimKey(key);
                Object value = properties.get(key);
                if (!RESERVED_PARAM_NAMES.contains(firebaseKey) && !Utils.isEmpty(value)) {
                    if (value instanceof String) {
                        String val = (String) value;
                        if (val.length() > 100) val = val.substring(0, 100);
                        params.putString(firebaseKey, val);
                    } else if (value instanceof Integer) {
                        params.putInt(firebaseKey, (Integer) value);
                    } else if (value instanceof Long) {
                        params.putLong(firebaseKey, (Long) value);
                    } else if (value instanceof Double) {
                        params.putDouble(firebaseKey, (Double) value);
                    } else if (value instanceof Boolean) {
                        params.putBoolean(firebaseKey, (Boolean) value);
                    } else {
                        String val = new Gson().toJson(value);
                        // if length exceeds 100, don't send the property
                        if (!(val.length() > 100)) params.putString(firebaseKey, val);
                    }
                }
            }
        }
    }

    private static void putProductValue(Bundle params, String firebaseKey, Object value) {
        if (value != null) {
            switch (firebaseKey) {
                case FirebaseAnalytics.Param.ITEM_ID:
                case FirebaseAnalytics.Param.ITEM_NAME:
                case FirebaseAnalytics.Param.ITEM_CATEGORY:
                    params.putString(firebaseKey, Utils.getString(value));
                    return;
                case FirebaseAnalytics.Param.QUANTITY:
                    if (Utils.isLong(value)) {
                        params.putLong(firebaseKey, Utils.getLong(value));
                    }
                    return;
                case FirebaseAnalytics.Param.PRICE:
                    if (Utils.isDouble(value)) {
                        params.putDouble(firebaseKey, Utils.getDouble(value));
                    }
                    return;
                default:
                    RudderLogger.logDebug("Product value is not of expected type");
            }
        }
    }

    // Handle product object of type ArrayList, JSONObject and LinkedHashMap
    private JSONArray getProductsJSONArray(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        if (object instanceof List){
            ArrayList<Object> arrayList = new ArrayList<>((Collection<?>) object);
            return new JSONArray(arrayList);
        }
        if (object instanceof LinkedHashMap) {
            LinkedHashMap product = (LinkedHashMap) object;
            JSONObject productJsonObject = new JSONObject();
            for (Object key: PRODUCT_PROPERTIES_MAPPING.keySet()) {
                if (product.containsKey(key)) {
                    try {
                        productJsonObject.put((String) key, product.get(key));
                    } catch (JSONException e) {
                        RudderLogger.logDebug("Error while converting the Products value to JSONArray type");
                    }
                }
            }
            if (!Utils.isEmpty(productJsonObject)) {
                return new JSONArray().put(productJsonObject);
            }
            return null;
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
