package ro.btanase.chordlearning.services;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;

import ro.btanase.tests.JdbcServiceImplTest;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class JdbcServiceImpl implements JdbcService {

  
  private String user = "SA";
  private String password = "";
  private Connection con;
  private static Logger log = Logger.getLogger(JdbcServiceImplTest.class);
  
  private UserData userData;

  @Inject
  public JdbcServiceImpl(UserData userData) {
    this.userData = userData;
    String url = "jdbc:hsqldb:file:" + userData.getConfigFolder() + File.separator + "chord" 
                +";create=false;shutdown=true;hsqldb.write_delay=false;hsqldb.sqllog=2;hsqldb.applog=3;";  
    try {
      con = DriverManager.getConnection(url, user, password);
      con.createStatement().execute("SET DATABASE EVENT LOG SQL LEVEL 3");
      log.debug("autocommit: " + con.getAutoCommit());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }

  /*
   * (non-Javadoc)
   * 
   * @see ro.btanase.chordlearning.services.JdbcService#getCon()
   */
  @Override
  public Connection getCon() {
    return con;
  }

}
