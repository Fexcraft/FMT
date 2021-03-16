package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.MarkerWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.json.JsonUtil;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TSIVMarkerExporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "TS/IV RotableModelObjects JSON Snippet", "*.json" };
	
	public TSIVMarkerExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		JsonArray array = new JsonArray();
		TurboList list = compound.getGroups().get("tsiv_rmo");
		if(list == null){
			return "Error: no group named \n'tsiv_rmo' found!";
		}
		List<PolygonWrapper> coll = list.stream().filter(pre -> pre instanceof MarkerWrapper).collect(Collectors.toList());
		if(coll.isEmpty()){
			return "Error: markers in group 'tsiv_rmo' found!";
		}
		for(PolygonWrapper wrapper : list){
			JsonObject obj = new JsonObject();
			if(wrapper.name == null){
				return "Error: at least one marker is nameless.";
			}
			if(!wrapper.name.contains(",")){
				return "Error: at least one marker has no rotation variable.\ncorrect usage example: \"name,variable\"";
			}
			String[] string = wrapper.name.split(",");
			obj.addProperty("partName", "$" + string[0]);
			JsonArray points = new JsonArray();
			points.add(wrapper.pos.xCoord / 16);
			points.add(-wrapper.pos.yCoord / 16);
			points.add(-wrapper.pos.zCoord / 16);
			obj.add("rotationPoint", points);
			JsonArray axes = new JsonArray();
			axes.add(wrapper.off.xCoord);
			axes.add(wrapper.off.yCoord);
			axes.add(wrapper.off.zCoord);
			obj.add("rotationAxis", axes);
			obj.addProperty("rotationVariable", string[1]);
			array.add(obj);
		}
		JsonObject obj = new JsonObject(); obj.add("rotatableModelObjects", array); JsonUtil.write(file, obj);
		return "Success!";
	}

	@Override
	public String getId(){
		return "tsiv_rotobj_exporter";
	}

	@Override
	public String getName(){
		return "TS/IV Rotable Objects Exporter";
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
	public List<Setting> getSettings(boolean export){
		return nosettings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "marker", "config" };
	}

}
