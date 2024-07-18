package net.fexcraft.app.fmt.settings;

import java.util.function.Consumer;

import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import com.spinyowl.legui.component.Component;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import net.fexcraft.app.fmt.polygon.PolyRenderer;
import net.fexcraft.app.fmt.ui.fields.BoolButton;
import net.fexcraft.app.fmt.ui.fields.ColorField;
import net.fexcraft.app.fmt.ui.fields.NumberField;
import net.fexcraft.app.fmt.ui.fields.TextField.TextFieldField;
import net.fexcraft.app.fmt.utils.JsonUtil;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

public class Setting<TYPE> {
	
	public final String id, group;
	public TYPE _default, value;
	protected TYPE min, max;
	protected Consumer<?> cons;
	
	public Setting(String id, TYPE def, String group){
		this._default = value = def;
		this.group = group;
		this.id = id;
		if(group != null) Settings.register(group, id, this);
	}
	
	public Setting(String id, TYPE def, String group, JsonMap obj){
		this(id, def, group);
		load(obj.getMap(group));
		//Settings.register(group, id, this);
	}
	
	public void load(JsonMap obj){
		value = obj.get(id, _default);
	}

	public <VALUE> VALUE value(){
		return (VALUE)value;
	}

	public <V> V value(Class<V> type){
		return (V)value;
	}

	public boolean bool(){
		return value instanceof Boolean ? (boolean)value : Boolean.parseBoolean(value.toString());
	}
	
	public void value(Object newval){
		this.value = (TYPE)newval;
	}

	public void save(JsonMap obj){
		obj.add(id, JsonUtil.toJson(value));
	}

	public void validate(boolean apply, String string){
		if(value instanceof String) value = (TYPE)string;
	}

	public boolean toggle(){
		if(value instanceof Boolean){
			value(!bool());
		}
		return value();
	}
	
	public Setting<TYPE> minmax(TYPE min, TYPE max){
		this.min = min;
		this.max = max;
		return this;
	}
	
	public Setting<TYPE> consumer(Consumer<?> cons){
		this.cons = cons;
		return this;
	}

	public Component createField(Component root, UpdateCompound updcom, int x, int y, int w, int h){
		if(value instanceof Boolean){
			return new BoolButton((Setting<Boolean>)this, x, y, w, h);
		}
		if(value instanceof RGB){
			return new ColorField(root, (Setting<RGB>)this, x, y, w, h);
		}
		if(value instanceof String){
			return new TextFieldField((Setting<String>)this, x, y, w, h).accept((Consumer<String>)cons);
		}
		boolean flt = value instanceof Float;
		if(flt || value instanceof Integer){
			float min = this.min == null ? Integer.MIN_VALUE : flt ? (float)this.min : (int)this.min;
			float max = this.max == null ? Integer.MAX_VALUE : flt ? (float)this.max : (int)this.max;
			return new NumberField(this, x, y, w, h).setup(min, max, value instanceof Float, field -> {
				value = value instanceof Float ? (TYPE)(Object)field.value() : (TYPE)(Object)(int)field.value();
				if(cons != null) ((Consumer<NumberField>)cons).accept(field);
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
			PolyRenderer.SELCOLOR[0] = arr[0];
			PolyRenderer.SELCOLOR[1] = arr[1];
			PolyRenderer.SELCOLOR[2] = arr[2];
		}
		if(FMT.WORKSPACE != null && group.equals("workspace")) FMT.WORKSPACE.update(this);
	}

	public boolean basicstr(){
		return true;
	}

}
