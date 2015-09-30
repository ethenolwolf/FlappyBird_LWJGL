package flappybird.gameobj;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.BufferUtils;

import flappybird.util.CleanUpAble;
import flappybird.util.Shader;
import flappybird.util.Texture;

public abstract class GameObject implements CleanUpAble {
	private List<Integer> vaoList;
	private List<Integer> baoList;

	protected Vector3f position;
	protected Matrix4f ml_matrix;
	
	protected Shader shader;
	protected Texture texture;
	
	public Texture getTexture() {
		return texture;
	}

	public void setTexture(Texture texture) {
		this.texture = texture;
	}

	public Shader getShader() {
		return shader;
	}

	public void setShader(Shader shader) {
		this.shader = shader;
	}

	public GameObject() {
		vaoList = new ArrayList<>();
		baoList = new ArrayList<>();
		ml_matrix = new Matrix4f();
		position = new Vector3f();
	}

	public abstract void render(double deltaTime);
	public abstract void update(double deltaTime);
	
	public Matrix4f getModelMatrix() {
		return ml_matrix;
	}
	
	public void setUniformMatrix4fv(String uniform, Matrix4f matrix) {
		if(shader == null) {
			System.err.println("Error: no shader binding...\n should bind shader first");
			System.exit(1);
		}
		int location = glGetUniformLocation(shader.getProgramID(), uniform);
		if(location == -1) {
			return;
		}
		
		FloatBuffer matrixFloatBuffer = BufferUtils.createFloatBuffer(16);
		matrix.get(matrixFloatBuffer);
		glUniformMatrix4fv(location, false, matrixFloatBuffer);
	}

	protected int genVertexArrays() {
		int id = glGenVertexArrays();
		vaoList.add(id);
		return id;
	}

	protected int genBuffers() {
		int id = glGenBuffers();
		baoList.add(id);
		return id;
	}

	public void cleanUp() {
		for(int i : baoList) {
			glDeleteBuffers(i);
		}

		for(int i : vaoList) {
			glDeleteVertexArrays(i);
		}

	}

}
