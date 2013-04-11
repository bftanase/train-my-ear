package ro.btanase.chordlearning.dao;

import org.apache.ibatis.session.SqlSession;
import org.apache.log4j.Logger;

import ro.btanase.chordlearning.mybatisgen.client.SettingsMapper;
import ro.btanase.chordlearning.mybatisgen.model.Settings;
import ro.btanase.chordlearning.services.SessionFactory;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SettingsDaoIbatisImpl implements SettingsDao {

  Logger log = Logger.getLogger(getClass());
  
  @Inject
  SessionFactory sessionFactory;
  
  @Override
  public String[] getSlots() {
    SqlSession session = null;
    Settings settings = null;
    
    String[] result = null;
    
    try{
      session = sessionFactory.get().openSession();
      
      SettingsMapper mapper = session.getMapper(SettingsMapper.class);
      settings = mapper.selectByKey(SLOTS);
    }finally{
      try{
        session.close();
      }catch(Exception e){}
    }

    if (settings == null){
      result = new String[]{"Slot 1", "Slot 2", "Slot 3", "Slot 4", "Slot 5"};  
    } else{
      Gson gson = new Gson();
      try{
        result = gson.fromJson(settings.getValue(), String[].class);
      }catch (JsonSyntaxException e) {
        log.info("settings retrieved from db not in expected format", e);
        result = null;
      }
    }
    return result;
  }

  @Override
  public void setSlots(String[] slotArr) {
    SqlSession session = null;
    Settings settings = null;

    Gson gson = new Gson();
    String value = gson.toJson(slotArr);
    
    try{
      session = sessionFactory.get().openSession();
      
      SettingsMapper mapper = session.getMapper(SettingsMapper.class);
      settings = mapper.selectByKey(SLOTS);
      
      if (settings == null){
        settings = new Settings();
        settings.setKey(SLOTS);
        settings.setValue(value);
        mapper.insert(settings);
        session.commit();
      } else {
        settings.setValue(value);
        mapper.updateByPrimaryKey(settings);
        session.commit();
      }
      
    }finally{
      try{
        session.close();
      }catch(Exception e){}
    }    
  }

}
