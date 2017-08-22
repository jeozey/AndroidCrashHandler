# AndroidCrashHandler
Remind and restart  friendly when android app crash

# How to use
In your Application:</br>
```java
crashHalder = CrashHandler.getInstance(new CrashHandler.ErrorCallBack() {
  @Override
  public void errorCallBack(String errContent) {
      Log.e(TAG, "errorCallBack errContent: " + errContent);
      new Thread() {
          @Override
          public void run() {
              Looper.prepare();
              new AlertDialog.Builder(crashHalder.getCurrentActivity()).
                      setMessage("Sorry,App has crashed,restart now?").
                      setPositiveButton("YES", new DialogInterface.OnClickListener() {
                          @Override
                          public void onClick(DialogInterface dialog, int which) {
                              //TODO
                              //handler the errcontent,upload to server or other
                              crashHalder.restart(FirstActivity.class);
                          }
                      }).create().show();
              Looper.loop();
          }
      }.start();
  }
}).init(this);
```
