package net.fexcraft.app.fmt.oui.trees;

public enum Trees {

    POLYGON(null),
    PIVOT(null),
    HELPER(null),
    TEXTURE(null),
    ANIMATION(null);

    public String id;

    Trees(String nid){
        id = (nid == null ? name() : nid) + "_tree";
    }

}
