# UserReport Android SDK

Brings UserReport capabilities in native Android application – Surveys and Audience Measurement

## Requirements

- Android SDK 19 (Android 4.4) or higher

Note: UserReport survey functionality is supported from Android SDK 24 or higher

## Usage

Maven:
```
<dependency>
  <groupId>com.audienceproject</groupId>
  <artifactId>userreport</artifactId>
  <version>[0.0.1.0,)</version>
</dependency>
```

or Gradle:
```
dependencies {
  ...
  compile "com.audienceproject:userreport:0.0.1.+"
}
```

### Screen tracking
To manually measure the screen view, use the method `userReport.trackScreenView();`.
Or `userReport.trackSectionScreenView(<SECTION_ID>);` for measuring section screen view.

```Java
import com.audienceproject.userreport.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ...

        UserReport userReport = new UserReportBuilder(<SAK_ID>, <MEDIA_ID>).build(this);
        userReport.trackScreenView();
    }
}

```


## Configuration

You can find out `ACCOUNT_ID`, `MEDIA_ID` parameters by following steps:

**If you do not have already created media:**

1. Go to https://app.userreport.com
2. Go to Media list page
3. Create a media with type Android App
4. On the last step you can find out needed parameters

**If you have already created media:**

1. Go to https://app.userreport.com
2. Go to Media list page
3. Open media in Edit mode
4. Go to UserReport SDK installation section

## Build

```
cd ./Userreport-survey
gradle assemblerelease -Poutput=../.artifacts/userreport-survey-android-sdk -PbuildVersion=0
```
