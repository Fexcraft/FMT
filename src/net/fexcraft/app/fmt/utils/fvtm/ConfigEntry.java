package net.fexcraft.app.fmt.utils.fvtm;

import java.util.ArrayList;

import net.fexcraft.app.fmt.polygon.uv.UVCoords;
import net.fexcraft.app.fmt.utils.Logging;

public class ConfigEntry {

    public static final ConfigEntry EMPTY = ConfigEntry.of(EntryType.OBJECT);
    public static final ConfigEntry TEXT = ConfigEntry.of(EntryType.TEXT);

    public String name, def;
    public EntryType type;
    public boolean required, def_ok;
    public ArrayList<ConfigEntry> subs;
    public int defi, mini, maxi;
    public float deff, minf, maxf;
    public String[] enums;
    public boolean defb;

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

    protected ConfigEntry def(String def, boolean ok){
        this.def = def;
        def_ok = ok;
        return this;
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

    public String typedef(){
        if(type == EntryType.INTEGER) return "" + defi;
        if(type == EntryType.DECIMAL) return "" + deff;
        return def;
    }
}
