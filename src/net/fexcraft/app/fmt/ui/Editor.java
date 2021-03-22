package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.TextArea;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator.Translations;

public class Editor extends Component {
	
	public static final HashMap<String, Editor> EDITORS = new HashMap<>();
	public static final ArrayList<Editor> EDITORLIST = new ArrayList<>();
	public ArrayList<EditorComponent> components = new ArrayList<>();
	private ScrollablePanel scrollable;
	private Icon rem, set, add, adj;
	private Label label;
	public static int CWIDTH = 300, WIDTH = 310, LABEL = 30;
	public boolean alignment, comp_adj_mode;
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
		add(label = new Label(this.name = name, 5, 0, CWIDTH - 10, LABEL));
		CursorEnterEventListener lis = l -> toggleIcons();
		label.getListenerMap().addListener(CursorEnterEvent.class, lis);
		label.getStyle().setFontSize(30f);
		add(rem = new Icon((byte)10, "./resources/textures/icons/component/remove.png", () -> {}));
		add(set = new Icon((byte)20, "./resources/textures/icons/component/edit.png", () -> {}));
		add(adj = new Icon((byte)30, "./resources/textures/icons/component/adjust.png", () -> comp_adj_mode = !comp_adj_mode));
		add(add = new Icon((byte)40, "./resources/textures/icons/component/add.png", () -> addComponentDialog()));
		rem.getListenerMap().addListener(CursorEnterEvent.class, lis);
		set.getListenerMap().addListener(CursorEnterEvent.class, lis);
		add.getListenerMap().addListener(CursorEnterEvent.class, lis);
		adj.getListenerMap().addListener(CursorEnterEvent.class, lis);
		rem.getStyle().setDisplay(DisplayType.NONE);
		set.getStyle().setDisplay(DisplayType.NONE);
		add.getStyle().setDisplay(DisplayType.NONE);
		adj.getStyle().setDisplay(DisplayType.NONE);
		align();
		hide();
	}

	private void toggleIcons(){
		boolean bool = label.isHovered() || rem.isHovered() || set.isHovered() || add.isHovered() || adj.isHovered();
		DisplayType type = bool ? DisplayType.MANUAL : DisplayType.NONE;
		rem.getStyle().setDisplay(type);
		set.getStyle().setDisplay(type);
		add.getStyle().setDisplay(type);
		adj.getStyle().setDisplay(type);
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
		UpdateHandler.registerHolder(com.getUpdateHolder());
		alignComponents();
	}

	public void removeComponent(EditorComponent com){
		components.remove(com);
		scrollable.getContainer().remove(com);
		UpdateHandler.deregisterHolder(com.getUpdateHolder());
		for(int i = 0; i < components.size(); i++){
			components.get(i).index = i;
		}
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
	
	private static String selected_component;
	private static TextArea dialog_area;
	private static Dialog dialog;
	
	public static void addComponentDialog(){
		if(dialog != null){
			dialog.close();
			dialog = null;
		}
		dialog_area = null;
		selected_component = null;
		String[] strs = new String[EditorComponent.REGISTRY.size()];
		int idx = 0;
		for(String str : EditorComponent.REGISTRY.keySet()){
			strs[idx++] = "editor.component." + str + ".name";
		}
		Translations trs = translate(strs);
		float dialog_width = 620, scrollable_width = trs.longest + 4 < 300 - 10 ? 300 - 10 : trs.longest + 4;
		Dialog dialog = new Dialog(translate("editor.component.add_dialog.title"), dialog_width, 300);
		ScrollablePanel panel = new ScrollablePanel(5, 5, scrollable_width, 270);
		panel.getContainer().setSize(scrollable_width, trs.results.length * 22);
		for(int i = 0; i < strs.length; i++){
			Label label = new Label(trs.results[i], 0, i * 22, scrollable_width, 22);
			int j = i;
			label.getListenerMap().addListener(MouseClickEvent.class, listener -> {
				selected_component = strs[j].substring("editor.component.".length(), strs[j].length() - 5);
				dialog_area.getTextState().setText(translate("editor.component." + selected_component + ".desc"));
			});
			Settings.applyBorderless(label.getStyle());
			Settings.applyBorderless(label.getPressedStyle());
			label.getFocusedStyle().getBackground().setColor(FMT.rgba(127, 127, 127, 1f));
			panel.getContainer().add(label);
		}
		Settings.applyBorderless(panel);
		Settings.applyBorderless(panel.getContainer());
		panel.setFocusable(false);
		dialog.getContainer().add(panel);
		dialog.getContainer().add(dialog_area = new TextArea(scrollable_width + 10, 5, dialog_width - (scrollable_width + 15), 170));
		dialog_area.getTextAreaField().setTextState(new ALBTextState(dialog_area.getSize().x - 15));
		dialog_area.getTextAreaField().getTextState().setEditable(false);
		dialog_area.getTextAreaField().getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
		dialog_area.getTextAreaField().getStyle().setVerticalAlign(VerticalAlign.TOP);
		dialog_area.setHorizontalScrollBarVisible(false);
		dialog.getContainer().add(new Label(translate("editor.component.add_dialog.select"), scrollable_width + 10, 185, dialog_width - (scrollable_width + 15), 25));
		SelectBox<String> box = new SelectBox<>(scrollable_width + 10, 210, dialog_width - (scrollable_width + 15), 25);
		for(Editor editor : EDITORS.values()) box.addElement(editor.name);
		box.setSelected(0, true);
		dialog.getContainer().add(box);
		Button button = new Button(translate("editor.component.add_dialog.confirm"), scrollable_width + 10, 245, dialog_width - (scrollable_width + 15), 25);
		button.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT || listener.getAction() != MouseClickAction.CLICK) return;
			try{
				Editor editor = byName(box.getSelection());
				if(editor == null || selected_component == null) return;
				editor.addComponent(EditorComponent.REGISTRY.get(selected_component).newInstance());
				dialog.close();
			}
			catch(Exception e){
				Logging.log(e);
			}
		});
		dialog.getContainer().add(button);
		dialog.setResizable(false);
		dialog.getTitleTextState().getTextWidth();
		dialog.show(FMT.FRAME);
	}

	private static Editor byName(String name){
		for(Editor editor : EDITORS.values()){
			if(editor.name.equals(name)) return editor;
		}
		return null;
	}
	
}
