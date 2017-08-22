package com.jeo;

import android.app.Activity;
import android.os.Bundle;
import android.os.Process;
import android.util.Log;
import android.view.View;

import com.jeo.crashhandler.R;

public class MainActivity extends Activity implements View.OnClickListener {
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn1) {
            throw new NullPointerException("Test");
        } else if (v.getId() == R.id.btn2) {
            android.os.Process.killProcess(Process.myPid());
        } else if (v.getId() == R.id.btn3) {
            System.exit(0);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: ");
        super.onDestroy();
    }

    String TAG = "TAG";

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: ");
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: ");
        super.onStop();
    }
}
