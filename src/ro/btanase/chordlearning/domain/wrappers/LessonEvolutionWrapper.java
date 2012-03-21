package ro.btanase.chordlearning.domain.wrappers;

import java.util.Date;

public class LessonEvolutionWrapper {
  private String lessonName;
  private Date scoreDate;
  private int correctCount;
  private int totalCount;
  
  private int accuracy;

  
  
  public LessonEvolutionWrapper(){
    
  }
  
  public LessonEvolutionWrapper(String lessonName, Date scoreDate,
      int correctCount, int totalCount) {
    super();
    this.lessonName = lessonName;
    this.scoreDate = scoreDate;
    this.correctCount = correctCount;
    this.totalCount = totalCount;
  }

  public String getLessonName() {
    return lessonName;
  }

  public Date getScoreDate() {
    return scoreDate;
  }

  public int getCorrectCount() {
    return correctCount;
  }

  public int getTotalCount() {
    return totalCount;
  }

  public int getAccuracy() {
    accuracy = (correctCount * 100) / totalCount;
    return accuracy;
  }
  
}
