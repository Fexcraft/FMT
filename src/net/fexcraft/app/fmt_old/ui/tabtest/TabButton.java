package net.fexcraft.app.fmt_old.ui.tabtest;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

public class TabButton extends Button {
	
	public static int WIDTH = 50;
	protected TabContainer root;
	protected int index;
	
	public TabButton(TabContainer root, int index, String title){
		super(title, 0, 0, WIDTH, 30);
		this.root = root;
		this.index = index;
        this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener) e -> {
        	if(CLICK == e.getAction()){
        		root.openTab(index);
        	}
        });
        this.getStyle().setBorder(null);
        this.getStyle().setBorderRadius(0);
	}

}
