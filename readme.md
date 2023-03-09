[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.audienceproject/userreport/badge.svg)](https://search.maven.org/search?q=g:com.audienceproject%20a:userreport)

# UserReport Android SDK

Brings UserReport capabilities to native Android applications – Surveys and Audience Measurement

## Requirements

- For screen/section tracking: Android SDK 19 (Android 4.4) or higher
- For surveying: Android SDK 24 or higher

## Installation
1. Add audienceprojct as a dependency to your project. See examples for Maven and Gradle below. Please use the latest version of SDK. 1.0.0.0 version which is specified here is provided just as an example. 

##### Maven
```
<dependency>
  <groupId>com.audienceproject</groupId>
  <artifactId>userreport</artifactId>
  <version>[1.0.0.0,)</version> 
</dependency>
```

##### Gradle
```
dependencies {
  ...
  implementation "com.audienceproject:userreport:1.0.0.0" //though you should use the latest version
}
```

2. Add audienceproject into the proguard-rules.pro so it won't be obfuscated (*app/proguard-rules.pro*)
```
-keep class com.audienceproject.userreport.models.** { *; }
```
3. SDK relies on **AAID**. In order to get it app needs **AdMob**.


###### Admob
Add your [AdMob App ID](https://support.google.com/admob/answer/7356431) to your app's `AndroidManifest.xml` file by adding a `<meta-data>` tag with name `com.google.android.gms.ads.APPLICATION_ID`, as shown below.

You can find your **App ID** in the AdMob UI. For `android:value` insert your own AdMob App ID in quotes, as shown below.

```
<manifest>
    <application>
        <!-- Sample AdMob App ID: ca-app-pub-3940256099942544~3347511713 -->
        <meta-data
            android:name="com.google.android.gms.ads.APPLICATION_ID"
            android:value="ca-app-pub-xxxxxxxxxxxxxxxx~yyyyyyyyyy"/>
    </application>
</manifest>
```

## Usage

### Configuration
Configure the UserReport instance on Application startup, and make sure you store the instance of UserReport. See example below:

```Java
import com.audienceproject.userreport.*;

public class App extends Application {
    private UserReport userReport;

    @Override
    public void onCreate() {
        ...
        userReport = UserReport.configure(this, PUBLISHER_ID, MEDIA_ID);
    }
    public UserReport getUserReport() {
        return userReport;
    }
}

```

### Surveying
At any point in time you can update user information or settings, see example below:
```Java
public void onLoggedIn(View view) {
  userReport = App.get().getUserReport();
  User newUser = new User();
  newUser.setEmail(userEmail);
  userReport.updateUser(newUser);
}
```

### Screen tracking
There are two types of tracking:
  - Screen view tracking (`UserReport.trackScreenView`)
  - Section view tracking (`UserReport.trackSectionScreenView`)

#### Screen view tracking
If a media (website) has one single topic, it can be tracked by using `UserReport.trackScreenView`.

```Java
protected void onCreate(Bundle savedInstanceState) {
  userReport = App.get().getUserReport();

  //track screen view
  userReport.trackScreenView();
}
```

#### Section view tracking
If a website has different sections, for instance *Health*, *World news* and *Local news*, then it should be tracked using both `UserReport.trackScreenView()` and `UserReport.trackSectionScreenView(sectionId)`. The `sectionId` for a particular section can be found on the Media Setting page in UserReport.

```Java
protected void onCreate(Bundle savedInstanceState) {
  userReport = App.get().getUserReport();

  //track section view (use both functions)
  userReport.trackScreenView();
  userReport.trackSectionScreenView(sectionId);
}
```

#### Automatic tracking
By default, automatic tracking is disabled.

Also, if the `UserReport.trackSectionScreenView` or `UserReport.trackScreenView` methods are invoked by your code, automatic tracking should normally not be used. However, you can enable automatic tracking by following the example below.

When using automatic activity tracking, you might want to disable it for specific screens i.e. your Settings or Login screens. This is also included in the example below.

```Java
      userReport = App.get().getUserReport();

      //enable automatic tracking (use 'false' if you don't need it)
      userReport.setAutoTracking(true);

      //do not auto-track certain activities
      ArrayList<String> skipActivities =new ArrayList<>();
      skipActivities.add(MainActivity.class.getName());
      // list of other activity names can be passed here
      userReport.skipTrackingFor(skipActivities);
```


### Anonymous tracking mode
If anonymous tracking is enabled: all requests will be fired to the do-not-track domain and AAID will never be sent.
To enable anonymous mode you can use following method:

```Java
userReport.setAnonymousTracking(true);
```

## Build

```
cd ./Userreport-survey
gradle assemblerelease -Poutput=../.artifacts/userreport-survey-android-sdk -PbuildVersion=0
```
