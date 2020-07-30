package net.fexcraft.app.fmt.ui.field;

import java.util.function.Consumer;

import org.liquidengine.legui.component.TextInput;

import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.utils.Setting;

public class TextField extends TextInput {

	public TextField(String string, int x, int y, int w, int h){
		super(string, x, y, w, h); UserInterfaceUtils.setupHoverCheck(this);
	}

	@SuppressWarnings("unchecked")
	public TextField(Setting setting, int x, int y, int w, int h){
		this(setting.toString(), x, y, w, h); UserInterfaceUtils.setupHoverCheck(this);
		this.addTextInputContentChangeEventListener(event -> {
			setting.validateAndApply(UserInterfaceUtils.validateString(event, true));
		});
	}

	public TextField(String string, int x, int y, int w, int h, Consumer<String> cons){
		this(string, x, y, w, h); UserInterfaceUtils.setupHoverCheck(this);
		this.addTextInputContentChangeEventListener(event -> {
			cons.accept(event.getNewValue());
		});
	}
	
}
