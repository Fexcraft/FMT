package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.json.JsonMap;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Pivot {

    public final boolean root;
    public ArrayList<Group> groups = new ArrayList<>();
    public boolean minimized;
    public boolean visible = true;
    public Vector3f pos;
    public Vector3f rot;
    public String id;

    public Pivot(String id, boolean root){
        this.id = id;
        this.root = root;
    }

    public Pivot(String id){
        this(id, false);
    }

    public Pivot(String key, JsonMap map){
        id = key;
        root = map.getBoolean("root", false);
    }

    public JsonMap save(){
        JsonMap map = new JsonMap();


        return map;
    }

    public boolean isin(Group group){
        if(group.pivot == null) return root;
        return id.equals(group.pivot);
    }

}
