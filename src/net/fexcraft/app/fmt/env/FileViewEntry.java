package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.event.MouseClickEvent;
import net.fexcraft.app.fmt.ui.Icon;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.nio.file.Path;
import java.util.*;

import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static com.spinyowl.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;
import static net.fexcraft.app.fmt.env.PackDevEnv.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileViewEntry extends Component {

	public static String ICON_LOC = "./resources/textures/icons/filetree/%s.png";
	protected ArrayList<FileViewEntry> sub = new ArrayList<>();
	protected FileViewEntry parent;
	protected PackDevEnv root;
	protected boolean maximized;
	protected Label label;
	protected File file;
	protected Path path;
	protected Icon icon;

	public FileViewEntry(PackDevEnv env, File fil){
		setSize(fp_inner, fe_height);
		setPosition(0, fe_height);
		root = env;
		file = fil;
		add(label = new Label(file.getName(), fe_height + 5, 5, fp_width - 30, 20));
		label.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() == CLICK){
				if(event.getButton() == MOUSE_BUTTON_LEFT){
					if(file.isDirectory()) return;
					root.addTab(this);
				}
			}
		});
		addIcon();
		if(file.isDirectory()){
			List<File> files = PackDevEnv.getSorted(file.listFiles(), true);
			for(File sub : files){
				addEntry(new FileViewEntry(env, sub).set(this));
			}
		}
	}

	private FileViewEntry set(FileViewEntry par){
		parent = par;
		return this;
	}

	private void addEntry(FileViewEntry entry){
		sub.add(entry);
		add(entry);
	}

	private void remEntry(FileViewEntry entry){
		sub.remove(entry);
		remove(entry);
		if(!file.exists() && parent != null) parent.remEntry(this);
	}

	public FileViewEntry getEntry(String filename){
		for(FileViewEntry entry : sub){
			if(entry.file.getName().equals(filename)) return entry;
		}
		return null;
	}

	protected void addIcon(){
		add(icon = new Icon(0, fe_height, 0, 0, 0, getIconLoc(), () -> maximize()));
	}

	private Object fromSuffix(){
		String fix = FilenameUtils.getExtension(file.getName());
		switch(fix){
			case "fmtb": return "file_fmtb";
			case "fvtm": return "file_fvtmaddonpack";
			case "json": return "file_json";
			case "lang": return "file_lang";
			case "vehicle":
			case "part":
			case "material":
			case "block":
			case "multiblock":
			case "consumable":
			case "fuel":
			case "wire":
			case "wiredeco":
			case "deco":
			case "cloth":
			case "gauge":
			case "sign":
			case "container": return "file_fvtmcfg";
			default: return "file";
		}
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
		setSize(500, h);
		return h;
	}

	public boolean onFileEvent(File file, FileEvent event){
		if(event.change() && file.equals(this.file)){
			ArrayList<FileViewEntry> rems = new ArrayList<>();
			for(FileViewEntry entry : sub){
				if(!entry.file.exists()) rems.add(entry);
				Logging.log(entry.file.exists() + " " + entry.file);
			}
			for(FileViewEntry rem : rems){
				remEntry(rem);
			}
			return true;
		}
		if(file.getParentFile().equals(this.file)){
			switch(event){
				case CREATE: {
					FileViewEntry entry = getEntry(file.getName());
					if(entry != null) return true;
					addEntry(new FileViewEntry(root, file).set(this));
					return true;
				}
				case DELETE: {
					FileViewEntry entry = getEntry(file.getName());
					remEntry(entry);
					return true;
				}
			}
		}
		for(FileViewEntry entry : sub){
			if(!entry.file.isDirectory()) continue;
			if(entry.onFileEvent(file, event)) return true;
		}
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

	public String getIconLoc(){
		return String.format(ICON_LOC, file.isDirectory() ? file.listFiles().length == 0 ? "folder_empty" : "folder" : fromSuffix());
	}

}
