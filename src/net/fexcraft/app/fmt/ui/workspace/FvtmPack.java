package net.fexcraft.app.fmt.ui.workspace;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmPack extends DirComponent {

	public final HashMap<FvtmType, ArrayList<DirComponent>> content = new HashMap<>();
	public final ArrayList<DirComponent> textures = new ArrayList<>();
	public final ArrayList<DirComponent> models = new ArrayList<>();
	public final String id;

	public FvtmPack(ViewerFileType type, WorkspaceViewer folcom, DirComponent root, File file, String id){
		super(type, folcom, root, file, 0);
		for(FvtmType ft : FvtmType.values()){
			content.put(ft, new ArrayList<>());
		}
		this.id = id;
	}

}
