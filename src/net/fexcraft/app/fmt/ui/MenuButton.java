package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;

public class MenuButton extends Button {

	private MenuEntry entry;

	public MenuButton(String string, Runnable run, MenuSubButton... subs){
		super(Translator.translate(string));
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			this.getStyle().setBorderRadius(0f);
		});
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				if(run != null) run.run();
				entry.toggle(false);
			}
			else return;
		});
		this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> {
			entry.checkClose();
		});
		if(subs == null || subs.length == 0) return;
	}

	public MenuButton(String string, MouseClickEventListener listener){
		super(Translator.translate(string));
		this.getStyle().setBorderRadius(0f);
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> entry.checkClose());
	}

	public MenuButton(String string){
		this(string, (Runnable)null);
	}

	public MenuButton setEntry(MenuEntry entry){
		this.entry = entry;
		return this;
	}

	public void toggle(){
		if(isVisible()) hide();
		else show();
	}

	public void hide(){
		this.getStyle().setDisplay(DisplayType.NONE);
		this.setSize(MenuEntry.size, 24);
	}

	public void show(){
		this.getStyle().setDisplay(DisplayType.MANUAL);
	}
	
	public static class MenuSubButton {
		
		public String text;
		public Runnable run;

		public MenuSubButton(String string, Runnable run){
			this.text = Translator.translate(string);
			this.run = run;
		}

	}

}
