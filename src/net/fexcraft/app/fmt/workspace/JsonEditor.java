package net.fexcraft.app.fmt.workspace;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class JsonEditor extends WFileEditor {

	public JsonEditor(File file){
		super(file);
	}

	@Override
	protected String get_editor_name(){
		return "Json Editor";
	}

}
