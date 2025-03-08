package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.ui.Icon;

import java.io.File;
import java.nio.file.Path;

import static net.fexcraft.app.fmt.env.PackDevEnv.fe_height;
import static net.fexcraft.app.fmt.env.PackDevEnv.fp_width;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileViewEntry extends Component {

	protected static String ICON_LOC = "./resources/textures/icons/filetree/%s.png";
	protected PackDevEnv root;
	protected Label label;
	protected File file;
	protected Path path;
	protected Icon icon;

	public FileViewEntry(PackDevEnv env, File fil){
		setSize(fp_width - 10, fe_height);
		root = env;
		file = fil;
		path = fil.toPath().toAbsolutePath();
		add(label = new Label(file.getName(), fe_height + 5, 5, fp_width - 30, 20));
		addIcon();
		if(file.isDirectory()) PackDevEnv.addWatch(this);
	}

	protected void addIcon(){
		String loc = String.format(ICON_LOC, file.isDirectory() && file.listFiles().length == 0 ? "folder_empty" : "folder");
		add(icon = new Icon(0, fe_height, 0, 0, 0, loc, () -> {}));
	}

	public int updateDisplay(int buf){
		setPosition(0, buf);
		return fe_height;
	}

}
