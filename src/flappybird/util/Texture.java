package flappybird.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.file.Files;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.stb.STBImage.*;

public class Texture implements CleanUpAble {
	
	int textureID;
	
	int width;
	int height;
	ByteBuffer image;
	
	private final String textureFolder = "res/textures/";
	
	public Texture(String texture) {
		loadImage(texture);
		
		textureID = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureID);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);
		
		glTexParameterf(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		
		glBindTexture(GL_TEXTURE_2D, 0);
	}
	
	public int getTextureID() {
		return textureID;
	}
	
	public void bind() {
		glBindTexture(GL_TEXTURE_2D, textureID);
	}
	
	public void unbind() {
		glBindTexture(GL_TEXTURE_2D, 0);
	}

	private void loadImage(String texture) {
		ByteBuffer imgBuffer = loadImageBuffer(textureFolder + texture);
		
		IntBuffer w = BufferUtils.createIntBuffer(1);
		IntBuffer h = BufferUtils.createIntBuffer(1);
		IntBuffer comp = BufferUtils.createIntBuffer(1);
		
		if(stbi_info_from_memory(imgBuffer, w, h, comp) == 0) {
			throw new RuntimeException("Failed to read image info: " + stbi_failure_reason());
		}
		
		// decode image
		image = stbi_load_from_memory(imgBuffer, w, h, comp, 0);
		
		width = w.get(0);
		height = h.get(0);
	}

	private ByteBuffer loadImageBuffer(String texture) {
		ByteBuffer imgBuffer = null;
		try {
			byte[] buffer = Files.readAllBytes(new File(texture).toPath());
			imgBuffer = BufferUtils.createByteBuffer(buffer.length);
			imgBuffer.put(buffer).flip();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		
		return imgBuffer;
	}
	
	public void cleanUp() {
		glDeleteTextures(textureID);
	}

}
