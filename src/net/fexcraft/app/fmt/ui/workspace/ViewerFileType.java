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
	TOML(false, true),
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

	static Object fromFile(File file){
		if(file.getName().equals("addonpack.fvtm")) return FVTM_FILE;
		String name = file.getName();
		if(name.contains(".")){
			String[] split = name.split("\\.");
			if(split.length > 1){
				String suffix = split[split.length - 1];
				switch(suffix){
					case "json":
						return JSON;
					case "toml":
						return TOML;
					case "fmtb":
						return FMTB;
					case "png":
						return PNG;
					case "obj":
						return OBJ;
					case "fmf":
						return FMF;
					//
					case "block":
						return file.getParentFile().getName().equals("blocks") ? new Object[]{ FVTM_CONFIG, FvtmType.BLOCK } : JSON;
					case "multiblock":
						return file.getParentFile().getName().equals("blocks") ? new Object[]{ FVTM_CONFIG, FvtmType.MULTIBLOCK } : JSON;
					case "cloth":
						return file.getParentFile().getName().equals("clothes") ? new Object[]{ FVTM_CONFIG, FvtmType.CLOTH } : JSON;
					case "container":
						return file.getParentFile().getName().equals("containers") ? new Object[]{ FVTM_CONFIG, FvtmType.CONTAINER } : JSON;
					case "material":
						return file.getParentFile().getName().equals("materials") ? new Object[]{ FVTM_CONFIG, FvtmType.MATERIAL } : JSON;
					case "vehicle":
						return file.getParentFile().getName().equals("vehicles") ? new Object[]{ FVTM_CONFIG, FvtmType.VEHICLE } : JSON;
					case "part":
						return file.getParentFile().getName().equals("parts") ? new Object[]{ FVTM_CONFIG, FvtmType.PART } : JSON;
					case "gauge":
						return file.getParentFile().getName().equals("railgauges") ? new Object[]{ FVTM_CONFIG, FvtmType.RAILGAUGE } : JSON;
					case "wire":
						return file.getParentFile().getName().equals("wires") ? new Object[]{ FVTM_CONFIG, FvtmType.WIRE } : JSON;
					case "consumable":
						return file.getParentFile().getName().equals("consumables") ? new Object[]{ FVTM_CONFIG, FvtmType.CONSUMABLE } : JSON;
					case "fuel":
						return file.getParentFile().getName().equals("fuels") ? new Object[]{ FVTM_CONFIG, FvtmType.FUEL } : JSON;
					default:
						break;
				}
			}
		}
		return FILE;
	}

	public boolean model(){
		return this == FMF || this == OBJ;
	}

}
