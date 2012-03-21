package ro.btanase.chordlearning.dao;

import ro.btanase.chordlearning.domain.ChordAccuracy;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.domain.Score;
import ro.btanase.chordlearning.domain.wrappers.ChordAccuracyWrapper;
import ro.btanase.chordlearning.domain.wrappers.LessonEvolutionWrapper;
import ca.odell.glazedlists.EventList;

public interface ScoreDao {
  public void addScore(Score score);
  public EventList<Score> getAllScores();
  public EventList<Score> getScoresByLesson(Lesson lesson);
  public EventList<Score> fetchScores();
  
  public EventList<ChordAccuracyWrapper> getChordAccuracyList();
  
  public EventList<LessonEvolutionWrapper> getEachExerciseLessonAccuracyList(Lesson lesson);
  
  public void resetScores();
    
}
