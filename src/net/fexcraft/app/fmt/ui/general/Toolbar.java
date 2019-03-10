package net.fexcraft.app.fmt.ui.general;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.utils.TextureManager;

public class Toolbar extends Element {

	private int oldwidth;
	
	public Toolbar(){
		super(null, "toolbar");
		TextureManager.loadTexture("icons/toolbar/info");
		TextureManager.loadTexture("icons/toolbar/new");
		TextureManager.loadTexture("icons/toolbar/open");
		TextureManager.loadTexture("icons/toolbar/save");
		TextureManager.loadTexture("icons/toolbar/profile");
		TextureManager.loadTexture("icons/toolbar/settings");
		this.setPosition(0, 0).setLevel(-20).setTexPosSize("ui/background_dark", 0, 0, 32, 32).setSize(100, 30);
		//
		int btsz = 96, bthg = 28;
		this.elements.add(new Icon(this, "info", "icons/toolbar/info", 32, 32, 0, 0));
		this.elements.add(new Icon(this, "new", "icons/toolbar/new", 32, 32, 0, 0));
		this.elements.add(new Icon(this, "open", "icons/toolbar/open", 32, 32, 0, 0));
		this.elements.add(new Icon(this, "save", "icons/toolbar/save", 32, 32, 0, 0));
		this.elements.add(new Icon(this, "profile", "icons/toolbar/profile", 32, 32, 0, 0));
		this.elements.add(new Icon(this, "settings", "icons/toolbar/settings", 32, 32, 0, 0));
		this.elements.add(new Button.Default(this, "file", btsz, bthg, 0, 0).setText("File", true));
		this.elements.add(new Button.Default(this, "utils", btsz, bthg, 0, 0).setText("Utils", true));
		this.elements.add(new Button.Default(this, "editor", btsz, bthg, 0, 0).setText("Editor", true));
		this.elements.add(new Button.Default(this, "shapeditor", btsz, bthg, 0, 0).setText("Shapeditor", true));
		this.elements.add(new Button.Default(this, "shapelist", btsz, bthg, 0, 0).setText("Shapelist", true));
		this.elements.add(new Button.Default(this, "textures", btsz, bthg, 0, 0).setText("Textures", true));
		this.elements.add(new Button.Default(this, "helpers", btsz, bthg, 0, 0).setText("Helpers", true));
		this.elements.add(new Button.Default(this, "exit", btsz, bthg, 0, 0).setText("Exit", true));
		//
		this.reorderElements();
	}
	
	private void reorderElements(){
		int start = 4, high = 3;
		for(Element elm : elements){
			if(start + elm.width >= width - 4){ start = 4; high += 34; }
			elm.setPosition(start, elm instanceof Icon ? high - 2 : high); start += elm.width + 2;
		}
		this.height = high + 31;
	}

	@Override
	public void renderSelf(int root_width, int root_height){
		this.width = root_width; this.renderSelfQuad();
		if(oldwidth != width){ oldwidth = width; this.reorderElements(); }
	}

}
