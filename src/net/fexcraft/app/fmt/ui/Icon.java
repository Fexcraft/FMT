package net.fexcraft.app.fmt.ui;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;
import static org.liquidengine.legui.input.Mouse.MouseButton.MOUSE_BUTTON_LEFT;

import org.liquidengine.legui.component.ImageView;
import org.liquidengine.legui.component.Tooltip;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.image.StbBackedLoadableImage;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.FontSizeUtil;
import net.fexcraft.app.fmt.utils.Translator;

public class Icon extends ImageView {
	
	public Icon(int index, String adress, MouseClickEventListener listener){
		super(new StbBackedLoadableImage(adress));
		this.setPosition(1 + (index * 29), 1);
		this.setFocusable(false);
		this.setSize(28, 28);
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		Settings.applyBorderless(getStyle());
		Settings.applyBorderless(getFocusedStyle());
		this.setFocusable(true);
	}
	
	public Icon(int index, String adress, Runnable run){
		this(index, adress, event -> {
			if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT) run.run();
		});
	}
	
	public Icon(byte index, String adress, MouseClickEventListener listener){
		super(new StbBackedLoadableImage(adress));
		int yoff = 1;
		if(index >= 10){
			index /= 10;
			yoff = 4;
		}
		this.setPosition(Editor.CWIDTH - (index * 23), yoff);
		this.setSize(22, 22);
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		Settings.applyBorderless(getStyle());
		Settings.applyBorderless(getFocusedStyle());
		this.setFocusable(true);
	}
	
	public Icon(byte index, String adress, Runnable run){
		this(index, adress, event -> {
			if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT) run.run();
		});
	}
	
	public Icon(int index, int size, int off, int x, int y, String adress, MouseClickEventListener listener){
		super(new StbBackedLoadableImage(adress));
		this.setPosition(x + (index * (size + off)), y);
		this.setFocusable(false);
		this.setSize(size, size);
		this.getListenerMap().addListener(MouseClickEvent.class, listener);
		Settings.applyBorderless(getStyle());
		Settings.applyBorderless(getFocusedStyle());
		this.setFocusable(true);
	}
	
	public Icon(int index, int size, int off, int x, int y, String adress, Runnable run){
		this(index, size, off, x, y, adress, event -> {
			if(event.getAction() == CLICK && event.getButton() == MOUSE_BUTTON_LEFT) run.run();
		});
	}

	public Icon addTooltip(String string, boolean alignment){
		Tooltip tip = new Tooltip(Translator.translate(string));
		tip.setSize(FontSizeUtil.getWidth(tip.getTextState().getText()) + 8, 24);
		tip.getStyle().setPadding(2f);
		tip.setPosition(alignment ? getSize().x : -tip.getSize().x, (getSize().y - 24) / 2);
		tip.getStyle().setBorderRadius(0f);
		this.setTooltip(tip);
		return this;
	}

	public Icon addTooltip(String string){
		return addTooltip(string, true);
	}
	
}
