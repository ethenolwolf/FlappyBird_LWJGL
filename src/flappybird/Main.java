package flappybird;

import java.io.File;

import flappybird.gamesys.Game;

public class Main {

	public static void main(String[] args) {
		System.setProperty("org.lwjgl.librarypath", new File("native").toString());
		new Game().start();

	}

}
