package net.fexcraft.app.fmt.wrappers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.editor.Editor;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class GroupCompound {
	
	public int textureX = 256, textureY = 256;
	public float rate = 1f;
	//
	private TreeMap<String, TurboList> compound = new TreeMap<>();
	public ArrayList<String> creators = new ArrayList<>();
	public File file; public String name = "unnamed model";
	public PolygonWrapper lastselected;
	public String texture;
	//
	public boolean visible = true, minimized;
	public Vec3f pos, rot, scale;
	
	public GroupCompound(){
		//compound.put("body", new TurboList("body"));
		/*for(int i = 0; i < 20; i++){
			TurboList list = new TurboList("list" + i);
			for(int j = 0; j < 1000; j++){
				BoxWrapper box = new BoxWrapper(this);
				box.size = new Vec3f(1, 1, 1);
				box.pos = new Vec3f((j * 2) - 500, i * 2, 0);
				box.name = "box" + j;
				list.add(box);
			}
			compound.put(list.id, list);
		};*/
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
		if(scale != null){
			GL11.glPushMatrix();
			GL11.glScalef(scale.xCoord, scale.yCoord, scale.zCoord);
		}
		if(RayCoastAway.PICKING){
			if(TextureEditor.pixelMode()){
				TextureManager.bindTexture(getTempTex());
				compound.values().forEach(elm -> elm.render(false));
			}
			else{
				compound.values().forEach(elm -> elm.renderPicking());
			}
			//compound.values().forEach(elm -> elm.renderLines());
			RayCoastAway.doTest(false);
		}
		else{
			TextureManager.bindTexture(texture == null ? "blank" : texture);
			compound.values().forEach(elm -> elm.render(true));
			compound.values().forEach(elm -> elm.renderLines());
			//compound.values().forEach(elm -> elm.renderPicking());//uncomment for debugging the ray-/color-picker
		}
		if(scale != null){
			GL11.glPopMatrix();
		}
		if(rot != null){
			GL11.glRotatef(-rot.zCoord, 0, 0, 1);
			GL11.glRotatef(-rot.yCoord, 0, 1, 0);
			GL11.glRotatef(-rot.xCoord, 1, 0, 0);
			GL11.glPopMatrix();
		}
		if(pos != null){
			GL11.glTranslatef(-pos.xCoord, -pos.yCoord, -pos.zCoord);
		}
	}
	
	public static final String temptexid = "temp/calculation_texture";
	
	private String getTempTex(){
		Texture tex = TextureManager.getTexture(temptexid, true);
		if(tex == null || (tex.getImage().getWidth() != this.textureX || tex.getImage().getHeight() != textureY)){
			if(textureX >= 8192 || textureY >= 8192){ /*//TODO*/ }
			else{
				BufferedImage image = null;//new BufferedImage(textureX, textureY, BufferedImage.TYPE_INT_ARGB);
				if(tex == null){
					image = new BufferedImage(textureX, textureY, BufferedImage.TYPE_INT_ARGB);
				}
				else{
					tex.resize(textureX, textureY, null); image = tex.getImage();
				}
				int lastint = 0;
				for(int x = 0; x < textureX; x++){
					for(int y = 0; y < textureY; y++){
						image.setRGB(x, y, new Color(lastint).getRGB()); lastint++;
					}
				}
				if(tex == null){
					TextureManager.loadTextureFromZip(image, temptexid, true);
				}
				else{
					tex.rebind(); TextureManager.saveTexture(temptexid);
				}
			}
		}
		return temptexid;
	}

	public ArrayList<PolygonWrapper> getSelected(){
		ArrayList<PolygonWrapper> polis = new ArrayList<>();
		for(TurboList list : compound.values()){
			if(list.selected){ polis.addAll(list); }
			else{
				for(PolygonWrapper poly : list){
					if(poly.selected) polis.add(poly);
				}
			}
		}
		return polis;
	}

	public final void clearSelection(){
		for(TurboList list : compound.values()){
			list.selected = false; for(PolygonWrapper poly : list) poly.selected = false;
		}
	}

	public boolean updateValue(TextField field, String id){
		ArrayList<PolygonWrapper> polis = this.getSelected();
		if(polis.isEmpty()) return false;
		boolean positive = id.endsWith("+"); id = id.replace("-", "").replace("+", "");
		boolean x = id.endsWith("x"), y = id.endsWith("y"), z = id.endsWith("z");
		id = id.substring(0, id.length() - 1);
		for(int i = 0; i < polis.size(); i++){
			float f = field.tryChange(polis.get(i).getFloat(id, x, y, z), positive, rate);
			if(i == 0){
				if(polis.get(i).apply(id, f, x, y, z)){
					field.applyChange(f);
				}
			}
			else{
				polis.get(i).apply(id, f, x, y, z);
			}
		}
		return true;
	}
	
	public boolean updateValue(TextField field){
		ArrayList<PolygonWrapper> polis = this.getSelected();
		if(polis.isEmpty()) return false;
		boolean x = field.id.endsWith("x"), y = field.id.endsWith("y"), z = field.id.endsWith("z");
		String id = field.id.substring(0, field.id.length() - 1);
		//
		float diffo = polis.get(0).getFloat(id, x, y, z);
		for(int i = 0; i < polis.size(); i++){
			if(i == 0){
				polis.get(i).apply(id, field.getFloatValue(), x, y, z);
			}
			else{
				float diff = polis.get(i).getFloat(id, x, y, z) - diffo;
				polis.get(i).apply(id, field.getFloatValue() + diff, x, y, z);
			}
		}
		return true;
	}

	public void add(PolygonWrapper shape, String group, boolean clear){
		try{
			if(compound.isEmpty() && group == null) compound.put("group0", new TurboList("group0"));
			if(group != null && !compound.containsKey(group)) compound.put(group, new TurboList(group));
			TurboList list = (group == null ? compound.containsKey("body") ? compound.get("body") : (TurboList)compound.values().toArray()[0] : compound.get(group));
			if(clear){ clearSelection(); } shape.selected = true; list.add(shape); shape.setList(list); shape.recompile(); this.updateFields();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public TreeMap<String, TurboList> getCompound(){
		return compound;
	}
	
	public PolygonWrapper getFirstSelection(){
		for(TurboList list : compound.values()){
			if(list.selected){ list.get(0); }
			else{
				for(PolygonWrapper poly : list){
					if(poly.selected) return poly;
				}
			}
		}
		return null;
	}
	
	public String getFirstSelectedGroupName(){
		for(TurboList list : compound.values()){
			if(list.selected){ return list.id; }
			else{
				for(PolygonWrapper poly : list){
					if(poly.selected) return list.id;
				}
			}
		}
		return "no polygon selected";
	}
	
	public TurboList getFirstSelectedGroup(){
		for(TurboList list : compound.values()){
			if(list.selected){ return list; }
			else{
				for(PolygonWrapper poly : list){
					if(poly.selected) return list;
				}
			}
		}
		return null;
	}
	
	public void updateFields(){
		try{
			if(FMTB.get() == null || FMTB.get().UI == null || !FMTB.get().UI.hasElement("general_editor")) return; FMTB.get().setTitle(this.name);
			PolygonWrapper poly = getFirstSelection();
			//ouch, I forgot not keeping a secondary "selection" list doesn't also save which was selected first...
			if(poly == null){
				Editor.getGlobalField("sizex").applyChange(0);
				Editor.getGlobalField("sizey").applyChange(0);
				Editor.getGlobalField("sizez").applyChange(0);
				//
				Editor.getGlobalField("posx").applyChange(0);
				Editor.getGlobalField("posy").applyChange(0);
				Editor.getGlobalField("posz").applyChange(0);
				//
				Editor.getGlobalField("offx").applyChange(0);
				Editor.getGlobalField("offy").applyChange(0);
				Editor.getGlobalField("offz").applyChange(0);
				//
				Editor.getGlobalField("rotx").applyChange(0);
				Editor.getGlobalField("roty").applyChange(0);
				Editor.getGlobalField("rotz").applyChange(0);
				//
				Editor.getGlobalField("texx").applyChange(0);
				Editor.getGlobalField("texy").applyChange(0);
				//
				Editor.getGlobalField("group").setText("none", true);
				Editor.getGlobalField("boxname").setText("no polygon selected", true);
				Editor.getGlobalField("boxtype").setText("no polygon selected", true);
			}
			else{
				Editor.getGlobalField("sizex").applyChange(poly.getFloat("size", true, false, false));
				Editor.getGlobalField("sizey").applyChange(poly.getFloat("size", false, true, false));
				Editor.getGlobalField("sizez").applyChange(poly.getFloat("size", false, false, true));
				//
				Editor.getGlobalField("posx").applyChange(poly.getFloat("pos", true, false, false));
				Editor.getGlobalField("posy").applyChange(poly.getFloat("pos", false, true, false));
				Editor.getGlobalField("posz").applyChange(poly.getFloat("pos", false, false, true));
				//
				Editor.getGlobalField("offx").applyChange(poly.getFloat("off", true, false, false));
				Editor.getGlobalField("offy").applyChange(poly.getFloat("off", false, true, false));
				Editor.getGlobalField("offz").applyChange(poly.getFloat("off", false, false, true));
				//
				Editor.getGlobalField("rotx").applyChange(poly.getFloat("rot", true, false, false));
				Editor.getGlobalField("roty").applyChange(poly.getFloat("rot", false, true, false));
				Editor.getGlobalField("rotz").applyChange(poly.getFloat("rot", false, false, true));
				//
				Editor.getGlobalField("texx").applyChange(poly.getFloat("tex", true, false, false));
				Editor.getGlobalField("texy").applyChange(poly.getFloat("tex", false, true, false));
				//
				Editor.getGlobalField("group").setText(this.getFirstSelectedGroupName(), true);
				Editor.getGlobalField("boxname").setText(poly.name == null ? "unnamed" : poly.name, true);
				Editor.getGlobalField("boxtype").setText(poly.getType().toString().toLowerCase(), true);
			}
			if(poly == null || !poly.getType().isShapebox()){
				Editor.getGlobalField("cor0x").applyChange(0);
				Editor.getGlobalField("cor0y").applyChange(0);
				Editor.getGlobalField("cor0z").applyChange(0);
				//
				Editor.getGlobalField("cor1x").applyChange(0);
				Editor.getGlobalField("cor1y").applyChange(0);
				Editor.getGlobalField("cor1z").applyChange(0);
				//
				Editor.getGlobalField("cor2x").applyChange(0);
				Editor.getGlobalField("cor2y").applyChange(0);
				Editor.getGlobalField("cor2z").applyChange(0);
				//
				Editor.getGlobalField("cor3x").applyChange(0);
				Editor.getGlobalField("cor3y").applyChange(0);
				Editor.getGlobalField("cor3z").applyChange(0);
				//
				Editor.getGlobalField("cor4x").applyChange(0);
				Editor.getGlobalField("cor4y").applyChange(0);
				Editor.getGlobalField("cor4z").applyChange(0);
				//
				Editor.getGlobalField("cor5x").applyChange(0);
				Editor.getGlobalField("cor5y").applyChange(0);
				Editor.getGlobalField("cor5z").applyChange(0);
				//
				Editor.getGlobalField("cor6x").applyChange(0);
				Editor.getGlobalField("cor6y").applyChange(0);
				Editor.getGlobalField("cor6z").applyChange(0);
				//
				Editor.getGlobalField("cor7x").applyChange(0);
				Editor.getGlobalField("cor7y").applyChange(0);
				Editor.getGlobalField("cor7z").applyChange(0);
				//
				/*Editor.getGlobalField("face0x").applyChange(0);
				Editor.getGlobalField("face0y").applyChange(0);
				Editor.getGlobalField("face0z").applyChange(0);
				Editor.getGlobalField("face1x").applyChange(0);
				Editor.getGlobalField("face1y").applyChange(0);
				Editor.getGlobalField("face1z").applyChange(0);*/
			}
			else{
				Editor.getGlobalField("cor0x").applyChange(poly.getFloat("cor0", true, false, false));
				Editor.getGlobalField("cor0y").applyChange(poly.getFloat("cor0", false, true, false));
				Editor.getGlobalField("cor0z").applyChange(poly.getFloat("cor0", false, false, true));
				//
				Editor.getGlobalField("cor1x").applyChange(poly.getFloat("cor1", true, false, false));
				Editor.getGlobalField("cor1y").applyChange(poly.getFloat("cor1", false, true, false));
				Editor.getGlobalField("cor1z").applyChange(poly.getFloat("cor1", false, false, true));
				//
				Editor.getGlobalField("cor2x").applyChange(poly.getFloat("cor2", true, false, false));
				Editor.getGlobalField("cor2y").applyChange(poly.getFloat("cor2", false, true, false));
				Editor.getGlobalField("cor2z").applyChange(poly.getFloat("cor2", false, false, true));
				//
				Editor.getGlobalField("cor3x").applyChange(poly.getFloat("cor3", true, false, false));
				Editor.getGlobalField("cor3y").applyChange(poly.getFloat("cor3", false, true, false));
				Editor.getGlobalField("cor3z").applyChange(poly.getFloat("cor3", false, false, true));
				//
				Editor.getGlobalField("cor4x").applyChange(poly.getFloat("cor4", true, false, false));
				Editor.getGlobalField("cor4y").applyChange(poly.getFloat("cor4", false, true, false));
				Editor.getGlobalField("cor4z").applyChange(poly.getFloat("cor4", false, false, true));
				//
				Editor.getGlobalField("cor5x").applyChange(poly.getFloat("cor5", true, false, false));
				Editor.getGlobalField("cor5y").applyChange(poly.getFloat("cor5", false, true, false));
				Editor.getGlobalField("cor5z").applyChange(poly.getFloat("cor5", false, false, true));
				//
				Editor.getGlobalField("cor6x").applyChange(poly.getFloat("cor6", true, false, false));
				Editor.getGlobalField("cor6y").applyChange(poly.getFloat("cor6", false, true, false));
				Editor.getGlobalField("cor6z").applyChange(poly.getFloat("cor6", false, false, true));
				//
				Editor.getGlobalField("cor7x").applyChange(poly.getFloat("cor7", true, false, false));
				Editor.getGlobalField("cor7y").applyChange(poly.getFloat("cor7", false, true, false));
				Editor.getGlobalField("cor7z").applyChange(poly.getFloat("cor7", false, false, true));
				//
				/*Editor.getGlobalField("face0x").applyChange(poly.getFloat("face0", true, false, false));
				Editor.getGlobalField("face0y").applyChange(poly.getFloat("face0", false, true, false));
				Editor.getGlobalField("face0z").applyChange(poly.getFloat("face0", false, false, true));
				Editor.getGlobalField("face1x").applyChange(poly.getFloat("face1", true, false, false));
				Editor.getGlobalField("face1y").applyChange(poly.getFloat("face1", false, true, false));
				Editor.getGlobalField("face1z").applyChange(poly.getFloat("face1", false, false, true));*/
			}
			if(poly == null || !poly.getType().isCylinder()){
				Editor.getGlobalField("cyl0x").applyChange(0); Editor.getGlobalField("cyl0y").applyChange(0); Editor.getGlobalField("cyl0z").applyChange(0);
				Editor.getGlobalField("cyl1x").applyChange(0); Editor.getGlobalField("cyl1y").applyChange(0); Editor.getGlobalField("cyl1z").applyChange(0);
				Editor.getGlobalField("cyl2x").applyChange(0); Editor.getGlobalField("cyl2y").applyChange(0); //Editor.getGlobalField("cyl2z").applyChange(0);
				Editor.getGlobalField("cyl3x").applyChange(0); Editor.getGlobalField("cyl3y").applyChange(0); Editor.getGlobalField("cyl3z").applyChange(0);
			}
			else{
				Editor.getGlobalField("cyl0x").applyChange(poly.getFloat("cyl0", true, false, false));
				Editor.getGlobalField("cyl0y").applyChange(poly.getFloat("cyl0", false, true, false));
				Editor.getGlobalField("cyl0z").applyChange(poly.getFloat("cyl0", false, false, true));
				Editor.getGlobalField("cyl1x").applyChange(poly.getFloat("cyl1", true, false, false));
				Editor.getGlobalField("cyl1y").applyChange(poly.getFloat("cyl1", false, true, false));
				Editor.getGlobalField("cyl1z").applyChange(poly.getFloat("cyl1", false, false, true));
				Editor.getGlobalField("cyl2x").applyChange(poly.getFloat("cyl2", true, false, false));
				Editor.getGlobalField("cyl2y").applyChange(poly.getFloat("cyl2", false, true, false));
				//Editor.getGlobalField("cyl2z").applyChange(poly.getFloat("cyl2", false, false, true));
				Editor.getGlobalField("cyl3x").applyChange(poly.getFloat("cyl3", true, false, false));
				Editor.getGlobalField("cyl3y").applyChange(poly.getFloat("cyl3", false, true, false));
				Editor.getGlobalField("cyl3z").applyChange(poly.getFloat("cyl3", false, false, true));
			}
			//
			if(poly == null || !poly.getType().isTexRectB()){
				for(int i = 0; i < 6; i++){
					Editor.getGlobalField("texpos" + i + "sx").applyChange(0f);
					Editor.getGlobalField("texpos" + i + "sy").applyChange(0f);
					Editor.getGlobalField("texpos" + i + "ex").applyChange(0f);
					Editor.getGlobalField("texpos" + i + "ey").applyChange(0f);
				}
			}
			else{
				for(int i = 0; i < 6; i++){
					Editor.getGlobalField("texpos" + i + "sx").applyChange(poly.getFloat("texpos" + i + "s", true, false, false));
					Editor.getGlobalField("texpos" + i + "sy").applyChange(poly.getFloat("texpos" + i + "s", false, true, false));
					Editor.getGlobalField("texpos" + i + "ex").applyChange(poly.getFloat("texpos" + i + "e", true, false, false));
					Editor.getGlobalField("texpos" + i + "ey").applyChange(poly.getFloat("texpos" + i + "e", false, true, false));
				}
			}
			//
			if(poly == null || !poly.getType().isTexRectA()){
				for(int i = 0; i < 6; i++){
					for(int j = 0; j < 8; j++){
						if(j % 2 == 0){ Editor.getGlobalField("texpos" + i + ":" + j + "x").applyChange(0f); }
						else{ Editor.getGlobalField("texpos" + i + ":" + j + "y").applyChange(0f); }
					}
				}
			}
			else{
				//for(Element field : Editor.getGlobalFields()){ Print.console(field.id); }
				for(int i = 0; i < 6; i++){
					for(int j = 0; j < 8; j++){
						if(j % 2 == 0){
							Editor.getGlobalField("texpos" + i + ":" + j + "x").applyChange(poly.getFloat("texpos" + i + ":" + j, true, false, false));
						}
						else{
							Editor.getGlobalField("texpos" + i + ":" + j + "y").applyChange(poly.getFloat("texpos" + i + ":" + j, false, true, false));
						}
					}
				}
			}
			TurboList list = this.getFirstSelectedGroup();
			if(list == null){
				Editor.getGlobalField("group_rgb0").applyChange(0);
				Editor.getGlobalField("group_rgb1").applyChange(0);
				Editor.getGlobalField("group_rgb2").applyChange(0);
				Editor.getGlobalField("groupname").setText("no polygon selected", true);
			}
			else{
				byte[] arr = list.color == null ? RGB.WHITE.toByteArray() : list.color.toByteArray();
				Editor.getGlobalField("group_rgb0").applyChange(arr[0] + 128);
				Editor.getGlobalField("group_rgb1").applyChange(arr[1] + 128);
				Editor.getGlobalField("group_rgb2").applyChange(arr[2] + 128);
				Editor.getGlobalField("groupname").setText(list.id, true);
			};
			//
			Editor.getGlobalField("model_posx").applyChange(pos == null ? 0 : pos.xCoord);
			Editor.getGlobalField("model_posy").applyChange(pos == null ? 0 : pos.yCoord);
			Editor.getGlobalField("model_posz").applyChange(pos == null ? 0 : pos.zCoord);
			Editor.getGlobalField("model_rotx").applyChange(rot == null ? 0 : rot.xCoord);
			Editor.getGlobalField("model_roty").applyChange(rot == null ? 0 : rot.yCoord);
			Editor.getGlobalField("model_rotz").applyChange(rot == null ? 0 : rot.zCoord);
			Editor.getGlobalField("model_texx").applyChange(this.textureX);
			Editor.getGlobalField("model_texy").applyChange(this.textureY);
			Editor.getGlobalField("model_name").setText(this.name, true);
			Editor.getGlobalField("multiplicator").applyChange(rate);
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	/*private float round(double df){
        BigDecimal deci = new BigDecimal(Float.toString((float)df));
        deci = deci.setScale(3, BigDecimal.ROUND_HALF_UP);
        return deci.floatValue();
	}*/

	public float multiply(float flea){
		return rate = (rate *= flea) < 0.01f ? 0.01f : rate > 1000 ? 1000 : rate;
	}

	public void changeGroupOfSelected(ArrayList<PolygonWrapper> polis2, String id){
		ArrayList<PolygonWrapper> polis = polis2 == null ? this.getSelected() : polis2;
		if(polis.isEmpty()) return;
		TurboList list = compound.get(id); if(list == null) return;
		polis.forEach(poly -> {
			if(poly.getTurboList() != null) poly.getTurboList().remove(poly);
		});
		polis.forEach(poly -> {
			list.add(poly); poly.setList(list);
		});
		this.updateFields();
	}

	public void changeGroupOfSelected(int offset){
		ArrayList<PolygonWrapper> polis = this.getSelected();
		if(polis.isEmpty()) return;
		String current = polis.get(0).getTurboList().id; int index = offset;
		for(String key : compound.keySet()){ if(key.equals(current)) break; else index++; }
		if(index >= compound.size()) index -= compound.size(); if(index < 0) index = 0;
		changeGroupOfSelected(polis, compound.keySet().toArray(new String[0])[index]);
	}

	public void changeTypeOfSelected(ArrayList<PolygonWrapper> selected, String text){
		ShapeType type = ShapeType.get(text);
		if(type == null){ FMTB.showDialogbox("Type not found!\n[" + text.toLowerCase() + "]", null, "ok", null, DialogBox.NOTHING); return; }
		int failed = 0; ShapeType lastfail = null;
		for(PolygonWrapper sel : selected){
			if(sel.getType().getConversionGroup().equals(type.getConversionGroup())){
				PolygonWrapper conv = sel.convertTo(type);
				if(conv != null){
					sel.getTurboList().remove(sel);
					sel.getTurboList().add(conv);
					conv.recompile(); conv.selected = true;
				}
			} else { failed++; lastfail = sel.getType(); }
		}
		if(failed > 0){
			FMTB.showDialogbox(failed + " shape(s) skipped!\n" + type.name().toLowerCase() + " !> " + lastfail.name().toLowerCase(), null, "ok", null, DialogBox.NOTHING);
		}
		this.updateFields();
	}

	public void changeTypeOfSelected(ArrayList<PolygonWrapper> selected, int offset){
		if(selected == null || selected.isEmpty()) return;
		int i = selected.get(0).getType().ordinal() + offset;
		if(i >= ShapeType.values().length) i = 0;
		if(i < 0) i = ShapeType.values().length - 1;
		this.changeTypeOfSelected(selected, ShapeType.values()[i].name());
	}
	
	public long countTotalMRTs(){
		long i = 0; for(TurboList list : compound.values()) i += list.size(); return i;
	}

	public void setTexture(String string){
		this.texture = string; this.compound.values().forEach(turbo -> turbo.forEach(poly -> poly.recompile()));
	}

	public int getDirectlySelectedGroupsAmount(){
		int i = 0; for(TurboList list : compound.values()) if(list.selected) i++; return i;
	}

	public ArrayList<TurboList> getDirectlySelectedGroups(){
		ArrayList<TurboList> array = new ArrayList<>();
		for(TurboList list : compound.values()) if(list.selected) array.add(list);
		return array;
	}

	public void copyAndSelect(){
		ArrayList<PolygonWrapper> list = this.getSelected(), newlist = new ArrayList<>();
		for(PolygonWrapper wrapper : list){ newlist.add(wrapper.clone()); } this.clearSelection();
		for(PolygonWrapper wrapper : newlist){ this.add(wrapper, "clipboard", false); } return;
	}

	public void flipShapeboxes(int axis){
		List<PolygonWrapper> wrappers = this.getSelected().stream().filter(pre -> pre.getType().isShapebox()).collect(Collectors.toList());
		ShapeboxWrapper shapebox = null;
		for(PolygonWrapper wrapper : wrappers){
			Vec3f[] copy = new Vec3f[8]; shapebox = (ShapeboxWrapper)wrapper;
			copy[0] = shapebox.cor0; copy[1] = shapebox.cor1; copy[2] = shapebox.cor2; copy[3] = shapebox.cor3;
			copy[4] = shapebox.cor4; copy[5] = shapebox.cor5; copy[6] = shapebox.cor6; copy[7] = shapebox.cor7;
			switch(axis){//corner data from golddolphinskb
				case 0:{
					shapebox.cor0 = copy[3]; shapebox.cor1 = copy[2]; shapebox.cor2 = copy[1]; shapebox.cor3 = copy[0];
					shapebox.cor4 = copy[7]; shapebox.cor5 = copy[6]; shapebox.cor6 = copy[5]; shapebox.cor7 = copy[4];
					break;
				}
				case 1:{
					shapebox.cor0 = copy[4]; shapebox.cor1 = copy[5]; shapebox.cor2 = copy[6]; shapebox.cor3 = copy[7];
					shapebox.cor4 = copy[0]; shapebox.cor5 = copy[1]; shapebox.cor6 = copy[2]; shapebox.cor7 = copy[3];
					break;
				}
				case 2:{
					shapebox.cor0 = copy[1]; shapebox.cor1 = copy[0]; shapebox.cor2 = copy[3]; shapebox.cor3 = copy[2];
					shapebox.cor4 = copy[5]; shapebox.cor5 = copy[4]; shapebox.cor6 = copy[7]; shapebox.cor7 = copy[6];
					break;
				}
			} shapebox.recompile(); continue;
		} this.updateFields(); return;
	}

	public void deleteSelected(){
		FMTB.showDialogbox("Are you sure to\ndelete all Selected?", "Yes!", "Cancel!", () -> {
			ArrayList<PolygonWrapper> wrapp = this.getSelected();
			for(PolygonWrapper wrapper : wrapp){
				wrapper.getTurboList().remove(wrapper);
			}
		}, DialogBox.NOTHING);
	}

	public PolygonWrapper getLastSelected(){
		return lastselected;
	}

}
