package hr.jsteiner.common.domain;

public class Exceptions {
  
  public static class FileReadException extends Exception {

    private static final long serialVersionUID = 875848902253139548L;

    public FileReadException(String detailMessage) {
      super(detailMessage);
    }
  }
  
  public static class FileSaveException extends Exception {

    private static final long serialVersionUID = 1L;

    public FileSaveException(String detailMessage) {
      super(detailMessage);
    }
  }
  
  public static class WebPageException extends Exception {

    private static final long serialVersionUID = -8883838364881309067L;

    public WebPageException(String detailMessage) {
      super(detailMessage);
    }
  }
  
  public static class HttpResponseCodeException extends Exception {

    private static final long serialVersionUID = -5061989025044241489L;
    
    public HttpResponseCodeException(String detailMessage) {
      super(detailMessage);
    }
    
  }
}
