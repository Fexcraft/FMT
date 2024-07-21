package net.fexcraft.app.fmt.utils.fvtm;

public enum EntryType {

    TEXT,
    PACKID,
    TEXLOC,
    MODELLOC,
    INTEGER,
    DECIMAL,
    BOOLEAN,
    COLOR,

    ARRAY,
    ARRAY_SUB,
    OBJECT,
    OBJECT_SUB,
    ARRAY_SIMPLE,
    OBJECT_KEY_VAL,
    SEPARATE,
    ENUM,
    VECTOR,

    ;

    static{
        ARRAY.sethassub().setarr();
        ARRAY_SUB.sethassub().setarr().setsub();
        ARRAY_SIMPLE.sethassub().setarr();
        //
        OBJECT.sethassub().setmap();
        OBJECT_SUB.sethassub().setmap().setsub();
        OBJECT_KEY_VAL.sethassub().setmap();
    }

    private boolean has_sub = false;
    private boolean is_sub = false;
    private boolean is_map = false;
    private boolean is_arr = false;

    private EntryType setsub(){
        is_sub = true;
        return this;
    }

    private EntryType setmap(){
        is_map = true;
        return this;
    }

    private EntryType sethassub(){
        has_sub = true;
        return this;
    }

    private EntryType setarr(){
        is_arr = true;
        return this;
    }

    //

    public boolean subs(){
        return has_sub;
    }

    public boolean bool(){
        return this == BOOLEAN;
    }

    public boolean numer(){
        return this == INTEGER || this == DECIMAL;
    }

    public boolean color(){
        return this == COLOR;
    }

    public boolean vector(){
        return this == VECTOR;
    }

    public String icon(){
        switch(this){
            case TEXT: return "text";
            case PACKID: return "text";
            case TEXLOC: return "text";
            case MODELLOC: return "text";
            case INTEGER: return "integer";
            case DECIMAL: return "float";
            case BOOLEAN: return "bool";
            case COLOR: return "color";
            case ARRAY: return "array";
            case OBJECT: return "object";
            case OBJECT_SUB: return "object";
            case ARRAY_SIMPLE: return "array_s";
            case OBJECT_KEY_VAL: return "object_kv";
            case ENUM: return "enum";
            case VECTOR: return "array_s";
        }
        return "unknown";
    }

    public boolean select(){
        return this == PACKID || this == TEXLOC || this == MODELLOC || this == VECTOR;
    }

	public boolean map(){
        return is_map;
	}

    public boolean subtype(){
        return is_sub;
    }

    public boolean array(){
        return is_arr;
    }

}
