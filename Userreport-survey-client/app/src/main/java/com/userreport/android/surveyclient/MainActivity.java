package com.userreport.android.surveyclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.audienceproject.userreport.DateConverter;
import com.audienceproject.userreport.UserIdentificationType;
import com.audienceproject.userreport.UserReport;
import com.audienceproject.userreport.UserReportBuilder;
import com.audienceproject.userreport.interfaces.ISurveyLogger;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.Settings;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    // UI
    private TextView networkLog;
    private TextView localQuarantineDaysTextView;
    private TextView inviteAfterNSecondsInAppTextView;
    private TextView inviteAfterTotalScreensViewedTextView;
    private TextView sessionScreensViewTextView;
    private TextView sessionNSecondsLengthTextView;

    // Survey
    private ISurveyLogger logger;
    private String userEmail;
    private UserReport userReport;

    private boolean testModeEnabled = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            Log.d("Unhandled Exception", e.toString());
            e.printStackTrace();
        });

        setContentView(R.layout.activity_main);

        Switch testModeSwitch = findViewById(R.id.switch1);
        testModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            testModeEnabled = isChecked;
            userReport.destroy();
            this.buildSurvey();
        });

        this.networkLog = findViewById(R.id.txtServerLog);
        localQuarantineDaysTextView = findViewById(R.id.localQuarantineDays);
        inviteAfterNSecondsInAppTextView = findViewById(R.id.inviteAfterNSecondsInApp);
        inviteAfterTotalScreensViewedTextView = findViewById(R.id.inviteAfterTotalScreensViewed);
        sessionScreensViewTextView = findViewById(R.id.sessionScreensView);
        sessionNSecondsLengthTextView = findViewById(R.id.sessionNSecondsLength);

        setSettingsValues();

        this.btnClearLog_Click(null);

        this.logger = new ISurveyLogger() {
            @Override
            public void networkActivity(String type, String data, String url) {
                Log.i(type, " URL: " + url + "\nData\n" + data);

                String t = networkLog.getText().toString();
                t = t + "\n\n" + type + " URL: " + url + "\nData\n" + data;
                networkLog.setText(t);
            }

            @Override
            public void error(String message, Exception ex) {
                Log.e("Error in UserReport", message, ex);
            }
        };

        this.buildSurvey();
    }

    private void setSettingsValues() {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new TimerTask() {
                    @Override
                    public void run() {
                        Session session = userReport.getSession();
                        Settings settings = userReport.getSettings();

                        if (settings == null) {
                            return;
                        }

                        localQuarantineDaysTextView.setText(getFormat(
                                "localQuarantine",
                                DateConverter.asString(DateConverter.getCurrentDate()),
                                DateConverter.asString(session.getLocalQuarantineDate())));

                        inviteAfterTotalScreensViewedTextView.setText(getFormat(
                                "inviteAfterTotalScreensViewed",
                                String.valueOf(session.getTotalScreenView()),
                                String.valueOf(settings.getInviteAfterTotalScreensViewed())));

                        inviteAfterNSecondsInAppTextView.setText(getFormat(
                                "inviteAfterNSeconds",
                                String.valueOf(session.getTotalSecondsInApp()),
                                String.valueOf(settings.getInviteAfterNSecondsInApp())));

                        sessionNSecondsLengthTextView.setText(getFormat(
                                "sessionNSecondsLength",
                                String.valueOf(session.getSessionSeconds()),
                                String.valueOf(settings.getSessionNSecondsLength())));

                        sessionScreensViewTextView.setText(getFormat(
                                "sessionScreensView",
                                String.valueOf(session.getScreenView()),
                                String.valueOf(settings.getSessionScreensView())));
                    }
                });
            }
        }, 0, 1000);
    }

    private String getFormat(String varName, String current, String rule) {
        return String.format("%s current / rule:\n %s / %s", varName, current, rule);
    }

    public void clearLocalQuarantineButtonClick(View view) {
        this.userReport.getSession().setLocalQuarantineDate(new Date());
    }

    public void btnClearLog_Click(View view) {
        this.networkLog.setText("Network response/request logs:");
    }

    public void btnSetEmail_Click(View view) {
        EditText v = findViewById(R.id.email_text_input);
        userEmail = v.getText().toString();

        userReport.destroy();
        this.buildSurvey();
    }

    public void onStartActivity_Click(View view) {
        Intent intent = new Intent(this, StubActivity.class);
        this.startActivity(intent);
    }

    public void trackScreen(View view) {
        userReport.trackScreenView();
    }

    public void trackSectionScreen(View view) {
        userReport.trackSectionScreenView(("a20ff83b-2b46-42a1-8969-5fb4ad6e4f17"));
    }

    public void btnTryInvite_Click(View view) {
        if (userReport.getSurvey() == null) {
            Toast.makeText(MainActivity.this, "Survey is not ready yet", Toast.LENGTH_LONG).show();
            return;
        }
        this.userReport.getSurvey().tryInvite();

    }

    private void buildSurvey() {
        // Optional
        Settings settings = new Settings();
        settings.setSessionNSecondsLength(7);
        settings.setSessionScreensView(3);
        settings.setLocalQuarantineDays(10);

        UserReportBuilder builder = new UserReportBuilder("audienceproject",
                "8aa7a61b-5c16-40c4-9b9e-c5ba641a160b")
                .setLogger(logger)
                .setSettings(settings)
                .setSurveyFinished(() -> new Handler(Looper.getMainLooper()).post(() ->
                        Toast.makeText(MainActivity.this, "Thanks for taking survey", Toast.LENGTH_LONG).show()));

        if (testModeEnabled) {
            builder.setSurveyTestMode();
        }

        if (userEmail != null) {
            builder.setUserInfo(UserIdentificationType.Email, userEmail);
        }

        this.userReport = builder.build(this);
        this.userReport.setOnError((httpStatusCode, message) -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Oops... Server Error.", Toast.LENGTH_LONG);
            toast.show();
        });
    }

    @Override
    public void onDestroy() {
        userReport.destroy();
        super.onDestroy();
    }
}