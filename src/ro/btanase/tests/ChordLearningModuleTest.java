package ro.btanase.tests;

import ro.btanase.chordlearning.dao.ChordDao;
import ro.btanase.chordlearning.dao.ChordDaoDbImpl;
import ro.btanase.chordlearning.services.JdbcService;
import ro.btanase.chordlearning.services.MessengerService;
import ro.btanase.chordlearning.services.MessengerServiceSwingImpl;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.chordlearning.services.SessionFactoryImplTest;
import ro.btanase.chordlearning.services.UserData;
import ro.btanase.chordlearning.services.UserDataImpl;

import com.google.inject.AbstractModule;

public class ChordLearningModuleTest extends AbstractModule{

  @Override
  protected void configure() {
//    bind(MessengerService.class).to(MessengerServiceSwingImpl.class);
    
//    bind(ChordDao.class).to(ChordDaoDbImpl.class);
//    bind(JdbcService.class).to(JdbcServiceImplTest.class);
//    bind(LessonDefinitionFrame.class).to(LessonDefinitionFrame.class);
//    bind(UserData.class).to(UserDataImpl.class);
   
      bind(SessionFactory.class).to(SessionFactoryImplTest.class);
  }

}
