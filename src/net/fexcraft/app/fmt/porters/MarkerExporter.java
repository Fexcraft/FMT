package net.fexcraft.app.fmt.porters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.wrappers.CollisionGridWrapper;
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
public class MarkerExporter extends InternalPorter {
	
	private static final String[] extensions = new String[]{ ".txt", ".collbox" };
	private static final ArrayList<Setting> settings = new ArrayList<>();
	static{ settings.add(new Setting(Type.BOOLEAN, "collbox", false)); }
	
	public MarkerExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		if(!settings.get("collbox").getBooleanValue()){
			StringBuffer buffer = new StringBuffer();
			buffer.append("# FMT Marker List // FMT version: " + FMTB.version + "\n");
			buffer.append("# Model: " + (compound.name == null ? "unnamed" : compound.name.toLowerCase()) + "\n\n");
			for(TurboList list : compound.getGroups()){
				List<PolygonWrapper> coll = list.stream().filter(pre -> pre instanceof MarkerWrapper).collect(Collectors.toList());
				if(!coll.isEmpty()){
					buffer.append("# Group: " + list.id + "\n");
					for(PolygonWrapper wrapper : list){
						if(!(wrapper instanceof MarkerWrapper)) continue;
						buffer.append(wrapper.name() + ": " + wrapper.pos.xCoord + ", " + wrapper.pos.yCoord + ", " + wrapper.pos.zCoord + ";\n");
					}
				}
			}
			//
			try {
				BufferedWriter writer = new BufferedWriter(new FileWriter(file));
				writer.append(buffer); writer.flush(); writer.close();
			}
			catch(IOException e){
				e.printStackTrace();
				return "Error:" + e.getMessage();
			}
		}
		else{
			JsonArray array = new JsonArray();
			compound.getGroups().forEach(list -> list.forEach(wrapper -> {
				if(wrapper.getType().isCollisionGrid()){
					JsonArray from = new JsonArray(); CollisionGridWrapper coll = (CollisionGridWrapper)wrapper;
					from.add(wrapper.pos.xCoord); from.add(wrapper.pos.yCoord); from.add(wrapper.pos.zCoord);
					JsonArray size = new JsonArray(); JsonObject obj = new JsonObject();
					size.add(coll.size.xCoord); size.add(coll.size.xCoord); size.add(coll.size.zCoord);
					obj.addProperty("unit", wrapper.rot.xCoord); obj.add("from", from); obj.add("size", size); array.add(obj);
				}
			}));
			JsonObject obj = new JsonObject();
			obj.add("CollisionGrid", array);
			JsonUtil.write(file, obj);
		}
		return "Success!";
	}

	@Override
	public String getId(){
		return "marker_exporter";
	}

	@Override
	public String getName(){
		return "MarkerList/Collbox Exporter";
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
		return settings;
	}

}
