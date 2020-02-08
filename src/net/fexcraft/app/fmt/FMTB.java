package net.fexcraft.app.fmt;

import static org.lwjgl.glfw.GLFW.*;

import java.awt.Desktop;
import java.awt.DisplayMode;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Timer;

import javax.script.ScriptException;

import org.lwjgl.glfw.GLFWCursorPosCallback;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.glfw.GLFWFramebufferSizeCallback;
import org.lwjgl.glfw.GLFWKeyCallback;
import org.lwjgl.glfw.GLFWMouseButtonCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.Configuration;
import org.lwjgl.system.Platform;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.fexcraft.app.fmt.demo.ModelT1P;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.Dialog;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.app.fmt.ui.editor.ModelGroupEditor;
import net.fexcraft.app.fmt.ui.editor.PreviewEditor;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.general.*;
import net.fexcraft.app.fmt.ui.tree.FVTMTree;
import net.fexcraft.app.fmt.ui.tree.HelperTree;
import net.fexcraft.app.fmt.ui.tree.ModelTree;
import net.fexcraft.app.fmt.utils.*;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.HttpUtil;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2020 fexcraft.net
 * */
public class FMTB {
	
	public static final String deftitle = "[FPS:%s] Fexcraft Modelling Toolbox - %s";
	public static final String deftitle0 = "Fexcraft Modelling Toolbox - %s";
	public static final String version = "1.3.7";
	public static final String CLID = "587016218196574209";
	//
	private static String title = "Unnamed Model";
	private boolean close;
	public static GGR ggr;
	private static FMTB INSTANCE;
	private DisplayMode displaymode;
	public UserInterface UI;
	public static GroupCompound MODEL = new GroupCompound(null);
	public static Timer BACKUP_TIMER, TEX_UPDATE_TIMER;
	private static int disk_update;
	//
	public static final ST_Timer timer = new ST_Timer();
	public float delta, accumulator = 0f, interval = 1f / 30f, alpha;
	//
	public static GLFWErrorCallback errorCallback;
	public static GLFWFramebufferSizeCallback framebufferSizeCallback;
	public static GLFWKeyCallback keyCallback;
	public static GLFWCursorPosCallback cursorCallback;
	public static GLFWMouseButtonCallback mouseCallback;
	public static double cursor_x, cursor_y;
	private long window;
	
	public static void main(String... args) throws Exception {
	    System.setProperty("org.lwjgl.librarypath", new File("./libs/").getAbsolutePath());
		Configuration.SHARED_LIBRARY_EXTRACT_DIRECTORY.set("./libs");
		Configuration.SHARED_LIBRARY_EXTRACT_PATH.set("./libs");
	    //
		FMTB.INSTANCE = new FMTB(); try{ INSTANCE.run(); } catch(Throwable thr){ thr.printStackTrace(); System.exit(1); }
	}

	public static final FMTB get(){ return INSTANCE; }
	
	public FMTB setTitle(String string){ title = string; DiscordUtil.update(false); return this; }
	
	public void run() throws InterruptedException, IOException, NoSuchMethodException, ScriptException {
		TextureManager.init(); this.setIcon(); Settings.load(); StyleSheet.load(); Translator.init(); timer.init();
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
		if(!glfwInit()) throw new IllegalStateException("Unable to initialize GLFW.");
        glfwWindowHint(GLFW_RESIZABLE, GL11.GL_TRUE);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 2);
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 0);
        //glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE);
        window = glfwCreateWindow(1280, 720, "Fex's Modelling Toolbox", 0, 0);
        if(window == 0) {
            throw new RuntimeException("Failed to create window");
        }
        glfwMakeContextCurrent(window);
        GL.createCapabilities();
		initOpenGL();
		glfwShowWindow(window);
		//
        glfwSetKeyCallback(window, (keyCallback = new GLFWKeyCallback() {
            @Override
            public void invoke(long window, int key, int scancode, int action, int mods){
                if(key == GLFW_KEY_SPACE && action == GLFW_RELEASE){
                    //
                } else if(key == GLFW_KEY_F1 && action == GLFW_RELEASE){
                	// 
                } else if(key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE){
                	//
                }
            }
        }));
        glfwSetCursorPosCallback(window, (cursorCallback = new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double xpos, double ypos) {
                cursor_x = xpos;
                cursor_y = /*//TODO height -*/ ypos;
            }
        }));
        glfwSetMouseButtonCallback(window, (mouseCallback = new GLFWMouseButtonCallback(){
            @Override
            public void invoke(long window, int button, int action, int mods){
                if(button == 0) {
                    if(action == GLFW_PRESS){
                        //
                    } else if(action == GLFW_RELEASE){
                        //
                    }
                }
            }
        }));
        //
		ggr = new GGR(this, 0, 4, 4); ggr.rotation.xCoord = 45; FontRenderer.init();
		PorterManager.load(); HelperCollector.reload(); //TODO UI = new UserInterface(this); this.setupUI(UI);
		SessionHandler.checkIfLoggedIn(true, true); checkForUpdates(); //TODO KeyCompound.init(); KeyCompound.load();
		//
		LocalDateTime midnight = LocalDateTime.of(LocalDate.now(ZoneOffset.systemDefault()), LocalTime.MIDNIGHT);
		long mid = midnight.toInstant(ZoneOffset.UTC).toEpochMilli(); long date = Time.getDate(); while((mid += Time.MIN_MS * 5) < date);
		if(BACKUP_TIMER == null){ (BACKUP_TIMER = new Timer()).schedule(new Backups(), new Date(mid), Time.MIN_MS * 5); }
		if(TEX_UPDATE_TIMER == null){ (TEX_UPDATE_TIMER = new Timer()).schedule(new TextureUpdate(), Time.SEC_MS, Time.SEC_MS / 2); }
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
			DiscordRPC.discordRunCallbacks(); DiscordUtil.update(true);
			Runtime.getRuntime().addShutdownHook(new Thread(){ @Override public void run(){ DiscordRPC.discordShutdown(); } });
		}
		//
		while(!close){
            //TODO input(accumulator += (delta = timer.getDelta()));
            while(accumulator >= interval){
            	loop(); timer.updateUPS();
                accumulator -= interval;
            } checkResizement();
			render(alpha = accumulator / interval);
			/*if(!RayCoastAway.PICKING){
				if(ImageHelper.HASTASK){
					UI.render(true); ImageHelper.doTask();
				}
				else UI.render(false);
				//
			}*/ updateFPS();
            glfwPollEvents();
            glfwSwapBuffers(window);
            //TODO timer.update();
			if(Settings.discordrpc()) if(++disk_update > 60000){ DiscordRPC.discordRunCallbacks(); disk_update = 0; }
			//Thread.sleep(50);
		}
		DiscordRPC.discordShutdown();
        glfwDestroyWindow(window);   
        glfwTerminate();
        Settings.save(); StyleSheet.save(); KeyCompound.save(); SessionHandler.save(); System.exit(0);
	}

	private void updateFPS(){
		timer.updateFPS();
	}

	private void setIcon(){
		//TODO Display.setIcon(new java.nio.ByteBuffer[]{ TextureManager.getTexture("icon", false).getBuffer(), TextureManager.getTexture("icon", false).getBuffer() });
	}
	
	private void input(float delta){
		ggr.pollInput(delta); ggr.apply();
	}

	private void loop(){
		if(glfwWindowShouldClose(window)){ SaveLoad.checkIfShouldSave(true, false); }
		//checkResizement();
        if(!TextureUpdate.HALT){ TextureUpdate.tryAutoPos(TextureUpdate.ALL); }
	}
	
	private void checkResizement(){
		glfwSetFramebufferSizeCallback(window, (framebufferSizeCallback = new GLFWFramebufferSizeCallback() {
		    @Override
		    public void invoke(long window, int width, int height) {
				/*displaymode = new DisplayMode(Display.getWidth(), Display.getHeight());
		        GLU.gluPerspective(45.0f, displaymode.getWidth() / displaymode.getHeight(), 0.1f, 4096f / 2);
				GL11.glViewport(0, 0, displaymode.getWidth(), displaymode.getHeight());*///TODO
				initOpenGL(); UI.rescale();
		    }
		}));
	}

	public long getTime(){
	    return System.currentTimeMillis();
	}
	
	private void render(float alpha){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity(); //GL11.glLoadIdentity();		
        GL11.glRotatef(ggr.rotation.xCoord, 1, 0, 0);
        GL11.glRotatef(ggr.rotation.yCoord, 0, 1, 0);
        GL11.glRotatef(ggr.rotation.zCoord, 0, 0, 1);
        GL11.glTranslatef(-ggr.pos.xCoord, -ggr.pos.yCoord, -ggr.pos.zCoord);
        //if(GAMETEST){ GameHandler.render(); return; }
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glPushMatrix();
        RGB.WHITE.glColorApply();
        if(ImageHelper.HASTASK && ImageHelper.getTaskId() == 2){
        	GL11.glRotatef((ImageHelper.getStage() - 20) * 10, 0, 1, 0);
        }
        //
        if(RayCoastAway.PICKING){ 
            MODEL.render(); GL11.glPopMatrix(); render(alpha);
        }
        else{
	        if(Settings.floor()){
	            TextureManager.bindTexture("floor");
	            GL11.glRotatef(-90, 0, 1, 0);
	            GL11.glPushMatrix();
	            //GL11.glCullFace(GL11.GL_BACK); GL11.glEnable(GL11.GL_CULL_FACE);
	            GL11.glBegin(GL11.GL_QUADS); float cs = 16;
	    		GL11.glTexCoord2f(0, 1); GL11.glVertex3f( cs, 1f / 16 * 10,  cs);
	            GL11.glTexCoord2f(1, 1); GL11.glVertex3f(-cs, 1f / 16 * 10,  cs);
	            GL11.glTexCoord2f(1, 0); GL11.glVertex3f(-cs, 1f / 16 * 10, -cs);
	            GL11.glTexCoord2f(0, 0); GL11.glVertex3f( cs, 1f / 16 * 10, -cs);
	            //GL11.glCullFace(GL11.GL_FRONT_AND_BACK); GL11.glDisable(GL11.GL_CULL_FACE); //apparently the front renderer doesn't like this.
	            GL11.glEnd(); GL11.glPopMatrix();
	            GL11.glRotatef( 90, 0, 1, 0);
	        }
			if(Settings.lighting()) GL11.glEnable(GL11.GL_LIGHTING);
            if(Settings.cube()){
                TextureManager.bindTexture("demo"); compound0.render();
            }
            if(Settings.cullface()) GL11.glEnable(GL11.GL_CULL_FACE);
            MODEL.render(); //sphere0.render(); sphere1.render();
            if(Settings.cullface()) GL11.glDisable(GL11.GL_CULL_FACE);
            if(HelperCollector.LOADED.size() > 0){
            	for(GroupCompound model : HelperCollector.LOADED){ RGB.glColorReset(); model.render(); }
            }
            //if(Settings.demo()){
                TextureManager.bindTexture("t1p"); ModelT1P.INSTANCE.render();
            //}
			if(Settings.lighting()) GL11.glDisable(GL11.GL_LIGHTING);
            GL11.glPopMatrix();
        }
	}
	
	private static final ModelRendererTurbo compound0 = new ModelRendererTurbo(null, 0, 0);
	static { compound0.textureHeight = compound0.textureWidth = 16; compound0.addBox(-8, 0, -8, 16, 16, 16); }
	//private static final ModelRendererTurbo sphere0 = new ModelRendererTurbo(null, 256, 256).addSphere(0, 0, 0, 32, 128, 128, 16, 16).setTextured(false);
	//private static final ModelRendererTurbo sphere1 = new ModelRendererTurbo(null, 256, 256).addSphere(0, 0, 0, 32.01f, 128, 128, 16, 16).setLines(true);

	private void initOpenGL(){
        GL11.glEnable(GL11.GL_LIGHTING);
        GL11.glEnable(GL11.GL_LIGHT0);
        GL11.glLightModeli(GL11.GL_LIGHT_MODEL_TWO_SIDE,GL11.GL_TRUE);
        GL11.glEnable(GL11.GL_COLOR_MATERIAL);
        GL11.glColorMaterial(GL11.GL_FRONT_AND_BACK,GL11.GL_AMBIENT_AND_DIFFUSE);
        this.setLightPos(Settings.getLight0Position());
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
        //TODO GLU.gluPerspective(45.0f, (float)displaymode.getWidth() / (float)displaymode.getHeight(), 0.1f, 4096f / 2);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        //
        GL11.glAlphaFunc(GL11.GL_GREATER, 0.5f); GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void setLightPos(float[] position){
		java.nio.FloatBuffer fb = org.lwjgl.BufferUtils.createFloatBuffer(4); fb.put(position); fb.flip();
        GL11.glLightfv(GL11.GL_LIGHT0, GL11.GL_POSITION, fb);
	}
	
	/** use SaveLoad.checkIfShouldSave(true) first! */
	public void close(boolean bool){
		close = bool;
	}
	
	public static void showDialogbox(String title, String button0, String button1, Runnable run0, Runnable run1){
		UserInterface.DIALOGBOX.show(title, button0, button1, run0, run1);
	}
	
	public static void showDialogbox(String title, String button0, String button1, Runnable run0, Runnable run1, int progress, RGB color){
		UserInterface.DIALOGBOX.show(title, button0, button1, run0, run1);
		UserInterface.DIALOGBOX.progress = progress; UserInterface.DIALOGBOX.progresscolor = color;
	}

	public DisplayMode getDisplayMode(){
		return displaymode;
	}

	public void setupUI(UserInterface ui){
		TextureManager.loadTexture("icons/pencil", null);
		TextureManager.loadTexture("icons/arrow_increase", null);
		TextureManager.loadTexture("icons/arrow_decrease", null);
		TextureManager.loadTexture("icons/group_delete", null);
		TextureManager.loadTexture("icons/group_visible", null);
		TextureManager.loadTexture("icons/group_edit", null);
		TextureManager.loadTexture("icons/group_minimize", null);
		TextureManager.loadTexture("icons/group_clone", null);
		TextureManager.loadTexture("icons/editors/minimized", null);
		TextureManager.loadTexture("icons/editors/expanded", null);
		//
		(UserInterface.TOOLBAR = new Toolbar()).repos();
		(UserInterface.BOTTOMBAR = new Bottombar()).setVisible(Settings.bottombar());
		ui.getElements().add(ModelTree.TREE);
		ui.getElements().add(HelperTree.TREE);
		ui.getElements().add(FVTMTree.TREE);
		ui.getElements().add(new GeneralEditor());
		ui.getElements().add(new ModelGroupEditor());
		ui.getElements().add(new TextureEditor());
		ui.getElements().add(new PreviewEditor());
		//
		ui.getElements().add(UserInterface.DIALOGBOX = new DialogBox());
		ui.getElements().add(UserInterface.SETTINGSBOX = new SettingsBox());
		ui.getElements().add(UserInterface.FILECHOOSER = new FileSelector());
		ui.getElements().add(UserInterface.CONTROLS = new ControlsAdjuster());
		//render last
		ui.getElements().add(UserInterface.TOOLBAR);
		ui.getElements().add(UserInterface.BOTTOMBAR);
		ui.getElements().add(UserInterface.TEXMAP = new TextureMap());
		ui.getElements().add(UserInterface.RIGHTMENU = AltMenu.MENU);
		ui.getElements().add(UserInterface.DROPDOWN = DropDown.INST);
		//ui.getElements().add(new Cursor());
		FMTB.MODEL.updateFields();
	}

	public void reset(boolean esc){
		if(Dialog.anyVisible() || TextField.anySelected()){
			UserInterface.DIALOGBOX.reset(); UserInterface.FILECHOOSER.reset();
			UserInterface.CONTROLS.reset(); UserInterface.SETTINGSBOX.reset();
			UserInterface.TEXMAP.reset(); TextField.deselectAll();
		} else if(esc && Editor.anyVisible()){ Editor.hideAll(); } else return;//open some kind of main menu / status / login screen.
	}

	private void checkForUpdates(){
		new Thread(){
			@Override
			public void run(){
				JsonObject obj = HttpUtil.request("http://fexcraft.net/minecraft/fcl/request", "mode=requestdata&modid=fmt");
				if(obj == null || !obj.has("latest_version")){
					Print.console("Couldn't fetch latest version.");
					Print.console(obj == null ? ">> no version response received" : obj.toString());
					return;
				}
				if(obj.has("blocked_versions")){
					JsonArray array = obj.get("blocked_versions").getAsJsonArray();
					for(JsonElement elm : array){
						if(elm.isJsonPrimitive() && elm.getAsString().equals(version)){
							Print.console("Blocked version detected, causing panic.");
							System.exit(2); System.exit(2); System.exit(2); System.exit(2);
						}
					}
				}
				String newver = obj.get("latest_version").getAsString(); boolean bool = version.equals(newver);
				String welcome = Translator.format("dialog.greeting.welcome", "Welcome to FMT!<nl><version:%s>", version);
				String newversion = Translator.format("dialog.greeting.newversion", "New version available!<nl>%s >> %s", newver, version);
				UserInterface.DIALOGBOX.show(bool ? welcome : newversion, Translator.translate("dialog.greeting.confirm", "ok"),
					bool ? Translator.translate("dialog.greeting.exit", "exit") : Translator.translate("dialog.greeting.update", "update"), DialogBox.NOTHING, () -> {
					if(bool){
						SaveLoad.checkIfShouldSave(true, false);
					}
					else{
						try{ Desktop.getDesktop().browse(new URL("http://fexcraft.net/app/fmt").toURI()); }
						catch(IOException | URISyntaxException e){ e.printStackTrace(); }
					}
				});
			}
		}.start();
	}
	
	public static boolean linux(){
		return Platform.get() == Platform.LINUX;//TODO ?
	}

	public static String getTitle(){
		return title;
	}

}
