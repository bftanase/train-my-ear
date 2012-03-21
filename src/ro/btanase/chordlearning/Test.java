package ro.btanase.chordlearning;

import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

import org.apache.ibatis.datasource.pooled.PooledDataSourceFactory;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class Test {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {
    String resource = "ro/btanase/chordlearning/data/SqlMapConfig.xml";
    Reader reader = Resources.getResourceAsReader(resource);
    
    SqlSessionFactoryBuilder sessionFactoryBuilder = new SqlSessionFactoryBuilder();
    Properties prop = new Properties();
    prop.put("driver", "com.mysql.jdbc.Driver");
    prop.put("url", "jdbc:mysql://prestashop.bmarket.ro/agheorg_presta");
    
    SqlSessionFactory sessionfactory = sessionFactoryBuilder.build(reader, prop);
    
    

//    sessionFactory.getConfiguration().getEnvironment().getDataSource().getConnection().se
    
  }

}
