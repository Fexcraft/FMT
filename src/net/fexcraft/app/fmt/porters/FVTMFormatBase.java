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
import net.fexcraft.app.fmt.utils.Animator.Animation;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.wrappers.*;
import net.fexcraft.app.fmt.wrappers.face.UVCoords;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public abstract class FVTMFormatBase extends ExImPorter {
	
	protected static final String[] extensions = new String[]{ "FVTM Format Java Model", "*.java" };
	protected static final String tab = "\t";//"    ";
	protected static final String tab2 = tab + tab;
	protected static final String tab3 = tab2 + tab;
	protected static final String tab4 = tab2 + tab2;
	protected boolean extended, onlyvisible, onlyselected, pergroupinit;
	protected String modelname;
	protected ArrayList<Setting> settings = new ArrayList<>();
	//
	private String name, id;
	
	public FVTMFormatBase(String name, String id){
		this.name = name; this.id = id;
		settings.add(new Setting(Type.BOOLEAN, "extended_form", false));
		settings.add(new Setting(Type.BOOLEAN, "export_only_visible", false));
		settings.add(new Setting(Type.BOOLEAN, "export_only_selected", false));
		settings.add(new Setting(Type.BOOLEAN, "per_group_init", false));
		settings.add(new Setting(Type.INTEGER, "max_pg_init_count", 250));
	}

	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
		return null;
	}

	/** The in-string "TODO" markers are for those who implement the model into the game. */
	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		ArrayList<String> addedgroups = new ArrayList<>(); this.initExport(compound, file, settings);
		extended = settings.get("extended_form").getBooleanValue();
		onlyvisible = settings.get("export_only_visible").getBooleanValue();
		onlyselected = settings.get("export_only_selected").getBooleanValue();
		pergroupinit = settings.get("per_group_init").getBooleanValue();
		StringBuffer buffer = new StringBuffer();
		buffer.append(getTopCommentLine());
		if(settings.get("per_group_init").getBooleanValue()) buffer.append("//Using PER-GROUP-INIT mode with limit '" + settings.get("max_pg_init_count").getValue() + "'!\n");
		buffer.append(getPackageLine());
		appendImports(buffer);
		buffer.append("/** This file was exported via the " + getTitle() + " of<br>\n");
		buffer.append(" *  FMT (Fex's Modelling Toolbox) v." + FMTB.VERSION + " &copy; " + Year.now().getValue() + " - Fexcraft.net<br>\n");
		buffer.append(" *  All rights reserved. For this Model's License contact the Author/Creator.\n */\n");
		appendClassDeclaration(buffer);
		if(this.extended){
			buffer.append("\n");
			for(TurboList list : compound.getGroups()){
				if((onlyvisible && !list.visible) || (onlyselected && !list.selected) || list.isEmpty()) continue;
				buffer.append(tab + "public TurboList " + list.exportID() + ";\n");
			}
			buffer.append("\n");
		}
		buffer.append(tab + "public " + modelname + "(){\n");
		buffer.append(tab2 + "super(); " + getScale() + "textureX = " + compound.tx(null) + "; textureY = " + compound.ty(null) + ";\n");
		for(String cr : compound.getAuthors()){
			buffer.append(tab2 + "this.addToCreators(\"" + cr + "\");\n");//TODO add "uuid" of logged in users if available;
		} buffer.append(tab2 + "//\n");
		if(pergroupinit){
			int count = settings.get("max_pg_init_count").getValue();
			for(TurboList list : compound.getGroups()){
				if(list.size() > count){
					int subs = list.size() / count; if(list.size() % count > 0) subs++;
					for(int i = 0; i < subs; i++){
						buffer.append(tab2 + "initGroup_" + list.exportID() + i + "();\n");
					}
				}
				else buffer.append(tab2 + "initGroup_" + list.exportID() + "();\n");
			}
			buffer.append(tab + "}\n\n");
			//
			for(TurboList list : compound.getGroups()){
				if(list.size() > count){
					int subs = list.size() / count; if(list.size() % count != 0) subs++;
					for(int i = 0; i < subs; i++){
						buffer.append(tab + "private final void initGroup_" + list.exportID() + i + "(){\n");
						int j = i * count, k = (i + 1) * count;
						List<PolygonWrapper> sub = list.subList(j, k >= list.size() ? list.size() - 1 : k);
						insertList(compound, sub, list.exportID(), buffer, addedgroups, false);
						buffer.append(tab + "}\n\n");
					}
				}
				else{
					buffer.append(tab + "private final void initGroup_" + list.exportID() + "(){\n");
					insertList(compound, list, null, buffer, addedgroups, false);
					buffer.append(tab + "}\n\n");
				}
			}
			//
			buffer.append("}\n");
		}
		else{
			for(TurboList list : compound.getGroups()){
				insertList(compound, list, null, buffer, addedgroups, true);
			}
			buffer.append(tab + "}\n\n}\n");
		}
		//buffer.append(tab2 + "fixRotations();\n");
		//
		try{
			BufferedWriter writer = new BufferedWriter(new FileWriter(file));
			writer.append(buffer); writer.flush(); writer.close();
		}
		catch(IOException e){
			log(e);
			return "Error:" + e.getMessage();
		}
		return "Success!";
	}

	protected abstract String getScale();

	protected abstract String getTopCommentLine();

	protected abstract String getPackageLine();

	/** Also init model name. */
	protected abstract void initExport(GroupCompound compound, File file, Map<String, Setting> settings);

	protected abstract void appendImports(StringBuffer buffer);
	
	protected abstract String getTitle();

	protected abstract void appendClassDeclaration(StringBuffer buffer);

	private void insertList(GroupCompound compound, List<PolygonWrapper> list, String id, StringBuffer buffer, ArrayList<String> groups, boolean append){
		String name = id; StringBuffer shape = new StringBuffer();
		if(list instanceof TurboList){
			TurboList turbo = (TurboList)list; name = turbo.exportID();
			if((onlyvisible && !turbo.visible) || (onlyselected && !turbo.selected) || list.isEmpty()) return;
		}
		boolean contains = groups.contains(name); if(!contains) groups.add(name);
		if(contains){
			buffer.append(tab2 + "TurboList " + name + " = groups.get(\"" + name + "\");\n");
		}
		else{
			buffer.append(tab2 + (this.extended ? "" : "TurboList ") + name + " = new TurboList(\"" + name + "\");\n");
		}
		for(PolygonWrapper wrapper : list){
			shape = new StringBuffer(); boolean extended = false;
			shape.append("new ModelRendererTurbo(" + name + ", " + wrapper.textureX + ", " + wrapper.textureY + ", textureX, textureY)");
			boolean boxbuilder = (wrapper.getType() == ShapeType.BOX || wrapper.getType() == ShapeType.SHAPEBOX) && wrapper.cuv.anyCustom();
			switch(wrapper.getType()){
				case BOX:{
					if(boxbuilder) break;
					BoxWrapper box = (BoxWrapper)wrapper;
					if(box.anySidesOff()){
						shape.append(format("\n" + tab3 + ".addBox(%s, %s, %s, %s, %s, %s, 0, 1f, ", null,
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
							box.size.xCoord, box.size.yCoord, box.size.zCoord));
						shape.append(String.format("new boolean[]{ %s, %s, %s, %s, %s, %s })", box.sides[0], box.sides[1], box.sides[2], box.sides[3], box.sides[4], box.sides[5]));
						extended = true;
					}
					else{
						shape.append(format(".addBox(%s, %s, %s, %s, %s, %s)", null,
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
							box.size.xCoord, box.size.yCoord, box.size.zCoord));
					}
					break;
				}
				case QUAD:{
					QuadWrapper quad = (QuadWrapper)wrapper;
					shape.append(format(".addQuad(%s, %s, %s, %s, %s)", null,
						wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
						quad.size.xCoord, quad.size.yCoord));
					break;
				}
				case SHAPEBOX:{
					if(boxbuilder) break;
					ShapeboxWrapper box = (ShapeboxWrapper)wrapper;
					if(box.anySidesOff()){
						shape.append(format("\n" + tab3 + ".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,", null,
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord, box.size.xCoord, box.size.yCoord, box.size.zCoord,
							box.cor0.xCoord, box.cor0.yCoord, box.cor0.zCoord, box.cor1.xCoord, box.cor1.yCoord, box.cor1.zCoord,
							box.cor2.xCoord, box.cor2.yCoord, box.cor2.zCoord, box.cor3.xCoord, box.cor3.yCoord, box.cor3.zCoord,
							box.cor4.xCoord, box.cor4.yCoord, box.cor4.zCoord, box.cor5.xCoord, box.cor5.yCoord, box.cor5.zCoord,
							box.cor6.xCoord, box.cor6.yCoord, box.cor6.zCoord, box.cor7.xCoord, box.cor7.yCoord, box.cor7.zCoord));
						shape.append(String.format("\n" + tab4 + "new boolean[]{ %s, %s, %s, %s, %s, %s })", box.sides[0], box.sides[1], box.sides[2], box.sides[3], box.sides[4], box.sides[5]));
					}
					else{
						shape.append(format("\n" + tab3 + ".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord, box.size.xCoord, box.size.yCoord, box.size.zCoord,
							box.cor0.xCoord, box.cor0.yCoord, box.cor0.zCoord, box.cor1.xCoord, box.cor1.yCoord, box.cor1.zCoord,
							box.cor2.xCoord, box.cor2.yCoord, box.cor2.zCoord, box.cor3.xCoord, box.cor3.yCoord, box.cor3.zCoord,
							box.cor4.xCoord, box.cor4.yCoord, box.cor4.zCoord, box.cor5.xCoord, box.cor5.yCoord, box.cor5.zCoord,
							box.cor6.xCoord, box.cor6.yCoord, box.cor6.zCoord, box.cor7.xCoord, box.cor7.yCoord, box.cor7.zCoord));
					}
					extended = true;
					break;
				}
				case SHAPEQUAD:{
					ShapeQuadWrapper box = (ShapeQuadWrapper)wrapper;
					shape.append(format("\n" + tab3 + ".addShapeQuad(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
						wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord, box.size.xCoord, box.size.yCoord, box.size.zCoord,
						box.cor0.xCoord, box.cor0.yCoord, box.cor0.zCoord, box.cor1.xCoord, box.cor1.yCoord, box.cor1.zCoord,
						box.cor2.xCoord, box.cor2.yCoord, box.cor2.zCoord, box.cor3.xCoord, box.cor3.yCoord, box.cor3.zCoord));
					extended = true;
					break;
				}
				case CYLINDER:{
					CylinderWrapper cyl = (CylinderWrapper)wrapper;
					String topoff = cyl.topoff.xCoord != 0f || cyl.topoff.yCoord != 0f || cyl.topoff.zCoord != 0 ?
						String.format("new net.fexcraft.lib.common.math.Vec3f(%s, %s, %s)", cyl.topoff.xCoord, cyl.topoff.yCoord, cyl.topoff.zCoord) : "null";
					if(cyl.radius2 != 0f || cyl.radial || cyl.usesTopRotation() || cyl.cuv.anyCustom()){
						String toprot = String.format(".setTopRotation(new net.fexcraft.lib.common.math.Vec3f(%s, %s, %s))", cyl.toprot.xCoord, cyl.toprot.yCoord, cyl.toprot.zCoord);
						shape.append(format(".newCylinderBuilder()\n" + tab3 + ".setPosition(%s, %s, %s).setRadius(%s, %s).setLength(%s).setSegments(%s, %s)" + 
							".setScale(%s, %s).setDirection(%s)\n" + tab3 + ".setRadialTexture(%s, %s).setTopOffset(%s)" + toprot, topoff, 
							wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
							cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit,
							cyl.base, cyl.top, cyl.direction, cyl.seg_width, cyl.seg_height));
						if(cyl.anySidesOff()){
							String off = new String();
							for(int i = 0; i < 6; i++){
								if(cyl.bools[i]){
									off += i + ", ";
								}
							}
							off = off.substring(0, off.length() - 2);
							shape.append("\n" + tab3 + ".removePolygons(" + off + ")");
						}
						appendCustomUV(cyl, shape);
						shape.append(".build()");
					}
					/*else if(cyl.radius2 != 0f){
						if(areAll(cyl.bools, false)){
							shape.append(format(".addHollowCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", topoff, 
								wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
								cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit, cyl.base, cyl.top, cyl.direction));
						}
						else{
							String str = format(".addHollowCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,\n" + tab3 + "%s", topoff, 
								wrapper.off.xCoord, wrapper.off.yCoord, wrapper.off.zCoord,
								cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit, cyl.base, cyl.top, cyl.direction);
							shape.append(str + format(", %s)", cyl.bools));
						}
					}*/
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
			if(boxbuilder){
				BoxWrapper box = (BoxWrapper)wrapper;
				shape.append(format(".newBoxBuilder()\n" + tab3 + ".setOffset(%s, %s, %s).setSize(%s, %s, %s)", null, 
					box.off.xCoord, box.off.yCoord, box.off.zCoord, box.size.xCoord, box.size.yCoord, box.size.zCoord));
				if(wrapper.getType().isShapebox()){
					ShapeboxWrapper sbox = (ShapeboxWrapper)wrapper;
					shape.append(format("\n" + tab3 + ".setCorners(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
						sbox.cor0.xCoord, sbox.cor0.yCoord, sbox.cor0.zCoord, sbox.cor1.xCoord, sbox.cor1.yCoord, sbox.cor1.zCoord,
						sbox.cor2.xCoord, sbox.cor2.yCoord, sbox.cor2.zCoord, sbox.cor3.xCoord, sbox.cor3.yCoord, sbox.cor3.zCoord,
						sbox.cor4.xCoord, sbox.cor4.yCoord, sbox.cor4.zCoord, sbox.cor5.xCoord, sbox.cor5.yCoord, sbox.cor5.zCoord,
						sbox.cor6.xCoord, sbox.cor6.yCoord, sbox.cor6.zCoord, sbox.cor7.xCoord, sbox.cor7.yCoord, sbox.cor7.zCoord));
				}
				if(box.anySidesOff()){
					String off = new String();
					for(int i = 0; i < 6; i++){
						if(box.sides[i]){
							off += i + ", ";
						}
					}
					off = off.substring(0, off.length() - 2);
					shape.append("\n" + tab3 + ".removePolygons(" + off + ")");
				}
				appendCustomUV(box, shape);
				shape.append(".build()");
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
			if(this.extended && (compound.texgroup != null || !wrapper.visible)){
				shape.append("\n" + tab3 + ".setTextured(" + (compound.texgroup != null) + ").setLines(" + !wrapper.visible + ")");
				extended = true;
			}
			if(wrapper.name != null){ shape.append(".setName(\"" + wrapper.name + "\")"); }
			if(extended) shape.append("\n" + tab2);
			buffer.append(tab2 + name + ".add(" + shape.toString() + ");\n");
		}
		if(!contains){
			if(this instanceof FVTMExporter && list instanceof TurboList){
				TurboList turbo = (TurboList)list;
				for(Animation anim : turbo.animations){
					String string = anim.getExportString("fvtm");
					if(string != null && !string.equals("")){
						buffer.append(tab2 + name + ".addProgram(" + string + ");\n");
					}
				}
				if(turbo.exportoffset != null){
					buffer.append(tab2 + name + format(".translate(%s, %s, %s);\n", null, turbo.exportoffset.xCoord, turbo.exportoffset.yCoord, turbo.exportoffset.zCoord));
				}
			}
			buffer.append(tab2 + "this.groups.add(" + name + ");\n");
		}
		if(append) buffer.append(tab2 + "//\n");
	}

	private void appendCustomUV(PolygonWrapper wrapper, StringBuffer shape){
		for(UVCoords coord : wrapper.cuv.values()){
			if(coord.automatic() || !wrapper.isFaceActive(coord.side())) continue;
			String arr = new String();
			for(int i = 0; i < coord.value().length; i++){
				arr += coord.value()[i] + "f, ";
			}
			arr = arr.substring(0, arr.length() - 2);
			shape.append("\n" + tab3 + ".setPolygonUV(" + coord.side().index() + ", new float[]{ " + arr + " })");
		}
		if(wrapper.anyFaceUVAbsolute()){
			String det = new String();
			for(UVCoords coord : wrapper.cuv.values()){
				if(coord.absolute() && wrapper.isFaceActive(coord.side())){
					det += coord.side().index() + ", ";
				}
			}
			det = det.substring(0, det.length() - 2);
			shape.append("\n" + tab3 + ".setDetachedUV(" + det + ")");
		}
	}

	private String format(String string, String add, double r0, double r1, double r2){
		return format(string, add, new float[]{ (float)r0, (float)r1, (float)r2});
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
		return id;
	}

	@Override
	public String getName(){
		return name;
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
