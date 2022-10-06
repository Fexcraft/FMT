package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.util.TimerTask;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class TextureUpdate extends TimerTask {


	@Override
	public void run(){
		for(TextureGroup group : TextureManager.getGroups()){
			try{
				if(group.texture == null || group.texture.getFile() == null){
					log("TEXGROUP '" + group.name + "' HAS NO FILE OR TEXTURE LINKED YET.");
					continue;
				}
				if(group.texture.getFile().lastModified() > group.texture.lastedit){
					group.texture.lastedit = group.texture.getFile().lastModified();
					group.texture.reload();
					log("Changes detected, reloading texture group '" + group.name + "'.");
				}
			}
			catch(Exception e){
				log(e);
			}
		}
	}

}
