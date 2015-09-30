package flappybird.gameobj;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import flappybird.gamesys.GameManager;

public class Pipe extends GameObject {
	int vao;
	
	final float WIDTH = 2.4f;
	final float HEIGHT = WIDTH * 6.0f;
	
	private float speed = 4.6f; 
	private float span = 4.8f;
	private float spacing = 3.6f;
			
	private Vector3f[] posPipes;
	
	public Pipe() {
		super();
		
		float[] vertices = {
				-WIDTH/2, HEIGHT/2, 1.0f,
				-WIDTH/2, -HEIGHT/2, 1.0f,
				WIDTH/2, -HEIGHT/2, 1.0f,
				WIDTH/2, HEIGHT/2, 1.0f,
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
		
		
		position.x = 10.0f;
		position.y = -10.0f;
		
		posPipes = new Vector3f[8];
		
		for(int i=0; i < posPipes.length; i+=2) {
			posPipes[i] = new Vector3f();
			posPipes[i].x = position.x + span * i;
			posPipes[i].y = (float) (position.y + Math.random() * 5.0f);
			
			posPipes[i+1] = new Vector3f();
			posPipes[i+1].x = posPipes[i].x;
			posPipes[i+1].y = posPipes[i].y + spacing + HEIGHT;
		}
	}
	
	public void checkBirdHit(Bird bird) {
		float birdLX = bird.position.x - bird.WIDTH / 2.0f;
		float birdRX = bird.position.x + bird.WIDTH / 2.0f;
		float birdTY = bird.position.y + bird.HEIGHT / 2.0f;
		float birdBY = bird.position.y - bird.HEIGHT / 2.0f;
		
		for(int i=0; i < posPipes.length; i++) {
			float pipeLX = posPipes[i].x - WIDTH / 2.0f;
			float pipeRX = posPipes[i].x + WIDTH / 2.0f;
			float pipeTY = posPipes[i].y + HEIGHT / 2.0f;
			float pipeBY = posPipes[i].y - HEIGHT / 2.0f;
			
			if(birdRX < pipeLX || birdLX > pipeRX || birdTY < pipeBY || birdBY > pipeTY) {
				continue;
			}
			
			bird.hit = true;
			break;
		}
	}
	
	@Override
	public void render(double deltaTime) {
		texture.bind();
		
		glBindVertexArray(vao);
		
		for(int i=0; i < posPipes.length; i++) {
			ml_matrix.setTranslation(posPipes[i]);
			setUniformMatrix4fv("ml_matrix", ml_matrix);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
			
			ml_matrix.scale(1.0f, -1.0f, 1.0f);
				
		}
		
		texture.unbind();
		shader.unbind();
	}

	@Override
	public void update(double deltaTime) {
		ml_matrix.setTranslation(position);
		
		if(GameManager.instance.isGameOver()) {
			return;
		}
		
		for(int i=0; i < posPipes.length; i+=2) {
			posPipes[i].x -= speed * deltaTime;
			
			if(posPipes[i].x < -10.0f - WIDTH) {
				posPipes[i].x +=  span * posPipes.length;
				
				posPipes[i].y = (float) (position.y + Math.random() * 5.0f);
				posPipes[i+1].y = posPipes[i].y + spacing + HEIGHT;
			}
			
			posPipes[i+1].x = posPipes[i].x;
		}
		
	}

}
