package net.fexcraft.app.fmt.ui.fields;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Translator;

public class RunButton extends Button {

	public RunButton(String str, float x, float y, float w, float h, Runnable run){
		super(Translator.translate(str), x, y, w, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		this.getListenerMap().addListener(MouseClickEvent.class, l -> {
			if(l.getAction() == MouseClickAction.CLICK && l.getButton() == MouseButton.MOUSE_BUTTON_LEFT) run.run();
		});
		
	}

}
