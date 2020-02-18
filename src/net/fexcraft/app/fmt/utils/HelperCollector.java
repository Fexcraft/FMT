package net.fexcraft.app.fmt.utils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;
import java.util.zip.ZipFile;

import javax.imageio.ImageIO;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TexrectWrapperA;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.utils.Print;
import net.fexcraft.lib.common.utils.ZipUtil;

public class HelperCollector {
	
	public static final ArrayList<GroupCompound> LOADED = new ArrayList<>();
	public static int SELECTED = -1;
	
	public static final void reload(){
		LOADED.clear(); File root = new File("./helpers"); if(!root.exists()) root.mkdirs();
	}
	
	public static final GroupCompound load(File file, ExImPorter exim, Map<String, Setting> settings){
		if(file == null || exim == null) return null;
		Print.console("Loading Preview/Helper model: " + file.getName());
		GroupCompound compound = exim.importModel(file, settings);
		if(!compound.name.startsWith("import/")){ compound.name = "import/" + compound.name; }
		if(compound != null) add(compound); return compound;
	}

	/** For loading FMTBs.*/
	public static GroupCompound loadFMTB(File file){
		if(file == null || !file.exists()){
			DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_fmtb.nofile"); return null;
		}
		GroupCompound compound = null;
		try{
			boolean conM = ZipUtil.contains(file, "model.jtmt"), conT = ZipUtil.contains(file, "texture.png");
			ZipFile zip = new ZipFile(file);
			if(conM){
				compound = SaveLoad.getModel(file, JsonUtil.getObjectFromInputStream(zip.getInputStream(zip.getEntry("model.jtmt"))), false);
				if(!compound.name.startsWith("fmtb/")) compound.name = "fmtb/" + compound.name;
			}
			else{
				DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_fmtb.invalid_file");
				zip.close(); return null;
			}
			if(conT){
				TextureManager.loadTextureFromZip(zip.getInputStream(zip.getEntry("texture.png")), "./temp/" + compound.name, false, true);
				compound.setTexture("./temp/" + compound.name);
			} zip.close();
		}
		catch(Exception e){
			e.printStackTrace(); DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_fmtb.errors");
		}
		if(compound != null) add(compound); return compound;
	}

	public static GroupCompound loadFrame(File file){
		if(file == null || !file.exists()){
			DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_frame.nofile"); 
		}
		GroupCompound compound = null;
		try{
			BufferedImage image = ImageIO.read(file);
			TextureManager.loadTextureFromZip(image, "./temp/frame/" + file.getName(), false, false);
			compound = new GroupCompound(file); compound.getGroups().clear();
			compound.file = file; if(!compound.name.startsWith("frame/")) compound.name = "frame/" + file.getName();
			compound.texture = "./temp/frame/" + file.getName();
			compound.textureSizeX = image.getWidth(); compound.textureSizeY = image.getHeight();
			TexrectWrapperA polygon = new TexrectWrapperA(compound);
			polygon.size.xCoord = image.getWidth(); polygon.size.yCoord = image.getHeight(); polygon.size.zCoord = 0.2f;
			polygon.texcor[4][0] = image.getWidth();
			{
				polygon.texcor[4][1] = 0;
				polygon.texcor[4][2] = 0;
				polygon.texcor[4][3] = 0;
				polygon.texcor[4][4] = 0;
				polygon.texcor[4][5] = image.getHeight();
				polygon.texcor[4][6] = image.getWidth();
				polygon.texcor[4][7] = image.getHeight();
			} /*  */ {
				polygon.texcor[5][0] = 0;
				polygon.texcor[5][1] = 0;
				polygon.texcor[5][2] = image.getWidth();
				polygon.texcor[5][3] = 0;
				polygon.texcor[5][4] = image.getWidth();
				polygon.texcor[5][5] = image.getHeight();
				polygon.texcor[5][6] = 0;
				polygon.texcor[5][7] = image.getHeight();
			}
			polygon.off.zCoord = -0.1f; polygon.off.xCoord = -(polygon.size.xCoord / 2); polygon.off.yCoord = -(polygon.size.yCoord / 2);
			compound.add(polygon, "frame", true);
		}
		catch(Exception e){
			e.printStackTrace(); DialogBox.showOK("helper_collector.title", null, null, "helper_collector.load_frame.errors");
		}
		if(compound != null) add(compound); return compound;
	}
	
	private static void add(GroupCompound compound){
		compound.clearSelection(); Trees.helper.addSub(compound.button);
		compound.getGroups().setAsHelperPreview(compound); LOADED.add(compound);
	}
	
	public static GroupCompound getSelected(){
		return SELECTED >= HelperCollector.LOADED.size() || SELECTED < 0 ? null : HelperCollector.LOADED.get(SELECTED);
	}

}
