package org.kneelawk.hellolwjgl.objparse;

import org.joml.Vector2f;
import org.joml.Vector3f;

public class Vertex {
	private Vector3f position;
	private Vector3f normal;
	private Vector2f texCoord;

	public Vertex(Vector3f position, Vector3f normal, Vector2f texCoord) {
		super();
		this.position = position;
		this.normal = normal;
		this.texCoord = texCoord;
	}

	public Vector3f getPosition() {
		return position;
	}

	public void setPosition(Vector3f position) {
		this.position = position;
	}

	public Vector3f getNormal() {
		return normal;
	}

	public void setNormal(Vector3f normal) {
		this.normal = normal;
	}

	public Vector2f getTexCoord() {
		return texCoord;
	}

	public void setTexCoord(Vector2f texCoord) {
		this.texCoord = texCoord;
	}

	public String toString() {
		return "Vertex{position: " + position + ", normal: " + normal
				+ ", texCoord: " + texCoord + "}";
	}
}
