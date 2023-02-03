package net.fexcraft.app.fmt.port.ex;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.settings.Setting;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TiMExporter extends FVTM_Format {

	private static final List<String> categories = Arrays.asList("model", "tim");
	private static final String VERSION = "1.1";
	private String pkg;
	
	public TiMExporter(){
		super("Trains in Motion Model (FVTM v3 Java)", "tim_exporter");
		settings.add(new Setting("package", "tim.models.locos", "exporter-tim"));
	}

	@Override
	protected void init(Model model, File file){
		modelname = validate_name(modelname == null ? model.name + "Model" : modelname);
		pkg = settings.g("package").value();
	}

	@Override
	protected String top_comment(){
		return "//FMT-Marker TiM-" + VERSION + "\n";
	}

	@Override
	protected String package_line(){
		return "package " + pkg + ";\n\n";
	}

	@Override
	protected void append_imports(StringBuffer buffer){
		buffer.append("import ebf.tim.models.StaticModelAnimator;\n");
		buffer.append("import fexcraft.fvtm.RollingStockModel;\n");
		buffer.append("import fexcraft.fvtm.TurboList;\n");
		buffer.append("import fexcraft.tmt.slim.ModelRendererTurbo;\n");
		buffer.append("import fexcraft.tmt.slim.Vec3f;\n");
		buffer.append("import net.minecraft.entity.Entity;\n\n");
	}

	@Override
	protected String title(){
		return "TiM Exporter V" + VERSION;
	}

	@Override
	protected String group_class() {
		return "TurboList";
	}

	@Override
	protected void append_declaration(StringBuffer buffer){
		buffer.append("public class " + modelname + " extends RollingStockModel {\n\n");
	}

	@Override
	public List<String> categories(){
		return categories;
	}

}
