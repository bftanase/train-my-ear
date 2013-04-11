package ro.btanase.btvalidators;






public class BTValidator {
  
  private String text;
  private boolean valid;
  String errorMessage="";
//  private static Validator instance;
  
  
  // Validator.string(Object obj).isNumber().notEmpty().validate()
  
  public static BTValidator input(String text){
    
    BTValidator instance = new BTValidator();
    instance.valid = true; // start validation as true
    if (text != null){
      instance.text = text.trim();
    }
    
    return instance;
  }

  public static BTValidator input(Double number){
    
    BTValidator instance = new BTValidator();
    instance.valid = true; // start validation as true
    if (number != null){
      instance.text = String.valueOf(number);
    }
    
    return instance;
  }
  
  public BTValidator required(){
    // skip if validation already failed
    if (this.valid == false){
      return this;
    }
    
    if (this.text==null || (this.text.isEmpty())){
      this.valid = false;
      errorMessage = "Value is required!";
    }
    
    return this;
  }
  
  public BTValidator numeric(){
    if (this.valid == false){
      return this;
    }
    
    try{
      Double.parseDouble(this.text);
    }catch (NumberFormatException ex) {
      this.valid = false;
      errorMessage = "Value " + text + " is not a number!";
    }
    
    return this;
  }
  
  public BTValidator integer(){
    if (this.valid == false){
      return this;
    }

    try{
      Integer.parseInt(this.text);
    }catch (NumberFormatException e) {
      this.valid = false;
      errorMessage = "Value " + text + " is not a valid number!";
    }
    
    return this;
  }

  public BTValidator gtZero(){
    if (this.valid == false){
      return this;
    }
    
    double num = 0.0;
    
    try{
      num = Double.parseDouble(this.text);
    }catch (NumberFormatException ex) {
      this.valid = false;
      errorMessage = "Value " + text + " is not a number!";
      return this;
    }
    
    if (num <= 0.0){
      errorMessage = "Value must be greater than 0!";
      this.valid = false;
      return this;
    }
    
    return this;
  }

  public BTValidator rangeInclusive(double lower, double higher){
    if (this.valid == false){
      return this;
    }
    
    double num = 0;

    try{
      num = Double.parseDouble(this.text);
    }catch (NumberFormatException ex) {
      this.valid = false;
      errorMessage = "Value " + text + " is not a number!";
      return this;
    }
    
    if (!(num >= lower && num <= higher)){
      this.valid = false;
      errorMessage = "The number must be between " + lower + " and " + higher + "!";
      return this;
    }
    
    return this;
    
  }
  
  
  public boolean validate(){
    return this.valid;
  }
  
  public void validateWithException(){
    if (this.valid == false){
      throw new BTValidationException(errorMessage);
    }
  }
  
  public BTValidator checkRegex(String pattern){
    // skip if validation already failed
    if (this.valid == false){
      return this;
    }
    
    if (this.text==null || (!this.text.matches(pattern))){
      this.valid = false;
      errorMessage = "Invalid String format";
    }
    
    return this;
  }
  
  
}
