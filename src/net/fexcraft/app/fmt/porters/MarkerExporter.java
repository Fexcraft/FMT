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
		settings.add(new StringArraySetting("type",
			Appender.NORMAL.name().toLowerCase(),
			Appender.FVTM_SEAT_JSON.name().toLowerCase(),
			Appender.FVTM_PART_SLOTS.name().toLowerCase(),
			Appender.TSIV_COORDS.name().toLowerCase()
		));
		settings.add(new Setting("selected-only", false));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer();
		Appender type = Appender.valueOf(settings.get("type").getStringValue().toUpperCase());
		boolean selected = settings.get("selected-only").getBooleanValue();
		if(type == Appender.FVTM_SEAT_JSON){
			JsonObject obj = new JsonObject();
			obj.addProperty("__comment", "FVTM Seat List // FMT version: " + FMTB.VERSION);
			for(TurboList list : compound.getGroups()){
				if(selected && !list.selected) continue;
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
		buffer.append("# FMT Marker List // FMT version: " + FMTB.VERSION + "\n");
		buffer.append(type.title());
		buffer.append("# Model: " + (compound.name == null ? "unnamed" : compound.name.toLowerCase()) + "\n\n");
		buffer.append(type.start());
		for(TurboList list : compound.getGroups()){
			if(selected && !list.selected) continue;
			List<PolygonWrapper> coll = list.stream().filter(pre -> pre instanceof MarkerWrapper).collect(Collectors.toList());
			if(!coll.isEmpty()){
				buffer.append(type.group_prefix(list));
				for(PolygonWrapper wrapper : list){
					buffer.append(type.polygon(list, wrapper));
				}
				buffer.append(type.group_suffix(list));
			}
		}
		buffer.append(type.end());
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

	protected static String nmz(float f){
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
	
	public static enum Appender {
		
		NORMAL {

			@Override
			public String title(){
				return "";
			}

			@Override
			public String start(){
				return "";
			}

			@Override
			protected String group_prefix(TurboList list){
				return "# Group: " + list.id + "\n";
			}

			@Override
			protected String polygon(TurboList list, PolygonWrapper wrapper){
				return wrapper.name() + ": " + wrapper.pos.xCoord + ", " + wrapper.pos.yCoord + ", " + wrapper.pos.zCoord + ";\n";
			}

			@Override
			protected String group_suffix(TurboList list){
				return "";
			}

			@Override
			protected String end(){
				return "";
			}
			
		},
		FVTM_SEAT_JSON {

			@Override
			public String title(){
				return "";
			}

			@Override
			public String start(){
				return "";
			}

			@Override
			protected String group_prefix(TurboList list){
				return "";
			}

			@Override
			protected String polygon(TurboList list, PolygonWrapper wrapper){
				return "";
			}

			@Override
			protected String group_suffix(TurboList list){
				return "";
			}

			@Override
			protected String end(){
				return "";
			}
			
		},
		FVTM_PART_SLOTS {

			@Override
			public String title(){
				return "";
			}

			@Override
			public String start(){
				return "\"slots\":[\n";
			}

			@Override
			protected String group_prefix(TurboList list){
				return "";
			}

			@Override
			protected String polygon(TurboList list, PolygonWrapper wrapper){
				return String.format("\t[ %s, %s, %s, \"%s\", \"%s\"],\n", nmz(wrapper.pos.zCoord / 16), nmz(-wrapper.pos.yCoord / 16), nmz(wrapper.pos.xCoord / 16), list.id, wrapper.name());
			}

			@Override
			protected String group_suffix(TurboList list){
				return "";
			}

			@Override
			protected String end(){
				return "}";
			}
			
		},
		TSIV_COORDS {

			@Override
			public String title(){
				return "# TS/IV Corrected Export Mode\n";
			}

			@Override
			public String start(){
				return "";
			}

			@Override
			protected String group_prefix(TurboList list){
				return "# Group: " + list.id + "\n";
			}

			@Override
			protected String polygon(TurboList list, PolygonWrapper wrapper){
				return String.format("\"pos\": [ %s, %s, %s],\n", nmz(wrapper.pos.zCoord / 16), nmz(-wrapper.pos.yCoord / 16), nmz(wrapper.pos.xCoord / 16));
			}

			@Override
			protected String group_suffix(TurboList list){
				return "";
			}

			@Override
			protected String end(){
				return "";
			}
			
		},
		;
		
		public abstract String title();

		protected abstract String start();

		protected abstract String polygon(TurboList list, PolygonWrapper wrapper);

		protected abstract String group_suffix(TurboList list);

		protected abstract String group_prefix(TurboList list);
		
		protected abstract String end();
		
	}

}
