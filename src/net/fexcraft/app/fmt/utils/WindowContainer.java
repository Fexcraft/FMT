package net.fexcraft.app.fmt.utils;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import net.fexcraft.app.fmt.FMT;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class WindowContainer {
	
	public static Thread thread;
	public long window;
	
	public WindowContainer(){
		thread = new Thread(() -> {
			glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			window = glfwCreateWindow(300, 200, "WINDOW TEST", MemoryUtil.NULL, MemoryUtil.NULL);
			if(window == MemoryUtil.NULL) throw new RuntimeException("Failed to create window!");
			glfwMakeContextCurrent(window);
			GL.createCapabilities();
			FMT.icon(window);
			glfwShowWindow(window);
			while(!glfwWindowShouldClose(FMT.getWindow())){
				glfwPollEvents();
				glfwSwapBuffers(window);
			}
			glfwDestroyWindow(window);
		});
		thread.setName("WINDOW");
		thread.start();
	}
}
