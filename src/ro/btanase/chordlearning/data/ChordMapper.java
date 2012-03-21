package ro.btanase.chordlearning.data;

import java.util.List;

import ro.btanase.chordlearning.domain.Chord;

public interface ChordMapper {
  public List<Chord> selectAll();
  public void insert(Chord chord);
  public int lastInsertId();
  public Chord selectById(int id);
  public void update(Chord chord);
  public void delete(int id);
}
