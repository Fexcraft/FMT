package net.fexcraft.app.fmt;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Timer;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.color.ColorConstants;
import org.liquidengine.legui.style.font.FontRegistry;
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
import net.fexcraft.app.fmt.ui.fields.Field;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.fmt.utils.MRTRenderer.DrawMode;
import net.fexcraft.app.fmt.utils.MRTRenderer.GlCache;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.tmt.BoxBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2021 fexcraft.net
 * */
public class FMT {

	public static final String VERSION = "3.0.0";
	public static final String TITLE = getCurrentTitle();
	public static final String CLID = "587016218196574209";
	//
	public static final FMT INSTANCE = new FMT();
	public static int WIDTH, HEIGHT, EXIT_CODE = 0;
	public static Timer BACKUP_TIMER;
	private static String title;
	//
	public static final ITimer timer = new ITimer();
	public float delta, accumulator, interval = 1f / 30f, alpha;
	private static boolean CLOSE;
	public static GGR CAM;
	public static Label pos, rot, fps, poly, info, bar;
	public static Label img_line0, img_line1;
	public static long bar_timer;
	//
	@SuppressWarnings("unused") private GLFWErrorCallback errorCallback;
	public long window;
	//
	public static float[] background;
	public static Frame FRAME, IMG_FRAME;
	public static Context CONTEXT;
	public static Renderer RENDERER;
	public static Toolbar TOOLBAR;
	public static Model MODEL;
	public static Field SELFIELD;
	
	public static void main(String... args) throws Exception {
		log("==================================================");
		log("Starting FMT " + VERSION + "!");
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
		window = glfwCreateWindow(WIDTH, HEIGHT, getTitle(null), MemoryUtil.NULL, MemoryUtil.NULL);
		if(window == MemoryUtil.NULL) throw new RuntimeException("Failed to create window");
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		icon(window);
		glfwShowWindow(window);
		glfwFocusWindow(window);
		//
		CAM = new GGR(5, -5, -5, -Static.rad45, Static.rad30);
		AxisRotator.setDefImpl(Axis3DL.class);
		Settings.applyTheme();
		FRAME = new Frame(WIDTH, HEIGHT);
		FRAME.getContainer().add(TOOLBAR = new Toolbar());
		EditorComponent.registerComponents();
		Settings.loadEditors();
		for(Editor editor : Editor.EDITORLIST) FRAME.getContainer().add(editor);
		FRAME.getContainer().add(pos = new Label("test", 0, 32, 200, 20));
		FRAME.getContainer().add(rot = new Label("test", 0, 54, 200, 20));
		FRAME.getContainer().add(fps = new Label("test", 0, 76, 200, 20));
		FRAME.getContainer().add(poly = new Label("test", 0, 98, 200, 20));
		FRAME.getContainer().add(info = new Label("test", 0, 120, 200, 20));
		FRAME.getContainer().add(bar = new Label("test", 0, 0, 500, 20));
		FRAME.getComponentLayer().setFocusable(false);
		CONTEXT = new Context(window);
		//
		IMG_FRAME = new Frame(WIDTH, HEIGHT);
		IMG_FRAME.getContainer().add(img_line0 = new Label("", 20, 20, 500, 20));
		img_line0.getStyle().getBackground().setColor(ColorConstants.transparent());
		img_line0.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		IMG_FRAME.getContainer().add(img_line1 = new Label("", 20, 40, 500, 20));
		img_line1.getStyle().getBackground().setColor(ColorConstants.transparent());
		img_line1.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
		//
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
				Picker.resetBuffer(true);
			}
		});
		keeper.getChainScrollCallback().add(new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset){
				if(SELFIELD == null) CAM.scrollCallback(window, xoffset, yoffset);
				else SELFIELD.scroll(yoffset);
			}
		});
		SystemEventProcessor sys_event_processor = new SystemEventProcessorImpl();
		SystemEventProcessor.addDefaultCallbacks(keeper, sys_event_processor);
		RENDERER = new NvgRenderer();
		RENDERER.initialize();
		TextureManager.load();
		//FMT.MODEL = new Model(new File("./saves/dodici.fmtb"), null).load();
		FMT.MODEL = new Model(null, "Unnamed Model");
		FMT.updateTitle();
		//TODO load previous model
		Settings.checkForUpdatesAndLogin();
		KeyCompound.init();
		//
		LocalDateTime midnight = LocalDateTime.of(LocalDate.now(ZoneOffset.systemDefault()), LocalTime.MIDNIGHT);
		long mid = midnight.toInstant(ZoneOffset.UTC).toEpochMilli();
		long date = Time.getDate();
		while((mid += Time.MIN_MS * 5) < date);
		if(BACKUP_TIMER == null && Settings.BACKUP_INTERVAL.value > 0){
			(BACKUP_TIMER = new Timer("BKUP")).schedule(new BackupHandler(), new Date(mid), Time.MIN_MS * Settings.BACKUP_INTERVAL.value);
		}
		//TODO tex timer
		//
		if(Settings.DISCORD_RPC.value){
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
			DiscordUtil.DISCORD_THREAD = new Thread(() -> {
				while(!CLOSE){
					DiscordRPC.discordRunCallbacks();
					try{
						Thread.sleep(100);
					}
					catch(InterruptedException e){
						e.printStackTrace();
					}
				}
			});
			DiscordUtil.DISCORD_THREAD.setName("DRPC");
			DiscordUtil.DISCORD_THREAD.start();
		}
		//
		vsync();
		ShaderManager.loadPrograms();
		ModelRendererTurbo.RENDERER = new MRTRenderer();
		int vao = glGenVertexArrays();
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		Picker.resetBuffer(true);
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
				info.getTextState().setText(SELFIELD == null ? "none" : SELFIELD.polyval() == null ? SELFIELD.setting() == null ? "other" : "setting:" + SELFIELD.setting().id : SELFIELD.polyval().toString());
				poly.getTextState().setText(MODEL.selected().isEmpty() ? "none" : MODEL.first_selected().name());
			}
			render(vao, alpha = accumulator / interval);
			//
			adjustLabels();
			ImageHandler.updateText();
			RENDERER.render(ImageHandler.shouldHide() ? IMG_FRAME : FRAME, CONTEXT);
			timer.updateFPS();
			glfwPollEvents();
			glfwSwapBuffers(window);
			sys_event_processor.processEvents(FRAME, CONTEXT);
			EventProcessorProvider.getInstance().processEvents();
			LayoutManager.getInstance().layout(FRAME);
			AnimatorProvider.getAnimator().runAnimations();
			ImageHandler.processTask();
			timer.update();
		}
		DiscordRPC.discordShutdown();
		RENDERER.destroy();
		glfwDestroyWindow(window);
		glfwTerminate();
		Settings.save();
		//TODO other saves
		System.exit(EXIT_CODE);
	}

	private void adjustLabels(){
		int xoff = Editor.LEFT == null ? 5 : 320;
		pos.setPosition(xoff, pos.getPosition().y);
		rot.setPosition(xoff, rot.getPosition().y);
		fps.setPosition(xoff, fps.getPosition().y);
		poly.setPosition(xoff, poly.getPosition().y);
		info.setPosition(xoff, info.getPosition().y);
		if(!Settings.SHOW_BOTTOMBAR.value) return;
		if(bar_timer == 0 || Time.getDate() >= bar_timer){
			bar_timer = 0;
			bar.setPosition(0, HEIGHT + 20);
			bar.getTextState().setText("");
		}
		else{
			bar.setPosition((WIDTH / 2) - (FontSizeUtil.getWidth(bar.getTextState().getText()) / 2), HEIGHT - 20);
		}
	}

	private void render(int vao, float alpha){
		//glClearColor(0.5f, 0.5f, 0.5f, 0.01f);
		CONTEXT.updateGlfwWindow();
		Vector2i size = CONTEXT.getFramebufferSize();
		glViewport(0, 0, size.x, size.y);
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
	    //
		ShaderManager.GENERAL.use();
		CAM.apply();
		glBindVertexArray(vao);
		TextureManager.bind("null");
		if(Picker.TYPE.polygon()){
			glClearColor(1, 1, 1, 1);
		    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			MRTRenderer.mode(DrawMode.POLYGON_PICKER);
			MODEL.renderPicking();
			Picker.process();
			if(Picker.TYPE.face()){
				glClearColor(1, 1, 1, 1);
			    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
				MRTRenderer.mode(DrawMode.FACE_PICKER);
				Picker.polygon().turbo.render();
				Picker.process();
			}
		    Picker.reset();
		}
		glClearColor(background[0], background[1], background[2], 1);
	    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		MRTRenderer.mode(DrawMode.TEXTURED);
		if(Settings.CUBE.value){
			TextureManager.bind("demo");
			center_cube.render();
		}
		if(Settings.FLOOR.value){
			TextureManager.bind("floor");
			floor.render();
		}
		if(Settings.DEMO.value){
			TextureManager.bind("t1p");
			ModelT1P.INSTANCE.render();
		}
		if(Settings.CMARKER.value){
			MRTRenderer.mode(DrawMode.RGBCOLOR);
            centermarker0.render(0.0625f / 4);
            centermarker1.render(0.0625f / 4);
            centermarker2.render(0.0625f / 4);
		}
		MODEL.render();
	}
	
	public static final ModelRendererTurbo center_cube = new BoxBuilder(new ModelRendererTurbo(null, 0, 0, 16, 16))
		.setSize(16, 16, 16).setOffset(-8, 0, -8).build();
	public static final ModelRendererTurbo floor = new BoxBuilder(new ModelRendererTurbo(null, 0, 0, 512, 512))
		.setSize(512, 0, 512).setOffset(-256, 10, -256).removePolygons(0, 1, 4, 5)
		.setPolygonUV(2, new float[]{ 512, 0, 512, 512, 0, 512, 0, 0 })
		.setPolygonUV(3, new float[]{ 512, 0, 512, 512, 0, 512, 0, 0 }).build();
	private static final ModelRendererTurbo centermarker0 = new ModelRendererTurbo(null, 0, 0, 0, 0).addBox(-0.5f, -256, -0.5f, 1, 512, 1).setTextured(false).setColor(RGB.GREEN.copy());
	private static final ModelRendererTurbo centermarker1 = new ModelRendererTurbo(null, 0, 0, 0, 0).addBox(-256, -0.5f, -0.5f, 512, 1, 1).setTextured(false).setColor(RGB.RED.copy());
	private static final ModelRendererTurbo centermarker2 = new ModelRendererTurbo(null, 0, 0, 0, 0).addBox(-0.5f, -0.5f, -256, 1, 1, 512).setTextured(false).setColor(RGB.BLUE.copy());
	static {
		centermarker0.glObject(new GlCache()).polycolor = centermarker0.getColor().toFloatArray();
		centermarker1.glObject(new GlCache()).polycolor = centermarker1.getColor().toFloatArray();
		centermarker2.glObject(new GlCache()).polycolor = centermarker2.getColor().toFloatArray();
	}
	
	public static final String getCurrentTitle(){
		String f = Static.random.nextInt(2) == 0 ? "Fex's " : "Fexcraft ";
		String m = Static.random.nextInt(2) == 0 ? "Modelling " : "Modding ";
		String t = Static.random.nextInt(2) == 0 ? "Toolbox " : "Toolset ";
		return f + m + t + VERSION + " - %s";
	}
	
	public static final String getTitle(String title){
		return String.format(TITLE, title == null ? FMT.title : title);
	}
	
	public FMT setTitle(String string){
		glfwSetWindowTitle(window, getTitle(title = string));
		DiscordUtil.update(false);
		return this;
	}

	public static FMT updateTitle(){
		return INSTANCE.setTitle(FMT.MODEL.name);
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
    	RGB rgb = new RGB(i);
    	rgb.alpha = a;
    	return rgba(rgb);
    }

    public static final Vector4f rgba(RGB rgb){
    	float[] arr = rgb.toFloatArray();
    	return new Vector4f(arr[0], arr[1], arr[2], arr[3]);
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

	public static void close(int exit_code){
		EXIT_CODE = exit_code;
		glfwSetWindowShouldClose(FMT.INSTANCE.window, true);
	}

	public static long getWindow(){
		return INSTANCE.window;
	}

}
