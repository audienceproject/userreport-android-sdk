# UserReport Android SDK

Brings UserReport capabilities to native Android application – Surveys and Audience Measurement

## Requirements

- Android SDK 19 (Android 4.4) or higher

Note: UserReport survey functionality is supported from Android SDK 24 or higher

## Installation

### Maven
```
<dependency>
  <groupId>com.audienceproject</groupId>
  <artifactId>userreport</artifactId>
  <version>[1.0.0.0,)</version>
</dependency>
```

### Gradle
```
dependencies {
  ...
  implementation "com.audienceproject:userreport:1.0.0.0" //or any recent version
}
```
## Usage

### Configuration
Configure the UserReport instance on Application startup, and make sure you store the instance of UserReport. See example below

```Java
import com.audienceproject.userreport.*;

public class App extends Application {
    private UserReport userReport;

    @Override
    public void onCreate() {
        ...
        userReport =
                UserReport.configure(this,
                        <SAK_ID>,
                        <MEDIA_ID>);
    }
    public UserReport getUserReport() {
        return userReport;
    }
}

```
Than at any point of time you can update user information or settings, see example below
```Java
public void onLoggedIn(View view) {
  userReport = App.get().getUserReport();
  User newUser = new User();
  newUser.setEmail(userEmail);
  userReport.updateUser(newUser);
```

### Screen tracking
There are two types of tracking *ScreenView* and *SectionScreenView*. One should be used in favor of another depending on the media.   

#### Screen View
If a media (website) has one topic it can be tracked by using `UserReport.trackScreenView`.

#### Section Screen View
If a website has different sections, for instance, media has *Health*, *World news*, *Local news* and it should be tracked differenlty `UserReport.trackSectionScreenView(sectionId)` method should be used instead.  

#### Manual invocation
If `UserReport.trackSectionScreenView` or `UserReport.trackScreenView()` methods invoked by your code, automatic tracking should not be used. 


```Java
protected void onCreate(Bundle savedInstanceState) {
  userReport = App.get().getUserReport();
  userReport.trackScreenView();
  //or section 
  userReport.trackSectionScreenView(sectionId);
}

```

#### Automatic tracking
By default automatic tracking is disabled.  If you enable activity tracking you might want to disable it for specific screens i.e. Settings or Login. Example below.

```Java
      userReport = App.get().getUserReport();

      //false if you don't need it
      userReport.setAutoTracking(true);
      
      ArrayList<String> skipActivities =new ArrayList<>();
      skipActivities.add(MainActivity.class.getName());
      // list of activity names can be passed here
      userReport.skipTrackingFor(skipActivities);
```


## Build

```
cd ./Userreport-survey
gradle assemblerelease -Poutput=../.artifacts/userreport-survey-android-sdk -PbuildVersion=0
```
