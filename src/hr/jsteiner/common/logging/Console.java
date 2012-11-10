package hr.jsteiner.common.logging;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

public class Console {
  private static final String TAG = Console.class.getCanonicalName();
  
  private List<String> mLog = new ArrayList<String>();
  
  private final int MAX_LOG_ENTRIES = 20;
  
  private OnLogEventCallback mOnLogEventCallback = null;
  
  private static Console mConsoleInstance = null;
  
  private static Context mAppContext = null;
  
  private Handler mHandler = new Handler();
  
  public static void setAppContext(Context appContext) {
    mAppContext = appContext;
  }
  
  public static Console getInstance() {
    if (mConsoleInstance != null) {
      return mConsoleInstance;
    }
    mConsoleInstance = new Console();
    return mConsoleInstance;
  }
  
  private Console () {
  }
  
  public String getFullLog() {
    StringBuffer log = new StringBuffer();
    for (String logEntry : mLog) {
      log.append(logEntry + "\n");
    }
    return log.toString();
  }
  
  public void log(String message) {
    if (mLog.size() > MAX_LOG_ENTRIES) {
      mLog.remove(0);
    }
    
    Date date = new Date(System.currentTimeMillis());
    SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

    mLog.add(sdf.format(date) + " " + message);
    
    if(mOnLogEventCallback != null) {
      mOnLogEventCallback.onLogEvent(message);
    }
  }
  
  public void log(int stringResourceId) {
    if (mAppContext == null) {
      Log.e(TAG + "#log(int)", "mAppContext is null. Tried to log "  + stringResourceId);
      return;
    }
    log(mAppContext.getString(stringResourceId));
  }
  
  public void trimLog() {
    while (mLog.size() > MAX_LOG_ENTRIES) {
      mLog.remove(0);
    }
  }
  
  public void toast(int stringResourceId) {
    if (mAppContext == null) {
      Log.e(TAG + "#log(int)", "mAppContext is null. Tried to toast "  + stringResourceId);
      return;
    }
    toast(mAppContext.getString(stringResourceId));
  }
  
  
  public void toast(final String message) {
    log(message);
    
    if (mAppContext == null) {
      Log.e(TAG + "#log(int)", "mAppContext is null. Tried to toast "  + message);
      return;
    }
    
    /* 
     * the Handler is here to prevent RuntimeException:
     * can't create a handler inside a thread that has not called Looper.prepare()
     */
    mHandler.post(
        new Runnable() {
          @Override public void run() {
            Toast.makeText(mAppContext, message, Toast.LENGTH_SHORT).show();
          }
        });
  }
  
  public void clearLog() {
    mLog = new ArrayList<String>();
  }
  
  public void setOnLogEventCallback(OnLogEventCallback callback){
    mOnLogEventCallback = callback;
  }
  
  public interface OnLogEventCallback {
    public void onLogEvent(String logMessage);
  }
  
}
