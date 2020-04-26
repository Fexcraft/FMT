package net.fexcraft.app.fmt.porters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.StringArraySetting;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.TextureGroup;
import net.fexcraft.app.fmt.wrappers.GroupCompound;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class PNGExporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "Portable Network Graphics", "*.png" };
	private BufferedImage image;
	private static final ArrayList<Setting> settings = new ArrayList<>();
	static{
		settings.add(new Setting(Type.BOOLEAN, "textured", false));
		settings.add(new StringArraySetting("texgroup", new String[]{}));
	}
	
	public PNGExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		image = null;
		boolean textured = settings.get("textured").getBooleanValue();
		TextureGroup group = TextureManager.getGroup(settings.get("texgroup").as(StringArraySetting.class).getSelected());
		if(group == null){
			return "Texture Group not found!";
		}
		if(textured){
			image = group.texture.getImage();
		}
		else{
			image = new BufferedImage(group.texture.getWidth(), group.texture.getHeight(), BufferedImage.TYPE_INT_ARGB);
			compound.getGroups().forEach(elm -> {
				if(compound.texgroup == group || elm.texgroup == group){
					elm.forEach(poly -> poly.burnToTexture(image, null));
				}
			});
		}
		try{
			ImageIO.write(image, "PNG", file);
			return "Success!";
		}
		catch(java.io.IOException e){
			e.printStackTrace(); return "Error, see Console.";
		}
	}

	@Override
	public String getId(){
		return "png_exporter";
	}

	@Override
	public String getName(){
		return "Standard PNG Exporter";
	}

	@Override
	public String[] getExtensions(){
		return extensions;
	}

	@Override
	public boolean isImporter(){
		return false;
	}

	@Override
	public boolean isExporter(){
		return true;
	}

	@Override
	public ArrayList<Setting> getSettings(boolean export){
		return settings;
	}

	public static ArrayList<Setting> getSettings(){
		return settings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "texture" };
	}

}
