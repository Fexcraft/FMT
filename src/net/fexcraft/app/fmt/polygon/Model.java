package net.fexcraft.app.fmt.polygon;

import static net.fexcraft.app.fmt.attributes.UpdateHandler.update;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_AUTHOR;
import static net.fexcraft.app.fmt.attributes.UpdateType.MODEL_LOAD;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.joml.Vector3f;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.MRTRenderer.DrawMode;
import net.fexcraft.app.fmt.utils.SaveHandler;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Model {

	public static long SELECTED_POLYGONS;
	//
	private LinkedHashMap<String, Boolean> authors = new LinkedHashMap<>();
	private ArrayList<Group> groups = new ArrayList<>();
	public Vector3f pos = new Vector3f();
	public Vector3f rot = new Vector3f();
	public TextureGroup texgroup = null;
	public String texhelper;
	public int texSizeX = 256, texSizeY = 256;
	public boolean visible = true, subhelper;
	public float opacity = 1f;
	public Vector3f scale;
	public String name;
	public File file;
	
	public Model(File file, String name){
		this.file = file;
		this.name = name;
	}
	
	/** For now just for FMTB files. */
	public Model load(){
		SaveHandler.open(this, file);
		update(MODEL_LOAD, this);
		return this;
	}
	
	public void addAuthor(String name, boolean locked){
		authors.put(name, locked);
		update(MODEL_AUTHOR, name);
	}

	public long count(boolean selected){
		long am = 0;
		for(Group group : groups){
			if(selected && !group.selected){
				for(Polygon poly : group){
					if(poly.selected) am++;
				}
			}
			else am += group.size();
		}
		return am;
	}
	
	public boolean isHelper(){
		return subhelper || FMT.MODEL != this;
	}

	public void recompile(){
		for(Group group : groups){
			group.recompile();
		}
	}

	public void bindtex(){
		if(texgroup != null) texgroup.texture.bind();
	}

	public void render(){
		if(!visible) return;
		DrawMode mode = DrawMode.textured(texgroup != null);
		for(Group group : groups){
			group.render(mode);
			if(Settings.LINES.value) group.render(DrawMode.LINES);
		}
	}

	public void add(String groupid, Polygon poly){
		Group group = null;
		if(groupid == null){
			if(groups.size() == 0) groups.add(group = new Group(this, "group0"));
			else group = groups.get(Settings.ADD_TO_LAST.value ? groups.size() - 1 : 0);
		}
		else{
			group = get(groupid);
			if(group == null) groups.add(group = new Group(this, groupid));
		}
		group.add(poly);
	}

	public Group get(String string){
		for(Group group : groups) if(group.id.equals(string)) return group;
		return null;
	}

	public boolean contains(String group){
		return get(group) != null;
	}

	public void addGroup(String name){
		groups.add(new Group(this, name));
	}
	
	public ArrayList<Group> groups(){
		return groups;
	}

}
