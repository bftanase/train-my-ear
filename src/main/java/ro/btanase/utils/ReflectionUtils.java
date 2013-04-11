package ro.btanase.utils;

import java.lang.reflect.Method;

import org.jfree.util.Log;

import ro.btanase.chordlearning.domain.Chord;

public class ReflectionUtils {

  public static String invokeChordGetFileName(Chord chord, int slot){
    String result = null;
    String methodName;
   
    if (slot == 0){
      methodName = "getFileName";
    } else {
      methodName = "getFileName" + (slot+1);
    }
    try{
      
    
    Method method = chord.getClass().getMethod(methodName, null);
    
    result = (String) method.invoke(chord, null);
    } catch (Exception e){
      Log.error("Reflection failure", e);
      throw new RuntimeException(e);
    }
    
    return result;
  }
  
}
