package ro.btanase.utils;


import static org.junit.Assert.assertEquals;

import org.apache.commons.io.FilenameUtils;
import org.junit.Before;
import org.junit.Test;

public class FileUtilsTest {

  @Before
  public void setUp() throws Exception {
  }

  @Test
  public void testSafeName(){
    String input = "abcd/er\\#|5?e%j*q:|\"<>";
    assertEquals("abcd#47#er#92###124#5#63#e#37#j#42#q#58##124##34##60##62#", FileUtils.safeName(input));
  }
  
  @Test
  public void testHtmlEncode(){
    char c = '/';
    assertEquals("#47#", FileUtils.unicodeEncode(c));
  }
  
  @Test
  public void testGetBaseName(){
    System.out.println(FilenameUtils.getBaseName("test.test.bla.ima"));
    
  }
}
