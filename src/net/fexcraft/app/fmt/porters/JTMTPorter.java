package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class JTMTPorter extends InternalPorter {
	
	private static final String[] extensions = new String[]{ ".jtmt" };
	private boolean visibleonly;
	
	public JTMTPorter(boolean bool){
		this.visibleonly = bool;
	}

	@Override
	public GroupCompound importModel(File file){
		return SaveLoad.getModel(file, JsonUtil.get(file), false);
	}

	@Override @SuppressWarnings("unchecked")
	public String exportModel(GroupCompound compound, File file){
		if(!visibleonly){
			JsonUtil.write(file, SaveLoad.modelToJTMT(compound, true));
			return "Done writing. [ALL]";
		}
		JsonObject obj = SaveLoad.modelToJTMT(compound, true);
		JsonObject groups = obj.get("groups").getAsJsonObject();
		ArrayList<String> torem = new ArrayList<String>();
		for(int i = 0; i < groups.entrySet().size(); i++){
			try{
				Entry<String, JsonElement> entry = (Entry<String, JsonElement>)groups.entrySet().toArray()[i];
				TurboList list = compound.getCompound().get(entry.getKey());
				if(list == null || !list.visible){ torem.add(entry.getKey()); }
			} catch(Exception e){ e.printStackTrace(); }
		}
		for(String str : torem) groups.remove(str);
		obj.add("groups", groups); JsonUtil.write(file, obj);
		return "Done writing. [VO]";
	}

	@Override
	public String getId(){
		return "internal_jtmt" + (visibleonly ? "_vo" : "_all");
	}

	@Override
	public String getName(){
		return "Internal JTMT" + (visibleonly ? " [VISIBLE ONLY]" : "");
	}

	@Override
	public String[] getExtensions(){
		return extensions;
	}

	@Override
	public boolean isImporter(){
		return !visibleonly;
	}

	@Override
	public boolean isExporter(){
		return true;
	}

}
