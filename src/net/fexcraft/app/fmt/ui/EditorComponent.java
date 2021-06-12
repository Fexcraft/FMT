package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.components.BoxComponent;
import net.fexcraft.app.fmt.ui.components.CylinderComponentBasic;
import net.fexcraft.app.fmt.ui.components.CylinderComponentFull;
import net.fexcraft.app.fmt.ui.components.CylinderComponentSimple;
import net.fexcraft.app.fmt.ui.components.GroupGeneral;
import net.fexcraft.app.fmt.ui.components.MarkerComponent;
import net.fexcraft.app.fmt.ui.components.PolygonGeneral;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.ui.components.ShapeboxComponent;
import net.fexcraft.app.fmt.utils.Translator;

public class EditorComponent extends Component {

	public static final float F20 = 8, F21 = 154f, F2S = 138;
	public static final float F30 = 7.5f, F31 = 105f, F32 = 202.5f, F3S = 90;
	public static final float F60 = 6, F61 = 55, F62 = 104, F63 = 153, F64 = 202, F65 = 251, F6S = 43;
	public static final float L5 = 5f, LW = Editor.CWIDTH - (L5 * 2);
	public static final int HEIGHT = 24;
	private static final byte[] orderT = { 1, 2, 4, 5, 3 }, orderE = { 1, 2, 3, 4, 5 };
	//
	public static final HashMap<Integer, EditorComponent> COMPONENTS = new HashMap<>();
	public static final LinkedHashMap<String, Class<? extends EditorComponent>> REGISTRY = new LinkedHashMap<>();
	public static final String LANG_PREFIX = "editor.component.";
	protected UpdateHolder updateholder = new UpdateHolder();
	private ArrayList<Icon> icons = new ArrayList<>();
	protected boolean minimized, unpinned, tree;
	protected Label label;
	private Icon size, mup, mdw, pin, rem;
	protected int uid, fullheight, row;
	public Editor editor;
	protected String id;
	public int index;
	
	public EditorComponent(String key){
		this(key, 0, false, true);
	}
	
	public EditorComponent(String key, int fullHeight, boolean tree, boolean resizeable){
		while(COMPONENTS.containsKey(uid)) uid++;
		setSize(Editor.CWIDTH, fullheight = fullHeight > 0 ? fullHeight : HEIGHT * 2);
		add(label = new Label(Translator.translate(LANG_PREFIX + (id = key) + ".name"), 4, 0, 296, 24));
		label.getStyle().setFontSize(22f);
		Settings.applyComponentTheme(this);
		byte[] order = (this.tree = tree) ? orderT : orderE;
		add(size = new Icon(order[0], "./resources/textures/icons/component/" + (tree ? "minimize" : "size") + ".png", () -> minimize(null)));
		add(pin = new Icon(order[1], "./resources/textures/icons/component/" + (tree ? "visible" : "pin") + ".png", () -> pin()));
		add(mup = new Icon(order[2], "./resources/textures/icons/component/move_up.png", () -> move(-1)));
		add(mdw = new Icon(order[3], "./resources/textures/icons/component/move_down.png", () -> move(1)));
		add(rem = new Icon(order[4], "./resources/textures/icons/component/remove.png", () -> rem()));
		icons.add(pin);
		icons.add(mup);
		icons.add(mdw);
		icons.add(rem);
		CursorEnterEventListener listener = l -> toggleIcons();
		label.getListenerMap().addListener(CursorEnterEvent.class, listener);
		for(Icon icon : icons){
			icon.getListenerMap().addListener(CursorEnterEvent.class, listener);
			icon.getStyle().setDisplay(DisplayType.NONE);
		}
		if(!resizeable) size.getStyle().setDisplay(DisplayType.NONE);
	}

	private void toggleIcons(){
		boolean bool = label.isHovered();
		if(!bool){
			if(size.isHovered()) bool = true;
			else if((editor.comp_adj_mode || tree) && (pin.isHovered() || mup.isHovered() || mdw.isHovered() || rem.isHovered())) bool = true;
		}
		if(!bool){
			pin.getStyle().setDisplay(DisplayType.NONE);
			mup.getStyle().setDisplay(DisplayType.NONE);
			mdw.getStyle().setDisplay(DisplayType.NONE);
			rem.getStyle().setDisplay(DisplayType.NONE);
		}
		else{
			bool = (!editor.comp_adj_mode || unpinned) && !tree;
			pin.getStyle().setDisplay(bool ? DisplayType.NONE : DisplayType.MANUAL);
			mup.getStyle().setDisplay(bool || index <= 0 ? DisplayType.NONE : DisplayType.MANUAL);
			mdw.getStyle().setDisplay(bool || index >= editor.components.size() - 1 ? DisplayType.NONE : DisplayType.MANUAL);
			rem.getStyle().setDisplay(bool ? DisplayType.NONE : DisplayType.MANUAL);
		}
	}

	protected void minimize(Boolean bool){
		this.minimized = bool == null ? !minimized : bool;
		setSize(getSize().x, minimized ? HEIGHT : fullheight);
		if(editor != null) editor.alignComponents();
	}

	protected boolean move(int dir){
		if(unpinned) return false;
		int nidx = index + dir;
		if(nidx < 0 || nidx >= editor.components.size()) return false;
		editor.swap(index, index + dir);
		return true;
	}

	protected void pin(){
		
	}

	protected void rem(){
		if(editor == null) return;
		editor.removeComponent(this);
	}

	public UpdateHolder getUpdateHolder(){
		return updateholder;
	}
	
	public int row(int next){
		if(next > 0) row += next * 25;
		return row;
	}
	
	public int row(){
		return row;
	}

	public static void registerComponents(){
		REGISTRY.put("polygon.quick", QuickAdd.class);
		REGISTRY.put("polygon.general", PolygonGeneral.class);
		REGISTRY.put("polygon.box", BoxComponent.class);
		REGISTRY.put("polygon.shapebox", ShapeboxComponent.class);
		REGISTRY.put("polygon.cylinder.all", CylinderComponentFull.class);
		REGISTRY.put("polygon.cylinder.basic", CylinderComponentBasic.class);
		REGISTRY.put("polygon.cylinder.simple", CylinderComponentSimple.class);
		REGISTRY.put("polygon.marker", MarkerComponent.class);
		REGISTRY.put("group.general", GroupGeneral.class);
	}

}
