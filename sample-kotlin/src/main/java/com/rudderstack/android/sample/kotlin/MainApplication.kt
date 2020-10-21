package com.rudderstack.android.sample.kotlin

import android.app.Application
import com.rudderstack.android.integration.firebase.FirebaseIntegrationFactory
import com.rudderstack.android.sdk.core.RudderClient
import com.rudderstack.android.sdk.core.RudderConfig
import com.rudderstack.android.sdk.core.RudderLogger

class MainApplication : Application() {
    companion object {
        lateinit var rudderClient: RudderClient
    }

    override fun onCreate() {
        super.onCreate()
        rudderClient = RudderClient.getInstance(
            this,
            "1TSRSskqa15PG7F89tkwEbl5Td8",
            RudderConfig.Builder()
                .withDataPlaneUrl("https://9d56fa39376f.ngrok.io")
                .withControlPlaneUrl("https://9d56fa39376f.ngrok.io")
                .withLogLevel(RudderLogger.RudderLogLevel.DEBUG)
                .withFactory(FirebaseIntegrationFactory.FACTORY)
                .build()
        )
    }
}
