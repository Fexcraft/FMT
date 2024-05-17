package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import net.fexcraft.app.fmt.ui.SettingsDialog.SPVSL;
import net.fexcraft.app.fmt.ui.trees.Trees;
import net.fexcraft.app.json.JsonValue;
import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.component.Dialog;
import com.spinyowl.legui.component.Label;
import com.spinyowl.legui.component.ScrollablePanel;
import com.spinyowl.legui.component.SelectBox;
import com.spinyowl.legui.component.TextArea;
import com.spinyowl.legui.component.misc.listener.scrollablepanel.ScrollablePanelViewportScrollListener;
import com.spinyowl.legui.component.optional.align.HorizontalAlign;
import com.spinyowl.legui.component.optional.align.VerticalAlign;
import com.spinyowl.legui.event.CursorEnterEvent;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.event.ScrollEvent;
import com.spinyowl.legui.input.Mouse.MouseButton;
import com.spinyowl.legui.listener.CursorEnterEventListener;
import com.spinyowl.legui.style.Border;
import com.spinyowl.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.fields.TextField;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.Translator.Translations;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Editor extends Component {

	public static final HashMap<String, Editor> EDITORS = new HashMap<>();
	public static Editor POLYGON_EDITOR;
	public static Editor GROUP_EDITOR;
	public static Editor PIVOT_EDITOR;
	public static Editor MODEL_EDITOR;
	public static Editor TEXTURE_EDITOR;
	public static Editor UV_EDITOR;
	public static Editor PREVIEW_EDITOR;
	public static Editor POLYGON_TREE;
	public static Editor PIVOT_TREE;
	public static Editor TEXTURE_TREE;
	public static Editor PREVIEW_TREE;
	public static float RATE = 1f;
	public static Editor VISIBLE_EDITOR = null;
	public static Editor VISIBLE_TREE = null;
	public ArrayList<EditorComponent> components = new ArrayList<>();
	private ScrollablePanel scrollable;
	private Icon[] trees;
	private Label label;
	public static int CWIDTH = 300, WIDTH = 310, LABEL = 30;
	public boolean comp_adj_mode, tree;
	public final String id;
	public String name;
	
	public Editor(String id, String name, boolean tree){
		Settings.applyBorderless(this);
		this.setFocusable(false);
		EDITORS.put(this.id = id, this);
		this.tree = tree;
		add(scrollable = new ScrollablePanel(0, topSpace(), WIDTH, getSize().y));
		scrollable.getViewport().getListenerMap().removeAllListeners(ScrollEvent.class);
		scrollable.getViewport().getListenerMap().addListener(ScrollEvent.class, new SPVSL());
		Settings.applyBorderlessScrollable(scrollable, true);
		add(label = new Label(this.name = name, 5, 0, CWIDTH - 10, LABEL));
		label.getStyle().setFontSize(26f);
		align();
		hide();
	}

	protected float topSpace(){
		return LABEL;
	}
	
	protected void addTreeIcons(Trees type){
		byte idx = 0, t = 0;
		trees = new Icon[4];
		if(type != Trees.POLYGON) add(trees[t++] = new Icon(idx += 10, "./resources/textures/icons/tree/polygon.png", () -> Editor.show(Trees.POLYGON.id)).addTooltip("editor.tree.polygon", false));
		if(type != Trees.PIVOT) add(trees[t++] = new Icon(idx += 10, "./resources/textures/icons/tree/pivot.png", () -> Editor.show(Trees.PIVOT.id)).addTooltip("editor.tree.pivot", false));
		if(type != Trees.HELPER) add(trees[t++] = new Icon(idx += 10, "./resources/textures/icons/tree/helper.png", () -> Editor.show(Trees.HELPER.id)).addTooltip("editor.tree.helper", false));
		if(type != Trees.TEXTURE) add(trees[t++] = new Icon(idx += 10, "./resources/textures/icons/tree/textures.png", () -> Editor.show(Trees.TEXTURE.id)).addTooltip("editor.tree.texture", false));
		if(type != Trees.ANIMATION) add(trees[t++] = new Icon(idx += 10, "./resources/textures/icons/tree/fvtm.png", () -> Editor.show(Trees.ANIMATION.id)).addTooltip("editor.tree.animation", false));
		//if(trees != null) for(Icon icon : trees) icon.getStyle().setDisplay(DisplayType.NONE);
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

	public void align(){
		setPosition(!tree ? 0 : FMT.WIDTH - WIDTH, ToolbarMenu.HEIGHT);
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
		if(VISIBLE_EDITOR == this) VISIBLE_EDITOR = null;
		if(VISIBLE_TREE == this) VISIBLE_TREE = null;
	}
	
	public void show(){
		if(tree){
			if(VISIBLE_TREE != null) VISIBLE_TREE.hide();
			VISIBLE_TREE = this;
		}
		else{
			if(VISIBLE_EDITOR != null) VISIBLE_EDITOR.hide();
			VISIBLE_EDITOR = this;
		}
		getStyle().setDisplay(DisplayType.MANUAL);
		setEnabled(true);
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
		UpdateHandler.register(com.updcom);
		alignComponents();
	}

	public void removeComponent(EditorComponent com){
		if(com == null) return;
		components.remove(com);
		scrollable.getContainer().remove(com);
		UpdateHandler.deregister(com.updcom);
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

	private static Editor byName(String name){
		for(Editor editor : EDITORS.values()){
			if(editor.name.equals(name)) return editor;
		}
		return null;
	}

	public static void show(String id){
		Editor editor = EDITORS.get(id);
		if(editor == null) return;
		if(editor.tree){
			if(VISIBLE_TREE != null) VISIBLE_TREE.hide();
		}
		else{
			if(VISIBLE_EDITOR != null) VISIBLE_EDITOR.hide();
		}
		editor.show();
	}
	
	public static class SPVSL extends ScrollablePanelViewportScrollListener {
		
	    @Override
	    public void process(@SuppressWarnings("rawtypes") ScrollEvent event){
	    	if(FMT.SELFIELD != null || FMT.FRAME.getLayers().size() > 0) return;
	    	else super.process(event);
	    }
	    
	}

	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("name", name);
		map.add("shown", this.getStyle().getDisplay() != DisplayType.NONE);
		if(components.size() > 0){
			JsonArray array = new JsonArray();
			for(EditorComponent component : components){
				JsonMap com = component.save();
				if(com != null) array.add(com);
				else array.add(component.id);
			}
			map.add("components", array);
		}
		return map;
	}
	
}
