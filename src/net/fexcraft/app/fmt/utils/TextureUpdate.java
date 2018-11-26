package net.fexcraft.app.fmt.utils;

import java.awt.image.BufferedImage;
import java.util.TimerTask;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.lib.common.math.Time;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureUpdate extends TimerTask {
	
	private Texture texture; private static long lastedit;

	@Override
	public void run(){
        try{
            if(FMTB.MODEL.texture == null) return;
            texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
            if(texture == null || texture.getFile() == null || !texture.getFile().exists()) return;
            if(texture.getFile().lastModified() > lastedit){
            	updateLastEdit(texture.getFile().lastModified()); texture.reload();
            	Print.console("Changes detected, reloading texture.");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}
	
	public static final void updateLastEdit(long date){
		lastedit = date; return;
	}

	public static void updateSizes(){
		Texture texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
		if(texture == null || texture.getImage() == null) return;
		BufferedImage image = texture.getImage();
		if(image.getWidth() != FMTB.MODEL.textureX || image.getHeight() != FMTB.MODEL.textureY){
			if(image.getWidth() > FMTB.MODEL.textureX && image.getWidth() % FMTB.MODEL.textureX == 0
				&& image.getHeight() > FMTB.MODEL.textureY && image.getHeight() % FMTB.MODEL.textureY == 0) return;
			texture.resize(FMTB.MODEL.textureX, FMTB.MODEL.textureY, 0x00ffffff);
			TextureManager.saveTexture(FMTB.MODEL.texture); FMTB.MODEL.recompile();
			updateLastEdit(Time.getDate());
		}
		else return;
	}

}
