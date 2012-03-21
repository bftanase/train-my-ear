package ro.btanase.tests.mappers;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ro.btanase.chordlearning.data.ChordMapper;
import ro.btanase.chordlearning.data.LessonMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.LessonType;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.tests.ChordLearningModuleTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class LessonMapperTest {

  private static Injector injector;
  private static Logger log = Logger.getLogger(LessonMapperTest.class);
  private static SessionFactory sessionFactory;
  
  
  @BeforeClass
  public static void setUpOnce() throws Exception {
    injector = Guice.createInjector(new ChordLearningModuleTest());
    sessionFactory = injector.getInstance(SessionFactory.class);
  }  

  @Before
  public void startup() throws IOException{

    SqlSession session = sessionFactory.get().openSession();

    String chord_script_filename = "ro/btanase/tests/scripts/chords.sql";
    String lessons_script_filename = "ro/btanase/tests/scripts/lessons.sql";

    Reader chord_script_reader = Resources.getResourceAsReader(chord_script_filename);
    Reader lesson_script_reader = Resources.getResourceAsReader(lessons_script_filename);
    
    ScriptRunner scriptRunner = new ScriptRunner(session.getConnection());
    scriptRunner.runScript(chord_script_reader); 
    scriptRunner.runScript(lesson_script_reader); 
    session.commit(true);

  }
  
  
  
  @Test
  public void testSelectAll(){
    SqlSession session = sessionFactory.get().openSession();
    
    LessonMapper mapper = session.getMapper(LessonMapper.class);
    
    List<Lesson> lessonList = mapper.selectAll();
    
    assertEquals(32, lessonList.size());
    
    Lesson lesson = lessonList.get(0);
    
    assertEquals(19, lesson.getId());
    assertEquals("Stage 1 SSR", lesson.getLessonName());
    assertEquals(5, lesson.getNoQuestions());
    assertEquals(LessonType.SINGLE, lesson.getType());
    
    
    session.close();
  }
  
  @Test
  public void testInsert(){
    SqlSession session = sessionFactory.get().openSession();
    LessonMapper mapper = session.getMapper(LessonMapper.class);
    
    Lesson lesson = new Lesson();
    
    lesson.setLessonName("Test Lesson");
    lesson.setType(LessonType.PROGRESSION);
    lesson.setChordDelay(400);
    lesson.setNoChordsInSequence(4);
    lesson.setNoQuestions(6);
    
    mapper.insert(lesson);
    session.commit();
    int insertId = mapper.lastInsertId();
    
    assertEquals(54, insertId);
    
    
    lesson = mapper.selectById(insertId);
    
    assertEquals("Test Lesson", lesson.getLessonName());
    assertEquals(LessonType.PROGRESSION, lesson.getType());
    assertEquals(400, lesson.getChordDelay());
    assertEquals(4, lesson.getNoChordsInSequence());
    assertEquals(6, lesson.getNoQuestions());
    
    session.close();
  }

  @Test
  public void testUpdate(){
    SqlSession session = sessionFactory.get().openSession();
    LessonMapper mapper = session.getMapper(LessonMapper.class);

    Lesson lesson = mapper.selectById(19);
    
    lesson.setLessonName("Test Lesson");
    lesson.setType(LessonType.PROGRESSION);
    lesson.setChordDelay(400);
    lesson.setNoChordsInSequence(4);
    lesson.setNoQuestions(6);
    
    mapper.update(lesson);
    session.commit();
    
    lesson = mapper.selectById(19);

    assertEquals("Test Lesson", lesson.getLessonName());
    assertEquals(LessonType.PROGRESSION, lesson.getType());
    assertEquals(400, lesson.getChordDelay());
    assertEquals(4, lesson.getNoChordsInSequence());
    assertEquals(6, lesson.getNoQuestions());
    
    
    session.close();
  }
  
  
  @Test
  public void testDelete(){
    SqlSession session = sessionFactory.get().openSession();
    LessonMapper mapper = session.getMapper(LessonMapper.class);

    Lesson lesson = new Lesson();
    
    lesson.setLessonName("Test Lesson");
    lesson.setType(LessonType.PROGRESSION);
    lesson.setChordDelay(400);
    lesson.setNoChordsInSequence(4);
    lesson.setNoQuestions(6);
    
    
    mapper.insert(lesson);
    int lastId = mapper.lastInsertId();
    session.commit();
    
    lesson = mapper.selectById(lastId);
    
    assertNotNull(lesson);
    
    mapper.delete(lastId);
    session.commit();
    
    lesson = mapper.selectById(lastId);
    
    assertNull(lesson);
    
    session.close();
  }
  
  @Test
  public void testSelectLessonChords(){
    SqlSession session = sessionFactory.get().openSession();
    LessonMapper mapper = session.getMapper(LessonMapper.class);

    List<Chord> chordList = mapper.selectChordsByLessonId(19);
    
    assertEquals(3, chordList.size());
    Chord chord = chordList.get(0);
    
    assertEquals("A", chord.getChordName());
    assertEquals("A.ima", chord.getFileName());

    session.close();
  }

  @Test
  public void testDeleteLessonChords(){
    SqlSession session = sessionFactory.get().openSession();
    LessonMapper mapper = session.getMapper(LessonMapper.class);
    
    List<Chord> chordList = mapper.selectChordsByLessonId(19);
    
    assertEquals(3, chordList.size());

    mapper.deleteLessonChords(19);

    session.commit();
    
    chordList = mapper.selectChordsByLessonId(19);
    
    assertEquals(0, chordList.size());
    session.close();
  }
  
  @Test
  public void testInsertChordToLesson(){
    SqlSession session = sessionFactory.get().openSession();
    LessonMapper mapper = session.getMapper(LessonMapper.class);
    ChordMapper chordMapper = session.getMapper(ChordMapper.class);
    
    Chord chord1 = chordMapper.selectById(31); 
    Chord chord2 = chordMapper.selectById(32); 
    Chord chord3 = chordMapper.selectById(33);
    
    Lesson lesson = new Lesson();
    lesson.setLessonName("Test");
    lesson.setType(LessonType.SINGLE);
    lesson.setNoQuestions(5);
    
    mapper.insert(lesson);
    
    int lessonId = mapper.lastInsertId();
    
    Map<String, Object> param = new HashMap<String, Object>();
    param.put("chordId", chord1.getId());
    param.put("lessonId", lessonId);
  
    mapper.insertChordToLesson(param);
    
    param.put("chordId", chord2.getId());
    param.put("lessonId", lessonId);
    mapper.insertChordToLesson(param);

    param.put("chordId", chord3.getId());
    param.put("lessonId", lessonId);
    mapper.insertChordToLesson(param);
    
    lesson = mapper.selectById(lessonId);
    
    assertEquals("Test", lesson.getLessonName());
    
    List<Chord> chordList = mapper.selectChordsByLessonId(lessonId);
    
    assertEquals(3, chordList.size());
    
  }
  
  @AfterClass
  public static void cleanup(){
    SqlSession session = sessionFactory.get().openSession();
    try{
      session.getConnection().createStatement().execute("SHUTDOWN");
    }catch (Exception e) {
      log.error(e);
    }finally{
      session.close();
    }
  }
}
