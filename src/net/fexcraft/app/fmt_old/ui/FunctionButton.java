package net.fexcraft.app.fmt_old.ui;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;

public class FunctionButton extends Button {

	public FunctionButton(String string, int x, int y, int w, int h, Runnable run){
		super(string, x, y, w, h);
		getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickAction.CLICK) run.run();
		});
	}

}
