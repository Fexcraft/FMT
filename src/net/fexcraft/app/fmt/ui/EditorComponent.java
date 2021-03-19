package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.utils.Translator;

public class EditorComponent extends Component {
	
	public static final HashMap<Integer, EditorComponent> COMPONENTS = new HashMap<>();
	public static final HashMap<String, Class<? extends EditorComponent>> REGISTRY = new HashMap<>();
	public static final int HEIGHT = 24;
	private UpdateHolder updateholder = new UpdateHolder();
	private ArrayList<Icon> icons = new ArrayList<>();
	private boolean minimized, unpinned;
	private Label label;
	private Icon size, mup, mdw, pin, rem;
	private int uid, fullheight;
	public Editor editor;
	public int index;
	
	public EditorComponent(String key){
		this(key, 0, true);
	}
	
	public EditorComponent(String key, int fullHeight, boolean resizeable){
		while(COMPONENTS.containsKey(uid)) uid++;
		setSize(Editor.CWIDTH, fullheight = fullHeight > 0 ? fullHeight : HEIGHT * 2);
		add(label = new Label(Translator.translate("editor.component." + key + ".name"), 4, 0, 296, 24));
		Settings.applyComponentTheme(this);
		add(size = new Icon((byte)1, "./resources/textures/icons/component/size.png", () -> minimize()));
		add(pin = new Icon((byte)2, "./resources/textures/icons/component/pin.png", () -> pin()));
		add(mup = new Icon((byte)3, "./resources/textures/icons/component/move_up.png", () -> move(-1)));
		add(mdw = new Icon((byte)4, "./resources/textures/icons/component/move_down.png", () -> move(1)));
		add(rem = new Icon((byte)5, "./resources/textures/icons/component/remove.png", () -> rem()));
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
			else if(editor.comp_adj_mode && (pin.isHovered() || mup.isHovered() || mdw.isHovered() || rem.isHovered())) bool = true;
		}
		if(!bool){
			pin.getStyle().setDisplay(DisplayType.NONE);
			mup.getStyle().setDisplay(DisplayType.NONE);
			mdw.getStyle().setDisplay(DisplayType.NONE);
			rem.getStyle().setDisplay(DisplayType.NONE);
		}
		else{
			bool = !editor.comp_adj_mode || unpinned;
			pin.getStyle().setDisplay(bool ? DisplayType.NONE : DisplayType.MANUAL);
			mup.getStyle().setDisplay(bool || index <= 0 ? DisplayType.NONE : DisplayType.MANUAL);
			mdw.getStyle().setDisplay(bool || index >= editor.components.size() - 1 ? DisplayType.NONE : DisplayType.MANUAL);
			rem.getStyle().setDisplay(bool ? DisplayType.NONE : DisplayType.MANUAL);
		}
	}

	private void minimize(){
		minimized = !minimized;
		setSize(getSize().x, minimized ? HEIGHT : fullheight);
		editor.alignComponents();
	}

	private void move(int dir){
		if(unpinned) return;
		int nidx = index + dir;
		if(nidx < 0 || nidx >= editor.components.size()) return;
		editor.swap(index, index + dir);
	}

	private void pin(){
		
	}

	private void rem(){
		if(editor == null) return;
		editor.removeComponent(this);
	}

	public UpdateHolder getUpdateHolder(){
		return updateholder;
	}

	public static void registerComponents(){
		REGISTRY.put("polygon.quick", QuickAdd.class);
	}

}
