package ro.btanase.btvalidators;


public class BTValidationException extends RuntimeException{

  /**
   * 
   */
  private static final long serialVersionUID = 5877658484951455546L;

  public BTValidationException() {
    super();
  }

  public BTValidationException(String message, Throwable cause) {
    super(message, cause);
  }

  public BTValidationException(String message) {
    super(message);
  }

  public BTValidationException(Throwable cause) {
    super(cause);
  }

  

}
