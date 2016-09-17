package org.kneelawk.hellolwjgl;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

import java.io.IOException;
import java.nio.FloatBuffer;

import org.joml.Matrix4f;
import org.kneelawk.hellolwjgl.imageload.ImageLoader;
import org.kneelawk.hellolwjgl.objparse.ObjParser;
import org.kneelawk.hellolwjgl.objparse.VertexArray;
import org.kneelawk.hellolwjgl.shader.Shader;
import org.kneelawk.hellolwjgl.shader.ShaderProgram;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL;

public class HelloLWJGL {

	public static final int WIDTH = 1280;
	public static final int HEIGHT = 720;
	public static final int TARGET_FRAMERATE = 60;

	public static final float[] TRIANGLE_DATA = { -1.0f, -1.0f, 0.0f, 1.0f,
			-1.0f, 0.0f, 0.0f, 1.0f, 0.0f };

	private static long lastUpdate;
	private static float updateDelta;

	private static long window;
	private static SyncHelper sync;
	private static int vertArrId;
	private static VertexArray objVertArr;
	private static int vertBuffId;
	private static int normBuffId;
	private static int uvBuffId;
	private static int shaderProgramId;
	private static Matrix4f projection;
	private static int mvpArgId;
	private static FloatBuffer mvpBuff;

	private static float rotation;

	public static void main(String[] args) {
		init();
		loop();
	}

	public static void loadObjects() {
		try {
			ObjParser parser = new ObjParser(
					HelloLWJGL.class.getResource("/itemScrewDriver.obj"));
			parser.read();
			objVertArr = parser.getVertexArrayFromAllFaces();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void init() {
		if (glfwInit() != GLFW_TRUE)
			throw new RuntimeException("Unable to initialize glfw.");

		glfwWindowHint(GLFW_SAMPLES, 4);
		glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GLFW_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);

		window = glfwCreateWindow(WIDTH, HEIGHT, "HelloLWJGL", 0, 0);

		glfwShowWindow(window);

		glfwMakeContextCurrent(window);

		glfwSwapInterval(1);

		GL.createCapabilities(true);

		sync = new SyncHelper();

		System.out.println("GL profile: " + glGetString(GL_VERSION));

		glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);

		// vertex position data
		vertArrId = glGenVertexArrays();
		glBindVertexArray(vertArrId);

		loadObjects();

		System.out.println("Obj size: " + objVertArr.getPositionData().length);
		FloatBuffer buf = BufferUtils.createFloatBuffer(objVertArr
				.getPositionData().length);
		buf.put(objVertArr.getPositionData());
		buf.flip();

		vertBuffId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, vertBuffId);
		glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);

		// normal data
		buf.put(objVertArr.getNormalData());
		buf.flip();

		normBuffId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, normBuffId);
		glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);

		// uv data
		buf = BufferUtils
				.createFloatBuffer(objVertArr.getTexCoordData().length);
		buf.put(objVertArr.getTexCoordData());
		buf.flip();

		uvBuffId = glGenBuffers();
		glBindBuffer(GL_ARRAY_BUFFER, uvBuffId);
		glBufferData(GL_ARRAY_BUFFER, buf, GL_STATIC_DRAW);

		// load and bind texture itself
		try {
			ImageLoader.loadImage(HelloLWJGL.class
					.getResource("/itemScrewDriver.png"));
		} catch (IOException e1) {
			e1.printStackTrace();
		}

		ShaderProgram program = new ShaderProgram(new Shader(
				"/shaders/shader.vs", GL_VERTEX_SHADER), new Shader(
				"/shaders/shader.fs", GL_FRAGMENT_SHADER));
		try {
			program.compileShaders();
			shaderProgramId = program.linkShaders();
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		projection = new Matrix4f().perspective((float) Math.PI / 4f,
				((float) WIDTH) / ((float) HEIGHT), 0.01f, 100.0f);

		mvpArgId = glGetUniformLocation(shaderProgramId, "MVP");
		mvpBuff = BufferUtils.createFloatBuffer(16);

		glfwShowWindow(window);

		lastUpdate = System.currentTimeMillis();
	}

	public static void loop() {
		while (glfwWindowShouldClose(window) != GLFW_TRUE) {
			glfwPollEvents();
			update();
			render();
			glfwSwapBuffers(window);
			sync.sync(TARGET_FRAMERATE);
		}

		glfwTerminate();
	}

	public static void updateDelta() {
		long now = System.currentTimeMillis();
		updateDelta = (now - lastUpdate) / 1000f;
		lastUpdate = now;
	}

	public static void update() {
		updateDelta();

		float x = (float) Math.sin(rotation) * 3f;
		float z = (float) Math.cos(rotation) * 3f;
		Matrix4f view = new Matrix4f().lookAt(x, 3f, z, 0f, 0f, 0f, 0f, 1f, 0f);
		Matrix4f model = new Matrix4f();
		Matrix4f mvp = new Matrix4f(projection).mul(view).mul(model);
		mvp.get(mvpBuff);

		rotation += 0.5f * updateDelta;
		if (rotation >= Math.PI * 2) {
			rotation -= Math.PI * 2;
		}
	}

	public static void render() {
		glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

		glUseProgram(shaderProgramId);

		glUniformMatrix4fv(mvpArgId, false, mvpBuff);

		glEnableVertexAttribArray(0);
		glBindBuffer(GL_ARRAY_BUFFER, vertBuffId);
		glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0);

		glEnableVertexAttribArray(1);
		glBindBuffer(GL_ARRAY_BUFFER, uvBuffId);
		glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

		glEnableVertexAttribArray(2);
		glBindBuffer(GL_ARRAY_BUFFER, normBuffId);
		glVertexAttribPointer(2, 3, GL_FLOAT, false, 0, 0);

		glDrawArrays(GL_TRIANGLES, 0, objVertArr.getPositionData().length / 3);

		glDisableVertexAttribArray(0);
		glDisableVertexAttribArray(1);
		glDisableVertexAttribArray(2);
	}
}
