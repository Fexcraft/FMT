package net.fexcraft.app.fmt.wrappers;

import static net.fexcraft.lib.common.Static.sixteenth;

import java.util.ArrayList;

import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;

import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.utils.Animator.ZVerHyd;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.lib.common.math.Vec3f;

public class SwivelPointLite {
	
	public Axis3DL axe = new Axis3DL();
	public Vec3f pos = new Vec3f(), rot = new Vec3f();
	public ArrayList<SwivelPointLite> subs = new ArrayList<>();
	public ArrayList<TurboList> lists = new ArrayList<>();
	public SwivelPointLite root;
	public String rootid;
	
	public SwivelPointLite(String text){
		rootid = text;
	}

	public void pos(int axe, NumberField field){
		switch(axe){
			case 0: pos.x = field.getValue(); break;
			case 1: pos.y = field.getValue(); break;
			case 2: pos.z = field.getValue(); break;
		}
	}
	
	public void rot(int axe, NumberField field){
		switch(axe){
			case 0: rot.x = field.getValue(); break;
			case 1: rot.y = field.getValue(); break;
			case 2: rot.z = field.getValue(); break;
		}
	}

	public void updatee(GroupCompound compound){
		for(TurboList list : lists){
			for(PolygonWrapper wrapper : list){
				Vec3f vec = getRelVec(wrapper.pos.x, wrapper.pos.y, wrapper.pos.z);
				ZVerHyd.sp.setPosition(vec.x, vec.y, vec.z).render();
			}
		}
		for(SwivelPointLite lite : subs){
			lite.updatee(compound);
		}
	}
	
	public Vec3f getRelVec(float x, float y, float z){
		axe.setAngles(-rot.y, -rot.z, -rot.x);
		Vec3f vec = axe.getRelativeVector(x, y, z).add(pos);
		if(root != null) return root.getRelVec(vec.x, vec.y, vec.z);
		return vec;
	}
	
	public Vec3f getRelVec(Vec3f vec){
		return getRelVec(vec.x, vec.y, vec.z);
	}

	public void update(GroupCompound compound, int mode){
		axe.setAngles(-rot.y, -rot.z, -rot.x);
		GL11.glRotatef(rot.y, 0, 1, 0);
		GL11.glRotatef(rot.z, 0, 0, 1);
		GL11.glRotatef(rot.x, 1, 0, 0);
		if(lists.size() > 0){
			if(mode == 0){
				for(TurboList list : lists){
					list.bindApplicableTexture(compound);
					list.render(true);
				}
			}
			else if(mode == 1){
				for(TurboList list : lists){
					list.renderLines();
				}
			}
			else{
				for(TurboList list : lists){
					list.renderPicking();
				}
			}
		}
		for(SwivelPointLite lite : subs){
			Vec3f vec = lite.pos;
			GL11.glPushMatrix();
			GL11.glTranslatef(vec.x * sixteenth, vec.y * sixteenth, vec.z * sixteenth);
			lite.update(compound, mode);
			GL11.glPopMatrix();
		}
	}

	public JsonArray save(){
		JsonArray array = new JsonArray();
		array.add(pos.x);
		array.add(pos.y);
		array.add(pos.z);
		array.add(rot.x);
		array.add(rot.y);
		array.add(rot.z);
		if(rootid != null) array.add(rootid);
		return array;
	}
	
	public SwivelPointLite load(JsonArray array){
		pos.x = array.get(0).getAsFloat();
		pos.y = array.get(1).getAsFloat();
		pos.z = array.get(2).getAsFloat();
		rot.x = array.get(3).getAsFloat();
		rot.y = array.get(4).getAsFloat();
		rot.z = array.get(5).getAsFloat();
		if(array.size() > 6) rootid = array.get(6).getAsString();
		return this;
	}
	

}
