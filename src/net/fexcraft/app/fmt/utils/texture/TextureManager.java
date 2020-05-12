package net.fexcraft.app.fmt.utils.texture;

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
import net.fexcraft.lib.common.utils.Print;

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
				System.out.println(String.format("Loaded Root Texture (%-6s) [%s]", name, file));
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
		System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, file));
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
			System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM IMPORTED MTB/ZIP>"));
		}
		catch(IOException e){
			e.printStackTrace();
		}
	}

	public static Texture loadTextureFromFile(File file, String string, Boolean rooted, boolean save){
		if(rooted == null) rooted = true;
		TEXTURES.put(string, new Texture(string, file));
		System.out.println(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM FILE>"));
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
		System.out.println(String.format("Loaded Texture (%-32s) [%s]", name, file));
	}

	/** Usually expects in form of "temp/NAME" */
	public static void newBlankTexture(String name, TurboList list){
		Texture tex = new Texture(name, FMTB.MODEL.tx(list), FMTB.MODEL.ty(list));
		TEXTURES.put(name, tex);
		tex.setFile(new File("./" + name + (list == null ? "" : "_" + list.id) + ".png"));
		TextureManager.saveTexture(name);
		System.out.println(String.format("Loaded Texture (%-32s) [%s]", name, tex.getFile()));
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
		return TEXTURES.remove(texture);
	}

	public static void saveTexture(String texture){
		saveTexture(TEXTURES.get(texture));
	}

	public static void saveTexture(Texture tex){
		if(tex == null){
			Print.console(String.format("Tried to save texture '%s', but it is not loaded as it seems.", texture));
			return;
		}
		if(tex.getFile() == null){
			Print.console(String.format("Tried to save texture '%s', but it has no file linked.", texture));
			return;
		}
		try{
			if(!tex.getFile().getParentFile().exists()){
				tex.getFile().getParentFile().mkdirs();
			}
			Print.console("Saving Texture (" + tex + ")!");
			STBImageWrite.stbi_write_png(tex.getFile().getPath(), tex.getWidth(), tex.getHeight(), Texture.CHANNELS, tex.getImage(), 0);
			TextureUpdate.updateLastEdit(tex);
		}
		catch(Exception e){
			e.printStackTrace();
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

	public static void addNewGroup(){
		String name = "newgroup";
		while(hasGroup(name)) name += "0";
		addGroup(new TextureGroup(name, new File("./temp/group-" + name + ".png")));
		DialogBox.showOK(null, null, null, "tree.textures.group_added", "#" + name);
		Trees.textures.reOrderGroups();
		GroupEditor.updateTextureGroups();
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

	public static Texture createTexture(String texid, int texX, int texY, byte[] color){
		Texture tex = new Texture(texid, texY, texY, color);
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