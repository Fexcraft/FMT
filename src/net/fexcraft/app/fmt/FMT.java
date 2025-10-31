package net.fexcraft.app.fmt;

import com.spinyowl.legui.animation.AnimatorProvider;
import com.spinyowl.legui.component.Frame;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.listener.processor.EventProcessorProvider;
import com.spinyowl.legui.style.color.ColorConstants;
import com.spinyowl.legui.style.font.FontRegistry;
import com.spinyowl.legui.system.context.CallbackKeeper;
import com.spinyowl.legui.system.context.Context;
import com.spinyowl.legui.system.context.DefaultCallbackKeeper;
import com.spinyowl.legui.system.handler.processor.SystemEventProcessor;
import com.spinyowl.legui.system.handler.processor.SystemEventProcessorImpl;
import com.spinyowl.legui.system.layout.LayoutManager;
import com.spinyowl.legui.system.renderer.Renderer;
import com.spinyowl.legui.system.renderer.RendererProvider;
import com.spinyowl.legui.system.renderer.nvg.NvgRenderer;
import net.fexcraft.app.fmt.animation.Animation;
import net.fexcraft.app.fmt.demo.ModelT1P;
import net.fexcraft.app.fmt.env.PackDevEnv;
import net.fexcraft.app.fmt.nui.FMTInterface;
import net.fexcraft.app.fmt.nui.FontRenderer;
import net.fexcraft.app.fmt.polygon.Arrows;
import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.texture.TextureUpdate;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.Toolbar;
import net.fexcraft.app.fmt.ui.ToolbarMenu;
import net.fexcraft.app.fmt.ui.UVViewer;
import net.fexcraft.app.fmt.ui.fields.Field;
import net.fexcraft.app.fmt.ui.trees.PolygonTree;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.fmt.workspace.Workspace;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.frl.GLO;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.gen.Generator;
import net.fexcraft.lib.tmt.BoxBuilder;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import org.joml.Vector4f;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.ConcurrentLinkedQueue;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2021 fexcraft.net
 * */
public class FMT {

	public static String VERSION = "3.0.0";
	public static String TITLE;
	//
	public static final FMT INSTANCE = new FMT();
	public static int WIDTH, HEIGHT, EXIT_CODE = 0;
	public static Timer BACKUP_TIMER, TEXUP_TIMER;
	private static String title;
	//
	public static final ITimer timer = new ITimer();
	public float delta, accumulator, interval = 1f / 30f;
	public float alpha = 0f;
	private static boolean CLOSE;
	public static GGR CAM;
	public static Label pos;
	public static Label rot;
	public static Label fps;
	public static Label poly;
	public static Label info;
	public static Label bar;
	public static Label img_line0;
	public static Label img_line1;
	public static long bar_timer;
	//
	@SuppressWarnings("unused") private GLFWErrorCallback errorCallback;
	public long window;
	//
	public static float[] background;
	public static FMTInterface UI;
	//
	public static Frame FRAME, IMG_FRAME;
	public static Context CONTEXT;
	public static Renderer RENDERER;
	public static Toolbar TOOLBAR;
	public static Model MODEL;
	public static Field SELFIELD;
	public static Workspace WORKSPACE;
	//
	static{
		GLO.SUPPLIER = () -> new GLObject();
	}
	public static ConcurrentLinkedQueue<Runnable> RUN_QUEUE = new ConcurrentLinkedQueue<>();

	private SystemEventProcessor sys_event_processor;

	public static void main(String... args) throws Exception {
		try{
			JsonMap cat = JsonHandler.parse(new File("./catalog.fmt")).asMap();
			if(cat.has("fmt_version")) VERSION = cat.get("fmt_version").string_value();
		}
		catch(Exception e){
			e.printStackTrace();
		}
		TITLE = getCurrentTitle();
		log("==================================================");
		log("Starting FMT " + VERSION + "!");
        System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
        if(System.getProperty("os.name").toLowerCase().contains("mac")){
        	System.setProperty("java.awt.headless", Boolean.TRUE.toString());
        }
		log("Running on " + System.getProperty("os.name") + " / " + System.getProperty("os.version") + " [" + System.getProperty("os.arch") + "]");
		log("Java " + System.getProperty("java.version") + " (" + System.getProperty("java.home") + ")");
	    System.setProperty("org.lwjgl.librarypath", new File("./lib/").getAbsolutePath());
		Configuration.SHARED_LIBRARY_EXTRACT_DIRECTORY.set("./lib/natives");
		Configuration.SHARED_LIBRARY_EXTRACT_PATH.set("./lib/natives");
	    //
		Settings.load();
		Settings.apply(INSTANCE);
		//Converter.run();
		//SCRIPT = new Script(new FileInputStream("./scripts/test.script"), "test");
		//Logging.log(SCRIPT.print());
		//net.fexcraft.lib.scr.Script scr = ScriptParser.parse("test", new FileInputStream("./scripts/test.script"));
		//Logging.log(scr.toString());
		//System.exit(0);
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
		log("GL Renderer: " + glGetString(GL_RENDERER));
		log("GL Vendor:   " + glGetString(GL_VENDOR));
		log("GL Version:  " + glGetString(GL_VERSION));
		icon(window);
		glfwShowWindow(window);
		glfwFocusWindow(window);
		//
		CAM = new GGR();
		AxisRotator.setDefImpl(Axis3DL.class);
		Settings.applyTheme();
		FRAME = new Frame(WIDTH, HEIGHT);
		FRAME.getContainer().add(TOOLBAR = new Toolbar());
		Editor.loadEditors();
		FRAME.getContainer().add(pos = new Label("test", 0, 32, 200, 20));
		FRAME.getContainer().add(rot = new Label("test", 0, 54, 200, 20));
		FRAME.getContainer().add(fps = new Label("test", 0, 76, 200, 20));
		FRAME.getContainer().add(poly = new Label("test", 0, 98, 200, 20));
		FRAME.getContainer().add(info = new Label("test", 0, 120, 200, 20));
		FRAME.getContainer().add(bar = new Label("test", 0, 0, 500, 25));
		bar.getStyle().setTextColor(rgba(Settings.BOTTOM_INFO_BAR_COLOR.value));
		bar.getStyle().setFontSize(bar.getSize().y);
		FRAME.getComponentLayer().setFocusable(false);
		sys_event_processor = new SystemEventProcessorImpl();
		CONTEXT = new Context(window, sys_event_processor);
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
				CONTEXT.updateGlfwWindow();
				resize();
			}
		});
		keeper.getChainScrollCallback().add(new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset){
				if(SELFIELD == null) CAM.scrollCallback(window, xoffset, yoffset);
				else SELFIELD.scroll(yoffset);
			}
		});
		SystemEventProcessor.addDefaultCallbacks(keeper, sys_event_processor);
		RendererProvider.getInstance().addComponentRenderer(UVViewer.UvElm.class, new UVViewer.UvElmRenderer());
		RENDERER = new NvgRenderer();
		RENDERER.initialize();
		TextureManager.load();
		if(Settings.TESTING.value){
			FontRenderer.init();
			UI = new FMTInterface();
		}
		Animation.init();
		FMT.WORKSPACE = new Workspace();
		FMT.updateTitle();
		if(FMT.MODEL.file != null) FMT.MODEL.load();
		Settings.checkForUpdatesAndLogin();
		KeyCompound.init();
		//
		LocalDateTime midnight = LocalDateTime.of(LocalDate.now(ZoneOffset.systemDefault()), LocalTime.MIDNIGHT);
		long mid = midnight.toInstant(ZoneOffset.UTC).toEpochMilli();
		long date = Time.getDate();
		while((mid += Time.MIN_MS * 5) < date);
		if(BACKUP_TIMER == null && Settings.BACKUP_INTERVAL.value > 0){
			(BACKUP_TIMER = new Timer("BACKUP")).schedule(new BackupHandler(), new Date(mid), Time.MIN_MS * Settings.BACKUP_INTERVAL.value);
		}
		if(TEXUP_TIMER == null){
			(TEXUP_TIMER = new Timer("TEXUPD")).schedule(new TextureUpdate(), Time.SEC_MS, Time.SEC_MS / 2);
		}
		//
		if(Settings.DISCORD_RPC.value) DiscordUtil.start();
		//
		vsync();
		ShaderManager.loadPrograms();
		net.fexcraft.lib.frl.Renderer.RENDERER = new PolyRenderer();
		int vao = glGenVertexArrays();
		glEnable(GL_DEPTH_TEST);
		glDepthFunc(GL_LESS);
		//
		CONTEXT.updateGlfwWindow();
		resize();
		while(!glfwWindowShouldClose(window)){
			FRAME.getContainer().setSize(WIDTH, HEIGHT);
			CAM.pollInput(accumulator += (delta = timer.getDelta()));
			//accumulator += (delta = timer.getDelta());
			while(accumulator >= interval){
				while(RUN_QUEUE.peek() != null) RUN_QUEUE.poll().run();
				//TODO "logic"
				CAM.update();
				if(Settings.ANIMATE.value) FMT.MODEL.updateAnimations();
				if(Settings.TESTING.value) UI.update();
				ToolbarMenu.checkHide();
				timer.updateUPS();
				accumulator -= interval;
				//Trees.updateCounters();
				fps.getTextState().setText(timer.getFPS() + "");
				info.getTextState().setText(SELFIELD == null ? "none" : SELFIELD.polyval() == null ? SELFIELD.setting() == null ? "other" : "setting:" + SELFIELD.setting().id : SELFIELD.polyval().toString());
				poly.getTextState().setText(MODEL.selected().isEmpty() ? "none" : MODEL.first_selected().name());
			}
			alpha = accumulator / interval;
			render(vao, alpha);
			//
			adjustLabels();
			ImageHandler.updateText();
			if(!Settings.TESTING.value) RENDERER.render(ImageHandler.shouldHide() ? IMG_FRAME : FRAME, CONTEXT);
			timer.updateFPS();
			glfwPollEvents();
			glfwSwapBuffers(window);
			if(!Settings.TESTING.value){
				sys_event_processor.processEvents(FRAME, CONTEXT);
				EventProcessorProvider.getInstance().processEvents();
				LayoutManager.getInstance().layout(FRAME);
				AnimatorProvider.getAnimator().runAnimations();
				ImageHandler.processTask();
			}
			timer.update();
		}
		Settings.save();
		SessionHandler.save();
		PackDevEnv.save();
		//TODO other saves
		RENDERER.destroy();
		glfwDestroyWindow(window);
		glfwTerminate();
		System.exit(EXIT_CODE);
	}

	private void resize(){
		int width = (int)(CONTEXT.getFramebufferSize().x * (1f / CONTEXT.getScale().x));
		int height = (int)(CONTEXT.getFramebufferSize().y * (1f / CONTEXT.getScale().y));
		WIDTH = CONTEXT.getFramebufferSize().x;
		HEIGHT = CONTEXT.getFramebufferSize().y;
		log("Resizing Window to " + width + "/" + height + " (" + WIDTH + "/" + HEIGHT + " scaled at " + (1f / CONTEXT.getScale().x) + "/" + (1f / CONTEXT.getScale().y) + ").");
		HEIGHT = height;
		TOOLBAR.setSize(WIDTH = width, TOOLBAR.getSize().y);
		Editor.EDITORS.forEach(editor -> editor.align());
		ToolbarMenu.MENUS.forEach((key, menu) -> menu.layer.hide());
		Picker.resetBuffer(true);
		if(UI != null) UI.onResize();
	}

	private void adjustLabels(){
		int xoff = Editor.VISIBLE_EDITOR == null ? 5 : 320;
		int yoff = 1;
		pos.setPosition(xoff, HEIGHT - yoff++ * 22);
		rot.setPosition(xoff, HEIGHT - yoff++ * 22);
		fps.setPosition(xoff, HEIGHT - yoff++ * 22);
		poly.setPosition(xoff, HEIGHT - yoff++ * 22);
		info.setPosition(xoff, HEIGHT - yoff++ * 22);
		if(!Settings.SHOW_BOTTOMBAR.value) return;
		if(bar_timer == 0 || Time.getDate() >= bar_timer){
			bar_timer = 0;
			bar.setPosition(0, HEIGHT + bar.getSize().y);
			bar.getTextState().setText("");
		}
		else{
			bar.setPosition((WIDTH / 2) - (FontSizeUtil.getWidth(bar.getTextState().getText()) / 2), HEIGHT - bar.getSize().y);
		}
		PolygonTree.polygons.getTextState().setText("Polygons: " + FMT.MODEL.totals());
	}

	private void render(int vao, float alpha){
		//glClearColor(0.5f, 0.5f, 0.5f, 0.01f);
		glViewport(0, 0, WIDTH, HEIGHT);
		CONTEXT.updateGlfwWindow();
		glEnable(GL_CULL_FACE);
		glEnable(GL_BLEND);
		glBlendFunc(GL_ONE, GL_ONE_MINUS_SRC_ALPHA);
	    //
		ShaderManager.GENERAL.use();
		glBindVertexArray(vao);
		TextureManager.bind("null");
		if(Picker.TYPE.polygon()){
			glClearColor(1, 1, 1, 1);
		    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			PolyRenderer.mode(DrawMode.PICKER);
			//if(Arrows.MODE.active()) Arrows.render(DrawMode.PICKER);
			MODEL.renderPicking();
			Picker.process();
			if(Picker.TYPE.face()){
				glClearColor(1, 1, 1, 1);
			    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			    boolean tex = Picker.TASK.paint() && TexturePainter.SELMODE.pixel();
				PolyRenderer.mode(tex ? DrawMode.TEXTURED : DrawMode.PICKER_FACE);
				if(!tex || TexturePainter.bindTex()){
					Picker.polygon().glm.render();
					Picker.process();
				}
			}
		    Picker.reset();
		}
		if(Picker.TYPE.vertex()){
			glClearColor(1, 1, 1, 1);
		    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			PolyRenderer.mode(DrawMode.RGBCOLOR);
			MODEL.renderVertexPicking();
			Picker.process();
			Picker.reset();
		}
		if(Settings.TESTING.value){
			if(Picker.TASK == Picker.PickTask.NONE){
				Picker.pick(Picker.PickType.UI, Picker.PickTask.HOVER, true);
			}
			CAM.ortho(1);
			glClearColor(1, 1, 1, 1);
		    glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
			PolyRenderer.mode(DrawMode.PICKER);
			UI.render(Picker.TASK);
			Picker.process();
			glClearColor(background[0], background[1], background[2], 1);
	    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
	    	PolyRenderer.mode(DrawMode.UI);
			UI.render(null);
		}
		else{
			glClearColor(background[0], background[1], background[2], 1);
	    	glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		}
		CAM.apply();
	    PolyRenderer.mode(DrawMode.TEXTURED);
		PolyRenderer.updateLightState();
		if(Settings.CUBE.value){
			TextureManager.bind("demo");
			center_cube.render();
		}
		if(Settings.FLOOR.value){
			TextureManager.bind(MODEL.orient.floor_texture);
			floor.posY = MODEL.orient.floor_height;
			(MODEL.orient.rect() ? floor0 : floor).render();
		}
		if(Settings.DEMO.value){
			TextureManager.bind("t1p");
			ModelT1P.INSTANCE.render();
		}
		if(Settings.CMARKER.value){
			PolyRenderer.mode(DrawMode.RGBCOLOR);
			centermarker0.render();
			centermarker1.render();
			centermarker2.render();
		}
		if(Arrows.MODE.active()) Arrows.render(DrawMode.RGBCOLOR); 
		MODEL.render(alpha);
		if(Selector.TYPE == Picker.PickType.VERTEX || Selector.SHOW_VERTICES){
			PolyRenderer.mode(DrawMode.RGBCOLOR);
			MODEL.renderVertexPicking();
		}
		for(Model model : PreviewHandler.getLoaded()){
			if(!model.visible) continue;
			PolyRenderer.setHelper(model);
			model.render(alpha);
		}
		PolyRenderer.setHelper(null);
	}
	
	public static final Polyhedron<GLObject> center_cube = new Polyhedron<GLObject>().importMRT(new BoxBuilder(new ModelRendererTurbo(null, 0, 0, 16, 16))
		.setSize(16, 16, 16).setOffset(-8, 0, -8).build(), false, 1f).setGlObj(new GLObject());;
	public static final Polyhedron<GLObject> floor = new Polyhedron<GLObject>().importMRT(new BoxBuilder(new ModelRendererTurbo(null, 0, 0, 512, 512))
		.setSize(512, 0, 512).setOffset(-256, 0, -256).removePolygons(0, 1, 4, 5)
		.setPolygonUV(2, new float[]{ 512, 0, 512, 512, 0, 512, 0, 0 })
		.setPolygonUV(3, new float[]{ 512, 0, 512, 512, 0, 512, 0, 0 }).build(), false, 1f).setGlObj(new GLObject());
	public static final Polyhedron<GLObject> floor0 = new Polyhedron<GLObject>().importMRT(new BoxBuilder(new ModelRendererTurbo(null, 0, 0, 512, 512))
		.setSize(512, 0, 512).setOffset(-256, 0, -256).removePolygons(0, 1, 4, 5)
		.setPolygonUV(2, new float[]{ 0, 512, 0, 0, 512, 0, 512, 512})
		.setPolygonUV(3, new float[]{ 512, 512, 512, 0, 0, 0,  0, 512 }).build(), false, 1f).setGlObj(new GLObject());
	private static final Polyhedron<GLObject> centermarker0 = new Generator<GLObject>(null, Generator.Type.CUBOID)
		.set("x", -.125f).set("y", -256f).set("z", -.125f).set("width", .25f).set("height", 512f).set("depth", .25f).make().setGlObj(new GLObject());
	private static final Polyhedron<GLObject> centermarker1 = new Generator<GLObject>(null, Generator.Type.CUBOID)
		.set("x", -256f).set("y", -.125f).set("z", -.125f).set("width", 512f).set("height", .25f).set("depth", .25f).make().setGlObj(new GLObject());
	private static final Polyhedron<GLObject> centermarker2 = new Generator<GLObject>(null, Generator.Type.CUBOID)
		.set("x", -.125f).set("y", -.125f).set("z", -256f).set("width", .25f).set("height", .25f).set("depth", 512f).make().setGlObj(new GLObject());
	static {
		centermarker0.glObj.polycolor = RGB.GREEN.toFloatArray();
		centermarker1.glObj.polycolor = RGB.RED.toFloatArray();
		centermarker2.glObj.polycolor = RGB.BLUE.toFloatArray();
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
		return INSTANCE.setTitle(FMT.WORKSPACE.name + " - " +  FMT.MODEL.name);
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
		char a = Settings.FVSYNC.value ? '+' : '-';
		char b = Settings.HVSYNC.value ? '+' : '-';
		char c = Settings.QVSYNC.value ? '+' : '-';
		log(String.format("Updating Vsync State [%s]", a + b + c));
		int in = Settings.FVSYNC.value ? 1 : 0;
		if(in > 0 && Settings.HVSYNC.value) in = 2;
		if(in > 0 && Settings.QVSYNC.value) in = 4;
		glfwSwapInterval(in);
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
