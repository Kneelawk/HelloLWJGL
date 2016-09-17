package org.kneelawk.hellolwjgl.objparse;

import java.util.List;

public class VertexArray {
	private float[] positionData;
	private float[] normalData;
	private float[] texCoordData;

	public VertexArray(float[] positionData, float[] normalData,
			float[] texCoordData) {
		this.positionData = positionData;
		this.normalData = normalData;
		this.texCoordData = texCoordData;
	}

	public float[] getPositionData() {
		return positionData;
	}

	public float[] getNormalData() {
		return normalData;
	}

	public float[] getTexCoordData() {
		return texCoordData;
	}

	public static VertexArray fromVertexList(List<Vertex> list) {
		int size = list.size();
		float[] positionData = new float[size * 3];
		float[] normalData = new float[size * 3];
		float[] texCoordData = new float[size * 2];

		for (int i = 0; i < size; i++) {
			Vertex v = list.get(i);

			positionData[i * 3 + 0] = v.getPosition().x;
			positionData[i * 3 + 1] = v.getPosition().y;
			positionData[i * 3 + 2] = v.getPosition().z;

			normalData[i * 3 + 0] = v.getNormal().x;
			normalData[i * 3 + 1] = v.getNormal().y;
			normalData[i * 3 + 2] = v.getNormal().z;

			texCoordData[i * 2 + 0] = v.getTexCoord().x;
			texCoordData[i * 2 + 1] = v.getTexCoord().y;
		}

		return new VertexArray(positionData, normalData, texCoordData);
	}
}
