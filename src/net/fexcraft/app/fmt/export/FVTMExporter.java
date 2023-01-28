package net.fexcraft.app.fmt.export;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.StringArraySetting;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class FVTMExporter extends FVTM_Format {

	private static final List<String> categories = Arrays.asList("model", "fvtm");
	private String modelclass, modelimport, modelkind, packid, model_id;
	private static final String VERSION = "1.6";
	private Model model;

	public FVTMExporter(){
		super("FVTM Model (FVTM v3 Java)", "fvtm_exporter");
		settings.add(new Setting("pack_id", "your_addon_id", "exporter-fvtm"));
		settings.add(new Setting("model_id", "null", "exporter-fvtm"));
		settings.add(new StringArraySetting("model_type", "part", "exporter-fvtm", "part", "vehicle", "container", "block", "generic"));
		settings.add(new Setting("model_name", "default", "exporter-fvtm"));
	}

	@Override
	protected void init(Model model, File file){
		this.model = model;
		packid = settings.g("pack_id").value();
		modelname = settings.g("model_name").value();
		model_id = settings.g("model_id").value();
		if(modelname.equals("default") || modelname.equals("null")) modelname = null;
		if(model_id.equals("default") || model_id.equals("null")) model_id = null;
		modelname = validate_name(modelname == null ? model.name + "Model" : modelname);
		switch((String)settings.g("model_type").value()){
			case "part":{
				modelclass = "PartModel";
				modelkind = "part";
				break;
			}
			case "vehicle":{
				modelclass = "VehicleModel";
				modelkind = "vehicle";
				break;
			}
			case "container":{
				modelclass = "ContainerModel";
				modelkind = "container";
				break;
			}
			case "block":{
				modelclass = "BlockModel";
				modelkind = "block";
				break;
			}
			case "generic":{
				modelclass = "GenericModel";
				modelkind = "generic";
				break;
			}
			default:{
				modelclass = "InvalidExporterInput";
				modelkind = "invalid-exporter-input";
				break;
			}
		}
		modelimport = modelclass;
	}

	@Override
	protected String top_comment(){
		return "//FMT-Marker FVTM-" + VERSION + "\n";
	}

	@Override
	protected String package_line(){
		return "package net.fexcraft.mod.addon." + packid + ".models." + modelkind + ";\n\n";
	}

	@Override
	protected void append_imports(StringBuffer buffer){
		buffer.append("import net.fexcraft.lib.mc.api.registry.fModel;\n");
		buffer.append("import net.fexcraft.lib.tmt.ModelRendererTurbo;\n");
		buffer.append("import net.fexcraft.mod.fvtm.model.TurboList;\n");
		buffer.append("import net.fexcraft.mod.fvtm.model." + modelimport + ";\n\n");
	}

	@Override
	protected String title(){
		return "FVTM Exporter v" + VERSION;
	}

	@Override
	protected void append_declaration(StringBuffer buffer){
		buffer.append("@fModel(registryname = \"" + packid + ":models/" + modelkind + "/" + (model_id == null ? modelname : model_id) + "\")\n");
		buffer.append("public class " + modelname + " extends " + modelclass + " {\n\n");
	}

	@Override
	public List<String> categories(){
		return categories;
	}

}
