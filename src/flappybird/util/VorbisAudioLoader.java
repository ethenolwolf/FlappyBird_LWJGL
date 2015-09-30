package flappybird.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBVorbisInfo;

import static org.lwjgl.openal.AL10.*;
import static org.lwjgl.system.MemoryUtil.*;
import static org.lwjgl.stb.STBVorbis.*;

public class VorbisAudioLoader {
	private static int BUFFER_SIZE = 1024 * 4;
	
	public final ByteBuffer data;
	public final int format;
	public final int samplerate;
	
	final int channels;
	
	public VorbisAudioLoader(String filename) {
		byte[] buffer;
		buffer = loadAllBytes(filename);
		ByteBuffer byteBuffer = BufferUtils.createAlignedByteBufferCacheLine(buffer.length);
		byteBuffer.put(buffer).flip();
		
		IntBuffer error = BufferUtils.createIntBuffer(1);
		long handle = stb_vorbis_open_memory(byteBuffer, error, null);
		if(handle == NULL) {
			System.err.println("Failed to open Ogg Vorbis file. Error: " + error.get(0));
			System.exit(1);
		}
		
		STBVorbisInfo info = getInfo(handle);
		
		this.channels = info.getChannels();
		this.samplerate = info.getSampleRate();
		this.format = getFormat(this.channels);
		
		BUFFER_SIZE = stb_vorbis_stream_length_in_samples(handle);
		this.data = BufferUtils.createByteBuffer(BUFFER_SIZE * 2);
		
		int samples = 0;
		while(samples < BUFFER_SIZE) {
			this.data.position(samples);
			int samplesPerChannel = stb_vorbis_get_samples_short_interleaved(handle, this.channels, this.data, BUFFER_SIZE-samples);
			if(samplesPerChannel == 0) {
				break;
			}
			
			samples += samplesPerChannel * this.channels;
		}
		
		this.data.position(0);
	}

	private int getFormat(int channels) {
		switch(channels) {
		case 1:
			return AL_FORMAT_MONO16;
		case 2:
			return AL_FORMAT_STEREO16;
		default:
			throw new UnsupportedOperationException("Unsupported number of channels: " + channels);	
		}
	}

	private STBVorbisInfo getInfo(long handle) {
		System.out.println("stream length, samples: " + stb_vorbis_stream_length_in_samples(handle));
		System.out.println("stream length, seconds: " + stb_vorbis_stream_length_in_seconds(handle));
		
		STBVorbisInfo info = new STBVorbisInfo();
		stb_vorbis_get_info(handle, info.buffer());
		
		System.out.println("channels: " + info.getChannels());
		System.out.println("sample rate: " + info.getSampleRate());
		System.out.println("max frame size: " + info.getMaxFrameSize());
		System.out.println("setup memory required: " + info.getSetupMemoryRequired());
		System.out.println("setup temp memory required: " + info.getSetupTempMemoryRequired());
		System.out.println("temp memory required:" + info.getTempMemoryRequired());
		
		return info;
	}

	private byte[] loadAllBytes(String filename) {
		byte[] buffer = null;
		try {
			buffer = Files.readAllBytes(new File(filename).toPath());
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return buffer;
	}

}
