package org.kneelawk.hellolwjgl;

import java.io.IOException;

import org.kneelawk.hellolwjgl.classpath.CPControl;

public class HelloLWJGLLaunch {
	public static final String MAIN_CLASS = "org.kneelawk.hellolwjgl.HelloLWJGL";

	public static void main(String[] args) throws IOException {
		CPControl cp = new CPControl(MAIN_CLASS);
		cp.addExtractingLibrary()
				.addLibraries("lwjgl", CPControl.isMe,
						str -> str.toLowerCase().contains("lwjgl"))
				.addLibraries("joml", CPControl.isMe,
						str -> str.toLowerCase().contains("joml"))
				.addLibraries("guava", CPControl.isMe,
						str -> str.toLowerCase().contains("guava"));
		cp.execute(args);
	}
}
