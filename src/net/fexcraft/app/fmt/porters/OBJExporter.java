package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.TexturedVertex;
import net.fexcraft.lib.common.math.Vec3f;

/**
 * ALL RIGHTS RESERVED. &copy; 2019 Fexcraft.net
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class OBJExporter extends ExImPorter {

	private static final ArrayList<Setting> settings = new ArrayList<>();
	private static final String[] extensions = new String[] { "Wavefront Obj Model", "*.obj" };

	public OBJExporter(){
		settings.add(new Setting("rotate_model", Settings.oldrot()));
		settings.add(new Setting("rotate_y", Settings.oldrot() ? 180f : 0f));
		settings.add(new Setting("rotate_z", Settings.oldrot() ? 180f : 0f));
		settings.add(new Setting("rotate_x", 0f));
		settings.add(new Setting("flip_texture", true));
		settings.add(new Setting("scale", 1f));
		settings.add(new Setting("create_mtl", false));
		settings.add(new Setting("index_vertices", false));
		settings.add(new Setting("only_visible_groups", true));
		settings.add(new Setting("groups_as_objects", false));
		settings.add(new Setting("include_normals", false));
		settings.add(new Setting("invert_normals", true));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer();
		boolean bool = settings.get("rotate_model").getBooleanValue();
		boolean nog = settings.get("groups_as_objects").getBooleanValue();
		boolean nor = settings.get("include_normals").getBooleanValue();
		boolean inn = settings.get("invert_normals").getBooleanValue();
		buffer.append("# FMT-Marker OBJ-2\n#\n");
		float scale = settings.get("scale").getFloatValue();
		String mtlname = null;
		buffer.append("# Model exported via the Standard FMT OBJ Exporter\n");
		boolean mtl = settings.get("create_mtl").getBooleanValue();
		buffer.append("# FMT (Fex's Modelling Toolbox) v." + FMTB.VERSION + " &copy; " + Year.now().getValue() + " - Fexcraft.net\n");
		buffer.append("# All rights reserved. For this Model's License contact the Author/Creator.\n#\n");
		if(compound.getAuthors().size() > 0){
			for(String str : compound.getAuthors()){
				buffer.append("# Creator: " + str + "\n");
			}
			buffer.append("\n");
		}
		else{
			buffer.append("# Creator: Empty/FMT\n");
		}
		if(mtl) buffer.append("mtllib " + (mtlname = file.getName().substring(0, file.getName().length() - 4)) + ".mtl\n\n");
		buffer.append("# Model Name" + (nog ? ": " : "\no ") + validateName(compound.name) + "\n\n");
		buffer.append("# TextureSizeX: " + compound.tx(null) + "\n");
		buffer.append("# TextureSizeY: " + compound.ty(null) + "\n");
		buffer.append("# FlipAxes: " + bool + "\n\n");
		Axis3DL axis, axis1 = null;
		if(bool){
			float yaw = settings.get("rotate_y").getFloatValue();
			float pit = settings.get("rotate_z").getFloatValue();
			float rol = settings.get("rotate_x").getFloatValue();
			(axis1 = new Axis3DL()).setAngles(yaw, pit, rol);
		}
		//
		String gpfx = nog ? "o" : "g";
		// float texsx = 1f / compound.textureSizeX, texsy = 1f/ compound.textureSizeY;
		boolean onlyvis = settings.get("only_visible_groups").getBooleanValue();
		boolean index = settings.get("index_vertices").getBooleanValue();
		int faceid = index ? 0 : 1, norid = 0;
		HashMap<String, Integer> indices = new HashMap<>();
		for(TurboList list : compound.getGroups()){
			if(onlyvis && !list.visible) continue;
			buffer.append("# Group Name\n");
			axis = new Axis3DL();
			if(index) indices.clear();
			buffer.append(gpfx + " " + list.id + "\nusemtl fmt_material\n");
			for(PolygonWrapper wrapper : list){
				// if(!wrapper.getType().isCuboid()) continue;
				if(wrapper.getType().isMarker() || wrapper.getType().isBoundingBox()) continue;
				buffer.append("\n");
				if(wrapper.name != null){
					buffer.append("# ID: " + wrapper.name() + "\n");
				}
				//
				ArrayList<TexturedPolygon> polis = wrapper.getTurboObject(0).getFaces();
				axis.setAngles(-wrapper.rot.yCoord, -wrapper.rot.zCoord, -wrapper.rot.xCoord);
				for(TexturedPolygon poly : polis){
					String[] verttext = new String[poly.getVertices().length];
					for(int i = 0; i < verttext.length; i++){
						TexturedVertex vert = poly.getVertices()[i];
						Vec3f rotated = axis.getRelativeVector(vert.vector);
						if(bool){
							rotated = axis1.getRelativeVector(rotated.add(wrapper.pos));
							verttext[i] = (rotated.xCoord * scale) + " " + (rotated.yCoord * scale) + " " + (rotated.zCoord * scale);
						}
						else{
							verttext[i] = ((rotated.xCoord + wrapper.pos.xCoord) * scale) + " " + ((rotated.yCoord + wrapper.pos.yCoord) * scale) + " " + ((rotated.zCoord + wrapper.pos.zCoord) * scale);
						}
						if(!index || !indices.containsKey(verttext[i])){
							buffer.append("v " + verttext[i] + "\n");
						}
					}
					for(int i = 0; i < verttext.length; i++){
						if(index && indices.containsKey(verttext[i])) continue;
						TexturedVertex vert = poly.getVertices()[i];
						if(settings.get("flip_texture").getBooleanValue()){
							buffer.append("vt " + vert.textureX + " " + (-vert.textureY + 1f) + "\n");
						}
						else buffer.append("vt " + vert.textureX + " " + vert.textureY + "\n");
					}
					if(nor){
				        Vec3f vec0 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[0].vector));
				        Vec3f vec1 = new Vec3f(poly.getVertices()[1].vector.subtract(poly.getVertices()[2].vector));
				        Vec3f vec2 = vec1.crossProduct(vec0).normalize();
				        if(inn){
				        	vec2.xCoord *= -1;
				        	vec2.yCoord *= -1;
				        	vec2.zCoord *= -1;
				        }
						buffer.append("vn " + vec2.xCoord + " " + vec2.yCoord + " " + vec2.zCoord + "\n");
						norid++;
					}
					buffer.append("f");
					for(int i = 0; i < poly.getVertices().length; i++){
						int face = faceid + i;
						if(index){
							if(indices.containsKey(verttext[i])){
								face = indices.get(verttext[i]);
							}
							else{
								face = faceid += 1;
								indices.put(verttext[i], face);
							}
						}
						buffer.append(" " + face + "/" + face);
						if(nor){
							buffer.append("/" + norid);
						}
					}
					buffer.append("\n");
					if(!index) faceid += poly.getVertices().length;
				}
			}
			buffer.append("\n");
		}
		buffer.append("# FMT-Marker OBJ-END\n");
		//
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buffer);
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			log(e);
			return "Error:" + e.getMessage();
		}
		if(mtl){
			buffer = new StringBuffer();
			buffer.append("newmtl fmt_material\nKd 1.00 1.00 1.00\nmap_Kd minecraft:blocks/" + mtlname.toLowerCase());
			File mtlfile = new File(file.getParentFile(), mtlname + ".mtl");
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(mtlfile));
				writer.append(buffer);
				writer.flush();
				writer.close();
			}
			catch(IOException e){
				log(e);
				return "Error:" + e.getMessage();
			}
		}
		return "Success!";
	}

	private String validateName(String name){
		if(name == null || name.length() == 0) return "Unnamed";
		name.replace(" ", "");
		return name;
	}

	@Override
	public String getId(){
		return "obj_exporter";
	}

	@Override
	public String getName(){
		return "Wavefront OBJ";
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
		return new String[] { "model" };
	}

}
