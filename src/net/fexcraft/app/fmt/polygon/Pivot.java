package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Pivot {

    public final boolean root;
    public ArrayList<Group> groups = new ArrayList<>();
    public ArrayList<Pivot> roots = new ArrayList<>();
    public boolean minimized;
    public boolean visible = true;
    public Vector3f pos = new Vector3f();
    public Vector3f rot = new Vector3f();
    private Pivot parent;
    public String parentid;
    public String id;

    public Pivot(String id, boolean root){
        this.id = id;
        this.root = root;
    }

    public Pivot(String id){
        this(id, false);
        parent = FMT.MODEL.getRootPivot();
    }

    public Pivot(String key, JsonMap map){
        id = key;
        root = map.getBoolean("root", false);
        if(map.has("pos")){
            JsonArray apos = map.getArray("pos");
            pos.x = apos.get(0).float_value();
            pos.y = apos.get(1).float_value();
            pos.z = apos.get(2).float_value();
        }
        if(map.has("rot")){
            JsonArray arot = map.getArray("rot");
            rot.x = arot.get(0).float_value();
            rot.y = arot.get(1).float_value();
            rot.z = arot.get(2).float_value();
        }
        visible = map.getBoolean("visible", true);
        minimized = map.getBoolean("minimized", false);
        parentid = map.getString("parent", null);
    }

    public JsonMap save(){
        JsonMap map = new JsonMap();
        map.add("root", root);
        map.add("pos", new JsonArray(pos.x, pos.y, pos.z));
        map.add("rot", new JsonArray(rot.x, rot.y, rot.z));
        map.add("visible", visible);
        map.add("minimized", minimized);
        if(parent != null) map.add("parent", parent.id);
        return map;
    }

    public boolean isin(Group group){
        if(group.pivot == null) return root;
        return id.equals(group.pivot);
    }

    public void parent(Pivot p){
        parent(p, true);
    }

    public void parent(Pivot p, boolean rr){
        parent = p;
        if(rr) reroot();
    }

    public void reroot(){
        roots.clear();
        if(root) return;
        Pivot pivot = this;
        while(pivot.parent != null){
            pivot = pivot.parent;
            roots.add(0, pivot);
        }
    }

    public Pivot parent(){
        return parent;
    }

}
