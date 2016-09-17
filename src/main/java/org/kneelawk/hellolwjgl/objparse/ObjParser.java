package org.kneelawk.hellolwjgl.objparse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.joml.Vector2f;
import org.joml.Vector3f;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;

public class ObjParser {
	private BufferedReader reader;

	private Multimap<String, Face> faces;

	public ObjParser(Reader reader) {
		this.reader = new BufferedReader(reader);
	}

	public ObjParser(InputStream is) {
		this(new InputStreamReader(is));
	}

	public ObjParser(File file) throws FileNotFoundException {
		this(new FileReader(file));
	}

	public ObjParser(URL url) throws IOException {
		this(url.openStream());
	}

	public Multimap<String, Face> read() throws IOException {
		if (faces != null)
			throw new IOException("This file has already been read");

		ArrayList<Vector3f> vertices = new ArrayList<Vector3f>();
		ArrayList<Vector3f> normals = new ArrayList<Vector3f>();
		ArrayList<Vector2f> texCoords = new ArrayList<Vector2f>();
		faces = HashMultimap.create();

		String object = "";
		String line;
		while ((line = reader.readLine()) != null) {
			// get comments out of the way
			if (line.startsWith("#")) {
				continue;
			} else if (line.contains("#")) {
				line = line.split("#")[0];
			}

			if (line.startsWith("v ")) {
				// vertex
				String[] args = line.split(" ");
				vertices.add(new Vector3f(Float.parseFloat(args[1]), Float
						.parseFloat(args[2]), Float.parseFloat(args[3])));
			} else if (line.startsWith("vn ")) {
				// vertex normal
				String[] args = line.split(" ");
				normals.add(new Vector3f(Float.parseFloat(args[1]), Float
						.parseFloat(args[2]), Float.parseFloat(args[3])));
			} else if (line.startsWith("vt ")) {
				// vertex texture
				String[] args = line.split(" ");
				texCoords.add(new Vector2f(Float.parseFloat(args[1]), Float
						.parseFloat(args[2])));
			} else if (line.startsWith("o ") || line.startsWith("g ")) {
				object = line.split(" ")[1];
			} else if (line.startsWith("f ")) {
				Face face = new Face();
				String[] args = line.split(" ");
				for (int i = 1; i < args.length; i++) {
					String arg = args[i];
					String[] inds = arg.split("\\/");
					if (arg.matches(".+\\/.+\\/.+")) {
						face.add(new Vertex(vertices.get(Integer
								.parseInt(inds[0]) - 1), normals.get(Integer
								.parseInt(inds[2]) - 1), texCoords.get(Integer
								.parseInt(inds[1]) - 1)));
					} else if (arg.matches(".+\\/\\/.+")) {
						face.add(new Vertex(vertices.get(Integer
								.parseInt(inds[0]) - 1), normals.get(Integer
								.parseInt(inds[2]) - 1), null));
					} else if (arg.matches(".+\\/.+")) {
						face.add(new Vertex(vertices.get(Integer
								.parseInt(inds[0]) - 1), null, texCoords
								.get(Integer.parseInt(inds[1]) - 1)));
					} else {
						face.add(new Vertex(
								vertices.get(Integer.parseInt(arg) - 1), null,
								null));
					}
				}
				faces.put(object, face);
			} else {
				System.out.println("Unused line: " + line);
			}
		}
		return faces;
	}

	public VertexArray getVertexArray(String... names) {
		if (faces == null)
			return null;

		List<Vertex> vertices = Lists.newArrayList();
		for (String name : names) {
			Collection<Face> object = faces.get(name);
			FaceTriangulator.triangleFanVerticesFromFaces(object, vertices);
		}
		return VertexArray.fromVertexList(vertices);
	}

	public VertexArray getVertexArrayFromAllFaces() {
		if (faces == null)
			return null;

		Collection<Face> all = faces.values();
		List<Vertex> vertices = FaceTriangulator
				.triangleFanVerticesFromFaces(all);
		return VertexArray.fromVertexList(vertices);
	}
}
