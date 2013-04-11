package ro.btanase.chordlearning.exceptions;

public class LessonValidationException extends RuntimeException{

  public LessonValidationException() {
    super();
  }

  public LessonValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public LessonValidationException(String message) {
    super(message);
  }

  public LessonValidationException(Throwable cause) {
    super(cause);
  }

}
