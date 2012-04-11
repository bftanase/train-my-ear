package ro.btanase.chordlearning.dao;

public interface SettingsDao {
  public static final String SLOTS = "SLOTS";
  
  public String[] getSlots();
  public void setSlots(String[] slotArr);
  
}
