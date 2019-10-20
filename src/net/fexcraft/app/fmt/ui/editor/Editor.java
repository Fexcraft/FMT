package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;
import java.util.Arrays;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.UserInterface;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.Icon;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.lib.common.math.RGB;

public abstract class Editor extends Element {
	
	private static final ArrayList<Editor> editors = new ArrayList<Editor>();
	public static final String[] xyz = new String[]{ "x", "y", "z" };
	private ContainerButton[] containers;
	private Button button; private TextField field; private Icon icon;

	public Editor(String id){
		super(null, id); this.setSize(308, 20).setLevel(-1);
		this.setPosition(0, 34).setVisible(false); editors.add(this);
		containers = this.setupSubElements(); elements.addAll(Arrays.asList(containers));
		this.elements.add(button = new Button(this, "multiplicator_text", 180, 26, 4, 0, RGB.WHITE).setBackgroundless(false).setText("Multiplicator / Rate", false));
		this.elements.add((field = new TextField(this, "multiplicator", 70, 184, 0){
			@Override protected boolean processScrollWheel(int wheel){
				applyChange(FMTB.MODEL.multiply(wheel > 0 ? 2.0f : 0.5f)); return true;
			}
		}.setAsNumberfield(0, 1024, true).applyChange(FMTB.MODEL.rate)).setLevel(5));
		/*this.elements.add((icon = new Icon(this, "multiplicator_reset", "", 26, 26, 258, 0){
			@Override
			protected boolean processButtonClick(int x, int y, boolean left){
				((TextField)root.getElement("multiplicator")).applyChange(1); FMTB.MODEL.rate = 1f; return true;
			}
		}).setTexPosSize("icons/group_delete", 0, 0, 16, 16));*/
	}

	protected abstract ContainerButton[] setupSubElements();

	@Override
	public void renderSelf(int rw, int rh){
		this.y = UserInterface.TOOLBAR.height;
		button.y = y + 4; field.y = y + 4; icon.y = y + 4;
		this.renderQuad(x, y, width, height = (rh - y + 2), "ui/background_light");
		this.renderQuad(width - 2, y - 2, 2, height = (rh - y + 4), "ui/background_dark");
		//
		int pass = 32;
		for(ContainerButton button : containers){
			button.y = y + pass; pass += button.getExpansionHeight() + 4;
		}
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		return false;
	}
	
	public void show(){
		hideAll(); this.visible = true;
	}
	
	public static void show(String id){
		for(Editor edit : editors) edit.setVisible(edit.id.equals(id));
	}
	
	public static void hideAll(){
		for(Editor edit : editors) edit.setVisible(false);
	}
	
	public static Editor get(String id){
		for(Editor edit : editors) if(edit.id.equals(id)) return edit; return null;
	}

	public static void toggle(String string){ toggle(string, true); }

	public static void toggle(String string, boolean close){
		Editor edit = get(string); if(close && edit != null && edit.isVisible()) hideAll(); else show(string);
	}
	
	protected boolean processScrollWheel(int wheel){ return true; }

	public ArrayList<TextField> getFields(){
		ArrayList<TextField> fields = new ArrayList<>();
		for(Element elm : elements) if(elm instanceof TextField) fields.add((TextField)elm);
		return fields;
	}

	public static void toggleAll(){
		boolean anyvisible = false;
		for(Editor edit : editors) if(edit.isVisible()){ anyvisible = true; break; }
		if(anyvisible) hideAll(); else show("general");
	}

	public static TextField getGlobalField(String string){
		for(TextField field : TextField.getAllFields()) if(field.id.equals(string)) return field; return null;
	}

	public static void toggleContainer(int i){
		Editor editor = getVisibleEditor(); if(editor == null){
			if(i == 0) show("general_editor");
			if(i == 1) show("model_group_editor");
			if(i == 2) show("texture_editor");
			if(i == 3) show("preview_editor");
			return;
		}
		if(i < 0 || i >= editor.containers.length) return;
		editor.containers[i].setExpanded(!editor.containers[i].isExpanded());
	}

	private static Editor getVisibleEditor(){
		for(Editor edit : editors) if(edit.isVisible()) return edit; return null;
	}

	public static boolean anyVisible(){
		for(Editor edit : editors) if(edit.isVisible()) return true; return false;
	}

}
