package net.fexcraft.app.fmt.port.ex;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.StructBox;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonHandler.PrintOption;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.Static;
import org.joml.Vector3f;

public class AABBExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("config", "fvtm");

	@Override
	public String id(){
		return "aabb";
	}

	@Override
	public String name(){
		return "FVTM Config - Block AABB";
	}

	@Override
	public FileType extensions(){
		return FileChooser.TYPE_JSON;
	}

	@Override
	public List<String> categories(){
		return categories;
	}

	@Override
	public List<Setting<?>> settings(){
		return Collections.EMPTY_LIST;
	}

	@Override
	public boolean nogroups(){
		return true;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		StringBuffer buffer = new StringBuffer();
		buffer.append("{\n\t\"AABBs\":[\n");
		boolean[] first = { true };
		for(Group group : model.groups()){
			for(Polygon poli : group){
				if(poli instanceof StructBox){
					StructBox box = (StructBox)poli;
					JsonArray array = new JsonArray();
					Vector3f start = box.pos.add(8, box.size.y, 8).mul(Static.sixteenth);
					Vector3f end = box.pos.add(8 + box.size.x, 0, 8 + box.size.z).mul(Static.sixteenth);
					buffer.append("\t\t\"" + poli.name().replace(" ", "_") + "\": " + " [ ");
					buffer.append(start.x + ", ");
					buffer.append((start.y == 0 ? 0 : -start.y) + ", ");
					buffer.append(start.z + ", ");
					buffer.append(end.x + ", ");
					buffer.append((end.y == 0 ? 0 : -end.y) + ", ");
					buffer.append(end.z);
					buffer.append(" ],\n");
				}
			}
		}
		int i = buffer.indexOf(",");
		if(i > 0) buffer.deleteCharAt(i);
		buffer.append("\t]\n}\n");
		try{
			BufferedWriter writer = writer = new BufferedWriter(new FileWriter(file));
			writer.write(buffer.toString());
			writer.flush();
			writer.close();
		}
		catch(IOException e){
			throw new RuntimeException(e);
		}
		return "export.complete";
	}

}
