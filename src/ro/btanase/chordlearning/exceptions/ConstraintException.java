package ro.btanase.chordlearning.exceptions;

public class ConstraintException extends RuntimeException{

  public ConstraintException() {
    super();
  }

  public ConstraintException(String message, Throwable cause) {
    super(message, cause);
  }

  public ConstraintException(String message) {
    super(message);
  }

  public ConstraintException(Throwable cause) {
    super(cause);
  }

}
