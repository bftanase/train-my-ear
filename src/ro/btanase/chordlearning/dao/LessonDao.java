package ro.btanase.chordlearning.dao;

import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.Lesson;
import ca.odell.glazedlists.EventList;


public interface LessonDao {
  public EventList<Lesson> fetchLessons();

  public EventList<Lesson> getAll();
  
  public void addLesson(Lesson lesson);
  
  public void deleteLesson(Lesson lesson);
  
  public void updateLesson(Lesson lesson);
  
  public Lesson getLesson(String lessonName);
  
  public void moveUp(Lesson lesson);

  public void moveDown(Lesson lesson);
  
  public EventList<Chord> getChordsFromLesson(int lessonId);
}
