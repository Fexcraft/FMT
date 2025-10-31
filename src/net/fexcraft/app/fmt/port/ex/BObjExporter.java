package net.fexcraft.app.fmt.port.ex;

import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.M4DW;
import net.fexcraft.lib.common.math.MV3D;
import net.fexcraft.lib.common.math.V3D;
import net.fexcraft.lib.frl.Polyhedron;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_BOB;
import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class BObjExporter implements Exporter {

	private static final List<String> categories = Arrays.asList("model");
	private static final ArrayList<Setting<?>> settings = new ArrayList<>();
	//
	public static final int FORMAT = 1;
	private static final int END = 0;
	private static final int NAME = 1;
	private static final int AUTHOR = 2;
	private static final int TEXSIZE = 3;
	private static final int GROUP = 4;
	private static final int OBJECT = 5;
	private static final int POSITION = 2;
	private static final int ROTATION = 3;
	private static final int VECTOR = 4;
	private static final int UV = 5;
	private static final int NORMAL = 6;
	private static final int FACE = 7;
	private ArrayList<V3D> vecs = new ArrayList<>();
	private ArrayList<Vector2f> uvs = new ArrayList<>();

	public BObjExporter(JsonMap map){
		settings.add(new Setting<>("group_as_single_polygon", true, "exporter-fmf"));
	}

	@Override
	public String id(){
		return "bob";
	}

	@Override
	public String name(){
		return "Byte Encoded Object (bob)";
	}

	@Override
	public FileType extensions(){
		return TYPE_BOB;
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
		boolean sing = settings().get(0).bool();
		vecs.clear();
		uvs.clear();
		try{
			stream = new FileOutputStream(file);
			stream.write(new byte[]{ 6, 2, 15, FORMAT });
			if(model.name != null){
				write(stream, NAME, model.name);
			}
			for(String str : model.getAuthors().keySet()){
				write(stream, AUTHOR, str);
			}
			writeIntegers(stream, TEXSIZE, model.texSizeX, model.texSizeY);
			for(Group group : groups){
				if(group.stream().filter(poly -> valid(poly.getShape())).count() == 0) continue;
				write(stream, GROUP, group.id);
				if(sing){
					M4DW rot = M4DW.create();
					Pivot piv = model.getP(group.pivot);
					stream.write(OBJECT);
					write(stream, NAME, group.id);
					writeVector(stream, POSITION, piv.pos);
					writeVector(stream, ROTATION, piv.rot);
					for(Polygon polygon : group){
						if(!valid(polygon.getShape())) continue;
						Polyhedron<GLObject> poly = polygon.glm;
						rot.setDegrees(-polygon.rot.y, -polygon.rot.z, -polygon.rot.x);
						fillPoly(stream, poly, rot);
					}
					stream.write(END);
				}
				else{
					for(Polygon polygon : group){
						if(!valid(polygon.getShape())) continue;
						stream.write(OBJECT);
						if(polygon.name(true) != null){
							write(stream, NAME, polygon.name());
						}
						if(nn(polygon.pos)){
							writeVector(stream, POSITION, polygon.pos);
						}
						if(nn(polygon.rot)){
							writeVector(stream, ROTATION, polygon.rot);
						}
						fillPoly(stream, polygon.glm, null);
						stream.write(END);
					}
				}
				stream.write(END);
			}
			//
			stream.write(END);
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
		vecs.clear();
		uvs.clear();
		return "export.complete";
	}

	private void fillPoly(FileOutputStream stream, Polyhedron<GLObject> poly, M4DW rot) throws IOException {
		int len;
		for(net.fexcraft.lib.frl.Polygon p : poly.polygons){
			len = p.vertices.length;
			int[] ids = new int[len + len + 1];
			ids[0] = len;
			for(int v = 0; v < len; v++){
				V3D vec = new MV3D(p.vertices[v].vector.x, p.vertices[v].vector.y, p.vertices[v].vector.z);
				if(rot != null) rot.rotate(vec, vec).add(poly.posX, poly.posY, poly.posZ);
				ids[v + 1] = vecs.indexOf(vec);
				if(ids[v + 1] < 0){
					writeFloats(stream, VECTOR, (float)vec.x, (float)vec.y, (float)vec.z);
					ids[v + 1] = vecs.size();
					vecs.add(vec);
				}
				Vector2f uv = new Vector2f(p.vertices[v].u, p.vertices[v].v);
				ids[v + len + 1] = uvs.indexOf(uv);
				if(ids[v + len + 1] < 0){
					writeFloats(stream, UV, uv.x, uv.y);
					ids[v + len + 1] = uvs.size();
					uvs.add(uv);
				}
			}
			writeIntegers(stream, FACE, ids);
		}
	}

	private boolean valid(Shape shape){
		return !shape.isMarker() && !shape.isBoundingBox();
	}

	private boolean nn(Vector3f vec){
		return vec != null && (vec.x != 0f || vec.y != 0f || vec.z != 0f);
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
		ByteBuffer buffer = ByteBuffer.allocate(4 * ints.length);
		for(int i : ints) buffer.putInt(i);
		stream.write(code);
		stream.write(buffer.array());
	}

	private void writeFloats(OutputStream stream, int code, float... flts) throws IOException {
		ByteBuffer buffer = ByteBuffer.allocate(4 * flts.length);
		for(float f : flts) buffer.putFloat(f);
		stream.write(code);
		stream.write(buffer.array());
	}

}
