package ro.btanase.chordlearning.dao;

import java.util.List;

import ca.odell.glazedlists.EventList;

import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.Lesson;


public interface LessonDao {
  public EventList<Lesson> fetchLessons();

  public EventList<Lesson> getAll();
  
  public void addLesson(Lesson lesson);
  
  public void deleteLesson(Lesson lesson);
  
  public void updateLesson(Lesson lesson);
  
  public Lesson getLesson(String lessonName);
  
  public EventList<Chord> getChordsFromLesson(int lessonId);
}
