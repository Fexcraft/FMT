package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.generic.Button;
import net.fexcraft.app.fmt.ui.generic.IconButton;
import net.fexcraft.app.fmt.ui.generic.TextField;

public class Editor extends Element {
	
	private static final ArrayList<Editor> editors = new ArrayList<Editor>();

	public Editor(String id){
		super(null, id); this.width = 308; z = -1;
		this.x = 0; this.y = 30; editors.add(this);
		this.visible = false;
	}

	@Override
	public void renderSelf(int rw, int rh){
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/button_bg");
		this.renderQuad(width - 2, y - 2, 2, height = (rh - y + 4), "ui/background");
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}
	
	public void show(){
		hideAll(); this.visible = true;
	}
	
	public static void show(String id){
		editors.forEach(elm -> elm.visible = elm.id.equals(id));
	}
	
	public static void hideAll(){
		editors.forEach(elm -> elm.visible = false);
	}

	public static void toggle(String string){
		boolean vis = editors.stream().filter(pre -> pre.id.equals(string)).findFirst().get().visible;
		if(vis) hideAll(); else show(string);
	}
	
	protected void addMultiplicator(int y){
		this.elements.put("multiplicator-", new Button(this, "multiplicator-", 12, 26, 4, y){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				((TextField)parent.getElement("multiplicator")).applyChange(FMTB.MODEL.multiply(0.5f)); return true;
			}
		}.setText(" < ", true).setTexture("ui/background").setLevel(-1));
		this.elements.put("multiplicator", new TextField(this, "multiplicator", 140, 16, y){
			@Override protected boolean processScrollWheel(int wheel){
				applyChange(FMTB.MODEL.multiply(wheel > 0 ? 2.0f : 0.5f)); return true;
			}
		}.setAsNumberfield(0.0001f, 1000, true).setLevel(-1));
		this.elements.put("multiplicator+", new Button(this, "multiplicator+", 12, 26, 152, y){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				((TextField)parent.getElement("multiplicator")).applyChange(FMTB.MODEL.multiply(2.0f)); return true;
			}
		}.setText(" > ", true).setTexture("ui/background").setLevel(-1));
		this.elements.put("multiplicator_reset", new IconButton(this, "multiplicator_reset", "icons/group_delete", 170, this.y + 3 + y){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				((TextField)parent.getElement("multiplicator")).applyChange(1); FMTB.MODEL.rate = 1f; return true;
			}
		});
	}

	public TextField getField(String string){
		return (TextField)elements.get(string);
	}

}
