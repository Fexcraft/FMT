package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Frame;
import org.liquidengine.legui.component.Label;
import org.liquidengine.legui.component.Panel;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.WindowSizeEvent;
import org.liquidengine.legui.style.Style.DisplayType;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.utils.Print;

public class UserInterpanels {
	
	public static Toolbar TOOLBAR = new Toolbar();
	
	public static class Toolbar extends Panel {
		
		private Toolbar(){
			super(0, 0, FMTB.WIDTH, 30);
			this.getListenerMap().addListener(WindowSizeEvent.class, event -> this.setSize(event.getWidth(), 30));
		}
		
	}

	public static void addToolbarButtons(Frame frame){
		frame.getContainer().add(new MenuEntry(0, "File",
			new MenuButton("New..."),
			new MenuButton("Open"),
			new MenuButton("Save"),
			new MenuButton("Save as..."),
			new MenuButton("Import <<"),
			new MenuButton("Export >>"),
			new MenuButton("Exit")
		));
	}
	
	public static class MenuEntry extends Panel {
		
		private MenuButton[] buttons;
		private boolean extended;
		private int index;
		
		public MenuEntry(int index, String title, MenuButton... buttons){
			super(1, 1, 100, 28); this.buttons = buttons;
			for(int i = 0; i < buttons.length; i++){
				this.add(buttons[i]); buttons[i].hide();
				buttons[i].setPosition(1, 28 + (i * 26));
				buttons[i].getTextState().setFontSize(20); buttons[i].setSize(98, 24);
				buttons[i].getTextState().setHorizontalAlign(HorizontalAlign.LEFT);
			}
			this.getStyle().setBorderRadius(0f);
			Label tatle = new Label(title, 4, 0, 50, 28);
			this.add(tatle); tatle.getTextState().setFontSize(28);
			this.getListenerMap().addListener(MouseClickEvent.class, event -> {
				if(extended){
					for(MenuButton button : buttons){ button.hide(); }
					this.setSize(100, 28); extended = false;
				}
				else{
					this.setSize(100, 28 + (buttons.length * 26));
					for(MenuButton button : buttons){ button.show(); }
					extended = true; Print.console("extending " + (28 + (buttons.length * 26)));
				}
				this.setFocused(false);
			});
		}
		
	}
	
	public static class MenuButton extends Button {
		
		public MenuButton(String string){
			super(string); this.getStyle().setBorderRadius(0f);
		}

		public void toggle(){
			if(isVisible()) hide(); else show();
		}
		
		public void hide(){
			this.getStyle().setDisplay(DisplayType.NONE);
		}
		
		public void show(){
			this.getStyle().setDisplay(DisplayType.MANUAL);
		}
		
	}

}
