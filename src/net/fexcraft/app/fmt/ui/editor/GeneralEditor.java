package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;

public class GeneralEditor extends Editor {
	
	private Container attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker;

	public GeneralEditor(){
		super("general_editor", "editor"); this.setVisible(false);
		this.elements.add((cylinder = new Container(this, "cylinder", width - 4, 28, 4, 0, null)).setText(translate("editor.general.cylinder.title", "Cylinder Settings"), false));
		this.elements.add((texrect_a = new Container(this, "texrect_a", width - 4, 28, 4, 0, null)).setText(translate("editor.general.texrect_a.title", "TexRect [Adv.]"), false));
		this.elements.add((texrect_b = new Container(this, "texrect_b", width - 4, 28, 4, 0, null)).setText(translate("editor.general.texrect_b.title", "TexRect [Basic]"), false));
		this.elements.add((marker = new Container(this, "marker", width - 4, 28, 4, 0, null)).setText(translate("editor.general.marker.title", "Marker Settings"), false));
		//
		int passed = 24;
		{//cylinder
			cylinder.getElements().add(new Button(cylinder, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.radius_length", "Radius / Length / R2"), false));
			cylinder.getElements().add(new TextField(cylinder, "cyl0x", "editor:field", 96, 4, passed += 24).setAsNumberfield(0.01f, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl0y", "editor:field", 96, 106, passed).setAsNumberfield(0.01f, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl0z", "editor:field", 96, 208, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.segments_direction", "Segments / Direction / SL"), false));
			cylinder.getElements().add(new TextField(cylinder, "cyl1x", "editor:field", 96, 4, passed += 24).setAsNumberfield(3, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl1y", "editor:field", 96, 106, passed).setAsNumberfield(0, 5, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl1z", "editor:field", 96, 208, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text2", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.scale", "Base Scale / Top Scale / Top Angle"), false));
			cylinder.getElements().add(new TextField(cylinder, "cyl2x", "editor:field", 96, 4, passed += 24).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl2y", "editor:field", 96, 106, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl2z", "editor:field", 96, 208, passed).setAsNumberfield(-360, 360, true, true).setEnabled(false));
			//
			cylinder.getElements().add(new Button(cylinder, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.top_offset", "Top Offset (x/y/z)"), false));
			cylinder.getElements().add(new TextField(cylinder, "cyl3x", "editor:field", 96, 4, passed += 24).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl3y", "editor:field", 96, 106, passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl3z", "editor:field", 96, 208, passed).setAsNumberfield(Integer.MIN_VALUE, Integer.MAX_VALUE, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text3", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.top_rotation", "Top Rotation (x/y/z)"), false));
			cylinder.getElements().add(new TextField(cylinder, "cyl7x", "editor:field", 96, 4, passed += 24).setAsNumberfield(-360, 360, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl7y", "editor:field", 96, 106, passed).setAsNumberfield(-360, 360, true, true));
			cylinder.getElements().add(new TextField(cylinder, "cyl7z", "editor:field", 96, 208, passed).setAsNumberfield(-360, 360, true, true));
			//
			cylinder.getElements().add(new Button(cylinder, "text4", "editor:title", 290, 20, 0, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.base_top_toggle", "Base/Top (on/off)"), false));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl4x", null, 96, 4, passed += 24));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl4y", null, 96, 106, passed));
			cylinder.getElements().add(new Button(cylinder, "text5", "editor:title", 290, 20, 0, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.outer_inner_toggle", "Outer/Inner (on/off)"), false));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl5x", null, 96, 4, passed += 24));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl5y", null, 96, 106, passed));
			//
			cylinder.getElements().add(new Button(cylinder, "text3", "editor:title", 290, 20, 0, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.cylinder.radial_texture", "Radial Texture (on-off/u/v)"), false));
			cylinder.getElements().add(new TextField.BooleanField(cylinder, "cyl6x", null, 96, 4, passed += 24));
			cylinder.getElements().add(new TextField(cylinder, "cyl6y", "editor:field", 96, 106, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true).setEnabled(false));
			cylinder.getElements().add(new TextField(cylinder, "cyl6z", "editor:field", 96, 208, passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
			//
			cylinder.setExpanded(false); passed = 0;
		}
		final String[] faces = new String[]{
			translate("editor.general.texrect.front", "Front"),
			translate("editor.general.texrect.back", "Back"),
			translate("editor.general.texrect.up", "Up"),
			translate("editor.general.texrect.down", "Down"),
			translate("editor.general.texrect.right", "Right"),
			translate("editor.general.texrect.left", "Left"),
		};
		{//texrect a
			int[] tra = new int[24]; for(int i = 0; i < 12; i++){ tra[i * 2] = 1; tra[i * 2 + 1] = 4; }
			for(int r = 0; r < 12; r++){
				texrect_a.getElements().add(new Button(texrect_a, "text" + r, "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
					.setText(format("editor.general.texrect_a.face_" + (r % 2 == 0 ? "x" : "y"), "%s [%s | TR, TL, BL, BR]", faces[r / 2]), false)); passed += 24;
				for(int i = 0; i < 4; i++){
					String id = "texpos" + (r / 2) + ":" + ((i * 2) + (r % 2 == 1 ? 1 : 0)) + (r % 2 == 0 ? "x" : "y");
					boolean bool = r == 2 || r == 3 || r == 6 || r == 7 || r == 10 || r == 11; int rgb = bool ? 0xff94251f : 0xff323273;
					texrect_a.getElements().add(new TextField(texrect_a, id, "editor:field_texrect_" + (bool ? 1 : 0), 72, 4 + (i * 75), passed)
						.setAsNumberfield(0, Integer.MAX_VALUE, true, true).setColor(rgb));
				}
			}
			//
			texrect_a.setExpanded(false); passed = 0;
		}
		{//texrect b
			for(int r = 0; r < 6; r++){
				texrect_b.getElements().add(new Button(texrect_b, "text" + r, "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
					.setText(format("editor.general.texrect_b.face", "%s [start x/y, end x/y]", faces[r]), false));
				 passed += 24; for(int i = 0; i < 4; i++){
					String id = "texpos" + r + (i < 2 ? "s" : "e") + (i % 2 == 0 ? "x" : "y"); 
					texrect_b.getElements().add(new TextField(texrect_b, id, "editor:field", 72, 4 + (i * 75), passed).setAsNumberfield(0, Integer.MAX_VALUE, true, true));
				}
			}
			//
			texrect_b.setExpanded(false); passed = 0;
		}
		{//marker
			marker.getElements().add(new Button(marker, "text0", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.marker.color", "Marker Color [#hex]"), false));
			marker.getElements().add(new TextField(marker, "marker_colorx", "editor:field", 300, 4, passed += 24){
				@Override public void updateTextField(){ FMTB.MODEL.updateValue(this); }
				@Override
				public float getFloatValue(){
					return Integer.parseInt(this.getTextValue().replace("#", "").replace("0x", ""), 16);
				}
			}.setText("null", true));
			marker.getElements().add(new Button(marker, "text1", "editor:title", 290, 20, 4, passed += 30, BLACK).setBackgroundless(true)
				.setText(translate("editor.general.marker.biped_display", "Biped Display [toggle / rot / scale]"), false));
			marker.getElements().add(new TextField.BooleanField(marker, "marker_bipedx", null, 96, 4, passed += 24));
			marker.getElements().add(new TextField(marker, "marker_anglex", "editor:field", 96, 106, passed).setAsNumberfield(-360, 360, true, true));
			marker.getElements().add(new TextField(marker, "marker_scalex", "editor:field", 96, 208, passed).setAsNumberfield(0, 1024f, true, true));
			//
			marker.setExpanded(false); passed = 0;
		}
		this.containers = new Container[]{ cylinder, texrect_a, texrect_b, marker }; this.repos();
	}

}
