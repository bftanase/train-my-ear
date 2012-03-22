package ro.btanase.tests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ro.btanase.chordlearning.dao.ChordDao;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.services.JdbcService;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.chordlearning.services.UserDataImpl;
import ca.odell.glazedlists.EventList;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ChordDaoTest {

  private static Injector injector;
  private static Logger log = Logger.getLogger(ChordDaoTest.class);
  
  private ChordDao chordDao;
  private UserData userData;
  
  @BeforeClass
  public static void setUpOnce() throws Exception {
    injector = Guice.createInjector(new ChordLearningModuleTest());
  }

  @Before
  public void setUp(){
    userData = injector.getInstance(UserDataImpl.class);
    chordDao = injector.getInstance(ChordDao.class);
    JdbcService jdbcService = injector.getInstance(JdbcService.class);
    
    try{
      Statement st = jdbcService.getCon().createStatement();
      st.execute("TRUNCATE SCHEMA PUBLIC AND COMMIT NO CHECK");
      st.execute("TRUNCATE TABLE chord RESTART IDENTITY AND COMMIT NO CHECK");
      chordDao.fetchChords();
    }catch (Exception e) {
      throw new RuntimeException(e);
    }
  }
  

  @Test
  public void testAddChord() {
    Chord chord1 = new Chord("A", "A.ima");
    Chord chord2 = new Chord("E", "E.ima");
    
    chordDao.addChord(chord1);
    chordDao.addChord(chord2);

    EventList<Chord> chordList = chordDao.getAllChords();
    
//    EventList<Chord> chordList = chordDao.fetchChords();
//    
    chord1 = chordList.get(0);
    chord2 = chordList.get(1);

    assertEquals(0, chord1.getId());
    assertEquals("A", chord1.getChordName());
    assertEquals("A.ima", chord1.getFileName());

    assertEquals(1, chord2.getId());
    assertEquals("E", chord2.getChordName());
    assertEquals("E.ima", chord2.getFileName());
    
  }
  
  @Test
  public void testFetchChords() {
    
  }

  @Test
  public void testGetAllChords() {
    this.testAddChord();
    assertTrue(chordDao.getAllChords().size() > 0);
  }


  @Test
  public void testDeleteChord() throws IOException {
    userData.setUserFolder("." + File.separator + "test");
    String fileName = userData.getMediaFolder() + File.separator + "E.ima";
    
    File file = new File(fileName);
    log.debug("fileName: " + fileName);
    if (!file.exists()){
      file.createNewFile();
    }
    
    testAddChord();
    Chord toDeleteChord = chordDao.getAllChords().get(1);
    
    chordDao.deleteChord(toDeleteChord);
   
    log.debug("chordList size: " + chordDao.getAllChords().size());
    
    assertTrue(chordDao.getAllChords().size() == 1);
    assertTrue(chordDao.fetchChords().size() == 1);
  }

  @Test
  public void testUpdateChord() {
    testAddChord();
    EventList<Chord> chordList = chordDao.getAllChords();
    Chord chord = chordList.get(0);
    
    chord.setChordName("Em");
    chord.setFileName("Em.ima");
    
    chordDao.updateChord(chord);
    
    // test getAllChords
    EventList<Chord> newChordList = chordDao.getAllChords();
    Chord updatedChord = newChordList.get(0);
    
    assertEquals("Em", updatedChord.getChordName());
    assertEquals("Em.ima", updatedChord.getFileName());

    // test getFetchChords
    newChordList = chordDao.fetchChords();
    updatedChord = newChordList.get(0);
    
    assertEquals("Em", updatedChord.getChordName());
    assertEquals("Em.ima", updatedChord.getFileName());
    
  }
  
  
  @AfterClass
  public static void tearDownClass(){
    JdbcService service = injector.getInstance(JdbcService.class);
    
    try {
      service.getCon().close();
    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
    
  }
  
  

}
