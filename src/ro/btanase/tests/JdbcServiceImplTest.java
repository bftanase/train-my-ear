package ro.btanase.tests;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.jfree.util.Log;

import ro.btanase.chordlearning.services.JdbcService;

import com.google.inject.Singleton;
/**
 * JDBC Wrapper used in unit testing
 * @author b.tanase
 *
 */
@Singleton
public class JdbcServiceImplTest implements JdbcService {

  private String url = "jdbc:hsqldb:file:./test/config/chord;create=false;shutdown=true;hsqldb.write_delay=false";
//  private String url = "jdbc:hsqldb:file:d:/java/projects/eclipse-projects/ChordLearning/test/config/chord";
  private String user="SA";
  private String password="";
  private Connection con;
  private static Logger log = Logger.getLogger(JdbcServiceImplTest.class);
  
  public JdbcServiceImplTest() {
    try {
      con = DriverManager.getConnection(url, user, password);
      log.debug("autocommit: " + con.getAutoCommit());
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }
  
  

  /* (non-Javadoc)
   * @see ro.btanase.chordlearning.services.JdbcService#getCon()
   */
  @Override
  public Connection getCon() {
    return con;
  }
  
  

}
