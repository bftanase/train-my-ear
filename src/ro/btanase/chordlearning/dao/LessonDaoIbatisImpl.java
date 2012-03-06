package ro.btanase.chordlearning.dao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import ro.btanase.chordlearning.data.LessonMapper;
import ro.btanase.chordlearning.domain.Chord;
import ro.btanase.chordlearning.domain.Lesson;
import ro.btanase.chordlearning.services.SessionFactory;
import ro.btanase.chordlearning.services.UserData;
import ca.odell.glazedlists.BasicEventList;
import ca.odell.glazedlists.EventList;
import ca.odell.glazedlists.GlazedLists;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class LessonDaoIbatisImpl implements LessonDao {
  
  private static Logger log = Logger.getLogger(LessonDaoIbatisImpl.class.getName());

  private EventList<Lesson> lessonList = new BasicEventList<Lesson>();
  private SessionFactory sessionFactory;
  
  @Inject
  private UserData userData;

  @Inject
  public LessonDaoIbatisImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

  @Override
  public EventList<Lesson> fetchLessons() {
    lessonList.clear();
    SqlSession session = sessionFactory.get().openSession();
    try {
      LessonMapper mapper = session.getMapper(LessonMapper.class);
      List<Lesson> lList = mapper.selectAll();

      for(Lesson lesson: lList) {
        
        // eager loading for child elements
        lesson.setChordSequence(getChordsFromLesson(lesson.getId()));
        this.lessonList.add(lesson);
      }

    } finally{
      session.close();
    }
    return lessonList;
  }

  @Override
  public EventList<Lesson> getAll() {
    if (lessonList.isEmpty()) {
      fetchLessons();
    }
    return lessonList;
  }

  @Override
  public void addLesson(Lesson lesson) {
    SqlSession session = sessionFactory.get().openSession();

    try {
      LessonMapper mapper = session.getMapper(LessonMapper.class);      
      mapper.insert(lesson);

      EventList<Chord> chordList = lesson.getChordSequence();
      
      for (Chord chord : chordList) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("chordId", chord.getId());
        param.put("lessonId", lesson.getId());
        
        mapper.insertChordToLesson(param);
      }
      
      session.commit();
      fetchLessons();
    } finally{
      session.close();
    }

  }

  @Override
  public void deleteLesson(Lesson lesson) {
    SqlSession session = sessionFactory.get().openSession();

    try{
      LessonMapper mapper = session.getMapper(LessonMapper.class);      
      mapper.delete(lesson.getId());
      fetchLessons();
    }finally{
      session.close();
    }

  }

  @Override
  public void updateLesson(Lesson lesson) {
    SqlSession session = sessionFactory.get().openSession();
    try {
      LessonMapper mapper = session.getMapper(LessonMapper.class);      
      
      mapper.update(lesson);

      EventList<Chord> chordList = lesson.getChordSequence();

      // clear existing relations
      mapper.deleteLessonChords(lesson.getId());
      
      
      // add new selected chords
      
      for (Chord chord : chordList) {
        Map<String, Object> param = new HashMap<String, Object>();
        
        param.put("chordId", chord.getId());
        param.put("lessonId", lesson.getId());
        
        mapper.insertChordToLesson(param);
      }
      
      session.commit();
      fetchLessons();

    } catch (Exception e) {
      session.rollback();
      throw new RuntimeException(e);
    }finally{
      session.close();
    }
  }

  @Override
  public Lesson getLesson(String lessonName) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public EventList<Chord> getChordsFromLesson(int lessonId) {
    SqlSession session = sessionFactory.get().openSession();

    EventList<Chord> chordList = new BasicEventList<Chord>();
    try {
      LessonMapper mapper = session.getMapper(LessonMapper.class);
      chordList = GlazedLists.eventList(mapper.selectChordsByLessonId(lessonId));
    } finally{
      session.close();
    }
    return chordList;
  }

}
