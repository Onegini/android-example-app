# Android Example App

The Android Example App is using the Onegini Android SDK to perform secure authentication and resource calls. Please have a look at the
[App developer quickstart](https://docs.onegini.com/app-developer-quickstart.html) if you want more information about how to get setup with this 
example app.

## Resolving dependencies

Before you can compile the application it must be able to resolve it's dependencies. The Onegini Android SDK is one of those dependencies. We have an 
Artifactory repository that distributes the required dependencies. Make sure that you have access to the Onegini Artifactory repository (https://repo.onegini.com).
If you don't have access, no problem just go to the [App developer quickstart](https://docs.onegini.com/app-developer-quickstart.html#step1) and perform the 
first step. Access to Artifactory is required to let Gradle download the Onegini Android SDK library.

When you have access you have to make sure that your Artifactory username and password are set in the `gradle.properties` file in your Gradle user home 
(e.g. ~/.gradle):

Example contents of the `gradle.properties` file in you Gradle user home:
```
artifactory_user=<username>
artifactory_password=<password>
```

See the documentation below for instructions on setting Gradle properties:
[https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_and_system_properties](https://docs.gradle.org/current/userguide/build_environment.html#sec:gradle_properties_and_system_properties)

## Receiving push messages (optional)

In order to use the Firebase Cloud Messaging you need to replace sample `app/google-services.json` file with one downloaded from 
the [Firebase Console](https://console.firebase.google.com/). If you need more information on how to set it up, please take a look at 
[Google Documentation](https://firebase.google.com/docs/android/setup#manually_add_firebase). 

If you ignore this step, you won't be able to receive push messages from the Token Server.
