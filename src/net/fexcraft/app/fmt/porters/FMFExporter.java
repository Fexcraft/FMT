package net.fexcraft.app.fmt.porters;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.app.fmt.wrappers.face.BoxFace;
import net.fexcraft.app.fmt.wrappers.face.CylFace;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.common.math.Vec3f;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FMFExporter extends ExImPorter {
	
	private static final String[] extensions = new String[]{ "Fex's Model Format", "*.fmf", "*.fex" };
	private static final ArrayList<Setting> settings = new ArrayList<>();
	//from fvtm
	private static final int PP = 1, PR = 2, PF = 3, PT = 4, PL = 6, PM = 7, PDF = 8, PDU = 9, PCU = 10;
	private static final int PBS = 16, PBC = 17;
	private static final int PCRL = 16, PCD = 17, PCSG = 18, PCSL = 19, PCTO = 20, PCTR = 21, PCRT = 22;
	
	
	public FMFExporter(){
		settings.add(new Setting("selected-only", false));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		FileOutputStream stream = null;
		try{
			stream = new FileOutputStream(file);
			stream.write(new byte[]{ 6, 13, 6, 1 });
			if(compound.name != null){
				write(stream, 1, compound.name);
			}
			for(String str : compound.getCreators().keySet()){
				write(stream, 4, str);
			}
			stream.write(3);
			stream.write(putInt(compound.textureSizeX));
			stream.write(putInt(compound.textureSizeX));
			stream.write(0);
			for(TurboList group : compound.getGroups()){
				if(group.stream().filter(poly -> poly.getType().isFMFExportable()).count() == 0) continue;
				write(stream, 2, group.id);
				for(PolygonWrapper wrapper : group){
					if(!wrapper.getType().isFMFExportable()) continue;
					boolean box = !wrapper.getType().isCylinder();
					stream.write(box ? 1 : 2);
					if(wrapper.name != null){
						write(stream, PM, wrapper.name);
					}
					if(nn(wrapper.pos)){
						writeVector(stream, PP, wrapper.pos);
					}
					if(nn(wrapper.rot)){
						writeVector(stream, PR, wrapper.rot);
					}
					if(nn(wrapper.off)){
						writeVector(stream, PF, wrapper.off);
					}
					if(wrapper.textureX >= 0 || wrapper.textureY >= 0){
						writeIntegers(stream, PT, wrapper.textureX, wrapper.textureY);
					}
					if(group.color != null){
						writeIntegers(stream, PL, group.color.packed);
					}
					if(box){
						BoxWrapper bwr = (BoxWrapper)wrapper;
						if(nn(bwr.size)){
							writeVector(stream, PBS, bwr.size);
						}
						if(bwr.anySidesOff()){
							writeBooleans(stream, PDF, bwr.sides);
						}
						if(wrapper instanceof ShapeboxWrapper){
							Vec3f[] corners = ((ShapeboxWrapper)wrapper).cornerArray();
							for(int i = 0; i < corners.length; i++){
								if(nn(corners[i])){
									stream.write(PBC);
									writeVector(stream, i, corners[i]);
								}
							}
						}
					}
					else{
						CylinderWrapper cyl = (CylinderWrapper)wrapper;
						if(cyl.radius > 0 || cyl.radius2 > 0 || cyl.length > 0){
							writeFloats(stream, PCRL, cyl.radius, cyl.radius2, cyl.length);
						}
						if(cyl.direction >= 0){
							writeIntegers(stream, PCD, cyl.direction);
						}
						if(cyl.segments > 2 || cyl.seglimit > 0){
							writeIntegers(stream, PCSG, cyl.segments, cyl.seglimit);
						}
						if(cyl.base != 1f || cyl.top != 1f){
							writeFloats(stream, PCSL, cyl.base, cyl.top);
						}
						if(nn(cyl.topoff)){
							writeVector(stream, PCTO, cyl.topoff);
						}
						if(nn(cyl.toprot)){
							writeVector(stream, PCTR, cyl.toprot);
						}
						if(cyl.anySidesOff()){
							writeBooleans(stream, PDF, cyl.bools);
						}
						if(cyl.seg_width != 0f || cyl.seg_height != 0f){
							writeFloats(stream, PCRT, cyl.seg_width, cyl.seg_height);
						}
					}
					if(wrapper.cuv.anyCustom()){
						boolean[] detached = new boolean[6];
						for(int i = 0; i < detached.length; i++){
							UVCoords coord = wrapper.cuv.get((box ? BoxFace.values() : CylFace.values())[i]);
							if(coord.absolute()) detached[i] = true;
							if(coord.automatic() || !wrapper.isFaceActive(coord.face())) continue;
							stream.write(PCU);
							stream.write(i);
							stream.write(coord.length());
							writeFloat(stream, coord.value());
						}
						boolean any = false;
						for(boolean bool : detached){
							if(bool){
								any = true;
								break;
							}
						}
						if(any) writeBooleans(stream, PDU, detached);
					}
					stream.write(0);
				}
				stream.write(0);
			}
			stream.write(0);
			/*stream.close();
			FileInputStream in = new FileInputStream(file);
			int r = -1;
			while((r = in.read()) > -1){
				log("# " + r + " / " + new String(new byte[]{ (byte) r }));
			}
			in.close();*/
		}
		catch(Exception e){
			log(e);
			return "Error:" + e.getMessage();
		}
		finally{
			try{
				if(stream != null) stream.close();
			}
			catch(IOException e){
				e.printStackTrace();
			}
		}
		return "Success!";
	}

	private boolean nn(Vec3f vec){
		return vec != null && (vec.x != 0f || vec.y != 0f || vec.z != 0f);
	}

	private byte[] putInt(int value){
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	private void write(FileOutputStream stream, int code, String string) throws IOException {
		stream.write(code);
		stream.write(string.getBytes(UTF_8));
		stream.write(0);
	}

	private void writeVector(FileOutputStream stream, int code, Vec3f vec) throws IOException {
		byte[] bytes = ByteBuffer.allocate(12).putFloat(vec.x).putFloat(vec.y).putFloat(vec.z).array();
		stream.write(code);
		stream.write(bytes);
	}

	private void writeIntegers(FileOutputStream stream, int code, int... ints) throws IOException {
		if(ints.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * ints.length);
		for(int i : ints) buffer.putInt(i);
		stream.write(code);
		stream.write(buffer.array());
	}

	private void writeFloats(FileOutputStream stream, int code, float... flts) throws IOException {
		if(flts.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * flts.length);
		for(float f : flts) buffer.putFloat(f);
		stream.write(code);
		stream.write(buffer.array());
	}

	private void writeBooleans(FileOutputStream stream, int code, boolean... bools) throws IOException {
		if(bools.length == 0) return;
		stream.write(code);
		for(boolean b : bools) stream.write(b ? 1 : 0);
	}

	/*private void writeInteger(FileOutputStream stream, int... ints) throws IOException {
		if(ints.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * ints.length);
		for(int i : ints) buffer.putInt(i);
		byte[] bytes = buffer.array();
		stream.write(bytes);
	}*/

	private void writeFloat(FileOutputStream stream, float... floats) throws IOException {
		if(floats.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * floats.length);
		for(float f : floats) buffer.putFloat(f);
		stream.write(buffer.array());
	}

	@Override
	public String getId(){
		return "fmf_exporter";
	}

	@Override
	public String getName(){
		return "FMF Exporter";
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
		return new String[]{ "model" };
	}

}
