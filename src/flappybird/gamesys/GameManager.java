package flappybird.gamesys;

import static org.lwjgl.glfw.GLFW.*;

import java.util.ArrayList;
import java.util.List;

import org.joml.Matrix4f;
import org.joml.Vector3f;

import flappybird.gameobj.Background;
import flappybird.gameobj.Bird;
import flappybird.gameobj.GameObject;
import flappybird.gameobj.Pipe;
import flappybird.util.Shader;
import flappybird.util.Texture;

public class GameManager {
	// make singleton
	public static GameManager instance;
	
	List<GameObject> gameObjectList;
	List<Shader> shaderList;
	List<Texture> textureList;
	
	double prevTime;
	
	Matrix4f pr_matrix;
	Matrix4f vw_matrix;
	
	private Bird bird;
	private Pipe pipe;
	
	private boolean gameOver = false;
	private boolean canRestart = false;
	
	public boolean isCanRestart() {
		return canRestart;
	}

	public void setCanRestart(boolean canRestart) {
		this.canRestart = canRestart;
	}

	public boolean isGameOver() {
		return gameOver;
	}

	public GameManager() {
		instance = this;
		
		gameObjectList = new ArrayList<>();
		shaderList = new ArrayList<>();
		textureList = new ArrayList<>();
		
		// background
		Shader backgroundShader = new Shader("background.vert", "background.frag");
		shaderList.add(backgroundShader);
		
		Texture backgroundTexture = new Texture("background.png");
		textureList.add(backgroundTexture);
		
		Background background = new Background();
		background.setShader(backgroundShader);
		background.setTexture(backgroundTexture);
		gameObjectList.add(background);
		
		// pipe
		Shader pipeShader = new Shader("pipe.vert", "pipe.frag");
		shaderList.add(pipeShader);
		
		Texture pipeTexture = new Texture("pipe.png");
		textureList.add(pipeTexture);
		
		pipe= new Pipe();
		pipe.setShader(pipeShader);
		pipe.setTexture(pipeTexture);
		gameObjectList.add(pipe);
		
		
		// bird
		Shader birdShader = new Shader("bird.vert", "bird.frag");
		shaderList.add(birdShader);
		
		Texture birdTexture = new Texture("flappy-bird-1.png");
		textureList.add(birdTexture);
		
		bird = new Bird();
		bird.setShader(birdShader);
		bird.setTexture(birdTexture);
		
		gameObjectList.add(bird);
		

		
		pr_matrix = new Matrix4f();
		vw_matrix = new Matrix4f();
		
		pr_matrix.setOrtho(-10.0f, 10.0f, -10.0f*6.0f/8.0f, 10.0f*6.0f/8.0f, 0.1f, 100.0f);
		vw_matrix.setLookAt(new Vector3f(0.0f, 0.0f, 5.0f), new Vector3f(0.0f, 0.0f, 0.0f), new Vector3f(0.0f, 1.0f, 0.0f));
		
		prevTime = glfwGetTime();
	}

	public void run() {
		double currentTime = glfwGetTime();
		double deltaTime = currentTime - prevTime;
		prevTime = currentTime;
		
		if(bird.hit) {
			gameOver = true;
		}
		
		for(GameObject gameObject : gameObjectList) {
			gameObject.update(deltaTime);
			
			Shader shader = gameObject.getShader();
			shader.bind();
			gameObject.setUniformMatrix4fv("pr_matrix", pr_matrix);
			gameObject.setUniformMatrix4fv("vw_matrix", vw_matrix);
			gameObject.setUniformMatrix4fv("ml_matrix", gameObject.getModelMatrix());
			gameObject.render(deltaTime);
		}
		
		pipe.checkBirdHit(bird);
		
	}
	
	public void cleanUp() {
		for(GameObject gameObject :  gameObjectList) {
			gameObject.cleanUp();
		}
		
		for(Shader shader : shaderList) {
			shader.cleanUp();
		}
		
		for(Texture texture : textureList) {
			texture.cleanUp();
		}
	}
	

}
