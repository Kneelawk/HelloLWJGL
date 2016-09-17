package org.kneelawk.hellolwjgl.shader;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.lwjgl.BufferUtils;

public class ShaderProgram {
	private Set<Shader> sources;
	private Set<Integer> shaderIds;
	private int programId;

	public ShaderProgram(Shader... sources) {
		this.sources = new HashSet<Shader>();
		this.sources.addAll(Arrays.asList(sources));
		shaderIds = new HashSet<Integer>();
	}

	public void addSource(Shader source) {
		sources.add(source);
	}

	public boolean removeSource(Shader source) {
		return sources.remove(source);
	}

	public void compileShaders() throws IOException {
		for (Shader source : sources) {
			shaderIds.add(compileShader(source));
		}
	}

	private static String loadFromClasspath(String path) throws IOException {
		InputStream is = ShaderProgram.class.getResourceAsStream(path);
		String str = "";
		byte[] buf = new byte[8192];
		int read;
		while ((read = is.read(buf)) >= 0) {
			str += new String(buf, 0, read);
		}
		is.close();
		return str;
	}

	private static int compileShader(Shader shader) throws IOException {
		int shaderId = glCreateShader(shader.getType());

		String str = null;
		str = loadFromClasspath(shader.getSource());

		IntBuffer result = BufferUtils.createIntBuffer(1);

		System.out.println("Compiling shader: " + shader.getSource());
		glShaderSource(shaderId, str);
		glCompileShader(shaderId);
		glGetShaderiv(shaderId, GL_COMPILE_STATUS, result);
		String log = glGetShaderInfoLog(shaderId);
		if (log != null && !"".equals(log)) {
			System.out.println(log);
		}
		int status = result.get(0);
		if (status != GL_TRUE) {
			glDeleteShader(shaderId);
			System.err.println("Error compiling shader: " + shader.getSource());
			throw new IOException("Status: " + status + ", log: " + log);
		}

		return shaderId;
	}

	public int linkShaders() throws IOException {
		System.out.println("Linking shader program...");
		programId = glCreateProgram();

		for (Integer id : shaderIds) {
			glAttachShader(programId, id);
		}

		IntBuffer result = BufferUtils.createIntBuffer(1);

		glLinkProgram(programId);
		glGetProgramiv(programId, GL_LINK_STATUS, result);
		String log = glGetShaderInfoLog(programId);
		if (log != null && !"".equals(log)) {
			System.out.println(log);
		}
		int status = result.get(0);
		if (status != GL_TRUE) {
			for (Integer id : shaderIds) {
				glDetachShader(programId, id);
			}
			System.err.println("Error linking shader program!");
			throw new IOException("Status: " + status + ", log: " + log);
		}

		for (Integer id : shaderIds) {
			glDetachShader(programId, id);
		}
		for (Integer id : shaderIds) {
			glDeleteShader(id);
		}
		shaderIds.clear();

		return programId;
	}

	public int getProgramId() {
		return programId;
	}
}
