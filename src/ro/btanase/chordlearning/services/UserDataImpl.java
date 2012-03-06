package ro.btanase.chordlearning.services;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;

@Singleton
public class UserDataImpl implements UserData {
  private String userFolder;

  private static Logger log = Logger.getLogger(UserDataImpl.class);
  
  @Inject
  Provider<JdbcService> jdbcProvider;

  /* (non-Javadoc)
   * @see ro.btanase.chordlearning.services.UserData#getUserFolder()
   */
  @Override
  public String getUserFolder() {
    return userFolder;
  }

  /* (non-Javadoc)
   * @see ro.btanase.chordlearning.services.UserData#setUserFolder(java.lang.String)
   */
  @Override
  public void setUserFolder(String userFolder) {
    this.userFolder = userFolder;
    if (userFolder == null || userFolder.isEmpty()){
      throw new RuntimeException("userFolder null or empty!");
    }
    
    // create application.properties folder
    String homeDirectory = System.getProperty("user.home");
    
    String propertiesDirectoryPath = homeDirectory + File.separator + ".gcet";
    File appPropertiesDir = new File(propertiesDirectoryPath);
    try{
      FileUtils.forceMkdir(appPropertiesDir);
      
      log.debug("user home: " + homeDirectory);
      File appPropertiesFile = new File(propertiesDirectoryPath + File.separator + "application.properties");
      
      Properties prop = new Properties();
      prop.put("userFolder", userFolder);
      
      prop.store(new FileOutputStream(appPropertiesFile), "");
    }catch (IOException e) {
      log.fatal("Error creating properties file in user.home dir : ", e);
      throw new RuntimeException(e);
    }
    
    
    
  }

  /* (non-Javadoc)
   * @see ro.btanase.chordlearning.services.UserData#getMediaFolder()
   */
  @Override
  public String getMediaFolder(){
    if (userFolder == null){
      throw new IllegalStateException("userFolder is null. Initialize UserData class first");
    }
    return userFolder + File.separator + MEDIA_FOLDER;
  }

  @Override
  public boolean isUserDirectoryDefined() {
    
    // first check if application.properties is found in user.home directory
    String homeDirectory = System.getProperty("user.home");
    log.debug("user home: " + homeDirectory);
    File appPropertiesFile = new File(homeDirectory + File.separator + ".gcet" + File.separator + "application.properties");
    
    if (!appPropertiesFile.exists()){
      return false;
    }
    
    // read userFolder property
    Properties prop = new Properties();
    try {
      prop.load(new FileInputStream(appPropertiesFile));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    userFolder = prop.getProperty("userFolder");
    
    // check for media & config folders
    String mediaFolderPath = userFolder + File.separator + MEDIA_FOLDER;
    String configFolderPath = userFolder + File.separator + CONFIG_FOLDER;
    
    log.debug("mediaFolderPath: " + mediaFolderPath);
    log.debug("configFolderPath: " + configFolderPath);
    
    File mediaFolderFile = new File(mediaFolderPath);
    File configFolderFile = new File(configFolderPath);
    
    if ((!mediaFolderFile.exists() || !configFolderFile.exists()) ||
        (!mediaFolderFile.isDirectory() || !configFolderFile.isDirectory())){
      log.debug("media/config folder does not exists or are not directories");
      return false;
    }
    
    // check if the database is there
    String databaseScriptFileName = configFolderPath + File.separator + "chord.script"; 
    log.debug("Looking for database script file in location: " + databaseScriptFileName);
    if (!(new File(databaseScriptFileName).exists())){
      log.debug("Database doesn't appear to be there");
      return false;
    }

    // otherwise assume everything is fine
    return true;
  }

  @Override
  public String getConfigFolder() {
    if (userFolder == null){
      throw new IllegalStateException("userFolder is null. Initialize UserData class first");
    }
    
    return userFolder + File.separator + CONFIG_FOLDER;
  }

  @Override
  public void upgradeIfNecessary() {

    String homeDirectory = System.getProperty("user.home");

    File appPropertiesFile = new File(homeDirectory + File.separator
        + ".gcet" + File.separator + "application.properties");
    
    // read properties file and check if the DB version is up to date
    // if it's ok we will not query the schema further, we'll just assume is ok
    Properties prop = new Properties();
    try {
      FileInputStream fis = new FileInputStream(appPropertiesFile); 
      prop.load(fis);
      fis.close();
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    
    String dbVersion = prop.getProperty("dbVersion");
    
    if (dbVersion == null || (!dbVersion.equals("2"))){
      upgradeDbSchema();
      
      try{
        FileOutputStream fos = new FileOutputStream(appPropertiesFile);
        prop.put("dbVersion", "2");
        prop.store(fos, "");
        fos.close();
      }catch (IOException e) {
        new RuntimeException(e);
      }
      
    }

    
  }

  /**
   * do the actual SQL ALTER stuff
   */
  private void upgradeDbSchema() {
    try{
      Connection con = jdbcProvider.get().getCon();
      
      PreparedStatement stmt = con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='LESSON' AND COLUMN_NAME='L_TYPE'");
      
      ResultSet rs = stmt.executeQuery();
      
      // if no results, create the missing column
      if (!rs.next()){
        stmt = con.prepareStatement("ALTER TABLE LESSON ADD COLUMN L_TYPE VARCHAR(20) DEFAULT 'SINGLE'");
        stmt.executeUpdate();
      }
      
      stmt = con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='LESSON' AND COLUMN_NAME='L_NO_CHORDS'");
      rs = stmt.executeQuery();
      
      // if no results, create the missing column
      if (!rs.next()){
        stmt = con.prepareStatement("ALTER TABLE LESSON ADD COLUMN L_NO_CHORDS INTEGER");
        stmt.executeUpdate();
      }

      stmt = con.prepareStatement("SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE TABLE_NAME='LESSON' AND COLUMN_NAME='L_CHORD_DELAY'");
      rs = stmt.executeQuery();
      
      // if no results, create the missing column
      if (!rs.next()){
        stmt = con.prepareStatement("ALTER TABLE LESSON ADD COLUMN L_CHORD_DELAY INTEGER");
        stmt.executeUpdate();
      }
      
    }catch (SQLException e) {
      throw new RuntimeException(e);
    }
    
  }
  
  
  
}
