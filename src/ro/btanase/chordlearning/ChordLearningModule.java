package ro.btanase.chordlearning;

import ro.btanase.chordlearning.dao.ChordDao;
import ro.btanase.chordlearning.dao.ChordDaoIbatisImpl;
import ro.btanase.chordlearning.dao.LessonDao;
import ro.btanase.chordlearning.dao.LessonDaoIbatisImpl;
import ro.btanase.chordlearning.dao.ScoreDao;
import ro.btanase.chordlearning.dao.ScoreDaoIbatisImpl;
import ro.btanase.chordlearning.services.JdbcService;
import ro.btanase.chordlearning.services.JdbcServiceImpl;
import ro.btanase.chordlearning.services.MessengerService;
import ro.btanase.chordlearning.services.MessengerServiceSwingImpl;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.chordlearning.services.SessionFactoryImpl;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.chordlearning.services.UserDataImpl;

import com.google.inject.AbstractModule;

public class ChordLearningModule extends AbstractModule{

  @Override
  protected void configure() {
    bind(MessengerService.class).to(MessengerServiceSwingImpl.class);
    
    bind(ChordDao.class).to(ChordDaoIbatisImpl.class);
    bind(LessonDao.class).to(LessonDaoIbatisImpl.class);
    bind(JdbcService.class).to(JdbcServiceImpl.class);
    bind(SessionFactory.class).to(SessionFactoryImpl.class);
    
//    bind(LessonDefinitionFrame.class).to(LessonDefinitionFrame.class);
    bind(ScoreDao.class).to(ScoreDaoIbatisImpl.class);
    bind(UserData.class).to(UserDataImpl.class);
    
  }

}
