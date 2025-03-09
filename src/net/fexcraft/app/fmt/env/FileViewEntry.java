package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

import java.io.File;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentLinkedQueue;

import static net.fexcraft.app.fmt.env.PackDevEnv.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileViewEntry extends Component {

	protected static String ICON_LOC = "./resources/textures/icons/filetree/%s.png";
	protected ArrayList<FileViewEntry> sub = new ArrayList<>();
	protected PackDevEnv root;
	protected boolean maximized;
	protected Label label;
	protected File file;
	protected Path path;
	protected Icon icon;

	public FileViewEntry(PackDevEnv env, File fil){
		setSize(fp_width - 10 - fe_offset, fe_height);
		setPosition(0, fe_height);
		root = env;
		file = fil;
		path = fil.toPath().toAbsolutePath();
		add(label = new Label(file.getName(), fe_height + 5, 5, fp_width - 30, 20));
		addIcon();
		if(file.isDirectory()){
			List<File> files = PackDevEnv.getSorted(file.listFiles(), true);
			for(File sub : files){
				addEntry(new FileViewEntry(env, sub));
			}
		}
	}

	private void addEntry(FileViewEntry entry){
		sub.add(entry);
		add(entry);
	}

	private void remEntry(FileViewEntry entry){
		sub.remove(entry);
		remove(entry);
	}

	public FileViewEntry getEntry(String filename){
		for(FileViewEntry entry : sub){
			if(entry.file.getName().equals(filename)) return entry;
		}
		return null;
	}

	protected void addIcon(){
		String loc = String.format(ICON_LOC, file.isDirectory() ? file.listFiles().length == 0 ? "folder_empty" : "folder" : "file");
		add(icon = new Icon(0, fe_height, 0, 0, 0, loc, () -> maximize()));
	}

	public void maximize(){
		maximized = !maximized;
		root.updateFileView();
	}

	public int updateDisplay(int off, int buf){
		setPosition(off, buf);
		int h = fe_height;
		if(maximized){
			for(FileViewEntry entry : sub){
				h += entry.updateDisplay(fe_offset, h);
			}
		}
		setSize(fp_width - 10 - off, h);
		return h;
	}

	public boolean onFileEvent(File file, FileEvent event){
		for(FileViewEntry entry : sub) if(entry.onFileEvent(file, event)) return true;
		return false;
	}

	public void load(JsonMap map){
		maximized = map.getBoolean("maximized", false);
		if(map.has("files")){
			JsonMap files = map.getMap("files");
			for(Map.Entry<String, JsonValue<?>> entry : files.entries()){
				FileViewEntry fe = getEntry(entry.getKey());
				if(fe != null) fe.load(entry.getValue().asMap());
			}
		}
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		if(maximized) map.add("maximized", true);
		JsonMap files = new JsonMap();
		for(FileViewEntry entry : sub){
			JsonMap mep = entry.save();
			if(mep.not_empty()) files.add(entry.file.getName(), mep);
		}
		if(!files.empty()) map.add("files", files);
		return map;
	}

}
