package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.DialogBox;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.app.fmt.utils.TextureManager.Texture;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.Print;

public class GeneralEditor extends Editor {
	
	private Container attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker;

	public GeneralEditor(){
		super("general_editor", "editor"); this.setVisible(false);
		this.elements.add((attributes = new Container(this, "attributes", width - 4, 28, 4, 0, null)).setText("Polygon Attributes", false));
		this.elements.add((shape = new Container(this, "shape", width - 4, 28, 4, 0, null)).setText("General Shape", false));
		this.elements.add((shapebox = new Container(this, "shapebox", width - 4, 28, 4, 0, null)).setText("Shapebox Corners", false));
		this.elements.add((cylinder = new Container(this, "cylinder", width - 4, 28, 4, 0, null)).setText("Cylinder Settings", false));
		this.elements.add((texrect_a = new Container(this, "texrect_a", width - 4, 28, 4, 0, null)).setText("TexRect [Adv.]", false));
		this.elements.add((texrect_b = new Container(this, "texrect_b", width - 4, 28, 4, 0, null)).setText("TexRect [Basic]", false));
		this.elements.add((marker = new Container(this, "marker", width - 4, 28, 4, 0, null)).setText("Marker Settings", false));
		//
		int passed = 24;
		{//attributes
			attributes.getElements().add(new Button(attributes, "text0", "editor:title", 290, 20, 4, passed = last(passed, attributes), BLACK).setBackgroundless(true).setText("Polygon Group", false));;
			attributes.getElements().add(new TextField(attributes, "group", "editor:field", 290, 4, passed = last(passed, attributes)){
				@Override
				public boolean processScrollWheel(int wheel){
					FMTB.MODEL.changeGroupOfSelected(wheel > 0 ? 1 : -1); return true;
				}
				@Override
				public void updateTextField(){
					String text = this.getTextValue();
					if(!FMTB.MODEL.getGroups().contains(text)){
						FMTB.showDialogbox("Group does not exists.\nDo you wish to create it?", "yes.", "no.", () -> {
							FMTB.MODEL.getGroups().add(new TurboList(text));
							FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), text);
						}, DialogBox.NOTHING);
					} else{ FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), text); }
				}
			}.setText("null", true));
			attributes.getElements().add(new Button(attributes, "text1", "editor:title", 290, 20, 4, passed = last(passed, attributes), BLACK).setBackgroundless(true).setText("Polygon Name", false));
			attributes.getElements().add(new TextField(attributes, "boxname", "editor:field", 290, 4, passed = last(passed, attributes)){
				@Override
				public void updateTextField(){
					if(FMTB.MODEL.getSelected().isEmpty()) return;
					PolygonWrapper wrapper;
					if(FMTB.MODEL.getSelected().size() == 1){
						wrapper = FMTB.MODEL.getFirstSelection();
						if(wrapper != null) wrapper.name = this.getTextValue();
					}
					else{
						ArrayList<PolygonWrapper> polis = FMTB.MODEL.getSelected();
						for(int i = 0; i < polis.size(); i++){
							wrapper = polis.get(i);
							if(wrapper != null){
								String str = this.getText().contains("_") ? "_" + i : this.getText().contains("-") ? "-" + i :
									this.getText().contains(" ") ? " " + i : this.getText().contains(".") ? "." + i : i + "";
								wrapper.name = this.getTextValue() + str;
							}
						}
					}
				}
			}.setText("null", true));
			attributes.getElements().add(new Button(attributes, "text2", "editor:title", 290, 20, 4, passed = last(passed, attributes), BLACK).setBackgroundless(true).setText("Polygon Type", false));
			attributes.getElements().add(new TextField(attributes, "boxtype", "editor:field", 290, 4, passed = last(passed, attributes)){
				@Override
				public boolean processScrollWheel(int wheel){
					FMTB.MODEL.changeTypeOfSelected(FMTB.MODEL.getSelected(), wheel > 0 ? 1 : -1); return true;
				}
				@Override
				public void updateTextField(){
					FMTB.MODEL.changeTypeOfSelected(FMTB.MODEL.getSelected(), this.getTextValue());
				}
			}.setText("null", true));
			attributes.getElements().add(new Button(attributes, "text3", "editor:title", 290, 20, 4, passed = last(passed, attributes), BLACK).setBackgroundless(true).setText("Paint to Texture", false));
			attributes.getElements().add(new Button(attributes, "burntotex", "editor:button", 290, 28, 4, passed = last(passed, attributes)){
				@Override
				public boolean processButtonClick(int x, int y, boolean left){
					if(!left) return true;
					if(FMTB.MODEL.texture == null){
						FMTB.showDialogbox("There is not texture loaded.", "ok", "load", DialogBox.NOTHING, () -> {
							try{
								FMTB.get().UI.getElement("toolbar").getElement("textures").getElement("menu").getElement("select").onButtonClick(x, y, left, true);
							}
							catch(Exception e){
								e.printStackTrace();
							}
						});
					}
					else{
						ArrayList<PolygonWrapper> selection = FMTB.MODEL.getSelected();
						for(PolygonWrapper poly : selection){
							String texname = poly.getTurboList().getGroupTexture() == null ? FMTB.MODEL.texture : poly.getTurboList().getGroupTexture();
							Texture tex = TextureManager.getTexture(texname, true);
							if(tex == null){//TODO group tex compensation
								FMTB.showDialogbox("Texture not found in Memory.\nThis rather bad.", "ok", null, DialogBox.NOTHING, null);
								return true;
							}
							poly.burnToTexture(tex.getImage(), null); poly.recompile(); TextureManager.saveTexture(texname); tex.rebind();
							Print.console("Polygon painted into Texture.");
						}
						return true;
					}
					return false;
				}
			}.setText("Burn to Texture", true));
			//
			attributes.setExpanded(false); passed = 0;
		}
		{//shape/box
			shape.getElements().add(new Button(shape, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Measurements / Size", false));
			passed += 24; for(int i = 0; i < xyz.length; i++){
				shape.getElements().add(new TextField(shape, "size" + xyz[i], "editor:field", 96, 2 + (i * 102), passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			}
			//
			shape.getElements().add(new Button(shape, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Position (x/y/z)", false));
			passed += 24; for(int i = 0; i < xyz.length; i++){
				shape.getElements().add(new TextField(shape, "pos" + xyz[i], "editor:field", 96, 2 + (i * 102), passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			//
			shape.getElements().add(new Button(shape, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Offset (x/y/z)", false));
			passed += 24; for(int i = 0; i < xyz.length; i++){
				shape.getElements().add(new TextField(shape, "off" + xyz[i], "editor:field", 96, 2 + (i * 102), passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			}
			//
			shape.getElements().add(new Button(shape, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Rotation (x/y/z)", false));
			passed += 24; for(int i = 0; i < xyz.length; i++){
				shape.getElements().add(new TextField(shape, "rot" + xyz[i], "editor:field", 96, 2 + (i * 102), passed).setAsNumberfield(-360, 360, true, true));
			}
			//
			shape.getElements().add(new Button(shape, "text4", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Texture (u/v)", false));
			passed += 24; for(int i = 0; i < 2; i++){
				shape.getElements().add(new TextField(shape, "tex" + xyz[i], "editor:field", 96, 2 + (i * 102), passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			}
			//
			shape.setExpanded(false); passed = 0;
		}
		{//shapebox
			for(int i = 0; i < 8; i++){
				shapebox.getElements().add(new Button(shapebox, "text" + i, "editor:title", 290, 20, 4, passed += 30, BLACK).setIcon("blank", 16, ShapeboxWrapper.cornercolors2[i])
					.setBackgroundless(true).setText("Corner " + i + "(x/y/z)", false)); passed += 24; 
				for(int k = 0; k < xyz.length; k++){
					shapebox.getElements().add(new TextField(shapebox, "cor" + i + xyz[k], "editor:field", 96, 2 + (k * 102), passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
				}
			}
			//
			shapebox.setExpanded(false); passed = 0;
		}
		{//cylinder
			cylinder.getElements().add(new Button(cylinder, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Radius / Length / R2", false));
			cylinder.getElements().add(new TextField(cylinder, "cyl0x", "editor:field", 96, 4, passed += 24).setAsNumberfield(0.01f, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl0y", "editor:field", 96, 106, passed).setAsNumberfield(0.01f, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl0z", "editor:field", 96, 208, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Segments / Direction / SL", false));
			cylinder.getElements().add(new TextField(cylinder, "cyl1x", "editor:field", 96, 4, passed += 24).setAsNumberfield(3, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl1y", "editor:field", 96, 106, passed).setAsNumberfield(0, 5, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl1z", "editor:field", 96, 208, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Base Scale / Top Scale / Top Angle", false));
			cylinder.getElements().add(new TextField(cylinder, "cyl2x", "editor:field", 96, 4, passed += 24).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl2y", "editor:field", 96, 106, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl2z", "editor:field", 96, 208, passed).setAsNumberfield(-360, 360, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Top Offset (x/y/z)", false));
			cylinder.getElements().add(new TextField(cylinder, "cyl3x", "editor:field", 96, 4, passed += 24).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl3y", "editor:field", 96, 106, passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl3z", "editor:field", 96, 208, passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text4", "editor:title", 290, 20, 0, passed += 30, BLACK).setBackgroundless(true).setText("Base/Top (on/off)", false));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl4x", null, 96, 4, passed += 24));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl4y", null, 96, 106, passed));
			cylinder.getElements().add(new Button(cylinder, "text5", "editor:title", 290, 20, 0, passed += 30, BLACK).setBackgroundless(true).setText("Outer/Inner (on/off)", false));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl5x", null, 96, 4, passed += 24));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl5y", null, 96, 106, passed));
			//
			cylinder.getElements().add(new Button(cylinder, "text3", "editor:title", 290, 20, 0, passed += 30, BLACK).setBackgroundless(true).setText("Radial Texture (on-off/u/v)", false));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl6x", null, 96, 4, passed += 24));
			cylinder.getElements().add(new TextField(cylinder, "cyl6y", "editor:field", 96, 106, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true).setEnabled(false));
			cylinder.getElements().add(new TextField(cylinder, "cyl6z", "editor:field", 96, 208, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			//
			cylinder.setExpanded(false); passed = 0;
		}
		final String[] faces = new String[]{ "Front", "Back", "Up", "Down", "Right", "Left" };
		{//texrect a
			int[] tra = new int[24]; for(int i = 0; i < 12; i++){ tra[i * 2] = 1; tra[i * 2 + 1] = 4; }
			for(int r = 0; r < 12; r++){
				texrect_a.getElements().add(new Button(texrect_a, "text" + r, "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
					.setText(faces[r / 2] + " [" + (r % 2 == 0 ? "x-pos" : "y-pos") + " | TR, TL, BL, BR]", false)); passed += 24;
				for(int i = 0; i < 4; i++){
					String id = "texpos" + (r / 2) + ":" + ((i * 2) + (r % 2 == 1 ? 1 : 0)) + (r % 2 == 0 ? "x" : "y");
					RGB rgb = r == 2 || r == 3 || r == 6 || r == 7 || r == 10 || r == 11 ? new RGB(204, 97, 91) : new RGB(102, 102, 173);
					texrect_a.getElements().add(new TextField(texrect_a, id, "editor:field", 72, 4 + (i * 75), passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true).setColor("inactive", rgb));
				}
			}
			//
			texrect_a.setExpanded(false); passed = 0;
		}
		{//texrect b
			for(int r = 0; r < 6; r++){
				texrect_b.getElements().add(new Button(texrect_b, "text" + r, "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText(faces[r] + " [start x/y, end x/y]", false));
				 passed += 24; for(int i = 0; i < 4; i++){
					String id = "texpos" + r + (i < 2 ? "s" : "e") + (i % 2 == 0 ? "x" : "y"); 
					texrect_b.getElements().add(new TextField(texrect_b, id, "editor:field", 72, 4 + (i * 75), passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
				}
			}
			//
			texrect_b.setExpanded(false); passed = 0;
		}
		{//marker
			marker.getElements().add(new Button(marker, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Marker Color [#hex]", false));
			marker.getElements().add(new TextField(marker, "marker_colorx", "editor:field", 300, 4, passed += 24){
				@Override public void updateTextField(){ FMTB.MODEL.updateValue(this); }
				@Override
				public float getFloatValue(){
					return Integer.parseInt(this.getTextValue().replace("#", "").replace("0x", ""), 16);
				}
			}.setText("null", true));
			marker.getElements().add(new Button(marker, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true).setText("Biped Display [toggle / rot / scale]", false));
			marker.getElements().add(new TextField.BooleanField(marker, "marker_bipedx", null, 96, 4, passed += 24));
			marker.getElements().add(new TextField(marker, "marker_anglex", "editor:field", 96, 106, passed).setAsNumberfield(-360, 360, true, true));
			marker.getElements().add(new TextField(marker, "marker_scalex", "editor:field", 96, 208, passed).setAsNumberfield(0, 1024f, true, true));
			//
			marker.setExpanded(false); passed = 0;
		}
		this.containers = new Container[]{ attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker }; this.repos();
	}

	private int last(int passed, Container container){
		return passed + container.getLastElementYSize(4);
	}

}
