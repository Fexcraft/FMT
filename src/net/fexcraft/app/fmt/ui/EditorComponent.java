package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.liquidengine.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Translator;

public class EditorComponent extends Component {
	
	public static final HashMap<Integer, EditorComponent> COMPONENTS = new HashMap<>();
	public static final int HEIGHT = 24;
	private ArrayList<Icon> icons = new ArrayList<>();
	private boolean minimized, unpinned;
	private Label label;
	private Icon size, mu, md, pin;
	private int uid, fullheight;
	public Editor editor;
	public int index;
	
	public EditorComponent(String key){
		while(COMPONENTS.containsKey(uid)) uid++;
		setSize(Editor.CWIDTH, HEIGHT);
		add(label = new Label(Translator.translate(key), 0, 0, 300, 24));
		Settings.applyComponentTheme(this);
		add(size = new Icon(1, "./resources/textures/icons/component/size.png", () -> minimize()));
		add(mu = new Icon(2, "./resources/textures/icons/component/move_up.png", () -> move(-1)));
		add(md = new Icon(3, "./resources/textures/icons/component/move_down.png", () -> move(1)));
		add(pin = new Icon(4, "./resources/textures/icons/component/pin.png", () -> pin()));
		icons.add(mu); icons.add(md); icons.add(pin);
		CursorEnterEventListener listener = l -> toggleIcons();
		label.getListenerMap().addListener(CursorEnterEvent.class, listener);
		for(Icon icon : icons){
			icon.getListenerMap().addListener(CursorEnterEvent.class, listener);
			icon.getStyle().setDisplay(DisplayType.NONE);
		}
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
	
	public class Icon extends ImageView {
		
		public Icon(int index, String adress, MouseClickEventListener listener){
			super(new StbBackedLoadableImage(adress));
			this.setPosition(Editor.CWIDTH - (index * 23), 1);
			this.setSize(22, 22);
			this.getListenerMap().addListener(MouseClickEvent.class, listener);
			Settings.applyBorderless(this.getStyle());
			Settings.applyBorderless(this.getFocusedStyle());
		}
		
		public Icon(int index, String adress, Runnable run){
			this(index, adress, event -> {
				if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT) run.run();
			});
		}
		
	}

}
