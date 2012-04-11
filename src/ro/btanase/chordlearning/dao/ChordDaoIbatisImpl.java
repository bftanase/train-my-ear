package ro.btanase.chordlearning.dao;

import java.io.File;
import java.lang.reflect.Method;
import java.sql.SQLIntegrityConstraintViolationException;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import ro.btanase.chordlearning.data.ChordMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.exceptions.ConstraintException;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.mediaplayer.MediaPlayer;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ChordDaoIbatisImpl implements ChordDao {

  private EventList<Chord> chordList = new BasicEventList<Chord>();
  private SessionFactory sessionFactory;
  private static Logger log = Logger.getLogger(ChordDaoIbatisImpl.class);
  @Inject
  private UserData userData;
  @Inject
  private MediaPlayer mediaPlayer;

  @Inject
  public ChordDaoIbatisImpl(SessionFactory sessionFactory) {
    super();
    this.sessionFactory = sessionFactory;
  }

  @Override
  public EventList<Chord> fetchChords() {
    chordList.clear();
      
    SqlSession session = sessionFactory.get().openSession();

    try{
      ChordMapper chordMapper = session.getMapper(ChordMapper.class);
      chordList.addAll(GlazedLists.eventList(chordMapper.selectAll()));
    }finally{
      session.close();
    }

    return chordList;
  }

  @Override
  public EventList<Chord> getAllChords() {
    if (chordList.isEmpty()) {
      fetchChords();
    }
    return chordList;
  }

  @Override
  public void addChord(Chord chord) {
    SqlSession session = sessionFactory.get().openSession();
    try {
      ChordMapper chordMapper = session.getMapper(ChordMapper.class);
      chordMapper.insert(chord);
      session.commit();
    }finally{
      session.close();
    }

    fetchChords();
  }

  @Override
  public void deleteChord(Chord chord) {
    SqlSession session = sessionFactory.get().openSession();    
    try {
      ChordMapper chordMapper = session.getMapper(ChordMapper.class);
      chordMapper.delete(chord.getId());

      // delete file
      mediaPlayer.stopPlayback();
      
      for (int i=0; i < MediaPlayer.NO_SLOTS; i++){
        String fileName;
        
        if (i == 0){
          fileName = chord.getFileName();
        } else {
          Method method = chord.getClass().getMethod("getFileName" + (i+1), null);
          try {
            fileName = (String) method.invoke(chord, null);
          } catch (Exception e) {
            log.error("Reflection failure", e);
            throw new RuntimeException(e);
          }
        }
        
        if (fileName != null){
          String fullpath = userData.getMediaFolder() + File.separator + fileName;
          log.debug("Trying to delete file: " + fullpath);
          File file = new File(fullpath);

          if (!file.delete()) {
            throw new RuntimeException("Cannot delete file: " + chord.getFileName());
          }
        }
      }

      chordList.remove(chord);
      session.commit();
    } catch (PersistenceException pe) {
      session.rollback();
      if (ExceptionUtils.indexOfThrowable(pe, SQLIntegrityConstraintViolationException.class) != -1){
        throw new ConstraintException(
            "This chord is used in other lessons. Remove it from the lesson first");
      }
    } catch (Exception e) {
      session.rollback();
      throw new RuntimeException(e);
    } finally {
      session.close();
    }

  }

  @Override
  public void updateChord(Chord chord) {
    SqlSession session = sessionFactory.get().openSession();    
    try {
      ChordMapper mapper = session.getMapper(ChordMapper.class);
      mapper.update(chord);
      
      chordList.set(chordList.indexOf(chord), chord);
      session.commit();
    }finally{
      session.close();
    }

  }

  @Override
  public Chord getChord(String name) {
    // TODO Auto-generated method stub
    return null;
  }

}
