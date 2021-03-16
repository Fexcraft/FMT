package net.fexcraft.app.fmt.wrappers;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.lwjgl.opengl.GL11;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.porters.JsonToFMT;
import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.ui.DialogBox.DialogTask;
import net.fexcraft.app.fmt.ui.TexViewBox;
import net.fexcraft.app.fmt.ui.editor.Editors;
import net.fexcraft.app.fmt.ui.editor.GeneralEditor;
import net.fexcraft.app.fmt.ui.editor.GroupEditor;
import net.fexcraft.app.fmt.ui.editor.ModelEditor;
import net.fexcraft.app.fmt.ui.editor.UVEditor;
import net.fexcraft.app.fmt.ui.field.Field;
import net.fexcraft.app.fmt.ui.field.NumberField;
import net.fexcraft.app.fmt.ui.tree.SubTreeGroup;
import net.fexcraft.app.fmt.ui.tree.TreeGroup;
import net.fexcraft.app.fmt.ui.tree.Trees;
import net.fexcraft.app.fmt.utils.RayCoastAway;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.utils.texture.Texture;
import net.fexcraft.app.fmt.utils.texture.TextureGroup;
import net.fexcraft.app.fmt.utils.texture.TextureManager;
import net.fexcraft.lib.common.Static;
import net.fexcraft.lib.common.json.JsonUtil;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.math.Vec3f;

public class GroupCompound {
	
	public int textureSizeX = 256, textureSizeY = 256;
	private LinkedHashMap<String, Boolean> creators = new LinkedHashMap<>();
	private ArrayList<String> authors = new ArrayList<>();
	private GroupList groups = new GroupList();
	public PolygonWrapper lastselected;
	public File file, origin;
	public ExImPorter porter;
	public float rate = 1f;
	public String name;
	public TextureGroup texgroup;
	//
	public static long SELECTED_POLYGONS;
	public boolean visible = true, minimized;
	public Vec3f pos, rot, scale;
	public TreeGroup button;
	public String helpertex;
	public boolean subhelper;
	public float opacity = 1f;
	public RGB op_color;
	public ConcurrentLinkedQueue<PolygonWrapper> detached = new ConcurrentLinkedQueue<>();
	
	public GroupCompound(File origin){
		this.origin = origin; name = "unnamed model";
		recompile();
		updateFields();
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
			/*if(pencil){
				TextureManager.bindTexture(getTempTex());
				groups.forEach(elm -> elm.render(false));
			}
			else{}*/
			GL11.glDisable(GL11.GL_TEXTURE_2D); 
			groups.forEach(elm -> elm.renderPicking());
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			RayCoastAway.doTest(false, null, false);//pencil);
		}
		else{
			if(Settings.preview_colorpicker()){
				GL11.glDisable(GL11.GL_TEXTURE_2D); 
				groups.forEach(elm -> elm.renderPicking());
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
			else{
				//TextureManager.bindTexture(texture == null ? "blank" : texture);
				groups.forEach(elm -> { 
					elm.bindApplicableTexture(this);
					elm.render(true);
				});
				GL11.glDisable(GL11.GL_TEXTURE_2D);
				groups.forEach(elm -> elm.renderLines());
				GL11.glEnable(GL11.GL_TEXTURE_2D);
			}
		}
		if(scale != null){
			GL11.glPopMatrix();
		}
		if(!detached.isEmpty() && !RayCoastAway.PICKING){
			for(PolygonWrapper poly : detached){
				if(!poly.getTurboList().visible) continue;
				poly.getTurboList().bindApplicableTexture(this);
				poly.render(true, false, false);
			}
			GL11.glDisable(GL11.GL_TEXTURE_2D);
			for(PolygonWrapper poly : detached){
				if(!poly.getTurboList().visible) continue;
				poly.renderLines(true, false, false);
			}
			GL11.glEnable(GL11.GL_TEXTURE_2D);
			detached.clear();
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
	
	public static final String temptexid = "./temp/calculation_texture_";
	
	public Texture getTempTex(PolygonWrapper wrapper){
		Texture tex = TextureManager.getTexture(temptexid + wrapper.getTextureGroup().group, true);
		if(tex == null){
			TextureGroup texgroup = wrapper.getTextureGroup();
			String texid = temptexid + wrapper.getTextureGroup().group;
			tex = TextureManager.createTexture(texid, texgroup.texture.getWidth(), texgroup.texture.getHeight());
			tex.setFile(new File(texid + ".png"));
			colortexcalc(tex);
		}
		else if(tex.getWidth() != texgroup.texture.getWidth() || tex.getHeight() != texgroup.texture.getHeight()){
			tex.resize(texgroup.texture.getWidth(), texgroup.texture.getHeight());
			colortexcalc(tex);
		}
		return tex;
	}
	
	private void colortexcalc(Texture tex){
		int lastint = 0;
		for(int x = 0; x < tex.getWidth(); x++){
			for(int y = 0; y < tex.getHeight(); y++){
				tex.set(x, y, new RGB(lastint).toByteArray());
				lastint++;
			}
		}
		tex.save();
		tex.rebind();
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
		for(TurboList list : groups){
			list.selected = false;
			list.button.updateColor();
			list.abutton.updateColor();
			for(PolygonWrapper wrapper : list){
				wrapper.selected = false;
				wrapper.button.updateColor();
			}
		}
		SELECTED_POLYGONS = 0;
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
			if(clear){
				clearSelection();
			}
			shape.selected = true;
			SELECTED_POLYGONS += 1;
			list.add(shape);
			shape.button.updateColor();
			shape.recompile();
			this.updateFields();
		}
		catch(Exception e){
			log(e);
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
			GeneralEditor.side0_x.apply(0);
			GeneralEditor.side0_y.apply(0);
			GeneralEditor.side0_z.apply(0);
			GeneralEditor.side1_x.apply(0);
			GeneralEditor.side1_y.apply(0);
			GeneralEditor.side1_z.apply(0);
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
			GeneralEditor.side0_x.apply(poly.getFloat("side0", true, false, false));
			GeneralEditor.side0_y.apply(poly.getFloat("side0", false, true, false));
			GeneralEditor.side0_z.apply(poly.getFloat("side0", false, false, true));
			GeneralEditor.side1_x.apply(poly.getFloat("side1", true, false, false));
			GeneralEditor.side1_y.apply(poly.getFloat("side1", false, true, false));
			GeneralEditor.side1_z.apply(poly.getFloat("side1", false, false, true));
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
		UVEditor.polygon_name.getTextState().setText(poly == null ? FMTB.NO_POLYGON_SELECTED : poly.name == null ? "unnamed" : poly.name);
		UVEditor.refreshEntries(poly, null);
		if(poly == null){
			UVEditor.texture_x.apply(0);
			UVEditor.texture_y.apply(0);
		}
		else{
			UVEditor.texture_x.apply(poly.getFloat("tex", true, false, false));
			UVEditor.texture_y.apply(poly.getFloat("tex", false, true, false));
		}
		//
		/*if(poly == null || !poly.getType().isTexRectB()){
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
					if(j % 2 == 0) GeneralEditor.texrect_a[i][j].apply(poly.getFloat("texpos" + i + ":" + j, true, false, false));
					else GeneralEditor.texrect_a[i][j].apply(poly.getFloat("texpos" + i + ":" + j, false, true, false));
				}
			}
		}*/
		if(poly == null || !poly.getType().isMarker()){
			GeneralEditor.marker_color.apply(0xffffff);
			GeneralEditor.marker_biped.apply(0);
			GeneralEditor.marker_scale.apply(0);
			GeneralEditor.marker_angle.apply(0);
			GeneralEditor.marker_detached.apply(0);
		}
		else{
			GeneralEditor.marker_color.apply(poly.getFloat("marker_color", true, false, false));
			GeneralEditor.marker_biped.apply(poly.getFloat("marker_biped", true, false, false));
			GeneralEditor.marker_scale.apply(poly.getFloat("marker_scale", true, false, false));
			GeneralEditor.marker_angle.apply(poly.getFloat("marker_angle", true, false, false));
			GeneralEditor.marker_detached.apply(poly.getFloat("marker_detached", true, false, false));
		}
		TurboList list = this.getFirstSelectedGroup();
		if(list == null){
			GroupEditor.group_color.apply(0xffffff);
			GroupEditor.group_name.getTextState().setText(FMTB.NO_POLYGON_SELECTED);
			GroupEditor.group_texture.setSelected(0, true);
			GroupEditor.g_tex_x.setSelected(8f, true);
			GroupEditor.g_tex_y.setSelected(8f, true);
			//GroupEditor.g_tex_s.setSelected(1f, true);
			GroupEditor.exoff_x.apply(0);
			GroupEditor.exoff_y.apply(0);
			GroupEditor.exoff_z.apply(0);
		}
		else{
			GroupEditor.group_color.apply((list.color == null ? RGB.WHITE : list.color).packed);
			GroupEditor.group_name.getTextState().setText(list.id);
			if(list.texgroup == null){
				GroupEditor.group_texture.setSelected(0, true);
			}
			else{
				GroupEditor.group_texture.setSelected(list.texgroup == null ? "none" : list.texgroup.group, true);
			}
			GroupEditor.g_tex_x.setSelected((float)list.textureX, true);
			GroupEditor.g_tex_y.setSelected((float)list.textureY, true);
			//GroupEditor.g_tex_s.setSelected((float)list.textureS, true);
			GroupEditor.exoff_x.apply(list.exportoffset == null ? 0 : list.exportoffset.xCoord);
			GroupEditor.exoff_y.apply(list.exportoffset == null ? 0 : list.exportoffset.yCoord);
			GroupEditor.exoff_z.apply(list.exportoffset == null ? 0 : list.exportoffset.zCoord);
		};
		GroupEditor.animations.refresh(list);
		//
		ModelEditor.pos_x.apply(pos == null ? 0 : pos.xCoord);
		ModelEditor.pos_y.apply(pos == null ? 0 : pos.yCoord);
		ModelEditor.pos_z.apply(pos == null ? 0 : pos.zCoord);
		ModelEditor.poss_x.apply(pos == null ? 0 : pos.xCoord * Static.sixteenth);
		ModelEditor.poss_y.apply(pos == null ? 0 : pos.yCoord * Static.sixteenth);
		ModelEditor.poss_z.apply(pos == null ? 0 : pos.zCoord * Static.sixteenth);
		ModelEditor.rot_x.apply(pos == null ? 0 : rot.xCoord);
		ModelEditor.rot_y.apply(pos == null ? 0 : rot.yCoord);
		ModelEditor.rot_z.apply(pos == null ? 0 : rot.zCoord);
		ModelEditor.scale.apply(scale == null ? 1 : scale.xCoord);
		ModelEditor.m_tex_x.setSelected((float)textureSizeX, true);
		ModelEditor.m_tex_y.setSelected((float)textureSizeY, true);
		//ModelEditor.m_tex_s.setSelected((float)textureScale, true);
		ModelEditor.model_name.getTextState().setText(name);
		//
		ModelEditor.model_texture.setSelected(FMTB.MODEL.texgroup == null ? "none" : FMTB.MODEL.texgroup.group, true);
		TexViewBox.update();
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
		long l = 0;
		for(TurboList list : groups) l += list.size();
		return l;
	}

	public long countTotalFaces(boolean visonly){
		long l = 0;
		for(TurboList list : groups){
			for(PolygonWrapper poly : list){
				l += poly.getFacesAmount(visonly);
			}
		}
		return l;
	}
	
	public long countSelectedMRTs(){
		long l = 0; for(TurboList list : groups){
			if(list.selected) l += list.size();
			else for(PolygonWrapper wrapper : list) if(wrapper.selected) l++;
		} return l;
	}

	public void setTexture(TextureGroup group){
		texgroup = group;
		this.groups.forEach(turbo -> turbo.forEach(poly -> poly.recompile()));
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

	public void flipShapeboxes(List<PolygonWrapper> list, int axis){
		List<PolygonWrapper> wrappers = list != null ? list : this.getSelected().stream().filter(pre -> pre.getType().isShapebox()).collect(Collectors.toList());
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

	public void flipBoxPosition(List<PolygonWrapper> list, int axis){
		List<PolygonWrapper> wrappers = list != null ? list : this.getSelected();
		for(PolygonWrapper wrapper : wrappers){
			if(wrapper instanceof BoxWrapper == false) continue;
			BoxWrapper box = (BoxWrapper)wrapper;
			switch(axis){
				case 0:{
					box.pos.xCoord += box.size.xCoord;
					box.pos.xCoord = -box.pos.xCoord;
					break;
				}
				case 1:{
					box.pos.yCoord += box.size.yCoord;
					box.pos.yCoord = -box.pos.yCoord;
					break;
				}
				case 2:{
					box.pos.zCoord += box.size.zCoord;
					box.pos.zCoord = -box.pos.zCoord;
					break;
				}
			}
			box.recompile();
		}
		this.updateFields();
		return;
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
	
	public int tx(TurboList list){
		return tx(list, true);
	}

	public int ty(TurboList list){
		return ty(list, true);
	}
	
	public int tx(TurboList list, boolean checktex){
		return list == null || (checktex && list.getTextureGroup() == null) ? textureSizeX : list.textureX;
	}

	public int ty(TurboList list, boolean checktex){
		return list == null || (checktex && list.getTextureGroup() == null) ? textureSizeY : list.textureY;
	}
	
	public static class GroupList extends ArrayList<TurboList> {
		
		@Override
		public boolean add(TurboList list){
			boolean bool = super.add(list);
			if(bool){
				Trees.polygon.addSub(list.button.update());
				Trees.polygon.reOrderGroups();
				Trees.fvtm.addSub(list.abutton.update());
				Trees.fvtm.reOrderGroups();
				Editors.general.refreshGroups();
			}
			return bool;
		}
		
		@Override
		public void add(int index, TurboList list){
			super.add(index, list);
			Trees.polygon.addSub(index, list.button.update());
			Trees.polygon.reOrderGroups();
			Trees.fvtm.addSub(index, list.abutton.update());
			Trees.fvtm.reOrderGroups();
			Editors.general.refreshGroups();
		}
		
		public boolean contains(String str){
			for(TurboList list : this) if(list.id.equals(str)) return true; return false;
		}
		
		public TurboList get(String str){
			for(TurboList list : this) if(list.id.equals(str)) return list; return null;
		}
		
		public TurboList remove(String str){
			TurboList list = get(str);
			if(list == null) return null;
			if(remove(list)){
				Editors.general.refreshGroups();
				return list;
			}
			return null;
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
				Editors.general.refreshGroups();
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
			Editors.general.refreshGroups();
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
			}
			compound.button.update();
			Trees.polygon.reOrderGroups();
		}
		
	}

	public void copyToClipboard(){
		Static.stop();
		ArrayList<PolygonWrapper> selected = this.getSelected();
		if(selected.isEmpty()) return;
		JsonObject obj = new JsonObject();
		obj.addProperty("origin", "fmt");
		obj.addProperty("version", FMTB.VERSION);
		obj.addProperty("model", this.name);
		obj.addProperty("type", "simple-clipboard");
		JsonArray array = new JsonArray();
		for(PolygonWrapper wrapper : selected){
			array.add(wrapper.toJson(false));
		}
		obj.add("polygons", array);
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection sel = new StringSelection(obj.toString());
		cp.setContents(sel, sel);
	}

	public void pasteFromClipboard(){
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable data = cp.getContents(null);
		if(data.isDataFlavorSupported(DataFlavor.stringFlavor)){
			try{
				String str = data.getTransferData(DataFlavor.stringFlavor).toString();
				if(!str.startsWith("{")) return;
				JsonObject obj = JsonUtil.getObjectFromString(str);
				if(!obj.has("origin") && !obj.get("origin").getAsString().equals("fmt")) return;
				if(!obj.has("type") || !obj.has("model") || !obj.has("polygons")) return;
				this.clearSelection();
				switch(obj.get("type").getAsString()){
					case "simple-clipboard":{
						boolean external = !obj.get("model").getAsString().equals(name);
						String groupto = external ? obj.get("model").getAsString() + "-cb" : "clipboard";
						DialogTask task = () -> {
							obj.get("polygons").getAsJsonArray().forEach(elm -> {
								this.add(JsonToFMT.parseWrapper(this, elm.getAsJsonObject()), groupto, false);
							});
						};
						if(external){
							DialogBox.showYN(null, task, null, "compound.simple-clipboard.external", "#ORIGIN: " + obj.get("model").getAsString());
						}
						else task.process();
						return;
					}
					default: return;
				}
			}
			catch(UnsupportedFlavorException | IOException e){
				log(e);
			}
		}
	}

	public void rectify(){
		for(TurboList list : groups){
			for(PolygonWrapper wrapper : list){
				wrapper.pos.yCoord = -wrapper.pos.yCoord + 26;
				if(wrapper instanceof BoxWrapper){
					wrapper.off.yCoord = -wrapper.off.yCoord - ((BoxWrapper)wrapper).size.yCoord;
				}
				else if(wrapper instanceof CylinderWrapper){
					CylinderWrapper cyl = (CylinderWrapper)wrapper;
					if(cyl.direction > 3){
						cyl.off.yCoord = -cyl.off.yCoord - cyl.length;
						float base = cyl.base;
						cyl.base = cyl.top;
						cyl.top = base;
					}
					if(cyl.topoff != null){
						cyl.topoff.yCoord = -cyl.topoff.yCoord;
					}
					if(cyl.toprot != null){
						cyl.toprot.yCoord = -cyl.toprot.yCoord;
						cyl.toprot.zCoord = -cyl.toprot.zCoord;
					}
				}
				else{
					wrapper.off.yCoord += -wrapper.off.yCoord;
				}
				wrapper.rot.xCoord = -wrapper.rot.xCoord;
				wrapper.rot.zCoord = -wrapper.rot.zCoord;
				if(wrapper.getType().isRectagular()){
					BoxWrapper box = (BoxWrapper)wrapper;
					if(box.getType().isShapebox()){
						flipShapeboxes(Arrays.asList(new PolygonWrapper[]{ wrapper }), 1);
					}
				}
				wrapper.recompile();
			}
		}
	}

	public void mirrorLRSelected(){
		for(TurboList list : groups){
			if(list.isEmpty()) continue;
			for(PolygonWrapper wrapper : list){
				if(!wrapper.selected && !list.selected) continue;
				wrapper.pos.zCoord = -wrapper.pos.zCoord;
				if(wrapper instanceof BoxWrapper){
					BoxWrapper box = (BoxWrapper)wrapper;
					if(box.off.zCoord != -box.size.zCoord / 2)
						wrapper.off.zCoord -= ((BoxWrapper)wrapper).size.zCoord;
				}
				else if(wrapper instanceof CylinderWrapper){
					//CylinderWrapper cyl = (CylinderWrapper)wrapper;
					//
				}
				else{
					//
				}
				wrapper.rot.yCoord = -wrapper.rot.yCoord;
				if(wrapper.getType().isRectagular()){
					BoxWrapper box = (BoxWrapper)wrapper;
					if(box.getType().isShapebox()){
						flipShapeboxes(Arrays.asList(new PolygonWrapper[]{ wrapper }), 0);
					}
				}
				wrapper.recompile();
			}
		}
	}

	/**
	 * For settings Helper Compounds "selected".
	 * @param value the new status
	 */
	public void setGroupsSelected(boolean value){
		for(TurboList list : groups) list.selected = value;
	}

	public ArrayList<String> getAuthors(){
		return authors;
	}

	public void setAuthors(ArrayList<String> array){
		creators.clear();
		authors.clear();
		for(String str : array){
			boolean locked = str.startsWith("!");
			if(locked) str = str.substring(1);
			creators.put(str, locked);
			authors.add(str);
		}
		ModelEditor.creators.refresh(creators);
	}

	public void addAuthor(String string, boolean additive, boolean lock){
		if(authors.contains(string)) return;
		if(additive){
			if(!allowed()) return;
		}
		creators.put(string, lock);
		authors.add(string);
		ModelEditor.creators.refresh();
	}

	public void remAuthor(String string){
		if(!allowed()) return;
		creators.remove(string);
		authors.remove(string);
		ModelEditor.creators.refresh();
	}
	
	private boolean allowed(){
		boolean anylocked = false;
		for(boolean bool : creators.values()){
			if(bool){
				anylocked = true;
				break;
			}
		}
		if(anylocked && (!creators.containsKey(SessionHandler.getUserName()) || !creators.get(SessionHandler.getUserName()))){
			DialogBox.showOK(null, null, null, "editor.model_group.authors.error_locked");
			return false;
		}
		return true;
	}

	public void lockAuthor(String author, boolean lock){
		if(!allowed()) return;
		creators.put(author, lock);
		ModelEditor.creators.refresh();
	}

	public Map<String, Boolean> getCreators(){
		return creators;
	}
	
	public void rescale(){
		int width = 350;
		Dialog dialog = new Dialog(translate("compound.rescale.dialog"), width, 0);
		int passed = 0;
		TurboList[] selected = new TurboList[1];
		float[] scale = new float[]{ 1f };
		dialog.setResizable(false);
		dialog.getContainer().add(new Label(translate("compound.rescale.scale"), 10, passed += 10, width - 20, 20));
		NumberField input = new NumberField(10, passed += 24, width - 20, 20);
		input.setup(0.001f, 16, true, () -> { scale[0] = input.getValue(); });
		input.apply(scale[0]);
		dialog.getContainer().add(input);
		dialog.getContainer().add(new Label(translate("compound.rescale.group"), 10, passed += 28, width - 20, 20));
		SelectBox<String> selectbox = new SelectBox<>(10, passed += 24, width - 20, 20);
		selectbox.addElement("all-groups");
		for(TurboList group : groups){
			selectbox.addElement(group.id);
		}
		selectbox.addSelectBoxChangeSelectionEventListener(listener -> {
			selected[0] = listener.getNewValue().equals("all-groups") ? null : groups.get(listener.getNewValue());
		});
		dialog.getContainer().add(selectbox);
		Label label = null;
		dialog.getContainer().add(label = new Label(translate("compound.rescale.warning0"), 10, passed += 28, width - 20, 20));
		label.getStyle().setFont("roboto-bold");
		dialog.getContainer().add(label = new Label(translate("compound.rescale.warning1"), 10, passed += 28, width - 20, 20));
		label.getStyle().setFont("roboto-bold");
		dialog.getContainer().add(label = new Label(translate("compound.rescale.warning2"), 10, passed += 28, width - 20, 20));
		label.getStyle().setFont("roboto-bold");
        Button button0 = new Button(translate("dialogbox.button.confirm"), 10, passed += 32, 100, 20);
        button0.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		rescale0(selected[0], scale[0]);
        		dialog.close();
        	}
        });
        dialog.getContainer().add(button0);
        Button button1 = new Button(translate("dialogbox.button.cancel"), 120, passed, 100, 20);
        button1.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()) dialog.close();
        });
        dialog.getContainer().add(button1);
        dialog.setSize(width, passed + 48);
		dialog.show(FMTB.frame);
	}

	public void rescale0(TurboList selected, float scale){
		for(TurboList list : groups){
			if(selected != null && selected != list) continue;
			ArrayList<PolygonWrapper> boxes = (ArrayList<PolygonWrapper>)list.stream().filter(wrapper -> wrapper.getType() == ShapeType.BOX).collect(Collectors.toList());
			list.removeAll(boxes);
			boxes.forEach(box -> {
				if(box.getType() == ShapeType.BOX){
					list.add(box.convertTo(ShapeType.SHAPEBOX));
				}
			});
			for(PolygonWrapper wrapper : list){
				scalePoly(wrapper, scale);
			}
		}
	}

	public static void scalePoly(PolygonWrapper wrapper, float scale){
		wrapper.pos = wrapper.pos.scale(scale);
		wrapper.off = wrapper.off.scale(scale);
		if(wrapper instanceof ShapeboxWrapper){
			ShapeboxWrapper sb = (ShapeboxWrapper)wrapper;
			sb.size = sb.size.scale(scale);
			sb.cor0 = sb.cor0.scale(scale);
			sb.cor1 = sb.cor1.scale(scale);
			sb.cor2 = sb.cor2.scale(scale);
			sb.cor3 = sb.cor3.scale(scale);
			sb.cor4 = sb.cor4.scale(scale);
			sb.cor5 = sb.cor5.scale(scale);
			sb.cor6 = sb.cor6.scale(scale);
			sb.cor7 = sb.cor7.scale(scale);
		}
		if(wrapper instanceof CylinderWrapper){
			CylinderWrapper cyl = (CylinderWrapper)wrapper;
			/*cyl.base *= scale;
			cyl.top *= scale;
			if(cyl.topoff != null && !cyl.topoff.isNull()){
				cyl.topoff = cyl.topoff.scale(scale);
			}
			float newlen = cyl.length * scale;
			int newlength = (int)newlen;
			if(newlen % 1 > 0){
				newlength += 1;
				cyl.topoff = cyl.topoff.add(cyl.getTopOffForDir(newlen % 1));
				cyl.pos = cyl.pos.add(cyl.getTopOffForDir(-(newlen % 1)));
			}
			cyl.length = newlength;*/
			cyl.radius *= scale;
			cyl.radius2 *= scale;
			cyl.length *= scale;
			if(cyl.topoff != null && !cyl.topoff.isNull()){
				cyl.topoff = cyl.topoff.scale(scale);
			}
		}
		wrapper.recompile();
	}

}
