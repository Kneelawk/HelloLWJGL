package org.kneelawk.hellolwjgl.objparse;

import java.util.List;

import com.google.common.collect.Lists;

public class Face {
	private List<Vertex> vertices;

	public Face() {
		vertices = Lists.newArrayList();
	}

	public void add(Vertex vertex) {
		vertices.add(vertex);
	}

	public boolean remove(Vertex vertex) {
		return vertices.remove(vertex);
	}

	public List<Vertex> getVertices() {
		return vertices;
	}

	public Vertex getVertex(int index) {
		return vertices.get(index);
	}

	public String toString() {
		return "Face" + vertices;
	}
}
