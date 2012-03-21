package ro.btanase.chordlearning.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import com.google.inject.Inject;
import com.google.inject.Singleton;

import ro.btanase.chordlearning.data.ScoreMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.ChordAccuracy;
import ro.btanase.chordlearning.domain.ExerciseResult;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.chordlearning.domain.wrappers.ChordAccuracyWrapper;
import ro.btanase.chordlearning.domain.wrappers.LessonEvolutionWrapper;
import ro.btanase.chordlearning.services.JdbcService;
import ro.btanase.chordlearning.services.SessionFactory;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

@Singleton
public class ScoreDaoIbatisImpl implements ScoreDao {

  private SessionFactory sessionFactory;
  private static Logger log = Logger.getLogger(ScoreDaoIbatisImpl.class);
  private EventList<Score> scoreList = new BasicEventList<Score>();
  

  @Inject
  public ScoreDaoIbatisImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public void addScore(Score score) {
    SqlSession session = sessionFactory.get().openSession();
    try {
      ScoreMapper scoreMapper = session.getMapper(ScoreMapper.class);

      // add current score
      scoreMapper.insertScore(score);

      List<ExerciseResult> resultList = score.getLessonResults();
      
      for (ExerciseResult er: resultList) {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("chordId", er.getChord().getId());
        params.put("scoreId", score.getId());
        params.put("correct", er.isCorrect());
        
        scoreMapper.insertExerciseResult(params);
      }
      
      session.commit();
      fetchScores();
    } finally{
      session.close();
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
    return null;
  }

  @Override
  public EventList<ChordAccuracyWrapper> getChordAccuracyList() {
    EventList<ChordAccuracyWrapper> chordAccuracy = new BasicEventList<ChordAccuracyWrapper>();
    SqlSession session = sessionFactory.get().openSession();
    try {
      ScoreMapper scoreMapper = session.getMapper(ScoreMapper.class);
      chordAccuracy = GlazedLists.eventList(scoreMapper.selectChordAccuracy());
    } finally{
      session.close();
    }

    return chordAccuracy;
  }

  @Override
  public EventList<LessonEvolutionWrapper> getEachExerciseLessonAccuracyList(Lesson lesson) {

    EventList<LessonEvolutionWrapper> lewList = new BasicEventList<LessonEvolutionWrapper>();
    SqlSession session = sessionFactory.get().openSession();
    try {
      ScoreMapper scoreMapper = session.getMapper(ScoreMapper.class);
      lewList = GlazedLists.eventList(scoreMapper.selectLessonAccuracyEvolution(lesson.getId()));
  
    } finally{
      session.close();
    }
    
    return lewList;
  }

  @Override
  public void resetScores() {
    SqlSession session = sessionFactory.get().openSession();
    try {
      ScoreMapper scoreMapper = session.getMapper(ScoreMapper.class);
      scoreMapper.deleteAllScores();
      session.commit();
    }finally{
      session.close();
    }
  }
  
  
  

}
