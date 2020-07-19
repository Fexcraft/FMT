package net.fexcraft.app.fmt.ui;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.component.optional.align.HorizontalAlign;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.utils.Translator;

public class ClickListenerButton extends Button {
	
	public ClickListenerButton(String string, int x, int y, int w, int h, Runnable runnable){
		super(string, x, y, w, h);
		this.getListenerMap().addListener(MouseClickEvent.class, listener -> {
			if(listener.getAction() == MouseClickAction.CLICK){
				runnable.run();
			}
		});
	}
	
	public ClickListenerButton(String string, int x, int y, int w, int h, MouseClickEventListener listener){
		super(string, x, y, w, h);
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
	}
	
	public ClickListenerButton setTooltip(String string){
		return setTooltip(string, null, null, null, null);
	}
	
	public ClickListenerButton setTooltipTR(String string, Integer x, Integer y, Integer w, Integer h){
		return setTooltip(Translator.translate(string), x, y, w, h);
	}
	
	public ClickListenerButton setTooltip(String string, Integer x, Integer y, Integer w, Integer h){
		Tooltip tip = new Tooltip(string);
		if(w != null && h != null) tip.setSize(w, h);
		if(x != null && y != null) tip.setPosition(x, y);
		tip.getStyle().setPadding(2f);
		tip.getStyle().setHorizontalAlign(HorizontalAlign.CENTER);
		this.setTooltip(tip);
		return this;
	}

}
