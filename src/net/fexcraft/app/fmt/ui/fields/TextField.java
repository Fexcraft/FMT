package net.fexcraft.app.fmt.ui.fields;

import java.util.function.Consumer;

import org.liquidengine.legui.component.TextInput;

import net.fexcraft.app.fmt.attributes.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;

public class TextField extends TextInput {

	public TextField(String string, float x, float y, float w, float h){
		super(string, x, y, w, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		//Field.setupHoverCheck(this);
	}

	public TextField(Setting<String> setting, float x, float y, float w, float h){
		this(setting.value.toString(), x, y, w, h);
		Settings.applyMenuTheme(this);
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
	
	public static class TextFieldField extends TextField implements Field {

		private Setting<String> setting;
		
		public TextFieldField(Setting<String> setting, float x, float y, float w, float h){
			super(setting, x, y, w, h);
			this.setting = setting;
		}
		
		@Override
		public float value(){
			return 0;
		}

		@Override
		public float test(float value, boolean additive, float rate){
			return 0;
		}

		@Override
		public Field apply(float value){
			return this;
		}

		@Override
		public void scroll(double yoffset){
			//
		}

		@Override
		public String id(){
			return null;
		}

		@Override
		public PolygonValue polyval(){
			return null;
		}

		@Override
		public Setting<?> setting(){
			return setting;
		}
		
	}
	
}
