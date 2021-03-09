package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Logging.log;
import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.style.Style.DisplayType;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.app.fmt.utils.Translator;
import net.fexcraft.app.fmt.utils.Translator.Translations;

public class Editor extends Component {
	
	public static final HashMap<String, Editor> EDITORS = new HashMap<>();
	public static final ArrayList<Editor> EDITORLIST = new ArrayList<>();
	public ArrayList<EditorComponent> components = new ArrayList<>();
	private ScrollablePanel scrollable;
	private Label label;
	public static int CWIDTH = 300, WIDTH = 310, LABEL = 30;
	public boolean alignment;
	public String name;
	
	public Editor(String id, String name, boolean left){
		Settings.applyBorderless(this);
		this.setFocusable(false);
		EDITORS.put(id, this);
		EDITORLIST.add(this);
		alignment = left;
		add(scrollable = new ScrollablePanel(0, LABEL, WIDTH, getSize().y));
		Settings.applyBorderless(scrollable);
		Settings.applyBorderless(scrollable.getContainer());
		add(label = new Label(name, 5, 0, CWIDTH - 10, LABEL));
		label.getStyle().setFontSize(30f);
		align();
		hide();
	}

	public Editor(String key, JsonObject obj){
		this(key, Jsoniser.get(obj, "name", "Nameless Editor"), Jsoniser.get(obj, "alignment", true));
	}

	public void align(){
		setPosition(alignment ? 0 : FMT.WIDTH - WIDTH, ToolbarMenu.HEIGHT);
		setSize(WIDTH, FMT.HEIGHT - ToolbarMenu.HEIGHT);
		scrollable.setSize(WIDTH, getSize().y - LABEL);
		scrollable.setHorizontalScrollBarVisible(false);
		alignComponents();
		if(scrollable.getContainer().getSize().y < getSize().y - LABEL){
			scrollable.getContainer().setSize(WIDTH, getSize().y - LABEL);
		}
	}
	
	public void alignComponents(){
		int passed = 0;
		for(EditorComponent com : components){
			com.setPosition(0, passed);
			passed += com.getSize().y + 2;
		}
		scrollable.getContainer().setSize(WIDTH, passed);
	}

	public void hide(){
		getStyle().setDisplay(DisplayType.NONE);
	}
	
	public void show(){
		getStyle().setDisplay(DisplayType.MANUAL);
		this.setEnabled(true);
	}

	public void addComponent(EditorComponent com){
		com.index = components.size();
		com.editor = this;
		components.add(com);
		scrollable.getContainer().add(com);
		alignComponents();
	}

	public void swap(int x, int y){
		Collections.swap(components, x, y);
		scrollable.getContainer().removeIf(c -> true);
		for(int i = 0; i < components.size(); i++){
			components.get(i).index = i;
		}
		scrollable.getContainer().addAll(components);
		alignComponents();
	}
	
	public static void addComponent(String id){
		Class<? extends EditorComponent> com = EditorComponent.REGISTRY.get(id);
		if(com == null){
			log("Editor Component with ID '" + id + "' not found.");
			return;
		}
		Dialog dialog = new Dialog(translate("editor.component.add_dialog.title"));
		//
		Translations trs = Translator.translate("editor.component.add_dialog.01", "editor.component.add_dialog.002");
		for(int i = 0; i < trs.lengths.length; i++){
			log(trs.results[i] + " = " + trs.lengths[i]);
		}
		dialog.getTitleTextState().getTextWidth();
		dialog.show(FMT.FRAME);
	}
	
}
