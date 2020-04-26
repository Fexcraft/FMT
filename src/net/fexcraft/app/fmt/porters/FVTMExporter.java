package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.util.Map;

import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Setting.Type;
import net.fexcraft.app.fmt.wrappers.GroupCompound;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FVTMExporter extends FVTMFormatBase {
	
	private String modelclass, modelkind, packid, model_id;
	private static final String VERSION = "1.4";
	
	public FVTMExporter(){
		super("FVTM v3 Scheme", "fvtm_exporter");
		settings.add(new Setting(Type.STRING, "pack_id", "your-addon-id"));
		settings.add(new Setting(Type.STRING, "model_id", "null"));
		settings.add(new Setting(Type.STRING, "model_type", "part"));
		settings.add(new Setting(Type.STRING, "model_name", "default"));
	}

	@Override
	protected void initExport(GroupCompound compound, File file, Map<String, Setting> settings){
		packid = settings.get("pack_id").getStringValue();
		modelname = settings.get("model_name").getStringValue();
		model_id = settings.get("model_id").getStringValue();
		if(modelname.equals("default") || modelname.equals("null")) modelname = null;
		if(model_id.equals("default") || model_id.equals("null")) model_id = null;
		modelname = validateName(modelname == null ? compound.name + "Model" : modelname);
		switch(settings.get("model_type").getStringValue()){
			case "part":{
				modelclass = "PartModel"; modelkind = "part"; break;
			}
			case "vehicle":{
				modelclass = "VehicleModel"; modelkind = "vehicle"; break;
			}
			case "container":{
				modelclass = "ContainerModel"; modelkind = "container"; break;
			}
			case "roadsign":{
				modelclass = "RoadSignModel"; modelkind = "roadsign"; break;
			}
			case "block":{
				modelclass = "BlockModel"; modelkind = "block"; break;
			}
			default:{
				modelclass = "InvalidExporterInput"; modelkind = "invalid-exporter-input"; break;
			}
		}
	}

	@Override
	protected String getTopCommentLine(){
		return "//FMT-Marker FVTM-" + VERSION + "\n";
	}

	@Override
	protected String getPackageLine(){
		return "package net.fexcraft.mod.addon." + packid + ".models." + modelkind + ";\n\n";
	}

	@Override
	protected void appendImports(StringBuffer buffer){
		buffer.append("import net.fexcraft.lib.mc.api.registry.fModel;\n");
		buffer.append("import net.fexcraft.lib.tmt.ModelRendererTurbo;\n");
		buffer.append("import net.fexcraft.mod.fvtm.model.TurboList;\n");
		buffer.append("import net.fexcraft.mod.fvtm.model." + modelclass + ";\n\n");
	}

	@Override
	protected String getTitle(){
		return "FVTM Exporter V" + VERSION;
	}

	@Override
	protected void appendClassDeclaration(StringBuffer buffer){
		buffer.append("@fModel(registryname = \"" + packid + ":models/" + modelkind + "/"+ (model_id == null ? modelname : model_id) + "\")\n");
		buffer.append("public class " + modelname + " extends " + modelclass + " {\n\n");
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}

}
