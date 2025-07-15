package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.PolyRenderer.DrawMode;
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
	public boolean enabled;

	public static void init(){
		ANIMATIONS.put("translator", new Translator());
	}

	public static Animation load(JsonMap map){
		Animation anim = ANIMATIONS.get(map.getString("id", null));
		return anim == null ? null : anim.create(map);
	}

	public abstract Animation create(JsonMap map);

	public abstract JsonMap save();

	public abstract void pre(Group group, DrawMode mode, float alpha);

	public abstract void pst(Group group, DrawMode mode, float alpha);

	public abstract String id();

	public abstract String[] keys();

	public abstract Object get(String str);

	public abstract void set(String str, Object val);

}
