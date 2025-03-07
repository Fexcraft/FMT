package net.fexcraft.app.fmt.env;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmPackEntry extends FileViewEntry {

	public FvtmPackEntry(PackDevEnv env, File file, File assets, File cfg){
		super(env, file);
		JsonMap map = JsonHandler.parse(cfg).asMap();
		label.getTextState().setText(map.getString("Name", label.getTextState().getText()));
	}

}
