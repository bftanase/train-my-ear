package ro.btanase.mediaplayer;

import imaadpcm.ImaAdpcm;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.Mixer.Info;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;

import org.apache.log4j.Logger;

import ro.btanase.chordlearning.services.UserData;

import com.google.inject.Inject;
import com.google.inject.Singleton;

@Singleton
public class MediaPlayer {

  private static MediaPlayer instance;
  
  public static final int NO_SLOTS = 5;
  
  // private Boolean shouldStop = false;
  private boolean shouldStop;
  private AudioInputStream[] memoryInputStreamSlots = new AudioInputStream[5];
  AudioFormat format;
  private Logger log = Logger.getLogger(getClass());
  private SourceDataLine sourceLine;
  @Inject
  private UserData userData;
  
  private volatile String filename = "";
  
  private volatile Clip clip;
  
  private final AudioFormat FORMAT = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, (16 / 8) * 2, 44100, false);

  public MediaPlayer() {
    format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, 44100, 16, 2, (16 / 8) * 2, 44100, false);
    
    try {
      // in OpenJDK the default clip obtained by AudioSystem.getClip() does not support our needed format
      // why? I have no idea. "default" seem to work fine on some tests
      Info[] mixers = AudioSystem.getMixerInfo();
      Info defaultMixer = null;
      for (Info info : mixers) {
        if (info.getName().equals("default [default]")) {
          defaultMixer = info;
          break;
        }
        log.debug("!!!! mixer: " + info.getName());
      }
      
      clip = AudioSystem.getClip(defaultMixer);
      
      log.debug("!!!!!!!!!!!!! format: " + clip.getFormat());
    } catch (LineUnavailableException e) {
      throw new RuntimeException(e);
    }
  }

  public void playImaFile(String fileName, final IMPCallback callback) {
    if (fileName.equals(this.filename)) {
      log.debug("Same filename. Stopping and draining");
      clip.stop();
      clip.drain();
      
      clip.setFramePosition(0);
      log.debug("Same filename. Playing...");
      clip.start();
    } else {
      
      if (clip.isOpen()) {
        clip.stop();
        clip.drain();
        
        clip.close();
      }
      
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
        
        // for some unkown reason the decoded PCM stream has a few miliseconds of "silence" in the beggining
        // we need to get rid of that silence, cause it will cause timing problems
        int silenceOffset = AudioUtils.findSilenceEndOffset(bArr, 50);
        bArr = Arrays.copyOfRange(bArr, silenceOffset, bArr.length);    
        
        clip.open(FORMAT, bArr, 0, bArr.length);
        
        clip.start();
        this.filename = fileName;
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
    
//    final File file = new File(userData.getMediaFolder() + File.separator + fileName);
//
//    Thread thread = new Thread(new Runnable() {
//      @Override
//      public void run() {
//        try {
//          final int BUF_SAMPLES = 16384;
//          // final int BUF_SAMPLES = 128;
//          AudioFormat audioFormat = new AudioFormat(44100, 16, 2, true, false);
//          sourceLine = AudioSystem.getSourceDataLine(audioFormat);
//          sourceLine.open();
//          FileInputStream input = null;
//          try {
//            int count = (int) file.length();
//            sourceLine.start();
//            ImaAdpcm imaAdpcm = new ImaAdpcm();
//            byte[] buffer = new byte[BUF_SAMPLES * 4];
//            input = new FileInputStream(file);
//            while (count > 0 && shouldStop == false && sourceLine.isOpen()) {
//              int samples = count > BUF_SAMPLES ? BUF_SAMPLES : count;
//              imaAdpcm.decode(input, buffer, samples);
//              sourceLine.write(buffer, 0, samples * 4);
//              count -= samples;
//            }
//            sourceLine.drain();
//          } finally {
//            try{
//              input.close();
//              sourceLine.close();
//            }catch (Exception e) {
//              log.error("Error closing source lines", e );
//            }
//          }
//
//          callback.onStop();
//        } catch (Exception e) {
//          throw new RuntimeException(e);
//        }
//
//      }
//    });
//
//    callback.onPlay();
//    shouldStop = false;
//    thread.start();
  }

  public void stopPlayback() {
    if (clip.isOpen()) {
      clip.stop();
      clip.drain();
      
      clip.setFramePosition(0);
    }

  }

  public void stopRecording() {
    shouldStop = true;
  }
  
  
  public void playFromMemory(final IMPCallback callback, final int slot) {

    Thread thread = new Thread(new Runnable() {

//      SourceDataLine line;

      public void run() {

        final int bufSize = 16384;

        // make sure we have something to play
        if (memoryInputStreamSlots[slot] == null) {
          throw new RuntimeException("Nothing to play!");
        }
        // reset to the beginnning of the stream
        try {
          memoryInputStreamSlots[slot].reset();
        } catch (Exception e) {
          // throw new RuntimeException("Unable to reset stream!", e);
          log.debug(e);
        }

        // define the required attributes for our line,
        // and make sure a compatible line is supported.

        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        if (!AudioSystem.isLineSupported(info)) {
          throw new RuntimeException("Line matching " + info + "not supported");
        }

        // get and open the source data line for playback.

        try {
          sourceLine = (SourceDataLine) AudioSystem.getLine(info);
          sourceLine.open(format, bufSize);
        } catch (LineUnavailableException ex) {
          throw new RuntimeException("Unable to open the line: ", ex);
        }

        // play back the captured audio data

        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = sourceLine.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead = 0;

        // start the source data line
        sourceLine.start();

        while (shouldStop != true) {
          try {
            if ((numBytesRead = memoryInputStreamSlots[slot].read(data)) == -1) {
              break;
            }
            int numBytesRemaining = numBytesRead;
            while (numBytesRemaining > 0 && sourceLine.isOpen()) {
              numBytesRemaining -= sourceLine.write(data, 0, numBytesRemaining);
            }
          } catch (Exception ex) {
            log.error("Error playing stream", ex);
            throw new RuntimeException("Error playing stream", ex);
          }
        }
        // we reached the end of the stream. let the data play out, then
        // stop and close the line.
        sourceLine.drain();
        sourceLine.stop();
        sourceLine.close();
        sourceLine = null;
        callback.onStop();
      }
    });
    shouldStop = false;
    callback.onPlay();
    thread.start();
  } // End class Playback

  /**
   * Reads data from the input channel and writes to the output stream
   */
  public void recordToMemoryBuffer(final IMPCallback callback, final int slot) {

    Thread thread = new Thread(new Runnable() {
      public void run() {

        double duration = 0;
        memoryInputStreamSlots[slot] = null;
        TargetDataLine line;

        // define the required attributes for our line,
        // and make sure a compatible line is supported.

        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);

        if (!AudioSystem.isLineSupported(info)) {
          throw new RuntimeException("Line not supported!" + info.toString());
        }

        // get and open the target data line for capture.
        try {
          line = (TargetDataLine) AudioSystem.getLine(info);
          line.open(format, line.getBufferSize());
        } catch (LineUnavailableException ex) {
          throw new RuntimeException("Line not available!", ex);
        } catch (Exception ex) {
          throw new RuntimeException("Line not available!", ex);
        }

        // play back the captured audio data
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int frameSizeInBytes = format.getFrameSize();
        int bufferLengthInFrames = line.getBufferSize() / 8;
        int bufferLengthInBytes = bufferLengthInFrames * frameSizeInBytes;
        byte[] data = new byte[bufferLengthInBytes];
        int numBytesRead;

        line.start();

        while (shouldStop != true) {
          if ((numBytesRead = line.read(data, 0, bufferLengthInBytes)) == -1) {
            break;
          }
          out.write(data, 0, numBytesRead);
        }

        // we reached the end of the stream. stop and close the line.
        line.stop();
        line.close();
        line = null;

        // stop and close the output stream
        try {
          out.flush();
          out.close();
        } catch (IOException ex) {
          throw new RuntimeException(ex);
        }

        // load bytes into the audio input stream for playback

        byte audioBytes[] = out.toByteArray();
        ByteArrayInputStream bais = new ByteArrayInputStream(audioBytes);
        memoryInputStreamSlots[slot] = new AudioInputStream(bais, format, audioBytes.length / frameSizeInBytes);

        long milliseconds = (long) ((memoryInputStreamSlots[slot].getFrameLength() * 1000) / format.getFrameRate());
        duration = milliseconds / 1000.0;

        try {
          memoryInputStreamSlots[slot].reset();
        } catch (Exception ex) {
          throw new RuntimeException(ex);
        }

        callback.onStop();
      }
    });

    shouldStop = false;
    callback.onPlay();
    thread.start();
  }

  public void saveToFile(String name, int slot) {

    if (memoryInputStreamSlots[slot] == null) {
      throw new RuntimeException("No audio to save");
    }
    // reset to the beginnning of the captured data
    try {
      memoryInputStreamSlots[slot].reset();
    } catch (Exception e) {
      log.debug("Error saving file", e);
      throw new RuntimeException("Can't reset stream");
    }

    FileOutputStream fos = null;
    try {
      // if (AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE,
      // file) == -1) {
      // throw new IOException("Problems writing to file");
      // }
      ByteArrayOutputStream out = new ByteArrayOutputStream();
      if (AudioSystem.write(memoryInputStreamSlots[slot], AudioFileFormat.Type.WAVE, out) == -1) {
        throw new IOException("Problems writing to file");
      }

      fos = new FileOutputStream(name);
      ImaAdpcm.convertWavToAdpcm(new ByteArrayInputStream(out.toByteArray()), fos);

    } catch (Exception ex) {
      throw new RuntimeException(ex);
    } finally {
      try {
        fos.close();
      } catch (Exception e) {
        log.debug("unable to close file ", e);
      }
    }
  }

  public void emptyRecordBuffer(int slot) {
    try{
      memoryInputStreamSlots[slot].close();
    }catch(Exception e){}
    
    memoryInputStreamSlots[slot] = null;
  }

  public boolean isBufferEmpty(int slot) {
    if (memoryInputStreamSlots[slot] == null) {
      return true;
    } else {
      return false;
    }
  }

  public void setMemoryAudioStream(File file, int slot) {
    try {
      AudioFormat fileFormat = AudioSystem.getAudioFileFormat(file).getFormat();
      AudioInputStream waveFileStream = AudioSystem.getAudioInputStream(file);

      log.debug("frame lenght: " + waveFileStream.getFrameLength() * fileFormat.getFrameSize());
      byte[] byteArr = new byte[(int) (waveFileStream.getFrameLength() * fileFormat.getFrameSize())];

      int count = waveFileStream.read(byteArr);
      log.debug("read to bytearr: " + count);

      ByteArrayInputStream bais = new ByteArrayInputStream(byteArr);

      waveFileStream.close();

      waveFileStream = new AudioInputStream(bais, fileFormat, count / fileFormat.getFrameSize());

      memoryInputStreamSlots[slot] = AudioSystem.getAudioInputStream(format, waveFileStream);

      waveFileStream.close();

    } catch (Exception e) {
      throw new RuntimeException("Unable to load wav file ", e);
    }
  }
}
