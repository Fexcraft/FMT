package net.fexcraft.app.fmt.ui.editor;

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
	
	private ContainerButton general, box, shapebox, cylinder, texrect_a, texrect_b, marker;

	public GeneralEditor(){ super("general_editor"); }

	@Override
	protected ContainerButton[] setupSubElements(){
		general = new ContainerButton(this, "general", 300, 28, 4, y, new int[]{ 1, 1, 1, 1, 1, 1, 1, 1 }){
			@Override
			public void addSubElements(){
				this.elements.add(new Button(this, "text0", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Polygon Group", false).setRowCol(0, 0));
				this.elements.add(new TextField(this, "group", 290, 0, 0){
					@Override
					protected boolean processScrollWheel(int wheel){
						FMTB.MODEL.changeGroupOfSelected(wheel > 0 ? 1 : -1); return true;
					}
					@Override
					public void updateTextField(){
						String text = this.getTextValue();
						if(!FMTB.MODEL.getCompound().containsKey(text)){
							FMTB.showDialogbox("Group does not exists.\nDo you wish to create it?", "yes.", "no.", () -> {
								FMTB.MODEL.getCompound().put(text, new TurboList(text));
								FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), text);
							}, DialogBox.NOTHING);
						} else{ FMTB.MODEL.changeGroupOfSelected(FMTB.MODEL.getSelected(), text); }
					}
				}.setText("null", true).setRowCol(1, 0));
				this.elements.add(new Button(this, "text1", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Polygon Name", false).setRowCol(2, 0));
				this.elements.add(new TextField(this, "boxname", 290, 0, 0){
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
				}.setText("null", true).setRowCol(3, 0));
				this.elements.add(new Button(this, "text2", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Polygon Type", false).setRowCol(4, 0));
				this.elements.add(new TextField(this, "boxtype", 290, 0, 0){
					@Override
					protected boolean processScrollWheel(int wheel){
						FMTB.MODEL.changeTypeOfSelected(FMTB.MODEL.getSelected(), wheel > 0 ? 1 : -1); return true;
					}
					@Override
					public void updateTextField(){
						FMTB.MODEL.changeTypeOfSelected(FMTB.MODEL.getSelected(), this.getTextValue());
					}
				}.setText("null", true).setRowCol(5, 0));
				this.elements.add(new Button(this, "text3", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Paint to Texture", false).setRowCol(6, 0));
				this.elements.add(new Button(this, "burntotex", 20, 28, 0, 0){
					@Override
					protected boolean processButtonClick(int x, int y, boolean left){
						if(!left) return true;
						if(FMTB.MODEL.texture == null){
							FMTB.showDialogbox("There is not texture loaded.", "ok", "load", DialogBox.NOTHING, () -> {
								try{
									//TODO FMTB.get().UI.getElement("toolbar").getElement("textures").getElement("menu").getElement("select").onButtonClick(x, y, left, true);
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
				}.setText("Burn to Texture", true).setTexPosSize("ui/background_dark", 0, 0, 64, 64).setRowCol(7, 0));
			}
		};
		general.setText("Polygon Attributes", false);
		box = new ContainerButton(this, "box", 300, 28, 4, y, new int[]{ 1, 3, 1, 3, 1, 3, 1, 3, 1, 2 }){
			@Override
			public void addSubElements(){
				this.elements.add(new Button(this, "text0", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Measurements / Size", false).setRowCol(0, 0));
				for(int i = 0; i < xyz.length; i++){
					this.elements.add(new TextField(this, "size" + xyz[i], 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(1, i));
				}
				//
				this.elements.add(new Button(this, "text1", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Position (x/y/z)", false).setRowCol(2, 0));
				for(int i = 0; i < xyz.length; i++){
					this.elements.add(new TextField(this, "pos" + xyz[i], 0, 0, 0).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(3, i));
				}
				//
				this.elements.add(new Button(this, "text2", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Offset (x/y/z)", false).setRowCol(4, 0));
				for(int i = 0; i < xyz.length; i++){
					this.elements.add(new TextField(this, "off" + xyz[i], 0, 0, 0).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(5, i));
				}
				//
				this.elements.add(new Button(this, "text3", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Rotation (x/y/z)", false).setRowCol(6, 0));
				for(int i = 0; i < xyz.length; i++){
					this.elements.add(new TextField(this, "rot" + xyz[i], 0, 0, 0).setAsNumberfield(-360, 360, true).setRowCol(7, i));
				}
				//
				this.elements.add(new Button(this, "text4", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Texture (u/v)", false).setRowCol(8, 0));
				for(int i = 0; i < 2; i++){
					this.elements.add(new TextField(this, "tex" + xyz[i], 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(9, i));
				}
			}
		};
		box.setText("General Shape", false);
		shapebox = new ContainerButton(this, "shapebox", 300, 28, 4, y, new int[]{ 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3, 1, 3 }){
			@Override
			public void addSubElements(){
				for(int i = 0; i < 8; i++){
					this.elements.add(new Button(this, "text" + i, 290, 20, 0, 0, RGB.WHITE).setIcon("ui/background_white", 16, ShapeboxWrapper.cornercolors2[i])
						.setBackgroundless(false).setText("Corner " + i + "(x/y/z)", false).setRowCol(i * 2, 0));
					for(int k = 0; k < xyz.length; k++){
						this.elements.add(new TextField(this, "cor" + i + xyz[k], 0, 0, 0).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(i * 2 + 1, k));
					}
				}
			}
		};
		shapebox.setText("Shapebox Corners", false);
		cylinder = new ContainerButton(this, "cylinder", 300, 28, 4, y, new int[]{ 1, 3, 1, 3, 1, 3, 1, 3, 1, 2, 1, 2, 1, 3 }){
			@Override
			public void addSubElements(){
				this.elements.add(new Button(this, "text0", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Radius / Length / R2", false).setRowCol(0, 0));
				this.elements.add(new TextField(this, "cyl0x", 0, 0, 0).setAsNumberfield(0.01f, Integer.MAX_VALUE, true).setRowCol(1, 0));
				this.elements.add(new TextField(this, "cyl0y", 0, 0, 0).setAsNumberfield(0.01f, Integer.MAX_VALUE, true).setRowCol(1, 1));
				this.elements.add(new TextField(this, "cyl0z", 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(1, 2));
				//
				this.elements.add(new Button(this, "text1", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Segments / Direction / SL", false).setRowCol(2, 0));
				this.elements.add(new TextField(this, "cyl1x", 0, 0, 0).setAsNumberfield(3, Integer.MAX_VALUE, true).setRowCol(3, 0));
				this.elements.add(new TextField(this, "cyl1y", 0, 0, 0).setAsNumberfield(0, 5, true).setRowCol(3, 1));
				this.elements.add(new TextField(this, "cyl1z", 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(3, 2));
				//
				this.elements.add(new Button(this, "text2", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Base Scale / Top Scale / Top Angle", false).setRowCol(4, 0));
				this.elements.add(new TextField(this, "cyl2x", 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(5, 0));
				this.elements.add(new TextField(this, "cyl2y", 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(5, 1));
				this.elements.add(new TextField(this, "cyl2z", 0, 0, 0).setAsNumberfield(-360, 360, true).setRowCol(5, 2));
				//
				this.elements.add(new Button(this, "text3", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Top Offset (x/y/z)", false).setRowCol(6, 0));
				this.elements.add(new TextField(this, "cyl3x", 0, 0, 0).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(7, 0));
				this.elements.add(new TextField(this, "cyl3y", 0, 0, 0).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(7, 1));
				this.elements.add(new TextField(this, "cyl3z", 0, 0, 0).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true).setRowCol(7, 2));
				//
				this.elements.add(new Button(this, "text4", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Base/Top (on/off)", false).setRowCol(8, 0));
				this.elements.add(new TextField.BooleanField(this, "cyl4x", 0, 0, 0).setRowCol(9, 0));
				this.elements.add(new TextField.BooleanField(this, "cyl4y", 0, 0, 0).setRowCol(9, 1));
				this.elements.add(new Button(this, "text5", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Outer/Inner (on/off)", false).setRowCol(10, 0));
				this.elements.add(new TextField.BooleanField(this, "cyl5x", 0, 0, 0).setRowCol(11, 0));
				this.elements.add(new TextField.BooleanField(this, "cyl5y", 0, 0, 0).setRowCol(11, 1));
				//
				this.elements.add(new Button(this, "text3", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Radial Texture (on-off/u/v)", false).setRowCol(12, 0));
				this.elements.add(new TextField.BooleanField(this, "cyl6x", 0, 0, 0).setRowCol(13, 0));
				this.elements.add(new TextField(this, "cyl6y", 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(13, 1).setEnabled(false));
				this.elements.add(new TextField(this, "cyl6z", 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(13, 2));
			}
		};
		cylinder.setText("Cylinder Settings", false);
		final String[] faces = new String[]{ "Front", "Back", "Up", "Down", "Right", "Left" };
		int[] tra = new int[24]; for(int i = 0; i < 12; i++){ tra[i * 2] = 1; tra[i * 2 + 1] = 4; }
		texrect_a = new ContainerButton(this, "texrect_a", 300, 28, 4, y, tra){
			@Override
			public void addSubElements(){
				for(int r = 0; r < 12; r++){
					this.elements.add(new Button(this, "text" + r, 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false)
						.setText(faces[r / 2] + " [" + (r % 2 == 0 ? "x-pos" : "y-pos") + " | TR, TL, BL, BR]", false).setRowCol(r * 2, 0));
					for(int i = 0; i < 4; i++){
						String id = "texpos" + (r / 2) + ":" + ((i * 2) + (r % 2 == 1 ? 1 : 0)) + (r % 2 == 0 ? "x" : "y");
						RGB rgb = r == 2 || r == 3 || r == 6 || r == 7 || r == 10 || r == 11 ? new RGB(204, 97, 91) : new RGB(102, 102, 173);
						this.elements.add(new TextField(this, id, 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setColor("inactive", rgb).setRowCol(r * 2 + 1, i));
					}
				}
			}
		};
		texrect_a.setText("TexRect [Adv.]", false);
		texrect_b = new ContainerButton(this, "texrect_b", 300, 28, 4, y, new int[]{ 1, 4, 1, 4, 1, 4, 1, 4, 1, 4, 1, 4 }){
			@Override
			public void addSubElements(){
				for(int r = 0; r < 6; r++){
					this.elements.add(new Button(this, "text" + r, 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText(faces[r] + " [start x/y, end x/y]", false).setRowCol(r * 2, 0));
					for(int i = 0; i < 4; i++){
						String id = "texpos" + r + (i < 2 ? "s" : "e") + (i % 2 == 0 ? "x" : "y"); 
						this.elements.add(new TextField(this, id, 0, 0, 0).setAsNumberfield(0, Integer.MAX_VALUE, true).setRowCol(r * 2 + 1, i));
					}
				}
			}
		};
		texrect_b.setText("TexRect [Basic]", false);
		marker = new ContainerButton(this, "marker", 300, 28, 4, y, new int[]{ 1, 1, 1, 3 }){
			@Override
			public void addSubElements(){
				this.elements.add(new Button(this, "text0", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Marker Color [#hex]", false).setRowCol(0, 0));
				this.elements.add(new TextField(this, "marker_colorx", 290, 0, 0){
					@Override public void updateTextField(){ /*FMTB.MODEL.updateValue(this);*/ }//TODO
					@Override
					public float getFloatValue(){
						return Integer.parseInt(this.getTextValue().replace("#", "").replace("0x", ""), 16);
					}
				}.setText("null", true).setRowCol(1, 0));
				this.elements.add(new Button(this, "text1", 290, 20, 0, 0, RGB.WHITE).setBackgroundless(false).setText("Biped Display [toggle / rot / scale]", false).setRowCol(2, 0));
				this.elements.add(new TextField.BooleanField(this, "marker_bipedx", 0, 0, 0).setRowCol(3, 0));
				this.elements.add(new TextField(this, "marker_anglex", 290, 0, 0).setAsNumberfield(-360, 360, true).setRowCol(3, 1));
				this.elements.add(new TextField(this, "marker_scalex", 290, 0, 0).setAsNumberfield(0, 1024f, true).setRowCol(3, 2));
			}
		};
		marker.setText("Marker Settings", false);
		return new ContainerButton[]{ general, box, shapebox, cylinder, texrect_b, texrect_a, marker };
	}

}
