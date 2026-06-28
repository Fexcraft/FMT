package net.fexcraft.app.fmt.texture;

import net.fexcraft.app.fmt.FMT;

import static net.fexcraft.app.fmt.utils.Logging.bar;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.util.TimerTask;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureUpdate extends TimerTask {


	@Override
	public void run(){
		for(TextureGroup group : FMT.MODEL.getTexGroups()){
			try{
				if(group.texture == null){
					bar("TEXGROUP '" + group.typeid() + "' HAS NO TEXTURE LINKED YET.", true);
					continue;
				}
				if(group.texture.getFile() == null){
					bar("TEXGROUP '" + group.typeid() + "' HAS NO FILE LINKED YET.", true);
					continue;
				}
				if(group.texture.getFile().lastModified() > group.texture.lastedit){
					group.texture.lastedit = group.texture.getFile().lastModified();
					group.texture.reload();
					log("Changes detected, reloading texture group '" + group.typeid() + "'.");
				}
			}
			catch(Exception e){
				log(e);
			}
		}
	}

}
