package net.fexcraft.app.fmt.porters;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Year;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.PorterManager.InternalPorter;
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
	private final boolean extended, onlyvisible;
	
	public FVTMExporter(boolean bool, boolean bool2){
		this.extended = bool; this.onlyvisible = bool2;
	}

	@Override
	public GroupCompound importModel(File file){
		return null;
	}

	/** The in-string "TODO" makers are for those who implement the model into the game. */
	@Override
	public String exportModel(GroupCompound compound, File file){
		StringBuffer buffer = new StringBuffer(), shape; int a = 0;
		buffer.append("//FMT-Marker FVTM-1\n");
		buffer.append("package net.fexcraft.mod.addons.YOURADDONID.models.SUBPACKAGENAME;\n\n");
		buffer.append("import net.fexcraft.lib.mc.api.registry.fModel;\n" + 
			"import net.fexcraft.lib.tmt.ModelRendererTurbo;\n" + 
			"import net.fexcraft.mod.fvtm.model.TurboList;\n" + 
			"import net.fexcraft.mod.fvtm.model.part.PartModel;//TODO replace this one if needed\n\n");
		buffer.append("/** This file was exported via the FVTM Exporter V1 of<br>\n");
		buffer.append(" *  FMT (Fex's Modelling Toolbox) v." + FMTB.version + " &copy; " + Year.now().getValue() + " - Fexcraft.net<br>\n");
		buffer.append(" *  All rights reserved. For this Model's License contact the Author/Creator.\n */\n");
		buffer.append("@fModel(registryname = \"youraddonid:models/part/"+ (compound.name == null ? "unnamed" : compound.name.toLowerCase()) + "\") //TODO adjust as needed\n");
		buffer.append("public class " + validateName(compound.name) + "Model extends PartModel { //TODO replace into correct super class if needed\n\n");
		if(this.extended){
			buffer.append("\n");
			for(TurboList list : compound.getCompound().values()){
				if((onlyvisible && !list.visible) || list.isEmpty()) continue;
				buffer.append(tab + "public TurboList " + list.id + ";\n");
			}
			buffer.append("\n");
		}
		buffer.append(tab + "public " + validateName(compound.name) + "Model(){\n");
		buffer.append(tab2 + "super(); textureX = " + compound.textureX + "; textureY = " + compound.textureY + ";\n");
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
						shape.append(format(".addBox(%s, %s, %s, %s, %s, %s)",
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
							box.size.xCoord, box.size.yCoord, box.size.zCoord));
						break;
					}
					case SHAPEBOX:{
						ShapeboxWrapper box = (ShapeboxWrapper)wrapper;
						shape.append(format("\n" + tab3 + ".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)",
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
						shape.append(format(".addCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s)", 
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
							cyl.radius, cyl.length, cyl.segments, cyl.base, cyl.top, cyl.direction));
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
					shape.append("\n" + tab3 + format(".setRotationPoint(%s, %s, %s)", wrapper.pos.xCoord, wrapper.pos.yCoord, wrapper.pos.zCoord));
					shape.append(format(".setRotationAngle(%s, %s, %s)", wrapper.rot.xCoord, wrapper.rot.yCoord, wrapper.rot.zCoord));
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
			if(++a == 1 && !this.extended) buffer.append(tab2 + name + ".addProgram(\"fvtm:example_program\");//TODO do not forget these exists!\n");
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

	private String format(String string, double r0, double r1, double r2){
		return format(string, new float[]{ (float)r0, (float)r1, (float)r2});
	}

	private String format(String string, float... arr){
		Object[] strs = new Object[arr.length];
		for(int i = 0; i < strs.length; i++){ strs[i] = arr[i] % 1.0f != 0 ? String.format("%s", arr[i]) + "f" : String.format("%.0f", arr[i]); }
		return String.format(string, strs);
	}

	private String validateName(String name){
		if(name == null || name.length() == 0) return "Unnamed"; name.replace(" ", "");
		return name.substring(0, 1).toUpperCase() + name.substring(1, name.length());
		//Making sure it starts uppercase.
	}

	@Override
	public String getId(){
		return (extended ? "fvtm_exporter_a"/*v1.1"*/ : "fvtm_exporter_b"/*v1.0"*/) + (onlyvisible ? "_onv" : "_xyz");
	}

	@Override
	public String getName(){
		return (extended ? "FVTM v2.9 Extended" : "FVTM v2.9 Scheme") + (onlyvisible ? " [VISIBLE-ONLY]" : " Exporter");
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

}
