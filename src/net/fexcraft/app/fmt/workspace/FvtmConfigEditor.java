package net.fexcraft.app.fmt.workspace;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmConfigEditor extends WFileEditor {

	private CheckMode checkmode;
	private FvtmType type;

	public FvtmConfigEditor(File file){
		super(file);
	}

	@Override
	public void init(Object... args){
		super.init(args);
		container.top = 30;
		container.updateSize(container.w, container.h);
		checkmode = CheckMode.gen(container);
		type = FvtmType.fromFile(file);
	}

	@Override
	protected String get_editor_name(){
		return "Fvtm Config Editor";
	}

}
