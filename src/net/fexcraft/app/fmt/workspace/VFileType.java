package net.fexcraft.app.fmt.workspace;

import org.apache.commons.lang3.tuple.Pair;

import java.io.File;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public enum VFileType {

	NORMAL_FOLDER(true, false),
	EMPTY_FOLDER(true, false),
	FILE(false, false),
	FVTM_FOLDER(true, false),
	FVTM_FILE(false, true),
	FVTM_CONFIG(false, true),
	FMTB(false, false),
	JSON(false, true),
	TOML(false, true),
	LANG(false, true),
	FMF(false, false),
	BOB(false, false),
	OBJ(false, true),
	PNG(false, true),
	;

	public final boolean editable, directory;
	public final Pair<VFileType, FvtmType> pair;

	VFileType(boolean dir, boolean edit){
		editable = edit;
		directory = dir;
		pair = Pair.of(this, null);
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
			case LANG:
				return "file_lang";
			case FILE:
			default:
				return "file";
		}
	}

	public static Pair<VFileType, FvtmType> fromFile(File file){
		if(file.isDirectory()){
			return (file.listFiles().length == 0 ? EMPTY_FOLDER : NORMAL_FOLDER).pair;
		}
		if(file.getName().equals("addonpack.fvtm")) return FVTM_FILE.pair;
		String name = file.getName();
		if(name.contains(".")){
			String[] split = name.split("\\.");
			if(split.length > 1){
				String suffix = split[split.length - 1];
				switch(suffix){
					case "json":
						return JSON.pair;
					case "toml":
						return TOML.pair;
					case "lang":
						return LANG.pair;
					case "fmtb":
						return FMTB.pair;
					case "png":
						return PNG.pair;
					case "obj":
						return OBJ.pair;
					case "fmf":
						return FMF.pair;
					case "beo":
					case "bob":
						return BOB.pair;
					//
					case "block":
						return parentOrGrandEquals(file, "blocks") ? FvtmType.BLOCK.pair : JSON.pair;
					case "multiblock":
						return parentOrGrandEquals(file, "blocks") ? FvtmType.MULTIBLOCK.pair : JSON.pair;
					case "cloth":
						return parentOrGrandEquals(file, "clothes") ? FvtmType.CLOTH.pair : JSON.pair;
					case "container":
						return parentOrGrandEquals(file, "containers") ? FvtmType.CONTAINER.pair : JSON.pair;
					case "material":
						return parentOrGrandEquals(file, "materials") ? FvtmType.MATERIAL.pair : JSON.pair;
					case "vehicle":
						return parentOrGrandEquals(file, "vehicles") ? FvtmType.VEHICLE.pair : JSON.pair;
					case "part":
						return parentOrGrandEquals(file, "parts") ? FvtmType.PART.pair : JSON.pair;
					case "gauge":
						return parentOrGrandEquals(file, "railgauges") ? FvtmType.RAILGAUGE.pair : JSON.pair;
					case "deco":
						return parentOrGrandEquals(file, "decos") ? FvtmType.DECORATION.pair : JSON.pair;
					case "wire":
						return parentOrGrandEquals(file, "wires") ? FvtmType.WIRE.pair : JSON.pair;
					case "wiredeco":
						return parentOrGrandEquals(file, "wires") ? FvtmType.WIRE_DECO.pair : JSON.pair;
					case "consumable":
						return parentOrGrandEquals(file, "consumables") ? FvtmType.CONSUMABLE.pair : JSON.pair;
					case "fuel":
						return parentOrGrandEquals(file, "fuels") ? FvtmType.FUEL.pair : JSON.pair;
					case "sign":
						return parentOrGrandEquals(file, "signs") ? FvtmType.SIGN.pair : JSON.pair;
					default:
						break;
				}
			}
		}
		return FILE.pair;
	}

	private static boolean parentOrGrandEquals(File file, String str){
		return file.getParentFile().getName().equals(str) || file.getParentFile().getParentFile().getName().equals(str);
	}

	public boolean model(){
		return this == FMF || this == BOB || this == OBJ;
	}

}
