package ro.btanase.tests;

import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.chordlearning.services.SessionFactoryImplTest;

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
