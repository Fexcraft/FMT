package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;

import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.app.json.JsonValue;

public class ConfigEntry {

    public static final ConfigEntry OBJ_SUB_ENTRY = ConfigEntry.of(EntryType.OBJECT_SUB);
	public static final ConfigEntry ARR_SUB_ENTRY = ConfigEntry.of(EntryType.ARRAY_SUB);
    public static final ConfigEntry TEXT_ENTRY = ConfigEntry.of(EntryType.TEXT);

    public String name;
    public String def;
    public String alt;
    public EntryType type;
    public boolean required;
    public boolean def_ok;
    public ArrayList<ConfigEntry> subs;
    public int defi;
    public int mini;
    public int maxi;
    public float deff;
    public float minf;
    public float maxf;
    public String[] enums;
    public boolean defb;
	public boolean static_;

    public ConfigEntry(){}

    public static ConfigEntry of(String name, EntryType type){
        return new ConfigEntry().name(name).type(type);
    }

    public static ConfigEntry of(EntryType type){
        return new ConfigEntry().type(type);
    }

    protected ConfigEntry name(String name){
        this.name = name;
        return this;
    }

    protected ConfigEntry type(EntryType type){
        this.type = type;
        return this;
    }

    protected ConfigEntry alt(String alt){
        this.alt = alt;
        return this;
    }

    protected ConfigEntry def(String def, boolean ok){
        this.def = def;
        def_ok = ok;
        return this;
    }

	protected ConfigEntry def(String def){
		return def(def, true);
	}

    protected ConfigEntry required(){
        required = true;
        return this;
    }

    public ConfigEntry add(ConfigEntry... sub){
        if(subs == null) subs = new ArrayList();
        for(ConfigEntry s : sub) subs.add(s);
        return this;
    }

    public ConfigEntry limit(int def, int min, int max){
        this.defi = def;
        this.mini = min;
        this.maxi = max;
        return this;
    }

    public ConfigEntry limit(int def, int min){
        return limit(def, min, min);
    }

    public ConfigEntry limit(float def, float min, float max){
        this.deff = def;
        this.minf = min;
        this.maxf = max;
        return this;
    }

    public ConfigEntry limit(float def, float min){
        return limit(def, min, min);
    }

    public ConfigEntry size(int size){
        deff = size;
        return this;
    }

    public ConfigEntry enums(String... values){
        enums = values;
        return this;
    }

    public ConfigEntry def(boolean bool){
        defb = bool;
        return this;
    }

	public ConfigEntry static_(boolean bool){
		static_ = bool;
		return this;
	}

    public JsonValue gendef(){
        switch(type){
			case TEXT -> {
                return new JsonValue<>(def);
			}
			case PACKID -> {
				return new JsonValue<>("gep");
			}
			case TEXLOC -> {
				return new JsonValue<>("fvtm:textures/entity/null.png");
			}
			case MODELLOC -> {
				return new JsonValue<>("null");
			}
			case INTEGER -> {
                return new JsonValue<>(defi);
			}
			case DECIMAL -> {
                return new JsonValue<>(deff);
			}
			case BOOLEAN -> {
                return new JsonValue<>(defb);
			}
			case COLOR -> {
                return new JsonValue<>(def);
			}
			case ARRAY, ARRAY_SUB, ARRAY_SIMPLE -> {
				return new JsonArray();
			}
			case OBJECT, OBJECT_SUB, OBJECT_KEY_VAL -> {
                return new JsonMap();
			}
			case ENUM -> {
                return new JsonValue<>(enums[0]);
			}
			case VECTOR_ARRAY -> {
				return new JsonArray.Flat(0, 0, 0);
			}
			case VECTOR_MAP -> {
                return new JsonMap("x", 0f, "y", 0f, "z", 0);
			}
		}
        return new JsonValue<>(def);
    }

	public SubKey key(){
		return new SubKey(name);
	}

}
