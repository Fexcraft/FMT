package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.components.QuickAdd;
import net.fexcraft.app.fmt.utils.Translator;

public class EditorComponent extends Component {
	
	public static final HashMap<Integer, EditorComponent> COMPONENTS = new HashMap<>();
	public static final HashMap<String, Class<? extends EditorComponent>> REGISTRY = new HashMap<>();
	public static final int HEIGHT = 24;
	private ArrayList<Icon> icons = new ArrayList<>();
	private boolean minimized, unpinned;
	private Label label;
	private Icon size, mu, md, pin;
	private int uid, fullheight;
	public Editor editor;
	public int index;
	
	public EditorComponent(String key){
		this(key, true);
	}
	
	public EditorComponent(String key, boolean resizeable){
		while(COMPONENTS.containsKey(uid)) uid++;
		setSize(Editor.CWIDTH, fullheight = HEIGHT * 2);
		add(label = new Label(Translator.translate(key), 0, 0, 300, 24));
		Settings.applyComponentTheme(this).accept(Settings.SELTHEME);
		add(size = new Icon((byte)1, "./resources/textures/icons/component/size.png", () -> minimize()));
		add(mu = new Icon((byte)2, "./resources/textures/icons/component/move_up.png", () -> move(-1)));
		add(md = new Icon((byte)3, "./resources/textures/icons/component/move_down.png", () -> move(1)));
		add(pin = new Icon((byte)4, "./resources/textures/icons/component/pin.png", () -> pin()));
		icons.add(mu); icons.add(md); icons.add(pin);
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
		for(Icon icon : icons) if(icon.isHovered()) bool = true;
		DisplayType type = bool ? DisplayType.MANUAL : DisplayType.NONE;
		mu.getStyle().setDisplay(!unpinned && index <= 0 ? DisplayType.NONE : type);
		md.getStyle().setDisplay(!unpinned && index >= editor.components.size() - 1 ? DisplayType.NONE : type);
		pin.getStyle().setDisplay(type);
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

	public static void registerComponents(){
		REGISTRY.put("polygon.quick", QuickAdd.class);
	}

}
