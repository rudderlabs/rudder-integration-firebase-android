package com.rudderstack.android.integration.firebase;

import static com.rudderstack.android.integration.firebase.TestConstants.getProductMap;
import static com.rudderstack.android.integration.firebase.TestConstants.getProductSingleLinkedHashMap;

import org.junit.Assert;
import org.junit.Test;

import java.util.LinkedHashMap;
import java.util.Map;

public class UtilsTest {

    @Test
    public void whenGetStringIsHashMap() {
        Map<String, Object> products = getProductMap();

        String value = Utils.getString(products);

        Assert.assertNotNull(value);
        Assert.assertNotEquals(0, value.length());
    }

    @Test
    public void whenGetStringIsLinkedHashMap() {
        LinkedHashMap<String, Object> products = getProductSingleLinkedHashMap();

        String value = Utils.getString(products);

        Assert.assertNotNull(value);
        Assert.assertNotEquals(0, value.length());
    }
}
