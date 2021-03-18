package net.fexcraft.app.fmt;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.joml.Matrix4f;
import org.joml.Vector2i;
import org.joml.Vector4f;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.system.context.CallbackKeeper;
import org.liquidengine.legui.system.context.Context;
import org.liquidengine.legui.system.context.DefaultCallbackKeeper;
import org.liquidengine.legui.system.handler.processor.SystemEventProcessor;
import org.liquidengine.legui.system.handler.processor.SystemEventProcessorImpl;
import org.liquidengine.legui.system.layout.LayoutManager;
import org.liquidengine.legui.system.renderer.Renderer;
import org.liquidengine.legui.system.renderer.nvg.NvgRenderer;
import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWImage;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.glfw.GLFWScrollCallback;
import org.lwjgl.glfw.GLFWWindowCloseCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.fexcraft.app.fmt.demo.ModelT1P;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.Toolbar;
import net.fexcraft.app.fmt.ui.ToolbarMenu;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.DiscordUtil;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.KeyCompound;
import net.fexcraft.app.fmt.utils.MRTRenderer;
import net.fexcraft.app.fmt.utils.ShaderManager;
import net.fexcraft.app.fmt.utils.Timer;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

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
	public static final FMT INSTANCE = new FMT();
	public static int WIDTH, HEIGHT;
	private static String title;
	//
	public static final Timer timer = new Timer();
	public float delta, accumulator, interval = 1f / 30f, alpha;
	private static boolean CLOSE;
	public static GGR CAM;
	public static Label pos, rot, fps;
	//
	@SuppressWarnings("unused") private GLFWErrorCallback errorCallback;
	public long window;
	//
	public static Frame FRAME, SS_FRAME;
	public static Context CONTEXT;
	public static Renderer RENDERER;
	public static Toolbar TOOLBAR;
	public static Model MODEL;
	
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
		//Binding binding = new Binding();
		//GroovyScriptEngine engine = new GroovyScriptEngine("./scripts/");
		//engine.run("test.script", binding);
		try{
			INSTANCE.run();
		}
		catch(Throwable thr){
			log(thr);
			System.exit(1);
		}
	}
	
	public void run() throws Exception{
		Translator.init();
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
		icon(window);
		glfwShowWindow(window);
		glfwFocusWindow(window);
		//
		CAM = new GGR(7, 3, -7, -Static.rad45, -Static.rad20);
		AxisRotator.setDefImpl(Axis3DL.class);
		Settings.applyTheme();
		FRAME = new Frame(WIDTH, HEIGHT);
		FRAME.getContainer().add(TOOLBAR = new Toolbar());
		EditorComponent.registerComponents();
		Settings.loadEditors();
		for(Editor editor : Editor.EDITORLIST) FRAME.getContainer().add(editor);
		//TODO interface
		FRAME.getContainer().add(pos = new Label("  test  ", 320, 32, 200, 20));
		FRAME.getContainer().add(rot = new Label("  test  ", 320, 54, 200, 20));
		FRAME.getContainer().add(fps = new Label("  test  ", 320, 76, 200, 20));
		
		CONTEXT = new Context(window);
		FRAME.getComponentLayer().setFocusable(false);
		Settings.applyTheme();
		CallbackKeeper keeper = new DefaultCallbackKeeper();
		CallbackKeeper.registerCallbacks(window, keeper);
		keeper.getChainKeyCallback().add(new GLFWKeyCallback(){
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods){
				KeyCompound.process(window, key, scancode, action, mods);
			}
		});
		keeper.getChainCursorPosCallback().add(new GLFWCursorPosCallback(){
			@Override
			public void invoke(long window, double xpos, double ypos){
				CAM.cursorPosCallback(window, xpos, ypos);
			}
		});
		keeper.getChainMouseButtonCallback().add(new GLFWMouseButtonCallback(){
			@Override
			public void invoke(long window, int button, int action, int mods){
				CAM.mouseCallback(window, button, action, mods);
			}
		});
		keeper.getChainWindowCloseCallback().add(new GLFWWindowCloseCallback(){
			@Override
			public void invoke(long window){
				//
			}
		});
		keeper.getChainFramebufferSizeCallback().add(new GLFWFramebufferSizeCallback(){
			@Override
			public void invoke(long window, int width, int height){
				HEIGHT = height;
				TOOLBAR.setSize(WIDTH = width, TOOLBAR.getSize().y);
				Editor.EDITORLIST.forEach(editor -> editor.align());
				ToolbarMenu.MENUS.forEach((key, menu) -> menu.layer.hide());
			}
		});
		keeper.getChainScrollCallback().add(new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset){
				//
				CAM.scrollCallback(window, xoffset, yoffset);
			}
		});
		SystemEventProcessor sys_event_processor = new SystemEventProcessorImpl();
		SystemEventProcessor.addDefaultCallbacks(keeper, sys_event_processor);
		RENDERER = new NvgRenderer();
		RENDERER.initialize();
		TextureManager.load();
		//TODO load previous model
		//TODO session, updates, keybinds
		KeyCompound.init();
		//TODO timers
		if(Settings.DISCORD.value){
			DiscordEventHandlers.Builder handler = new DiscordEventHandlers.Builder();
			handler.setReadyEventHandler(new DiscordUtil.ReadyEventHandler());
			handler.setErroredEventHandler(new DiscordUtil.ErroredEventHandler());
			handler.setDisconnectedEventHandler(new DiscordUtil.DisconectedEventHandler());
			handler.setJoinGameEventHandler(new DiscordUtil.JoinGameEventHandler());
			handler.setJoinRequestEventHandler(new DiscordUtil.JoinRequestEventHandler());
			handler.setSpectateGameEventHandler(new DiscordUtil.SpectateGameEventHandler());
			DiscordRPC.discordInitialize(CLID, handler.build(), true);
			DiscordRPC.discordRunCallbacks();
			DiscordUtil.update(true);
			Runtime.getRuntime().addShutdownHook(new Thread(){
				@Override
				public void run(){
					DiscordRPC.discordShutdown();
				}
			});
			(DiscordUtil.DISCORD_THREAD = new Thread(() -> {
				while(!CLOSE){
					DiscordRPC.discordRunCallbacks();
					try{
						Thread.sleep(100);
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			})).start();
		}
		//
		vsync();
		ShaderManager.loadPrograms();
		ModelRendererTurbo.RENDERER = new MRTRenderer();
		int vao = glGenVertexArrays();
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		//
		while(!glfwWindowShouldClose(window)){
			CAM.pollInput(accumulator += (delta = timer.getDelta()));
			accumulator += (delta = timer.getDelta());
			while(accumulator >= interval){
				//TODO "logic"
				timer.updateUPS();
				accumulator -= interval;
				//Trees.updateCounters();
				fps.getTextState().setText(timer.getFPS() + "");
			}
			render(vao, alpha = accumulator / interval);
			//
			RENDERER.render(FRAME, CONTEXT);
			timer.updateFPS();
			glfwPollEvents();
			glfwSwapBuffers(window);
			sys_event_processor.processEvents(FRAME, CONTEXT);
			EventProcessorProvider.getInstance().processEvents();
			LayoutManager.getInstance().layout(FRAME);
			AnimatorProvider.getAnimator().runAnimations();
			timer.update();
		}
		//TODO other saves
		DiscordRPC.discordShutdown();
		RENDERER.destroy();
		glfwDestroyWindow(window);
		glfwTerminate();
		Settings.save();
		//TODO other saves
		System.exit(0);
	}

	private void render(int vao, float alpha){
		glClearColor(0.5f, 0.5f, 0.5f, 0.01f);
		CONTEXT.updateGlfwWindow();
		Vector2i size = CONTEXT.getFramebufferSize();
		glClearColor(0.5f, 0.5f, 0.5f, 1);
		glViewport(0, 0, size.x, size.y);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
	    //
		////glEnable(GL_CULL_FACE);
		//glEnable(GL_BLEND);
		//glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
		ShaderManager.GENERAL.use();
		Matrix4f model_mat = new Matrix4f().identity();
		//TODO uniforms
		CAM.apply();
		glBindVertexArray(vao);
		TextureManager.bind("null");
		TextureManager.bind("t1p");
		ModelT1P.INSTANCE.render();
	    //TODO tex bind
		//TODO render
		ShaderManager.applyUniforms(cons -> {});
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

	public static void vsync(){
		log(String.format("Updating Vsync State [%s]", (Settings.VSYNC.value ? "+" : "-") + (Settings.VSYNC.value && Settings.HVSYNC.value ? "+" : "-")));
		glfwSwapInterval(Settings.VSYNC.value ? Settings.HVSYNC.value ? 2 : 1 : 0);
	}

	public static void icon(long window){
		try(MemoryStack stack = MemoryStack.stackPush()){
			ByteBuffer imgbuff;
			IntBuffer ch = stack.mallocInt(1), w = stack.mallocInt(1), h = stack.mallocInt(1);
			imgbuff = STBImage.stbi_load("./resources/textures/icon.png", w, h, ch, 4);
			if(imgbuff == null) return;
			GLFWImage image = GLFWImage.malloc();
			GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
			image.set(w.get(), h.get(), imgbuff);
			imagebf.put(0, image);
			glfwSetWindowIcon(window, imagebf);
		}
	}

	public static void close(){
		glfwSetWindowShouldClose(FMT.INSTANCE.window, true);
	}

}
