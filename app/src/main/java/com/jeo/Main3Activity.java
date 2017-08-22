package com.jeo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.jeo.crashhandler.R;

public class Main3Activity extends Activity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);

    }

    @Override
    public void onClick(View v) {
        Intent intent =new Intent(this,Main2Activity.class);
        startActivity(intent);
    }
}
