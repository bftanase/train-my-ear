package ro.btanase.chordlearning;

import imaadpcm.ImaAdpcm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineUnavailableException;

import com.sun.xml.internal.messaging.saaj.util.ByteInputStream;
import com.sun.xml.internal.messaging.saaj.util.ByteOutputStream;

public class JavaSoundExperiment {

  /**
   * @param args
   * @throws LineUnavailableException 
   * @throws IOException 
   * @throws FileNotFoundException 
   * @throws InterruptedException 
   */
  public static void main(String[] args) throws LineUnavailableException, FileNotFoundException, IOException, InterruptedException {
    
    Thread th1 = new Thread(new Runnable() {
      
      @Override
      public void run() {
        System.out.println("T1 start");
        printDummyData("T1");
//        try {
//          playIma("c:/Users/Bogdan/.gcet/application_data/media/G.ima");
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
        
      }
    });
    
    Thread th2 = new Thread(new Runnable() {
      
      @Override
      public void run() {
        System.out.println("T2 start");
        printDummyData("T2");        
//        try {
//          playIma("c:/Users/Bogdan/.gcet/application_data/media/A.ima");
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
        
      }
    });

    Thread th3 = new Thread(new Runnable() {
      
      @Override
      public void run() {
        System.out.println("T3 start");
        printDummyData("T3");        
//        try {
//          playIma("c:/Users/Bogdan/.gcet/application_data/media/D.ima");
//        } catch (Exception e) {
//          e.printStackTrace();
//        }
        
      }
    });
    int delay = 200;
    
    th1.start();
    Thread.sleep(delay);
    th2.start();
    Thread.sleep(delay);
    th3.start();
    
    
//    playIma("c:/Users/Bogdan/.gcet/application_data/media/A.ima", 6.0206f);
//    Thread.sleep(500);
//    playIma("c:/Users/Bogdan/.gcet/application_data/media/D.ima", -5f);
//    Thread.sleep(500);
//    playIma("c:/Users/Bogdan/.gcet/application_data/media/G.ima", 5f);
//    System.in.read();
  }

  private static void playIma(String filename, float gain) throws IOException, LineUnavailableException{
    AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, (16 / 8) * 2, 44100, false);
//    ImaAdpcm ima = new ImaAdpcm();
    
    File file = new File(filename);
    
    if (!file.exists()){
      throw new IllegalStateException("File does not exists");
    }
    
    ByteOutputStream bos  = new ByteOutputStream();
    
    final int BUF_SAMPLES = 16384;
    
    int count = (int) file.length();
    ImaAdpcm imaAdpcm = new ImaAdpcm();
    byte[] buffer = new byte[BUF_SAMPLES * 4];
    FileInputStream input = new FileInputStream(file);
    while (count > 0) {
      int samples = count > BUF_SAMPLES ? BUF_SAMPLES : count;
      imaAdpcm.decode(input, buffer, samples);
      bos.write(buffer);
      count -= samples;
    }    
    
    ByteInputStream bis = new ByteInputStream();
    bis.setBuf(bos.getBytes());
    
    System.out.println("count: " + bis.getCount());
//    AudioInputStream ais = new AudioInputStream(bis, format, bis.getCount()/4);
    
    Clip clip = AudioSystem.getClip();
//    clip.open(ais);
    clip.open(format, bis.getBytes(), 0, bis.getCount());
    FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    System.out.println("maxGain: " + control.getMaximum());
    System.out.println("minGain: " + control.getMinimum());
    control.setValue(gain);

    clip.start();
    clip.drain();
    
  }

  private static void playIma(String filename) throws IOException, LineUnavailableException{
    playIma(filename, 0);
  }
  
  private static void printDummyData(String label){
    for(int i=0; i<1000; i++){
      for (int j=0; j<10000000;j++){int x = 1+1; }
      System.out.println(System.currentTimeMillis() + " " + label + " " +i);
    }
  }
}
