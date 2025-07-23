package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AnimRef extends Animation {

	private String id;

	public AnimRef(String id){
		this.id = id;
	}

	@Override
	public Animation create(JsonMap map){
		AnimRef ref = new AnimRef(map.getString("id", "fvtm:glow"));
		return ref;
	}

	@Override
	public JsonMap save(){
		return new JsonMap();
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){

	}

	@Override
	public void pst(Group group, PolyRenderer.DrawMode mode, float alpha){

	}

	@Override
	public String id(){
		return id;
	}

	@Override
	public Object get(String str){
		return null;
	}

	@Override
	public void set(String str, Object val){

	}

}
