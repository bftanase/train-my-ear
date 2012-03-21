package ro.btanase.chordlearning.domain;

import java.io.Serializable;

public class Chord implements Serializable {
  private int id;
  private String chordName;
  private String fileName;
//  private ChordType chordType;
  
  public Chord(String chordName, String fileName) {
    this.chordName = chordName;
    this.fileName = fileName;
    
  }
  
  public Chord() {
  }



  public String getChordName() {
    return chordName;
  }
  public void setChordName(String chordName) {
    this.chordName = chordName;
  }
  public String getFileName() {
    return fileName;
  }
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }


  @Override
  public String toString() {
    return chordName;
  }

//  public ChordType getChordType() {
//    return chordType;
//  }
//
//  public void setChordType(ChordType chordType) {
//    this.chordType = chordType;
//  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
    this.id = id;
  }
  
  
}
