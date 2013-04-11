package ro.btanase.chordlearning.exceptions;

public class PlaybackException extends RuntimeException{

  public PlaybackException() {
    super();
  }

  public PlaybackException(String message, Throwable cause) {
    super(message, cause);
  }

  public PlaybackException(String message) {
    super(message);
  }

  public PlaybackException(Throwable cause) {
    super(cause);
  }

}
