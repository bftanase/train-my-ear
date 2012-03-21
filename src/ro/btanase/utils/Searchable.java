package ro.btanase.utils;

public interface Searchable<E, T> {
  public boolean match (E e, T t);
}
