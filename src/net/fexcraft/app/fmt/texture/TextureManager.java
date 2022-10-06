package net.fexcraft.app.fmt.texture;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import org.lwjgl.stb.STBImageWrite;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.ui.GenericDialog;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TextureManager {

	public static int[] RESOLUTIONS = { 8, 16, 32, 64, 128, 256, 512, 1024, 2048, 4096 };
	public static Texture NULLTEX;
	private static final TextureMap TEXTURES = new TextureMap();
	private static final ArrayList<TextureGroup> GROUPS = new ArrayList<>();
	private static Texture texture;

	public static void load(){
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
		texture = NULLTEX = TEXTURES.get("null");
	}

	public static Texture get(String string, boolean allownull){
		Texture texture = TEXTURES.get(string);
		return texture == null ? allownull ? null : TEXTURES.get("null") : texture;
	}

	public static void load(String string, Boolean rooted){
		if(rooted == null) rooted = true;
		File file = new File(rooted ? String.format("./resources/textures/%s.png", string) : string + ".png");
		TEXTURES.put(string, new Texture(string, file));
		log(String.format("Loaded Texture (%-32s) [%s]", string, file));
	}

	public static void loadFromStream(InputStream stream, String string, Boolean rooted, boolean temp){
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
			log(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM ARCHIVE/ZIP>"));
		}
		catch(IOException e){
			log(e);
		}
	}

	public static Texture loadFromFile(File file, String string, Boolean rooted, boolean save){
		if(rooted == null) rooted = true;
		TEXTURES.put(string, new Texture(string, file));
		log(String.format("Loaded Texture (%-32s) [%s]", string, "<FROM FILE>"));
		if(save){
			if(!file.exists()) file.getParentFile().mkdirs();
			TEXTURES.get(string).setFile(file);
			save(string);
		}
		return TEXTURES.get(string);
	}

	public static void loadFromFile(String id, File file){
		String name = id == null ? file.getPath() : id;
		TEXTURES.put(name, new Texture(name, file));
		log(String.format("Loaded Texture (%-32s) [%s]", name, file));
	}

	public static void bind(String string){
		if(string.equals(texture.name)) return;
		(texture = TEXTURES.containsKey(string) ? TEXTURES.get(string) : NULLTEX).bind();
	}

	public static void bind(Texture newtex){
		if(newtex == null || newtex.name.equals(texture.name)) return;
		(texture = newtex).bind();
	}

	public static void bind(TextureGroup group){
		if(group == null){
			bind("blank");
			return;
		}
		if(group.texture.name.equals(texture.name)) return;
		(texture = group.texture).bind();
	}

	public static void unbind(){
		texture = NULLTEX;
	}

	public static String getBound(){
		return texture.name;
	}

	public static Texture remove(String texture){
		Texture tex = TEXTURES.remove(texture);
		log(String.format("Unloaded Texture (%-32s) [%s]", texture, tex.getFile()));
		return tex;
	}

	public static void save(String texture){
		save(TEXTURES.get(texture));
	}

	public static void save(Texture tex){
		if(tex == null){
			log(String.format("Tried to save texture '%s', but it does not exists.", texture));
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
			tex.lastedit = tex.getFile().lastModified();
		}
		catch(Exception e){
			log(e);
		}
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
	
	// GROUPS

	public static TextureGroup getGroup(String id){
		for(TextureGroup group : GROUPS) if(group.name.equals(id)) return group;
		return null;
	}

	public static boolean hasGroup(String id){
		for(TextureGroup group : GROUPS) if(group.name.equals(id)) return true;
		return false;
	}

	public static int getGroupAmount(){
		return GROUPS.size();
	}

	public static boolean anyGroupsLoaded(){
		return GROUPS.size() > 0;
	}

	public static ArrayList<TextureGroup> getGroups(){
		return GROUPS;
	}

	public static void addGroup(TextureGroup group){
		GROUPS.add(group);
		UpdateHandler.update(UpdateType.TEXGROUP_ADDED, group);
	}

	public static void clearGroups(){
		for(TextureGroup group : GROUPS){
			UpdateHandler.update(UpdateType.TEXGROUP_REMOVED, group);
		}
		GROUPS.clear();
	}

	public static TextureGroup addGroup(String name, boolean show){
		if(name == null) name = "newgroup";
		if(hasGroup(name)){
			int i = 0;
			while(hasGroup(name + i)) i++;
			name += i;
		}
		TextureGroup group = new TextureGroup(name, new File("./temp/group-" + name + ".png"));
		addGroup(group);
		if(show) GenericDialog.showOK("texture.manager", null, null, "texture.added_group", "#" + group.name);
		return group;
	}

	public static void remGroup(TextureGroup texgroup){
		if(texgroup == null) return;
		if(texgroup == FMT.MODEL.texgroup){
			GenericDialog.showOK("texture.manager", null, null, "texture.group_in_use_model", "#texgroup:" + texgroup.name);
			return;
		}
		for(Group group : FMT.MODEL.groups()){
			if(group.texgroup == texgroup){
				GenericDialog.showOK("texture.manager", null, null, "texture.group_in_use_group", "#group:" + group.id, "#texgroup:" + texgroup.name);
				return;
			}
		}
		GROUPS.remove(texgroup);
		UpdateHandler.update(UpdateType.TEXGROUP_REMOVED, texgroup);
	}

}