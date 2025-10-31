package net.fexcraft.app.fmt.polygon;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.M4DW;
import net.fexcraft.lib.common.math.V3D;
import org.joml.Matrix4f;
import org.joml.Vector3f;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Pivot {

    public final boolean root;
    public ArrayList<Group> groups = new ArrayList<>();
    public ArrayList<Pivot> roots = new ArrayList<>();
	public String[] pos_attr = new String[]{ "", "", "" };
	public String[] rot_attr = new String[]{ "", "", "" };
    public Matrix4f matrix;
    public boolean minimized;
    public boolean visible = true;
	public boolean root_rot = true;
    public Vector3f pos = new Vector3f();
    public Vector3f rot = new Vector3f();
    private Pivot parent;
	private M4DW rotmat;
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
		if(map.has("pos_attr")){
			JsonArray pa = map.getArray("pos_attr");
			pos_attr[0] = pa.get(0).string_value();
			pos_attr[1] = pa.get(1).string_value();
			pos_attr[2] = pa.get(2).string_value();
		}
		if(map.has("rot_attr")){
			JsonArray ra = map.getArray("rot_attr");
			rot_attr[0] = ra.get(0).string_value();
			rot_attr[1] = ra.get(1).string_value();
			rot_attr[2] = ra.get(2).string_value();
		}
        visible = map.getBoolean("visible", true);
        minimized = map.getBoolean("minimized", false);
        parentid = map.getString("parent", null);
		root_rot = map.getBoolean("root_rot", true);
    }

    public JsonMap save(){
        JsonMap map = new JsonMap();
        map.add("root", root);
        map.add("pos", new JsonArray(pos.x, pos.y, pos.z));
        map.add("rot", new JsonArray(rot.x, rot.y, rot.z));
        map.add("visible", visible);
        map.add("minimized", minimized);
        if(parent != null) map.add("parent", parent.id);
		JsonArray arr = new JsonArray();
		for(String s : pos_attr) arr.add(s);
		map.add("pos_attr", arr);
		arr = new JsonArray();
		for(String s : rot_attr) arr.add(s);
		map.add("rot_attr", arr);
		map.add("root_rot", root_rot);
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
        while(pivot.parent != null && !roots.contains(pivot.parent)){
            pivot = pivot.parent;
            roots.add(0, pivot);
        }
    }

    public Pivot parent(){
        return parent;
    }

	public V3D getVec(V3D off){
		if(rotmat == null) rotmat = M4DW.create();
		rotmat.setDegrees(-rot.y, -rot.z, -rot.x);
		off = rotmat.rotate(off).add(pos.x, pos.y, pos.z);
		return parent != null ? parent.getVec(off) : off;
	}

	public V3D getPosOnBranch(V3D off){
		if(off == null) off = new V3D();
		off = off.add(pos.x, pos.y, pos.z);
		return parent != null ? parent.getVec(off) : off;
	}

}
