package com.rudderstack.android.sample.kotlin

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderProperty
import com.rudderstack.android.sdk.core.RudderTraits
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.track_all_ecommerce_events).setOnClickListener { sendECommerceCustomAndScreenEvents() }
        findViewById<Button>(R.id.identify).setOnClickListener { (identify()) }

        findViewById<Button>(R.id.checkoutStarted).setOnClickListener { checkoutStartedEvent() }
        findViewById<Button>(R.id.orderCompleted).setOnClickListener { orderCompletedEvent() }
        findViewById<Button>(R.id.orderRefunded).setOnClickListener { orderRefundedEvent() }
        findViewById<Button>(R.id.productListViewed).setOnClickListener { productListViewedEvent() }
        findViewById<Button>(R.id.cartView).setOnClickListener { cartViewEvent() }

        findViewById<Button>(R.id.productAdded).setOnClickListener { productAddedEvent() }
        findViewById<Button>(R.id.productAddedToWishlist).setOnClickListener { productAddedToWishlistEvent() }
        findViewById<Button>(R.id.productViewed).setOnClickListener { productViewedEvent() }
        findViewById<Button>(R.id.productRemoved).setOnClickListener { productRemovedEvent() }

        findViewById<Button>(R.id.paymentInfoEntered).setOnClickListener { paymentInfoEnteredEvent() }
        findViewById<Button>(R.id.productsSearched).setOnClickListener { productsSearchedEvent() }
        findViewById<Button>(R.id.cartShared).setOnClickListener { cartSharedEvent() }
        findViewById<Button>(R.id.productShared).setOnClickListener { productSharedEvent() }
        findViewById<Button>(R.id.cartShared).setOnClickListener { cartSharedEvent() }
        findViewById<Button>(R.id.productShared).setOnClickListener { productSharedEvent() }
        findViewById<Button>(R.id.productClicked).setOnClickListener { productClickedEvent() }
        findViewById<Button>(R.id.promotionViewed).setOnClickListener { promotionViewedEvent() }
        findViewById<Button>(R.id.promotionClicked).setOnClickListener { promotionClickedEvent() }

        findViewById<Button>(R.id.customTrackEventWithoutProperties).setOnClickListener { customTrackEventWithoutProperties() }
        findViewById<Button>(R.id.customTrackEventWithProperties).setOnClickListener { customTrackEventWithProperties() }
        findViewById<Button>(R.id.screenEventWithoutProperties).setOnClickListener { screenEventWithoutProperties() }
        findViewById<Button>(R.id.screenEventWithProperties).setOnClickListener { screenEventWithProperties() }
        findViewById<Button>(R.id.reset).setOnClickListener { RudderClient.getInstance()?.reset() }
    }

    private fun identify() {
        // Creating Different Primitive types
        val myByte: Byte = 100
        val myShort: Short = 5000
        val myInt = 100000
        val myLong = 15000000000L
        val myFloat = 5.75f
        val myDouble = 19.99
        val f1 = 35e3f
        val d1 = 12E4
        val isJavaFun = true
        val isFishTasty = false
        val myGrade = 'B'
        val greeting = "Hello World"

        // Creating Different Object types
        val map2 = HashMap<String, String>()
        map2["key2"] = "value2"
        val mutableMap2: MutableMap<String, String> = mutableMapOf()
        mutableMap2["name"] = "Java World"
        mutableMap2["city"] = "Delhi"

        // Creating Different Array types in Kotlin
        val address1 = JSONObject()
        address1.put("city", "Hyderabad")
        address1.put("state", "Telangana")
        address1.put("country", "India")
        address1.put("street", "Mig")
        val address2 = JSONObject()
        address2.put("city", "Hyderabad")
        address2.put("state", "Telangana")
        address2.put("country", "India")
        address2.put("street", "Mig")
        val arr = intArrayOf(1, 2, 3)
        val justArr = arrayOf(1, 2, 3, 4)
        val addressesArray = JSONArray()
        addressesArray.put(address1)
        addressesArray.put(address2)


        val traits = RudderTraits()
        traits.putBirthday(Date())
        traits.putEmail("ravi@desu.com")
        traits.putFirstName("ravi")
        traits.putLastName("kumar")
        traits.putPhone("8919969994")
        traits.put("myByte", myByte)
        traits.put("myShort", myShort)
        traits.put("myInt", myInt)
        traits.put("myLong", myLong)
        traits.put("myFloat", myFloat)
        traits.put("myDouble", myDouble)
        traits.put("f1", f1)
        traits.put("d1", d1)
        traits.put("isJavaFun", isJavaFun)
        traits.put("isFishTasty", isFishTasty)
        traits.put("myGrade", myGrade)
        traits.put("greeting", greeting)
        // Inserting Array types into Traits
        traits.put("intArr", arr)
        traits.put("justArr", justArr)
        // Inserting Object Types into Traits
        MainApplication.rudderClient.identify("user_id_4", traits, null)
    }

    private fun sendECommerceCustomAndScreenEvents() {
        checkoutStartedEvent()
        orderCompletedEvent()
        orderRefundedEvent()
        productListViewedEvent()
        cartViewEvent()

        productAddedEvent()
        productAddedToWishlistEvent()
        productViewedEvent()
        productRemovedEvent()

        paymentInfoEnteredEvent()
        productsSearchedEvent()
        cartSharedEvent()
        productSharedEvent()
        productClickedEvent()
        promotionViewedEvent()
        promotionClickedEvent()
    }

    // Events with multiple products array
    private fun checkoutStartedEvent() {
        MainApplication.rudderClient.track(
            "Checkout Started",
            getStandardAndCustomProperties()
                .putValue("products", getMultipleProducts())
        )
    }

    private fun orderCompletedEvent() {
        MainApplication.rudderClient.track(
            "Order Completed",
            getStandardAndCustomProperties()
                .putValue("products", getMultipleProducts())
        )

        MainApplication.rudderClient.track(
            "Order Completed", RudderProperty()
                .putValue("value", 200)
        )

        MainApplication.rudderClient.track(
            "Order Completed", RudderProperty()
                .putValue("total", 300)
        )
    }

    private fun orderRefundedEvent() {
        MainApplication.rudderClient.track(
            "Order Refunded",
            getStandardAndCustomProperties()
                .putValue("products", getMultipleProducts())
        )
    }

    private fun productListViewedEvent() {
        MainApplication.rudderClient.track(
            "Product List Viewed",
            getStandardAndCustomProperties()
                .putValue("products", getMultipleProducts())
        )
    }

    private fun cartViewEvent() {
        MainApplication.rudderClient.track(
            "Cart Viewed",
            getStandardAndCustomProperties()
                .putValue("products", getMultipleProducts())
        )
    }

    // Events with single products array

    private fun productAddedEvent() {
        MainApplication.rudderClient.track(
            "Product Added", getStandardCustomAndProductAtRoot()
        )
    }

    private fun productAddedToWishlistEvent() {
        MainApplication.rudderClient.track(
            "Product Added to Wishlist", getStandardCustomAndProductAtRoot()
        )
    }

    private fun productViewedEvent() {
        MainApplication.rudderClient.track(
            "Product Viewed", getStandardCustomAndProductAtRoot()
        )
    }

    private fun productRemovedEvent() {
        MainApplication.rudderClient.track(
            "Product Removed", getStandardCustomAndProductAtRoot()
        )
    }

    // Events without products properties

    private fun paymentInfoEnteredEvent() {
        MainApplication.rudderClient.track(
            "Payment Info Entered", getStandardAndCustomProperties()
        )
    }

    private fun productsSearchedEvent() {
        MainApplication.rudderClient.track(
            "Products Searched", getStandardAndCustomProperties()
        )
    }

    private fun cartSharedEvent() {
        MainApplication.rudderClient.track(
            "Cart Shared",
            getStandardAndCustomProperties()
                .putValue("cart_id", "item value - 1")
        )
        MainApplication.rudderClient.track(
            "Cart Shared",
            getStandardAndCustomProperties()
                .putValue("product_id", "item value - 2")
        )
    }

    private fun productSharedEvent() {
        MainApplication.rudderClient.track(
            "Product Shared", RudderProperty()
                .putValue("cart_id", "item value - 1")
        )
        MainApplication.rudderClient.track(
            "Product Shared", RudderProperty()
                .putValue("product_id", "item value - 2")
        )
    }

    private fun productClickedEvent() {
        MainApplication.rudderClient.track(
            "Product Clicked",
            getStandardAndCustomProperties()
                .putValue("product_id", "Item id - 1")
        )
    }

    private fun promotionViewedEvent() {
        MainApplication.rudderClient.track(
            "Promotion Viewed",
            getStandardAndCustomProperties()
                .putValue("name", "promotion name-1")
        )
    }

    private fun promotionClickedEvent() {
        MainApplication.rudderClient.track(
            "Promotion Clicked",
            getStandardAndCustomProperties()
                .putValue("name", "promotion name-1")
        )
    }

    private fun getMultipleProducts(): List<Map<String, Any>> {
        val product1: MutableMap<String, Any> = HashMap()
        product1["product_id"] = "RSPro1"
        product1["name"] = "RSMonopoly1"
        product1["price"] = 1000.2
        product1["quantity"] = "100"
        product1["category"] = "RSCat1"
        val product2: MutableMap<String, Any> = HashMap()
        product2["product_id"] = "Pro2"
        product2["name"] = "Games2"
        product2["price"] = "2000.20"
        product2["quantity"] = 200
        product2["category"] = "RSCat2"

        // Creating a List of Maps
        val products: MutableList<Map<String, Any>> = ArrayList()
        products.add(product1)
        products.add(product2)
        return products
    }

    private fun getStandardAndCustomProperties(): RudderProperty =
        RudderProperty()
            .putValue("revenue", 100.0)
            .putValue("payment_method", "payment type 1")
            .putValue("coupon", "100% off coupon")
            .putValue("query", "Search query")
            .putValue("list_id", "item list id 1")
            .putValue("promotion_id", "promotion id 1")
            .putValue("creative", "creative name 1")
            .putValue("affiliation", "affiliation value 1")
            .putValue("share_via", "method 1")
            .putValue("currency", "INR")
            .putValue("shipping", "500")
            .putValue("tax", 15)
            .putValue("order_id", "transaction id 1")
            // Custom properties
            .putValue("key1", "value 1")
            .putValue("key2", 100)
            .putValue("key3", 200.25)
            .putValue("key4", true)

    private fun getStandardCustomAndProductAtRoot(): RudderProperty =
        getStandardAndCustomProperties()
            //Product properties at root
            .putValue("product_id", "RSPro1")
            .putValue("name", "RSMonopoly1")
            .putValue("price", 1000.2)
            .putValue("quantity", "100")
            .putValue("category", "RSCat1")

    // Custom events

    private fun customTrackEventWithoutProperties() {
        MainApplication.rudderClient.track(
            "Track event without properties"
        )
    }

    private fun customTrackEventWithProperties() {
        MainApplication.rudderClient.track(
            "Track event with properties",
            getCustomProperties()
        )
    }

    // Screen events

    private fun screenEventWithoutProperties() {
        MainApplication.rudderClient.screen(
            "Screen event without properties"
        )
    }

    private fun screenEventWithProperties() {
        MainApplication.rudderClient.screen(
            "Screen event with properties",
            getCustomProperties()
        )
    }

    private fun getCustomProperties(): RudderProperty =
        RudderProperty()
            // Custom properties
            .putValue("key1", "value 1")
            .putValue("key2", 100)
            .putValue("key3", 200.25)
            .putValue("key4", true)
}
