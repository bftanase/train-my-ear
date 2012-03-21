package ro.btanase.tests;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import ca.odell.glazedlists.matchers.Matcher;

import ro.btanase.utils.ListUtils;
import ro.btanase.utils.Searchable;
import sun.util.logging.resources.logging;

public class ListUtilsTest {
  
  private List<Person> personList;
  
  @Before
  public void setUp(){
    personList = new ArrayList<ListUtilsTest.Person>();
    
    personList.add(new Person("Alex", 3));
    personList.add(new Person("Alex", 6));
    personList.add(new Person("John", 6));
    personList.add(new Person("Matt", 7));
    personList.add(new Person("Simon", 45));
    personList.add(new Person("Simon", 6));
    personList.add(new Person("Simon", 6));
    personList.add(new Person("Dan", 6));
    personList.add(new Person("Paul", 6));
    personList.add(new Person("Andrew", 6));
    personList.add(new Person("Andrew", 6));
    
    Collections.sort(personList, new Comparator<Person>() {

      @Override
      public int compare(Person o1, Person o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });

  }

  @Test
  public void testRemoveDuplicates() {
    List<Person> newPersonList = ListUtils.removeDuplicates(personList, new Comparator<Person>() {

      @Override
      public int compare(Person o1, Person o2) {
        return o1.getName().compareTo(o2.getName());
      }
    });
    
    for (Person person : newPersonList) {
      System.out.println(person.getName());
    }
    
    assertEquals(7, newPersonList.size());
    assertEquals("Alex", newPersonList.get(0).getName());
    assertEquals("Andrew", newPersonList.get(1).getName());
    assertEquals("Dan", newPersonList.get(2).getName());
    assertEquals("John", newPersonList.get(3).getName());
    assertEquals("Matt", newPersonList.get(4).getName());
    assertEquals("Paul", newPersonList.get(5).getName());
    assertEquals("Simon", newPersonList.get(6).getName());
  }

  @Test
  public void testFindInList(){
    String str = "test";
    
    Person pers = ListUtils.findInList(personList, str, new Searchable<Person, String>() {

      @Override
      public boolean match(Person e, String t) {
        return e.getName().equals(t);
      }
    });
    
    assertNull(pers);
    
    pers = ListUtils.findInList(personList, "Andrew", new Searchable<Person, String>(){

      @Override
      public boolean match(Person e, String t) {
        return e.getName().equals(t);
      }
     });
    
    System.out.println("Matched name: " + pers.getName());
    assertEquals(pers, personList.get(2));
  }
  
  class Person{
    String name;
    int age;
    public String getName() {
      return name;
    }
    public void setName(String name) {
      this.name = name;
    }
    public int getAge() {
      return age;
    }
    public void setAge(int age) {
      this.age = age;
    }

    public Person(String name, int age) {
      this.name = name;
      this.age = age;
    }
    
    
  }
}
