package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Dialog;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.ScrollablePanel;
import org.liquidengine.legui.component.SelectBox;
import org.liquidengine.legui.component.TextArea;
import org.liquidengine.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.component.optional.align.VerticalAlign;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.event.ScrollEvent;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.style.Border;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler;
import net.fexcraft.app.fmt.attributes.UpdateType;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator.Translations;
import net.fexcraft.app.json.JsonMap;

public class Editor extends Component {
	
	public static final List<String> TREES = Arrays.asList("polygon_tree");
	public static final HashMap<String, Editor> EDITORS = new HashMap<>();
	public static final ArrayList<Editor> EDITORLIST = new ArrayList<>();
	public static float RATE = 1f;
	public static Editor LEFT = null, RIGHT = null;
	public ArrayList<EditorComponent> components = new ArrayList<>();
	private ScrollablePanel scrollable;
	private Icon rem, set, add, adj, sid;
	private Icon[] trees;
	private Label label;
	public static int CWIDTH = 300, WIDTH = 310, LABEL = 30;
	public boolean alignment, comp_adj_mode, tree;
	public final String id;
	public String name;
	
	public Editor(String id, String name, boolean tree, boolean left){
		Settings.applyBorderless(this);
		this.setFocusable(false);
		EDITORS.put(this.id = id, this);
		EDITORLIST.add(this);
		alignment = left;
		this.tree = tree;
		add(scrollable = new ScrollablePanel(0, topSpace(), WIDTH, getSize().y));
		scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
		scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
		Settings.applyBorderlessScrollable(scrollable, true);
		add(label = new Label(this.name = name, 5, 0, CWIDTH - 10, LABEL));
		CursorEnterEventListener lis = l -> toggleIcons();
		label.getListenerMap().addListener(CursorEnterEvent.class, lis);
		label.getStyle().setFontSize(30f);
		if(!tree){
			add(rem = new Icon((byte)10, "./resources/textures/icons/component/remove.png", () -> {}).addTooltip("editor.remove", alignment));
			add(adj = new Icon((byte)30, "./resources/textures/icons/component/adjust.png", () -> comp_adj_mode = !comp_adj_mode).addTooltip("editor.adjust_components", alignment));
			add(add = new Icon((byte)40, "./resources/textures/icons/component/add.png", () -> addComponentDialog()).addTooltip("editor.add_component", alignment));
		}
		add(set = new Icon((byte)(tree ? 10 : 20), "./resources/textures/icons/component/edit.png", () -> rename()).addTooltip("editor.rename", alignment));
		add(sid = new Icon((byte)(tree ? 20 : 50), "./resources/textures/icons/component/side.png", () -> changeSide()).addTooltip("editor.change_side", alignment));
		if(!tree){
			rem.getListenerMap().addListener(CursorEnterEvent.class, lis);
			add.getListenerMap().addListener(CursorEnterEvent.class, lis);
			adj.getListenerMap().addListener(CursorEnterEvent.class, lis);
			rem.getStyle().setDisplay(DisplayType.NONE);
			add.getStyle().setDisplay(DisplayType.NONE);
			adj.getStyle().setDisplay(DisplayType.NONE);
		}
		set.getListenerMap().addListener(CursorEnterEvent.class, lis);
		sid.getListenerMap().addListener(CursorEnterEvent.class, lis);
		set.getStyle().setDisplay(DisplayType.NONE);
		sid.getStyle().setDisplay(DisplayType.NONE);
		align();
		hide();
		UpdateHandler.update(UpdateType.EDITOR_CREATED, this);
	}

	protected float topSpace(){
		return LABEL;
	}

	protected DisplayType toggleIcons(){
		boolean bool = label.isHovered() || set.isHovered() || sid.isHovered();
		if(!bool && !tree) bool = rem.isHovered() || add.isHovered() || adj.isHovered();
		if(!bool && trees != null) for(Icon icon : trees) if(icon.isHovered()) bool = true;
		DisplayType type = bool ? DisplayType.MANUAL : DisplayType.NONE;
		if(!tree){
			rem.getStyle().setDisplay(type);
			add.getStyle().setDisplay(type);
			adj.getStyle().setDisplay(type);
		}
		set.getStyle().setDisplay(type);
		sid.getStyle().setDisplay(type);
		if(trees != null) for(Icon icon : trees) icon.getStyle().setDisplay(type);
		return type;
	}
	
	protected void addTreeIcons(int i){
		byte idx = 20;
		trees = new Icon[3];
		if(i != 0) add(trees[i++] = new Icon(idx += 10, "./resources/textures/icons/tree/polygon.png", () -> Editor.show("polygon_tree")).addTooltip("editor.tree.polygon", alignment));
		if(i != 1) add(trees[i++] = new Icon(idx += 10, "./resources/textures/icons/tree/helper.png", () -> Editor.show("helper_tree")).addTooltip("editor.tree.helper", alignment));
		if(i != 2) add(trees[i++] = new Icon(idx += 10, "./resources/textures/icons/tree/textures.png", () -> Editor.show("texture_tree")).addTooltip("editor.tree.texture", alignment));
		if(i != 3) add(trees[i++] = new Icon(idx += 10, "./resources/textures/icons/tree/fvtm.png", () -> Editor.show("fvtm_tree")).addTooltip("editor.tree.animation", alignment));
		if(trees != null) for(Icon icon : trees) icon.getStyle().setDisplay(DisplayType.NONE);
	}

	private void changeSide(){
		Editor other = get(!alignment);
		if(other != null){
			other.alignment = alignment;
			other.align();
		}
		alignment = !alignment;
		this.align();
		if(alignment){
			LEFT = this;
			RIGHT = other;
		}
		else {
			LEFT = other;
			RIGHT = this;
		}
	}

	private void rename(){
		float dialog_width = 300;
		Dialog dialog = new Dialog(translate("editor.rename"), dialog_width, 80);
		TextField field = new TextField(name, 5, 5, dialog_width - 10, 25, true);
		field.getStyle().setBorder(new Border(){});
		dialog.getContainer().add(field);
		Button button = new Button(translate("dialog.button.confirm"), 5, 35, 100, 20);
		button.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getButton() != MouseButton.MOUSE_BUTTON_LEFT || listener.getAction() != MouseClickAction.CLICK) return;
			label.getTextState().setText(name = field.getTextState().getText());
			dialog.close();
		});
		dialog.getContainer().add(button);
		dialog.setResizable(false);
		dialog.getTitleTextState().getTextWidth();
		dialog.show(FMT.FRAME);
	}

	public Editor(String key, JsonMap obj){
		this(key, obj.get("name", "Nameless Editor"), false, obj.get("alignment", true));
	}

	public void align(){
		setPosition(alignment ? 0 : FMT.WIDTH - WIDTH, ToolbarMenu.HEIGHT);
		setSize(WIDTH, FMT.HEIGHT - ToolbarMenu.HEIGHT);
		scrollable.setSize(WIDTH, getSize().y - topSpace());
		scrollable.setHorizontalScrollBarVisible(false);
		alignComponents();
		if(scrollable.getContainer().getSize().y < getSize().y - topSpace()){
			scrollable.getContainer().setSize(WIDTH, getSize().y - topSpace());
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
		if(LEFT == this) LEFT = null;
		if(RIGHT == this) RIGHT = null;
	}
	
	public void show(){
		for(Editor editor : EDITORLIST) if(editor.alignment == alignment) editor.hide();
		getStyle().setDisplay(DisplayType.MANUAL);
		this.setEnabled(true);
		if(alignment) LEFT = this;
		else RIGHT = this;
	}
	
	public void toggle(){
		if(getStyle().getDisplay() == DisplayType.NONE) show();
		else hide();
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
		if(com == null) return;
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
				editor.addComponent(EditorComponent.REGISTRY.get(selected_component).getDeclaredConstructor().newInstance());
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

	private static Editor get(boolean alignment){
		for(Editor editor : EDITORS.values()){
			if(editor.alignment == alignment && editor.isVisible()) return editor;
		}
		return null;
	}

	public static void show(String id){
		Editor editor = EDITORS.get(id);
		if(editor == null) return;
		Editor other = get(editor.alignment);
		if(other != null) other.hide();
		editor.show();
	}
	
	public static class SPVSL extends ScrollablePanelViewportScrollListener {
		
	    @Override
	    public void process(@SuppressWarnings("rawtypes") ScrollEvent event){
	    	if(FMT.SELFIELD != null || FMT.FRAME.getLayers().size() > 0) return;
	    	else super.process(event);
	    }
	    
	}
	
}
