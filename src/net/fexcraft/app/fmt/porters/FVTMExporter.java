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
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
import net.fexcraft.app.fmt.utils.Settings.Setting;
import net.fexcraft.app.fmt.utils.Settings.Type;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.CylinderWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FVTMExporter extends InternalPorter {
	
	private static final String[] extensions = new String[]{ ".java" };
	private static final String tab = "\t";//"    ";
	private static final String tab2 = tab + tab;
	private static final String tab3 = tab2 + tab;
	private boolean extended, onlyvisible;
	private static final ArrayList<Setting> settings = new ArrayList<>();
	static {
		settings.add(new Setting(Type.BOOLEAN, "extended_form", false));
		settings.add(new Setting(Type.BOOLEAN, "export_only_visible", false));
		settings.add(new Setting(Type.STRING, "pack_id", "your-addon-id"));
		settings.add(new Setting(Type.STRING, "model_id", "null"));
		settings.add(new Setting(Type.STRING, "model_type", "part"));
		settings.add(new Setting(Type.STRING, "model_name", "default"));
	}
	
	public FVTMExporter(){}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	/** The in-string "TODO" markers are for those who implement the model into the game. */
	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		extended = settings.get("extended_form").getBooleanValue();
		onlyvisible = settings.get("export_only_visible").getBooleanValue();
		String modelclass, modelkind, packid = settings.get("pack_id").getStringValue();
		String modelname = settings.get("model_name").getStringValue();
		String model_id = settings.get("model_id").getStringValue();
		if(modelname.equals("default") || modelname.equals("null")) modelname = null;
		if(model_id.equals("default") || model_id.equals("null")) model_id = null;
		modelname = validateName(modelname == null ? compound.name + "Model" : modelname);
		switch(settings.get("model_type").getStringValue()){
			case "part":{
				modelclass = "PartModel"; modelkind = "part"; break;
			}
			case "vehicle":{
				modelclass = "VehicleModel"; modelkind = "vehicle"; break;
			}
			case "container":{
				modelclass = "ContainerModel"; modelkind = "container"; break;
			}
			default:{
				modelclass = "InvalidExporterInput"; modelkind = "invalid-exporter-input"; break;
			}
		}
		StringBuffer buffer = new StringBuffer(), shape; int a = 0;
		buffer.append("//FMT-Marker FVTM-1.1\n");
		buffer.append("package net.fexcraft.mod.addon." + packid + ".models." + modelkind + ";\n\n");
		buffer.append("import net.fexcraft.lib.mc.api.registry.fModel;\n" + 
			"import net.fexcraft.lib.tmt.ModelRendererTurbo;\n" + 
			"import net.fexcraft.mod.fvtm.model.TurboList;\n" + 
			"import net.fexcraft.mod.fvtm.model." + modelclass + ";\n\n");
		buffer.append("/** This file was exported via the FVTM Exporter V1.2 of<br>\n");
		buffer.append(" *  FMT (Fex's Modelling Toolbox) v." + FMTB.version + " &copy; " + Year.now().getValue() + " - Fexcraft.net<br>\n");
		buffer.append(" *  All rights reserved. For this Model's License contact the Author/Creator.\n */\n");
		buffer.append("@fModel(registryname = \"" + packid + ":models/" + modelkind + "/"+ (model_id == null ? modelname : model_id) + "\")\n");
		buffer.append("public class " + modelname + " extends " + modelclass + " {\n\n");
		if(this.extended){
			buffer.append("\n");
			for(TurboList list : compound.getCompound().values()){
				if((onlyvisible && !list.visible) || list.isEmpty()) continue;
				buffer.append(tab + "public TurboList " + list.id + ";\n");
			}
			buffer.append("\n");
		}
		buffer.append(tab + "public " + modelname + "(){\n");
		buffer.append(tab2 + "super(); textureX = " + compound.tx(null) + "; textureY = " + compound.ty(null) + ";\n");
		for(String cr : compound.creators){
			buffer.append(tab2 + "this.addToCreators(\"" + cr + "\");\n");//TODO add "uuid" of logged in users if available;
		} buffer.append(tab2 + "//\n");
		for(TurboList list : compound.getCompound().values()){
			if((onlyvisible && !list.visible) || list.isEmpty()) continue;
			String name = list.id;
			buffer.append(tab2 + (this.extended ? "" : "TurboList ") + name + " = new TurboList(\"" + name + "\");\n");
			for(PolygonWrapper wrapper : list){
				shape = new StringBuffer(); boolean extended = false;
				shape.append("new ModelRendererTurbo(" + name + ", " + wrapper.textureX + ", " + wrapper.textureY + ", textureX, textureY)");
				switch(wrapper.getType()){
					case BOX:{
						BoxWrapper box = (BoxWrapper)wrapper;
						shape.append(format(".addBox(%s, %s, %s, %s, %s, %s)", null,
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
							box.size.xCoord, box.size.yCoord, box.size.zCoord));
						break;
					}
					case SHAPEBOX:{
						ShapeboxWrapper box = (ShapeboxWrapper)wrapper;
						shape.append(format("\n" + tab3 + ".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord, box.size.xCoord, box.size.yCoord, box.size.zCoord,
							box.cor0.xCoord, box.cor0.yCoord, box.cor0.zCoord, box.cor1.xCoord, box.cor1.yCoord, box.cor1.zCoord,
							box.cor2.xCoord, box.cor2.yCoord, box.cor2.zCoord, box.cor3.xCoord, box.cor3.yCoord, box.cor3.zCoord,
							box.cor4.xCoord, box.cor4.yCoord, box.cor4.zCoord, box.cor5.xCoord, box.cor5.yCoord, box.cor5.zCoord,
							box.cor6.xCoord, box.cor6.yCoord, box.cor6.zCoord, box.cor7.xCoord, box.cor7.yCoord, box.cor7.zCoord));
						extended = true;
						break;
					}
					case CYLINDER:{
						CylinderWrapper cyl = (CylinderWrapper)wrapper;
						String topoff = cyl.topoff.xCoord != 0f || cyl.topoff.yCoord != 0f || cyl.topoff.zCoord != 0 ?
							String.format("new net.fexcraft.lib.common.math.Vec3f(%s, %s, %s)", cyl.topoff.xCoord, cyl.topoff.yCoord, cyl.topoff.zCoord) : "null";
						if(cyl.radius2 != 0f){
							if(cyl.radial){
								String str = ".setSidesVisible(" + cyl.bools[0] + ", " + cyl.bools[1] + ", " + cyl.bools[2] + ", " + cyl.bools[3] + ")";
								shape.append(format(".newCylinderBuilder()\n" + tab3 + ".setPosition(%s, %s, %s).setRadius(%s, %s).setLength(%s).setSegments(%s, %s)" + 
									".setScale(%s, %s).setDirection(%s)\n" + tab3 + ".setRadialTexture(%s, %s)" + str + ".setTopOffset(%s).build()", topoff, 
									wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
									cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit,
									cyl.base, cyl.top, cyl.direction, cyl.seg_width, cyl.seg_height));
							}
							else{
								if(areAll(cyl.bools, false) && cyl.topangle == 0f){
									shape.append(format(".addHollowCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", topoff, 
										wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
										cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit, cyl.base, cyl.top, cyl.direction));
								}
								else{
									String str = format(".addHollowCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,\n" + tab3 + "%s", topoff, 
										wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
										cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit, cyl.base, cyl.top, cyl.direction);
									shape.append(str + format(", %s", null, cyl.topangle) + format(", %s)", cyl.bools));
								}
							}
						}
						else{
							shape.append(format(".addCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", topoff, 
								wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
								cyl.radius, cyl.length, cyl.segments, cyl.base, cyl.top, cyl.direction));
						}
						break;
					}
					case SPHERE:{}
					case OBJ:{}
					default:{
						shape.append("/* An exporter for the polygon type " + wrapper.getType().name() + " was not made yet. */");
						break;
					}
				}
				if(wrapper.pos.xCoord != 0f || wrapper.pos.yCoord != 0f || wrapper.pos.zCoord != 0f ||
					wrapper.rot.xCoord != 0f || wrapper.rot.yCoord != 0f || wrapper.rot.zCoord != 0f){
					shape.append("\n" + tab3 + format(".setRotationPoint(%s, %s, %s)", null, wrapper.pos.xCoord, wrapper.pos.yCoord, wrapper.pos.zCoord));
					shape.append(format(".setRotationAngle(%s, %s, %s)", null, wrapper.rot.xCoord, wrapper.rot.yCoord, wrapper.rot.zCoord));
					extended = true;
				}
				if(wrapper.mirror || wrapper.flip){
					shape.append("\n" + tab3 + ".setMirrored(" + wrapper.mirror + ").setFlipped(" + wrapper.flip + ")");
					extended = true;
				}
				if(this.extended && (compound.texture != null || !wrapper.visible)){
					shape.append("\n" + tab3 + ".setTextured(" + (compound.texture != null) + ").setLines(" + !wrapper.visible + ")");
					extended = true;
				}
				if(wrapper.name != null){ shape.append(".setName(\"" + wrapper.name + "\")"); }
				if(extended) shape.append("\n" + tab2);
				buffer.append(tab2 + name + ".add(" + shape.toString() + ");\n");
			}
			//if(++a == 1 && !this.extended) buffer.append(tab2 + name + ".addProgram(\"fvtm:example_program\");//TODO do not forget these exists!\n");
			buffer.append(tab2 + "this.groups.add(" + name + ");\n");
			if(a < compound.getCompound().size() - 1) buffer.append(tab2 + "//\n");
		}
		//
		//buffer.append(tab2 + "fixRotations();\n");
		buffer.append(tab + "}\n\n}\n");
		//
		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buffer); writer.flush(); writer.close();
		}
		catch(IOException e){
			e.printStackTrace();
			return "Error:" + e.getMessage();
		}
		return "Success!";
	}
	private String format(String string, String add, double r0, double r1, double r2){
		return format(string, add, new float[]{ (float)r0, (float)r1, (float)r2});
	}

	private String format(String string, String add, float... arr){
		Object[] strs = new Object[arr.length + (add == null ? 0 : 1)];
		for(int i = 0; i < arr.length; i++){ strs[i] = arr[i] % 1.0f != 0 ? String.format("%s", arr[i]) + "f" : String.format("%.0f", arr[i]); }
		if(add != null) strs[arr.length] = add; return String.format(string, strs);
	}

	private String format(String string, boolean[] bools){
		String array = "new boolean[]{ %s }", out = "";
		for(int i = 0; i < bools.length; i++){
			out += bools[i]; if(i < bools.length - 1) out += ", ";
		} return String.format(string, String.format(array, out));
	}

	private String validateName(String name){
		if(name == null || name.length() == 0) return "Unnamed"; /*String[] art = name.split(" ");
		for(int i = 0; i < art.length; i++){
			if(art[i].length() == 0) continue; if(art[i].length() < 2) art[i] = art[i].toUpperCase();
			art[i] = name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		} name = ""; for(String str : art) name += str; return name;*/
		//Making sure it starts uppercase, eventually CamelCase output.
		return name.trim().replace(" ", "_").replaceAll("[^a-zA-Z0-9 _]", "");
	}

	private boolean areAll(boolean[] bools, boolean same){
		for(boolean bool : bools) if(bool != same) return false; return true;
	}


	@Override
	public String getId(){
		return "fvtm_exporter";
	}

	@Override
	public String getName(){
		return "FVTM v3 Scheme";
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

}
