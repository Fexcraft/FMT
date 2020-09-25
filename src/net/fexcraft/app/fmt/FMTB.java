package net.fexcraft.app.fmt;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.lwjgl.glfw.GLFW.*;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Timer;

import javax.script.ScriptException;

import org.joml.Vector2i;
import org.joml.Vector4f;
import org.liquidengine.legui.animation.AnimatorProvider;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.BufferedImage;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.listener.processor.EventProcessorProvider;
import org.liquidengine.legui.style.Style.DisplayType;
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
import org.liquidengine.legui.theme.Themes;
import org.liquidengine.legui.theme.colored.FlatColoredTheme;
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

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.fexcraft.app.fmt.demo.ModelT1P;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.MenuButton;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.editor.ModelEditor;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.field.Field;
import net.fexcraft.app.fmt.ui.field.TextField;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.utils.texture.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.AxisRotator;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.HttpUtil;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2020 fexcraft.net
 * */
public class FMTB {

	public static final String VERSION = "2.6.5";
	public static final String deftitle = "[FPS:%s] Fexcraft Modelling Toolbox " + VERSION + " - %s";
	public static final String CLID = "587016218196574209";
	//
	public static GGR ggr;
	private boolean close;
	private static String title = "Unnamed Model";
	private static FMTB INSTANCE;
	public static GroupCompound MODEL = new GroupCompound(null);
	public static Timer BACKUP_TIMER, TEX_UPDATE_TIMER;
	private static int disk_update;
	public static String NO_POLYGON_SELECTED, NO_PREVIEW_SELECTED;
	//
	public static final ST_Timer timer = new ST_Timer();
	public float delta, accumulator = 0f, interval = 1f / 30f, alpha;
	//
	public static GLFWErrorCallback errorCallback;
	public static boolean hold_right, hold_left, field_scrolled;
	public static int WIDTH = 1280, HEIGHT = 720;
	public static Context context;
	public static Renderer renderer;
	public static Frame frame, ss_frame;
	public static Label ss_title, ss_credits;
	public static long window;
	public static ImageView cursor;
	
	public static void main(String... args) throws Exception {
		log("==================================================");
		log("Starting FMT! " + VERSION);
        System.setProperty("joml.nounsafe", Boolean.TRUE.toString());
        if(System.getProperty("os.name").toLowerCase().contains("mac")){
        	System.setProperty("java.awt.headless", Boolean.TRUE.toString());
        }
		log("Running on " + System.getProperty("os.name") + " / " + System.getProperty("os.version"));
	    System.setProperty("org.lwjgl.librarypath", new File("./lib/").getAbsolutePath());
		Configuration.SHARED_LIBRARY_EXTRACT_DIRECTORY.set("./lib/natives");
		Configuration.SHARED_LIBRARY_EXTRACT_PATH.set("./lib/natives");
	    //
		//File[] folders = { new File("./saves"), new File("./imports"), new File("./exports") };
		//for(File folder : folders){ if(!folder.exists()) folder.mkdirs(); }
		FMTB.INSTANCE = new FMTB();
		try{
			INSTANCE.run();
		}
		catch(Throwable thr){
			log(thr);
			System.exit(1);
		}
	}

	public static final FMTB get(){ return INSTANCE; }
	
	public FMTB setTitle(String string){ title = string; DiscordUtil.update(false); return this; }
	
	public void run() throws InterruptedException, IOException, NoSuchMethodException, ScriptException{
		TextureManager.init();
		Settings.load();
		Translator.init();
		timer.init();
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW.");
		glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
		glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
		// glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
		window = glfwCreateWindow(WIDTH, HEIGHT, "Fex's Modelling Toolbox", MemoryUtil.NULL, MemoryUtil.NULL);
		if(window == MemoryUtil.NULL) throw new RuntimeException("Failed to create window");
		glfwMakeContextCurrent(window);
		GL.createCapabilities();
		// GLUtil.setupDebugMessageCallback();
		initOpenGL();
		this.setIcon();
		glfwShowWindow(window);
		glfwFocusWindow(window);
		ggr = new GGR(0, 4, 4);
		ggr.rotation.xCoord = 45;
		//
		AxisRotator.setDefImpl(Axis3DL.class);
		NO_POLYGON_SELECTED = Translator.translate("error.no_polygon_selected");
		NO_PREVIEW_SELECTED = Translator.translate("error.no_preview_selected");
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			if(bool){
				Themes.setDefaultTheme(new FlatColoredTheme(rgba(33, 33, 33, 1), // backgroundColor
					rgba(97, 97, 97, 1), // borderColor
					rgba(97, 97, 97, 1), // sliderColor
					rgba(2, 119, 189, 1), // strokeColor
					rgba(27, 94, 32, 1), // allowColor
					rgba(183, 28, 28, 1), // denyColor
					ColorConstants.transparent(), // shadowColor
					ColorConstants.lightGray(), // text color
					FontRegistry.ROBOTO_LIGHT, // font
					20f // font size
				));
			}
			else{
				Themes.setDefaultTheme(new FlatColoredTheme(rgba(245, 245, 245, 1), // backgroundColor
					rgba(176, 190, 197, 1), // borderColor
					rgba(176, 190, 197, 1), // sliderColor
					rgba(100, 181, 246, 1), // strokeColor
					rgba(165, 214, 167, 1), // allowColor
					rgba(239, 154, 154, 1), // denyColor
					ColorConstants.transparent(), // shadowColor
					ColorConstants.darkGray(), // text color
					FontRegistry.ROBOTO_LIGHT, // font
					20f // font size
				));
			}
			if(frame != null) Themes.getDefaultTheme().applyAll(frame);
		});
		Settings.updateTheme();
		frame = new Frame(WIDTH, HEIGHT);
		// frame.getContainer().add(new Interface());
		Trees.initializeTrees(frame);
		Editors.initializeEditors(frame);
		UserInterfaceUtils.addToolbarButtons(frame);
		PorterManager.load();
		// TabContainer.addTest(frame);
		boolean loadedold = false;
		File file = new File(Settings.SETTINGS.get("last_file").getStringValue());
		if(file.exists() && file.getName().endsWith(".fmtb")){
			SaveLoad.openModel(file);
			loadedold = true;
		}
		MODEL.initButton();
		if(Settings.internal_cursor()){
			cursor = new ImageView(new BufferedImage("./resources/textures/cursor.png"));
			cursor.setSize(16, 16);
			cursor.setFocusable(false);
			cursor.setEnabled(false);
			Settings.THEME_CHANGE_LISTENER.add(bool -> {
				cursor.getStyle().getBackground().setColor(ColorConstants.transparent());
				cursor.getStyle().getBorder().setEnabled(false);
				// cursor.getStyle().setBorderRadius(0);
			});
			frame.getContainer().add(cursor);
		}
		context = new Context(window);
		context.setDebugEnabled(Settings.ui_debug());
		frame.getComponentLayer().setFocusable(false);
		CallbackKeeper keeper = new DefaultCallbackKeeper();
		CallbackKeeper.registerCallbacks(window, keeper);
		//
		keeper.getChainKeyCallback().add(new GLFWKeyCallback(){
			@Override
			public void invoke(long window, int key, int scancode, int action, int mods){
				if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE) reset();
				if(context.getFocusedGui() instanceof Field || context.getFocusedGui() instanceof TextField) return;
				KeyCompound.process(window, key, scancode, action, mods);
			}
		});
		keeper.getChainCursorPosCallback().add(new GLFWCursorPosCallback(){
			@Override
			public void invoke(long window, double xpos, double ypos){
				ggr.cursorPosCallback(window, xpos, ypos);
				if(cursor != null){
					cursor.setPosition((float)xpos - 8, (float)ypos - 8);
				}
			}
		});
		keeper.getChainMouseButtonCallback().add(new GLFWMouseButtonCallback(){
			@Override
			public void invoke(long window, int button, int action, int mods){
				ggr.mouseCallback(window, button, action, mods);
			}
		});
		keeper.getChainWindowCloseCallback().add(new GLFWWindowCloseCallback(){
			@Override
			public void invoke(long window){
				SaveLoad.checkIfShouldSave(true, false);
			}
		});
		keeper.getChainFramebufferSizeCallback().add(new GLFWFramebufferSizeCallback(){
			@Override
			public void invoke(long window, int width, int height){
				resize(width, height);
			}
		});
		keeper.getChainScrollCallback().add(new GLFWScrollCallback(){
			@Override
			public void invoke(long window, double xoffset, double yoffset){
				if(!Settings.no_scroll_fields() && (field_scrolled = (context.getFocusedGui() instanceof Field))){
					if(!context.getFocusedGui().isHovered()){
						context.setFocusedGui(null);
						field_scrolled = false;
						return;
					}
					Field field = (Field)context.getFocusedGui();
					if(field.id() != null || field.update() != null) field.onScroll(yoffset);
				}
				else if(Editors.anyCurrentHovered()){
					Editors.getVisible().modifyCurrent(yoffset);
				}
				else if(!GGR.isNotOverUI()) return;
				else ggr.scrollCallback(window, xoffset, yoffset);
			}
		});
		SystemEventProcessor systemEventProcessor = new SystemEventProcessorImpl();
		SystemEventProcessor.addDefaultCallbacks(keeper, systemEventProcessor);
		renderer = new NvgRenderer();
		renderer.initialize();
		Settings.updateTheme();
		ModelEditor.creators.refresh();
		//
		HelperCollector.reload(loadedold);
		SessionHandler.checkIfLoggedIn(true, true);
		checkForUpdates();
		KeyCompound.init();
		KeyCompound.load();
		if(!loadedold){
			FMTB.MODEL.updateFields();
		}
		//
		LocalDateTime midnight = LocalDateTime.of(LocalDate.now(ZoneOffset.systemDefault()), LocalTime.MIDNIGHT);
		long mid = midnight.toInstant(ZoneOffset.UTC).toEpochMilli();
		long date = Time.getDate();
		while((mid += Time.MIN_MS * 5) < date);
		if(BACKUP_TIMER == null){
			(BACKUP_TIMER = new Timer("backup")).schedule(new Backups(), new Date(mid), Time.MIN_MS * 5);
		}
		if(TEX_UPDATE_TIMER == null){
			(TEX_UPDATE_TIMER = new Timer("tex-update")).schedule(new TextureUpdate(), Time.SEC_MS, Time.SEC_MS / 2);
		}
		//
		if(Settings.discordrpc()){
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
		}
		updateVsync();
		//
		while(!close){
			ggr.pollInput(accumulator += (delta = timer.getDelta()));
			while(accumulator >= interval){
				loop();
				timer.updateUPS();
				accumulator -= interval;
				Trees.updateCounters();
			}
			render(alpha = accumulator / interval);
			if(!RayCoastAway.PICKING){
				renderUI(ImageHelper.HASTASK);
				if(ImageHelper.HASTASK){
					ImageHelper.doTask();
				}
			}
			timer.updateFPS();
			glfwPollEvents();
			glfwSwapBuffers(window);
			systemEventProcessor.processEvents(frame, context);
			EventProcessorProvider.getInstance().processEvents();
			LayoutManager.getInstance().layout(frame);
			AnimatorProvider.getAnimator().runAnimations();
			timer.update();
			if(Settings.discordrpc()) if(++disk_update > 60000){
				DiscordRPC.discordRunCallbacks();
				disk_update = 0;
			}
			// Thread.sleep(50);
		}
		DiscordRPC.discordShutdown();
		renderer.destroy();
		glfwDestroyWindow(window);
		glfwTerminate();
		Settings.save();
		KeyCompound.save();
		SessionHandler.save();
		System.exit(0);
	}

	private void setIcon(){
        try(MemoryStack stack = MemoryStack.stackPush()){
        	ByteBuffer imgbuff; IntBuffer ch = stack.mallocInt(1), w = stack.mallocInt(1), h = stack.mallocInt(1);
            imgbuff = STBImage.stbi_load("./resources/textures/icon.png", w, h, ch, 4);
            if(imgbuff == null) return;
            GLFWImage image = GLFWImage.malloc(); GLFWImage.Buffer imagebf = GLFWImage.malloc(1);
            image.set(w.get(), h.get(), imgbuff); imagebf.put(0, image);
            glfwSetWindowIcon(window, imagebf);
        }
	}

	public void resize(int width, int height){
    	WIDTH = width; HEIGHT = height;
    	//perspective(45.0f, (float)WIDTH / (float)HEIGHT, 0.1f, 4096f / 2);
    	initOpenGL();
    	Editors.resize(width, height);
    	Trees.resize(width, height);
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

	private void loop(){
        //if(!TextureUpdate.HALT){ TextureUpdate.tryAutoPos(TextureUpdate.ALL); }
		for(MenuButton.Extension ext : MenuButton.EXTENSIONS){
			if(ext.button.isVisible()) continue;
			boolean bool = false;
			if(ext.isHovered()) bool = true;
			else{
				for(Component com : ext.getChildComponents()){
					if(com.isHovered()){
						bool = true;
						break;
					}
				}
			}
			if(!bool){
				ext.getStyle().setDisplay(DisplayType.NONE);
			}
		}
	}

	public long getTime(){
	    return System.currentTimeMillis();
	}
	
	private void render(float alpha){
		render(alpha, false);
	}
	
	private void render(float alpha, boolean pixelpass){
		if(!RayCoastAway.PICKING) context.updateGlfwWindow();
        Vector2i size = context.getFramebufferSize();
        float[] color = Settings.getBackGroundColor();
        GL11.glClearColor(color[0], color[1], color[2], color[3]);
        GL11.glViewport(0, 0, size.x, size.y);
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        RGB.glColorReset(); ggr.apply();
		//
        if(Settings.oldrot()) GL11.glRotatef(180, 1, 0, 0);
        GL11.glPushMatrix();
        RGB.glColorReset();
        if(ImageHelper.HASTASK && ImageHelper.getTaskId() == 2){
        	GL11.glRotatef((ImageHelper.getStage() - 20) * 10, 0, 1, 0);
        }
        //
        if(RayCoastAway.PICKING){
            if(pixelpass){
				TextureManager.bindTexture(MODEL.getTempTex(RayCoastAway.lastsel));
				TurboList list = RayCoastAway.lastsel.getTurboList();
				RayCoastAway.lastsel.render(list.rotXb, list.rotYb, list.rotZb);
				RayCoastAway.doTest(false, null, true);
            }
            else MODEL.render();
            GL11.glPopMatrix();
            render(alpha, pixelpass ? false : TextureEditor.pixelMode());
        }
        else{
	        if(Settings.floor()){
				TextureManager.bindTexture("floor");
				GL11.glRotatef(-90, 0, 1, 0);
				GL11.glPushMatrix();
				// GL11.glCullFace(GL11.GL_BACK);
				//GL11.glEnable(GL11.GL_CULL_FACE);
				GL11.glBegin(GL11.GL_QUADS);
				float cs = 16, mid = Settings.oldrot() ? 1f / 16 * 10 : 1 - 0.001f;
				GL11.glTexCoord2f(0, 1);
				GL11.glVertex3f(cs, mid, cs);
				GL11.glTexCoord2f(1, 1);
				GL11.glVertex3f(-cs, mid, cs);
				GL11.glTexCoord2f(1, 0);
				GL11.glVertex3f(-cs, mid, -cs);
				GL11.glTexCoord2f(0, 0);
				GL11.glVertex3f(cs, mid, -cs);
				// GL11.glCullFace(GL11.GL_FRONT_AND_BACK);
				// GL11.glDisable(GL11.GL_CULL_FACE);
				GL11.glEnd();
				GL11.glPopMatrix();
				GL11.glRotatef(90, 0, 1, 0);
	        }
			if(Settings.lighting()){
				GL11.glEnable(GL11.GL_LIGHTING);
				setLightPos(Settings.getLight0Position());
			}
            if(Settings.cube()){
                TextureManager.bindTexture("demo"); compound0.render();
            }
            if(Settings.cullface()) GL11.glEnable(GL11.GL_CULL_FACE);
            MODEL.render(); //sphere0.render(); sphere1.render();
            if(Settings.cullface()) GL11.glDisable(GL11.GL_CULL_FACE);
            if(HelperCollector.LOADED.size() > 0){
            	for(GroupCompound model : HelperCollector.LOADED){
            		RGB.glColorReset();
            		if(model.opacity < 1f){
            			if(model.op_color == null) model.op_color = RGB.WHITE.copy().setAlpha(model.opacity);
            			model.op_color.glColorApply();
            		}
            		model.render();
            		if(model.opacity < 1f) RGB.glColorReset();
            	}
            }
            if(Settings.demo()){
                TextureManager.bindTexture("t1p");
                if(!Settings.oldrot()){
                    GL11.glRotatef(180, 1, 0, 0);
                    GL11.glTranslatef(0, -1.625f, 0);
                }
                RGB.glColorReset(); ModelT1P.INSTANCE.render();
            }
            if(Settings.center_marker()){
                GL11.glDisable(GL11.GL_TEXTURE_2D); 
                centermarker0.render(0.0625f / 4);
                centermarker1.render(0.0625f / 4);
                centermarker2.render(0.0625f / 4);
                GL11.glEnable(GL11.GL_TEXTURE_2D);
            }
			if(Settings.lighting()) GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
	}
	
	public void renderUI(boolean screenshot){
		{
			GL11.glPushMatrix();
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
	        GL11.glOrtho(0, WIDTH, HEIGHT, 0, -100, 100);
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPushMatrix();
	        GL11.glLoadIdentity();
		}
		//
		GL11.glLoadIdentity(); RGB.glColorReset();
		GL11.glDepthFunc(GL11.GL_ALWAYS); GL11.glDisable(GL11.GL_ALPHA_TEST);
		if(screenshot){
			initScreenshotFrame();
			renderer.render(ss_frame, context);
		}
		else{
			renderer.render(frame, context);
		}
		GL11.glDepthFunc(GL11.GL_LESS); GL11.glEnable(GL11.GL_ALPHA_TEST);
		GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
		//
		{
	        GL11.glMatrixMode(GL11.GL_PROJECTION);
	        GL11.glPopMatrix();
	        GL11.glMatrixMode(GL11.GL_MODELVIEW);
	        GL11.glPopMatrix();
	        GL11.glDepthFunc(GL11.GL_LEQUAL);
	    	if(clearcolor == null){ clearcolor = Settings.getBackGroundColor(); }
	    	GL11.glClearColor(clearcolor[0], clearcolor[1], clearcolor[2], clearcolor[3]);
	        GL11.glClearDepth(1.0);
	        GL11.glPopMatrix();
		}
		TextureManager.bindTexture("null");
	}
	
	private void initScreenshotFrame(){
		if(ss_frame == null) ss_frame = new Frame(WIDTH, HEIGHT); ss_frame.setSize(WIDTH, HEIGHT);
		if(ss_title == null){
			ss_title = new Label(10, 10, 500, 20);
			ss_title.getStyle().getBackground().setColor(ColorConstants.transparent());
			ss_title.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
			ss_frame.getContainer().add(ss_title);
		}
		if(ss_credits == null){
			ss_credits = new Label(10, 40, 500, 20);
			ss_credits.getStyle().getBackground().setColor(ColorConstants.transparent());
			ss_credits.getStyle().setFont(FontRegistry.ROBOTO_BOLD);
			ss_frame.getContainer().add(ss_credits);
		}
		ss_title.getTextState().setText((Time.getDay() % 2 == 0 ? "FMT - Fexcraft Modelling Toolbox" : "FMT - Fex's Modelling Toolbox") + " [" + SessionHandler.getLicenseName() + "]");
		switch(FMTB.MODEL.getAuthors().size()){
			case 0: {
				ss_credits.getTextState().setText(FMTB.MODEL.name + " - " + (SessionHandler.isLoggedIn() ? SessionHandler.getUserName() : "Guest User"));
				break;
			}
			case 1: {
				if(FMTB.MODEL.getAuthors().get(0).equals(SessionHandler.getUserName())){
					ss_credits.getTextState().setText(FMTB.MODEL.name + " - by " + SessionHandler.getUserName());
				}
				else{
					ss_credits.getTextState().setText(FMTB.MODEL.name + " - by " + String.format("%s (logged:%s)", FMTB.MODEL.getAuthors().get(0), SessionHandler.getUserName()));
				}
				break;
			}
			default: {
				if(FMTB.MODEL.getAuthors().contains(SessionHandler.getUserName())){
					String authors = "";
					for(int i = 0; i < FMTB.MODEL.getAuthors().size(); i++){
						authors += FMTB.MODEL.getAuthors().get(i);
						if(i < FMTB.MODEL.getAuthors().size() - 1) authors += ", ";
					}
					ss_credits.getTextState().setText(FMTB.MODEL.name + " - by " + authors);
				}
				else{
					ss_credits.getTextState().setText(FMTB.MODEL.name + " - " + String.format("(logged:%s)", SessionHandler.getUserName()));
				}
				break;
			}
		}
	}

	private float[] clearcolor;
	
	private static final ModelRendererTurbo compound0 = new ModelRendererTurbo(null, 0, 0);
	static { compound0.textureHeight = compound0.textureWidth = 16; compound0.addBox(-8, 0, -8, 16, 16, 16); }
	//private static final ModelRendererTurbo sphere0 = new ModelRendererTurbo(null, 256, 256).addSphere(0, 0, 0, 32, 128, 128, 16, 16).setTextured(false);
	//private static final ModelRendererTurbo sphere1 = new ModelRendererTurbo(null, 256, 256).addSphere(0, 0, 0, 32.01f, 128, 128, 16, 16).setLines(true);
	private static final ModelRendererTurbo centermarker0 = new ModelRendererTurbo(null, 0, 0, 0, 0).addBox(-0.5f, -256, -0.5f, 1, 512, 1).setTextured(false).setColor(RGB.GREEN.copy());
	private static final ModelRendererTurbo centermarker1 = new ModelRendererTurbo(null, 0, 0, 0, 0).addBox(-256, -0.5f, -0.5f, 512, 1, 1).setTextured(false).setColor(RGB.RED.copy());
	private static final ModelRendererTurbo centermarker2 = new ModelRendererTurbo(null, 0, 0, 0, 0).addBox(-0.5f, -0.5f, -256, 1, 1, 512).setTextured(false).setColor(RGB.BLUE.copy());

	private void initOpenGL(){
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE, GL11.GL_TRUE);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK, GL11.GL_AMBIENT_AND_DIFFUSE);
        if(!Settings.lighting()) GL11.glDisable(GL11.GL_LIGHTING);
        //
		GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
    	//float[] clearcolor = Settings.background_color.toFloatArray(); //applied in UserInterface.class
    	//GL11.glClearColor(clearcolor[0], clearcolor[1], clearcolor[2], Settings.background_color.alpha);
        GL11.glClearDepth(1.0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        perspective(45.0f, (float)WIDTH / (float)HEIGHT, 0.1f, 4096f / 2);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        //
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f); GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}
	
	public static void perspective(float fovY, float aspect, float zNear, float zFar){
		float fW, fH; fH = (float)(Math.tan( fovY / 360 * Static.PI) * zNear); fW = fH * aspect;
	    GL11.glFrustum( -fW, fW, -fH, fH, zNear, zFar);
	}

	public static void setLightPos(float[] position){
		//log(position[0] + ", " + position[1] + ", " + position[2] + ", " + position[3]);
		java.nio.FloatBuffer fb = org.lwjgl.BufferUtils.createFloatBuffer(4); fb.put(position); fb.flip();
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, fb);
	}
	
	/** use SaveLoad.checkIfShouldSave(true) first! */
	public void close(boolean bool){
		close = bool;
	}

	public void reset(){
		if(context.getFocusedGui() instanceof Field || context.getFocusedGui() instanceof TextField){
			context.getFocusedGui().setFocused(false);
			context.setFocusedGui(null);
		}
		else if(Editors.anyVisible()){ Editors.hideAll(); }
		else return;//open some kind of main menu / status / login screen.
	}

	private void checkForUpdates(){
		new Thread(){
			@Override
			public void run(){
				boolean translate = Settings.getLanguage().equals("none");
				JsonObject obj = HttpUtil.request("http://fexcraft.net/minecraft/fcl/request", "mode=requestdata&modid=fmt");
				if(obj == null || !(obj.has("versions")) && obj.has("latest_version")){
					log("Couldn't fetch latest version.");
					log(obj == null ? ">> no version response received" : obj.toString());
					if(translate) Translator.showSelectDialog(frame);
					return;
				}
				if(obj.has("blocked_versions")){
					JsonArray array = obj.get("blocked_versions").getAsJsonArray();
					for(JsonElement elm : array){
						if(elm.isJsonPrimitive() && elm.getAsString().equals(VERSION)){
							log("Blocked version detected, causing panic.");
							System.exit(2); System.exit(2); System.exit(2); System.exit(2);
						}
					}
				}
				String newver = obj.get("latest_version").getAsString(); boolean bool = VERSION.equals(newver);
				String welcome = Translator.translate("dialog.welcome.title");
				String cversion = Translator.format("dialog.welcome.version", VERSION);
				String new_title = Translator.format("dialog.welcome.title_new", newver, VERSION);
				String new_version = Translator.format("dialog.welcome.version_new", newver, VERSION);
				//
		        Dialog dialog = new Dialog(bool ? welcome : new_title, 300, 100);
		        Label label = new Label(bool ? cversion : new_version, 10, 10, 200, 20);
		        Button okbutton = new Button(Translator.translate("dialog.welcome.confirm"), 10, 50, 50, 20);
		        Button upbutton = new Button(Translator.translate(bool ? "dialog.welcome.exit" : "dialog.welcome.update"), 70, 50, 50, 20);
		        okbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> { if(event.getAction() == CLICK) dialog.close(); });
		        upbutton.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) event -> {
		        	if(event.getAction() == CLICK){
		        		if(bool){
		        			SaveLoad.checkIfShouldSave(true, false);
		        		}
		        		else{
		        			openLink("https://fexcraft.net/app/fmt");
		        		}
		        	}
		        });
		        dialog.getContainer().add(okbutton);
		        dialog.getContainer().add(upbutton);
		        dialog.getContainer().add(label);
		        dialog.show(frame);
		        if(translate) Translator.showSelectDialog(frame);
			}
		}.start();
	}

	public static String getTitle(){
		return title;
	}

	public static void setModel(GroupCompound compound, boolean clearhelpers, boolean cleartex){
		if(cleartex) TextureManager.clearGroups();
		Trees.polygon.clear();
		Trees.helper.clear();
		Trees.fvtm.clear();
		FMTB.MODEL = compound;
		if(clearhelpers) HelperCollector.LOADED.clear();
		for(TurboList list : compound.getGroups()){
			Trees.polygon.addSub(list.button);
			list.button.updateColor();
			Trees.fvtm.addSub(list.abutton);
			list.abutton.updateColor();
		}// fix for loaded-in groups that should display "not-visible" color
		for(GroupCompound com : HelperCollector.LOADED){
			Trees.helper.addSub(com.button);
			com.button.updateColor();
		}
		Trees.polygon.reOrderGroups();
		Trees.fvtm.reOrderGroups();
		Trees.helper.reOrderGroups();
		Editors.general.refreshGroups();
		ModelEditor.creators.refresh();
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

	public static void updateVsync(){
		log(String.format("Updating Vsync State [%s]", (Settings.vsync() ? "+" : "-") + (Settings.vsync() && Settings.vsyncHalf() ? "+" : "-")));
		glfwSwapInterval(Settings.vsync() ? Settings.vsyncHalf() ? 2 : 1 : 0);
	}

}
