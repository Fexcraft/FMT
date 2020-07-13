package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;

public class ClickListenerButton extends Button {
	
	public ClickListenerButton(String string, int x, int y, int w, int h, Runnable runnable){
		super(string, x, y, w, h);
		this.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickAction.CLICK){
				runnable.run();
			}
		});
	}

}
