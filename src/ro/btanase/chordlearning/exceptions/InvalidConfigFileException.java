package ro.btanase.chordlearning.exceptions;

public class InvalidConfigFileException extends RuntimeException {

  public InvalidConfigFileException() {
    super();
  }

  public InvalidConfigFileException(String message, Throwable cause) {
    super(message, cause);
  }

  public InvalidConfigFileException(String message) {
    super(message);
  }

  public InvalidConfigFileException(Throwable cause) {
    super(cause);
  }

}
