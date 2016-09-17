package org.kneelawk.hellolwjgl.shader;

public class Shader {
	private String source;
	private int type;

	public Shader(String source, int type) {
		super();
		this.source = source;
		this.type = type;
	}

	public String getSource() {
		return source;
	}

	public int getType() {
		return type;
	}
}
