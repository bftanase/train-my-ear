
package imaadpcm;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

/*
	A simple, good-enough-in-1995 quality audio compression algorithm.
	The basic algorithm encodes the difference between each sample into
	4 bits, increasing or decreasing the quantization step size depending on the
	size of the difference. A 16-bit stereo sample is neatly packed into 1 byte.
	This class implements an improved linear-extrapolation predictor, which
	results in less noise under most circumstances.
*/
public class ImaAdpcmPlus {
	public static final String VERSION = "20101025 (c)2010 mumart@gmail.com";

	private static final byte[] stepIdxTable = {
		8, 6, 4, 2, -1, -1, -1, -1, -1, -1, -1, -1, 2, 4, 6, 8
	};

	private static final short[] stepTable = { 
		7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 
		19, 21, 23, 25, 28, 31, 34, 37, 41, 45, 
		50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 
		130, 143, 157, 173, 190, 209, 230, 253, 279, 307,
		337, 371, 408, 449, 494, 544, 598, 658, 724, 796,
		876, 963, 1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 
		2272, 2499, 2749, 3024, 3327, 3660, 4026, 4428, 4871, 5358,
		5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487, 12635, 13899, 
		15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767 
	};

	private int lStepIdx, rStepIdx, lPrevious, rPrevious, lPredicted, rPredicted;

	/*
		Reset the ADPCM predictor.
		Call when encoding or decoding a new stream.
	*/
	public void reset() {
		lStepIdx = rStepIdx = 0;
		lPrevious = rPrevious = 0;
		lPredicted = rPredicted = 0;
	}

	/*
		Encode count samples of 16-bit stereo
		little-endian PCM audio data to count bytes of ADPCM.
		The output buffer is used for temporary data and must be at least count * 4 in size.
	*/
	public void encode( InputStream input, byte[] output, int count ) throws IOException {
		readFully( input, output, 0, count * 4 );
		int inputIdx = 0, outputIdx = 0;
		while( outputIdx < count ) {
			int lSam  = ( output[ inputIdx++ ] & 0xFF ) | ( output[ inputIdx++ ] << 8 );
			int rSam  = ( output[ inputIdx++ ] & 0xFF ) | ( output[ inputIdx++ ] << 8 );
			int lStep = stepTable[ lStepIdx ];
			int rStep = stepTable[ rStepIdx ];
			int lCode = ( ( lSam - lPredicted ) * 4 + lStep * 8 ) / lStep;
			int rCode = ( ( rSam - rPredicted ) * 4 + rStep * 8 ) / rStep;
			if( lCode > 15 ) lCode = 15;
			if( rCode > 15 ) rCode = 15;
			if( lCode <  0 ) lCode =  0;
			if( rCode <  0 ) rCode =  0;
			int lCurrent = lPredicted + ( ( lCode * lStep ) >> 2 ) - ( ( 15 * lStep ) >> 3 );
			int rCurrent = rPredicted + ( ( rCode * rStep ) >> 2 ) - ( ( 15 * rStep ) >> 3 );
			if( lCurrent >  32767 ) lCurrent =  32767;
			if( rCurrent >  32767 ) rCurrent =  32767;
			if( lCurrent < -32768 ) lCurrent = -32768;
			if( rCurrent < -32768 ) rCurrent = -32768;
			lPredicted = lCurrent + lCurrent - lPrevious;
			rPredicted = rCurrent + rCurrent - rPrevious;
			lPrevious = lCurrent;
			rPrevious = rCurrent;
			lStepIdx += stepIdxTable[ lCode ];
			rStepIdx += stepIdxTable[ rCode ];
			if( lStepIdx > 88 ) lStepIdx = 88;
			if( rStepIdx > 88 ) rStepIdx = 88;
			if( lStepIdx <  0 ) lStepIdx =  0;
			if( rStepIdx <  0 ) rStepIdx =  0;
			output[ outputIdx++ ] = ( byte ) ( ( lCode << 4 ) | rCode );
		}
	}
	
	/*
		Decode count samples of ADPCM to 16-bit stereo little-endian PCM audio data.
	*/
	public void decode( InputStream input, byte[] output, int count ) throws IOException {
		readFully( input, output, count * 3, count );
		int inputIdx = count * 3, outputIdx = 0, outputEnd = count * 4;
		while( outputIdx < outputEnd ) {
			int lCode = output[ inputIdx++ ] & 0xFF;
			int rCode = lCode & 0xF;
			lCode = lCode >> 4;
			int lStep = stepTable[ lStepIdx ];
			int rStep = stepTable[ rStepIdx ];
			int lCurrent = lPredicted + ( ( lCode * lStep ) >> 2 ) - ( ( 15 * lStep ) >> 3 );
			int rCurrent = rPredicted + ( ( rCode * rStep ) >> 2 ) - ( ( 15 * rStep ) >> 3 );
			if( lCurrent >  32767 ) lCurrent =  32767;
			if( rCurrent >  32767 ) rCurrent =  32767;
			if( lCurrent < -32768 ) lCurrent = -32768;
			if( rCurrent < -32768 ) rCurrent = -32768;
			output[ outputIdx++ ] = ( byte )   lCurrent;
			output[ outputIdx++ ] = ( byte ) ( lCurrent >> 8 );
			output[ outputIdx++ ] = ( byte )   rCurrent;
			output[ outputIdx++ ] = ( byte ) ( rCurrent >> 8 );
			lPredicted = lCurrent + lCurrent - lPrevious;
			rPredicted = rCurrent + rCurrent - rPrevious;
			lPrevious = lCurrent;
			rPrevious = rCurrent;
			lStepIdx += stepIdxTable[ lCode ];
			rStepIdx += stepIdxTable[ rCode ];
			if( lStepIdx > 88 ) lStepIdx = 88;
			if( rStepIdx > 88 ) rStepIdx = 88;
			if( lStepIdx <  0 ) lStepIdx =  0;
			if( rStepIdx <  0 ) rStepIdx =  0;
		}
	}
	
	/*
		Convert a 16-bit stereo Wav stream to an IMA ADPCM stream.
	*/
	public static void convertWavToAdpcm( InputStream input, OutputStream output ) throws IOException {
		final int BUF_SAMPLES = 16384;
		byte[] buf = new byte[ BUF_SAMPLES * 4 ];
		ImaAdpcmPlus imaAdpcm = new ImaAdpcmPlus();
		int samples = readWav( input );
		while( samples > 0 ) {
			int count = samples > BUF_SAMPLES ? BUF_SAMPLES : samples;
			imaAdpcm.encode( input, buf, count );
			output.write( buf, 0, count );
			samples -= count;
		}
	}
	
	/*
		Read the header of a 16-bit stereo WAV file.
		The InputStream is positioned at the start of the data.
		The number of samples in the file are returned.
	*/
	public static int readWav( InputStream input ) throws IOException {
		/*
			CHAR[4] "RIFF"
			UINT32  Size of following data. Sample data length+36. Must be even.
			  CHAR[4] "WAVE"
				CHAR[4] "fmt "
				UINT32  PCM Header chunk size = 16
				  UINT16 0x0001 (PCM)
				  UINT16 NumChannels
				  UINT32 SampleRate
				  UINT32 BytesPerSec = samplerate*frame size
				  UINT16 frame Size (eg 4 bytes for stereo PCM16)
				  UINT16 BitsPerSample
				CHAR[4] "data"
				UINT32 Length of sample data.
				<Samples>
		*/
		if( !"RIFF".equals( readASCII( input, 4 ) ) ) throw new IOException( "RIFF header not found." );
		int dataSize = readInt32( input );
		if( !"WAVE".equals( readASCII( input, 4 ) ) ) throw new IOException( "WAVE header not found." );
		if( !"fmt ".equals( readASCII( input, 4 ) ) ) throw new IOException( "'fmt' header not found." );
		int chunkSize = readInt32( input );
		int format = readInt16( input );
		if( format != 1 ) throw new IOException( "Format is not PCM." );
		int channels = readInt16( input );
		if( channels != 2 ) throw new IOException( "Number of channels must be 2." );
		int sampleRate = readInt32( input );
		int bytesPerSec = readInt32( input );
		int frameSize = readInt16( input );
		if( frameSize != 4 ) throw new IOException( "Frame size must be 4." );
		int bits = readInt16( input );
		if( bits != 16 ) throw new IOException( "PCM data must be 16 bit." );
		if( !"data".equals( readASCII( input, 4 ) ) ) throw new IOException( "'data' header not found." );
		int dataLen = readInt32( input );
		return dataLen / 4;
	}

	/* Read a 16-bit little-endian unsigned integer from input.*/
	public static int readInt16( InputStream input ) throws IOException {
		return ( input.read() & 0xFF ) | ( ( input.read() & 0xFF ) << 8 );
	}

	/* Read a 32-bit little-endian signed integer from input.*/
	public static int readInt32( InputStream input ) throws IOException {
		return ( input.read() & 0xFF ) | ( ( input.read() & 0xFF ) << 8 )
			| ( ( input.read() & 0xFF ) << 16 ) | ( ( input.read() & 0xFF ) << 24 );
	}

	/* Return a String containing count characters of ASCII/ISO-8859-1 text from input. */
	public static String readASCII( InputStream input, int count ) throws IOException {
		byte[] chars = new byte[ count ];
		readFully( input, chars, 0, count );
		return new String( chars, "ISO-8859-1" );
	}

	/* Read no less than count bytes from input into the output array. */
	public static void readFully( InputStream input, byte[] output, int offset, int count ) throws IOException {
		int end = offset + count;
		while( offset < end ) {
			int read = input.read( output, offset, end - offset );
			if( read < 0 ) throw new java.io.EOFException();
			offset += read;
		}
	}

	public static void main( String[] args ) {
		try {
			if( args.length != 2 ) {
				System.out.println( "IMA ADPCM Converter " + VERSION );
				System.out.println( "Usage: java " + ImaAdpcmPlus.class.getName() + " input.wav output.ima" );
				System.exit( 0 );
			}
			String wavFileName = args[ 0 ];
			String imaFileName = args[ 1 ];
			System.out.println( "Converting " + wavFileName + " to " + imaFileName );
			InputStream input = new java.io.FileInputStream( wavFileName );
			OutputStream output = new java.io.FileOutputStream( imaFileName );
			try {
				convertWavToAdpcm( input, output );
			} finally {
				output.close();
			}
		} catch( Exception e ) {
			//e.printStackTrace();
			System.err.println( e );
			System.exit( 1 );
		}
	}
}
