package net.fexcraft.app.fmt.utils.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.lwjgl.stb.STBImageWrite;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.editor.GroupEditor;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.wrappers.TurboList;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureManager {

	private static final Map<String, Texture> TEXTURES = new HashMap<>();
	private static final ArrayList<TextureGroup> GROUPS = new ArrayList<>();
	private static Texture texture, nulltex;

	public static void init(){
		TEXTURES.clear();
		GROUPS.clear();
		String name;
		File folder = new File("./resources/textures/");
		for(File file : folder.listFiles()){
			if(file.isDirectory()) continue;
			if((name = file.getName()).toLowerCase().endsWith(".png")){
				TEXTURES.put(name = name.replace(".png", ""), new Texture(name, file));
				log(String.format("Loaded Root Texture (%-6s) [%s]", name, file));
			}
			else continue;
		}
		texture = nulltex = TEXTURES.get("null");
	}

	public static Texture getTexture(String string, boolean allownull){
		Texture texture = TEXTURES.get(string);
		return texture == null ? allownull ? null : TEXTURES.get("null") : texture;
	}

	public static void loadTexture(String string, Boolean rooted){
		if(rooted == null) rooted = true;
		File file = new File(rooted ? String.format("./resources/textures/%s.png", string) : string + ".png");
		TEXTURES.put(string, new Texture(string, file));
		log(String.format("Loaded Texture (%-32s) [%s]", string, file));
	}

	public static void loadTextureFromZip(InputStream stream, String string, Boolean rooted, boolean temp){
		try{
			if(rooted == null) rooted = true;
			File file = new File(rooted ? String.format("./resources/textures/%s.png", string) : (temp ? "./temp/" : "") + string + ".png");
			if(!file.getParentFile().exists()) file.getParentFile().mkdirs();
		    OutputStream out = new FileOutputStream(file);
		    while(stream.available() > 0){
		    	out.write(stream.read());
		    }
		    out.flush();
		    out.close();
			TEXTURES.put(string, new Texture(string, file));
			log(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM IMPORTED MTB/ZIP>"));
		}
		catch(IOException e){
			log(e);
		}
	}

	public static Texture loadTextureFromFile(File file, String string, Boolean rooted, boolean save){
		if(rooted == null) rooted = true;
		TEXTURES.put(string, new Texture(string, file));
		log(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM FILE>"));
		if(save){
			if(!file.exists()) file.getParentFile().mkdirs();
			TEXTURES.get(string).setFile(file);
			saveTexture(string);
		}
		return TEXTURES.get(string);
	}

	public static void loadTextureFromFile(String id, File file){
		String name = id == null ? file.getPath() : id;
		TEXTURES.put(name, new Texture(name, file));
		log(String.format("Loaded Texture (%-32s) [%s]", name, file));
	}

	public static void bindTexture(String string){
		if(string.equals(texture.name)) return;
		(texture = TEXTURES.containsKey(string) ? TEXTURES.get(string) : nulltex).bind();
	}

	public static void bindTexture(Texture newtex){
		if(newtex == null || newtex.name.equals(texture.name)) return;
		(texture = newtex).bind();
	}

	public static void bindTexture(TextureGroup group){
		if(group == null){
			bindTexture("blank");
			return;
		}
		if(group.texture.name.equals(texture.name)) return;
		(texture = group.texture).bind();
	}

	public static void unbind(){
		texture = nulltex;
	}

	public static String getBoundTexture(){
		return texture.name;
	}

	public static Texture removeTexture(String texture){
		Texture tex = TEXTURES.remove(texture);
		log(String.format("Unloaded Texture (%-32s) [%s]", texture, tex.getFile()));
		return tex;
	}

	public static void saveTexture(String texture){
		saveTexture(TEXTURES.get(texture));
	}

	public static void saveTexture(Texture tex){
		if(tex == null){
			log(String.format("Tried to save texture '%s', but it is not loaded as it seems.", texture));
			return;
		}
		if(tex.getFile() == null){
			log(String.format("Tried to save texture '%s', but it has no file linked.", texture));
			return;
		}
		if(tex.getImage() == null){
			log(String.format("Tried to save texture '%s', but the image buffer is NULL.", texture));
			return;
		}
		try{
			if(!tex.getFile().getParentFile().exists()){
				tex.getFile().getParentFile().mkdirs();
			}
			log("Saving Texture (" + tex + ")!");
			STBImageWrite.stbi_write_png(tex.getFile().getPath(), tex.getWidth(), tex.getHeight(), Texture.CHANNELS, tex.getImage(), 0);
			TextureUpdate.updateLastEdit(tex);
		}
		catch(Exception e){
			log(e);
		}
	}

	public static Texture getNullTex(){
		return nulltex;
	}

	public static TextureGroup getGroup(String id){
		for(TextureGroup group : GROUPS) if(group.group.equals(id)) return group;
		return null;
	}

	public static boolean hasGroup(String id){
		for(TextureGroup group : GROUPS) if(group.group.equals(id)) return true;
		return false;
	}

	public static int getGroupAmount(){
		return GROUPS.size();
	}

	public static boolean anyGroupsLoaded(){
		return GROUPS.isEmpty();
	}

	public static ArrayList<TextureGroup> getGroupsFE(){
		return GROUPS;
	}

	public static void addGroup(TextureGroup group){
		Trees.textures.addSub(group.button);
		GROUPS.add(group);
		GroupEditor.updateTextureGroups();
	}

	public static void clearGroups(){
		for(TextureGroup group : GROUPS){
			group.button.removeFromTree();
		}
		GROUPS.clear();
	}

	public static TextureGroup addNewGroup(String name, boolean show){
		if(name == null) name = "newgroup";
		while(hasGroup(name)) name += "0";
		TextureGroup group = new TextureGroup(name, new File("./temp/group-" + name + ".png"));
		addGroup(group);
		if(show) DialogBox.showOK(null, null, null, "tree.textures.group_added", "#" + name);
		Trees.textures.reOrderGroups();
		GroupEditor.updateTextureGroups();
		return group;
	}

	public static void removeGroup(TextureGroup texgroup){
		if(texgroup == null) return;
		if(texgroup == FMTB.MODEL.texgroup){
			DialogBox.showOK(null, null, null, "tree.textures.remove_default");
		}
		for(TurboList list : FMTB.MODEL.getGroups()){
			if(list.texgroup == texgroup){
				DialogBox.showOK(null, null, null, "tree.textures.remove_in_use", "#" + list.id);
				return;
			}
		}
		texgroup.button.removeFromTree();
		GROUPS.remove(texgroup);
		GroupEditor.updateTextureGroups();
	}

	public static Texture createTexture(String texid, int texX, int texY){
		Texture tex = new Texture(texid, texX, texY);
		TEXTURES.put(texid, tex);
		return tex;
	}

	public static Texture putTexture(String texid, Texture texture){
		return TEXTURES.put(texid, texture);
	}

	public static boolean containsTexture(String texid){
		return TEXTURES.containsKey(texid);
	}

}