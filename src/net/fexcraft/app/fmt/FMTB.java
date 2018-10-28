package net.fexcraft.app.fmt;

import java.io.File;
import java.io.IOException;

import javax.script.ScriptException;
import javax.swing.JOptionPane;

import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.fexcraft.app.fmt.demo.ModelT1P;
import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2018 fexcraft.net
 * */
public class FMTB {
	
	public static final String deftitle = "Fexcraft Modelling Toolbox - %s";
	public static final String version = "1.0.0-test";
	//
	private static String title;
	private boolean close;
	public static GGR ggr;
	//public int width, height;
	private static FMTB INSTANCE;
	public DisplayMode displaymode;
	public UserInterface UI;
	private static File lwjgl_natives;
	public static GroupCompound MODEL = new GroupCompound();
	
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
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Seems the app crashed!\n" + e.getMessage() + "\nCheck console for more info.", "FMT Runtime Error", JOptionPane.INFORMATION_MESSAGE);
			System.exit(1);
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
		setupDisplay(); initOpenGL(); ggr = new GGR(0, 4, 4); ggr.rotation.xCoord = 45;
		TextureManager.loadTextures();
		Display.setResizable(true);
		UI = new UserInterface(this);
		PorterManager.load();
		//
		while(!close){
			loop(); render(); UI.render();
			Display.update(); Display.sync(60);
			/*Thread.sleep(33);*/
		}
		Display.destroy(); System.exit(0);
	}

	private void loop(){
		ggr.acceptInput(0.05F); ggr.apply();
		//
		if(Display.isCloseRequested()) close = true;
		if(Keyboard.isKeyDown(Keyboard.KEY_F11)){
			try{ Display.setFullscreen(Settings.toogleFullscreen()); }
			catch(Exception ex){ ex.printStackTrace(); }
		}
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
        TextureManager.bindTexture("blank"); MODEL.render();
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
	
	public static final void print(Object... objs){
		System.out.print("[ ");
		for(Object obj : objs){
			System.out.print(obj == null ? "null " : obj.toString() + " ");
		}
		System.out.print("]\n");
	}
	
	public static final <T> T print(T obj){
		System.out.print(String.format("[ %s ]\n", obj)); return obj;
	}

	public void close(){
		SaveLoad.checkIfShouldSave(); close = true;
	}

}
