package ro.btanase.mediaplayer;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;

public class AudioUtils {

  /**
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException {
    FileInputStream fis = new FileInputStream("d:/temp/C.ima.RAW");
    byte[] data = loadStreamToMemory(fis);

    fis.read(data);

    fis.close();
    int silenceOffset = findSilenceEndOffset(data, 50);

    byte[] trimmedData = Arrays.copyOfRange(data, silenceOffset, data.length);
    

    FileOutputStream fos = new FileOutputStream("d:/temp/final_audio.raw");
    fos.write(trimmedData);
    fos.close();
    
    System.out.println("silence offset: " + silenceOffset);


  }

  /**
   * Scans a PCM 44100 stereo stream for beggining silence and returns the offset where the "silence" ends.
   * 
   * What exactly is "silence" is determined by threshold parameter, which represents a 16 bit sample value.
   * 
   * @param data
   * @param threshold
   * @return
   */
  public static int findSilenceEndOffset(byte[] data, int threshold) {

    for (int i = 0; i < data.length; i += 4) {
      int avgL = 0;
      int avgR = 0;

      avgL = averrageChSamples(i, i + (100 * 4), data).avgLeft;
      avgR = averrageChSamples(i, i + (100 * 4), data).avgRight;

      if ((avgL + avgR) / 2 > threshold) {
        return i;
      }
    }

    return -1;
  }

  
  /**
   * <p>Does an absolute value sample average for each channel in a PCM 44100 stereo stream</p>
   * 
   * <p>total scanned lenght must be a multiple of 4</p>
   * 
   * <p>each 4 bytes represents:</>
   * <ul>
   * <li>first 2 bytes -> 16 bit signed short value for left channel</li>
   * <li>next 2 bytes -> 16 bit signed short value for right channel</li>
   * </ul> 
   * 
   * @param offsetStart - byte offset where to start avg
   * @param offsetEnd - byte offset where to end (inclusive)
   * @param data - data to scan
   * @return container class for both channels average
   */
  public static AvgChSamples averrageChSamples(int offsetStart, int offsetEnd, byte[] data) {
    ByteBuffer bb = ByteBuffer.allocate(4);
    bb.order(ByteOrder.LITTLE_ENDIAN);

    AvgChSamples avgSum = new AvgChSamples();

    avgSum.avgLeft = 0;
    avgSum.avgRight = 0;
    int count = 0;

    for (int i = offsetStart; i < offsetEnd; i += 4) {
      bb.clear();
      bb.put(data[i]);
      bb.put(data[i + 1]);

      avgSum.avgLeft += Math.abs(bb.getShort(0));
      avgSum.avgRight += Math.abs(bb.getShort(2));
      count++;
    }
    avgSum.avgLeft = avgSum.avgLeft / count;
    avgSum.avgRight = avgSum.avgRight / count;

    return avgSum;
  }

  /**
   * load and input stream to a byte array.
   * @param stream
   * @return
   * @throws IOException
   */
  public static byte[] loadStreamToMemory(InputStream stream) throws IOException {
    byte[] buffer = new byte[2048];
    int count = 0;

    ByteArrayOutputStream baous = new ByteArrayOutputStream();

    while ((count = stream.read(buffer)) != -1) {
      baous.write(buffer, 0, count);
    }

    return baous.toByteArray();

  }

  /**
   * class to store average values for each channel in a stereo stream
   * @author bftanase@gmail.com
   *
   */
  private static class AvgChSamples {
    int avgLeft;
    int avgRight;
  }

}
