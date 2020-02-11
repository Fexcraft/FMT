package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.lib.common.math.RGB;

public class Toolbar extends Element {

	public Toolbar(){
		super(null, "toolbar", "toolbar"); hovercolor = RGB.WHITE;
		this.setPosition(0, 0).setSize(100, 30).setColor(0xff484848).setBorder(0xff32a852, 0xffeb4034, 3, false, true);
		//
		this.elements.add(new Icon(this, "info", "toolbar:icon", "icons/toolbar/info", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				DialogBox.notAvailableYet(); return true;
			}
		});
		this.elements.add(new Icon(this, "new_file", "toolbar:icon", "icons/toolbar/new", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.openNewModel(); return true;
			}
		});
		this.elements.add(new Icon(this, "open_file", "toolbar:icon", "icons/toolbar/open", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.openModel(); return true;
			}
		});
		this.elements.add(new Icon(this, "save_file", "toolbar:icon", "icons/toolbar/save", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.saveModel(false, false); return true;
			}
		});
		this.elements.add(new Icon(this, "profile", "toolbar:icon", "icons/toolbar/profile", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				DialogBox.notAvailableYet(); return true;
			}
		});
		this.elements.add(new Icon(this, "settings", "toolbar:icon", "icons/toolbar/settings", 32, 0, 0){
			@Override
			public boolean processButtonClick(int x, int y, boolean left){
				/*UserInterface.SETTINGSBOX.show();*/ return true;
			}
		});
	}
	
	@Override
	public Element repos(){
		width = UserInterface.width; x = xrel; y = yrel; return this.clearVertexes();
	}

}
