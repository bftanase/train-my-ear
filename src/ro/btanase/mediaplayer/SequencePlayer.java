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

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.services.UserData;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SequencePlayer {

  private List<String> playList;

  private List<Clip> clipList = new ArrayList<Clip>();

  private Thread playbackThread;

  private boolean shouldStop;

  @Inject
  private UserData userData;

  Logger log = Logger.getLogger(getClass());

  public void setPlayList(List<String> playList) {
    this.playList = playList;

    clipList.clear();

    // should cache the Audio Clips
    for (String fileName : playList) {
      File file = null;
      FileInputStream input = null;
      try {
        AudioFormat format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
            44100, 16, 2, (16 / 8) * 2, 44100, false);
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


        // log.debug("count: " + bis.getCount());

        Clip clip = AudioSystem.getClip();
        clip.open(format, bos.toByteArray(), 0, bos.size());
        clipList.add(clip);
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

  public void play(final int delay) {
    if (playbackThread != null && playbackThread.isAlive()) {
      stop();
      try {
        log.debug("waiting for thread to terminate");
        playbackThread.join();
        log.debug("moving forward");
      } catch (InterruptedException e1) {/* do nothing */
      }
    }

    shouldStop = false;

    playbackThread = new Thread(new Runnable() {

      @Override
      public void run() {
        for (final Clip clip : clipList) {
          // clip.flush();
          if (shouldStop) {
            break;
          }
          clip.stop();
          clip.setFramePosition(0);
          // log.debug("playing clip " + clip.isOpen());
          clip.start();
          try {
            Thread.sleep(delay);
          } catch (InterruptedException e) { /* do nothing */
          }
        }

      }
    });

    playbackThread.start();

  }

  public void stop() {
    shouldStop = true;
    for (Clip clip : clipList) {
      clip.stop();
      clip.flush();
    }
    playbackThread.interrupt();
  }

  public static void main(String[] args) throws IOException,
      InterruptedException {
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

}
