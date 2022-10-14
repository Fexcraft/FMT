package net.fexcraft.app.fmt.export;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser.FileType;

public class FMFExporter implements Exporter {
	
	private static final List<String> categories = Arrays.asList("model");
	public static FileType TYPE_FMF = new FileType("Fex's Model Format", "*.fmf");

	@Override
	public String id(){
		return "fmf";
	}

	@Override
	public String name(){
		return "FMF (Fex's Model Format)";
	}

	@Override
	public FileType extensions(){
		return TYPE_FMF;
	}

	@Override
	public List<String> categories(){
		return categories;
	}

	@Override
	public List<Setting<?>> settings(){
		return Arrays.asList(new Setting<>("test", "value", null), new Setting<>("other", "value", null));
	}

	@Override
	public boolean nogroups(){
		return false;
	}

	@Override
	public String export(Model model, File file, List<Group> groups){
		return "//TODO";
	}

}
