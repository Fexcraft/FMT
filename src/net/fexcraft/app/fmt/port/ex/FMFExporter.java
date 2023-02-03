package net.fexcraft.app.fmt.port.ex;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.polygon.Box;
import net.fexcraft.app.fmt.polygon.Cylinder;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Shape;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.CylFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.json.JsonMap;

public class FMFExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("model");
	public static FileType TYPE_FMF = new FileType("Fex's Model Format", "*.fmf");
	private static final ArrayList<Setting<?>> settings = new ArrayList<>();
	//from fvtm
	private static final int PP = 1, PR = 2, PF = 3, PT = 4, PL = 6, PM = 7, PDF = 8, PDU = 9, PCU = 10;
	private static final int PBS = 16, PBC = 17;
	private static final int PCRL = 16, PCD = 17, PCSG = 18, PCSL = 19, PCTO = 20, PCTR = 21, PCRT = 22, PCSO = 23;

	public FMFExporter(JsonMap map){
		settings.add(new Setting<>("modeldata", true, "exporter-fmf"));
	}

	@Override
	public String id(){
		return "fmf";
	}

	@Override
	public String name(){
		return "FMF (Fex's Model Format)";
	}

	@Override
	public FileType extensions(){
		return TYPE_FMF;
	}

	@Override
	public List<String> categories(){
		return categories;
	}

	@Override
	public List<Setting<?>> settings(){
		return settings;
	}

	@Override
	public boolean nogroups(){
		return false;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		FileOutputStream stream = null;
		try{
			stream = new FileOutputStream(file);
			stream.write(new byte[]{ 6, 13, 6, 1 });
			if(model.name != null){
				write(stream, 1, model.name);
			}
			for(String str : model.getAuthors().keySet()){
				write(stream, 4, str);
			}
			stream.write(3);
			stream.write(putInt(model.texSizeX));
			stream.write(putInt(model.texSizeY));
			stream.write(0);
			for(Group group : groups){
				if(group.stream().filter(poly -> valid(poly.getShape())).count() == 0) continue;
				write(stream, 2, group.id);
				for(Polygon polygon : group){
					if(!valid(polygon.getShape())) continue;
					boolean isbox = !polygon.getShape().isCylinder();
					stream.write(isbox ? 1 : 2);
					if(polygon.name(true) != null){
						write(stream, PM, polygon.name());
					}
					if(nn(polygon.pos)){
						writeVector(stream, PP, polygon.pos);
					}
					if(nn(polygon.rot)){
						writeVector(stream, PR, polygon.rot);
					}
					if(nn(polygon.off)){
						writeVector(stream, PF, polygon.off);
					}
					if(polygon.textureX >= 0 || polygon.textureY >= 0){
						writeIntegers(stream, PT, polygon.textureX, polygon.textureY);
					}
					if(group.color != null){
						writeIntegers(stream, PL, group.color.packed);
					}
					if(isbox){
						Box box = (Box)polygon;
						if(nn(box.size)){
							writeVector(stream, PBS, box.size);
						}
						if(box.anySidesOff()){
							writeBooleans(stream, PDF, box.sides);
						}
						if(polygon instanceof Shapebox){
							Vector3f[] corners = ((Shapebox)polygon).corners();
							for(int i = 0; i < corners.length; i++){
								if(nn(corners[i])){
									stream.write(PBC);
									writeVector(stream, i, corners[i]);
								}
							}
						}
					}
					else{
						Cylinder cyl = (Cylinder)polygon;
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
						if(cyl.seg_width > 0f || cyl.seg_height > 0f){
							writeFloats(stream, PCRT, cyl.seg_width, cyl.seg_height);
						}
						if(cyl.seg_off != 0f){
							writeFloats(stream, PCSO, cyl.seg_off);
						}
					}
					if(polygon.cuv.any()){
						boolean[] detached = new boolean[6];
						for(int i = 0; i < detached.length; i++){
							UVCoords coord = polygon.cuv.get((isbox ? BoxFace.values() : CylFace.values())[i]);
							if(coord.detached()) detached[i] = true;
							if(coord.automatic() || !polygon.isActive(coord.face())) continue;
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
			//
			if(settings.get(0).bool()){
				ArrayList<String> programs = new ArrayList<>();
				//TODO export animations as programs
				if(programs.size() > 0){
					write(stream, 6, "Programs");
					for(String string : programs){
						write(stream, 7, string);
					}
				}
				if(model.export_values.size() > 0){
					for(Entry<String, String> entry : model.export_values.entrySet()){
						write(stream, 5, entry.getKey());
						write(stream, 0, entry.getValue());
					}
				}
				if(model.export_listed_values.size() > 0){
					for(Entry<String, ArrayList<String>> entry : model.export_listed_values.entrySet()){
						if(entry.getValue().size() == 0) continue;
						write(stream, 6, entry.getKey());
						for(String string : entry.getValue()){
							write(stream, 7, string);
						}
					}
				}
			}
			//
			stream.write(0);
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
		return "export.complete";
	}

	private boolean valid(Shape shape){
		return shape.isBox() || shape.isShapebox() || shape.isCylinder();
	}

	private boolean nn(Vector3f vec){
		return vec != null && (vec.x != 0f || vec.y != 0f || vec.z != 0f);
	}

	private byte[] putInt(int value){
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	private void write(FileOutputStream stream, int code, String string) throws IOException {
		if(code > 0) stream.write(code);
		stream.write(string.getBytes(UTF_8));
		stream.write(0);
	}

	private void writeVector(FileOutputStream stream, int code, Vector3f vec) throws IOException {
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

}
