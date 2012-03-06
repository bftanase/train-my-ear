package ro.btanase.tests.mappers;


import static org.junit.Assert.*;

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
import ro.btanase.chordlearning.data.ScoreMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.ExerciseResult;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.LessonType;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.chordlearning.domain.wrappers.ChordAccuracyWrapper;
import ro.btanase.chordlearning.domain.wrappers.LessonEvolutionWrapper;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.tests.ChordLearningModuleTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class ScoreMapperTest {

  private static Injector injector;
  private static Logger log = Logger.getLogger(ScoreMapperTest.class);
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
    String score_script_filename = "ro/btanase/tests/scripts/scores.sql";

    Reader chord_script_reader = Resources.getResourceAsReader(chord_script_filename);
    Reader lesson_script_reader = Resources.getResourceAsReader(lessons_script_filename);
    Reader score_script_reader = Resources.getResourceAsReader(score_script_filename);
    
    ScriptRunner scriptRunner = new ScriptRunner(session.getConnection());
    scriptRunner.runScript(chord_script_reader); 
    scriptRunner.runScript(lesson_script_reader); 
    scriptRunner.runScript(score_script_reader); 
    session.commit(true);

  }
  
  
  
  @Test
  public void testSelectOne(){
    SqlSession session = sessionFactory.get().openSession();
    ScoreMapper mapper = session.getMapper(ScoreMapper.class);
    
    Score score = mapper.selectScoreById(26);
    
    assertNotNull(score);
    assertNotNull(score.getDate());
    assertEquals(26, score.getId());
    
    Lesson lesson = score.getLesson();
    
    assertNotNull(lesson);
    assertEquals(34, lesson.getId());
    assertEquals("Stage 5 CQR (D/Dm/D7)",lesson.getLessonName());
    assertEquals(7,lesson.getNoQuestions());
    assertEquals(LessonType.SINGLE, lesson.getType());
    
    List<ExerciseResult> resultList = score.getLessonResults();
    
    assertEquals(7, resultList.size());
    
    ExerciseResult er = resultList.get(0);
    
    assertNotNull(er);
    assertEquals(162, er.getId());
    
    assertTrue(er.isCorrect());
    
    Chord chord = er.getChord();
    
    assertNotNull(chord);
    assertEquals(29, er.getChord().getId());
    assertEquals("Dm", er.getChord().getChordName());
    assertEquals("Dm.ima", er.getChord().getFileName());
    
    session.close();
  }
  
  @Test
  public void testSelectChordAccuracy(){
    SqlSession session = sessionFactory.get().openSession();
    ScoreMapper mapper = session.getMapper(ScoreMapper.class);
    
    List<ChordAccuracyWrapper> cawList = mapper.selectChordAccuracy();
    
    assertEquals(8, cawList.size());
    
    ChordAccuracyWrapper caw = cawList.get(0);
    
    assertEquals("Dm", caw.getChordName());
    assertEquals(5, caw.getCorrectCount());
    assertEquals(5, caw.getTotalCount());
    
  }

  @Test
  public void testSelectLessonAccuracyEvolution(){
    SqlSession session = sessionFactory.get().openSession();
    ScoreMapper mapper = session.getMapper(ScoreMapper.class);
    
    List<LessonEvolutionWrapper> lewList = mapper.selectLessonAccuracyEvolution(19);
    
    assertEquals(5, lewList.size());
    
    LessonEvolutionWrapper lew = lewList.get(0);
    
    assertEquals("Stage 1 SSR", lew.getLessonName());
    assertNotNull(lew.getScoreDate());
    assertEquals(4, lew.getCorrectCount());
    assertEquals(5, lew.getTotalCount());
    
  }
  
  @Test
  public void testInsertScore(){
    SqlSession session = sessionFactory.get().openSession();
    ScoreMapper mapper = session.getMapper(ScoreMapper.class);
    LessonMapper lessonMapper = session.getMapper(LessonMapper.class);
    ChordMapper chordMapper = session.getMapper(ChordMapper.class);
    
    
    Lesson lesson = lessonMapper.selectById(34);
    Score score = new Score();
    score.setLesson(lesson);
    
    mapper.insertScore(score);

    assertEquals(35, score.getId());
    
    Chord chord1 = chordMapper.selectById(26); 
    Chord chord2 = chordMapper.selectById(27); 
    Chord chord3 = chordMapper.selectById(28);

    Map<String, Object> param1 = new HashMap<String, Object>();
    Map<String, Object> param2 = new HashMap<String, Object>();
    Map<String, Object> param3 = new HashMap<String, Object>();
    
    param1.put("chordId", chord1.getId());
    param1.put("scoreId", score.getId());
    param1.put("correct", true);

    param2.put("chordId", chord2.getId());
    param2.put("scoreId", score.getId());
    param2.put("correct", false);

    param3.put("chordId", chord3.getId());
    param3.put("scoreId", score.getId());
    param3.put("correct", true);
    
    
    mapper.insertExerciseResult(param1);
    mapper.insertExerciseResult(param2);
    mapper.insertExerciseResult(param3);
    
    session.commit();
    
    score = mapper.selectScoreById(35);
    
    lesson = score.getLesson();
    assertEquals("Stage 5 CQR (D/Dm/D7)", lesson.getLessonName());

    List<ExerciseResult> erList = score.getLessonResults();
    
    assertEquals(3, erList.size());
    ExerciseResult er = erList.get(0);
    
    assertEquals("D", er.getChord().getChordName());
    assertEquals(true, er.isCorrect());
    
    session.close();
  }
  
  @Test
  public void testDeleteAllScores(){
    SqlSession session = sessionFactory.get().openSession();
    ScoreMapper mapper = session.getMapper(ScoreMapper.class);

    List<ChordAccuracyWrapper> listCaw = mapper.selectChordAccuracy();
    
    assertTrue(listCaw.size() > 0);
    
    mapper.deleteAllScores();
    
    listCaw = mapper.selectChordAccuracy();
    
    assertTrue(listCaw.size() == 0);
    
    session.commit();
    session.close();
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
