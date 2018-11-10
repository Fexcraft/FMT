package net.fexcraft.app.fmt.utils;

import java.util.TimerTask;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.lib.common.utils.Print;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureUpdate extends TimerTask {
	
	private Texture texture; private long lastedit;

	@Override
	public void run(){
        try{
            if(FMTB.MODEL.texture == null) return;
            texture = TextureManager.getTexture(FMTB.MODEL.texture, true);
            if(texture == null || texture.getFile() == null || !texture.getFile().exists()) return;
            if(texture.getFile().lastModified() != lastedit){
            	lastedit = texture.getFile().lastModified(); texture.reload();
            	Print.console("Changes detected, reloading texture.");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
	}

}
