package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.BBWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.Vec3f;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class AABBExporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "new AABB(...);", "*.txt", "*.java" };
	//private static final ArrayList<Setting> settings = new ArrayList<>();
	
	public AABBExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer();
		buffer.append("# FMT AABB Export // FMT version: " + FMTB.VERSION + "\n");
		buffer.append("# Model: " + (compound.name == null ? "unnamed" : compound.name.toUpperCase()) + "\n\n");
		for(TurboList list : compound.getGroups()){
			List<PolygonWrapper> coll = list.stream().filter(pre -> pre instanceof BBWrapper).collect(Collectors.toList());
			if(!coll.isEmpty()){
				buffer.append("# Group: " + list.id.toUpperCase() + "\n");
				for(PolygonWrapper wrapper : list){
					if(!(wrapper instanceof BBWrapper)) continue;
					BBWrapper box = (BBWrapper)wrapper;
					Vec3f start = box.pos.addVector(8, box.size.yCoord, 8).scale(Static.sixteenth);
					Vec3f end = box.pos.addVector(8 + box.size.xCoord, 0, 8 + box.size.zCoord).scale(Static.sixteenth);
					String name = wrapper.name().replace(" ", "_").replace("-", "_").toUpperCase();
					buffer.append(String.format("AxisAlignedBB %s = new AxisAlignedBB(%sF, %sF, %sF, %sF, %sF, %sF);\n", name,
						start.xCoord, start.yCoord == 0 ? 0 : -start.yCoord, start.zCoord, end.xCoord, end.yCoord == 0 ? 0 : -end.yCoord, end.zCoord));
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

	@Override
	public String getId(){
		return "aabb_exporter";
	}

	@Override
	public String getName(){
		return "AABB Exporter";
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
		return new String[]{ "marker", "boundingbox", "config" };
	}

}
