package ro.btanase.chordlearning.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.ChordAccuracy;
import ro.btanase.chordlearning.domain.ExerciseResult;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.chordlearning.domain.wrappers.ChordAccuracyWrapper;
import ro.btanase.chordlearning.domain.wrappers.LessonEvolutionWrapper;
import ro.btanase.chordlearning.services.JdbcService;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

@Singleton
public class ScoreDaoDbImpl implements ScoreDao {

  private JdbcService jdbcService;
  private static Logger log = Logger.getLogger(ScoreDaoDbImpl.class);
  private EventList<Score> scoreList = new BasicEventList<Score>();
  

  @Inject
  public ScoreDaoDbImpl(JdbcService jdbcService) {
    this.jdbcService = jdbcService;
  }

  @Override
  public void addScore(Score score) {
    try {
      
      // disable autocommit mode; we'll use transactions for this method
      jdbcService.getCon().setAutoCommit(false);

      // add current score
      PreparedStatement statement = jdbcService.getCon().prepareStatement(
          "INSERT INTO SCORE (S_DATE, L_ID) VALUES (?, ?) ", Statement.RETURN_GENERATED_KEYS);
      // requires converstion to java.sql.Date, yuck!
      Date currentDate = new Date(score.getDate().getTime());
      statement.setDate(1, currentDate);
      statement.setInt(2, score.getLesson().getId());

      statement.executeUpdate();

      ResultSet resultSet = statement.getGeneratedKeys();

      resultSet.next();

      int insertedId = resultSet.getInt(1);

      // Log.debug(message)"gen key" + statement.getGeneratedKeys().getInt(0);
      // statement.execute("SHUTDOWN");
      statement.close();

      score.setId(insertedId);

      List<ExerciseResult> resultList = score.getLessonResults();
      
      for (ExerciseResult er: resultList) {
        PreparedStatement exerciseResultStatement = 
            jdbcService.getCon().prepareStatement("INSERT INTO EXERCISE_SCORE (C_ID, S_ID, CORRECT) VALUES ?, ?, ?");
        exerciseResultStatement.setInt(1, er.getChord().getId());
        exerciseResultStatement.setInt(2, score.getId());
        exerciseResultStatement.setBoolean(3, er.isCorrect());
        exerciseResultStatement.executeUpdate();
        
      }
      
      jdbcService.getCon().commit();
      fetchScores();
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
  public EventList<Score> getAllScores() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EventList<Score> getScoresByLesson(Lesson lesson) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EventList<Score> fetchScores() {
//    scoreList.clear();
//    try {
//      Connection conn = jdbcService.getCon();
//      PreparedStatement statement = conn.prepareStatement("SELECT * FROM SCORE");
//      ResultSet rs = statement.executeQuery();
//
//      while (rs.next()) {
//        Score score = new Score(lesson, lessonResults)
//        lesson.setLessonName(rs.getString("L_NAME"));
//        lesson.setId(rs.getInt("L_ID"));
//        lesson.setNoQuestions(rs.getInt("L_NO_QUESTIONS"));
//
//        // eager loading for child elements
//        lesson.setChordSequence(getChordsFromLesson(lesson.getId()));
//        lessonList.add(lesson);
//      }
//
//    } catch (Exception e) {
//      throw new RuntimeException(e);
//    }
//    return lessonList;
    return null;
  }

  @Override
  public EventList<ChordAccuracyWrapper> getChordAccuracyList() {
    EventList<ChordAccuracyWrapper> chordAccuracy = new BasicEventList<ChordAccuracyWrapper>();
    String queryString = "SELECT " + 
                            "c_name, " +
                            "(SELECT COUNT(*) " +
                            " FROM exercise_score es " +
                            " WHERE es.c_id = exercise_score.c_id AND es.correct = true ) AS correct_count, " +
                            "COUNT(correct) AS total_count " +
                         "FROM exercise_score " + 
                         "JOIN chord ON exercise_score.C_ID = chord.C_ID " +
                         "GROUP BY c_id, c_name";  
  
    try {
      Connection conn = jdbcService.getCon();
      PreparedStatement statement = conn.prepareStatement(queryString);
      ResultSet rs = statement.executeQuery();
  
      while (rs.next()) {
        String chordName = rs.getString("c_name");
        int correctCount = rs.getInt("correct_count");
        int totalCount = rs.getInt("total_count");
        
        ChordAccuracyWrapper caw = new ChordAccuracyWrapper(chordName, correctCount, totalCount);
        chordAccuracy.add(caw);
      }
  
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

    return chordAccuracy;
  }

  @Override
  public EventList<LessonEvolutionWrapper> getEachExerciseLessonAccuracyList(Lesson lesson) {

    EventList<LessonEvolutionWrapper> lewList = new BasicEventList<LessonEvolutionWrapper>();
    String queryString =  " SELECT " +
                          "   lesson.l_name, " +
                          "   s_date, " +
                          "   (SELECT COUNT(correct) " +
                          "     FROM exercise_score " +  
                          "     WHERE exercise_score.s_id = score.s_id AND correct = true) as correct_count, " +
                          "   (SELECT COUNT(correct) " +
                          "     FROM exercise_score " +
                          "     WHERE exercise_score.s_id = score.s_id) as total_count " +
                          " FROM score " +
                          " JOIN lesson ON score.l_id  = lesson.l_id " +
                          " WHERE l_id = ? ";
    
    try {
      Connection conn = jdbcService.getCon();
      PreparedStatement statement = conn.prepareStatement(queryString);
      statement.setInt(1, lesson.getId());
      ResultSet rs = statement.executeQuery();
  
      while (rs.next()) {
        String lessonName = rs.getString("l_name");
        java.util.Date scoreDate = rs.getDate("s_date");
        int correctCount = rs.getInt("correct_count");
        int totalCount = rs.getInt("total_count");
        
        LessonEvolutionWrapper lew = new LessonEvolutionWrapper(lessonName, scoreDate, correctCount, totalCount);
        lewList.add(lew);
      }
  
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
    
    return lewList;
  }

  @Override
  public void resetScores() {
    try{
      PreparedStatement stmnt = jdbcService.getCon().prepareStatement("DELETE FROM score");
      stmnt.executeUpdate();
    }catch (Exception e) {
      throw new RuntimeException(e);
    }
    
  }
  
  
  

}
