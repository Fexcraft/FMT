package net.fexcraft.app.fmt.wrappers;

import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.TreeMap;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.generic.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class GroupCompound {
	
	public int textureX = 256, textureY = 256;
	public float rate = 1;
	//
	private TreeMap<String, TurboList> compound = new TreeMap<>();
	private ArrayList<Selection> selection = new ArrayList<>();
	public ArrayList<String> creators = new ArrayList<>();
	public File file; public String name = "unnamed model";
	public String texture;
	//
	public boolean visible = true, minimized;
	public Vec3f pos, rot;
	
	public GroupCompound(){
		//compound.put("body", new TurboList("body"));
		recompile(); this.updateFields();
	}

	public void recompile(){
		compound.values().forEach(turbo -> turbo.forEach(elm -> elm.recompile()));
	}

	public void render(){
		if(!visible) return; RGB.glColorReset();
		if(pos != null){
			GL11.glTranslatef(pos.xCoord, pos.yCoord, pos.zCoord);
		}
		if(rot != null){
			GL11.glPushMatrix();
			GL11.glRotatef(rot.xCoord, 1, 0, 0);
			GL11.glRotatef(rot.yCoord, 0, 1, 0);
			GL11.glRotatef(rot.zCoord, 0, 0, 1);
		}
		TextureManager.bindTexture(texture == null ? "blank" : texture);
		compound.values().forEach(elm -> elm.render());
		compound.values().forEach(elm -> elm.renderLines());
		if(pos != null){
			GL11.glTranslatef(-pos.xCoord, -pos.yCoord, -pos.zCoord);
		}
		if(rot != null){
			GL11.glRotatef(-rot.zCoord, 0, 0, 1);
			GL11.glRotatef(-rot.yCoord, 0, 1, 0);
			GL11.glRotatef(-rot.xCoord, 1, 0, 0);
			GL11.glPopMatrix();
		}
	}
	
	public ArrayList<Selection> getSelected(){ return selection; }

	public boolean updateValue(TextField field, String id){
		if(selection.isEmpty()) return false;
		boolean positive = id.endsWith("+"), alright = false;
		id = id.replace("-", "").replace("+", "");
		float f = field.tryChange(positive, rate);
		boolean x = id.endsWith("x"), y = id.endsWith("y"), z = id.endsWith("z");
		id = id.substring(0, id.length() - 1);
		//FMTB.print(field, id, positive, x, y, z, f);
		for(int i = 0; i < selection.size(); i++){
			if(i == 0){ alright = selection.get(i).apply(id, f, x, y, z); continue; }
			selection.get(i).apply(id, f, x, y, z);
		}
		if(alright) field.applyChange(f);
		return alright;
	}
	
	public boolean updateValue(TextField field){
		if(selection.isEmpty()) return false;
		boolean x = field.id.endsWith("x"), y = field.id.endsWith("y"), z = field.id.endsWith("z");
		String id = field.id.substring(0, field.id.length() - 1);
		for(int i = 0; i < selection.size(); i++){
			if(i == 0){ selection.get(i).apply(id, field.getFloatValue(), x, y, z); continue; }
			selection.get(i).apply(id, field.getFloatValue(), x, y, z);
		}
		return true;
	}
	
	public static class Selection {
		
		public Selection(String string, int i){
			this.group = string; this.element = i;
		}

		public boolean apply(String id, float value, boolean x, boolean y, boolean z){
			PolygonWrapper shape = FMTB.MODEL.compound.containsKey(group) ? FMTB.MODEL.compound.get(group).get(element) : null;
			if(shape == null) return false;
			boolean bool = false;
			switch(id){
				case "size":{
					if(shape.getType().isCuboid()){
						bool = shape.setFloat(id, x, y, z, value);
					} break;
				}
				case "pos": case "off": {
					bool = shape.setFloat(id, x, y, z, value); break;
				}
				case "rot":{
					bool = shape.setFloat(id, x, y, z, (float)Math.toRadians(value)); break;
				}
				case "cor0": case "cor1": case "cor2": case "cor3": case "cor4": case "cor5": case "cor6": case "cor7":{
					if(shape.getType().isShapebox()){
						bool = shape.setFloat(id, x, y, z, value);
					} break;
				}
				case "cyl0": case "cyl1": case "cyl2":{
					if(shape.getType().isCylinder()){
						bool = shape.setFloat(id, x, y, z, value);
					} break;
				}
			}
			shape.recompile();
			return bool;
		}

		public String group;
		public int element;
		
		@Override
		public boolean equals(Object o){
			return o instanceof Selection ? ((Selection)o).group.equals(group) && ((Selection)o).element == element : false; 
		}
		
	}

	public void add(PolygonWrapper shape){
		try{
			if(compound.isEmpty()) compound.put("group0", new TurboList("group0"));
			TurboList list = (compound.containsKey("body") ? compound.get("body") : (TurboList)compound.values().toArray()[0]);
			selection.clear(); selection.add(new Selection(list.id, list.size())); list.add(shape); shape.recompile();
			this.updateFields();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public TreeMap<String, TurboList> getCompound(){
		return compound;
	}

	public void deselectGroup(String id){
		selection.removeIf(pre -> pre.group.equals(id)); this.updateFields();
	}

	public void selectGroup(String id){
		TurboList list = compound.get(id); if(list == null) return;
		Selection temp = null;
		for(int i = 0; i < list.size(); i++){
			if(!selection.contains(temp = new Selection(list.id, i))){
				selection.add(temp);
			}
		} this.updateFields();
	}

	public void deselect(String id, int poly){
		selection.removeIf(pre -> pre.group.equals(id) && pre.element == poly); this.updateFields();
	}

	public void select(String id, int poly){
		if(!Keyboard.isKeyDown(Keyboard.KEY_LSHIFT)) selection.clear();
		Selection temp = new Selection(id, poly); if(!selection.contains(temp)) selection.add(temp); this.updateFields();
	}
	
	public void updateFields(){
		try{
			if(FMTB.get() == null || FMTB.get().UI == null || !FMTB.get().UI.hasElement("general_editor")) return; FMTB.get().setTitle(this.name);
			Editor editor = (Editor)FMTB.get().UI.getElement("general_editor"); PolygonWrapper poly = getSelectedPolygon(0);
			if(poly == null){
				editor.getField("sizex").applyChange(0);
				editor.getField("sizey").applyChange(0);
				editor.getField("sizez").applyChange(0);
				//
				editor.getField("posx").applyChange(0);
				editor.getField("posy").applyChange(0);
				editor.getField("posz").applyChange(0);
				//
				editor.getField("offx").applyChange(0);
				editor.getField("offy").applyChange(0);
				editor.getField("offz").applyChange(0);
				//
				editor.getField("rotx").applyChange(0);
				editor.getField("roty").applyChange(0);
				editor.getField("rotz").applyChange(0);
				//
				editor.getField("texx").applyChange(0);
				editor.getField("texy").applyChange(0);
				//
				editor.getField("group").setText("none", true);
				editor.getField("boxname").setText("no polygon selected", true);
			}
			else{
				editor.getField("sizex").applyChange(poly.getFloat("size", true, false, false));
				editor.getField("sizey").applyChange(poly.getFloat("size", false, true, false));
				editor.getField("sizez").applyChange(poly.getFloat("size", false, false, true));
				//
				editor.getField("posx").applyChange(poly.getFloat("pos", true, false, false));
				editor.getField("posy").applyChange(poly.getFloat("pos", false, true, false));
				editor.getField("posz").applyChange(poly.getFloat("pos", false, false, true));
				//
				editor.getField("offx").applyChange(poly.getFloat("off", true, false, false));
				editor.getField("offy").applyChange(poly.getFloat("off", false, true, false));
				editor.getField("offz").applyChange(poly.getFloat("off", false, false, true));
				//
				editor.getField("rotx").applyChange(round(Math.toDegrees(poly.getFloat("rot", true, false, false))));
				editor.getField("roty").applyChange(round(Math.toDegrees(poly.getFloat("rot", false, true, false))));
				editor.getField("rotz").applyChange(round(Math.toDegrees(poly.getFloat("rot", false, false, true))));
				//
				editor.getField("texx").applyChange(poly.getFloat("tex", true, false, false));
				editor.getField("texy").applyChange(poly.getFloat("tex", false, true, false));
				//
				editor.getField("group").setText(selection.get(0).group, true);
				editor.getField("boxname").setText(poly.name == null ? "unnamed" : poly.name, true);
			}
			editor.getField("multiplicator").applyChange(rate);
			//
			editor = (Editor)FMTB.get().UI.getElement("shapebox_editor");
			if(poly == null || !poly.getType().isShapebox()){
				editor.getField("cor0x").applyChange(0);
				editor.getField("cor0y").applyChange(0);
				editor.getField("cor0z").applyChange(0);
				//
				editor.getField("cor1x").applyChange(0);
				editor.getField("cor1y").applyChange(0);
				editor.getField("cor1z").applyChange(0);
				//
				editor.getField("cor2x").applyChange(0);
				editor.getField("cor2y").applyChange(0);
				editor.getField("cor2z").applyChange(0);
				//
				editor.getField("cor3x").applyChange(0);
				editor.getField("cor3y").applyChange(0);
				editor.getField("cor3z").applyChange(0);
				//
				editor.getField("cor4x").applyChange(0);
				editor.getField("cor4y").applyChange(0);
				editor.getField("cor4z").applyChange(0);
				//
				editor.getField("cor5x").applyChange(0);
				editor.getField("cor5y").applyChange(0);
				editor.getField("cor5z").applyChange(0);
				//
				editor.getField("cor6x").applyChange(0);
				editor.getField("cor6y").applyChange(0);
				editor.getField("cor6z").applyChange(0);
				//
				editor.getField("cor7x").applyChange(0);
				editor.getField("cor7y").applyChange(0);
				editor.getField("cor7z").applyChange(0);
			}
			else{
				editor.getField("cor0x").applyChange(poly.getFloat("cor0", true, false, false));
				editor.getField("cor0y").applyChange(poly.getFloat("cor0", false, true, false));
				editor.getField("cor0z").applyChange(poly.getFloat("cor0", false, false, true));
				//
				editor.getField("cor1x").applyChange(poly.getFloat("cor1", true, false, false));
				editor.getField("cor1y").applyChange(poly.getFloat("cor1", false, true, false));
				editor.getField("cor1z").applyChange(poly.getFloat("cor1", false, false, true));
				//
				editor.getField("cor2x").applyChange(poly.getFloat("cor2", true, false, false));
				editor.getField("cor2y").applyChange(poly.getFloat("cor2", false, true, false));
				editor.getField("cor2z").applyChange(poly.getFloat("cor2", false, false, true));
				//
				editor.getField("cor3x").applyChange(poly.getFloat("cor3", true, false, false));
				editor.getField("cor3y").applyChange(poly.getFloat("cor3", false, true, false));
				editor.getField("cor3z").applyChange(poly.getFloat("cor3", false, false, true));
				//
				editor.getField("cor4x").applyChange(poly.getFloat("cor4", true, false, false));
				editor.getField("cor4y").applyChange(poly.getFloat("cor4", false, true, false));
				editor.getField("cor4z").applyChange(poly.getFloat("cor4", false, false, true));
				//
				editor.getField("cor5x").applyChange(poly.getFloat("cor5", true, false, false));
				editor.getField("cor5y").applyChange(poly.getFloat("cor5", false, true, false));
				editor.getField("cor5z").applyChange(poly.getFloat("cor5", false, false, true));
				//
				editor.getField("cor6x").applyChange(poly.getFloat("cor6", true, false, false));
				editor.getField("cor6y").applyChange(poly.getFloat("cor6", false, true, false));
				editor.getField("cor6z").applyChange(poly.getFloat("cor6", false, false, true));
				//
				editor.getField("cor7x").applyChange(poly.getFloat("cor7", true, false, false));
				editor.getField("cor7y").applyChange(poly.getFloat("cor7", false, true, false));
				editor.getField("cor7z").applyChange(poly.getFloat("cor7", false, false, true));
			}
			editor.getField("multiplicator").applyChange(rate);
			//
			editor = (Editor)FMTB.get().UI.getElement("cylinder_editor");
			if(poly == null || !poly.getType().isCylinder()){
				editor.getField("cyl0x").applyChange(0); editor.getField("cyl0y").applyChange(0);
				editor.getField("cyl1x").applyChange(0); editor.getField("cyl1y").applyChange(0);
				editor.getField("cyl2x").applyChange(0); editor.getField("cyl2y").applyChange(0);
			}
			else{
				editor.getField("cyl0x").applyChange(poly.getFloat("cyl0", true, false, false));
				editor.getField("cyl0y").applyChange(poly.getFloat("cyl0", false, true, false));
				editor.getField("cyl1x").applyChange(poly.getFloat("cyl1", true, false, false));
				editor.getField("cyl1y").applyChange(poly.getFloat("cyl1", false, true, false));
				editor.getField("cyl2x").applyChange(poly.getFloat("cyl2", true, false, false));
				editor.getField("cyl2y").applyChange(poly.getFloat("cyl2", false, true, false));
			}
			editor.getField("multiplicator").applyChange(rate);
			//
			editor = (Editor)FMTB.get().UI.getElement("group_editor"); TurboList list = this.getSelectedGroup(0);
			if(list == null){
				editor.getField("rgb0").applyChange(0);
				editor.getField("rgb1").applyChange(0);
				editor.getField("rgb2").applyChange(0);
				editor.getField("groupname").setText("no polygon selected", true);
			}
			else{
				byte[] arr = list.color == null ? RGB.WHITE.toByteArray() : list.color.toByteArray();
				editor.getField("rgb0").applyChange(arr[0] + 128);
				editor.getField("rgb1").applyChange(arr[1] + 128);
				editor.getField("rgb2").applyChange(arr[2] + 128);
				editor.getField("groupname").setText(list.id, true);
			}
			editor.getField("multiplicator").applyChange(rate);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	private float round(double df){
        BigDecimal deci = new BigDecimal(Float.toString((float)df));
        deci = deci.setScale(3, BigDecimal.ROUND_HALF_UP);
        return deci.floatValue();
	}

	public PolygonWrapper getSelectedPolygon(int i){
		if(selection.isEmpty()) return null;
		if(i >= selection.size()) return null;
		if(compound.containsKey(selection.get(i).group)){
			if(selection.get(i).element >= compound.get(selection.get(i).group).size()){
				selection.remove(i); return null;
			} else return compound.get(selection.get(i).group).get(selection.get(i).element);
		}
		else return null;
	}

	private PolygonWrapper removeSelectedPolygon(int s){
		if(selection.isEmpty()) return null;
		if(s >= selection.size()) return null;
		if(compound.containsKey(selection.get(s).group)){
			if(selection.get(s).element >= compound.get(selection.get(s).group).size()){
				selection.remove(s); return null;
			} else return compound.get(selection.get(s).group).remove(selection.get(s).element);
		}
		else return null;
	}

	public float multiply(float flea){
		return rate = (rate *= flea) < 0.01f ? 0.01f : rate > 1000 ? 1000 : rate;
	}

	public void changeGroupIndex(int i){
		if(selection.isEmpty()) return;
		String current = selection.get(0).group; int index = i;
		for(String key : compound.keySet()){ if(key.equals(current)) break; else index++; }
		if(index >= compound.size()) index -= compound.size(); if(index < 0) index = 0;
		current = compound.keySet().toArray(new String[0])[index];
		ArrayList<PolygonWrapper> array = new ArrayList<>();
		for(int s = 0; s < selection.size(); s++){ array.add(getSelectedPolygon(s)); }
		for(int s = 0; s < selection.size(); s++){ removeSelectedPolygon(s); }
		selection.clear();
		for(PolygonWrapper wrapper : array){
			if(wrapper != null){
				selection.add(new Selection(current, compound.get(current).size()));
				compound.get(current).add(wrapper);
			}
		} array.clear(); this.updateFields();
	}
	
	public int countTotalMRTs(){
		int i = 0; for(TurboList list : compound.values()) i += list.size(); return i;
	}
	
	public TurboList getSelectedGroup(int i){
		if(i >= selection.size() || i < 0) return null;
		return compound.get(selection.get(i).group);
	}

	public int getSelectedGroups(){
		ArrayList<String> list = new ArrayList<>();
		for(Selection sel : selection){ if(!list.contains(sel.group)){ list.add(sel.group); } }
		return list.size();
	}

	public void setTexture(String string){
		this.texture = string; this.compound.values().forEach(turbo -> turbo.forEach(poly -> poly.recompile()));
	}

}
