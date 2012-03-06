package ro.btanase.chordlearning.domain;

import java.io.Serializable;

public class ExerciseResult implements Serializable {
  private int id;
  private Chord chord;
  private boolean correct;

  public ExerciseResult(){
    
  }
  
  public ExerciseResult(Chord chord, boolean passed) {
    super();
    this.chord = chord;
    this.correct = passed;
  }
  public Chord getChord() {
    return chord;
  }
  public void setChord(Chord chord) {
    this.chord = chord;
  }
  public boolean isCorrect() {
    return correct;
  }
  public void setCorrect(boolean correct) {
    this.correct = correct;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  
}
