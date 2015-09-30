package flappybird.gamesys;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.glfw.Callbacks.*;
import static org.lwjgl.system.MemoryUtil.*;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;

import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWvidmode;
import org.lwjgl.openal.ALContext;
import org.lwjgl.opengl.GL;

import flappybird.util.Input;

public class Game implements Runnable {
	
	int WIDTH = 800;
	int HEIGHT = 600;
	
	private long windowID;
	private GLFWKeyCallback keyCallback;
	private GLFWErrorCallback errorCallback;
	
	private ALContext alContext;
	
	public Game() {
	}

	public void start() {
		Thread thread = new Thread(this, "FlappyBird");
		thread.start();
	}
	
	private void init() {
		// setup GLFW
		
		if(glfwInit() != GL_TRUE) {
			System.err.println("Unable to initialize GLFW");
			System.exit(1);
		}
		
		glfwSetErrorCallback(errorCallback = errorCallbackPrint(System.err));
		
		glfwWindowHint(GLFW_VISIBLE, GL_FALSE);
		glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);
		
		windowID = glfwCreateWindow(WIDTH, HEIGHT, "FlappyBird LWJGL", NULL, NULL);
		
		if(windowID == NULL) {
			System.err.println("Cannot create GLFW window.");
			System.exit(1);
		}
		
		glfwSetKeyCallback(windowID, keyCallback = new Input());
		
		ByteBuffer vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());
		glfwSetWindowPos(windowID, (GLFWvidmode.width(vidmode) - WIDTH)/2, (GLFWvidmode.height(vidmode) - HEIGHT)/2);
		
		glfwMakeContextCurrent(windowID);
		glfwSwapInterval(1);
		glfwShowWindow(windowID);
		

	}

	@Override
	public void run() {
		init();
		
		// OpenAL
		alContext = ALContext.create();
		
		// OpneGL
		GL.createCapabilities();
		
		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		
		glEnable(GL_DEPTH_TEST);
		
		glEnable(GL_BLEND);
		glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		
		GameManager gameManager = new GameManager();
		
		while(glfwWindowShouldClose(windowID) != GL_TRUE) {
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			if(Input.isKeyReleased(GLFW_KEY_ESCAPE)) {
				glfwSetWindowShouldClose(windowID, GL_TRUE);
			}
			
			gameManager.run();	

			if(GameManager.instance.isCanRestart() && Input.isKeyReleased(GLFW_KEY_SPACE)) {
				gameManager.cleanUp();
				gameManager = new GameManager();
			}
			
			glfwSwapBuffers(windowID);
			Input.resetKeyState();
			glfwPollEvents();
			
		}
		
		gameManager.cleanUp();
		
		alContext.destroy();
		
		try {
			glfwDestroyWindow(windowID);
			keyCallback.release();
		}
		finally {
			glfwTerminate();
			errorCallback.release();
		}
		
	}

}
