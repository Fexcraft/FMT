package net.fexcraft.app.fmt.ui.workspace;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public enum ViewerFileType {

	NORMAL_FOLDER(true, false),
	EMPTY_FOLDER(true, false),
	FILE(false, false),
	FVTM_FOLDER(true, false),
	FVTM_FILE(false, true),
	FVTM_CONFIG(false, true),
	FMTB(false, false),
	JSON(false, true),
	FMF(false, false),
	OBJ(false, false),
	PNG(false, false),
	;

	public final boolean editable, directory;

	ViewerFileType(boolean dir, boolean edit){
		editable = edit;
		directory = dir;
	}

	public String filename(){
		switch(this){
			case EMPTY_FOLDER:
				return "folder_empty";
			case FVTM_FOLDER:
				return "folder_fvtmpack";
			case NORMAL_FOLDER:
				return "folder";
			case FVTM_FILE:
				return "file_fvtmaddonpack";
			case FVTM_CONFIG:
				return "file_fvtmcfg";
			case JSON:
				return "file_json";
			case FILE:
			default:
				return "file";
		}
	}

	static ViewerFileType fromFile(File file){
		if(file.getName().equals("addonpack.fvtm")) return FVTM_FILE;
		String name = file.getName();
		if(name.contains(".")){
			String[] split = name.split("\\.");
			if(split.length > 1){
				String suffix = split[split.length - 1];
				switch(suffix){
					case "json":
						return JSON;
					case "block":
					case "multiblock":
					case "cloth":
					case "container":
					case "material":
					case "vehicle":
					case "part":
					case "gauge":
					case "wire":
					case "consumable":
					case "fuel":
					case "recipe":
						return FVTM_CONFIG;
					case "fmtb":
						return FMTB;
					case "png":
						return PNG;
					case "obj":
						return OBJ;
					case "fmf":
						return FMF;
					default:
						break;
				}
			}
		}
		return FILE;
	}
}
