package hr.jsteiner.common.util;

import hr.jsteiner.common.domain.Exceptions.FileReadException;
import hr.jsteiner.common.domain.Exceptions.FileSaveException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;

import android.util.Log;

public class FileUtil {
  
  public static final String TAG = FileUtil.class.getCanonicalName();
 
  
  /**
   * Reads the contents of a text file to a {@link StringBuffer}
   * @param path to the file
   * @return {@link String} object which contains the file contents.
   */
  public static String readTextFile(File file) throws FileReadException {
    StringBuffer stringBuffer = new StringBuffer();
    BufferedReader bufferedReader = null;
    try {
      FileReader fileReader = new FileReader(file);
      bufferedReader = new BufferedReader(fileReader);
      
      String line;
      while((line = bufferedReader.readLine()) != null) {
        stringBuffer.append(line);
        stringBuffer.append("\n");
      }
      
      fileReader.close();
    
    }
    catch (FileNotFoundException e) {
      Log.e(TAG, e.toString());
      throw new FileReadException(e.toString());
    }
    catch (IOException e) {
      Log.e(TAG, e.toString());
      throw new FileReadException(e.toString());
    }
    finally {
      try {
        if (bufferedReader != null) bufferedReader.close();
      }
      catch (IOException e) {
        Log.e(TAG, e.toString());
        throw new FileReadException(e.toString());
      }
      
    }
    
    return stringBuffer.toString();
  }
  
  /**
   * Save text file to mobile storage
   * @param file to save
   */
  public static void saveTextFile(File file, String text) throws FileSaveException {
    Log.d(TAG, "saveTextFile(File) method called!");Log.i(TAG, "saveTextFile(File) method called!");
    
    PrintStream out = null;
    try {
      out = new PrintStream(new FileOutputStream(file));
      out.print(text);
      Log.i(TAG, "File saved: " + file.getPath());
    }
    catch(Exception e) { 
      Log.e(TAG, e.toString());
      throw new FileSaveException(e.toString());
    }
    finally {
      if (out != null) out.close();
    }
  }
}
