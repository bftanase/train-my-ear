package ro.btanase.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.google.inject.Guice;
import com.google.inject.Injector;

import ro.btanase.chordlearning.ChordLearningModule;
import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.domain.ChordAccuracy;
import ro.btanase.utils.ListUtils;

public class ScoreDaoTest {
  private Logger log = Logger.getLogger(getClass());
  
  
  @Before
  public void setUp(){

  }

  @Test
  public void testGetChordAccuracyList(){
//    Injector injector = Guice.createInjector(new ChordLearningModule());
//    ScoreDao scoreDao = injector.getInstance(ScoreDao.class);
//    List<ChordAccuracy> chordAccuracyList = scoreDao.getChordAccuracyList();
//    for (ChordAccuracy chordAccuracy : chordAccuracyList) {
//      log.debug("chord: + " + chordAccuracy.getChord().getChordName() +
//            " totalTests: " + chordAccuracy.getTotalEntries() +
//            " correct: " + chordAccuracy.getCorrectEntries() + 
//            " acc: " + chordAccuracy.getAccuracy());
//    }
  }

  
}
