package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.ArrayList;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.event.CursorEnterEvent;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.CursorEnterEventListener;
import org.liquidengine.legui.listener.MouseClickEventListener;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.utils.Settings;
import net.fexcraft.app.fmt.utils.Translator;

public class MenuButton extends Button {

	private MenuEntry entry;
	private Extension extension;
	public static final ArrayList<Extension> EXTENSIONS = new ArrayList<>();

	public MenuButton(String string, Runnable run, MenuSubButton... subs){
		super(Translator.translate(string));
		Settings.THEME_CHANGE_LISTENER.add(bool -> {
			this.getStyle().setBorderRadius(0f);
		});
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				if(run != null){
					run.run();
					entry.toggle(false);
				}
				else if(extension != null){
					showExtension();
				}
			}
		});
		this.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> {
			entry.checkClose();
			if(extension != null){
				showExtension();
			}
		});
		if(subs == null || subs.length == 0) return;
		extension = new Extension(this, MenuEntry.size, subs.length * MenuEntry.buttonheight);
		extension.getStyle().setDisplay(DisplayType.NONE);
		for(int index = 0; index < subs.length; index++){
			MenuSubButton sub = subs[index];
			Button button = new Button(sub.text, 0, index * MenuEntry.buttonheight, MenuEntry.size, MenuEntry.buttonheight);
			button.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
				if(event.getAction() == CLICK){
					if(sub.run != null) sub.run.run();
					entry.toggle(false);
				}
			});
			button.getListenerMap().addListener(CursorEnterEvent.class, (CursorEnterEventListener)lis -> {
				entry.checkClose();
			});
			Settings.THEME_CHANGE_LISTENER.add(bool -> {
				button.getStyle().setBorderRadius(0f);
			});
			extension.add(button);
		}
		/*Settings.THEME_CHANGE_LISTENER.add(bool -> {
			extension.getStyle().setBorderRadius(0f);
		});*/
		EXTENSIONS.add(extension);
		FMTB.frame.getContainer().add(extension);
	}

	private void showExtension(){
		EXTENSIONS.forEach(ext -> ext.button.anyHovered());
		extension.getStyle().setDisplay(DisplayType.MANUAL);
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
		if(extension != null){
			extension.getStyle().setDisplay(DisplayType.NONE);
		}
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
	
	public static class Extension extends Panel {
		
		public Extension(MenuButton button, int w, int h){
			super(0, 0, w, h);
			this.button = button;
		}

		public MenuButton button;
		
	}
	
	public void updateExtension(int index){
		if(extension == null) return;
		extension.setPosition(entry.getPosition().x + MenuEntry.size - 1, 30 + (index * MenuEntry.buttonheight) - 1);
	}

	public boolean anyHovered(){
		boolean bool = this.isHovered() || extensionHovered();
		if(!bool && extension != null){
			extension.getStyle().setDisplay(DisplayType.NONE);
		}
		return bool;
	}

	private boolean extensionHovered(){
		if(extension == null) return false;
		if(extension.isHovered()) return true;
		for(Component com : extension.getChildComponents()){
			if(com.isHovered()) return true;
		}
		return false;
	}

}
