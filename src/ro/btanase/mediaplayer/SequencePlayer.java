package ro.btanase.mediaplayer;

import imaadpcm.ImaAdpcm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.services.UserData;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SequencePlayer {

  private volatile List<String> playList;

  private volatile List<Clip> clipList = new ArrayList<Clip>();
  private volatile List<Byte[]> clipDataList = new ArrayList<Byte[]>();

  private volatile Thread playbackThread;

  private volatile boolean shouldStop;

  private final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, (16 / 8) * 2, 44100, false);
  
  @Inject
  private UserData userData;

  Logger log = Logger.getLogger(getClass());

  public void setPlayList(List<String> playList) {
    this.playList = playList;

    // release sound resources
    for (Clip clip : clipList) {
      clip.close();
    }

    clipList.clear();

    // clear loaded data
    clipDataList.clear();
    
    
    // should cache the Audio Clips
    for (String fileName : playList) {
      File file = null;
      FileInputStream input = null;
      try {

        file = new File(userData.getMediaFolder() + File.separator + fileName);

        if (!file.exists()) {
          throw new IllegalStateException("File does not exists");
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream();

        final int BUF_SAMPLES = 16384;

        int count = (int) file.length();
        ImaAdpcm imaAdpcm = new ImaAdpcm();
        byte[] buffer = new byte[BUF_SAMPLES * 4];
        input = new FileInputStream(file);
        while (count > 0) {
          int samples = count > BUF_SAMPLES ? BUF_SAMPLES : count;
          imaAdpcm.decode(input, buffer, samples);
          bos.write(buffer);
          count -= samples;
        }

        // convert from byte[] to Byte[]
        byte[] bArr = bos.toByteArray();
        
        Byte[] byteArr = new Byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
          byteArr[i] = Byte.valueOf(bArr[i]);
        }
        
        clipDataList.add(byteArr);
        
      } catch (Exception e) {
        throw new RuntimeException(e);
      } finally {
        try {
          input.close();
        } catch (IOException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

      }

    }
  }

  public synchronized void play(final int delay) {

    log.debug("Playlist: " + playList.toString());
    
    if (playbackThread != null) {
      stop();
    }

    shouldStop = false;

    playbackThread = new Thread(new Runnable() {

      @Override
      public void run() {
        
        clipList.clear();
        
        for (Byte[] stream : clipDataList) {
          try{
            Clip clip = AudioSystem.getClip();
            
            // convert Byte[] to byte[]
            byte[] byteArr = new byte[stream.length];
            for (int i=0; i < stream.length; i++){
              byteArr[i] = stream[i];
            }
            
            clip.open(FORMAT, byteArr, 0, byteArr.length);
            clipList.add(clip);
          }catch (LineUnavailableException e) {
            throw new RuntimeException(e);
          }
        }
        
        
        
        for (int i=0; i<clipList.size(); i++){
          Clip clip = clipList.get(i);
          // clip.flush();
          if (shouldStop) {
            break;
          }
          clip.stop();
          clip.start();
          
          // maximum 2 simultaneous clips should play
          // release resources for the other clips
          if ((i - 2) >= 0){
            clipList.get(i-2).stop();
            clipList.get(i-2).close();
          }

          log.debug("frame length: " + clip.getFrameLength());
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) { /* do nothing */
          }
        }

      }
    });

    playbackThread.start();

  }

  public synchronized void stop() {
    shouldStop = true;
    for (Clip clip : clipList) {
      clip.stop();
      clip.flush();
      clip.close();

    }
    playbackThread.interrupt();
  }

  public static void main(String[] args) throws IOException, InterruptedException {
    SequencePlayer player = new SequencePlayer();
    List<String> playList = new ArrayList<String>();
    playList.add("c:/Users/Bogdan/.gcet/application_data/media/A.ima");
    playList.add("c:/Users/Bogdan/.gcet/application_data/media/D.ima");
    playList.add("c:/Users/Bogdan/.gcet/application_data/media/G.ima");
    // playList.add("Em");

    player.setPlayList(playList);

    // player.play();
    // Thread.sleep(1500);
    // player.play();
    // player.play();

    System.in.read();
  }

  // private void playIma(String filename, float gain) throws IOException,
  // LineUnavailableException{
  // AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
  // 44100, 16, 2, (16 / 8) * 2, 44100, false);
  // // ImaAdpcm ima = new ImaAdpcm();
  //
  // File file = new File(filename);
  //
  // if (!file.exists()){
  // throw new IllegalStateException("File does not exists");
  // }
  //
  // ByteOutputStream bos = new ByteOutputStream();
  //
  // final int BUF_SAMPLES = 16384;
  //
  // int count = (int) file.length();
  // ImaAdpcm imaAdpcm = new ImaAdpcm();
  // byte[] buffer = new byte[BUF_SAMPLES * 4];
  // FileInputStream input = new FileInputStream(file);
  // while (count > 0) {
  // int samples = count > BUF_SAMPLES ? BUF_SAMPLES : count;
  // imaAdpcm.decode(input, buffer, samples);
  // bos.write(buffer);
  // count -= samples;
  // }
  //
  // ByteInputStream bis = new ByteInputStream();
  // bis.setBuf(bos.getBytes());
  //
  // log.debug("count: " + bis.getCount());
  // // AudioInputStream ais = new AudioInputStream(bis, format,
  // bis.getCount()/4);
  //
  // Clip clip = AudioSystem.getClip();
  // // clip.open(ais);
  // clip.open(format, bis.getBytes(), 0, bis.getCount());
  // FloatControl control = (FloatControl)
  // clip.getControl(FloatControl.Type.MASTER_GAIN);
  // log.debug("maxGain: " + control.getMaximum());
  // log.debug("minGain: " + control.getMinimum());
  // control.setValue(gain);
  //
  // clipList.add(clip);
  // log.debug("start clip on " + (System.nanoTime()/1000000));
  // clip.start();
  // // clip.drain();
  //
  // }

  public List<String> getPlayList() {
    return playList;
  }

  private class MyClip implements Runnable {
    private Clip clip;
    private int stopPosition; // miliseconds

    public MyClip(Clip clip) {
      this.clip = clip;
    }

    @Override
    public void run() {
      clip.setFramePosition(0);
      clip.start();
      try {
        Thread.sleep(stopPosition);
      } catch (InterruptedException e) {
      }
      clip.stop();
      clip.close();
    }

    public void setStopPosition(int stopPosition) {
      this.stopPosition = stopPosition;
    }

    public void play() {
      Thread th = new Thread(this);
      th.start();
    }

  }

}
