package net.fexcraft.app.fmt.settings;

import org.liquidengine.legui.component.Component;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.attributes.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField.TextFieldField;
import net.fexcraft.app.fmt.utils.Jsoniser;
import net.fexcraft.app.fmt.utils.MRTRenderer;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

public class Setting<TYPE> {
	
	public final String id, group;
	public TYPE _default, value;
	
	public Setting(String id, TYPE def, String group){
		this._default = value = def;
		this.group = group;
		this.id = id;
		Settings.register(group, id, this);
	}
	
	public Setting(String id, TYPE def, String group, JsonMap obj){
		this(id, def, group);
		load(obj.getMap(group));
		//Settings.register(group, id, this);
	}
	
	public void load(JsonMap obj){
		value = (TYPE)obj.get(id, _default);
	}

	public <VALUE> VALUE value(){
		return (VALUE)value;
	}
	
	public void value(TYPE newval){
		this.value = newval;
	}

	public void save(JsonMap obj){
		obj.add(id, Jsoniser.toJson(value));
	}

	public void validate(boolean apply, String string){
		if(value instanceof String) value = (TYPE)string;
	}

	public boolean toggle(){
		if(value instanceof Boolean){
			Object obj = !(Boolean)value;
			value((TYPE)obj);
		}
		return value();
	}

	public Component createField(Component root, UpdateHolder holder, int x, int y, int w, int h){
		if(value instanceof Boolean){
			return new BoolButton((Setting<Boolean>)this, x, y, w, h);
		}
		if(value instanceof RGB){
			return new ColorField(root, (Setting<RGB>)this, x, y, w, h);
		}
		if(value instanceof String){
			return new TextFieldField((Setting<String>)this, x, y, w, h);
		}
		if(value instanceof Float || value instanceof Integer){
			float min = id.equals("rounding_digits") ? 0 : Integer.MIN_VALUE;
			float max = id.equals("rounding_digits") ? 10 : Integer.MAX_VALUE;
			NumberField field = new NumberField(this, x, y, w, h);
			return field.setup(min, max, value instanceof Float, () -> {
				value = value instanceof Float ? (TYPE)(Object)field.value() : (TYPE)(Object)(int)field.value();
				if(id.equals("rounding_digits")) NumberField.updateRoundingDigits();
			});
		}
		return null;
	}

	public void refresh(){
		if(id.equals("background")){
			FMT.background = Settings.BACKGROUND.value.toFloatArray();
		}
		if(id.equals("selection_lines")){
			float[] arr = Settings.SELECTION_LINES.value.toFloatArray();
			MRTRenderer.SELCOLOR[0] = arr[0];
			MRTRenderer.SELCOLOR[1] = arr[1];
			MRTRenderer.SELCOLOR[2] = arr[2];
		}
	}

}
