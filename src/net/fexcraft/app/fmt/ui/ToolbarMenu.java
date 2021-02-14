package net.fexcraft.app.fmt.ui;

import static net.fexcraft.app.fmt.utils.Translator.translate;
import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.liquidengine.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

import java.util.ArrayList;

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
	
	public static final int WIDTH = 120, HEIGHT = 30;
	private Component[] components;
	private MenuLayer layer;
	
	public ToolbarMenu(int index, String id, Component... components){
		super(176 + (index * (WIDTH + 1)), 0, WIDTH, HEIGHT);
		this.getTextState().setText(translate("toolbar." + id));
		Settings.applyMenuTheme(this);
		//if(components.length == 0) return;
		this.components = components;
		layer = new MenuLayer(this);
		this.getListenerMap().addListener(MouseClickEvent.class, event -> {
			if(event.getAction() != CLICK || event.getButton() != MOUSE_BUTTON_LEFT) return;
			layer.show();
		});
		this.getListenerMap().addListener(CursorEnterEvent.class, event -> {
			if(!event.isEntered()) return;
			layer.show();
		});
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

        public MenuLayer(ToolbarMenu menu){
    		Settings.applyBorderless(this);
    		Settings.THEME_CHANGE_LISTENERS.add(bool -> {
    			float w = bool ? 0 : 1;
    			this.getStyle().getBackground().setColor(w, w, w, 1);
    		});
            setEventReceivable(true);
            setEventPassable(true);
            LAYERS.add(this);
            this.setSize(WIDTH, menu.components.length * (HEIGHT + 1));
            this.setPosition(menu.getPosition().add(0, HEIGHT, new Vector2f()));
        	CursorEnterEventListener listener = lis -> {
        		if(!lis.isEntered()){
        			boolean out = true;
                	for(Component com : menu.components){
                		if(com.isHovered()){
                			out = false;
                			break;
                		}
                	}
                	if(out) FMT.FRAME.removeLayer(this);
        		}
    		};
        	for(Component com : menu.components){
        		com.getListenerMap().addListener(CursorEnterEvent.class, listener);
        		Settings.applyMenuTheme(com);
        		this.add(com);
        	}
        }

		public void show(){
			for(MenuLayer layer : LAYERS) layer.hide();
			FMT.FRAME.addLayer(this);
			shown = true;
		}
		
		public void hide(){
			if(shown){
				FMT.FRAME.removeLayer(this);
				shown = false;
			}
		}
        
	}

}
