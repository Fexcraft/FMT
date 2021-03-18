package net.fexcraft.app.fmt.ui.fieds;

import java.util.function.Consumer;

import org.liquidengine.legui.component.TextInput;

import net.fexcraft.app.fmt.settings.Setting;

public class TextField extends TextInput {

	public TextField(String string, int x, int y, int w, int h){
		super(string, x, y, w, h);
		Field.setupHoverCheck(this);
	}

	public TextField(Setting<?> setting, int x, int y, int w, int h){
		this(setting.toString(), x, y, w, h);;
		Field.setupHoverCheck(this);
		this.addTextInputContentChangeEventListener(event -> setting.validate(true, Field.validateString(event, true)));
	}

	public TextField(String string, int x, int y, int w, int h, Consumer<String> cons){
		this(string, x, y, w, h);;
		Field.setupHoverCheck(this);
		this.addTextInputContentChangeEventListener(event -> cons.accept(event.getNewValue()));
	}
	
}
