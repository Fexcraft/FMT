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
    OBJECT,
    ARRAY_SIMPLE,
    OBJECT_KEY_VAL,
    SEPARATE,
    ENUM,

    POSITION,
    ROTATION,

    ;

    public boolean subs(){
        return this == ARRAY || this == OBJECT || this == ARRAY_SIMPLE || this == OBJECT_KEY_VAL;
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

    public boolean trio(){
        return this == POSITION || this == ROTATION;
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
            case ARRAY_SIMPLE: return "array_s";
            case OBJECT_KEY_VAL: return "object_kv";
            case ENUM: return "enum";
            case POSITION: return "array_s";
            case ROTATION: return "array_s";
        }
        return "unknown";
    }

    public boolean select(){
        return this == PACKID || this == TEXLOC || this == MODELLOC || this == POSITION || this == ROTATION;
    }

	public boolean map(){
        return this == OBJECT || this == OBJECT_KEY_VAL;
	}

}
