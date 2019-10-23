package net.fexcraft.app.fmt.ui.re.editor;

public class GeneralEditor extends Editor {
	
	private Container attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker;

	public GeneralEditor(){
		super("general_editor", "editor"); this.setVisible(true);
		this.elements.add((attributes = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("Polygon Attributes", false));
		this.elements.add((shape = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("General Shape", false));
		this.elements.add((shapebox = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("Shapebox Corners", false));
		this.elements.add((cylinder = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("Cylinder Settings", false));
		this.elements.add((texrect_a = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("TexRect [Adv.]", false));
		this.elements.add((texrect_b = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("TexRect [Basic]", false));
		this.elements.add((marker = new Container(this, "editor:container", width - 8, 28, 4, 0, null)).setText("Marker Settings", false));
		//
		this.containers = new Container[]{ attributes, shape, shapebox, cylinder, texrect_a, texrect_b, marker }; this.repos();
	}

}
