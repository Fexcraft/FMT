package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.liquidengine.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import org.joml.Vector2f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Layer;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;

public class ToolbarMenu extends Button {
	
	public static final HashMap<String, ToolbarMenu> MENUS = new HashMap<>();
	public static final int WIDTH = 120, HEIGHT = 30;
	private ArrayList<Component> components = new ArrayList<>();
	private MenuLayer layer;
	
	public ToolbarMenu(int index, String id, Component... comps){
		super(index < 0 ? 0 : indexWidth(index), index < 0 ? 1 + -index * 31 : 0, WIDTH, HEIGHT);
		this.getTextState().setText(translate("toolbar." + id));
		Settings.applyMenuTheme(this);
		MENUS.put(id, this);
		for(int i = 0; i < comps.length; i++) components.add(comps[i]);
		layer = new MenuLayer(this.getPosition(), components, index < 0 ? id.substring(0, id.lastIndexOf('.')) : null);
		this.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() != CLICK || event.getButton() != MOUSE_BUTTON_LEFT) return;
			layer.show();
		});
		this.getListenerMap().addListener(CursorEnterEvent.class, event -> {
			if(!event.isEntered()) return;
			layer.show();
		});
	}

	private static int indexWidth(int index){
		return 176 + (index * (WIDTH + 1));
	}

	public ToolbarMenu(int index, String id, Runnable run){
		this(index, id);
		this.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT) run.run();
		});
	}
	
	public static class MenuLayer extends Layer {
		
		public static final ArrayList<MenuLayer> LAYERS = new ArrayList<>();
		private boolean shown;
		private Vector2f pos;
		private String root;

        public MenuLayer(Vector2f pos, Collection<Component> components, String rootid){
    		Settings.applyBorderless(this);
    		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
    			float w = bool ? 0 : 1;
    			this.getStyle().getBackground().setColor(w, w, w, 1);
    		});
            setEventReceivable(true);
            setEventPassable(true);
            LAYERS.add(this);
            root = rootid;
            this.pos = pos;
            this.setSize(WIDTH, components.size() * (HEIGHT + 1));
            this.setPosition(pos.add(0, HEIGHT, new Vector2f()));
        	CursorEnterEventListener listener = lis -> {
        		if(!lis.isEntered() && !anyComponentHovered(components)) hide();
    		};
        	for(Component com : components){
        		com.getListenerMap().addListener(CursorEnterEvent.class, listener);
        		Settings.applyMenuTheme(com);
        		this.add(com);
        	}
        }

		private boolean anyComponentHovered(Collection<Component> components){
			boolean out = false;
			for(Component com : components){
        		if(com.isHovered() || anyComponentHovered(com instanceof ToolbarMenu ? ((ToolbarMenu)com).components : com.getChildComponents())){
        			out = true;
        			break;
        		}
        	}
			return out;
		}

		public void show(){
			hideAll(root == null);
			if(root != null) offset();
			FMT.FRAME.addLayer(this);
			shown = true;
		}

		public void offset(){
			int j = 0;
			float y = pos.y;
			MenuLayer layer = this;
			while(layer.root != null){
				ToolbarMenu menu = MENUS.get(layer.root);
				y += menu.getPosition().y;
				layer = menu.layer;
				j++;
			}
			setPosition(layer.getPosition().add(j * WIDTH, y, new Vector2f()));
		}
		
		public void hideAll(boolean rootless){
			for(MenuLayer layer : LAYERS){
				if(rootless || (layer.root != null && layer.root.contains(root))) layer.hide();
			}
		}

		public void hide(){
			if(shown){
				FMT.FRAME.removeLayer(this);
				shown = false;
			}
			if(root != null){
				MenuLayer layer = MENUS.get(root).layer;
				if(!layer.anyComponentHovered(layer.getChildComponents())) layer.hide();
			}
		}
        
	}
	
	public static class MenuButton extends Button {

		public MenuButton(int index){
			super(0, 1 + index * 31, WIDTH, HEIGHT);
		}
		
	}

}
