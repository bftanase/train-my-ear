package ro.btanase.tests.mappers;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.Reader;
import java.util.List;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ro.btanase.chordlearning.data.ChordMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.tests.ChordDaoTest;
import ro.btanase.tests.ChordLearningModuleTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ChordMapperTest {

  private static Injector injector;
  private static Logger log = Logger.getLogger(ChordDaoTest.class);
  private static SessionFactory sessionFactory;
  
  
  @BeforeClass
  public static void setUpOnce() throws Exception {
    injector = Guice.createInjector(new ChordLearningModuleTest());
    sessionFactory = injector.getInstance(SessionFactory.class);
  }  

  @Before
  public void startup() throws IOException{

    SqlSession session = sessionFactory.get().openSession();

//    String user_authentication_tbl = "data/scripts/w_user_authentication.sql";
    String wishlist_tbl = "ro/btanase/tests/scripts/chords.sql";
//    String user_tbl = "data/scripts/w_user.sql";
//    String product_tbl = "data/scripts/w_product.sql";
//    Reader ua_reader = Resources.getResourceAsReader(user_authentication_tbl);
//    Reader utbl_reader = Resources.getResourceAsReader(user_tbl);
    Reader wtbl_reader = Resources.getResourceAsReader(wishlist_tbl);
//    Reader product_tbl_reader = Resources.getResourceAsReader(product_tbl);
    
    ScriptRunner scriptRunner = new ScriptRunner(session.getConnection());
//    scriptRunner.runScript(utbl_reader); 
    scriptRunner.runScript(wtbl_reader); 
//    scriptRunner.runScript(product_tbl_reader); 
//    scriptRunner.runScript(ua_reader);
    session.close();
  }
  
  
  
  @Test
  public void testSelectAll(){
    SqlSession session = sessionFactory.get().openSession();
    
    ChordMapper mapper = session.getMapper(ChordMapper.class);
    
    List<Chord> chordList = mapper.selectAll();
    
    assertEquals(25, chordList.size());
    
    Chord chord = chordList.get(0);
    
    assertEquals(25, chord.getId());
    assertEquals("A", chord.getChordName());
    assertEquals("A.ima", chord.getFileName());
    
    session.close();
  }
  
  @Test
  public void testInsert(){
    SqlSession session = sessionFactory.get().openSession();
    ChordMapper mapper = session.getMapper(ChordMapper.class);
    
    Chord chord = new Chord("Test", "test.ima");
    
    mapper.insert(chord);
    
    int insertId = mapper.lastInsertId();
    
    assertEquals(50, insertId);
    session.commit();
    chord = mapper.selectById(insertId);
    
    assertEquals("Test", chord.getChordName());
    assertEquals("test.ima", chord.getFileName());
    
    session.close();
  }

  @Test
  public void testUpdate(){
    SqlSession session = sessionFactory.get().openSession();
    ChordMapper mapper = session.getMapper(ChordMapper.class);

    Chord chord = mapper.selectById(33);
    
    chord.setChordName("updatedChord");
    chord.setFileName("updatedFilename");
    
    mapper.update(chord);
    session.commit();
    
    chord = mapper.selectById(33);
    
    assertEquals("updatedChord", chord.getChordName());
    assertEquals("updatedFilename", chord.getFileName());
    
    session.close();
  }
  
  
  @Test
  public void testDelete(){
    SqlSession session = sessionFactory.get().openSession();
    ChordMapper mapper = session.getMapper(ChordMapper.class);

    Chord chord = new Chord("test", "test.ima");
    mapper.insert(chord);
    int lastId = mapper.lastInsertId();
    session.commit();
    
    mapper.delete(lastId);
    session.commit();
    
    chord = mapper.selectById(lastId);
    
    assertNull(chord);
    
    session.close();
  }
}
