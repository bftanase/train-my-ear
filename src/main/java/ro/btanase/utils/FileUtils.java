package ro.btanase.utils;

import org.apache.log4j.Logger;

/**
 * @author b.tanase
 *
 */
public class FileUtils {
  private static Logger log = Logger.getLogger(FileUtils.class);

  /**
   * converts the input string to a file name safe string;
   * Converts the following characters to their corresponding escaped html code:<br/>
   * &#47; &#92; &#63; &#37; &#42; &#58; &#124; &#34; &#60; &#62;
   * @param fileName
   * @return
   */
  public static String safeName(String fileName){
    String safeName = null;
    // not the most efficient, but ...
    safeName = fileName.replaceAll("/", unicodeEncode('/'));
    safeName = safeName.replaceAll("[\\\\]", unicodeEncode('\\'));
    safeName = safeName.replaceAll("[?]", unicodeEncode('?'));
    safeName = safeName.replaceAll("[%]", unicodeEncode('%'));
    safeName = safeName.replaceAll("[*]", unicodeEncode('*'));
    safeName = safeName.replaceAll("[:]", unicodeEncode(':'));
    safeName = safeName.replaceAll("[|]", unicodeEncode('|'));
    safeName = safeName.replaceAll("[\"]", unicodeEncode('\"'));
    safeName = safeName.replaceAll("[<]", unicodeEncode('<'));
    safeName = safeName.replaceAll("[>]", unicodeEncode('>'));
    log.debug("Output string: " + safeName);
    return safeName;
  }
  
  public static String unicodeEncode(char c){
    String result = null;
    result = "#" + (int)c + "#";
    return result;
  }

//  public static String htmlEncode(char c){
//    String result = null;
//    result = "&#" + (int)c + ";";
//    return result;
//  }

  
}
