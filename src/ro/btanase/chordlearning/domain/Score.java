package ro.btanase.chordlearning.domain;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Score implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  private int id;
  private Lesson lesson;
  private List<ExerciseResult> lessonResults;
  private Date date;

  public Score(){
    
  }
  
  
  public Score(Lesson lesson, List<ExerciseResult> lessonResults) {
    super();
    this.lesson = lesson;
    this.lessonResults = lessonResults;
    date = new Date();
  }

  public Lesson getLesson() {
    return lesson;
  }

  public void setLesson(Lesson lesson) {
    this.lesson = lesson;
  }

  public List<ExerciseResult> getLessonResults() {
    return lessonResults;
  }

  public void setLessonResults(List<ExerciseResult> lessonResults) {
    this.lessonResults = lessonResults;
  }

  public double getAccuracy() {
    if (lessonResults == null || lessonResults.isEmpty()){
      return 0.0;
    }else{
      int size = lessonResults.size();
      int correctCount = 0;
      // count correct answers
      for(ExerciseResult er: lessonResults){
        if (er.isCorrect()){
          correctCount++;
        }
      }
      
      return (correctCount * 100)/size;
    }
    
  }

  public Date getDate() {
    return date;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  
  
}
