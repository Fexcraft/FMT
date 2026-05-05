package net.fexcraft.app.fmt.workspace;

import net.fexcraft.app.fmt.utils.fvtm.LangCache;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class FvtmPackElm extends DirElm {

	public final HashMap<FvtmType, List<FileElm>> content = new HashMap<>();
	public final List<FileElm> textures = new ArrayList<>();
	public final List<FileElm> models = new ArrayList<>();
	public final LangCache lang;
	public final String name;
	public final String id;

	public FvtmPackElm(File file, String id){
		super(VFileType.FVTM_FOLDER, file);
		this.id = id;
		for(FvtmType val : FvtmType.values()){
			content.put(val, new ArrayList<>());
		}
		lang = new LangCache(this);
		JsonMap map = JsonHandler.parse(new File(file, "assets/" + id + "/addonpack.fvtm")).asMap();
		name = map.get("Name", id);
	}

}
