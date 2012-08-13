package hr.jsteiner.common.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class WebUtil {
  
  private static int READ_TIMEOUT = 0;
  private static int CONNECT_TIMEOUT = 0;
  
  /**
   * Download page content to string using GET method
   * @param pageUrl to download from
   * @return page content
   */
  public static String downloadPageToString(String pageUrl) {
    String result = null;
    try {
        URL url = new URL(pageUrl);
        
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setConnectTimeout(CONNECT_TIMEOUT);
        connection.setReadTimeout(READ_TIMEOUT);
        connection.setRequestMethod("GET");
        connection.setDoOutput(false);
        connection.setDoInput(true);
        
        result = readInputOrErrorStream(connection);
        
    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return result;
  }
  
  /**
   * Download page content to string, but send data first with POST
   * @param pageUrl to download from
   * @param postParams POST parameters in format "param1=value1&param2=value2"
   * @return page content
   */
  public static String downloadPageToStringWithPostMethod(String pageUrl, 
      String postParams)
  {
    String pageContent = null;
    try {
      URL url = new URL(pageUrl);
      
      HttpURLConnection connection = (HttpURLConnection) url.openConnection();
      connection.setConnectTimeout(CONNECT_TIMEOUT);
      connection.setReadTimeout(READ_TIMEOUT);
      connection.setRequestMethod("POST");
      connection.setDoOutput(true);
      connection.setDoInput(true);
      
      /*
       *  important: send output stream (POST data) before getting the input stream
       */
      DataOutputStream wr = new DataOutputStream (
          connection.getOutputStream ());
      wr.write(postParams.getBytes("UTF-8"));
      wr.flush();
      wr.close();
      
      pageContent = readInputOrErrorStream(connection);

    } catch (MalformedURLException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
    
    return pageContent;
  }
  
  /**
   * Tries to read content from connection's input stream, if fails returns the error stream.
   * @param connection
   * @return
   */
  private static String readInputOrErrorStream(HttpURLConnection connection) {
    InputStream inputStream = null;
    try { 
      inputStream = connection.getInputStream(); 
    }
    catch (IOException e) {
      inputStream = connection.getErrorStream();
    }
    
    String result = null;
    
    if (inputStream != null) {
      result = readInputStream(inputStream);
    }

    return result;
  }
  
  /**
   * Reads from input stream and returns a String with the content
   * @param istream to read from
   * @return read content
   * @throws IllegalArgumentException if istream is null
   */
  private static String readInputStream(InputStream istream) {
    if (istream == null) {
      throw new IllegalArgumentException("istream must not be null!");
    }
    StringBuffer buffer = new StringBuffer();
    
    BufferedReader in = new BufferedReader(new InputStreamReader(istream));
    String line;
    try {
      while( (line = in.readLine()) != null) {
        buffer.append(line + "\n");
      }
    } 
    catch (IOException e) {
      e.printStackTrace();
    }
    finally {
      try {
        in.close();
      }
      catch(IOException e) {      
        e.printStackTrace();
      }
    }
    
    return buffer.toString();
  }
}
