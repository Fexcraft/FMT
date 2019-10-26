package net.fexcraft.app.fmt.porters;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Map;

import javax.imageio.ImageIO;

import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.wrappers.GroupCompound;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class PNGExporter extends InternalPorter {
	
	private static final String[] extensions = new String[]{ ".png" };
	private BufferedImage image;
	private static final ArrayList<Setting> settings = new ArrayList<>();
	static{ settings.add(new Setting(Type.BOOLEAN, "textured", false)); }
	
	public PNGExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){ image = null;
		if(settings.get("textured").getBooleanValue()){
			if(compound.texture == null || TextureManager.getTexture(compound.texture, true) == null){
				return "No texture loaded!";
			}
			image = TextureManager.getTexture(compound.texture, true).getImage();
		}
		else{
			image = new BufferedImage(compound.tx(null), compound.ty(null), BufferedImage.TYPE_INT_ARGB);
			compound.getGroups().forEach(elm -> elm.forEach(poly -> poly.burnToTexture(image, null)));
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
		return "internal_png_exporter";
	}

	@Override
	public String getName(){
		return "Internal PNG Exporter";
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

}
