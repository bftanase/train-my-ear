package ro.btanase.chordlearning.exceptions;

public class ChordValidationException extends RuntimeException{

  public ChordValidationException() {
    super();
  }

  public ChordValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public ChordValidationException(String message) {
    super(message);
  }

  public ChordValidationException(Throwable cause) {
    super(cause);
  }

}
