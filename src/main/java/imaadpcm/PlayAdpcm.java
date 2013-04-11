
package imaadpcm;

import java.io.File;
import java.io.InputStream;
import java.io.FileInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.SourceDataLine;

public class PlayAdpcm {
	/* Play count samples of an ADPCM stream using the default JavaSound device.*/
	public static void playAdpcm( InputStream input, int samplingRate, int count ) throws Exception {
		final int BUF_SAMPLES = 16384;
		AudioFormat audioFormat = new AudioFormat( samplingRate, 16, 2, true, false );
		SourceDataLine sourceLine = AudioSystem.getSourceDataLine( audioFormat );
		sourceLine.open();
		try {
			sourceLine.start();
			ImaAdpcm imaAdpcm = new ImaAdpcm();
			byte[] buffer = new byte[ BUF_SAMPLES * 4 ];
			while( count > 0 ) {
				int samples = count > BUF_SAMPLES ? BUF_SAMPLES : count;
				imaAdpcm.decode( input, buffer, samples );
				sourceLine.write( buffer, 0, samples * 4 );
				count -= samples;
			}
			sourceLine.drain();
		} finally {
			sourceLine.close();
		}
	}

	public static void main( String[] args ) {
		if( args.length != 2 ) {
			System.out.println( "IMA ADPCM player" );
			System.out.println( "Usage: java " + PlayAdpcm.class.getName() + " input.ima [sampling rate]" );
			System.exit( 0 );
		}
		try {
			String imaFileName = args[ 0 ];
			int samplingRate = Integer.parseInt( args[ 1 ] );
			System.out.println( "Playing " + imaFileName );
			System.out.println( "Press Ctrl-C to exit." );
			File file = new File( imaFileName );
			int length = ( int ) file.length();
			FileInputStream input = new FileInputStream( file );
			playAdpcm( input, samplingRate, length );
		} catch( Exception e ) {
			//e.printStackTrace();
			System.err.println( e );
			System.exit( 1 );
		}
	}
}
