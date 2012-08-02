package hr.jsteiner.common.logging;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.widget.Toast;

public class Console {
  private static List<String> mLog = new ArrayList<String>();
  
  private static final int MAX_LOG_ENTRIES = 20;
  
  private static OnLogEventCallback mOnLogEventCallback = null;
  
  public static String getFullLog() {
    StringBuffer log = new StringBuffer();
    for (String logEntry : mLog) {
      log.append(logEntry + "\n");
    }
    return log.toString();
  }
  
  public static void log(String message) {
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
  
  public static void trimLog() {
    while (mLog.size() > MAX_LOG_ENTRIES) {
      mLog.remove(0);
    }
  }
  
  public static void toast(Context context, String message) {
    log(message);
    Toast toast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
    toast.show();
  }
  
  public static void clearLog() {
    mLog = new ArrayList<String>();
  }
  
  public static void setOnLogEventCallback(OnLogEventCallback callback){
    mOnLogEventCallback = callback;
  }
  
  public interface OnLogEventCallback {
    public void onLogEvent(String logMessage);
  }
  
}
