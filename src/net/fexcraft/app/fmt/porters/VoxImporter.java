package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joml.Vector4f;

import alemax.model.Chunk;
import alemax.model.Model;
import alemax.model.Voxel;
import alemax.util.FileHandler;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.app.fmt.wrappers.BBWrapper;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.VoxelWrapper;
import net.fexcraft.app.fmt.wrappers.face.BoxFace;
import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.FaceUVType;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.tmt.ColorIndexedVoxelBuilder;
import net.fexcraft.lib.tmt.VoxelBuilder;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class VoxImporter extends ExImPorter {
	
	private static String[] extensions = new String[]{ "VOX Importer", "*.vox" };
	protected ArrayList<Setting> settings = new ArrayList<>();
	
	public VoxImporter(){
		settings.add(new Setting("color_indexed", false));
		settings.add(new Setting("devoxelize", false));
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
        boolean colors = settings.get("color_indexed").getBooleanValue();
        boolean devox = settings.get("devoxelize").getBooleanValue();
        try{
        	byte[] voxdata = FileHandler.readVoxFile(file.getPath());
        	Model model = new Model(voxdata);
            if(devox){
            	TextureGroup group = TextureManager.addNewGroup("voxel", false);
            	group.texture.resize(16, 16);
            	group.texture.clear(null);
            	for(int i = 0; i < 256; i++){
        			Vector4f col = model.colors[i];
            		group.texture.set(i / 16, i % 16, new RGB((int)col.x, (int)col.y, (int)col.z).toByteArray());
            	}
            	group.texture.getImage().rewind();
            	group.texture.save();
            	compound.textureSizeX = 16;
            	compound.textureSizeY = 16;
            	compound.setTexture(group);
            	log("Voxel Texture Created.");
            }
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
            	VoxelWrapper wrapper = null;
            	if(colors){
            		HashMap<Integer, RGB> colours = new HashMap<>();
                	int[][][] ints = new int[cx][cy][cz];
                    for(Voxel voxel : chunk.voxels){
                    	if(voxel.i == 0) continue;
                    	ints[voxel.x - minx][voxel.y - miny][voxel.z - minz] = voxel.i;
                    	if(!colours.containsKey(voxel.i)){
                			Vector4f col = model.colors[voxel.i];
                			colours.put(voxel.i, new RGB((int)col.x, (int)col.y, (int)col.z));
                    	}
                    }
                    wrapper = new VoxelWrapper(compound, cx, cy, cz, ints, colours);
                	log("Color Voxel " + (chunks + 1) + " Created.");
            	}
            	else{
                	boolean[][][] bools = new boolean[cx][cy][cz];
                    for(Voxel voxel : chunk.voxels){
                    	if(voxel.i == 0) continue;
                    	bools[voxel.x - minx][voxel.y - miny][voxel.z - minz] = true;
                    }
                    wrapper = new VoxelWrapper(compound, cx, cy, cz, bools);
                	log("Voxel " + (chunks + 1) + " Created.");
            	}
                wrapper.pos.xCoord = minx;
                wrapper.pos.yCoord = miny;
                wrapper.pos.zCoord = minz;
                wrapper.name = "voxel_" + (chunks++);
                if(devox){
                	ArrayList<int[]> array = null;
                	if(wrapper.icontent != null){
                		array = new ColorIndexedVoxelBuilder(null, wrapper.segx, wrapper.segy, wrapper.segz)
                			.setColors(wrapper.colors).setVoxels(wrapper.icontent).rectangulate();
                	}
                	else{
                		array = new VoxelBuilder(null, wrapper.segx, wrapper.segy, wrapper.segz).setVoxels(wrapper.content).rectangulate();
                	}
                	for(int[] arr : array){
                		BoxWrapper box = new BoxWrapper(compound);
                		box.pos.xCoord = minx + arr[0];
                		box.pos.yCoord = miny + arr[1];
                		box.pos.zCoord = minz + arr[2];
                		box.size.xCoord = arr[3] - arr[0] + 1;
                		box.size.yCoord = arr[4] - arr[1] + 1;
                		box.size.zCoord = arr[5] - arr[2] + 1;
                		compound.add(box, "voxel_" + chunks, false);
                		if(colors){
                			for(Face face : BoxFace.values()){
                				if(!box.isFaceActive(face)) continue;
                				int xx = arr[6] / 16, yy = arr[6] % 16;
                				box.getUVCoords(face).set(FaceUVType.ABSOLUTE_ENDS).value(new float[]{ xx, yy, xx + 1, yy + 1 });
                			}
                		}
                	}
                	log("Devoxelized Voxel " + chunks + ".");
                }
                else{
                	compound.add(wrapper, "voxels", false);
                	log("Voxel Shape " + chunks + " Created.");
                }
                BBWrapper bounding = new BBWrapper(compound);
                bounding.pos.xCoord = minx;
                bounding.pos.yCoord = miny;
                bounding.pos.zCoord = minz;
                bounding.size = new Vec3f(cx, cy, cz);
                bounding.visible = false;
                bounding.name = "voxel_" + chunks;
                compound.add(bounding, "voxel_bbs", false);
            	log("Bounding Box " + chunks + " Created.");
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