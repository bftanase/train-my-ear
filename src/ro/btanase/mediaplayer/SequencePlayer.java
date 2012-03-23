package ro.btanase.mediaplayer;

import imaadpcm.ImaAdpcm;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.services.UserData;
import ro.btanase.chordlearning.services.WaveSoundMixer;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class SequencePlayer {

  private volatile List<String> playList;

  private volatile Clip clip;
  private volatile List<byte[]> audioBufferList = new ArrayList<byte[]>();


  private final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, (16 / 8) * 2, 44100, false);
  
  @Inject
  private UserData userData;

  Logger log = Logger.getLogger(getClass());

  public void setPlayList(List<String> playList, int interval) {
    
    this.playList = playList;
    audioBufferList.clear();
    clip.close();

    WaveSoundMixer wsm = new WaveSoundMixer();
    
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

        byte[] bArr = bos.toByteArray();
        audioBufferList.add(bArr);
        
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
    Collections.reverse(audioBufferList); // createAudioStream will reverse the order!
    
    try {
      byte[] finalAudioData = wsm.createAudioStreamFromBufferList(audioBufferList, interval);
      clip.open(FORMAT, finalAudioData, 0, finalAudioData.length);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  public void play() {
    log.debug("Playlist: " + playList.toString());
    clip.stop();
    clip.setFramePosition(0);
    clip.start();
  }

  public synchronized void stop() {
    clip.stop();
    clip.setFramePosition(0);
  }

  public static void main(String[] args) throws IOException, InterruptedException {
  }


  public List<String> getPlayList() {
    return playList;
  }

  public SequencePlayer() {
    try {
      clip = AudioSystem.getClip();
    } catch (LineUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  
}
