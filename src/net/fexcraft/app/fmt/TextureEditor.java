package net.fexcraft.app.fmt;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.script.ScriptException;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.generic.Toolbar;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2018 fexcraft.net
 * */
@Deprecated
public class TextureEditor implements FMTGLProcess {

	private boolean close;
	private static String title;
	private static TextureEditor INSTANCE;
	private DisplayMode displaymode;
	public UserInterface UI;
	private static File lwjgl_natives;
	public static Receiver receiver;
	
	public static void main(String... args) throws Exception {
	    switch(LWJGLUtil.getPlatform()){
	        case LWJGLUtil.PLATFORM_WINDOWS:{ lwjgl_natives = new File("./libs/native/windows"); break; }
	        case LWJGLUtil.PLATFORM_LINUX:{ lwjgl_natives = new File("./libs/native/linux"); break; }
	        case LWJGLUtil.PLATFORM_MACOSX:{ lwjgl_natives = new File("./libs/native/macosx"); break; }
	    }
	    System.setProperty("org.lwjgl.librarypath", lwjgl_natives.getAbsolutePath());
	    //
		TextureEditor.INSTANCE = new TextureEditor();
		try{ INSTANCE.run(); }
		catch(Exception e){ e.printStackTrace(); System.exit(1); }
	}
	
	public static final TextureEditor get(){ return INSTANCE; }
	
	public void run() throws LWJGLException, InterruptedException, IOException, NoSuchMethodException, ScriptException {
		TextureManager.loadTextures(null); TextureManager.loadTextures("texed");
		Display.setIcon(new java.nio.ByteBuffer[]{
			TextureManager.getTexture("texed/pencil", false).getBuffer(),
			TextureManager.getTexture("texed/pencil", false).getBuffer()
		});
		//
		Display.setFullscreen(Settings.fullscreen());
		Display.setResizable(false);
		Display.setDisplayMode(displaymode = new DisplayMode(600, 480));
		Display.setTitle(title); Display.setVSyncEnabled(true);
		Display.create();
		//
		Display.setTitle("FMT - Texture Editor");
		Display.setResizable(true);
		UI = new UserInterface(this);
		(receiver = new Receiver()).start();
		//
		while(!close){
			loop(); GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
	        UI.render(); Display.update(); Display.sync(60); Thread.sleep(50);
		}
		Display.destroy(); System.exit(0);
	}

	private void loop(){
		if(Display.isCloseRequested()){ close = true; }
		//
		if(Display.wasResized()){
			displaymode = new DisplayMode(Display.getWidth(), Display.getHeight());
	        GLU.gluPerspective(45.0f, displaymode.getWidth() / displaymode.getHeight(), 0.1f, 100.0f);
			GL11.glViewport(0, 0, displaymode.getWidth(), displaymode.getHeight());
		}
        if(!Display.isVisible()) {
            try{ Thread.sleep(100); }
            catch(Exception e){ e.printStackTrace(); }
        }
	}

	@Override
	public DisplayMode getDisplayMode(){
		return displaymode;
	}

	@Override
	public void setupUI(UserInterface ui){
		TextureManager.loadTexture("ui/background");
		TextureManager.loadTexture("ui/button_bg");
		ui.getElements().put("toolbar", new Toolbar());
	}
	
    public static class Receiver extends Thread {
    	
        private static String input;
        
        @Override
        public void run(){
            Print.console("Starting local texture editor receiver on port 6993!");
            try{
                ServerSocket socket = new ServerSocket(6993);
                while(!TextureEditor.INSTANCE.close){
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
            Print.console("Stopping local texture editor receiver on port 6993!");
        }
        
    }

}
