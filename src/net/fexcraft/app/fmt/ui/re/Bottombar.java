package net.fexcraft.app.fmt.ui.re;

import net.fexcraft.app.fmt.ui.NewElement;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.lib.common.math.RGB;

public class Bottombar extends NewElement {
	
	private static TextField netfield;
	private UserInterface ui;

	public Bottombar(UserInterface ui){
		super(null, "bottombar", "bottombar"); this.ui = ui; hovercolor = RGB.WHITE;
		this.setPosition(0, 0, -20).setSize(100, 26).setColor(0xff8f8f8f).setBorder(0xff32a852, 0xffeb4034, 3, true, false);
		this.elements.add(new TextField(this, "polygons", "bottombar:field", ui.width / 6, 0, 0){
			@Override
			public void renderSelf(int rw, int rh){
				this.setText("Polygons: " + GroupCompound.COUNT, false);
				super.renderSelf(rw, rh);
			}
		}.setEnabled(false));
		this.elements.add(new TextField(this, "selected", "bottombar:field", ui.width / 6, 0, 0){
			@Override
			public void renderSelf(int rw, int rh){
				this.setText("Selected: " + GroupCompound.COUNT, false);
				super.renderSelf(rw, rh);
			}
		}.setEnabled(false));
		this.elements.add((netfield = new TextField(this, "netfield", "bottombar:field", ui.width / 3, 0, 0)).setEnabled(false));
		this.repos();
	}
	
	@Override
	public NewElement repos(){
		width = ui.width; x = 0; y = ui.height - height; int buff = 0;
		for(NewElement elm : elements){ elm.xrel = buff; buff += elm.width; elm.repos(); }
		return this.clearVertexes();
	}
	
	public static void updateLoginState(String string){
		if(netfield != null) netfield.setText(string, true);
	}

}
