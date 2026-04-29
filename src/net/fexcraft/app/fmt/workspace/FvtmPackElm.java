package net.fexcraft.app.fmt.workspace;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmPackElm extends DirElm {

	protected String id;

	public FvtmPackElm(File file, String id){
		super(VFileType.FVTM_FOLDER, file);
		this.id = id;
	}

}
