package ro.btanase.chordlearning.data;

import java.util.List;
import java.util.Map;

import ro.btanase.chordlearning.domain.Score;
import ro.btanase.chordlearning.domain.wrappers.ChordAccuracyWrapper;
import ro.btanase.chordlearning.domain.wrappers.LessonEvolutionWrapper;

public interface ScoreMapper {

  public Score selectScoreById(int id);
  /**
   * prepares information necessary for reporting how well a user is able to recognize the chords
   * from exercises
   * @return
   */
  public List<ChordAccuracyWrapper> selectChordAccuracy();
  
  /**
   * prepares information necessary for reporting the success rate over time for a given lesson
   * @param lessonId
   * @return
   */
  public List<LessonEvolutionWrapper> selectLessonAccuracyEvolution(int lessonId);

  /**
   * removes all the information in scores table. It will cascade to all score related tables 
   */
  public void deleteAllScores();

  public void insertScore(Score score);

  /**
   * parameter map expects these keys:<br/>
   * <ul>
     *  <li><b>chordId</b> (int) - id of the chord object</li>
     *  <li><b>scoreId</b> (int) - id of the parent score object</li>
     *  <li><b>correct</b> (boolean) - did the user answered correctly?</li>
   * </ul>
   * 
   * @param params - what is send to the sql query
   */
  public void insertExerciseResult(Map<String, Object> params);

  
  /**
   * This method should no longer be used. Instead set <b>keyProperty</b> and <b>useGeneratedKeys</b>
   * in the mybatis xml mapper files.
   * It will automatically set the generated id on the passed object
   */
  @Deprecated
  public int lastInsertId();
}
