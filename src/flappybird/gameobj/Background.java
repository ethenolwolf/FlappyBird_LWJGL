package flappybird.gameobj;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;

import flappybird.gamesys.GameManager;

public class Background extends GameObject {
	
	int vao;
	
	private double scrollSpeed = 3.0;
	
	public Background () {
		super();
		
		position.x = -10.0f;
		
		float[] vertex = {
				0.0f / 1.33f, 7.5f, 0.0f,
				0.0f / 1.33f, -7.5f, 0.0f,
				15.0f / 1.33f, -7.5f, 0.0f,
				15.0f / 1.33f, 7.5f, 0.0f,
		};
		
		float[] uv = {
				0.0f, 0.0f,
				0.0f, 1.0f,
				1.0f, 1.0f,
				1.0f, 0.0f,
		};
		
		int[] index =  {
				0, 1, 3,
				1, 2, 3,
		};
		
		FloatBuffer vertexFloatBuffer = BufferUtils.createFloatBuffer(vertex.length);
		vertexFloatBuffer.put(vertex).flip();
		
		FloatBuffer uvFloatBuffer = BufferUtils.createFloatBuffer(uv.length);
		uvFloatBuffer.put(uv).flip();
		
		IntBuffer indexIntBuffer = BufferUtils.createIntBuffer(index.length);
		indexIntBuffer.put(index).flip();
		
		vao = genVertexArrays();
		glBindVertexArray(vao);
		
		int vertexID = genBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertexID);
		glBufferData(GL_ARRAY_BUFFER, vertexFloatBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(0);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);
		
		int uvID = genBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvID);
		glBufferData(GL_ARRAY_BUFFER, uvFloatBuffer, GL_STATIC_DRAW);
		glEnableVertexAttribArray(1);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
		
		int indexID = genBuffers();
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, indexID);
		glBufferData(GL_ELEMENT_ARRAY_BUFFER, indexIntBuffer, GL_STATIC_DRAW);
		
		
		glBindVertexArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, 0);
		glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, 0);
	}

	@Override
	public void render(double deltaTime) {
		if(shader == null) {
			System.err.println("Error: no shader binded... \nshould set shader first");
			System.exit(1);
		}
		
		texture.bind();
		
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		
		for(int i = 1; i < 4; i++) {
			ml_matrix.translate(11.2f, 0.0f, 0.0f);
			setUniformMatrix4fv("ml_matrix", ml_matrix);
			glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
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
		
		position.x -= deltaTime * scrollSpeed ;
		if(position.x < -21.2f) {
			position.x += 11.2f;
		}
		
		
		
	}

}
