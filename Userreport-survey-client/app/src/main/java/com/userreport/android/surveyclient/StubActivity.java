package com.userreport.android.surveyclient;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.audienceproject.userreport.UserReport;

// used to emulate new activity starts in real app

public class StubActivity extends AppCompatActivity {
    private UserReport userReport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stub);
        userReport = App.get().getUserReport();
    }

    public void closeActivity(View view) {
        finish();
    }
}
