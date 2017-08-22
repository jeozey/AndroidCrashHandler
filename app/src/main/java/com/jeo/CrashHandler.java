package com.jeo;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Looper;
import android.os.Process;
import android.util.Log;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Jeozey on 2017/8/22.
 */

public class CrashHandler implements Thread.UncaughtExceptionHandler {
    private static final String TAG = CrashHandler.class.getSimpleName();

    public interface ErrorCallBack {
        void errorCallBack(String errContent);
    }

    private static CrashHandler instance;
    private ErrorCallBack errorCallBack;
    private Context mContext;
    private StringBuilder errorSb;
    //系统默认的UncaughtException处理类
    private Thread.UncaughtExceptionHandler mDefaultHandler;
    private List<Activity> activityList = new ArrayList<>();//activity stack

    /**
     * 保证只有一个CrashHandler实例
     */
    private CrashHandler(ErrorCallBack errorCallBack) {
        this.errorCallBack = errorCallBack;
    }

    /**
     * 获取CrashHandler实例 ,单例模式
     */
    public static CrashHandler getInstance(ErrorCallBack errorCallBack) {
        if (instance == null)
            instance = new CrashHandler(errorCallBack);
        return instance;
    }

    /**
     * 初始化
     */
    public CrashHandler init(Application application) {
        mContext = application.getApplicationContext();
        //获取系统默认的UncaughtException处理器
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        //设置该CrashHandler为程序的默认处理器
        Thread.setDefaultUncaughtExceptionHandler(this);
        application.registerActivityLifecycleCallbacks(new SimpleActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                activityList.add(activity);
            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                activityList.remove(activity);
            }
        });
        return this;
    }


    /**
     * 当UncaughtException发生时会转入该函数来处理
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "uncaughtException: " + Thread.currentThread().getName());

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        if (errorCallBack != null) {
            errorSb = new StringBuilder();
            errorSb.append(writer.toString());
            for (StackTraceElement se : ex.getStackTrace()) {
                errorSb.append("\n");
                errorSb.append(se);
            }

            new Thread() {
                @Override
                public void run() {
                    Looper.prepare();
                    new AlertDialog.Builder(getCurrentActivity()).
                            setMessage("Sorry,App has crashed,restart now?").
                            setPositiveButton("YES", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    errorCallBack.errorCallBack(errorSb.toString());
                                }
                            }).create().show();
                    Looper.loop();
                }
            }.start();

        }

    }

    public Activity getCurrentActivity() {
        return activityList.size() > 0 ? activityList.get(activityList.size() - 1) : null;
    }

    private void finishAllActivity() {
        for (Activity activity : activityList) {
            if (!activity.isFinishing()) {
                Log.e(TAG, "finish: " + activity.getLocalClassName());
                activity.finish();
            }
        }
    }

    private void startFirstActivity() {
//        Intent intent = new Intent("android.intent.action.MAIN");
//        intent.setPackage(mContext.getPackageName());
//        intent.addCategory(Intent.CATEGORY_DEFAULT);
        Intent intent = new Intent(mContext, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent restartIntent = PendingIntent.getActivity(mContext,
                0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager mgr = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 50, restartIntent);
    }

    public void restart() {
        finishAllActivity();
        startFirstActivity();
        Process.killProcess(Process.myPid());
        System.exit(0);
    }
}
