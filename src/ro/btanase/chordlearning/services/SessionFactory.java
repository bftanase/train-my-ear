package ro.btanase.chordlearning.services;

import org.apache.ibatis.session.SqlSessionFactory;

public interface SessionFactory {
  public SqlSessionFactory get();
}
