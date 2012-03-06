package ro.btanase.chordlearning.services;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.inject.Singleton;

@Singleton
public class SessionFactoryImplTest implements SessionFactory {

  private SqlSessionFactory sessionFactory;
  
  @Override
  public SqlSessionFactory get() {
    if (sessionFactory == null){
      String resource = "ro/btanase/chordlearning/data/SqlMapConfig.xml";
      Reader reader;
     try {
       reader = Resources.getResourceAsReader(resource);
     } catch (IOException e) {
       throw new RuntimeException(e);
     }
     Properties prop = new Properties();
     prop.put("url", "jdbc:hsqldb:file:./test/config/chord;create=false;shutdown=true;hsqldb.write_delay=false;hsqldb.sqllog=2;hsqldb.applog=3;");
     prop.put("user", "SA");
     prop.put("pass", "");
     
      sessionFactory = new SqlSessionFactoryBuilder().build(reader, prop);      
    }
    
    return sessionFactory;
  }

}
