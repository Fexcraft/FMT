package net.fexcraft.app.fmt.utils;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.imageio.ImageIO;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ImageHelper {
	
	public static boolean HASTASK;
	private static int tasktype = -1;
	private static int stage;
	
	/** see http://wiki.lwjgl.org/wiki/Taking_Screen_Shots.html */
	public static final void takeScreenshot(boolean open){
		if(tasktype <= -1){ Print.console("setup"); tasktype = open ? 1 : 0; HASTASK = true; return; }
		if(stage < 20){ stage++; return;}
		GL11.glReadBuffer(GL11.GL_FRONT);
		int width = Display.getDisplayMode().getWidth(), height = Display.getDisplayMode().getHeight();
		int bpp = 4; // Assuming a 32-bit display with a byte each for red, green, blue, and alpha.
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * bpp);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		//
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++) {
		    for(int y = 0; y < height; y++){
		        int i = (x + (width * y)) * bpp;
		        int r = buffer.get(i) & 0xFF, g = buffer.get(i + 1) & 0xFF, b = buffer.get(i + 2) & 0xFF;
		        image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
		    }
		}
		//
		tasktype = -1; HASTASK = false; stage = 0;
		try{
			File file = new File("./screenshots/" + Backups.getSimpleDateFormat(true).format(Time.getDate()) + ".png");
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			ImageIO.write(image, "png", file);
			if(open){
				try{ Desktop.getDesktop().open(file); } catch(IOException e){ Desktop.getDesktop().open(file.getParentFile()); }
			}
		}
		catch(IOException e){ e.printStackTrace(); }
	}

	public static void doTask(){
		Print.console("pre " + tasktype + " " + HASTASK);
		if(tasktype == 0 || tasktype == 1){
			Print.console("passpass");
			takeScreenshot(tasktype == 1);
		}
		else{
			Print.console("nonpass");
			HASTASK = false;
			return;//TODO gifs
		}
	}

}
