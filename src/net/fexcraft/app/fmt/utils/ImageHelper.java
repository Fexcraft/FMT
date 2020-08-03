package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.awt.Desktop;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Iterator;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.FileImageOutputStream;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.lib.common.math.Time;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ImageHelper {
	
	public static boolean HASTASK;
	private static int tasktype = -1;
	private static int stage;
	//
	//partially based on https://stackoverflow.com/questions/16649620
	private static ImageWriter gifwriter;
	private static ImageWriteParam param;
	private static IIOMetadata meta;
	private static ImageTypeSpecifier imgtypespec = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
	private static FileImageOutputStream currgifout;
	private static File currgif;
	private static final int ms = 100;
	static {
		Iterator<ImageWriter> iterator = ImageIO.getImageWritersBySuffix("gif");
		if(iterator.hasNext()){ gifwriter = iterator.next(); }
		if(gifwriter != null) param = gifwriter.getDefaultWriteParam();
	}
	
	public static final void takeScreenshot(boolean open){
		if(tasktype <= -1){ tasktype = open ? 1 : 0; HASTASK = true; return; }
		if(stage < 20){ stage++; return; }
		//
		BufferedImage image = displayToImage();
		tasktype = -1; HASTASK = false; stage = 0;
		try{
			File file = new File("./screenshots/" + Backups.getSimpleDateFormat(true).format(Time.getDate()) + ".png");
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			ImageIO.write(image, "png", file);
			if(open){
        		try{
    				if(System.getProperty("os.name").toLowerCase().contains("windows")){
    					Runtime.getRuntime().exec( "rundll32 url.dll,FileProtocolHandler " + file.getAbsolutePath());
		   			}
        			if(!Desktop.isDesktopSupported()){
        				DialogBox.showOK(null, null, null, "#desktop.api.notsupported");
        			}
        			else Desktop.getDesktop().open(file);
        		}
        		catch(Throwable e){
        			log(e);
        		}
			}
		}
		catch(IOException e){
			log(e);
		}
	}
	
	public static final void createGif(boolean loopgif){
		if(tasktype <= -1){ tasktype = 2; HASTASK = true; return; }
		if(meta == null){
			try{
				meta = gifwriter.getDefaultImageMetadata(imgtypespec, param);
				String name = meta.getNativeMetadataFormatName();
				IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(name);
				IIOMetadataNode graphicsControlExtensionNode = getNode(root, "GraphicControlExtension");
				graphicsControlExtensionNode.setAttribute("disposalMethod", "none");
				graphicsControlExtensionNode.setAttribute("userInputFlag", "FALSE");
				graphicsControlExtensionNode.setAttribute("transparentColorFlag", "FALSE");
				graphicsControlExtensionNode.setAttribute("delayTime", Integer.toString(ms / 10));
				graphicsControlExtensionNode.setAttribute("transparentColorIndex", "0");
				IIOMetadataNode comment = getNode(root, "CommentExtensions");
				comment.setAttribute("CommentExtension", "Created via FMT \u00a9 2018 Fexcraft.net");//TODO add current year automatically
				IIOMetadataNode extension = getNode(root, "ApplicationExtensions");
				IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
				child.setAttribute("applicationID", "NETSCAPE"); child.setAttribute("authenticationCode", "2.0");
				int loop = loopgif ? 0 : 1; child.setUserObject(new byte[] { 0x1, (byte)(loop & 0xFF), (byte)((loop >> 8) & 0xFF) });
				extension.appendChild(child); meta.setFromTree(name, root);
				if(currgif == null){
					currgif = new File("./screenshots/" + Backups.getSimpleDateFormat(true).format(Time.getDate()) + ".gif");
					currgif.getParentFile().mkdirs();
				}
				gifwriter.setOutput(currgifout = new FileImageOutputStream(currgif));
				gifwriter.prepareWriteSequence(null);
			}
			catch(Exception e){
				log(e);
				log("Failed to setup GIF creation, aborting operation.");
				reset();
			}
		}
		if(stage < 20){ stage++; return; } //let's make sure all UI rendering is cleared;
		if(stage >= 20 && stage < 56){ stage++;
			try{ gifwriter.writeToSequence(new IIOImage(displayToImage(), null, meta), param); }
			catch(IOException e){
				log("Failed to write next GIF sequence, aborting operation.");
				reset();
				log(e);
			}
		}
		else{
			try{
				gifwriter.endWriteSequence(); currgifout.close();
			}
			catch(IOException e){
				log(e);
			}
			//
        	DialogBox.show(null, "dialogbox.button.ok", "dialogbox.button.open", null, () -> {
        		try{
        			FMTB.openLink(new File("./screenshots/").getCanonicalPath());
        		}
        		catch(Throwable e){
        			log(e);
        		}
        	}, "image_helper.gif.done");
			reset();
		}
	}
	
	private static IIOMetadataNode getNode(IIOMetadataNode root, String name){
		for(int i = 0; i < root.getLength(); i++) {
			if(root.item(i).getNodeName().equalsIgnoreCase(name)){
				return ((IIOMetadataNode)root.item(i));
			}
		}
		return (IIOMetadataNode)root.appendChild(new IIOMetadataNode(name));
	}

	public static void doTask(){
		if(tasktype == 0 || tasktype == 1){
			takeScreenshot(tasktype == 1);
		}
		else if(tasktype == 2){ createGif(true); }
		else{ reset(); return; }
	}
	
	private static void reset(){
		meta = null; currgif = null; tasktype = -1; HASTASK = false; stage = 0;
	}

	/** see http://wiki.lwjgl.org/wiki/Taking_Screen_Shots.html */
	private static BufferedImage displayToImage(){
		//GL11.glReadBuffer(GL11.GL_FRONT);
		int width = FMTB.WIDTH, height = FMTB.HEIGHT;
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
		return image;
	}

	public static int getTaskId(){ return tasktype; }

	public static int getStage(){ return stage; }

}
