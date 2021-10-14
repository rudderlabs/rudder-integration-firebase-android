package com.rudderstack.android.sample.kotlin

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rudderstack.android.sdk.core.RudderMessageBuilder
import com.rudderstack.android.sdk.core.RudderTraits
import com.rudderstack.android.sdk.core.TrackPropertyBuilder
import com.rudderstack.android.sdk.core.ecomm.ECommerceEvents
import com.rudderstack.android.sdk.core.ecomm.ECommerceOrder
import com.rudderstack.android.sdk.core.ecomm.ECommerceProduct
import com.rudderstack.android.sdk.core.ecomm.events.OrderCompletedEvent
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        sendEvents()
    }

    private fun sendEvents() {
//        MainApplication.rudderClient.track(
//            RudderMessageBuilder()
//                .setEventName("daily_rewards_claim_4")
//                .setProperty(
//                    TrackPropertyBuilder()
//                        .setCategory("test_category")
//                        .build()
//                )
//                .setUserId("test_user_id")
//        )
//
//        // Creating Different Primitve types
//        val myByte: Byte = 100
//        val myShort: Short = 5000
//        val myInt = 100000
//        val myLong = 15000000000L
//        val myFloat = 5.75f
//        val myDouble = 19.99
//        val f1 = 35e3f
//        val d1 = 12E4
//        val isJavaFun = true
//        val isFishTasty = false
//        val myGrade = 'B'
//        val greeting = "Hello World"
//
//        // Creating Different Object types
//        val map2 = HashMap<String, String>()
//        map2["key2"] = "value2"
//        val answer1 = JSONObject("""{"name":"test name", "age":25}""")
//        val map = mapOf("product_id" to "pro1", "name" to "monopoly", "price" to 1000)
//        val mutableMap2: MutableMap<String, String> = mutableMapOf<String, String>()
//        mutableMap2.put("name", "Ashu")
//        mutableMap2.put("city", "Delhi")
//
//        // Creating Different Array types in Kotlin
//        val address1 = JSONObject()
//        address1.put("city", "Hyderabad")
//        address1.put("state", "Telangana")
//        address1.put("country", "India")
//        address1.put("street", "Mig")
//        val address2 = JSONObject()
//        address2.put("city", "Hyderabad")
//        address2.put("state", "Telangana")
//        address2.put("country", "India")
//        address2.put("street", "Mig")
//        val arr = intArrayOf(1, 2, 3)
//        val justarr = arrayOf(1, 2, 3, 4)
//        val list = listOf(map);
//        val addressesArray = JSONArray()
//        addressesArray.put(address1)
//        addressesArray.put(address2)
//
//
//        val traits = RudderTraits()
//        traits.putBirthday(Date())
//        traits.putEmail("ravi@desu.com")
//        traits.putFirstName("ravi")
//        traits.putLastName("kumar")
//        traits.putGender("Male")
//        traits.putPhone("8919969994")
//        traits.put("myByte", myByte)
//        traits.put("myShort", myShort)
//        traits.put("myInt", myInt)
//        traits.put("myLong", myLong)
//        traits.put("myFloat", myFloat)
//        traits.put("myDouble", myDouble)
//        traits.put("f1", f1)
//        traits.put("d1", d1)
//        traits.put("isJavaFun", isJavaFun)
//        traits.put("isFishTasty", isFishTasty)
//        traits.put("myGrade", myGrade)
//        traits.put("greeting", greeting)
//        // Inserting Array types into Traits
//        traits.put("intarr", arr)
//        traits.put("kotlinlist", list);
//        traits.put("addressArray", addressesArray)
//        traits.put("justarr", justarr)
//        // Inserting Object Types into Traits
//        traits.put("hashmap", map2)
//        traits.put("jsonobj", answer1)
//        traits.put("kotlinmap", map)
//        traits.put("mutablemap", mutableMap2)
//
//
//
//        MainApplication.rudderClient!!.identify("user_id_4", traits, null)
//
//        MainApplication.rudderClient.track(
//            RudderMessageBuilder()
//                .setEventName("level_up")
//                .setProperty(
//                    TrackPropertyBuilder()
//                        .setCategory("test_category")
//                        .build()
//                )
//                .setUserId("test_user_id")
//        )
//
//        MainApplication.rudderClient.reset()
//
//        val revenueProperty = TrackPropertyBuilder()
//            .setCategory("test_category")
//            .build()
//        revenueProperty.put("total", 4.99)
//        revenueProperty.put("currency", "USD")
//        MainApplication.rudderClient.track(
//            RudderMessageBuilder()
//                .setEventName("revenue")
//                .setProperty(revenueProperty)
//                .setUserId("test_user_id")
//        )


        val productA: ECommerceProduct =
            ECommerceProduct.Builder().withProductId("prod1").withCurrency("USD").withPrice(4.99f)
                .withName("Product Name 1").build()
        val orderCompleted: OrderCompletedEvent = OrderCompletedEvent().withOrder(
            ECommerceOrder.Builder().withOrderId("order1").withValue(4.99f).withProduct(productA)
                .build()
        )
        MainApplication.rudderClient.track(
            ECommerceEvents.ORDER_COMPLETED,
            orderCompleted.properties()
        )

    }
}
