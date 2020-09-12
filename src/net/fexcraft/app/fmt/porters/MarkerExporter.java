package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

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
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.StringArraySetting;
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
public class MarkerExporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "Marker List File", "*.txt", "*.json" };
	private static final ArrayList<Setting> settings = new ArrayList<>();
	
	public MarkerExporter(){
		settings.add(new StringArraySetting("type", "normal", "fvtm_seats_json", "tsiv_coords"));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer();
		String type = settings.get("type").getStringValue();
		if(type.equals("fvtm_seats_json")){
			JsonObject obj = new JsonObject();
			obj.addProperty("__comment", "FVTM Seat List // FMT version: " + FMTB.VERSION);
			for(TurboList list : compound.getGroups()){
				List<PolygonWrapper> coll = list.stream().filter(pre -> pre instanceof MarkerWrapper).collect(Collectors.toList());
				if(!coll.isEmpty()){
					JsonArray array = new JsonArray();
					for(int i = 0; i < coll.size(); i++){
						MarkerWrapper marker = (MarkerWrapper)coll.get(i);
						String name = marker.name == null ? "seat" + i : marker.name();
						JsonObject seat = new JsonObject();
						seat.addProperty("x", marker.pos.xCoord);
						seat.addProperty("y", -marker.pos.yCoord);
						seat.addProperty("z", -marker.pos.zCoord);
						seat.addProperty("name", name);
						array.add(seat);
					}
					obj.add(list.exportID(), array);
				}
			}
			JsonUtil.write(file, obj);
			return "Success!";
		}
		boolean tsiv = type.equals("tsiv_coords");
		buffer.append("# FMT Marker List // FMT version: " + FMTB.VERSION + "\n");
		if(tsiv) buffer.append("# TS/IV Corrected Export Mode\n");
		buffer.append("# Model: " + (compound.name == null ? "unnamed" : compound.name.toLowerCase()) + "\n\n");
		for(TurboList list : compound.getGroups()){
			List<PolygonWrapper> coll = list.stream().filter(pre -> pre instanceof MarkerWrapper).collect(Collectors.toList());
			if(!coll.isEmpty()){
				buffer.append("# Group: " + list.id + "\n");
				for(PolygonWrapper wrapper : list){
					if(tsiv){
						buffer.append(String.format("\"pos\": [ %s, %s, %s],\n", nmz(wrapper.pos.zCoord / 16), nmz(-wrapper.pos.yCoord / 16), nmz(wrapper.pos.xCoord / 16)));
					}
					else buffer.append(wrapper.name() + ": " + wrapper.pos.xCoord + ", " + wrapper.pos.yCoord + ", " + wrapper.pos.zCoord + ";\n");
				}
			}
		}
		//
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buffer); writer.flush(); writer.close();
		}
		catch(IOException e){
			log(e);
			return "Error:" + e.getMessage();
		}
		return "Success!";
	}

	private String nmz(float f){
		if(f > 0 || f < 0) return f + "";
		return "0";
	}

	@Override
	public String getId(){
		return "marker_exporter";
	}

	@Override
	public String getName(){
		return "MarkerList Exporter";
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

	@Override
	public String[] getCategories(){
		return new String[]{ "marker", "config" };
	}

}
