package ro.btanase.chordlearning.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.exceptions.PersistenceException;
import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import ro.btanase.chordlearning.data.ChordMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.LessonType;
import ro.btanase.chordlearning.exceptions.ConstraintException;
import ro.btanase.chordlearning.services.JdbcService;
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
    chordList = new BasicEventList<Chord>();
      
    SqlSession session = sessionFactory.get().openSession();

    try{
      ChordMapper chordMapper = session.getMapper(ChordMapper.class);
      chordList = GlazedLists.eventList(chordMapper.selectAll());
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
    }finally{
      session.close();
    }

  }

  @Override
  public void deleteChord(Chord chord) {
    SqlSession session = sessionFactory.get().openSession();    
    try {
      ChordMapper chordMapper = session.getMapper(ChordMapper.class);
      chordMapper.delete(chord.getId());

      // delete file
      mediaPlayer.stopPlayback();
      String fileName = userData.getMediaFolder() + File.separator
          + chord.getFileName();
      log.debug("Trying to delete file: " + fileName);
      File file = new File(fileName);

      if (!file.delete()) {
        throw new RuntimeException("Cannot delete file: " + chord.getFileName());
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
