package net.fexcraft.app.fmt.porters;

import java.io.File;
import java.util.Map;

import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.GroupCompound;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TiMExporter extends FVTMFormatBase {
	
	private static final String VERSION = "1.0";
	private String pkg;
	
	public TiMExporter(){
		super("TiM (FVTM-Scheme) Exporter", "tim_exporter");
		settings.add(new Setting("package", "tim.models.locos"));
	}

	@Override
	protected void initExport(GroupCompound compound, File file, Map<String, Setting> settings){
		modelname = validateName(modelname == null ? compound.name + "Model" : modelname);
		pkg = settings.get("package").getStringValue();
	}

	@Override
	protected String getTopCommentLine(){
		return "//FMT-Marker TiM-" + VERSION + "\n";
	}

	@Override
	protected String getPackageLine(){
		return "package " + pkg + ";\n\n";
	}

	@Override
	protected void appendImports(StringBuffer buffer){
		buffer.append("import ebf.tim.models.StaticModelAnimator;\n");
		buffer.append("import fexcraft.fvtm.RollingStockModel;\n");
		buffer.append("import fexcraft.fvtm.TurboList;\n");
		buffer.append("import fexcraft.tmt.slim.ModelRendererTurbo;\n");
		buffer.append("import fexcraft.tmt.slim.Vec3f;\n");
		buffer.append("import net.minecraft.entity.Entity;\n\n");
	}

	@Override
	protected String getTitle(){
		return "TiM Exporter V" + VERSION;
	}

	@Override
	protected void appendClassDeclaration(StringBuffer buffer){
		buffer.append("public class " + modelname + " extends RollingStockModel {\n\n");
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}

	@Override
	protected String getScale(){
		return "";
	}

}
