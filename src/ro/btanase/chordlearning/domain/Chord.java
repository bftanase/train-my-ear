package ro.btanase.chordlearning.domain;

import java.io.Serializable;

public class Chord implements Serializable {
  private int id;
  private String chordName;
  private String fileName;
  private String fileName2;
  private String fileName3;
  private String fileName4;
  private String fileName5;
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

  public String getFileName2() {
    return fileName2;
  }

  public void setFileName2(String fileName2) {
    this.fileName2 = fileName2;
  }

  public String getFileName3() {
    return fileName3;
  }

  public void setFileName3(String fileName3) {
    this.fileName3 = fileName3;
  }

  public String getFileName4() {
    return fileName4;
  }

  public void setFileName4(String fileName4) {
    this.fileName4 = fileName4;
  }

  public String getFileName5() {
    return fileName5;
  }

  public void setFileName5(String fileName5) {
    this.fileName5 = fileName5;
  }

  
  
}
