package net.fexcraft.app.fmt.animation;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class AttrRotator extends Animation {

	public int axe = 0;
	public float min = -16;
	public float max = 16;
	public float step = 0.1f;
	public float def = 0f;
	public float ocur;
	public float curr;
	public float icur;
	public boolean booltype = true;
	public String attr;

	@Override
	public Animation create(JsonMap map){
		AttrRotator ani = new AttrRotator();
		ani.min = map.getFloat("min", min);
		ani.max = map.getFloat("max", max);
		ani.axe = map.getInteger("axe", axe);
		ani.step = map.getFloat("step", step);
		ani.booltype = map.getBoolean("bool", booltype);
		ani.def = map.getFloat("defrot", def);
		ani.attr = map.getString("attr", "");
		return ani;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("min", min);
		map.add("max", max);
		map.add("axe", axe);
		map.add("step", step);
		map.add("def", def);
		map.add("bool", booltype);
		map.add("attr", attr);
		return map;
	}

	@Override
	public void update(){
		ocur = curr;
		if(FMT.MODEL.vehattrs.containsKey(attr)){
			try{
				if(booltype){
					curr += Model.getAttrVal(attr, boolean.class) ? step : -step;
				}
				else{
					curr = Model.getAttrVal(attr, Number.class).floatValue();
				}
				if(curr > max) curr = max;
				if(curr < min) curr = min;
			}
			catch(Exception e){
				Logging.bar(e.getMessage());
			}
		}
	}

	@Override
	public void pre(Group group, PolyRenderer.DrawMode mode, float alpha){
		icur = ocur + (curr - ocur) * alpha + def;
		if(axe == 0) for(Polygon poly : group) poly.glm.rotX += icur;
		else if(axe == 1) for(Polygon poly : group) poly.glm.rotY += icur;
		else if(axe == 2) for(Polygon poly : group) poly.glm.rotZ += icur;
	}

	@Override
	public void pst(Group group, PolyRenderer.DrawMode mode, float alpha){
		if(axe == 0) for(Polygon poly : group) poly.glm.rotX -= icur;
		else if(axe == 1) for(Polygon poly : group) poly.glm.rotY -= icur;
		else if(axe == 2) for(Polygon poly : group) poly.glm.rotZ -= icur;
	}

	@Override
	public String id(){
		return "fvtm:attribute_rotator";
	}

	@Override
	public Object get(String str){
		switch(str){
			case "min": return min;
			case "max": return max;
			case "axe": return axe;
			case "step": return step;
			case "def-rot": return def;
			case "bool-type": return booltype;
			case "attribute": return attr;
		}
		return null;
	}

	@Override
	public void set(String str, Object val){
		switch(str){
			case "min": min = (float)val; break;
			case "max": max = (float)val; break;
			case "axe":{
				int ax = (int)(float)val;
				if(ax < 0) ax = 0;
				if(ax > 2) ax = 2;
				axe = ax;
				break;
			}
			case "step": step = (float)val; break;
			case "def-rot": def = (float)val; break;
			case "bool-type": booltype = (boolean)val; break;
			case "attribute": attr = val.toString(); break;
		}
	}

}
