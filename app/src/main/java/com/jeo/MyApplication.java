package com.jeo;

import android.app.AlertDialog;
import android.app.Application;
import android.content.DialogInterface;
import android.os.Looper;
import android.util.Log;

/**
 * Created by Jeozey on 2017-08-21.
 */

public class MyApplication extends Application {
    private static final String TAG = MyApplication.class.getSimpleName();
    private CrashHandler crashHalder;

    @Override
    public void onCreate() {
        super.onCreate();
        crashHalder = CrashHandler.getInstance(new CrashHandler.ErrorCallBack() {
            @Override
            public void errorCallBack(String errContent) {
                Log.e(TAG, "errorCallBack errContent: " + errContent);
//                new Thread() {
//                    @Override
//                    public void run() {
//                        Looper.prepare();
//                        new AlertDialog.Builder(crashHalder.getCurrentActivity()).
//                                setMessage("Sorry,App has crashed,restart now?").
//                                setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        //TODO
//                                        //handler the errcontent,upload to server or other
//                                        crashHalder.restart();
//                                    }
//                                }).create().show();
//                        Looper.loop();
//                    }
//                }.start();
                crashHalder.restart();
            }
        }).init(this);
    }
}
