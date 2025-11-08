package net.fexcraft.app.fmt.porters;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.*;
import net.fexcraft.app.fmt.wrappers.face.BoxFace;
import net.fexcraft.app.fmt.wrappers.face.CylFace;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;
import net.fexcraft.lib.common.math.*;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.tmt.ModelRendererTurbo;
import org.joml.Vector2f;
import org.joml.Vector3f;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static java.nio.charset.StandardCharsets.UTF_8;
import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class BEOExporter extends ExImPorter {

	private static final String[] extensions = new String[]{ "Byte Encoded Object (bob)", "*.bob", "*.beo" };
	private static final ArrayList<Setting> settings = new ArrayList<>();
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


	public BEOExporter(){
		settings.add(new Setting("group_as_single_polygon", false));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound model, File file, ArrayList<TurboList> groups, Map<String, Setting> settings){
		FileOutputStream stream = null;
		boolean sing = settings.get("group_as_single_polygon").getBooleanValue();
		vecs.clear();
		uvs.clear();
		try{
			stream = new FileOutputStream(file);
			stream.write(new byte[]{ 6, 2, 15, FORMAT });
			if(model.name != null){
				write(stream, NAME, model.name);
			}
			for(String str : model.getAuthors()){
				write(stream, AUTHOR, str);
			}
			writeIntegers(stream, TEXSIZE, model.textureSizeX, model.textureSizeY);
			for(TurboList group : groups){
				if(group.stream().filter(poly -> valid(poly.getType())).count() == 0) continue;
				write(stream, GROUP, group.id);
				if(sing){
					M4DW rot = M4DW.create();
					SwivelPointLite piv = model.pivots.get(group.pivot_root);
					stream.write(OBJECT);
					write(stream, NAME, group.id);
					writeVector(stream, POSITION, piv.pos);
					writeVector(stream, ROTATION, piv.rot);
					for(PolygonWrapper polygon : group){
						if(!valid(polygon.getType())) continue;
						ModelRendererTurbo poly = polygon.getTurboObject(0);
						rot.setDegrees(-polygon.rot.y, -polygon.rot.z, -polygon.rot.x);
						fillPoly(stream, poly, rot);
					}
					stream.write(END);
				}
				else{
					for(PolygonWrapper polygon : group){
						if(!valid(polygon.getType())) continue;
						stream.write(OBJECT);
						if(polygon.name != null){
							write(stream, NAME, polygon.name());
						}
						if(nn(polygon.pos)){
							writeVector(stream, POSITION, polygon.pos);
						}
						if(nn(polygon.rot)){
							writeVector(stream, ROTATION, polygon.rot);
						}
						fillPoly(stream, polygon.getTurboObject(0), null);
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

	private void fillPoly(FileOutputStream stream, ModelRendererTurbo poly, M4DW rot) throws IOException {
		int len;
		for(TexturedPolygon p : poly.getFaces()){
			len = p.getVertices().length;
			int[] ids = new int[len + len + 1];
			ids[0] = len;
			for(int v = 0; v < len; v++){
				V3D vec = new MV3D(p.getVertices()[v].vector.x, p.getVertices()[v].vector.y, p.getVertices()[v].vector.z);
				if(rot != null) rot.rotate(vec, vec).add(poly.rotationPointX, poly.rotationPointY, poly.rotationPointZ);
				ids[v + 1] = vecs.indexOf(vec);
				if(ids[v + 1] < 0){
					writeFloats(stream, VECTOR, (float)vec.x, (float)vec.y, (float)vec.z);
					ids[v + 1] = vecs.size();
					vecs.add(vec);
				}
				Vector2f uv = new Vector2f(p.getVertices()[v].textureX, p.getVertices()[v].textureY);
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

	private boolean valid(ShapeType shape){
		return !shape.isMarker() && !shape.isBoundingBox();
	}

	private boolean nn(Vec3f vec){
		return vec != null && (vec.x != 0f || vec.y != 0f || vec.z != 0f);
	}

	private void write(OutputStream stream, int code, String string) throws IOException {
		if(code > 0) stream.write(code);
		stream.write(string.getBytes(UTF_8));
		stream.write(0);
	}

	private void writeVector(OutputStream stream, int code, Vec3f vec) throws IOException {
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

	@Override
	public String getId(){
		return "beo_exporter";
	}

	@Override
	public String getName(){
		return "BOB/BEO";
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
		return new String[]{ "model" };
	}

}
