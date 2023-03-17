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
    ARRAY_OR_TEXT,
    OBJECT_KEY_VAL,
    ENUM,

    POSITION,
    ROTATION,

    ;

    public boolean subs(){
        return this == ARRAY || this == OBJECT || this == ARRAY_OR_TEXT || this == OBJECT_KEY_VAL;
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

}
