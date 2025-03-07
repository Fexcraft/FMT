package net.fexcraft.app.fmt.env;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Label;

import java.io.File;
import java.nio.file.Path;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FileViewEntry extends Component {

	protected PackDevEnv root;
	protected Label label;
	protected File file;
	protected Path path;

	public FileViewEntry(PackDevEnv env, File fil){
		setSize(PackDevEnv.fp_width - 10, PackDevEnv.fe_height);
		root = env;
		file = fil;
		path = fil.toPath();
		add(label = new Label(file.getName(), 30, 5,PackDevEnv.fp_width - 30, 20));
	}

	public int updateDisplay(int buf){
		setPosition(0, buf);
		return PackDevEnv.fe_height;
	}

}
