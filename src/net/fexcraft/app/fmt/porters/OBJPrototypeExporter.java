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
import net.fexcraft.app.fmt.utils.Axis3D;
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
	private static final String[] extensions = new String[]{ ".obj" };
	
	public OBJPrototypeExporter(){
		settings.add(new Setting(Type.BOOLEAN, "flip_model", true));
		settings.add(new Setting(Type.FLOAT, "scale", 1));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}
	
	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		StringBuffer buffer = new StringBuffer(); boolean bool = settings.get("flip_model").getBooleanValue();
		buffer.append("# FMT-Marker OBJ-2\n#\n"); float scale = settings.get("scale").getFloatValue();
		buffer.append("# Model exported via the Standard FMT OBJ Exporter\n");
		buffer.append("# FMT (Fex's Modelling Toolbox) v." + FMTB.version + " &copy; " + Year.now().getValue() + " - Fexcraft.net\n");
		buffer.append("# All rights reserved. For this Model's License contact the Author/Creator.\n#\n");
		if(compound.creators.size() > 0){
			for(String str : compound.creators){
				buffer.append("# Creator: " + str + "\n");
			}
			buffer.append("\n");
		} else { buffer.append("# Creator: Empty/FMT\n"); } int faceid = 1;
		buffer.append("# Model Name\no " + validateName(compound.name) + "\n\n");
		buffer.append("# TextureSizeX: " + compound.tx(null) + "\n");
		buffer.append("# TextureSizeY: " + compound.ty(null) + "\n");
		buffer.append("# FlipAxes: true\n\n"); Axis3D axis, axis1 = null;
		if(bool) (axis1 = new Axis3D()).setAngles(180, 180, 0);
		//
		for(TurboList list : compound.getGroups()){
			buffer.append("# Group Name\n"); axis = new Axis3D();
			buffer.append("g " + list.id + "\nusemtl fmt_null_material\n");
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
						buffer.append("vt " + vert.textureX + " " + vert.textureY + "\n");
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
