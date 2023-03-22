
# What is RudderStack?

[RudderStack](https://rudderstack.com/) is a **customer data pipeline tool** for collecting, routing and processing data from your websites, apps, cloud tools, and data warehouse.

With RudderStack, you can build customer data pipelines that connect your whole customer data stack and then make them smarter by triggering enrichment and activation in customer tools based on analysis in your data warehouse. Its easy-to-use SDKs and event source integrations, Cloud Extract integrations, transformations, and expansive library of destination and warehouse integrations makes building customer data pipelines for both event streaming and cloud-to-warehouse ELT simple.

| Try **RudderStack Cloud Free** - a no time limit, no credit card required, completely free tier of [RudderStack Cloud](https://resources.rudderstack.com/rudderstack-cloud). Click [here](https://app.rudderlabs.com/signup?type=freetrial) to start building a smarter customer data pipeline today, with RudderStack Cloud Free. |
|:------|

Questions? Please join our [Slack channel](https://resources.rudderstack.com/join-rudderstack-slack) or read about us on [Product Hunt](https://www.producthunt.com/posts/rudderstack).

## Integrating Firebase with the RudderStack Android SDK

1. Add [Firebase](https://firebase.google.com) as a destination in the [RudderStack dashboard](https://app.rudderstack.com/).

2. Open your project level ```build.gradle``` file, and add the following lines of code:
```
buildscript {
    repositories {
        mavenCentral()
    }
}
allprojects {
    repositories {
        mavenCentral()
    }
}
```
3. Add the following dependencies under ```dependencies```
```
implementation 'com.rudderstack.android.sdk:core:[1.0,2.0)'
implementation 'com.rudderstack.android.integration:firebase:3.0.0'
```

## Initializing ```RudderClient```

```
val rudderClient: RudderClient = RudderClient.getInstance(
    this,
    <WRITE_KEY>,
    RudderConfig.Builder()
        .withDataPlaneUrl(<DATA_PLANE_URL>)
        .withFactory(FirebaseIntegrationFactory.FACTORY)
        .build()
)
```

## Overriding `firebase-analytics` SDK Versions 
In order to override the `firebase-analytics` dependency with a different version, you can use the code snippet provided below in the `build.gradle` file located at the root level of your project:

> Warning: Using your own SDK versions is generally not recommended as it can lead to breaking changes in your application. Proceed with caution.

```
project.ext {
    set("rudderstack", [
            "rudder-android-sdk": "1.6.0",  // It'll override the rudder android sdk
            "firebase-bom": "31.2.3"        // It'll override the firebase android sdk
    ])
}
```

## Sending Events

Follow the steps from our [RudderStack Android SDK](https://github.com/rudderlabs/rudder-sdk-android#send-events) repo.

## Contact Us

If you come across any issues while configuring or using this integration, please feel free to start a conversation on our [Slack](https://resources.rudderstack.com/join-rudderstack-slack) channel. We will be happy to help you.
