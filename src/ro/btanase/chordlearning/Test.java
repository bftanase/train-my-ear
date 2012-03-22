package ro.btanase.chordlearning;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Test {

  /**
   * @param args
   * @throws IOException 
   */
  public static void main(String[] args) throws IOException {

    final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(10);
    
    Runnable beeper = new Runnable() {
      
      @Override
      public void run() {
        System.out.println("Beeeep");
//        try {
//          Thread.sleep(2000);
//        } catch (InterruptedException e) {
//          // TODO Auto-generated catch block
//          e.printStackTrace();
//        }
      }
    };

    final ScheduledFuture<?> beeperHandle = scheduler.scheduleAtFixedRate(beeper, 0, 500, TimeUnit.MILLISECONDS);
    
    scheduler.schedule(new Runnable() {
      
      @Override
      public void run() {
        beeperHandle.cancel(true);
        
      }
    }, 60 * 60, TimeUnit.SECONDS);
    
    System.in.read();
  }

}
