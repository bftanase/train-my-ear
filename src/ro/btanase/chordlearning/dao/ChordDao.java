package ro.btanase.chordlearning.dao;

import ro.btanase.chordlearning.domain.Chord;
import ca.odell.glazedlists.EventList;


public interface ChordDao {
  
  /**
   * load the chord list in memory from storage (db, file, etc - depends on implementation)
   * @return
   */
  public EventList<Chord> fetchChords(); 
  
  public EventList<Chord> getAllChords();
  
  public void addChord(Chord chord);
  
  public void deleteChord(Chord chord);
  
  public void updateChord(Chord chord);
  
  public Chord getChord(String name);
  
}
