package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class JTMTPorter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "JTMT Model", "*.jtmt" };
	private static final ArrayList<Setting> settings = new ArrayList<>();
	static{
		settings.add(new Setting(Type.BOOLEAN, "visible_only", false));
		settings.add(new Setting(Type.BOOLEAN, "selected_only", false));
	}
	
	public JTMTPorter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return SaveLoad.getModel(file, JsonUtil.get(file), false);
	}

	@Override public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		boolean selected = settings.get("selected_only").getBooleanValue();
		boolean visible = settings.get("visible_only").getBooleanValue();
		if(!visible && !selected){
			JsonUtil.write(file, SaveLoad.modelToJTMT(compound, true));
			return "Done writing. [ALL]";
		}
		JsonObject obj = SaveLoad.modelToJTMT(compound, true);
		JsonObject groups = obj.get("groups").getAsJsonObject();
		ArrayList<String> torem = new ArrayList<String>();
		for(int i = 0; i < groups.entrySet().size(); i++){
			try{
				Entry<String, JsonElement> entry = (Entry<String, JsonElement>)groups.entrySet().toArray()[i];
				TurboList list = compound.getGroups().get(entry.getKey());
				if(list == null || (visible && !list.visible) || (selected && !list.selected)) torem.add(entry.getKey());
			}
			catch(Exception e){
				log(e);
			}
		}
		for(String str : torem) groups.remove(str);
		obj.add("groups", groups); JsonUtil.write(file, obj);
		return "Done writing. [" + (visible ? "VO" : selected ? "SO" : "??") + "]";
	}

	@Override
	public String getId(){
		return "jtmt";
	}

	@Override
	public String getName(){
		return "Standard JTMT";
	}

	@Override
	public String[] getExtensions(){
		return extensions;
	}

	@Override
	public boolean isImporter(){
		return true;
	}

	@Override
	public boolean isExporter(){
		return true;
	}

	@Override
	public List<Setting> getSettings(boolean export){
		return export ? settings : nosettings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}

}
