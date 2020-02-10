package net.fexcraft.app.fmt.ui.editor;

public class GeneralEditor extends Editor {
	
	private Container attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker;

	public GeneralEditor(){
		super("general_editor", "editor"); this.setVisible(false);
		this.containers = new Container[]{}; this.repos();
	}

}
