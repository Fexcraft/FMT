package net.fexcraft.app.fmt.utils;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Calendar;
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

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Time;

public class ImageHandler {

	private static ImageWriter writer;
	private static ImageWriteParam param;
	private static IIOMetadata meta;
	private static ImageTypeSpecifier imgtypespec = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_ARGB);
	private static FileImageOutputStream currgifout;
	private static File currgif;
	static{
		Iterator<ImageWriter> iterator = ImageIO.getImageWritersBySuffix("gif");
		if(iterator.hasNext()) writer = iterator.next();
		if(writer != null) param = writer.getDefaultWriteParam();
	}
	private static Task CURRENT = Task.NONE;
	private static int WAIT;
	private static int pass;
	public static Float ROT;

	public static void takeScreenshot(){
		takeScreenshot(true);
	}

	public static void takeScreenshot(boolean fromKey){
		if(CURRENT == Task.NONE){
			CURRENT = fromKey || !Settings.OPEN_FOLDER_AFTER_IMG.value ? Task.SCREENSHOT : Task.SCREENSHOT_OPEN;
			WAIT = 2;
			return;
		}
		BufferedImage image = displayToImage();
		try{
			String name = BackupHandler.getSimpleDateFormat(true).format(Time.getDate());
			File file = new File("./screenshots/" + name + ".png");
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
			ImageIO.write(image, "png", file);
			if(CURRENT == Task.SCREENSHOT_OPEN){
				FMT.openLink(file.getAbsolutePath());
			}
			Logging.bar("Screenshot taken [" + name + "]", true);
		}
		catch(Exception e){
			log(e);
		}
		reset();
	}

	public static void createGif(){
		if(CURRENT == Task.NONE){
			CURRENT = Task.GIF;
			WAIT = 2;
			return;
		}
		if(meta == null){
			try{
				meta = writer.getDefaultImageMetadata(imgtypespec, param);
				String name = meta.getNativeMetadataFormatName();
				IIOMetadataNode root = (IIOMetadataNode)meta.getAsTree(name);
				IIOMetadataNode gcex = getNode(root, "GraphicControlExtension");
				gcex.setAttribute("disposalMethod", "none");
				gcex.setAttribute("userInputFlag", "FALSE");
				gcex.setAttribute("transparentColorFlag", "FALSE");
				gcex.setAttribute("delayTime", Integer.toString(Settings.GIF_DELAY_TIME.value / 10));
				gcex.setAttribute("transparentColorIndex", "0");
				IIOMetadataNode comment = getNode(root, "CommentExtensions");
				comment.setAttribute("CommentExtension", "Created via FMT " + FMT.VERSION + " \u00a9 " + Calendar.getInstance().get(Calendar.YEAR) + " Fexcraft.net");
				IIOMetadataNode extension = getNode(root, "ApplicationExtensions");
				IIOMetadataNode child = new IIOMetadataNode("ApplicationExtension");
				child.setAttribute("applicationID", "NETSCAPE");
				child.setAttribute("authenticationCode", "2.0");
				int loop = Settings.GIF_LOOP.value ? 0 : 1;
				child.setUserObject(new byte[] { 0x1, (byte)(loop & 0xFF), (byte)((loop >> 8) & 0xFF) });
				extension.appendChild(child);
				meta.setFromTree(name, root);
				if(currgif == null){
					currgif = new File("./screenshots/" + BackupHandler.getSimpleDateFormat(true).format(Time.getDate()) + ".gif");
					if(!currgif.getParentFile().exists()) currgif.getParentFile().mkdirs();
				}
				writer.setOutput(currgifout = new FileImageOutputStream(currgif));
				writer.prepareWriteSequence(null);
			}
			catch(Exception e){
				log(e);
				log("Failed to setup GIF creation, aborting operation.");
				reset();
			}
		}
		if(pass < Settings.GIF_ROT_PASS.value){
			try{
				writer.writeToSequence(new IIOImage(displayToImage(), null, meta), param);
			}
			catch(IOException e){
				log("Failed to write next GIF sequence, aborting operation.");
				reset();
				log(e);
			}
			pass++;
			ROT = pass > 0 ? 360f / Settings.GIF_ROT_PASS.value * pass * Static.rad1 : null;
		}
		else{
			try{
				writer.endWriteSequence();
				currgifout.close();
				if(Settings.OPEN_FOLDER_AFTER_IMG.value){
					FMT.openLink(currgif.getAbsolutePath());
				}
				Logging.bar("GIF created [" + currgif.getName() + "]", true);
			}
			catch(IOException e){
				log(e);
			}
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

	private static BufferedImage displayToImage(){
		int width = FMT.WIDTH, height = FMT.HEIGHT;
		ByteBuffer buffer = BufferUtils.createByteBuffer(width * height * 4);
		GL11.glReadPixels(0, 0, width, height, GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buffer);
		//
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		for(int x = 0; x < width; x++){
			for(int y = 0; y < height; y++){
				int i = (x + (width * y)) * 4;
				int r = buffer.get(i) & 0xFF, g = buffer.get(i + 1) & 0xFF, b = buffer.get(i + 2) & 0xFF;
				image.setRGB(x, height - (y + 1), (0xFF << 24) | (r << 16) | (g << 8) | b);
			}
		}
		return image;
	}

	private static void reset(){
		meta = null;
		currgif = null;
		CURRENT = Task.NONE;
		pass = 0;
	}

	public static enum Task {

		NONE, SCREENSHOT, SCREENSHOT_OPEN, GIF

	}
	
	public static void processTask(){
		if(CURRENT == Task.NONE) return;
		else if(WAIT > 0){
			WAIT--;
			return;
		}
		else if(CURRENT == Task.GIF) createGif();
		else takeScreenshot();
	}
	
	public static boolean shouldHide(){
		return CURRENT != Task.NONE && Settings.HIDE_UI_FOR_IMAGE.value;
	}

	public static void updateText(){
		if(CURRENT == Task.NONE) return;
		String title = Settings.NO_RANDOM_TITLE.value ? "FMT - Fex's Modelling Toolbox " + FMT.VERSION + " - " + SessionHandler.getLicenseName() : FMT.getTitle(SessionHandler.getLicenseName());
		FMT.img_line0.getTextState().setText(title);
		if(FMT.MODEL.getAuthors().size() == 0){
			FMT.img_line1.getTextState().setText(FMT.MODEL.name + " - " + (SessionHandler.isLoggedIn() ? SessionHandler.getUserName() : "Guest User"));
		}
		else if(FMT.MODEL.getAuthors().size() == 1){
			String author = FMT.MODEL.getAuthors().keySet().toArray(new String[]{})[0];
			if(author.equals(SessionHandler.getUserName())){
				FMT.img_line1.getTextState().setText(FMT.MODEL.name + " - by " + SessionHandler.getUserName());
			}
			else{
				FMT.img_line1.getTextState().setText(FMT.MODEL.name + " - by " + String.format("%s (logged:%s)", author, SessionHandler.getUserName()));
			}
		}
		else{
			if(FMT.MODEL.getAuthors().keySet().contains(SessionHandler.getUserName())){
				String authors = "";
				int i = 0;
				for(String author : FMT.MODEL.getAuthors().keySet()){
					authors += author;
					if(i < FMT.MODEL.getAuthors().size() - 1) authors += ", ";
					i++;
				}
				FMT.img_line1.getTextState().setText(FMT.MODEL.name + " - by " + authors);
			}
			else{
				FMT.img_line1.getTextState().setText(FMT.MODEL.name + " - " + String.format("(logged:%s)", SessionHandler.getUserName()));
			}
		}
	}

}
