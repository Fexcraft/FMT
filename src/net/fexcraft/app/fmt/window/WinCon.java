package net.fexcraft.app.fmt.window;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

import org.joml.Vector2i;
import com.spinyowl.legui.animation.AnimatorProvider;
import com.spinyowl.legui.component.Frame;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.listener.processor.EventProcessorProvider;
import com.spinyowl.legui.system.context.CallbackKeeper;
import com.spinyowl.legui.system.context.Context;
import com.spinyowl.legui.system.context.DefaultCallbackKeeper;
import com.spinyowl.legui.system.handler.processor.SystemEventProcessor;
import com.spinyowl.legui.system.handler.processor.SystemEventProcessorImpl;
import com.spinyowl.legui.system.layout.LayoutManager;
import com.spinyowl.legui.system.renderer.Renderer;
import com.spinyowl.legui.system.renderer.nvg.NvgRenderer;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryUtil;

import net.fexcraft.app.fmt.FMT;

/**
 *
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class WinCon {

	public static Thread thread;
	public long window;
	//
	public static Frame FRAME;
	public static Context CONTEXT;
	public static Renderer RENDERER;

	public WinCon(String title, int width, int height, boolean resize){
		thread = new Thread(() -> {
			glfwWindowHint(GLFW_RESIZABLE, resize ? GL11.GL_TRUE : GL11.GL_FALSE);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
			glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
			glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
			glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
			glfwWindowHint(GLFW_FLOATING, GL11.GL_TRUE);
			window = glfwCreateWindow(width, height, title, MemoryUtil.NULL, MemoryUtil.NULL);
			if(window == MemoryUtil.NULL) throw new RuntimeException(String.format("Failed to create window '%s'-%sx%s !", title, width, height));
			glfwMakeContextCurrent(window);
			GL.createCapabilities();
			FMT.icon(window);
			glfwShowWindow(window);
			//
			FRAME = new Frame(width, height);
			FRAME.getContainer().add(new Label("  test  ", 5, 32, 200, 20));
			FRAME.getContainer().add(new Label("  test  ", 5, 54, 200, 20));
			FRAME.getContainer().add(new Label("  test  ", 5, 76, 200, 20));
			FRAME.getContainer().add(new Label("  test  ", 5, 98, 200, 20));
			FRAME.getContainer().add(new Label("  test  ", 5, 120, 200, 20));
			SystemEventProcessor sys_event_processor = new SystemEventProcessorImpl();
			CONTEXT = new Context(window, sys_event_processor);
			CONTEXT.setWindowSize(new Vector2i(width, height));
			FRAME.getComponentLayer().setFocusable(false);
			CallbackKeeper keeper = new DefaultCallbackKeeper();
			CallbackKeeper.registerCallbacks(window, keeper);
			SystemEventProcessor.addDefaultCallbacks(keeper, sys_event_processor);
			RENDERER = new NvgRenderer();
			RENDERER.initialize();
			FMT.vsync();
			glEnable(GL_DEPTH_TEST);
			glDepthFunc(GL_LESS);
			//
			while(!glfwWindowShouldClose(window)){
				prerender();
				RENDERER.render(FRAME, CONTEXT);
				glfwPollEvents();
				glfwSwapBuffers(window);
				sys_event_processor.processEvents(FRAME, CONTEXT);
				EventProcessorProvider.getInstance().processEvents();
				LayoutManager.getInstance().layout(FRAME);
				AnimatorProvider.getAnimator().runAnimations();
			}
			glfwDestroyWindow(window);
		});
		thread.setName(title.toUpperCase());
		thread.start();
		WinMan.add(title, this);
	}

	private void prerender(){
		CONTEXT.updateGlfwWindow();
		Vector2i size = CONTEXT.getFramebufferSize();
		glViewport(0, 0, size.x, size.y);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
		glClearColor(0.5f, 0.5f, 0.5f, 1);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	}

	public void close(){
		glfwSetWindowShouldClose(window, true);
	}
}
