package net.fexcraft.app.fmt.wrappers;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.wrappers.face.Face;
import net.fexcraft.app.fmt.wrappers.face.NullFace;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.TexturedPolygon;
import net.fexcraft.lib.common.utils.ObjParser;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

public class ObjPreviewWrapper extends PolygonWrapper {
	
	public File source;
	public String group;
	private ArrayList<TexturedPolygon> polis;
	private int groupidx;
	
	public ObjPreviewWrapper(GroupCompound compound, File file, String group, ArrayList<TexturedPolygon> value, int idx){
		super(compound);
		this.source = file;
		this.group = group;
		this.polis = value;
		this.groupidx = idx;
	}

	public void render(boolean rotX, boolean rotY, boolean rotZ){
		if(visible && turbo != null) turbo.render(); this.selected = false;
	}

	@Override
	protected PolygonWrapper createClone(GroupCompound compound){
		return new ObjPreviewWrapper(compound, source, group, null, groupidx);
	}
	
	@SuppressWarnings("resource")
	protected ModelRendererTurbo newMRT(){
		try{
			if(polis == null){
				polis = new ObjParser(new FileInputStream(source)).readComments(false).readModel(true).parse().polygons.get(group);
			}
			return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList())){
					@Override
					public RGB getColor(int i){
						return super.getColor(groupidx);
					}
				}
				.setRotationPoint(pos.x, pos.y, pos.z)
				.setRotationAngle(rot.x, rot.y, rot.z)
				.copyTo(polis);//this.source.toString()
		}
		catch(Exception e){
			log(e);
			return new ModelRendererTurbo(null, textureX, textureY, compound.tx(getTurboList()), compound.ty(getTurboList()))
				.setRotationPoint(pos.x, pos.y, pos.z)
				.addSphere(-8, -8, -8, 16, 16, 16, 1, 1);
		}
	}

	@Override
	protected JsonObject populateJson(JsonObject obj, boolean export){
		obj.addProperty("location", source.getPath());
		return obj;
	}

	@Override
	public ShapeType getType(){
		return ShapeType.OBJ;
	}

	@Override
	public float[][][] newTexturePosition(boolean include_offsets, boolean exclude_detached){
		return new float[0][][];
	}

	@Override
	public PolygonWrapper convertTo(ShapeType type){
		return type == this.getType() ? this.clone() : null;
	}

	@Override
	public Face[] getTexturableFaces(){
		return NullFace.values();
	}
	
}
