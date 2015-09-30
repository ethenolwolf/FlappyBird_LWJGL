package flappybird.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.ShortBuffer;
import java.nio.file.Files;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.lwjgl.openal.AL10;

public class WaveAudioLoader {
	public final ByteBuffer data;
	public final int format;
	public final int samplerate;
	
	public void dispose() {
		data.clear();
	}
	
	public WaveAudioLoader(String filename) {
		byte[] buffer;
		buffer = loadAllByte(filename);
		
		AudioInputStream ais = null;
		try {
			ais = AudioSystem.getAudioInputStream(new BufferedInputStream(new ByteArrayInputStream(buffer)));
		} catch (UnsupportedAudioFileException | IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		AudioFormat audioFormat = ais.getFormat();
		int channels = 0;
		
		if(audioFormat.getChannels() == 1) {
			if(audioFormat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_MONO8;
			}
			else if(audioFormat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_MONO16;
			}
			else {
				assert false : "Illegal sample size";
			}
		}
		else if(audioFormat.getChannels() == 2) {
			if(audioFormat.getSampleSizeInBits() == 8) {
				channels = AL10.AL_FORMAT_STEREO8;
			}
			else if(audioFormat.getSampleSizeInBits() == 16) {
				channels = AL10.AL_FORMAT_STEREO16;
			}
			else {
				assert false : "Illegal sample size";
			}
		}
		else {
			assert false : "Only mono or stereo is supported";
		}
		
		ByteBuffer byteBuffer = null;
		try {
			int available = ais.available();
			if(available <= 0) {
				available = (int) (ais.getFormat().getChannels() * ais.getFrameLength() * ais.getFormat().getSampleRate() / 8);
			}
			
			byte[] buf = new byte[available];
			int read = 0;
			int total = 0;
			while((read = ais.read(buf, total, buf.length-total)) != -1 && total < buf.length) {
				total += read;
			}
			
			byteBuffer = convertAudioBytes(buf, audioFormat.getSampleSizeInBits()==16, audioFormat.isBigEndian() ? ByteOrder.BIG_ENDIAN : ByteOrder.LITTLE_ENDIAN); 
			
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		this.data = byteBuffer;
		this.format = channels;
		this.samplerate = (int)audioFormat.getFrameRate();
		
		try {
			ais.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private ByteBuffer convertAudioBytes(byte[] audio_bytes, boolean two_bytes_data, ByteOrder byteOrder) {
		ByteBuffer dest = ByteBuffer.allocateDirect(audio_bytes.length);
		dest.order(ByteOrder.nativeOrder());
		
		ByteBuffer src = ByteBuffer.wrap(audio_bytes);
		src.order(byteOrder);
		
		if(two_bytes_data) {
			ShortBuffer dest_short = dest.asShortBuffer();
			ShortBuffer src_short = src.asShortBuffer();
			while(src_short.hasRemaining()) {
				dest_short.put(src_short.get());
			}
		}
		else {
			while(src.hasRemaining()) {
				dest.put(src.get());
			}
		}
		
		dest.rewind();
		return dest;
	}

	private byte[] loadAllByte(String filename) {
		byte[] buffer = null;
		try {
			buffer = Files.readAllBytes(new File(filename).toPath());
		} catch(IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return buffer;
	}
	
}
