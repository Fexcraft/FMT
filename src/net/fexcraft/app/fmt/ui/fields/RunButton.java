package net.fexcraft.app.fmt.ui.fields;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.input.Mouse.MouseButton;

import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Translator;

public class RunButton extends Button {

	public RunButton(String str, float x, float y, float w, float h, Runnable run, boolean borderless){
		super(Translator.translate(str), x, y, w, h);
		if(borderless) Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		this.getListenerMap().addListener(MouseClickEvent.class, l -> {
			if(l.getAction() == MouseClickAction.CLICK && l.getButton() == MouseButton.MOUSE_BUTTON_LEFT) run.run();
		});
	}
	
	public RunButton(String str, float x, float y, float w, float h, Runnable run){
		this(str, x, y, w, h, run, true);
	}

}
