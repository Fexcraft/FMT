package net.fexcraft.app.fmt.ui.editor;

import static net.fexcraft.app.fmt.utils.StyleSheet.BLACK;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;

public class GeneralEditor extends Editor {
	
	private Container attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker;

	public GeneralEditor(){
		super("general_editor", "editor"); this.setVisible(false);
		this.elements.add((marker = new Container(this, "marker", width - 4, 28, 4, 0, null)).setText(translate("editor.general.marker.title", "Marker Settings"), false));
		//
		int passed = 24;
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
		this.containers = new Container[]{ marker }; this.repos();
	}

}
