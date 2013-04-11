package ro.btanase.chordlearning.services;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SessionFactoryImpl implements SessionFactory {

  private SqlSessionFactory sessionFactory;
  
  private UserData userData;

  @Inject
  public SessionFactoryImpl(UserData userData) {
    this.userData = userData;
  }



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
     String url = "jdbc:hsqldb:file:"+userData.getConfigFolder() + File.separator 
                 + "chord;create=false;shutdown=true;hsqldb.write_delay=false;"; 
     prop.put("url", url);
     prop.put("user", "SA");
     prop.put("pass", "");
     
      sessionFactory = new SqlSessionFactoryBuilder().build(reader, prop);      
    }
    return sessionFactory;
  }

}
