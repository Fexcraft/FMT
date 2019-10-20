package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.NewElement;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public class NewToolbar extends NewElement {
	
	private UserInterface ui;

	public NewToolbar(UserInterface ui){
		super(null, "toolbar"); this.ui = ui; hovercolor = RGB.WHITE;
		this.setPosition(0, 0, -20)/*.setTexture("ui/background_dark")*/.setSize(100, 30);
		TextureManager.loadTexture("icons/toolbar/info", null);
		TextureManager.loadTexture("icons/toolbar/new", null);
		TextureManager.loadTexture("icons/toolbar/open", null);
		TextureManager.loadTexture("icons/toolbar/save", null);
		TextureManager.loadTexture("icons/toolbar/profile", null);
		TextureManager.loadTexture("icons/toolbar/settings", null);
		this.setColor(0xff484848).setBorder(0xff32a852, 0xffeb4034, 3, false, true);
		//
		this.elements.add(new Icon(this, "info", "icons/toolbar/info", 32, 0, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				DialogBox.notAvailableYet(); return true;
			}
		});
		this.elements.add(new Icon(this, "new", "icons/toolbar/new", 32, 0, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.openNewModel(); return true;
			}
		});
		this.elements.add(new Icon(this, "open", "icons/toolbar/open", 32, 0, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.openModel(); return true;
			}
		});
		this.elements.add(new Icon(this, "save", "icons/toolbar/save", 32, 0, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				SaveLoad.saveModel(false, false); return true;
			}
		});
		this.elements.add(new Icon(this, "profile", "icons/toolbar/profile", 32, 0, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				DialogBox.notAvailableYet(); return true;
			}
		});
		this.elements.add(new Icon(this, "settings", "icons/toolbar/settings", 32, 0, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				UserInterface.SETTINGSBOX.show(); return true;
			}
		});
		//
		realign();
	}

	private void realign(){
		int start = 4, high = 3;
		for(NewElement elm : elements){
			if(start + elm.width >= width - 4){ start = 4; high += 34; }
			elm.setPosition(start, elm instanceof Icon ? high - 2 : high, null); start += elm.width + 2;
		}
		this.height = high + 31; for(NewElement elm : elements) elm.repos();
	}
	
	@Override
	public NewElement repos(){
		width = ui.width; x = xrel; y = yrel; realign(); return this.clearVertexes();
	}

}
