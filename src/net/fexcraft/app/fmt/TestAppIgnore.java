package net.fexcraft.app.fmt;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.script.ScriptException;
import org.lwjgl.LWJGLException;
import org.lwjgl.LWJGLUtil;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.util.glu.GLU;

import net.fexcraft.app.fmt.porters.PorterManager;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 * 
 * All rights reserved &copy; 2018 fexcraft.net
 * */
@FMTGLProcess.Experimental
public class TestAppIgnore implements FMTGLProcess {
	
	public static final String version = "1.0.0-test";
	//
	private boolean close;
	public static GGR ggr;
	//public int width, height;
	private static TestAppIgnore INSTANCE;
	private DisplayMode displaymode;
	public UserInterface UI;
	private static File lwjgl_natives;
	private static CHUNK[][] chunks;
	private BufferedImage image;
	
	public static void main(String... args) throws Exception {
	    switch(LWJGLUtil.getPlatform()){
	        case LWJGLUtil.PLATFORM_WINDOWS:{ lwjgl_natives = new File("./libs/native/windows"); break; }
	        case LWJGLUtil.PLATFORM_LINUX:{ lwjgl_natives = new File("./libs/native/linux"); break; }
	        case LWJGLUtil.PLATFORM_MACOSX:{ lwjgl_natives = new File("./libs/native/macosx"); break; }
	    }
	    System.setProperty("org.lwjgl.librarypath", lwjgl_natives.getAbsolutePath());
	    //
		TestAppIgnore.INSTANCE = new TestAppIgnore();
		try{ INSTANCE.run(); }
		catch(LWJGLException | InterruptedException | IOException e){
			e.printStackTrace(); System.exit(1);
		}
	}

	public static final TestAppIgnore get(){ return INSTANCE; }
	
	private static final DecimalFormat form = new DecimalFormat("#.#");
	static{ form.setRoundingMode(RoundingMode.DOWN); }
	public static Color color = null;
	
	public void run() throws LWJGLException, InterruptedException, IOException, NoSuchMethodException, ScriptException {
		TextureManager.loadTextures(null);
		Display.setIcon(new java.nio.ByteBuffer[]{
			TextureManager.getTexture("icon", false).getBuffer(),
			TextureManager.getTexture("icon", false).getBuffer()
		});
		setupDisplay(); initOpenGL(); ggr = new GGR(this, 0, 4, 4); ggr.rotation.xCoord = 45;
		PorterManager.load(); Display.setResizable(true); UI = new UserInterface(this);
		//(receiver = new Receiver()).start();
		//
		image = ImageIO.read(new File("./resources/textures/temp/gebco_08_rev_elev_21600x10800.png"));
		chunks = new CHUNK[21600 / CHUNK.size][]; for(int i = 0; i < chunks.length; i++) chunks[i] = new CHUNK[10800 / CHUNK.size];
		for(int x = 0; x < chunks.length; x++){
			for(int z = 0; z < chunks[x].length; z++){
				chunks[x][z] = new CHUNK(image, x, z);
			}
		}
		//
		while(!close){
			loop(); render(); UI.render(false);
			Display.update(); Display.sync(60);
			//Thread.sleep(50);
		}
		Display.destroy(); System.exit(0);
	}

	private void loop(){
		ggr.pollInput(1f); ggr.apply();
		//
		if(Display.isCloseRequested()){ close = true; }
		//
		if(Display.wasResized()){
			displaymode = new DisplayMode(Display.getWidth(), Display.getHeight());
	        GLU.gluPerspective(45.0f, displaymode.getWidth() / displaymode.getHeight(), 0.1f, 2048f);
			GL11.glViewport(0, 0, displaymode.getWidth(), displaymode.getHeight());
			this.initOpenGL();
		}
        if(!Display.isVisible()) {
            try{ Thread.sleep(100); }
            catch(Exception e){ e.printStackTrace(); }
        }
	}
	
	public static class CHUNK {
		
		public Vec3f[][] vecs = new Vec3f[size][];
		public static final int size = 108 / 4;
		private BufferedImage image;
		private float cs = 0.5f;
		private boolean loaded;
		private Integer dl;
		public int x, z;
		//
		private static final RGB gray = new RGB(128, 128, 128);
		private static final RGB light = new RGB(168, 168, 168);
		
		public CHUNK(BufferedImage image, int x, int z){
			this.x = x; this.z = z; this.image = image;
		}

		public void render(){
			if(!loaded){ load(); }
			if(dl == null){
				dl = GL11.glGenLists(1); /*float diff;*/ Vec3f side;
				GL11.glNewList(dl, GL11.GL_COMPILE);
				for(int x = 0; x < size; x++){
					for(int z = 0; z < size; z++){
						Vec3f arr = vecs[x][z];
						side = getVector(x(x) + 1, z(z));
						boolean bool = false;
						if(side != null && arr.yCoord > side.yCoord){
							//diff = side.yCoord - arr.yCoord;
					        GL11.glPushMatrix(); GL11.glBegin(GL11.GL_QUADS);
							GL11.glTexCoord2f(0, 1); GL11.glVertex3f(arr.xCoord + cs, side.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(arr.xCoord + cs, arr.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(arr.xCoord + cs, arr.yCoord, arr.zCoord - cs);
					        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(arr.xCoord + cs, side.yCoord, arr.zCoord - cs);
					        GL11.glEnd(); GL11.glPopMatrix(); bool = true;
						}
						side = getVector(x(x) - 1, z(z));
						if(side != null && arr.yCoord > side.yCoord){
							//diff = side.yCoord - arr.yCoord;
					        GL11.glPushMatrix(); GL11.glBegin(GL11.GL_QUADS);
							GL11.glTexCoord2f(0, 1); GL11.glVertex3f(arr.xCoord - cs, side.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(arr.xCoord - cs, arr.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(arr.xCoord - cs, arr.yCoord, arr.zCoord - cs);
					        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(arr.xCoord - cs, side.yCoord, arr.zCoord - cs);
					        GL11.glEnd(); GL11.glPopMatrix(); bool = true;
						}
						side = getVector(x(x), z(z) + 1);
						if(side != null && arr.yCoord > side.yCoord){
							//diff = side.yCoord - arr.yCoord;
							light.glColorApply();
					        GL11.glPushMatrix(); GL11.glBegin(GL11.GL_QUADS);
							GL11.glTexCoord2f(0, 1); GL11.glVertex3f(arr.xCoord + cs, side.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(arr.xCoord - cs, side.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(arr.xCoord - cs, arr.yCoord, arr.zCoord + cs);
					        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(arr.xCoord + cs, arr.yCoord, arr.zCoord + cs);
					        GL11.glEnd(); GL11.glPopMatrix(); bool = true;
					        RGB.glColorReset();
						}
						side = getVector(x(x), z(z) - 1);
						if(side != null && arr.yCoord > side.yCoord){
							//diff = side.yCoord - arr.yCoord;
							light.glColorApply();
					        GL11.glPushMatrix(); GL11.glBegin(GL11.GL_QUADS);
							GL11.glTexCoord2f(0, 1); GL11.glVertex3f(arr.xCoord + cs, side.yCoord, arr.zCoord - cs);
					        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(arr.xCoord - cs, side.yCoord, arr.zCoord - cs);
					        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(arr.xCoord - cs, arr.yCoord, arr.zCoord - cs);
					        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(arr.xCoord + cs, arr.yCoord, arr.zCoord - cs);
					        GL11.glEnd(); GL11.glPopMatrix(); bool = true;
					        RGB.glColorReset();
						}
						if(arr.yCoord < 1f && !bool) continue;
						gray.glColorApply();
				        GL11.glPushMatrix(); GL11.glBegin(GL11.GL_QUADS);
						GL11.glTexCoord2f(0, 1); GL11.glVertex3f(arr.xCoord + cs, arr.yCoord + 0, arr.zCoord + cs);
				        GL11.glTexCoord2f(1, 1); GL11.glVertex3f(arr.xCoord - cs, arr.yCoord + 0, arr.zCoord + cs);
				        GL11.glTexCoord2f(1, 0); GL11.glVertex3f(arr.xCoord - cs, arr.yCoord + 0, arr.zCoord - cs);
				        GL11.glTexCoord2f(0, 0); GL11.glVertex3f(arr.xCoord + cs, arr.yCoord + 0, arr.zCoord - cs);
				        GL11.glEnd(); GL11.glPopMatrix();
				        RGB.glColorReset();
					}
				}
				GL11.glEndList();
			}
			GL11.glCallList(dl);
		}

		private int z(int zz){ return (z * size) + zz; }

		private int x(int xx){ return (x * size) + xx; }

		private void load(){
			for(int xx = 0; xx < size; xx++){
				vecs[xx] = new Vec3f[size];
				for(int y = 0; y < size; y++){
					color = new Color(image.getRGB(xx + (x * size), y + (z * size)));
					vecs[xx][y] = new Vec3f((x * size) + xx, (color.getRed() + 128) * 0.1f, (z * size) + y);
				}
			} loaded = true;
			Print.console(String.format("CHUNK %s, %s DONE!", x, z));
		}
		
	}

	private static Vec3f getVector(int xx, int zz){
		int x = xx / CHUNK.size, z = zz / CHUNK.size;
		if(x > chunks.length || z > chunks[0].length) return null;
		if(chunks[x][z] == null || !chunks[x][z].loaded) return null;
		xx %= CHUNK.size; zz %= CHUNK.size;
		if(xx < 0 || zz < 0) return null;
		return chunks[x][z].vecs[xx][zz];
	}
	
	private void render(){
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT | GL11.GL_DEPTH_BUFFER_BIT);
        GL11.glLoadIdentity(); //GL11.glLoadIdentity();		
        GL11.glRotatef(ggr.rotation.xCoord, 1, 0, 0);
        GL11.glRotatef(ggr.rotation.yCoord, 0, 1, 0);
        GL11.glRotatef(ggr.rotation.zCoord, 0, 0, 1);
        GL11.glTranslatef(-ggr.pos.xCoord, -ggr.pos.yCoord, -ggr.pos.zCoord);
        //GL11.glRotatef(180, 1, 0, 0);
        GL11.glPushMatrix();
        //GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        RGB.WHITE.glColorApply();
        //
        TextureManager.bindTexture("tai");
        getNearby(); for(CHUNK chunk : list){ chunk.render(); }
        //
        //GL11.glDisable(GL11.GL_BLEND);
        GL11.glPopMatrix();
	}
	
	private final ArrayList<CHUNK> list = new ArrayList<>();
	
	private void getNearby(){
		list.clear(); int xx = (int)ggr.pos.xCoord / CHUNK.size; int zz = (int)ggr.pos.zCoord / CHUNK.size;
		for(int x = -8; x < 9; x++){
			for(int z = -8; z < 9; z++){
				if(xx + x < 0 || xx + x >= chunks.length) continue;
				if(zz + z < 0 || zz + z >= chunks[0].length) continue;
				list.add(chunks[xx + x][zz + z]);
			}
		}
	}

	//private static final ModelRendererTurbo compound0 = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.5f, 0, -.5f, 1, 1, 1).setTextured(false);
	//private static final ModelRendererTurbo compound1 = new ModelRendererTurbo(null, 0, 0, 16, 16).addBox(-.5f, 0, -.5f, 1, 1, 1).setLines(true);

	private void initOpenGL(){
		GL11.glEnable(GL11.GL_TEXTURE_2D);
    	GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glClearColor(0.5f, 0.5f, 0.5f, 0.2f);
        GL11.glClearDepth(1.0);
        GL11.glEnable(GL11.GL_DEPTH_TEST);
        GL11.glDepthFunc(GL11.GL_LEQUAL);
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GLU.gluPerspective(45.0f, (float)displaymode.getWidth() / (float)displaymode.getHeight(), 0.1f, 2048f);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
        //
        GL11.glEnable(GL11.GL_BLEND); GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
	}

	private void setupDisplay() throws LWJGLException {
		Display.setFullscreen(Settings.fullscreen());
		Display.setResizable(false);
		Display.setDisplayMode(displaymode = new DisplayMode(1000, 600));
		Display.setTitle("MBEH Test"); Display.setVSyncEnabled(true);
		Display.create();
	}
	
	public static void showDialogbox(String title, String desc, String button0, String button1, Runnable run0, Runnable run1){
		UserInterface.DIALOGBOX.show(new String[]{ title == null ? "" : title, desc == null ? "" : desc, button0, button1 }, run0, run1);
	}

	@Override
	public DisplayMode getDisplayMode(){
		return displaymode;
	}

	@Override
	public void setupUI(UserInterface ui){
		TextureManager.loadTexture("ui/background"); TextureManager.loadTexture("ui/button_bg");
		//ui.getElements().put("dialogbox", UserInterface.DIALOGBOX = new DialogBox());
		//ui.getElements().put("filechooser", UserInterface.FILECHOOSER = new FileChooser());
		ui.getElements().add(new TextField(null, "text", 0, 0, 500){
			@Override
			public void renderSelf(int rw, int rh){
				this.y = rh - displaymode.getHeight() + 30;
				this.setText(form.format(ggr.rotation.xCoord) + ", " + form.format(ggr.rotation.yCoord) + ", " + form.format(ggr.rotation.zCoord)
					+ (", [" + (int)ggr.pos.xCoord / CHUNK.size + "x, " + (int)ggr.pos.zCoord / CHUNK.size + "]"), false);
				super.renderSelf(rw, rh);
			}
		});
		ui.getElements().add(new Toolbar());
	}

	@Override
	public UserInterface getUserInterface(){
		return UI;
	}

	@Override
	public void reset(){
		TextField.deselectAll();
	}
	
	public class Toolbar extends Element {
		
		public Toolbar(){
			super(null, "toolbar"); this.height = 30;
			this.elements.put("xpos", new TextField(this, "xpos", 150, 2, 2){
				@Override protected void updateNumberField(){ ggr.pos.xCoord = this.getFloatValue(); }
				@Override public void renderSelf(int rw, int rh){ if(!isSelected()) this.applyChange(ggr.pos.xCoord); super.renderSelf(rw, rh); }
			}.setAsNumberfield(0, 21600, false));
			this.elements.put("ypos", new TextField(this, "ypos", 150, 154, 2){
				@Override protected void updateNumberField(){ ggr.pos.yCoord = this.getFloatValue(); }
				@Override public void renderSelf(int rw, int rh){ if(!isSelected()) this.applyChange(ggr.pos.yCoord); super.renderSelf(rw, rh); }
			}.setAsNumberfield(0, 300, false));
			this.elements.put("zpos", new TextField(this, "zpos", 150, 306, 2){
				@Override protected void updateNumberField(){ ggr.pos.zCoord = this.getFloatValue(); }
				@Override public void renderSelf(int rw, int rh){ if(!isSelected()) this.applyChange(ggr.pos.zCoord); super.renderSelf(rw, rh); }
			}.setAsNumberfield(0, 10800, false));
		}

		@Override
		public void renderSelf(int rw, int rh){
			this.width = rw;
			this.renderQuad(0, 0, width, height, "ui/background");
		}

		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
			return false;
		}

	}

}
