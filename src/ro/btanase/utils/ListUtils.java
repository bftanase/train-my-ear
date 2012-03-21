package ro.btanase.utils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ListUtils {

  /**
   * Return a list with duplicates removed based on a supplied Comparator<br/>
   * 
   * <b style="color:red">Warning:</b> List must be sorted first!!!
   * 
   * @param <T>
   * @param initialList
   * @param comparator
   * @return sorted list
   */
  public static <T> List<T> removeDuplicates (List<T> initialList, Comparator<T> comparator){
    List<T> finalList = new ArrayList<T>();
    
    for (int i = 0; i<initialList.size(); i++){
      if (i == 0){
        finalList.add(initialList.get(i));
        continue;
      }
      
      if (comparator.compare(initialList.get(i-1), initialList.get(i)) == 0){
        continue;
      }else{
        finalList.add(initialList.get(i));
      }
    }
    
    return finalList;
  }
  
  public static <E, T> E findInList(List<E> list, T t, Searchable<E, T> search){
    for(E elem : list){
      if (search.match(elem, t)){
        return elem;
      }
    
    }
    
    return null;
  }
  
}
