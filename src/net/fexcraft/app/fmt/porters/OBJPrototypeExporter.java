package net.fexcraft.app.fmt.porters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
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
public class OBJPrototypeExporter extends ExImPorter {
	
	private static final ArrayList<Setting> settings = new ArrayList<>();
	private static final String[] extensions = new String[]{ "Wavefront Obj Model", "*.obj" };
	
	public OBJPrototypeExporter(){
		settings.add(new Setting(Type.BOOLEAN, "rotate_model", Settings.oldrot()));
		settings.add(new Setting(Type.FLOAT, "rotate_y", Settings.oldrot() ? 0 : 180));
		settings.add(new Setting(Type.FLOAT, "rotate_z", Settings.oldrot() ? 0 : 180));
		settings.add(new Setting(Type.FLOAT, "rotate_x", 0));
		settings.add(new Setting(Type.BOOLEAN, "flip_texture", true));
		settings.add(new Setting(Type.FLOAT, "scale", 1f));
		settings.add(new Setting(Type.BOOLEAN, "create_mtl", false));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}
	
	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer(); boolean bool = settings.get("rotate_model").getBooleanValue();
		buffer.append("# FMT-Marker OBJ-2\n#\n"); float scale = settings.get("scale").getFloatValue(); String mtlname = null;
		buffer.append("# Model exported via the Standard FMT OBJ Exporter\n"); boolean mtl = settings.get("create_mtl").getBooleanValue();
		buffer.append("# FMT (Fex's Modelling Toolbox) v." + FMTB.VERSION + " &copy; " + Year.now().getValue() + " - Fexcraft.net\n");
		buffer.append("# All rights reserved. For this Model's License contact the Author/Creator.\n#\n");
		if(compound.creators.size() > 0){
			for(String str : compound.creators){
				buffer.append("# Creator: " + str + "\n");
			}
			buffer.append("\n");
		} else { buffer.append("# Creator: Empty/FMT\n"); } int faceid = 1;
		if(mtl) buffer.append("mtllib " + (mtlname = file.getName().substring(0, file.getName().length() - 4)) + ".mtl\n\n");
		buffer.append("# Model Name\no " + validateName(compound.name) + "\n\n");
		buffer.append("# TextureSizeX: " + compound.tx(null) + "\n");
		buffer.append("# TextureSizeY: " + compound.ty(null) + "\n");
		buffer.append("# FlipAxes: " + bool + "\n\n"); Axis3DL axis, axis1 = null;
		if(bool){
			float yaw = settings.get("rotate_y").getFloatValue();
			float pit = settings.get("rotate_z").getFloatValue();
			float rol = settings.get("rotate_x").getFloatValue();
			(axis1 = new Axis3DL()).setAngles(yaw, pit, rol);
		}
		//
		//float texsx = 1f / compound.textureSizeX, texsy = 1f/ compound.textureSizeY;
		for(TurboList list : compound.getGroups()){
			buffer.append("# Group Name\n"); axis = new Axis3DL();
			buffer.append("g " + list.id + "\nusemtl fmt_material\n");
			for(PolygonWrapper wrapper : list){
				//if(!wrapper.getType().isCuboid()) continue;
				buffer.append("\n");
				if(wrapper.name != null){
					buffer.append("# ID: " + wrapper.name() + "\n");
				}
				//
				TexturedPolygon[] polis = wrapper.getTurboObject(0).getFaces();
				axis.setAngles(-wrapper.rot.yCoord, -wrapper.rot.zCoord, -wrapper.rot.xCoord);
				for(TexturedPolygon poly : polis){
					for(TexturedVertex vert : poly.getVertices()){
						Vec3f rotated = axis.getRelativeVector(vert.vector);
						if(bool){
							rotated = axis1.getRelativeVector(rotated.add(wrapper.pos));
							buffer.append("v " + (rotated.xCoord * scale) + " " + (rotated.yCoord * scale) + " " + (rotated.zCoord * scale) + "\n");
						}
						else{
							buffer.append("v " + ((rotated.xCoord + wrapper.pos.xCoord) * scale)
								+ " " + ((rotated.yCoord + wrapper.pos.yCoord) * scale)
								+ " " + ((rotated.zCoord + wrapper.pos.zCoord) * scale) + "\n");
						}
					}
					for(TexturedVertex vert : poly.getVertices()){
						if(settings.get("flip_texture").getBooleanValue()){
							buffer.append("vt " + vert.textureX + " " + (-vert.textureY + 1f) + "\n");
						} else buffer.append("vt " + vert.textureX + " " + vert.textureY + "\n");
					}
					buffer.append("f"); for(int i = 0; i < poly.getVertices().length; i++){
						buffer.append(" " + (faceid + i) + "/" + (faceid + i));
					} buffer.append("\n"); faceid += poly.getVertices().length;
				}
			} buffer.append("\n");
		}
		buffer.append("# FMT-Marker OBJ-END\n");
		//
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buffer); writer.flush(); writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
		if(mtl){
			buffer = new StringBuffer(); buffer.append("newmtl fmt_material\nKd 1.00 1.00 1.00\nmap_Kd minecraft:blocks/" + mtlname.toLowerCase());
			File mtlfile = new File(file.getParentFile(), mtlname + ".mtl");
			try{
				BufferedWriter writer = new BufferedWriter(new FileWriter(mtlfile));
				writer.append(buffer); writer.flush(); writer.close();
			}
			catch(IOException e){
				e.printStackTrace();
				return "Error:" + e.getMessage();
			}
		}
		return "Success!";
	}

	private String validateName(String name){
		if(name == null || name.length() == 0) return "Unnamed"; name.replace(" ", ""); return name;
	}

	@Override
	public String getId(){
		return "obj_exporter";
	}

	@Override
	public String getName(){
		return "Wavefront OBJ - Prototype";
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
