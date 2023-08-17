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
            BuildConfig.WRITE_KEY,
            RudderConfig.Builder()
                .withDataPlaneUrl(BuildConfig.DATA_PLANE_URL)
                .withControlPlaneUrl(BuildConfig.CONTROL_PLANE_URL)
                .withRecordScreenViews(false)
                .withTrackLifecycleEvents(false)
                .withLogLevel(RudderLogger.RudderLogLevel.VERBOSE)
                .withFactory(FirebaseIntegrationFactory.FACTORY)
                .build()
        )
    }
}
