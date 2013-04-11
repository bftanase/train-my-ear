package ro.btanase.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.google.gson.Gson;

public class GsonTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void tesGson() {
    String[] arr = {"Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5"};
    
    Gson gson = new Gson();
    String json = gson.toJson(arr);
    
    String[] fromJsonArr = gson.fromJson(json, String[].class);
    
    assertEquals(5, fromJsonArr.length);
    assertEquals("Slot 1", fromJsonArr[0]);
    assertEquals("Slot 2", fromJsonArr[1]);
    assertEquals("Slot 3", fromJsonArr[2]);
    
    
  }

}
