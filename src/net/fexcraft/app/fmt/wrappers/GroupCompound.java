package net.fexcraft.app.fmt.wrappers;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.lwjgl.opengl.GL11;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.UserInterpanels.Field;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.app.fmt.ui.editor.ModelGroupEditor;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.ui.tree.SubTreeGroup;
import net.fexcraft.app.fmt.ui.tree.TreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class GroupCompound {
	
	public int textureSizeX = 256, textureSizeY = 256, textureScale = 1;
	public ArrayList<String> creators = new ArrayList<>();
	private GroupList groups = new GroupList();
	public PolygonWrapper lastselected;
	public File file, origin;
	public float rate = 1f;
	public String texture;
	public String name;
	//
	public static long SELECTED_POLYGONS;
	public boolean visible = true, minimized;
	public Vec3f pos, rot, scale;
	public TreeGroup button;
	
	public GroupCompound(File origin){
		this.origin = origin; name = "unnamed model";
		recompile(); this.updateFields();
		if(Trees.helper != null) button = new TreeGroup(Trees.helper, this);
	}
	
	public final void initButton(){ if(button == null) button = new TreeGroup(Trees.helper, this); };

	public void recompile(){
		for(TurboList list : groups) list.recompile();
	}

	public void render(){
		if(!visible) return;
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
				groups.forEach(elm -> elm.render(false));
			}
			else{
				groups.forEach(elm -> elm.renderPicking());
			}
			RayCoastAway.doTest(false);
		}
		else{
			if(Settings.preview_colorpicker()){
				groups.forEach(elm -> elm.renderPicking());
			}
			else{
				//TextureManager.bindTexture(texture == null ? "blank" : texture);
				groups.forEach(elm -> { TextureManager.bindTexture(elm.getApplicableTexture(this)); elm.render(true); });
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				groups.forEach(elm -> elm.renderLines());
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
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
	
	public static final String temptexid = "./temp/calculation_texture";
	
	private String getTempTex(){
		Texture tex = TextureManager.getTexture(temptexid, true);
		int texX = this.textureSizeX * this.textureScale, texY = this.textureSizeY * this.textureScale;
		if(tex == null || (tex.getImage().getWidth() != texX || tex.getImage().getHeight() != texY)){
			if(texX >= 8192 || texY >= 8192){ /*//TODO*/ }
			else{
				BufferedImage image = null;
				if(tex == null){
					image = new BufferedImage(texX, texY, BufferedImage.TYPE_INT_ARGB);
				}
				else{
					tex.resize(texX, texY, null); image = tex.getImage();
				}
				int lastint = 0;
				for(int x = 0; x < texX; x++){
					for(int y = 0; y < texY; y++){
						image.setRGB(x, y, new Color(lastint).getRGB()); lastint++;
					}
				}
				if(tex == null){
					TextureManager.loadTextureFromZip(image, temptexid, false, true);
				}
				else{
					tex.rebind(); TextureManager.saveTexture(temptexid);
				}
			}
		}
		return temptexid;
	}
	
	public final ArrayList<PolygonWrapper> getSelected(){
		ArrayList<PolygonWrapper> polis = new ArrayList<>();
		for(TurboList list : groups){
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
		for(TurboList list : groups){ list.selected = false; list.button.updateColor(); for(PolygonWrapper wrapper : list) wrapper.selected = false; } SELECTED_POLYGONS = 0;
	}

	public boolean updateValue(Field field, String id, boolean positive){
		ArrayList<PolygonWrapper> polis = this.getSelected(); if(polis.isEmpty()) return false;
		boolean x = id.endsWith("x"), y = id.endsWith("y"), z = id.endsWith("z");
		id = id.substring(0, id.length() - 1);
		for(int i = 0; i < polis.size(); i++){
			float f = field.tryAdd(polis.get(i).getFloat(id, x, y, z), positive, rate);
			if(i == 0){
				if(polis.get(i).apply(id, f, x, y, z)){
					field.apply(f);
				}
			}
			else{
				polis.get(i).apply(id, f, x, y, z);
			}
		}
		return true;
	}
	
	public boolean updateValue(Field field, String id){
		ArrayList<PolygonWrapper> polis = this.getSelected(); if(polis.isEmpty()) return false;
		boolean x = id.endsWith("x"), y = id.endsWith("y"), z = id.endsWith("z");
		id = id.substring(0, id.length() - 1);
		//
		float diffo = polis.get(0).getFloat(id, x, y, z);
		for(int i = 0; i < polis.size(); i++){
			if(i == 0){
				polis.get(i).apply(id, field.getValue(), x, y, z);
			}
			else{
				float diff = polis.get(i).getFloat(id, x, y, z) - diffo;
				polis.get(i).apply(id, field.getValue() + diff, x, y, z);
			}
		}
		return true;
	}

	public void add(PolygonWrapper shape, String group, boolean clear){
		try{
			if(groups.isEmpty() && group == null) groups.add(new TurboList("group0"));
			if(group != null && !groups.contains(group)) groups.add(new TurboList(group));
			TurboList list = (group == null ? groups.contains("body") ? groups.get("body") : groups.get(0) : groups.get(group));
			if(clear){ clearSelection(); } shape.selected = true; SELECTED_POLYGONS += 1; list.add(shape); shape.recompile(); this.updateFields();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}

	public GroupList getGroups(){
		return groups;
	}
	
	public PolygonWrapper getFirstSelection(){
		for(TurboList list : groups){
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
		for(TurboList list : groups){
			if(list.selected){ return list.id; }
			else{
				for(PolygonWrapper poly : list){
					if(poly.selected) return list.id;
				}
			}
		}
		return FMTB.NO_POLYGON_SELECTED;
	}
	
	public TurboList getFirstSelectedGroup(){
		for(TurboList list : groups){
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
		if(FMTB.get() == null || FMTB.frame == null) return; FMTB.get().setTitle(this.name);
		PolygonWrapper poly = getFirstSelection();
		if(poly == null){
			GeneralEditor.size_x.apply(0);
			GeneralEditor.size_y.apply(0);
			GeneralEditor.size_z.apply(0);
			//
			GeneralEditor.pos_x.apply(0);
			GeneralEditor.pos_y.apply(0);
			GeneralEditor.pos_z.apply(0);
			//
			GeneralEditor.off_x.apply(0);
			GeneralEditor.off_y.apply(0);
			GeneralEditor.off_z.apply(0);
			//
			GeneralEditor.rot_x.apply(0);
			GeneralEditor.rot_y.apply(0);
			GeneralEditor.rot_z.apply(0);
			//
			GeneralEditor.texture_x.apply(0);
			GeneralEditor.texture_y.apply(0);
			//
			GeneralEditor.polygon_group.setSelected("> new group <", true);
			GeneralEditor.polygon_name.getTextState().setText(FMTB.NO_POLYGON_SELECTED);
			GeneralEditor.polygon_type.setSelected("box", true);
		}
		else{
			GeneralEditor.size_x.apply(poly.getFloat("size", true, false, false));
			GeneralEditor.size_y.apply(poly.getFloat("size", false, true, false));
			GeneralEditor.size_z.apply(poly.getFloat("size", false, false, true));
			//
			GeneralEditor.pos_x.apply(poly.getFloat("pos", true, false, false));
			GeneralEditor.pos_y.apply(poly.getFloat("pos", false, true, false));
			GeneralEditor.pos_z.apply(poly.getFloat("pos", false, false, true));
			//
			GeneralEditor.off_x.apply(poly.getFloat("off", true, false, false));
			GeneralEditor.off_y.apply(poly.getFloat("off", false, true, false));
			GeneralEditor.off_z.apply(poly.getFloat("off", false, false, true));
			//
			GeneralEditor.rot_x.apply(poly.getFloat("rot", true, false, false));
			GeneralEditor.rot_y.apply(poly.getFloat("rot", false, true, false));
			GeneralEditor.rot_z.apply(poly.getFloat("rot", false, false, true));
			//
			GeneralEditor.texture_x.apply(poly.getFloat("tex", true, false, false));
			GeneralEditor.texture_y.apply(poly.getFloat("tex", false, true, false));
			//
			GeneralEditor.polygon_group.setSelected(this.getFirstSelectedGroupName(), true);
			GeneralEditor.polygon_name.getTextState().setText(poly.name == null ? "unnamed" : poly.name);
			GeneralEditor.polygon_type.setSelected(poly.getType().toString().toLowerCase(), true);
		}
		if(poly == null || !poly.getType().isShapebox()){
			for(int i = 0; i < 8; i++){
				GeneralEditor.corner_x[i].apply(0);
				GeneralEditor.corner_y[i].apply(0);
				GeneralEditor.corner_z[i].apply(0);
			}
		}
		else{
			for(int i = 0; i < 8; i++){
				GeneralEditor.corner_x[i].apply(poly.getFloat("cor" + i, true, false, false));
				GeneralEditor.corner_y[i].apply(poly.getFloat("cor" + i, false, true, false));
				GeneralEditor.corner_z[i].apply(poly.getFloat("cor" + i, false, false, true));
			}
		}
		if(poly == null || !poly.getType().isCylinder()){
			GeneralEditor.cyl0_x.apply(0); GeneralEditor.cyl0_y.apply(0); GeneralEditor.cyl0_z.apply(0);
			GeneralEditor.cyl1_x.apply(0); GeneralEditor.cyl1_y.apply(0); GeneralEditor.cyl1_z.apply(0);
			GeneralEditor.cyl2_x.apply(0); GeneralEditor.cyl2_y.apply(0);
			GeneralEditor.cyl3_x.apply(0); GeneralEditor.cyl3_y.apply(0); GeneralEditor.cyl3_z.apply(0);
			GeneralEditor.cyl4_x.apply(0); GeneralEditor.cyl4_y.apply(0);
			GeneralEditor.cyl5_x.apply(0); GeneralEditor.cyl5_y.apply(0);
			GeneralEditor.cyl6_x.apply(0); GeneralEditor.cyl6_y.apply(0); GeneralEditor.cyl6_z.apply(0);
			GeneralEditor.cyl7_x.apply(0); GeneralEditor.cyl7_y.apply(0); GeneralEditor.cyl7_z.apply(0);
		}
		else{
			GeneralEditor.cyl0_x.apply(poly.getFloat("cyl0", true, false, false));
			GeneralEditor.cyl0_y.apply(poly.getFloat("cyl0", false, true, false));
			GeneralEditor.cyl0_z.apply(poly.getFloat("cyl0", false, false, true));
			GeneralEditor.cyl1_x.apply(poly.getFloat("cyl1", true, false, false));
			GeneralEditor.cyl1_y.apply(poly.getFloat("cyl1", false, true, false));
			GeneralEditor.cyl1_z.apply(poly.getFloat("cyl1", false, false, true));
			GeneralEditor.cyl2_x.apply(poly.getFloat("cyl2", true, false, false));
			GeneralEditor.cyl2_y.apply(poly.getFloat("cyl2", false, true, false));
			GeneralEditor.cyl3_x.apply(poly.getFloat("cyl3", true, false, false));
			GeneralEditor.cyl3_y.apply(poly.getFloat("cyl3", false, true, false));
			GeneralEditor.cyl3_z.apply(poly.getFloat("cyl3", false, false, true));
			//
			GeneralEditor.cyl4_x.apply(poly.getFloat("cyl4", true, false, false));
			GeneralEditor.cyl4_y.apply(poly.getFloat("cyl4", false, true, false));
			GeneralEditor.cyl5_x.apply(poly.getFloat("cyl5", true, false, false));
			GeneralEditor.cyl5_y.apply(poly.getFloat("cyl5", false, true, false));
			//
			GeneralEditor.cyl6_x.apply(poly.getFloat("cyl6", true, false, false));
			GeneralEditor.cyl6_y.apply(poly.getFloat("cyl6", false, true, false));
			GeneralEditor.cyl6_z.apply(poly.getFloat("cyl6", false, false, true));
			//
			GeneralEditor.cyl7_x.apply(poly.getFloat("cyl7", true, false, false));
			GeneralEditor.cyl7_y.apply(poly.getFloat("cyl7", false, true, false));
			GeneralEditor.cyl7_z.apply(poly.getFloat("cyl7", false, false, true));
		}
		//
		if(poly == null || !poly.getType().isTexRectB()){
			for(int i = 0; i < 6; i++){
				GeneralEditor.texrect_b[i][0].apply(0);
				GeneralEditor.texrect_b[i][1].apply(0);
				GeneralEditor.texrect_b[i][2].apply(0);
				GeneralEditor.texrect_b[i][3].apply(0);
			}
		}
		else{
			for(int i = 0; i < 6; i++){
				GeneralEditor.texrect_b[i][0].apply(poly.getFloat("texpos" + i + "s", true, false, false));
				GeneralEditor.texrect_b[i][1].apply(poly.getFloat("texpos" + i + "s", false, true, false));
				GeneralEditor.texrect_b[i][2].apply(poly.getFloat("texpos" + i + "e", true, false, false));
				GeneralEditor.texrect_b[i][3].apply(poly.getFloat("texpos" + i + "e", false, true, false));
			}
		}
		//
		if(poly == null || !poly.getType().isTexRectA()){
			for(int i = 0; i < 6; i++) for(int j = 0; j < 8; j++) GeneralEditor.texrect_a[i][j].apply(0);
		}
		else{
			for(int i = 0; i < 6; i++){
				for(int j = 0; j < 8; j++){
					if(j % 2 == 0) GeneralEditor.texrect_a[i][j].apply(poly.getFloat("texpos" + i + ":" + j + "x", true, false, false));
					else GeneralEditor.texrect_a[i][j].apply(poly.getFloat("texpos" + i + ":" + j + "y", false, true, false));
				}
			}
		}
		if(poly == null || !poly.getType().isMarker()){
			GeneralEditor.marker_color.apply(0xffffff);
			GeneralEditor.marker_biped.apply(0);
			GeneralEditor.marker_scale.apply(0);
			GeneralEditor.marker_angle.apply(0);
		}
		else{
			GeneralEditor.marker_color.apply(poly.getFloat("marker_color", true, false, false));
			GeneralEditor.marker_biped.apply(poly.getFloat("marker_biped", true, false, false));
			GeneralEditor.marker_scale.apply(poly.getFloat("marker_scale", true, false, false));
			GeneralEditor.marker_angle.apply(poly.getFloat("marker_angle", true, false, false));
		}
		TurboList list = this.getFirstSelectedGroup();
		if(list == null){
			ModelGroupEditor.group_color.apply(0xffffff);
			ModelGroupEditor.group_name.getTextState().setText(FMTB.NO_POLYGON_SELECTED);
			ModelGroupEditor.group_texture.getTextState().setText(FMTB.NO_POLYGON_SELECTED);
			ModelGroupEditor.g_tex_x.setSelected(8f, true);
			ModelGroupEditor.g_tex_y.setSelected(8f, true);
			ModelGroupEditor.g_tex_s.setSelected(8f, true);
		}
		else{
			ModelGroupEditor.group_color.apply((list.color == null ? RGB.WHITE : list.color).packed);
			ModelGroupEditor.group_name.getTextState().setText(list.id);
			ModelGroupEditor.g_tex_x.setSelected((float)list.textureX, true);
			ModelGroupEditor.g_tex_y.setSelected((float)list.textureY, true);
			ModelGroupEditor.g_tex_s.setSelected((float)list.textureS, true);
			//
			String texname = list.getGroupTexture() + "";
			if(texname.length() > 32){ texname = texname.substring(texname.length() - 32, texname.length()); }
			ModelGroupEditor.group_texture.getTextState().setText(texname);
		};
		ModelGroupEditor.animations.refresh(list);
		//
		ModelGroupEditor.pos_x.apply(pos == null ? 0 : pos.xCoord);
		ModelGroupEditor.pos_y.apply(pos == null ? 0 : pos.yCoord);
		ModelGroupEditor.pos_z.apply(pos == null ? 0 : pos.zCoord);
		ModelGroupEditor.poss_x.apply(pos == null ? 0 : pos.xCoord * Static.sixteenth);
		ModelGroupEditor.poss_y.apply(pos == null ? 0 : pos.yCoord * Static.sixteenth);
		ModelGroupEditor.poss_z.apply(pos == null ? 0 : pos.zCoord * Static.sixteenth);
		ModelGroupEditor.rot_x.apply(pos == null ? 0 : rot.xCoord);
		ModelGroupEditor.rot_y.apply(pos == null ? 0 : rot.yCoord);
		ModelGroupEditor.rot_z.apply(pos == null ? 0 : rot.zCoord);
		ModelGroupEditor.m_tex_x.setSelected((float)textureSizeX, true);
		ModelGroupEditor.m_tex_y.setSelected((float)textureSizeY, true);
		ModelGroupEditor.m_tex_s.setSelected((float)textureScale, true);
		ModelGroupEditor.model_name.getTextState().setText(name);
		//
		String texname = this.texture + "";
		if(texname.length() > 64){ texname = texname.substring(texname.length() - 64, texname.length()); }
		ModelGroupEditor.model_texture.getTextState().setText(texname);
	}
	
	public float multiply(float flea){
		return rate = (rate *= flea) < 0.01f ? 0.01f : rate > 1000 ? 1000 : rate;
	}

	public void changeGroupOfSelected(ArrayList<PolygonWrapper> polis2, String id){
		ArrayList<PolygonWrapper> polis = polis2 == null ? this.getSelected() : polis2;
		if(polis.isEmpty()) return;
		TurboList list = groups.get(id); if(list == null) return;
		polis.forEach(poly -> {
			if(poly.getTurboList() != null) poly.getTurboList().remove(poly);
		});
		polis.forEach(poly -> {
			list.add(poly); poly.setList(list);
		});
		this.updateFields();
	}

	/*public void changeGroupOfSelected(int offset){
		ArrayList<PolygonWrapper> polis = this.getSelected();
		if(polis.isEmpty()) return;
		String current = polis.get(0).getTurboList().id; int index = offset;
		for(TurboList key : groups){ if(key.id.equals(current)) break; else index++; }
		if(index >= groups.size()) index -= groups.size(); if(index < 0) index = 0;
		changeGroupOfSelected(polis, groups.get(index).id);
	}*/

	public void changeTypeOfSelected(ArrayList<PolygonWrapper> selected, String text){
		ShapeType type = ShapeType.get(text);
		if(type == null){
			DialogBox.showOK(null, null, null, "compound.change_type.not_found", "#" + text.toLowerCase()); return;
		}
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
			DialogBox.showOK(null, null, null, "#" + Translator.format("compound.change_type.failed", failed), "#" + String.format("%s != %s", type.name().toLowerCase(), lastfail.name().toLowerCase()));
		}
		this.updateFields();
	}

	/*public void changeTypeOfSelected(ArrayList<PolygonWrapper> selected, int offset){
		if(selected == null || selected.isEmpty()) return;
		int i = selected.get(0).getType().ordinal() + offset;
		if(i >= ShapeType.values().length) i = 0;
		if(i < 0) i = ShapeType.values().length - 1;
		this.changeTypeOfSelected(selected, ShapeType.values()[i].name());
	}*/
	
	public long countTotalMRTs(){
		long i = 0; for(TurboList list : groups) i += list.size(); return i;
	}
	
	public long countSelectedMRTs(){
		long i = 0; for(TurboList list : groups){
			if(list.selected) i += list.size();
			else for(PolygonWrapper wrapper : list) if(wrapper.selected) i++;
		} return i;
	}

	public void setTexture(String string){
		this.texture = string; this.groups.forEach(turbo -> turbo.forEach(poly -> poly.recompile()));
	}

	public int getDirectlySelectedGroupsAmount(){
		int i = 0; for(TurboList list : groups) if(list.selected) i++; return i;
	}

	public ArrayList<TurboList> getDirectlySelectedGroups(){
		ArrayList<TurboList> array = new ArrayList<>();
		for(TurboList list : groups) if(list.selected) array.add(list);
		return array;
	}

	public void copyAndSelect(){
		ArrayList<PolygonWrapper> list = this.getSelected(), newlist = new ArrayList<>();
		for(PolygonWrapper wrapper : list){ newlist.add(wrapper.clone()); } this.clearSelection();
		for(PolygonWrapper wrapper : newlist){ this.add(wrapper, "clipboard", false); }
		/*Trees.polygon.reOrderGroups();*/ return;
	}

	public void flipShapeboxes(int axis){
		List<PolygonWrapper> wrappers = this.getSelected().stream().filter(pre -> pre.getType().isShapebox()).collect(Collectors.toList());
		for(PolygonWrapper wrapper : wrappers){
			if(wrapper instanceof ShapeboxWrapper){
				Vec3f[] copy = new Vec3f[8]; ShapeboxWrapper shapebox = (ShapeboxWrapper)wrapper;
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
			}
			else if(wrapper instanceof ShapeQuadWrapper){
				Vec3f[] copy = new Vec3f[4]; ShapeQuadWrapper quad = (ShapeQuadWrapper)wrapper;
				copy[0] = quad.cor0; copy[1] = quad.cor1; copy[2] = quad.cor2; copy[3] = quad.cor3;
				switch(axis){
					case 0:{
						quad.cor0 = copy[3]; quad.cor1 = copy[2]; quad.cor2 = copy[1]; quad.cor3 = copy[0];
						break;
					}
					case 1:{
						quad.cor0 = copy[0]; quad.cor1 = copy[1]; quad.cor2 = copy[2]; quad.cor3 = copy[3];
						break;
					}
					case 2:{
						quad.cor0 = copy[1]; quad.cor1 = copy[0]; quad.cor2 = copy[3]; quad.cor3 = copy[2];
						break;
					}
				} quad.recompile(); continue;
			}
		} this.updateFields(); return;
	}

	public void deleteSelected(){
		DialogBox.showYN(null, () -> {
			ArrayList<PolygonWrapper> wrapp = this.getSelected();
			for(PolygonWrapper wrapper : wrapp){
				wrapper.getTurboList().remove(wrapper);
				wrapper.button.removeFromSubTree();
			} SELECTED_POLYGONS = 0;
		}, null, "compound.delete_selected");
	}

	public PolygonWrapper getLastSelected(){
		return lastselected;
	}
	
	public int tx(TurboList list){ return list == null || list.getGroupTexture() == null ? textureSizeX : list.textureX; }
	public int ty(TurboList list){ return list == null || list.getGroupTexture() == null ? textureSizeY : list.textureY; }
	
	public static class GroupList extends ArrayList<TurboList> {
		
		@Override
		public boolean add(TurboList list){
			boolean bool = super.add(list); Editors.general.refreshGroups();
			if(bool){
				Trees.polygon.addSub(list.button.update()); Trees.polygon.reOrderGroups();
				Trees.fvtm.addSub(list.abutton.update()); Trees.fvtm.reOrderGroups();
			}
			return bool;
		}
		
		public boolean contains(String str){
			for(TurboList list : this) if(list.id.equals(str)) return true; return false;
		}
		
		public TurboList get(String str){
			for(TurboList list : this) if(list.id.equals(str)) return list; return null;
		}
		
		public TurboList remove(String str){
			TurboList list = get(str); if(list == null) return null;
			if(remove(list)){ return list; } return null;
		}
		
		@Override
		public boolean remove(Object obj){
			TurboList list = (TurboList)obj;
			if(obj instanceof TurboList){
				list.button.removeFromTree();
				list.abutton.removeFromTree();
			}
			boolean bool = super.remove(obj);
			if(list != null){
				list.button.tree().reOrderGroups();
				list.abutton.tree().reOrderGroups();
			}
			return bool;
		}
		
		@Override
		public TurboList remove(int index){
			TurboList list = get(index);
			if(list != null){
				list.button.removeFromTree();
				list.abutton.removeFromTree();
			}
			list = super.remove(index);
			list.button.tree().reOrderGroups();
			list.abutton.tree().reOrderGroups();
			return list;
		}
		
		@Override
		public void clear(){
			super.clear(); Trees.polygon.clear();
		}

		public void setAsHelperPreview(GroupCompound compound){
			for(TurboList list : this){
				list.button.removeFromTree();
				list.abutton.removeFromTree();
				list.button = null; list.abutton = null;
				list.pbutton = new SubTreeGroup(Trees.helper, list);
				list.pbutton.setRoot(compound.button);
			} compound.button.update(); Trees.polygon.reOrderGroups();
		}
		
	}

}
