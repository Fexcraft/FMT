package net.fexcraft.app.fmt.port.ex;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_FMF;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import org.joml.Vector3f;

import net.fexcraft.app.fmt.polygon.uv.BoxFace;
import net.fexcraft.app.fmt.polygon.uv.CylFace;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.json.JsonMap;

public class FMFExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("model");
	private static final ArrayList<Setting<?>> settings = new ArrayList<>();
	//from fvtm
	private static final int PP = 1, PR = 2, PF = 3, PT = 4, PL = 6, PM = 7, PDF = 8, PDU = 9, PCU = 10;
	private static final int PBS = 16, PBC = 17;
	private static final int PCRL = 16, PCD = 17, PCSG = 18, PCSL = 19, PCTO = 20, PCTR = 21, PCRT = 22, PCSO = 23;
	private static Axis3DL axe0 = new Axis3DL();
	private static Axis3DL axe1 = new Axis3DL();

	public FMFExporter(JsonMap map){
		settings.add(new Setting<>("modeldata", true, "exporter-fmf"));
		settings.add(new Setting<>("group_as_single_polygon", true, "exporter-fmf"));
		axe0.setAngles(0, 180, 0);
		axe1.setAngles(90, 0, 0);
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
		boolean flip = !FMT.MODEL.orient.rect();
		boolean sing = settings().get(1).bool();
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
				if(sing){
					//Axis3DL axe2 = new Axis3DL();
					Axis3DL rot = new Axis3DL();
					Pivot piv = model.getP(group.pivot);
					//axe2.setAngles(-piv.rot.y, -piv.rot.z, -piv.rot.x);
					stream.write(3);
					write(stream, PM, group.id);
					writeVector(stream, PP, piv.pos);
					writeVector(stream, PR, piv.rot);
					for(Polygon polygon : group){
						if(!valid(polygon.getShape())) continue;
						Polyhedron<GLObject> poly = polygon.glm;
						rot.setAngles(-polygon.rot.y, -polygon.rot.z, -polygon.rot.x);
						for(net.fexcraft.lib.frl.Polygon p : poly.polygons){
							for(Vertex vertex : p.vertices){
								Vec3f vec = /*axe2.get(*/rot.get(vertex.vector).add(poly.posX, poly.posY, poly.posZ);//);
								writeFloats(stream, PF, vec.x, vec.y, vec.z);
								writeFloats(stream, PT, vertex.u, vertex.v);
							}
							stream.write(PDF);
						}
					}
					stream.write(0);
				}
				else insertGroupPolygons(group, stream, flip);
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

	private void insertGroupPolygons(Group group, OutputStream stream, boolean flip) throws IOException {
		for(Polygon polygon : group){
			if(!valid(polygon.getShape())) continue;
			boolean isbox = polygon instanceof Box;
			boolean o = flip || (!isbox);// && !polygon.getShape().isCylinder());
			stream.write(o ? 3 : isbox ? 1 : 2);
			if(polygon.name(true) != null){
				write(stream, PM, polygon.name());
			}
			if(nn(polygon.pos)){
				if(flip){
					writeVector(stream, PP, axe1.getRelativeVector(axe0.getRelativeVector(polygon.pos)));
				}
				else writeVector(stream, PP, polygon.pos);
			}
			if(nn(polygon.rot)){
				if(flip){
					writeFloats(stream, PR, -polygon.rot.z, -polygon.rot.y, -polygon.rot.x);
				}
				else writeVector(stream, PR, polygon.rot);
			}
			if(!o && nn(polygon.off)){
				writeVector(stream, PF, polygon.off);
			}
			if(!o && (polygon.textureX >= 0 || polygon.textureY >= 0)){
				writeIntegers(stream, PT, polygon.textureX, polygon.textureY);
			}
			if(group.color.packed != RGB.WHITE.packed){
				writeIntegers(stream, PL, group.color.packed);
			}
			if(!flip){
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
				/*else if(polygon.getShape().isCylinder()){
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
				}*/
			}
			if(o){
				Polyhedron<GLObject> poly = polygon.glm;
				for(net.fexcraft.lib.frl.Polygon p : poly.polygons){
					for(Vertex vertex : p.vertices){
						if(flip){
							Vec3f vec = axe1.get(axe0.get(vertex.vector));
							writeFloats(stream, PF, vec.x, vec.y, vec.z);
						}
						else writeFloats(stream, PF, vertex.vector.x, vertex.vector.y, vertex.vector.z);
						//writeFloats(stream, 5, vertex.norm.x, vertex.norm.y, vertex.norm.z);
						writeFloats(stream, PT, vertex.u, vertex.v);
					}
					stream.write(PDF);
					//writeIntegers(stream, PDF, p.vertices.length);
				}
			}
			if(!o && polygon.cuv.any()){
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
	}

	private boolean valid(Shape shape){
		return !shape.isMarker() && !shape.isBoundingBox();//shape.isBox() || shape.isShapebox() || shape.isCylinder();
	}

	private boolean nn(Vector3f vec){
		return vec != null && (vec.x != 0f || vec.y != 0f || vec.z != 0f);
	}

	private byte[] putInt(int value){
		return ByteBuffer.allocate(4).putInt(value).array();
	}

	private void write(OutputStream stream, int code, String string) throws IOException {
		if(code > 0) stream.write(code);
		stream.write(string.getBytes(UTF_8));
		stream.write(0);
	}

	private void writeVector(OutputStream stream, int code, Vector3f vec) throws IOException {
		byte[] bytes = ByteBuffer.allocate(12).putFloat(vec.x).putFloat(vec.y).putFloat(vec.z).array();
		stream.write(code);
		stream.write(bytes);
	}

	private void writeIntegers(OutputStream stream, int code, int... ints) throws IOException {
		if(ints.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * ints.length);
		for(int i : ints) buffer.putInt(i);
		stream.write(code);
		stream.write(buffer.array());
	}

	private void writeFloats(OutputStream stream, int code, float... flts) throws IOException {
		if(flts.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * flts.length);
		for(float f : flts) buffer.putFloat(f);
		stream.write(code);
		stream.write(buffer.array());
	}

	private void writeBooleans(OutputStream stream, int code, boolean... bools) throws IOException {
		if(bools.length == 0) return;
		stream.write(code);
		for(boolean b : bools) stream.write(b ? 1 : 0);
	}

	/*private void writeInteger(OutputStream stream, int... ints) throws IOException {
		if(ints.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * ints.length);
		for(int i : ints) buffer.putInt(i);
		byte[] bytes = buffer.array();
		stream.write(bytes);
	}*/

	private void writeFloat(OutputStream stream, float... floats) throws IOException {
		if(floats.length == 0) return;
		ByteBuffer buffer = ByteBuffer.allocate(4 * floats.length);
		for(float f : floats) buffer.putFloat(f);
		stream.write(buffer.array());
	}

}
