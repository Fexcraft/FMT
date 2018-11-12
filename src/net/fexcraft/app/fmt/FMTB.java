package net.fexcraft.app.fmt;

import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Timer;

import javax.script.ScriptException;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.fexcraft.app.fmt.demo.ModelT1P;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.HelperTree;
import net.fexcraft.app.fmt.ui.ModelTree;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.editor.CylinderEditor;
import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.app.fmt.ui.editor.GroupEditor;
import net.fexcraft.app.fmt.ui.editor.PreviewEditor;
import net.fexcraft.app.fmt.ui.editor.ShapeboxEditor;
import net.fexcraft.app.fmt.ui.generic.Crossbar;
import net.fexcraft.app.fmt.ui.generic.DialogBox;
import net.fexcraft.app.fmt.ui.generic.FileChooser;
import net.fexcraft.app.fmt.ui.generic.Toolbar;
import net.fexcraft.app.fmt.utils.Backups;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.HelperCollector;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureUpdate;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2018 fexcraft.net
 * */
public class FMTB implements FMTGLProcess {
	
	public static final String deftitle = "Fexcraft Modelling Toolbox - %s";
	public static final String version = "1.0.0-test";
	//
	private static String title;
	private boolean close;
	public static GGR ggr;
	//public int width, height;
	private static FMTB INSTANCE;
	private DisplayMode displaymode;
	public UserInterface UI;
	private static File lwjgl_natives;
	public static GroupCompound MODEL = new GroupCompound();
	public static Timer BACKUP_TIMER, TEX_UPDATE_TIMER;
	
	public static void main(String... args) throws Exception {
	    switch(LWJGLUtil.getPlatform()){
	        case LWJGLUtil.PLATFORM_WINDOWS:{ lwjgl_natives = new File("./libs/native/windows"); break; }
	        case LWJGLUtil.PLATFORM_LINUX:{ lwjgl_natives = new File("./libs/native/linux"); break; }
	        case LWJGLUtil.PLATFORM_MACOSX:{ lwjgl_natives = new File("./libs/native/macosx"); break; }
	    }
	    System.setProperty("org.lwjgl.librarypath", lwjgl_natives.getAbsolutePath());
	    //
		FMTB.INSTANCE = new FMTB();
		INSTANCE.setDefaults(false, "Unnamed Model");
		try{ INSTANCE.run(); }
		catch(LWJGLException | InterruptedException | IOException e){
			e.printStackTrace(); System.exit(1);
		}
	}
	
	public static final Process startProcess(Class<?> clazz) throws Exception {
	    String sp = System.getProperty("file.separator"), path = System.getProperty("java.home") + sp + "bin" + sp + "java";
	    return new ProcessBuilder(path, "-cp", System.getProperty("java.class.path"), clazz.getName()).start();
	}
	
	public static final int getResult(String[] text, int buttons) throws Exception {
	    String sp = System.getProperty("file.separator"), path = System.getProperty("java.home") + sp + "bin" + sp + "java";
	    return new ProcessBuilder(path, "-cp", System.getProperty("java.class.path"), Object.class.getName()).start().waitFor();
	    //TODO make new "dialogbox class" using this?
	}
	
    public static class Receiver extends Thread {
    	
        private static String input;
        
        @Override
        public void run(){
            Print.console("Starting local receiver on port 6992!");
            try{
                ServerSocket socket = new ServerSocket(6992);
                while(!FMTB.INSTANCE.close){
                    try{
                        Socket client = socket.accept();
                        BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
                        //
                        StringBuffer response = new StringBuffer();
                        while((input = in.readLine()) != null){ response.append(input); } in.close(); client.close();
                        //TODO process response;
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
                socket.close();
            }
            catch(IOException e){
                e.printStackTrace();
            }
            Print.console("Stopping local receiver on port 6992!");
        }
        
    }

	public static final FMTB get(){ return INSTANCE; }

	private void setDefaults(boolean full, String string){
		Settings.setFullScreen(full); setTitle(string);
	}
	
	public void setTitle(String string){
		title = String.format(deftitle, string); Display.setTitle(title);
	}
	
	public void run() throws LWJGLException, InterruptedException, IOException, NoSuchMethodException, ScriptException {
		TextureManager.loadTextures(null);
		Display.setIcon(new java.nio.ByteBuffer[]{
			TextureManager.getTexture("icon", false).getBuffer(),
			TextureManager.getTexture("icon", false).getBuffer()
		});
		setupDisplay(); initOpenGL(); ggr = new GGR(0, 4, 4); ggr.rotation.xCoord = 45;
		PorterManager.load(); HelperCollector.reload(); Display.setResizable(true); UI = new UserInterface(this);
		//(receiver = new Receiver()).start();
		//
		LocalDateTime midnight = LocalDateTime.of(LocalDate.now(ZoneOffset.systemDefault()), LocalTime.MIDNIGHT);
		long mid = midnight.toInstant(ZoneOffset.UTC).toEpochMilli(); long date = Time.getDate(); while((mid += Time.MIN_MS * 5) < date);
		if(BACKUP_TIMER == null){ (BACKUP_TIMER = new Timer()).schedule(new Backups(), new Date(mid), Time.MIN_MS * 5); }
		if(TEX_UPDATE_TIMER == null){ (TEX_UPDATE_TIMER = new Timer()).schedule(new TextureUpdate(), Time.SEC_MS, Time.SEC_MS / 2); }
		//
		while(!close){
			loop(); render(); UI.render();
			Display.update(); Display.sync(60);
			//Thread.sleep(50);
		}
		Display.destroy(); System.exit(0);
	}

	private void loop(){
		ggr.pollInput(0.05f); ggr.apply();
		//
		if(Display.isCloseRequested()){ SaveLoad.checkIfShouldSave(true); }
		//
		if(Display.wasResized()){
			displaymode = new DisplayMode(Display.getWidth(), Display.getHeight());
	        GLU.gluPerspective(45.0f, displaymode.getWidth() / displaymode.getHeight(), 0.1f, 100.0f);
			GL11.glViewport(0, 0, displaymode.getWidth(), displaymode.getHeight());
			this.initOpenGL();
		}
        if(!Display.isVisible()) {
            try{ Thread.sleep(100); }
            catch(Exception e){ e.printStackTrace(); }
        }
	}
	
	private void render(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity(); //GL11.glLoadIdentity();		
        GL11.glRotatef(ggr.rotation.xCoord, 1, 0, 0);
        GL11.glRotatef(ggr.rotation.yCoord, 0, 1, 0);
        GL11.glRotatef(ggr.rotation.zCoord, 0, 0, 1);
        GL11.glTranslatef(-ggr.pos.xCoord, -ggr.pos.yCoord, -ggr.pos.zCoord);
        GL11.glRotatef(180, 1, 0, 0);
        GL11.glPushMatrix();
        //GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RGB.WHITE.glColorApply();
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
        //
        if(Settings.cube()){
            TextureManager.bindTexture("demo"); compound0.render();
        }
        MODEL.render();
        if(HelperCollector.LOADED.size() > 0){
        	for(GroupCompound model : HelperCollector.LOADED) model.render();
        }
        if(Settings.demo()){
            TextureManager.bindTexture("t1p"); ModelT1P.INSTANCE.render();
        }
        //
        //GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
	}
	
	private static final ModelRendererTurbo compound0 = new ModelRendererTurbo(null, 0, 0);
	static { compound0.textureHeight = compound0.textureWidth = 16; compound0.addBox(-8, 0, -8, 16, 16, 16); }

	private void initOpenGL(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
        GL11.glClearDepth(1.0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45.0f, (float)displaymode.getWidth() / (float)displaymode.getHeight(), 0.1f, 100.0f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        //
        GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void setupDisplay() throws LWJGLException {
		Display.setFullscreen(Settings.fullscreen());
		Display.setResizable(false);
		Display.setDisplayMode(displaymode = new DisplayMode(1000, 600));
		Display.setTitle(title); Display.setVSyncEnabled(true);
		Display.create();
	}
	
	/*public static final void print(Object... objs){
		System.out.print("[ ");
		for(Object obj : objs){
			System.out.print(obj == null ? "null " : obj.toString() + " ");
		}
		System.out.print("]\n");
	}
	
	public static final <T> T print(T obj){
		System.out.print(String.format("[ %s ]\n", obj)); return obj;
	}*/

	public void close(){
		SaveLoad.checkIfShouldSave(true);
	}
	
	public void close(boolean bool){
		close = bool;
	}
	
	public static void showDialogbox(String title, String desc, String button0, String button1, Runnable run0, Runnable run1){
		EventQueue.invokeLater(new Runnable(){
			@Override
			public void run(){
				UserInterface.DIALOGBOX.show(new String[]{ title == null ? "" : title, desc == null ? "" : desc, button0, button1 }, run0, run1);
			}
		});
	}

	@Override
	public DisplayMode getDisplayMode(){
		return displaymode;
	}

	@Override
	public void setupUI(UserInterface ui){
		TextureManager.loadTexture("ui/background");
		TextureManager.loadTexture("ui/button_bg");
		TextureManager.loadTexture("icons/group_delete");
		TextureManager.loadTexture("icons/group_visible");
		TextureManager.loadTexture("icons/group_edit");
		TextureManager.loadTexture("icons/group_minimize");
		ui.getElements().put("crossbar", new Crossbar());
		ui.getElements().put("toolbar", new Toolbar());
		ui.getElements().put("general_editor", new GeneralEditor());
		ui.getElements().put("shapebox_editor", new ShapeboxEditor());
		ui.getElements().put("modeltree", new ModelTree());
		ui.getElements().put("cylinder_editor", new CylinderEditor());
		ui.getElements().put("dialogbox", UserInterface.DIALOGBOX = new DialogBox());
		ui.getElements().put("filechooser", UserInterface.FILECHOOSER = new FileChooser());
		ui.getElements().put("group_editor", new GroupEditor());
		ui.getElements().put("helpertree", new HelperTree());
		ui.getElements().put("preview_editor", new PreviewEditor());
		FMTB.MODEL.updateFields();
	}

}
