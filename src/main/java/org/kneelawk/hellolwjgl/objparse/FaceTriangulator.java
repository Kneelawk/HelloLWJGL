package org.kneelawk.hellolwjgl.objparse;

import java.util.Collection;
import java.util.List;

import com.google.common.collect.Lists;

public class FaceTriangulator {
	public static List<Face> triangleFan(Face face) {
		return triangleFan(face, Lists.newArrayList());
	}

	public static List<Face> triangleFan(Face face, List<Face> list) {
		Vertex first = null, last = null;
		for (Vertex v : face.getVertices()) {
			if (first == null) {
				first = v;
			} else if (last == null) {
				last = v;
			} else {
				Face n = new Face();
				n.add(first);
				n.add(last);
				n.add(v);
				list.add(n);
				last = v;
			}
		}

		return list;
	}

	public static List<Vertex> triangleFanVertices(Face face, List<Vertex> list) {
		Vertex first = null, last = null;
		for (Vertex v : face.getVertices()) {
			if (first == null) {
				first = v;
			} else if (last == null) {
				last = v;
			} else {
				list.add(first);
				list.add(last);
				list.add(v);
				last = v;
			}
		}

		return list;
	}

	public static List<Vertex> triangleFanVerticesFromFaces(
			Collection<Face> faces) {
		return triangleFanVerticesFromFaces(faces, Lists.newArrayList());
	}

	public static List<Vertex> triangleFanVerticesFromFaces(
			Collection<Face> faces, List<Vertex> dest) {
		for (Face face : faces) {
			triangleFanVertices(face, dest);
		}

		return dest;
	}
}
