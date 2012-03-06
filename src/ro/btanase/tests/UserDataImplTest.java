package ro.btanase.tests;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import ro.btanase.chordlearning.services.UserData;
import ro.btanase.chordlearning.services.UserDataImpl;

public class UserDataImplTest {
  private UserData userData;

  @Before
  public void setUp() throws Exception {
    userData = new UserDataImpl();
  }

  @Test
  public void testGetUserFolder() {
    fail("Not yet implemented");
  }

  @Test
  public void testSetUserFolder() {
    fail("Not yet implemented");
  }

  @Test
  public void testGetMediaFolder() {
    fail("Not yet implemented");
  }

  @Test
  public void testIsUserDirectoryDefined() {
    assertTrue(userData.isUserDirectoryDefined());
  }
  
  @Test
  public void directoryCreateTest() throws IOException{
    FileUtils.copyDirectory(new File("c:/test"), new File("c:/test2"));
  }

}
