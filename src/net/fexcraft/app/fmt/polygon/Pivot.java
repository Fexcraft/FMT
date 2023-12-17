package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.json.JsonMap;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Pivot {

    public final boolean root;
    public ArrayList<Group> groups = new ArrayList<>();
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
        root = map.getBoolean("root", false);
    }

    public JsonMap save(){
        JsonMap map = new JsonMap();


        return map;
    }

}
