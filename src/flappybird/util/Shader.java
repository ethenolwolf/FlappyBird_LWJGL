package flappybird.util;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Shader implements CleanUpAble {

	private final int programID;

	private final int vertShaderID;
	private final int fragShaderID;
	
	private final String shaderFolder = "res/shaders/";


	public Shader(String vert, String frag) {
		programID = glCreateProgram();

		vertShaderID = glCreateShader(GL_VERTEX_SHADER);
		fragShaderID = glCreateShader(GL_FRAGMENT_SHADER);

		glShaderSource(vertShaderID, loadSource(vert));
		glCompileShader(vertShaderID);
		if(glGetShaderi(vertShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(vertShaderID));
			System.exit(1);
		}

		glShaderSource(fragShaderID, loadSource(frag));
		glCompileShader(fragShaderID);
		if(glGetShaderi(fragShaderID, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println(glGetShaderInfoLog(fragShaderID));
			System.exit(1);
		}

		glAttachShader(programID, vertShaderID);
		glAttachShader(programID, fragShaderID);

		glLinkProgram(programID);
		if(glGetProgrami(programID, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println(glGetProgramInfoLog(programID));
			System.exit(1);
		} 

		glValidateProgram(programID);
		if(glGetProgrami(programID, GL_VALIDATE_STATUS) == GL_FALSE) {
			System.err.println(glGetProgramInfoLog(programID));
			System.exit(1);
		}

		glDetachShader(programID, vertShaderID);
		glDetachShader(programID, fragShaderID);
		glDeleteShader(vertShaderID);
		glDeleteShader(fragShaderID);
	}
	
	public int getProgramID() {
		return programID;
	};
	
	public int getUniformLocation(String uniform) {
		return glGetUniformLocation(programID, uniform);
	}

	public void bind() {
		glUseProgram(programID);
	}

	public void unbind() {
		glUseProgram(0);
	}

	public void cleanUp() {
		unbind();
		glUseProgram(0);
	}

	private String loadSource(String filename) {
		StringBuilder source = new StringBuilder();
		try(BufferedReader bReader = new BufferedReader(new FileReader(shaderFolder+filename))) {
			String tmp;
			while((tmp = bReader.readLine()) != null) {
				source.append(tmp).append("\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return source.toString();
	}

}
