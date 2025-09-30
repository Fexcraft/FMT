package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
import net.fexcraft.app.fmt.utils.fvtm.FvtmTypes;
import net.fexcraft.app.json.JsonMap;

import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public abstract class Animation {

	public static HashMap<String, Animation> ANIMATIONS = new LinkedHashMap<>();
	protected static String[] NOKEYS = new String[0];
	//
	public boolean enabled = true;

	public static void init(){
		ANIMATIONS.put("ref", new AnimRef("fmt:reference"));
		ANIMATIONS.put("translator", new Translator());
	}

	public static Animation load(JsonMap map){
		String id = map.getString("id", null);
		if(id == null) return null;
		Animation anim = ANIMATIONS.containsKey(id) ? ANIMATIONS.get(id) : null;
		if(anim == null){
			var ref = FvtmTypes.getProgRef(id);
			if(ref != null) anim = ref.anim();
		}
		if(anim == null) return null;
		anim = anim.create(map);
		anim.enabled = map.getBoolean("enabled", true);
		return anim;
	}

	public abstract Animation create(JsonMap map);

	public abstract JsonMap save();

	public abstract void update();

	public abstract void pre(Group group, DrawMode mode, float alpha);

	public abstract void pst(Group group, DrawMode mode, float alpha);

	public abstract String id();

	public abstract Object get(String str);

	public abstract void set(String str, Object val);

}
