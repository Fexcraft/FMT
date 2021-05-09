package net.fexcraft.app.fmt.ui.fieds;

import java.util.function.Consumer;

import org.liquidengine.legui.component.TextInput;

import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;

public class TextField extends TextInput {

	public TextField(String string, float x, float y, float w, float h){
		super(string, x, y, w, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		//Field.setupHoverCheck(this);
	}

	public TextField(Setting<?> setting, float x, float y, float w, float h){
		this(setting.toString(), x, y, w, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		//Field.setupHoverCheck(this);
		this.addTextInputContentChangeEventListener(event -> setting.validate(true, Field.validateString(event, true)));
	}

	public TextField(String string, float x, float y, float w, float h, boolean basic){
		this(string, x, y, w, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		//Field.setupHoverCheck(this);
		this.addTextInputContentChangeEventListener(event -> Field.validateString(event, basic));
	}
	
	public TextField accept(Consumer<String> cons){
		this.addTextInputContentChangeEventListener(event -> cons.accept(event.getNewValue()));
		return this;
	}
	
}
