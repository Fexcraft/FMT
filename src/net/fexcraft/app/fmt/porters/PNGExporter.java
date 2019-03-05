package net.fexcraft.app.fmt.porters;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
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
	private boolean textured;
	
	public PNGExporter(boolean textured){ this.textured = textured; }

	@Override
	public GroupCompound importModel(File file){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file){ image = null;
		if(textured){
			if(compound.texture == null || TextureManager.getTexture(compound.texture, true) == null){
				return "No texture loaded!";
			}
			image = TextureManager.getTexture(compound.texture, true).getImage();
		}
		else{
			image = new BufferedImage(compound.textureX, compound.textureY, BufferedImage.TYPE_INT_ARGB);
			compound.getCompound().values().forEach(elm -> elm.forEach(poly -> poly.burnToTexture(image, null)));
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
		return "internal_png_exporter" + (textured ? "_textured" : "_untextured");
	}

	@Override
	public String getName(){
		return "Internal PNG Exporter " + (textured ? "[TEXTURED]" : "[TEMPLATE]");
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

}
