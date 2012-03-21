package ro.btanase.chordlearning.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.LessonType;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.services.JdbcService;
import ro.btanase.chordlearning.services.UserData;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

@Singleton
public class LessonDaoDbImpl implements LessonDao {
  
  private static Logger log = Logger.getLogger(LessonDaoDbImpl.class.getName());

  private EventList<Lesson> lessonList = new BasicEventList<Lesson>();
  private JdbcService jdbcService;

  @Inject
  private UserData userData;

  @Inject
  public LessonDaoDbImpl(JdbcService jdbcService) {
    this.jdbcService = jdbcService;
  }

  @Override
  public EventList<Lesson> fetchLessons() {
    lessonList.clear();
    try {
      Connection conn = jdbcService.getCon();
      PreparedStatement statement = conn.prepareStatement("SELECT * FROM lesson");
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        Lesson lesson = new Lesson();
        lesson.setLessonName(rs.getString("L_NAME"));
        lesson.setId(rs.getInt("L_ID"));
        lesson.setNoQuestions(rs.getInt("L_NO_QUESTIONS"));

        if (rs.getString("L_TYPE").equals("SINGLE")){
          lesson.setType(LessonType.SINGLE);
        }else if (rs.getString("L_TYPE").equals("PROGRESSION")){
          lesson.setType(LessonType.PROGRESSION);
        }
        
        lesson.setNoChordsInSequence(rs.getInt("L_NO_CHORDS"));
        lesson.setChordDelay(rs.getInt("L_CHORD_DELAY"));
        
        // eager loading for child elements
        lesson.setChordSequence(getChordsFromLesson(lesson.getId()));
        lessonList.add(lesson);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return lessonList;
  }

  @Override
  public EventList<Lesson> getAll() {
    if (lessonList.isEmpty()) {
      fetchLessons();
    }
    return lessonList;
  }

  @Override
  public void addLesson(Lesson lesson) {
    try {
      
      // disable autocommit mode; we'll use transactions for this method
      jdbcService.getCon().setAutoCommit(false);

      // add current lesson
      PreparedStatement statement = jdbcService.getCon().prepareStatement(
          "INSERT INTO lesson (L_NAME, L_NO_QUESTIONS, L_TYPE, L_NO_CHORDS, L_CHORD_DELAY) VALUES (?, ?, ?, ?, ?) ", Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, lesson.getLessonName());
      statement.setInt(2, lesson.getNoQuestions());

      if (lesson.getType().equals(LessonType.SINGLE)){
        statement.setString(3, "SINGLE");
      }else if (lesson.getType().equals(LessonType.PROGRESSION)){
        statement.setString(3, "PROGRESSION");
      }

      statement.setInt(4, lesson.getNoChordsInSequence());
      statement.setInt(5, lesson.getChordDelay());
      
      statement.executeUpdate();

      ResultSet resultSet = statement.getGeneratedKeys();

      resultSet.next();

      int insertedId = resultSet.getInt(1);

      // Log.debug(message)"gen key" + statement.getGeneratedKeys().getInt(0);
      // statement.execute("SHUTDOWN");
      statement.close();

      lesson.setId(insertedId);

      EventList<Chord> chordList = lesson.getChordSequence();
      
      for (Chord chord : chordList) {
        PreparedStatement lessonChordStatement = 
            jdbcService.getCon().prepareStatement("INSERT INTO LESSON_CHORD (C_ID, L_ID) VALUES ?, ?");
        lessonChordStatement.setInt(1, chord.getId());
        lessonChordStatement.setInt(2, lesson.getId());
        lessonChordStatement.executeUpdate();
        
      }
      
      jdbcService.getCon().commit();
      fetchLessons();
      jdbcService.getCon().setAutoCommit(true);

    } catch (Exception e) {
      try{
        jdbcService.getCon().rollback();
      }catch (SQLException ex) {
        log.error("Cannot rollback transaction", ex);
      }
      throw new RuntimeException(e);
    }

  }

  @Override
  public void deleteLesson(Lesson lesson) {
    try{
      PreparedStatement stmnt = jdbcService.getCon().prepareStatement("DELETE FROM lesson WHERE l_id = ?");
      stmnt.setInt(1, lesson.getId());
      stmnt.executeUpdate();
      fetchLessons();
    }catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void updateLesson(Lesson lesson) {
    try {
      
      // disable autocommit mode; we'll use transactions for this method
      jdbcService.getCon().setAutoCommit(false);

      // add current lesson
      PreparedStatement statement = jdbcService.getCon().prepareStatement(
          "UPDATE LESSON SET L_NAME = ?, L_NO_QUESTIONS = ?, L_TYPE = ?, L_NO_CHORDS = ?, L_CHORD_DELAY = ? WHERE L_ID = ? ");

      statement.setString(1, lesson.getLessonName());
      statement.setInt(2, lesson.getNoQuestions());

      if (lesson.getType().equals(LessonType.SINGLE)){
        statement.setString(3, "SINGLE");
      }else if (lesson.getType().equals(LessonType.PROGRESSION)){
        statement.setString(3, "PROGRESSION");
      }

      statement.setInt(4, lesson.getNoChordsInSequence());
      statement.setInt(5, lesson.getChordDelay());

      statement.setInt(6, lesson.getId());      
      
      statement.executeUpdate();

      EventList<Chord> chordList = lesson.getChordSequence();

      // clear existing relations
      PreparedStatement removeChildrenStamement = jdbcService.getCon().prepareStatement("DELETE FROM LESSON_CHORD WHERE L_ID = ?");
      removeChildrenStamement.setInt(1, lesson.getId());
      removeChildrenStamement.executeUpdate();
      
      // add new selected chords
      
      for (Chord chord : chordList) {
        PreparedStatement lessonChordStatement = 
            jdbcService.getCon().prepareStatement("INSERT INTO LESSON_CHORD (C_ID, L_ID) VALUES ?, ?");
        lessonChordStatement.setInt(1, chord.getId());
        lessonChordStatement.setInt(2, lesson.getId());
        lessonChordStatement.executeUpdate();
        
      }
      
      jdbcService.getCon().commit();
      fetchLessons();
      jdbcService.getCon().setAutoCommit(true);

    } catch (Exception e) {
      try{
        jdbcService.getCon().rollback();
      }catch (SQLException ex) {
        log.error("Cannot rollback transaction", ex);
      }
      throw new RuntimeException(e);
    }
  }

  @Override
  public Lesson getLesson(String lessonName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EventList<Chord> getChordsFromLesson(int lessonId) {
    EventList<Chord> chordList = new BasicEventList<Chord>();
    try {
      Connection conn = jdbcService.getCon();
      PreparedStatement statement = conn
          .prepareStatement("SELECT * FROM LESSON_CHORD JOIN CHORD ON LESSON_CHORD.C_ID = CHORD.C_ID WHERE L_ID = ?");
      statement.setInt(1, lessonId);
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        Chord chord = new Chord(rs.getString("C_NAME"), rs.getString("C_FILENAME"));
        chord.setId(rs.getInt("C_ID"));
        chordList.add(chord);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    return chordList;
  }

}
