package ro.btanase.chordlearning.exceptions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.Thread.UncaughtExceptionHandler;

import javax.swing.JOptionPane;

import org.apache.log4j.Logger;

public class MyUncaughtExceptionHandler implements UncaughtExceptionHandler{
  private static Logger log = Logger.getLogger(MyUncaughtExceptionHandler.class);

  @Override
  public void uncaughtException(Thread t, Throwable e) {
    log.fatal("Uncaught exception!", e);
    displayErrorMessage(e.toString());
  }
  
  public void handle(Throwable throwable) {
    log.fatal("Uncaught exception!", throwable);
    displayErrorMessage(throwable.toString());
  }

  private void displayErrorMessage(String message){
    try {
      String userHomePath = System.getProperty("user.home") + File.separator + ".gcet";
      System.getProperties().store(new FileOutputStream(userHomePath + File.separator + "systeminfo.log"), "system information");
    } catch (IOException e) {
      log.fatal("Error saving system info", e);
    }
    JOptionPane.showMessageDialog(null, message + "\n" + 
        "Whoops, fatal error; cannot continue. " +
        "Please send 'application.log' & 'systeminfo.log' to the author so it can be fixed", "Fatal Error", JOptionPane.ERROR_MESSAGE );
    System.exit(-1);
  }
  
  public static void registerExceptionHandler() {
    Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler());
    System.setProperty("sun.awt.exception.handler", MyUncaughtExceptionHandler.class.getName());
  }  

}
