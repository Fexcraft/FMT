package net.fexcraft.app.fmt.porters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.MarkerWrapper;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class MarkerExporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "Marker List File", "*.txt", "*.json" };
	//private static final ArrayList<Setting> settings = new ArrayList<>();
	
	public MarkerExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer();
		buffer.append("# FMT Marker List // FMT version: " + FMTB.VERSION + "\n");
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
		return "Success!";
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
		return nosettings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "marker", "config" };
	}

}
