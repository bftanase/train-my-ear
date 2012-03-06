package ro.btanase.chordlearning.domain;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

public class Lesson implements Serializable {
  private int id;
  private String lessonName;
  private int noQuestions;
  private LessonType type;

  // these are used if type == LessonType.PROGRESSION
  private int noChordsInSequence;
  private int chordDelay;
  
  private EventList<Chord> chordSequence;

  public String getLessonName() {
    return lessonName;
  }

  public void setLessonName(String lessonName) {
    this.lessonName = lessonName;
  }
  public EventList<Chord> getChordSequence() {
    return chordSequence;
  }
  public void setChordSequence(EventList<Chord> chordSequence) {
    this.chordSequence = chordSequence;
  }
  
  public EventList<Chord> randomize(){
    EventList<Chord> shuffledList = new BasicEventList<Chord>();
    
    Random random = new Random();
    if (noQuestions > 1){
      for (int i = 0; i < noQuestions; i++){
        int randomPosition = random.nextInt(chordSequence.size());
        shuffledList.add(chordSequence.get(randomPosition));
      }
    }
    
    return shuffledList;
  }

  public EventList<Chord> randomSequence(){
    EventList<Chord> shuffledList = new BasicEventList<Chord>();
    
    Random random = new Random();
    if (noChordsInSequence > 1){
      for (int i = 0; i < noChordsInSequence; i++){
        int randomPosition = random.nextInt(chordSequence.size());
        shuffledList.add(chordSequence.get(randomPosition));
      }
    }
    
    return shuffledList;
  }


  @Override
  public String toString() {
    return lessonName;
  }

  public int getNoQuestions() {
    return noQuestions;
  }

  public void setNoQuestions(int noQuestions) {
    this.noQuestions = noQuestions;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }

  public LessonType getType() {
    return type;
  }

  public void setType(LessonType type) {
    this.type = type;
  }

  public int getNoChordsInSequence() {
    return noChordsInSequence;
  }

  public void setNoChordsInSequence(int noChordsInSequence) {
    this.noChordsInSequence = noChordsInSequence;
  }

  public int getChordDelay() {
    return chordDelay;
  }

  public void setChordDelay(int chordDelay) {
    this.chordDelay = chordDelay;
  }
  
  
  
}
