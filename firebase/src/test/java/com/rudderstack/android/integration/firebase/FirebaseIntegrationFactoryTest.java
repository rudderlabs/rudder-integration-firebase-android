package com.rudderstack.android.integration.firebase;

import static com.rudderstack.android.integration.firebase.TestConstants.getProductMap;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductSingleJSONArray;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductSingleLinkedHashMap;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductsArrayList;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductsJSONArray;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductsLinkedHashMap;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductsList;
import static com.rudderstack.android.integration.firebase.TestUtils.jsonArrayToString;

import org.json.JSONArray;
import org.junit.Assert;
import org.junit.Test;
import static org.mockito.Mockito.*;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirebaseIntegrationFactoryTest {

    @Test
    public void whenObjectParameterIsNull() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        Assert.assertNull(firebaseIntegrationFactory.getProductsJSONArray(null));
    }

    @Test
    public void whenObjectParameterIsJSONArray() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);
        JSONArray products = getProductsJSONArray();

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        verifyJSONString(products, firebaseIntegrationFactory.getProductsJSONArray(products));
    }

    @Test
    public void whenObjectParameterIsList() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);
        JSONArray products = getProductsJSONArray();
        List<Map<String, Object>> productList = getProductsList();

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        verifyJSONString(products, firebaseIntegrationFactory.getProductsJSONArray(productList));
    }

    @Test
    public void whenObjectParameterIsMap() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);
        JSONArray product = getProductSingleJSONArray();
        Map<String, Object> productList = getProductMap();

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        verifyJSONString(product, firebaseIntegrationFactory.getProductsJSONArray(productList));
    }

    @Test
    public void whenObjectParameterIsLinkedHashMap() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);
        JSONArray products = getProductsJSONArray();
        List<LinkedHashMap<String, Object>> productList = getProductsLinkedHashMap();

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        verifyJSONString(products, firebaseIntegrationFactory.getProductsJSONArray(productList));
    }

    @Test
    public void whenObjectParameterIsSingleLinkedHashMap() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);
        JSONArray product = getProductSingleJSONArray();
        LinkedHashMap<String, Object> productList = getProductSingleLinkedHashMap();

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        verifyJSONString(product, firebaseIntegrationFactory.getProductsJSONArray(productList));
    }

    @Test
    public void whenObjectParameterIsArrayList() {
        FirebaseIntegrationFactory firebaseIntegrationFactory = mock(FirebaseIntegrationFactory.class);
        JSONArray product = getProductsJSONArray();
        ArrayList<Map<String, Object>> productList = getProductsArrayList();

        when(firebaseIntegrationFactory.getProductsJSONArray(any())).thenCallRealMethod();

        verifyJSONString(product, firebaseIntegrationFactory.getProductsJSONArray(productList));
    }

    private void verifyJSONString(JSONArray products1, JSONArray products2) {
        Assert.assertEquals(jsonArrayToString(products1), jsonArrayToString(products2));
    }
}
