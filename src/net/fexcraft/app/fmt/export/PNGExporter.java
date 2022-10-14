package net.fexcraft.app.fmt.export;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_PNG;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.StringArraySetting;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;
import net.fexcraft.app.json.JsonMap;

public class PNGExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("texture");
	private static final ArrayList<Setting<?>> settings = new ArrayList<>();
	private Texture image;

	public PNGExporter(JsonMap map){
		settings.add(new Setting<>("textured", false, "exporter-png"));
		settings.add(new StringArraySetting("group", "", "exporter-png"));
	}

	@Override
	public String id(){
		return "png";
	}

	@Override
	public String name(){
		return ".png Textures";
	}

	@Override
	public FileType extensions(){
		return TYPE_PNG;
	}

	@Override
	public List<String> categories(){
		return categories;
	}

	@Override
	public List<Setting<?>> settings(){
		String[] names = TextureManager.getGroupNames();
		settings.get(1).value(names.length == 0 ? "none" : names[0]);
		((StringArraySetting)settings.get(1)).setElms(names.length == 0 ? new String[]{ "none" } : names);
		return settings;
	}

	@Override
	public boolean nogroups(){
		return true;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		image = null;
		boolean textured = settings.get(0).value();
		String grid = settings.get(1).value();
		TextureGroup group = grid.equals("none") ? null : TextureManager.getGroup(grid);
		if(group == null){
			return "exporter.png.group_not_found";
		}
		if(textured){
			image = group.texture;
		}
		else{
			image = new Texture("png_exporter_cache", group.texture.getWidth(), group.texture.getHeight());
			model.groups().forEach(elm -> {
				if(model.texgroup == group || elm.texgroup == group){
					elm.forEach(poly -> poly.paintTex(image, null));
				}
			});
		}
		((StringArraySetting)settings.get(1)).setElms(new String[0]);
		try{
			image.save();
			FileUtils.copyFile(image.getFile(), file);
			return "export.complete";
		}
		catch(java.io.IOException e){
			log(e);
			return "export.errors";
		}
	}

}
