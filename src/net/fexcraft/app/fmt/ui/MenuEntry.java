package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;

import org.joml.Vector4f;
import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Background;

import net.fexcraft.app.fmt.utils.SaveLoad;
import net.fexcraft.app.fmt.utils.Settings;

public class MenuEntry extends Panel {

	private static ArrayList<MenuEntry> entries = new ArrayList<>();
	public MenuButton[] buttons;
	private boolean extended;
	public final int index;
	public static final int size = 135;
	public static final int buttonheight = 24;

	public MenuEntry(int index, String title, MenuButton... buttons){
		super(187 + (index * (size + 2)), 1, size, 28);
		Label tatle = new Label(title, 4, 0, 50, 28);
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			this.getStyle().setBorderRadius(0f);
			tatle.getStyle().setFontSize(28f);
			tatle.setFocusable(false);
			Background background = new Background();
			if(bool){
				background.setColor(new Vector4f(0.2f, 0.2f, 0.2f, 1));
			}
			else{
				background.setColor(new Vector4f(0.9f, 0.9f, 0.9f, 1));
			}
			this.getStyle().setBackground(background);
			for(Button button : buttons){
				button.getStyle().setHorizontalAlign(HorizontalAlign.LEFT);
			}
			this.getHoveredStyle().getBackground().setColor(new Vector4f(background.getColor()).mul(0.8f, 0.8f, 0.8f, 1f));
		});
		this.add(tatle);
		this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> {
			if(!lis.isEntered()) this.checkClose();
		});
		//
		this.buttons = buttons;
		this.index = index;
		if(buttons == null || buttons.length == 0){// assumably this is the exit button
			this.buttons = new MenuButton[0];
			tatle.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
				if(event.getAction() == CLICK) SaveLoad.checkIfShouldSave(true, false);
			});
			this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
				if(event.getAction() == CLICK) SaveLoad.checkIfShouldSave(true, false);
			});
			return;
		}
		for(int i = 0; i < buttons.length; i++){
			this.add(buttons[i]);
			buttons[i].hide();
			buttons[i].setEntry(this);
			buttons[i].setPosition(1, 28 + (i * buttonheight));
			buttons[i].setSize(size, 24);
			buttons[i].updateExtension(i);
		}
		tatle.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK) toggle(null);
		});
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK) toggle(null);
		});
		entries.add(this);
	}

	public void toggle(Boolean bool){
		bool = bool == null ? extended : !bool;
		if(bool){
			for(MenuButton button : buttons){
				button.hide();
			}
			this.setSize(size, 28);
			extended = false;
		}
		else{
			this.setSize(size, 26 + (buttons.length * buttonheight));
			for(MenuButton button : buttons){
				button.show();
			}
			extended = true;
		}
		this.setFocused(false);
	}

	public void checkClose(){
		if(this.isHovered()) return;
		for(MenuButton button : buttons){
			if(button.anyHovered()) return;
		}
		this.toggle(false);
	}

	public static boolean anyHovered(){
		for(MenuEntry entry : entries){
			if(entry.isHovered()) return true;
			for(MenuButton button : entry.buttons){
				if(button.anyHovered()) return true;
			}
		}
		return false;
	}

}