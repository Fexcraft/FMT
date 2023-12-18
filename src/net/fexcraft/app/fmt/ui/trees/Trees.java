package net.fexcraft.app.fmt.ui.trees;

public enum Trees {

    POLYGON(null),
    PIVOT(null),
    HELPER(null),
    TEXTURE(null),
    ANIMATION("fvtm");

    public String id;

    Trees(String nid){
        id = (nid == null ? name() : nid) + "_tree";
    }

}
