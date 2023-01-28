package net.fexcraft.app.fmt.export;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public abstract class FVTM_Format implements Exporter {

	public static final FileType TYPE_FVTM_JAVA = new FileType("FVTM Format Java Model", "*.java");
	protected final ExSetList settings = new ExSetList();
	//
	protected static final String tab = "\t";//"    ";
	protected static final String tab2 = tab + tab;
	protected static final String tab3 = tab2 + tab;
	protected static final String tab4 = tab2 + tab2;
	protected boolean extended, pergroupinit;
	protected String modelname;
	//
	private String name, id;
	
	public FVTM_Format(String name, String id){
		this.name = name;
		this.id = id;
		settings.add(new Setting("extended_form", false, "exporter-fvtm-format"));
		settings.add(new Setting("per_group_init", false, "exporter-fvtm-format"));
		settings.add(new Setting("max_pg_init_count", 250, "exporter-fvtm-format"));
	}

	@Override
	public String id(){
		return id;
	}

	@Override
	public String name(){
		return name;
	}

	@Override
	public FileType extensions(){
		return TYPE_FVTM_JAVA;
	}

	@Override
	public List<Setting<?>> settings(){
		return settings;
	}

	@Override
	public boolean nogroups(){
		return false;
	}

	/** The in-string "TODO" markers are for those who implement the model into the game. */
	@Override
	public String export(Model model, File file, List<Group> groups){
		ArrayList<String> addedgroups = new ArrayList<>();
		init(model, file);
		extended = settings.g("extended_form").bool();
		pergroupinit = settings.g("per_group_init").bool();
		StringBuffer buffer = new StringBuffer();
		buffer.append(top_comment());
		if(settings.g("per_group_init").bool()){
			buffer.append("//Using PER-GROUP-INIT mode with limit '" + settings.g("max_pg_init_count").value + "'!\n");
		}
		buffer.append(package_line());
		append_imports(buffer);
		buffer.append("/** This file was exported via the " + title() + " of<br>\n");
		buffer.append(" *  FMT (Fex's Modelling Toolbox) v." + FMT.VERSION + " &copy; " + Year.now().getValue() + " - fexcraft.net<br>\n");
		buffer.append(" *  All rights reserved. For this Model's License contact the Author/Creator.\n */\n");
		append_declaration(buffer);
		if(extended){
			buffer.append("\n");
			for(Group group : groups){
				if(group.isEmpty()) continue;
				buffer.append(tab + "public ModelGroup " + group.exportId() + ";\n");
			}
			buffer.append("\n");
		}
		buffer.append(tab + "public " + modelname + "(){\n");
		buffer.append(tab2 + "super();\n\t\ttextureX = " + model.texSizeX + ";\n\t\ttextureY = " + model.texSizeY + ";\n");
		for(String cr : model.getAuthors().keySet()){
			buffer.append(tab2 + "addToCreators(\"" + cr + "\");\n");//TODO add "uuid" of logged in users if available;
		}
		buffer.append(tab2 + "//\n");
		if(pergroupinit){
			int count = settings.g("max_pg_init_count").value();
			for(Group group : groups){
				if(group.size() > count){
					int subs = group.size() / count; if(group.size() % count > 0) subs++;
					for(int i = 0; i < subs; i++){
						buffer.append(tab2 + "initGroup_" + group.exportId() + i + "();\n");
					}
				}
				else buffer.append(tab2 + "initGroup_" + group.exportId() + "();\n");
			}
			buffer.append(tab + "}\n\n");
			//
			for(Group group : groups){
				if(group.size() > count){
					int subs = group.size() / count; if(group.size() % count != 0) subs++;
					for(int i = 0; i < subs; i++){
						buffer.append(tab + "private final void initGroup_" + group.exportId() + i + "(){\n");
						int j = i * count, k = (i + 1) * count;
						List<Polygon> sub = group.subList(j, k >= group.size() ? group.size() - 1 : k);
						insert_list(model, sub, group.exportId(), buffer, addedgroups, false);
						buffer.append(tab + "}\n\n");
					}
				}
				else{
					buffer.append(tab + "private final void initGroup_" + group.exportId() + "(){\n");
					insert_list(model, group, null, buffer, addedgroups, false);
					buffer.append(tab + "}\n\n");
				}
			}
			//
			buffer.append("}\n");
		}
		else{
			for(Group group : groups){
				insert_list(model, group, null, buffer, addedgroups, true);
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

	/** Also init model name. */
	protected abstract void init(Model model, File file);

	protected abstract String top_comment();

	protected abstract String package_line();

	protected abstract String title();

	protected abstract String group_class();

	protected abstract void append_imports(StringBuffer buffer);

	protected abstract void append_declaration(StringBuffer buffer);

	private void insert_list(Model model, List<Polygon> list, String id, StringBuffer buffer, ArrayList<String> groups, boolean append){
		String name = id;
		StringBuffer shape = new StringBuffer();
		if(list instanceof Group){
			Group group = (Group)list;
			name = group.exportId();
			if(list.isEmpty()) return;
		}
		boolean contains = groups.contains(name);
		if(!contains) groups.add(name);
		if(contains){
			buffer.append(tab2 + group_class() + " " + name + " = groups.get(\"" + name + "\");\n");
		}
		else{
			buffer.append(tab2 + (this.extended ? "" : group_class() + " ") + name + " = new " + group_class() + "(\"" + name + "\");\n");
		}
		for(Polygon polygon : list){
			shape = new StringBuffer();
			boolean extended = false;
			shape.append("new ModelRendererTurbo(" + name + ", " + polygon.textureX + ", " + polygon.textureY + ", textureX, textureY)");
			boolean boxbuilder = (polygon.getShape().isBox() || polygon.getShape().isShapebox()) && polygon.cuv.any();
			switch(polygon.getShape()){
				case BOX:{
					if(boxbuilder) break;
					Box box = (Box)polygon;
					if(box.anySidesOff()){
						shape.append(format("\n" + tab3 + ".addBox(%s, %s, %s, %s, %s, %s, 0, 1f, ", null,
							polygon.off.x, polygon.off.y, polygon.off.z,
							box.size.x, box.size.y, box.size.z));
						shape.append(String.format("new boolean[]{ %s, %s, %s, %s, %s, %s })", box.sides[0], box.sides[1], box.sides[2], box.sides[3], box.sides[4], box.sides[5]));
						extended = true;
					}
					else{
						shape.append(format(".addBox(%s, %s, %s, %s, %s, %s)", null,
							polygon.off.x, polygon.off.y, polygon.off.z,
							box.size.x, box.size.y, box.size.z));
					}
					break;
				}
				case SHAPEBOX:{
					if(boxbuilder) break;
					Shapebox box = (Shapebox)polygon;
					if(box.anySidesOff()){
						shape.append(format("\n" + tab3 + ".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s,", null,
							polygon.off.x, polygon.off.y, polygon.off.z, box.size.x, box.size.y, box.size.z,
							box.cor0.x, box.cor0.y, box.cor0.z, box.cor1.x, box.cor1.y, box.cor1.z,
							box.cor2.x, box.cor2.y, box.cor2.z, box.cor3.x, box.cor3.y, box.cor3.z,
							box.cor4.x, box.cor4.y, box.cor4.z, box.cor5.x, box.cor5.y, box.cor5.z,
							box.cor6.x, box.cor6.y, box.cor6.z, box.cor7.x, box.cor7.y, box.cor7.z));
						shape.append(String.format("\n" + tab4 + "new boolean[]{ %s, %s, %s, %s, %s, %s })", box.sides[0], box.sides[1], box.sides[2], box.sides[3], box.sides[4], box.sides[5]));
					}
					else{
						shape.append(format("\n" + tab3 + ".addShapeBox(%s, %s, %s, %s, %s, %s, 0, "
							+ "%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
							polygon.off.x, polygon.off.y, polygon.off.z, box.size.x, box.size.y, box.size.z,
							box.cor0.x, box.cor0.y, box.cor0.z, box.cor1.x, box.cor1.y, box.cor1.z,
							box.cor2.x, box.cor2.y, box.cor2.z, box.cor3.x, box.cor3.y, box.cor3.z,
							box.cor4.x, box.cor4.y, box.cor4.z, box.cor5.x, box.cor5.y, box.cor5.z,
							box.cor6.x, box.cor6.y, box.cor6.z, box.cor7.x, box.cor7.y, box.cor7.z));
					}
					extended = true;
					break;
				}
				case CYLINDER:{
					Cylinder cyl = (Cylinder)polygon;
					String topoff = cyl.topoff.x != 0f || cyl.topoff.y != 0f || cyl.topoff.z != 0 ?
						String.format("new net.fexcraft.lib.common.math.Vec3f(%s, %s, %s)", cyl.topoff.x, cyl.topoff.y, cyl.topoff.z) : "null";
					if(cyl.radius2 != 0f || (cyl.seglimit > 0 && cyl.seglimit < cyl.segments) || cyl.radial || cyl.usesTopRotation() || cyl.cuv.any()){
						String toprot = String.format(".setTopRotation(new net.fexcraft.lib.common.math.Vec3f(%s, %s, %s))", cyl.toprot.x, cyl.toprot.y, cyl.toprot.z);
						if(cyl.radial){
							shape.append(format(".newCylinderBuilder()\n" + tab3 + ".setPosition(%s, %s, %s).setRadius(%s, %s).setLength(%s).setSegments(%s, %s)" + 
								".setScale(%s, %s).setDirection(%s)\n" + tab3 + ".setRadialTexture(%s, %s).setTopOffset(%s)" + toprot, topoff, 
								polygon.off.x, polygon.off.y, polygon.off.z,
								cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit,
								cyl.base, cyl.top, cyl.direction, cyl.seg_width, cyl.seg_height));
						}
						else{
							shape.append(format(".newCylinderBuilder()\n" + tab3 + ".setPosition(%s, %s, %s).setRadius(%s, %s).setLength(%s).setSegments(%s, %s)" + 
								".setScale(%s, %s).setDirection(%s)\n" + tab3 + ".setTopOffset(%s)" + toprot, topoff, 
								polygon.off.x, polygon.off.y, polygon.off.z,
								cyl.radius, cyl.radius2, cyl.length, cyl.segments, cyl.seglimit,
								cyl.base, cyl.top, cyl.direction));
						}
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
						append_cuv(cyl, shape);
						shape.append(".build()");
					}
					else{
						shape.append(format(".addCylinder(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", topoff, 
							polygon.off.x, polygon.off.y, polygon.off.z,
							cyl.radius, cyl.length, cyl.segments, cyl.base, cyl.top, cyl.direction));
					}
					break;
				}
				case SPHERE:{}
				case OBJECT:{}
				default:{
					shape.append("/* An exporter for the polygon type " + polygon.getShape().name() + " was not made yet. */");
					break;
				}
			}
			if(boxbuilder){
				Box box = (Box)polygon;
				shape.append(format(".newBoxBuilder()\n" + tab3 + ".setOffset(%s, %s, %s).setSize(%s, %s, %s)", null, 
					box.off.x, box.off.y, box.off.z, box.size.x, box.size.y, box.size.z));
				if(polygon.getShape().isShapebox()){
					Shapebox sbox = (Shapebox)polygon;
					shape.append(format("\n" + tab3 + ".setCorners(%s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s, %s)", null,
						sbox.cor0.x, sbox.cor0.y, sbox.cor0.z, sbox.cor1.x, sbox.cor1.y, sbox.cor1.z,
						sbox.cor2.x, sbox.cor2.y, sbox.cor2.z, sbox.cor3.x, sbox.cor3.y, sbox.cor3.z,
						sbox.cor4.x, sbox.cor4.y, sbox.cor4.z, sbox.cor5.x, sbox.cor5.y, sbox.cor5.z,
						sbox.cor6.x, sbox.cor6.y, sbox.cor6.z, sbox.cor7.x, sbox.cor7.y, sbox.cor7.z));
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
				append_cuv(box, shape);
				shape.append(".build()");
			}
			if(polygon.pos.x != 0f || polygon.pos.y != 0f || polygon.pos.z != 0f ||
				polygon.rot.x != 0f || polygon.rot.y != 0f || polygon.rot.z != 0f){
				shape.append("\n" + tab3 + format(".setRotationPoint(%s, %s, %s)", null, polygon.pos.x, polygon.pos.y, polygon.pos.z));
				shape.append(format(".setRotationAngle(%s, %s, %s)", null, polygon.rot.x, polygon.rot.y, polygon.rot.z));
				extended = true;
			}
			if(polygon.mirror || polygon.flip){
				shape.append("\n" + tab3 + ".setMirrored(" + polygon.mirror + ").setFlipped(" + polygon.flip + ")");
				extended = true;
			}
			if(this.extended && (model.texgroup != null || !polygon.visible)){
				shape.append("\n" + tab3 + ".setTextured(" + (model.texgroup != null) + ").setLines(" + !polygon.visible + ")");
				extended = true;
			}
			if(polygon.name(true) != null){ shape.append(".setName(\"" + polygon.name() + "\")"); }
			if(extended) shape.append("\n" + tab2);
			buffer.append(tab2 + name + ".add(" + shape.toString() + ");\n");
		}
		if(!contains){
			/*if(this instanceof FVTMExporter && list instanceof Group){
				Group group = (Group)list;
				for(Animation anim : group.animations){
					String string = anim.getExportString("fvtm");
					if(string != null && !string.equals("")){
						buffer.append(tab2 + name + ".addProgram(" + string + ");\n");
					}
				}
				if(!group.exoff.isNull()){
					buffer.append(tab2 + name + format(".translate(%s, %s, %s);\n", null, group.exoff.x, group.exoff.y, group.exoff.z));
				}
			}*/
			buffer.append(tab2 + "groups.add(" + name + ");\n");
		}
		if(append) buffer.append(tab2 + "//\n");
	}

	private void append_cuv(Polygon polygon, StringBuffer shape){
		for(UVCoords coord : polygon.cuv.values()){
			if(coord.automatic() || !polygon.isActive(coord.side())) continue;
			String arr = new String();
			for(int i = 0; i < coord.value().length; i++){
				arr += coord.value()[i] + "f, ";
			}
			arr = arr.substring(0, arr.length() - 2);
			shape.append("\n" + tab3 + ".setPolygonUV(" + coord.side().index() + ", new float[]{ " + arr + " })");
		}
		if(polygon.cuv.anyDetached()){
			String det = new String();
			for(UVCoords coord : polygon.cuv.values()){
				if(coord.detached() && polygon.isActive(coord.side())){
					det += coord.side().index() + ", ";
				}
			}
			if(det.contains(",")){
				det = det.substring(0, det.length() - 2);
				shape.append("\n" + tab3 + ".setDetachedUV(" + det + ")");
			}
		}
	}

	private String format(String string, String add, double r0, double r1, double r2){
		return format(string, add, new float[]{ (float)r0, (float)r1, (float)r2});
	}

	private String format(String string, String add, float... arr){
		Object[] strs = new Object[arr.length + (add == null ? 0 : 1)];
		for(int i = 0; i < arr.length; i++){
			strs[i] = arr[i] % 1.0f != 0 ? String.format("%s", arr[i]) + "f" : String.format("%.0f", arr[i]);
		}
		if(add != null) strs[arr.length] = add;
		return String.format(string, strs);
	}

	protected String validate_name(String name){
		if(name == null || name.length() == 0) return "Unnamed";
		return name.trim().replace(" ", "_").replaceAll("[^a-zA-Z0-9 _]", "");
	}

}
