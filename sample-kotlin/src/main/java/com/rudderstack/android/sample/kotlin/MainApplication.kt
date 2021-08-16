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
            "1pTxG1Tqxr7FCrqIy7j0p28AENV",
            RudderConfig.Builder()
                .withDataPlaneUrl("https://896a5e9ecfd6.ngrok.io")
                .withRecordScreenViews(false)
                .withTrackLifecycleEvents(false)
                .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                .withFactory(FirebaseIntegrationFactory.FACTORY)
                .build()
        )
    }
}
