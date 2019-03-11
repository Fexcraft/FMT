package net.fexcraft.app.fmt.ui.editor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.OldElement;
import net.fexcraft.app.fmt.ui.general.Button;
import net.fexcraft.app.fmt.ui.general.TextField;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.TextureManager;

public abstract class Editor extends Element {
	
	private static final ArrayList<Editor> editors = new ArrayList<Editor>();
	public static final String[] xyz = new String[]{ "x", "y", "z" };

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
		return true;
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

	public static void toggle(String string){ toggle(string, true); }

	public static void toggle(String string, boolean close){
		Optional<Editor> opt = editors.stream().filter(pre -> pre.id.equals(string)).findFirst();
		if(close && opt.isPresent() && opt.get().visible) hideAll(); else show(string);
	}
	
	protected void addMultiplicator(int y){
		this.elements.add(new Button(this, "multiplicator-", 12, 26, 4, y){
			@Override protected boolean processButtonClick(int x, int y, boolean left){
				((TextField)root.getElement("multiplicator")).applyChange(FMTB.MODEL.multiply(0.5f)); return true;
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
		return (TextField)this.getElement(string);
	}

	public Button getButton(String string){
		return (Button)this.getElement(string);
	}

	public static Editor get(String string){
		for(Editor edit : editors) if(edit.id.equals(string)) return edit; return null;
	}
	
	protected boolean processScrollWheel(int wheel){ return true; }

	public ArrayList<TextField> getFields(){
		ArrayList<TextField> fields = new ArrayList<>();
		for(Element elm : elements) if(elm instanceof TextField) fields.add((TextField)elm);
		return fields;
	}

	/** Run after all editors are initialized. */
	public static void addQuickButtons(){;
		String[] all = getAllEditorNames();
		for(String str : all){
			TextureManager.loadTexture("icons/editors/" + str);
		}
		for(Editor editor : editors){
			String[] getwanted = editor.getExpectedQuickButtons();
			if(getwanted == null) getwanted = all;
			for(int i = 0; i < getwanted.length; i++){ final String wanted = getwanted[i];
				editor.elements.add(new IconButton(editor, "open_" + wanted, "icons/editors/" + wanted, editor.x + editor.width - (i * 24) - 24, editor.y + 2){
					@Override protected boolean processButtonClick(int x, int y, boolean left){ Editor.show(wanted); return true; }
				});
				editor.getElement("open_" + wanted).setVisible(Settings.editorShortcuts());
			}
		}
	}

	private static String[] getAllEditorNames(){
		String[] arr = new String[editors.size()];
		for(int i = 0; i < arr.length; i++){
			arr[i] = editors.get(i).id;
		} return arr;
	}

	protected abstract String[] getExpectedQuickButtons();

	public static void toggleQuickButtons(){
		Settings.toggleEditorShortcuts();
		for(Editor editor : editors){
			for(OldElement elm : editor.elements){
				if(elm instanceof IconButton && elm.id.startsWith("open_")){
					elm.visible = Settings.editorShortcuts();
				}
			}
		}
	}

}
