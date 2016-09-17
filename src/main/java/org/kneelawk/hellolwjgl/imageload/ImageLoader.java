package org.kneelawk.hellolwjgl.imageload;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;
import java.nio.IntBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;

public class ImageLoader {
	public static int loadImage(URL path) throws IOException {
		BufferedImage image = ImageIO.read(path);
		int width = image.getWidth();
		int height = image.getHeight();
		int rowSize = width;
		int dataSize = height * rowSize;
		int[] data = new int[dataSize];

		image.getRGB(0, 0, width, height, data, 0, rowSize);

		IntBuffer buf = BufferUtils.createIntBuffer(dataSize);
		for (int y = height - 1; y >= 0; y--) {
			buf.put(data, y * rowSize, rowSize);
		}
		buf.flip();

		int textureId = glGenTextures();
		glBindTexture(GL_TEXTURE_2D, textureId);
		glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_BGRA,
				GL_UNSIGNED_BYTE, buf);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
		glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER,
				GL_LINEAR_MIPMAP_LINEAR);
		glGenerateMipmap(GL_TEXTURE_2D);
		return textureId;
	}
}
