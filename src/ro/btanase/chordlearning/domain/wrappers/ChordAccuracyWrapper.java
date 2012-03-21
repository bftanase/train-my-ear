package ro.btanase.chordlearning.domain.wrappers;

public class ChordAccuracyWrapper {
  private String chordName;
  private int correctCount;
  private int totalCount;
  
  private int accuracy; // calculated

  public ChordAccuracyWrapper(){
    
  }
  
  public ChordAccuracyWrapper(String chordName, int correctCount, int totalCount) {
    super();
    this.chordName = chordName;
    this.correctCount = correctCount;
    this.totalCount = totalCount;
  }

  public String getChordName() {
    return chordName;
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
