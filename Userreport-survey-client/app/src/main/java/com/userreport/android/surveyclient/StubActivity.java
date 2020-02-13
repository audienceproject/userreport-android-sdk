package com.userreport.android.surveyclient;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

// used to emulate new activity starts in real app

public class StubActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stub);
    }

    public void closeActivity(View view){
        finish();
    }
}
