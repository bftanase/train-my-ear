package ro.btanase.chordlearning;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;

import ro.btanase.chordlearning.data.LessonMapper;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.tests.ChordLearningModuleTest;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class Test {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {

    Injector injector = Guice.createInjector(new ChordLearningModule());
    SessionFactory sessionFactory = injector.getInstance(SessionFactory.class);
    
    SqlSession session = sessionFactory.get().openSession();
    
    LessonMapper mapper = session.getMapper(LessonMapper.class);
    
    List<Lesson> lessonList = mapper.selectAll();
    
    int i=0;
    
    for (Lesson lesson : lessonList) {
      lesson.setOrder(i);
      i++;
      
      mapper.update(lesson);
    }
    
    session.commit();
    
    session.close();
  }

}
