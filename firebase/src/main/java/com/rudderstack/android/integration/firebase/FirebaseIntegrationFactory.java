package com.rudderstack.android.integration.firebase;

import static com.rudderstack.android.integration.firebase.Utils.ECOMMERCE_PROPERTY_MAPPING;
import static com.rudderstack.android.integration.firebase.Utils.ECOMMERCE_EVENTS_MAPPING;
import static com.rudderstack.android.integration.firebase.Utils.EVENT_WITH_PRODUCTS_ARRAY;
import static com.rudderstack.android.integration.firebase.Utils.EVENT_WITH_PRODUCTS_AT_ROOT;
import static com.rudderstack.android.integration.firebase.Utils.IDENTIFY_RESERVED_KEYWORDS;
import static com.rudderstack.android.integration.firebase.Utils.PRODUCT_PROPERTIES_MAPPING;
import static com.rudderstack.android.integration.firebase.Utils.TRACK_RESERVED_KEYWORDS;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;

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
                        if (!IDENTIFY_RESERVED_KEYWORDS.contains(firebaseKey)) {
                            RudderLogger.logDebug("Setting userProperties to Firebase");
                            _firebaseAnalytics.setUserProperty(firebaseKey, traits.get(key));
                        }
                    }
                    break;
                case MessageType.SCREEN:
                    String screenName = element.getEventName();
                    if (Utils.isEmpty(screenName)) {
                        RudderLogger.logDebug("Since the event name is not present, the screen event sent to Firebase has been dropped.");
                        return;
                    }
                    Bundle params = new Bundle();
                    params.putString(FirebaseAnalytics.Param.SCREEN_NAME, screenName);
                    attachAllCustomProperties(params, element.getProperties(), false);
                    _firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SCREEN_VIEW, params);
                    break;
                case MessageType.TRACK:
                    String eventName = element.getEventName();
                    if (Utils.isEmpty(eventName)) {
                        RudderLogger.logDebug("Since the event name is not present, the track event sent to Firebase has been dropped.");
                        return;
                    }
                    if (eventName.equals("Application Opened")) {
                        handleApplicationOpenedEvent(element.getProperties());
                    }
                    else if (ECOMMERCE_EVENTS_MAPPING.containsKey(eventName)) {
                        handleECommerceEvent(eventName, element.getProperties());
                    }
                    else {
                        handleCustomEvent(eventName, element.getProperties());
                    }
                    break;
                default:
                    RudderLogger.logInfo("MessageType is not supported through " + FIREBASE_KEY);
                    break;
            }
        }
    }

    private void handleApplicationOpenedEvent(@Nullable Map<String, Object> properties) {
        String firebaseEvent = FirebaseAnalytics.Event.APP_OPEN;
        Bundle params = new Bundle();
        makeFirebaseEvent(firebaseEvent, params, properties, true);
    }

    private void handleECommerceEvent(@NonNull String eventName, @Nullable Map<String, Object> properties) {
        Bundle params = new Bundle();
        String firebaseEvent = Utils.getECommerceEventMapping(eventName);
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
            if (firebaseEvent.equals(FirebaseAnalytics.Event.SELECT_CONTENT)) {
                if (!Utils.isEmpty(properties.get("product_id"))) {
                    params.putString(FirebaseAnalytics.Param.ITEM_ID, Utils.getString(properties.get("product_id")));
                }
                params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product");
            }
            addConstantParamsForECommerceEvent(params, eventName);
            handleECommerceEventProperties(params, properties, firebaseEvent);
        }
        makeFirebaseEvent(firebaseEvent, params, properties, true);
    }

    private void addConstantParamsForECommerceEvent(Bundle params, String eventName) {
        if (eventName.equals(ECommerceEvents.PRODUCT_SHARED)) {
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "product");
        } else if (eventName.equals(ECommerceEvents.CART_SHARED)) {
            params.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "cart");
        }
    }

    private void handleCustomEvent(@NonNull String eventName, @Nullable Map<String, Object> properties) {
        Bundle params = new Bundle();
        String firebaseEvent = Utils.getTrimKey(eventName);
        makeFirebaseEvent(firebaseEvent, params, properties, false);
    }

    private void makeFirebaseEvent(@NonNull String firebaseEvent, @NonNull Bundle params, @Nullable Map<String, Object> properties, boolean filterReservedKeywords) {
        attachAllCustomProperties(params, properties, filterReservedKeywords);
        RudderLogger.logDebug("Logged \"" + firebaseEvent + "\" to Firebase and properties: " + properties);
        _firebaseAnalytics.logEvent(firebaseEvent, params);
    }

    private void handleECommerceEventProperties(Bundle params, Map<String, Object> properties, String firebaseEvent) {
        if (properties.containsKey("revenue") && !Utils.isEmpty(properties.get("revenue")) && Utils.isDouble(properties.get("revenue"))) {
            params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("revenue")));
        } else if (properties.containsKey("value") && !Utils.isEmpty(properties.get("value")) && Utils.isDouble(properties.get("value"))) {
            params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("value")));
        } else if (properties.containsKey("total") && !Utils.isEmpty(properties.get("total")) && Utils.isDouble(properties.get("total"))) {
            params.putDouble(FirebaseAnalytics.Param.VALUE, Utils.getDouble(properties.get("total")));
        }
        if (EVENT_WITH_PRODUCTS_ARRAY.contains(firebaseEvent) && properties.containsKey(ECommerceParamNames.PRODUCTS)) {
            handleProducts(params, properties, true);
        }
        if (EVENT_WITH_PRODUCTS_AT_ROOT.contains(firebaseEvent)) {
            handleProducts(params, properties, false);
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
        // order_id is being mapped to FirebaseAnalytics.Param.TRANSACTION_ID.∂
        if (properties.containsKey("order_id") && !Utils.isEmpty(properties.get("order_id"))) {
            params.putString(FirebaseAnalytics.Param.TRANSACTION_ID, Utils.getString(properties.get("order_id")));
            // As this change is made at version 2.0.2. So to have the backward compatibility, we're inserting order_id properties as well.
            params.putString("order_id", Utils.getString(properties.get("order_id")));
        }
    }

    private void handleProducts(Bundle params, Map<String, Object> properties, boolean isProductsArray) {
        // If Products array is present
        if (isProductsArray) {
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

    private void attachAllCustomProperties(@NonNull Bundle params, @Nullable Map<String, Object> properties, boolean filterReservedKeywords) {
        if (Utils.isEmpty(properties)) {
            return;
        }
        for (String key : properties.keySet()) {
            String firebaseKey = Utils.getTrimKey(key);
            Object value = properties.get(key);
            if ((filterReservedKeywords && TRACK_RESERVED_KEYWORDS.contains(firebaseKey)) || Utils.isEmpty(value)) {
                continue;
            }
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

    private static void putProductValue(Bundle params, String firebaseKey, Object value) {
        if (value == null) {
            return;
        }
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

    // Handle product object of type ArrayList, JSONObject and hMap
    @VisibleForTesting
    JSONArray getProductsJSONArray(Object object) {
        if (object == null) {
            return null;
        }
        if (object instanceof JSONArray) {
            return (JSONArray) object;
        }
        if (object instanceof List) {
            ArrayList<Object> arrayList = new ArrayList<>((Collection<?>) object);
            return new JSONArray(arrayList);
        }
        if (object instanceof Map) {
            Map<?, ?> product = (Map<?, ?>) object;
            JSONObject productJsonObject = new JSONObject();
            for (String key : PRODUCT_PROPERTIES_MAPPING.keySet()) {
                if (product.containsKey(key)) {
                    try {
                        productJsonObject.put(key, product.get(key));
                    } catch (JSONException e) {
                        RudderLogger.logDebug("Error while converting the Products value to JSONArray type");
                    }
                }
            }
            return Utils.isEmpty(productJsonObject) ? null : new JSONArray().put(productJsonObject);
        }
        return null;
    }

    @Override
    public void reset() {
        _firebaseAnalytics.setUserId(null);
        RudderLogger.logDebug("Reset: _firebaseAnalytics.setUserId(null);");
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
