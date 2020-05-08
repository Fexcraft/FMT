package net.fexcraft.app.fmt.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.lib.common.math.RGB;
import net.fexcraft.lib.common.utils.Print;

public class Setting {
	
	private String id;
	private Type type;
	private Object value;
	
	/** For creating defaults or in-code settings. */
	public Setting(Type type, String id, Object value){
		this.type = type; this.id = id; this.value = value;
	}
	
	public Setting(String id, String value){
		this(Type.STRING, id, value);
	}
	
	public Setting(String id, int value){
		this(Type.INTEGER, id, value);
	}
	
	public Setting(String id, float value){
		this(Type.FLOAT, id, value);
	}
	
	public Setting(String id, boolean value){
		this(Type.BOOLEAN, id, value);
	}
	
	public Setting(String id, RGB value){
		this(Type.RGB, id, value);
	}
	
	public Setting(String id, float... value){
		this(Type.FLOAT_ARRAY, id, value);
	}

	/** For parsing of Settings. */
	public Setting(String type, String id, JsonElement elm){
		this.type = Type.valueOf(type.toUpperCase()); this.id = id;
		switch(this.type){
			case BOOLEAN: value = elm.getAsBoolean(); break;
			case FLOAT: value = elm.getAsFloat(); break;
			case FLOAT_ARRAY:{
				JsonArray array = elm.getAsJsonArray();
				float[] arr = new float[array.size()];
				for(int i = 0; i < arr.length; i++)
					arr[i] = array.get(i).getAsFloat();
				value = arr; break;
			}
			case INTEGER: value = elm.getAsInt(); break;
			case RGB:{
				if(elm.isJsonPrimitive()){
					value = new RGB(elm.getAsString());
				}
				else{
					JsonArray array = elm.getAsJsonArray();
					value = new RGB(array.get(0).getAsInt(), array.get(1).getAsInt(),
						array.get(2).getAsInt(),array.size() >= 4 ? array.get(3).getAsFloat() : 1f);
				}
				break;
			}
			case STRING: value = elm.getAsString(); break;
			default: value = elm; break;
		}
	}
	
	/** Don't use unless required. */
	public void setValue(Object newval){
		this.value = newval;
	}
	
	@SuppressWarnings("unchecked")
	public <U> U getValue(){
		return (U)value;
	}
	
	public boolean toggle(){
		if(value instanceof Boolean){
			value = !(boolean)value;
			if(this.id.equals("dark_theme")){
				//DialogBox.showOK(null, null, null, "settingsbox.darktheme.mayneedrestart");
				Settings.updateTheme();
			}
			if(this.id.equals("ui_debug")){
				FMTB.context.setDebugEnabled((boolean)value);
			}
			/*if(this.id.equals("no_scroll_fields")){
				DialogBox.showOK(null, null, null, "settingsbox.settings_needs_restart");
			}*/
			return (boolean)value;
		} else return false;
	}
	
	public String getId(){
		return id;
	}
	
	public Type getType(){
		return type;
	}
	
	public boolean validateAndApply(String newval){
		switch(type){
			case BOOLEAN:{
				value = Boolean.parseBoolean(newval);
				return true;
			}
			case FLOAT:{
				try{
					value = Float.parseFloat(newval);
					return true;
				}
				catch(Exception e){
					e.printStackTrace(); return false;
				}
			}
			case FLOAT_ARRAY:
				try{
					String[] arr = newval.split(",");
					float[] all = (float[])value;
					for(int i = 0; i < arr.length; i++){
						if(i >= all.length) break;
						all[i] = Float.parseFloat(arr[i]);
					} return true;
				}
				catch(Exception e){
					e.printStackTrace(); return false;
				}
			case INTEGER:
				try{
					value = Integer.parseInt(newval);
					return true;
				}
				catch(Exception e){
					e.printStackTrace(); return false;
				}
			case RGB:{
				try{
					int i = Integer.parseInt(newval.replace("#", ""), 16);
					((RGB)value).packed = i; return true;
				}
				catch(Exception e){
					e.printStackTrace(); return false;
				}
			}
			case STRING: value = newval;
			default: Print.console("Error - typeless setting.");
		}
		return true;
	}
	
	@Override
	public String toString(){
		switch(type){
			case BOOLEAN: return value + "";
			case FLOAT: return value + "";
			case FLOAT_ARRAY:{
				float[] arr = (float[])value; String str = "";
				for(int i = 0; i < arr.length; i++){
					str += arr[i]; if(i < arr.length - 1) str += ", ";
				} return str;
			}
			case INTEGER: return value + "";
			case RGB: return "#" + ((RGB)value).toString();
			case STRING: return value + "";
			default: return "[" + value + "]";
		}
	}
	
	public JsonElement save(){
		switch(type){
			case BOOLEAN: return new JsonPrimitive((boolean)value);
			case FLOAT: return new JsonPrimitive((float)value);
			case FLOAT_ARRAY:{
				JsonArray array = new JsonArray();
				float[] arr = (float[])value;
				for(int i = 0; i < arr.length; i++){
					array.add(arr[i]);
				} return array;
			}
			case INTEGER: return new JsonPrimitive((int)value);
			case RGB: return new JsonPrimitive("#" + Integer.toHexString(((RGB)value).packed));
			case STRING: return new JsonPrimitive(value.toString());
			default: break;
		}
		return new JsonPrimitive("null");
	}

	public boolean getBooleanValue(){
		if(this.getType().isBoolean()) return (boolean)value; return false;
	}

	public float getFloatValue(){
		if(this.getType().isBoolean()) return (boolean)value ? 1f : 0f;
		return value instanceof Float ? (float)value : (int)value + 0f;
	}

	public Setting copy(){
		return new Setting(type.name(), id, save());
	}

	public String getStringValue(){
		return value.toString();
	}
	
	public float directFloat(){
		return (float)value;
	}
	
	public static enum Type {
		
		STRING, BOOLEAN, INTEGER, FLOAT, RGB, FLOAT_ARRAY, STRING_ARRAY;

		public boolean isBoolean(){
			return this == BOOLEAN;
		}
		
	}
	
	public static class StringArraySetting extends Setting {
		
		private String selected;
		
		public StringArraySetting(String id, String... array){
			super(Type.STRING_ARRAY, id, array);
		}
		
		public void setSelected(String string){
			selected = string;
		}
		
		public String getSelected(){
			if(selected == null){
				if(((String[])getValue()).length < 1){
					return null;
				}
				return ((String[])getValue())[0];
			}
			return selected;
		}
		
		@Override
		public void setValue(Object newval){
			super.setValue(newval);
			selected = null;
		}
		
		@Override
		public boolean validateAndApply(String newval){
			selected = null;
			return super.validateAndApply(newval);
		}
		
		@Override 
		public String getStringValue(){
			return getSelected();
		}
		
	}

	@SuppressWarnings("unchecked")
	public <T extends Setting> T as(Class<T> clazz){
		return (T)this;
	}
	
}
