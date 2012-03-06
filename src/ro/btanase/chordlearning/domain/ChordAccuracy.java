package ro.btanase.chordlearning.domain;

public class ChordAccuracy {
  private Chord chord;
  private int totalEntries;
  private int correctEntries;

  public Chord getChord() {
    return chord;
  }

  public void setChord(Chord chord) {
    this.chord = chord;
  }

  public double getAccuracy() {
    if (totalEntries == 0){
      return 0;
    }
    
    return (correctEntries * 100) / totalEntries; 
  }


  public int getTotalEntries() {
    return totalEntries;
  }

  public void setTotalEntries(int totalEntries) {
    this.totalEntries = totalEntries;
  }

  public int getCorrectEntries() {
    return correctEntries;
  }

  public void setCorrectEntries(int correctyEntries) {
    this.correctEntries = correctyEntries;
  }
  
  
  
}
