package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import alemax.model.Chunk;
import alemax.model.Model;
import alemax.model.Voxel;
import alemax.util.FileHandler;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.BBWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.VoxelWrapper;
import net.fexcraft.lib.common.math.Vec3f;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class VoxImporter extends ExImPorter {
	
	private static String[] extensions = new String[]{ "VOX Importer", "*.vox" };
	protected ArrayList<Setting> settings = new ArrayList<>();
	
	public VoxImporter(){
		//
	}

    public String[] getExtensions(){
        return extensions;
    }
    
    public String getId(){
    	return "vox_importer";
    }
    
    public String getName(){
    	return "AM Based VOX Importer";
    }
    
	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
        GroupCompound compound = new GroupCompound(file);
        try{
        	byte[] voxdata = FileHandler.readVoxFile(file.getPath());
        	Model model = new Model(voxdata);
        	int chunks = 0;
            for(Chunk chunk : model.chunks){
            	int minx = 0, miny = 0, minz = 0;
            	int maxx = 0, maxy = 0, maxz = 0;
                for(Voxel voxel : chunk.voxels){
                	if(voxel.i == 0) continue;
                	if(voxel.x > maxx) maxx = voxel.x;
                	else if(voxel.x < minx) minx = voxel.x;
                	if(voxel.y > maxy) maxy = voxel.y;
                	else if(voxel.y < miny) miny = voxel.y;
                	if(voxel.z > maxz) maxz = voxel.z;
                	else if(voxel.z < minz) minz = voxel.z;
                }
            	int cx = maxx + -minx + 1;
            	int cy = maxy + -miny + 1;
            	int cz = maxz + -minz + 1;
            	boolean[][][] bools = new boolean[cx][cy][cz];
                for(Voxel voxel : chunk.voxels){
                	if(voxel.i == 0) continue;
                	bools[voxel.x - minx][voxel.y - miny][voxel.z - minz] = true;
                }
                VoxelWrapper wrapper = new VoxelWrapper(compound, cx, cy, cz, bools);
                wrapper.pos.xCoord = minx;
                wrapper.pos.yCoord = miny;
                wrapper.pos.zCoord = minz;
                wrapper.name = "voxel_" + (chunks++);
                compound.add(wrapper, "voxels", false);
                BBWrapper bounding = new BBWrapper(compound);
                bounding.pos.xCoord = minx;
                bounding.pos.yCoord = miny;
                bounding.pos.zCoord = minz;
                bounding.size = new Vec3f(cx, cy, cz);
                bounding.visible = false;
                bounding.name = "voxel_" + chunks;
                compound.add(bounding, "voxel_bbs", false);
            }
        }
        catch(Exception e){
        	log(e);
        }
        compound.clearSelection();
        return compound;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		return "This isn't an exporter.";
	}
	
	@Override
	public boolean isImporter(){
		return true;
	}
	
	@Override
	public boolean isExporter(){
		return false;
	}

	@Override
	public List<Setting> getSettings(boolean export){
		return settings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}
    
}