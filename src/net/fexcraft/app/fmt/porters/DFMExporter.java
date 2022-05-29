package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

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
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.math.TexturedVertex;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class DFMExporter extends ExImPorter {
	
	protected static final String[] extensions = new String[]{ "FlansMod Java Model", "*.java" };
	protected static final String tab = "\t";//"    ";
	protected static final String tab2 = tab + tab;
	protected static final String tab3 = tab2 + tab;
	protected boolean pergroupinit;
	protected String modelname, modeltype, packid;
	protected ArrayList<Setting> settings = new ArrayList<>();
	private static final String VERSION = "1.0";
	
	public DFMExporter(){
		settings.add(new Setting(Type.STRING, "pack_id", "your_pack_id"));
		settings.add(new Setting(Type.STRING, "model_type", "ModelVehicle"));
		settings.add(new Setting(Type.STRING, "model_name", "default"));
		settings.add(new Setting(Type.BOOLEAN, "per_group_init", false));
		settings.add(new Setting(Type.INTEGER, "max_pg_init_count", 250));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	/** The in-string "TODO" markers are for those who implement the model into the game. */
	@Override
	public String exportModel(GroupCompound compound, File file, ArrayList<TurboList> groups, Map<String, Setting> settings){
		packid = settings.get("pack_id").getStringValue();
		modelname = settings.get("model_name").getStringValue();
		if(modelname.equals("default") || modelname.equals("null")) modelname = null;
		modelname = validateName(modelname == null ? compound.name + "Model" : modelname);
		modeltype = settings.get("model_type").getStringValue();
		//
		pergroupinit = settings.get("per_group_init").getBooleanValue();
		StringBuffer buffer = new StringBuffer();
		buffer.append("//FMT-Marker DFM-" + VERSION + "\n");
		for(String cr : compound.getAuthors()){
			buffer.append("//Creator: " + cr + "\n");
		} buffer.append("\n");
		if(settings.get("per_group_init").getBooleanValue()) buffer.append("//Using PER-GROUP-INIT mode with limit '" + settings.get("max_pg_init_count").getValue() + "'!\n");
		buffer.append("package com.flansmod.client.model." + packid + ";\n\n");
		buffer.append("import com.flansmod.client.model." + modeltype + ";\n");
		buffer.append("import com.flansmod.client.tmt.ModelRendererTurbo;\n");
		buffer.append("import com.flansmod.client.tmt.PositionTextureVertex;\n");
		buffer.append("import com.flansmod.client.tmt.TexturedPolygon;\n\n");
		buffer.append("/** This file was exported via the (Default) FlansMod Exporter of<br>\n");
		buffer.append(" *  FMT (Fex's Modelling Toolbox) v." + FMTB.VERSION + " &copy; " + Year.now().getValue() + " - Fexcraft.net<br>\n");
		buffer.append(" *  All rights reserved. For this Model's License contact the Author/Creator.\n */\n");
		buffer.append("public class " + modelname + " extends " + modeltype + " {\n\n");
		buffer.append(tab + "private int textureX = " + compound.tx(null) + ";\n");
		buffer.append(tab + "private int textureY = " + compound.tx(null) + ";\n\n");
		buffer.append(tab + "public " + modelname + "(){\n");
		for(TurboList list : groups){
			buffer.append(tab2 + list.exportID() + " = new ModelRendererTurbo[" + list.size() + "];\n");
		} buffer.append(tab2 + "//\n");
		if(pergroupinit){
			int count = settings.get("max_pg_init_count").getValue();
			for(TurboList list : groups){
				if(list.size() > count){
					int subs = list.size() / count; if(list.size() % count > 0) subs++;
					for(int i = 0; i < subs; i++){
						buffer.append(tab2 + "initGroup_" + list.exportID() + i + "();\n");
					}
				}
				else buffer.append(tab2 + "initGroup_" + list.exportID() + "();\n");
			}
			appendEnding(compound, buffer);
			buffer.append(tab + "}\n\n");
			//
			for(TurboList list : groups){
				if(list.size() > count){
					int subs = list.size() / count; if(list.size() % count != 0) subs++;
					for(int i = 0; i < subs; i++){
						buffer.append(tab + "private final void initGroup_" + list.exportID() + i + "(){\n");
						int j = i * count, k = (i + 1) * count;
						List<PolygonWrapper> sub = list.subList(j, k >= list.size() ? list.size() - 1 : k);
						insertList(compound, sub, j, list.exportID(), buffer);
						buffer.append(tab + "}\n\n");
					}
				}
				else{
					buffer.append(tab + "private final void initGroup_" + list.exportID() + "(){\n");
					insertList(compound, list, 0, null, buffer);
					buffer.append(tab + "}\n\n");
				}
			}
			//
			buffer.append("}\n");
		}
		else{
			for(TurboList list : groups){
				insertList(compound, list, 0, null, buffer);
			}
			appendEnding(compound, buffer);
			buffer.append(tab + "}\n\n}\n");
		}
		//
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buffer); writer.flush(); writer.close();
		}
		catch(IOException e){
			log(e);
			return "Error:" + e.getMessage();
		}
		return "Success!";
	}

	private void appendEnding(GroupCompound compound, StringBuffer buffer){
		if(compound.pos != null){
			buffer.append(tab2 + "translateAll(" + (compound.pos.x / 16) + "f, " + (compound.pos.y / 16) + "f, " + (compound.pos.z / 16) + "f);\n");
		}
		buffer.append(tab2 + "flipAll();\n");
	}

	private void insertList(GroupCompound compound, List<PolygonWrapper> list, int index, String id, StringBuffer buffer){
		String name = id;
		if(list instanceof TurboList){
			TurboList turbo = (TurboList)list; name = turbo.exportID();
			if(list.isEmpty()) return;
		}
		for(PolygonWrapper wrapper : list){
			buffer.append(tab2 + name + "[" + index + "] = new ModelRendererTurbo(this, " + wrapper.textureX + ", "
				+ wrapper.textureY + ", textureX, textureY);" + (wrapper.name == null ? "" : " // " + wrapper.name()) + "\n");
			switch(wrapper.getType()){
				case BOX:{
					if(!wrapper.cuv.anyCustom()){
						BoxWrapper box = (BoxWrapper)wrapper;
						buffer.append(tab2 + name + "[" + index + "]" + format(".addBox(%s, %s, %s, %s, %s, %s, 0f)", null,
							wrapper.off.x, wrapper.off.y, wrapper.off.z,
							box.size.x, box.size.y, box.size.z) + ";\n");
						break;
					}
				}
				case SHAPEBOX:{
					if(!wrapper.cuv.anyCustom()){
						ShapeboxWrapper box = (ShapeboxWrapper)wrapper;
						buffer.append(tab2 + name + "[" + index + "]" + format(".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
							wrapper.off.x, wrapper.off.y, wrapper.off.z, box.size.x, box.size.y, box.size.z,
							box.cor0.x, box.cor0.y, box.cor0.z, box.cor1.x, box.cor1.y, box.cor1.z,
							box.cor2.x, box.cor2.y, box.cor2.z, box.cor3.x, box.cor3.y, box.cor3.z,
							box.cor4.x, box.cor4.y, box.cor4.z, box.cor5.x, box.cor5.y, box.cor5.z,
							box.cor6.x, box.cor6.y, box.cor6.z, box.cor7.x, box.cor7.y, box.cor7.z) + ";\n");
						break;
					}
				}
				case CYLINDER:{
					CylinderWrapper cyl = (CylinderWrapper)wrapper;
					if(!wrapper.cuv.anyCustom() && cyl.radius2 == 0f){
						buffer.append(tab2 + name + "[" + index + "].flip = true;\n");
						buffer.append(tab2 + name + "[" + index + "]" + format(".addCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s)", null, 
							wrapper.off.x, wrapper.off.y, wrapper.off.z,
							cyl.radius, cyl.length, cyl.segments, cyl.base, cyl.top, cyl.direction) + ";\n");
						break;
					}
					//else continue with the converter bellow
				}
				case OBJ:
				case SPHERE:
				case TEXRECT_A:
				case TEXRECT_B:
				default:{
					buffer.append(tab2 + "{//LEGACY CONVERTER START\n");
					ModelRendererTurbo turbo = wrapper.getTurboObject(0); int face = 0;
					buffer.append(tab3 + "TexturedPolygon[] faces = new TexturedPolygon[" + turbo.getFaces().size() + "]; PositionTextureVertex[] vertices = null;\n");
					for(TexturedPolygon poly : turbo.getFaces()){
						buffer.append(tab3 + "vertices = new PositionTextureVertex[" + poly.getVertices().length + "];\n");
						for(int i = 0; i < poly.getVertices().length; i++){
							TexturedVertex vertex = poly.getVertices()[i];
							buffer.append(tab3 + "vertices[" + i + "] = new PositionTextureVertex(" + vertex.vector.x + "f, " + vertex.vector.y
								+ "f, " + vertex.vector.z + "f, " + vertex.textureX + "f, " + vertex.textureY + "f);\n");
						}
						buffer.append(tab3 + "faces[" + face + "] = new TexturedPolygon(vertices);\n"); face++;
					}
					buffer.append(tab3 + "//\n");
					buffer.append(tab3 + name + "[" + index + "].copyTo(new PositionTextureVertex[0], faces);\n");
					buffer.append(tab2 + "}//LEGACY CONVERTER END\n");
					break;
				}
			}
			buffer.append(tab2 + name + "[" + index + "].setRotationPoint(" + wrapper.pos.x + "f, " + wrapper.pos.y + "f, " + wrapper.pos.z + "f);\n");
			if(wrapper.rot.x != 0f){
				buffer.append(tab2 + name + "[" + index + "].rotateAngleX = " + (float)Math.toRadians(wrapper.rot.x) + "f;\n");
			}
			if(wrapper.rot.y != 0f){
				buffer.append(tab2 + name + "[" + index + "].rotateAngleY = " + (float)Math.toRadians(wrapper.rot.y) + "f;\n");
			}
			if(wrapper.rot.z != 0f){
				buffer.append(tab2 + name + "[" + index + "].rotateAngleZ = " + (float)Math.toRadians(-wrapper.rot.z) + "f;\n");
			} buffer.append("\n");
			//
			if(list instanceof TurboList && !((TurboList)list).exoff.isNull()){
				TurboList turbo = (TurboList)list;
				buffer.append(tab2 + "translate(" + name + format(",%s, %s, %s);\n", null, turbo.exoff.x, turbo.exoff.y, turbo.exoff.z));
			}
			index++;
		}
	}

	private String format(String string, String add, float... arr){
		Object[] strs = new Object[arr.length + (add == null ? 0 : 1)];
		for(int i = 0; i < arr.length; i++){ strs[i] = arr[i] % 1.0f != 0 ? String.format("%s", arr[i]) + "f" : String.format("%.0f", arr[i]); }
		if(add != null) strs[arr.length] = add; return String.format(string, strs);
	}

	protected String validateName(String name){
		if(name == null || name.length() == 0) return "Unnamed"; /*String[] art = name.split(" ");
		for(int i = 0; i < art.length; i++){
			if(art[i].length() == 0) continue; if(art[i].length() < 2) art[i] = art[i].toUpperCase();
			art[i] = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		} name = ""; for(String str : art) name += str; return name;*/
		//Making sure it starts upper-case, eventually CamelCase output.
		return name.trim().replace(" ", "_").replaceAll("[^a-zA-Z0-9 _]", "");
	}


	@Override
	public String getId(){
		return "default_flansmod";
	}

	@Override
	public String getName(){
		return "Default Flansmod";
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
		return export ? settings : nosettings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}

}
