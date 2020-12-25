package net.fexcraft.app.fmt;

import static net.fexcraft.app.fmt_old.utils.Logging.log;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_TRUE;

import java.io.File;

import org.joml.Vector4f;
import org.liquidengine.legui.component.Frame;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryUtil;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt_old.utils.DiscordUtil;
import net.fexcraft.app.fmt_old.utils.ST_Timer;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.RGB;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2020 fexcraft.net
 * */
public class FMT {

	public static final String VERSION = "3.0.0";
	public static final String TITLE = getCurrentTitle();
	public static final String CLID = "587016218196574209";
	//
	private static final FMT INSTANCE = new FMT();
	public static int WIDTH, HEIGHT;
	private static String title;
	//
	private final ST_Timer timer = new ST_Timer();
	private GLFWErrorCallback errorCallback;
	private long window;
	//
	public static Frame FRAME, SS_FRAME;
	
	public static void main(String... args) throws Exception {
		log("==================================================");
		log("Starting FMT" + VERSION + "!");
        System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
        if(System.getProperty("os.name").toLowerCase().contains("mac")){
        	System.setProperty("java.awt.headless", Boolean.TRUE.toString());
        }
		log("Running on " + System.getProperty("os.name") + " / " + System.getProperty("os.version"));
	    System.setProperty("org.lwjgl.librarypath", new File("./lib/").getAbsolutePath());
		Configuration.SHARED_LIBRARY_EXTRACT_DIRECTORY.set("./lib/natives");
		Configuration.SHARED_LIBRARY_EXTRACT_PATH.set("./lib/natives");
	    //
		Settings.load();
		Settings.apply(INSTANCE);
		try{
			INSTANCE.run();
		}
		catch(Throwable thr){
			log(thr);
			System.exit(1);
		}
	}
	
	public void run() throws Exception{
		//TODO Translator.init();
		timer.init();
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW.");
		glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 3);
		glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE);
		glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		window = glfwCreateWindow(WIDTH, HEIGHT, getTitle(), MemoryUtil.NULL, MemoryUtil.NULL);
		if(window == MemoryUtil.NULL) throw new RuntimeException("Failed to create window");
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		//TODO loadIcon(window);
		glfwShowWindow(window);
		glfwFocusWindow(window);
		//
		//TODO camcon = new GGR(0, 20, 0, Static.PI + -Static.rad45, -Static.rad30);
		AxisRotator.setDefImpl(Axis3DL.class);
		Settings.applyTheme();
		Settings.updateTheme();
		FRAME = new Frame(WIDTH, HEIGHT);
		
		
		//
		while(!glfwWindowShouldClose(window)){
			//TODO
			
			//TODO renderer.render(frame, context);
			timer.updateFPS();
			glfwPollEvents();
			glfwSwapBuffers(window);
			//TODO systemEventProcessor.processEvents(frame, context);
			//TODO EventProcessorProvider.getInstance().processEvents();
			//TODO LayoutManager.getInstance().layout(frame);
			//TODO AnimatorProvider.getAnimator().runAnimations();
			timer.update();
		}
		Settings.save();
		System.exit(0);
	}
	
	public static final String getCurrentTitle(){
		String f = Static.random.nextInt(2) == 0 ? "Fex's " : "Fexcraft ";
		String m = Static.random.nextInt(2) == 0 ? "Modelling " : "Modding ";
		String t = Static.random.nextInt(2) == 0 ? "Toolbox " : "Toolset ";
		return f + m + t + VERSION + " - %s";
	}
	
	public static final String getTitle(){
		return String.format(TITLE, title);
	}
	
	public FMT setTitle(String string){
		title = string;
		glfwSetWindowTitle(window, getTitle());
		DiscordUtil.update(false);
		return this;
	}

	//https://mkyong.com/java/open-browser-in-java-windows-or-linux/
	public static final void openLink(String url){
		String os = System.getProperty("os.name").toLowerCase();
		Runtime rt = Runtime.getRuntime();
		try{
			if(os.indexOf("win") >= 0){
				rt.exec("rundll32 url.dll,FileProtocolHandler " + url);
			}
			else if(os.indexOf("mac") >= 0){
				rt.exec("open " + url);
			}
			else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0){
				String[] browsers = { "xdg-open", "epiphany", "firefox", "mozilla", "konqueror", "netscape", "opera", "links", "lynx" };
				StringBuffer cmd = new StringBuffer();
				for(int i = 0; i < browsers.length; i++){
					cmd.append((i == 0 ? "" : " || ") + browsers[i] + " \"" + url + "\" ");
				}
				rt.exec(new String[]{ "sh", "-c", cmd.toString() });
			}
			else return;
		}
		catch(Exception e){
			log(e);
		}
		return;
	}

    public static Vector4f rgba(int r, int g, int b, float a){
        return new Vector4f(r / 255f, g / 255f, b / 255f, a);
    }

    public static final Vector4f rgba(int i){
    	return rgba(new RGB(i));
    }

    public static final Vector4f rgba(int i, Float a){
    	RGB rgb = new RGB(i); rgb.alpha = a; return rgba(rgb);
    }

    public static final Vector4f rgba(RGB rgb){
    	float[] arr = rgb.toFloatArray(); return new Vector4f(arr[0], arr[1], arr[2], arr[3]);
    }

}
