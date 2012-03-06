package ro.btanase.chordlearning.dao;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.LessonType;
import ro.btanase.chordlearning.exceptions.ConstraintException;
import ro.btanase.chordlearning.services.JdbcService;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.mediaplayer.MediaPlayer;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class ChordDaoDbImpl implements ChordDao {

  private EventList<Chord> chordList = new BasicEventList<Chord>();
  private JdbcService jdbcService;
  private static Logger log = Logger.getLogger(ChordDaoDbImpl.class);
  @Inject
  private UserData userData;
  @Inject
  private MediaPlayer mediaPlayer;

  @Inject
  public ChordDaoDbImpl(JdbcService jdbcService) {
    super();
    this.jdbcService = jdbcService;
  }

  @Override
  public EventList<Chord> fetchChords() {
    chordList = new BasicEventList<Chord>();
    try {
      Connection conn = jdbcService.getCon();
      PreparedStatement statement = conn
          .prepareStatement("SELECT * FROM chord");
      ResultSet rs = statement.executeQuery();

      while (rs.next()) {
        Chord chord = new Chord(rs.getString("C_NAME"),
            rs.getString("C_FILENAME"));
        chord.setId(rs.getInt("C_ID"));
        chordList.add(chord);
      }

    } catch (Exception e) {
      throw new RuntimeException(e);
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
    try {
      PreparedStatement statement = jdbcService.getCon().prepareStatement(
          "INSERT INTO chord (C_NAME, C_FILENAME) VALUES (?, ?) ",
          Statement.RETURN_GENERATED_KEYS);
      statement.setString(1, chord.getChordName());
      statement.setString(2, chord.getFileName());

      statement.executeUpdate();

      ResultSet resultSet = statement.getGeneratedKeys();

      resultSet.next();

      log.debug("gen key" + resultSet.getInt(1));
      int insertedId = resultSet.getInt(1);

      // Log.debug(message)"gen key" + statement.getGeneratedKeys().getInt(0);
      // statement.execute("SHUTDOWN");
      statement.close();

      chord.setId(insertedId);

      chordList.add(chord);

    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public void deleteChord(Chord chord) {
    PreparedStatement statement = null;
    try {
      jdbcService.getCon().setAutoCommit(false);
      statement = jdbcService.getCon().prepareStatement(
          "DELETE FROM chord WHERE C_ID=?");
      statement.setInt(1, chord.getId());

      if (statement.executeUpdate() != 1) {
        throw new RuntimeException("Error deleting selected chord");
      }

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
      jdbcService.getCon().commit();
    } catch (SQLIntegrityConstraintViolationException se) {
      try {
        jdbcService.getCon().rollback();
      } catch (SQLException e1) {
        throw new RuntimeException(e1);
      }
      throw new ConstraintException(
          "This chord is used in other lessons. Remove it from the lesson first");
    } catch (Exception e) {
      try {
        jdbcService.getCon().rollback();
      } catch (SQLException e1) {
        throw new RuntimeException(e1);
      }
      throw new RuntimeException(e);
    } finally {
      try {
        jdbcService.getCon().setAutoCommit(true);
        statement.close();
      } catch (Exception ex) {
        log.error(ex);
      }
    }

  }

  @Override
  public void updateChord(Chord chord) {
    try {
      PreparedStatement statement = jdbcService.getCon().prepareStatement(
          "UPDATE chord SET C_NAME = ?, C_FILENAME = ? WHERE C_ID=?");
      statement.setString(1, chord.getChordName());
      statement.setString(2, chord.getFileName());
      statement.setInt(3, chord.getId());

      log.debug("chord.chordName: " + chord.getChordName());
      log.debug("chord.fileName: " + chord.getFileName());
      log.debug("chord.id: " + chord.getId());
      
      if (statement.executeUpdate() != 1) {
        throw new RuntimeException("Error updating selected chord");
      }
      
      statement.close();
      chordList.set(chordList.indexOf(chord), chord);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }

  }

  @Override
  public Chord getChord(String name) {
    // TODO Auto-generated method stub
    return null;
  }

}
