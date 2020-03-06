package com.userreport.android.surveyclient;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.audienceproject.userreport.DateConverter;
import com.audienceproject.userreport.UserReport;
import com.audienceproject.userreport.interfaces.SurveyLogger;
import com.audienceproject.userreport.models.Session;
import com.audienceproject.userreport.models.Settings;
import com.audienceproject.userreport.models.User;

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
    private SurveyLogger logger;
    private UserReport userReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Thread.setDefaultUncaughtExceptionHandler((thread, e) -> {
            Log.d("Unhandled Exception", e.toString());
            e.printStackTrace();
        });
        setContentView(R.layout.activity_main);
        initUserReport();
        initUi();
        this.onClearLogClick(null);
    }

    private void initUserReport() {
        userReport = App.get().getUserReport();
        this.logger = new SurveyLogger() {
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

            @Override
            public void message(String message) {
                Log.v("Message from UserReport", message);
            }
        };
        userReport.setLogger(logger);
        userReport.setSurveyFinishedCallback(() -> new Handler(Looper.getMainLooper()).post(() ->
                Toast.makeText(MainActivity.this, "Thanks for taking survey", Toast.LENGTH_LONG).show()));
        userReport.setOnErrorListener((httpStatusCode, message) -> {
            Toast toast = Toast.makeText(getApplicationContext(), "Oops... Server Error.", Toast.LENGTH_LONG);
            toast.show();
        });
    }

    private void initUi() {
        Switch testModeSwitch = findViewById(R.id.switch1);
        testModeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            userReport.setTestMode(isChecked);
        });

        this.networkLog = findViewById(R.id.txtServerLog);
        localQuarantineDaysTextView = findViewById(R.id.localQuarantineDays);
        inviteAfterNSecondsInAppTextView = findViewById(R.id.inviteAfterNSecondsInApp);
        inviteAfterTotalScreensViewedTextView = findViewById(R.id.inviteAfterTotalScreensViewed);
        sessionScreensViewTextView = findViewById(R.id.sessionScreensView);
        sessionNSecondsLengthTextView = findViewById(R.id.sessionNSecondsLength);
        setSettingsValues();
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
                        Settings settings = userReport.getMediaSettings();

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

    public void onClearLogClick(View view) {
        this.networkLog.setText("Network response/request logs:");
    }

    public void onSetEmailClick(View view) {
        EditText v = findViewById(R.id.email_text_input);
        String userEmail = v.getText().toString();
        if (!TextUtils.isEmpty(userEmail)) {
            User newUser = new User();
            newUser.setEmail(userEmail);
            userReport.updateUser(newUser);
        }
    }

    public void onStartActivityClick(View view) {
        Intent intent = new Intent(this, StubActivity.class);
        this.startActivity(intent);
    }

    public void trackScreen(View view) {
        userReport.trackScreenView();
    }

    public void trackSectionScreen(View view) {
        userReport.trackSectionScreenView(("a20ff83b-2b46-42a1-8969-5fb4ad6e4f17"));
    }

    public void onTryInviteClick(View view) {
        if (userReport.getSurvey() == null) {
            Toast.makeText(MainActivity.this, "Survey is not ready yet", Toast.LENGTH_LONG).show();
            return;
        }
        this.userReport.tryToInvite();
    }
}