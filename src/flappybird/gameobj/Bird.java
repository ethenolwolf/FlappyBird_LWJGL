package flappybird.gameobj;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import static org.lwjgl.openal.AL10.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import flappybird.gamesys.GameManager;
import flappybird.util.Input;
import flappybird.util.WaveAudioLoader;

public class Bird extends GameObject {
	
	public boolean hit = false;
	
	final float GRAVITY = 9.8f;
	
	int vao;
	
	final float WIDTH = 1.6f;
	final float HEIGHT = WIDTH / 1.42f;
	
	float yVelocity = 0.0f;
	
	int audioSourceID;
	int audioBufferFlapID;
	int audioBufferGameOverID;
	
	boolean playingGameOverAudio = false;
	
	public Bird() {
		super();
		
		float[] vertices = {
				-WIDTH/2, HEIGHT/2, 2.0f,
				-WIDTH/2, -HEIGHT/2, 2.0f,
				WIDTH/2, -HEIGHT/2, 2.0f,
				WIDTH/2, HEIGHT/2, 2.0f,
		};
		
		float[] uv = {
			0.0f, 0.0f,
			0.0f, 1.0f,
			1.0f, 1.0f,
			1.0f, 0.0f,
		};
		
		int[] index = {
			0, 1, 3,
			1, 2, 3,
		};
		
		FloatBuffer verticesFloatBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesFloatBuffer.put(vertices).flip();
		
		FloatBuffer uvFloatBuffer = BufferUtils.createFloatBuffer(uv.length);
		uvFloatBuffer.put(uv).flip();
		
		IntBuffer indexIntBuffer = BufferUtils.createIntBuffer(index.length);
		indexIntBuffer.put(index).flip();
		
		vao = genVertexArrays();
		glBindVertexArray(vao);
		
		
		// vertex
		int vbo = genBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vbo);
		glBufferData(GL_ARRAY_BUFFER, verticesFloatBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		// uv
		int uvID = genBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvID);
		glBufferData(GL_ARRAY_BUFFER, uvFloatBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		// index
		int indexID = genBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexIntBuffer, GL_STATIC_DRAW);;
		
		glBindVertexArray(0);
		
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
		
		position.x = 0;
		
		// OpenAL
		WaveAudioLoader sndFlap = new WaveAudioLoader("res/sounds/Flap.wav");
//		VorbisAudioLoader sndFlap = new VorbisAudioLoader("res/sounds/Flap.ogg");
		
		WaveAudioLoader sndGameOver = new WaveAudioLoader("res/sounds/GameOver.wav");
//		VorbisAudioLoader sndGameOver = new VorbisAudioLoader("res/sounds/GameOver.ogg");
		
		audioBufferFlapID = alGenBuffers();
		alBufferData(audioBufferFlapID, sndFlap.format, sndFlap.data, sndFlap.samplerate);
		
		audioBufferGameOverID = alGenBuffers();
		alBufferData(audioBufferGameOverID, sndGameOver.format, sndGameOver.data, sndGameOver.samplerate);
		
		audioSourceID = alGenSources();
		alSource3f(audioBufferFlapID, AL_POSITION, 0.0f, 0.0f, 0.0f);
		alSourcei(audioSourceID, AL_BUFFER, audioBufferFlapID);
	}

	@Override
	public void render(double deltaTime) {
		texture.bind();
		
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		
		texture.unbind();
		shader.unbind();
	}

	@Override
	public void update(double deltaTime) {
		
		if(!GameManager.instance.isGameOver()) {
			if(Input.isKeyPressed(GLFW_KEY_SPACE)) {
				// play sound 
				alSourcePlay(audioSourceID);
				
				yVelocity = 6.0f;
			}	
		} else {
			if(!playingGameOverAudio) {
				alSourceStop(audioSourceID);
				alSourcei(audioSourceID, AL_BUFFER, audioBufferGameOverID);
				alSourcePlay(audioSourceID);
				
				playingGameOverAudio = true;
			}
		}
		
		position.y += yVelocity * deltaTime;
		yVelocity -= GRAVITY * deltaTime;
		
		ml_matrix.rotationZ((float)Math.toRadians(yVelocity*3.6f));
		ml_matrix.setTranslation(position);
		
		if(position.y > 7.5f || position.y < -7.5f) {
			hit = true;
		}
		
		if(position.y < -8.0f) {
			GameManager.instance.setCanRestart(true);
		}
	}
	
	@Override
	public void cleanUp() {
		super.cleanUp();
		alDeleteBuffers(audioBufferFlapID);
		alDeleteBuffers(audioBufferGameOverID);
		alDeleteSources(audioSourceID);
	}

}
