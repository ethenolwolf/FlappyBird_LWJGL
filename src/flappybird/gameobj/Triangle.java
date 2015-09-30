package flappybird.gameobj;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

public class Triangle extends GameObject {
	int vao;
	int vbo;
	
	float SIZE = 1.6f;
	
	Vector3f position;
	double time=0;
	
	public Triangle() {
		super();
		
		float[] vertices = {
				-SIZE/2, SIZE/2/1.42f, 2.0f,
				-SIZE/2, -SIZE/2/1.42f, 2.0f,
				SIZE/2, -SIZE/2/1.42f, 2.0f,
				SIZE/2, SIZE/2/1.42f, 2.0f,
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
		
		position = new Vector3f();
		
		FloatBuffer verticesFloatBuffer = BufferUtils.createFloatBuffer(vertices.length);
		verticesFloatBuffer.put(vertices).flip();
		
		FloatBuffer uvFloatBuffer = BufferUtils.createFloatBuffer(uv.length);
		uvFloatBuffer.put(uv).flip();
		
		IntBuffer indexIntBuffer = BufferUtils.createIntBuffer(index.length);
		indexIntBuffer.put(index).flip();
		
		vao = genVertexArrays();
		glBindVertexArray(vao);
		
		// vertex
		vbo = genBuffers();
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
		
	}

	@Override
	public void render(double deltaTime) {
		if(shader == null) {
			System.err.println("Error: no shader binding... \nrequires to set shader first");
			throw new NullPointerException();
		}

		texture.bind();
		
		glBindVertexArray(vao);
		glDrawElements(GL_TRIANGLES, 6, GL_UNSIGNED_INT, 0);
		
		texture.unbind();
		shader.unbind();
	}

	@Override
	public void update(double deltaTime) {
		ml_matrix.setTranslation(position);
		
		position.set((float)Math.sin(time) * 2.0f, (float)Math.cos(time) * 2.0f, 0.0f);
		time += deltaTime;
	}

}
