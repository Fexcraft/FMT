package net.fexcraft.app.fmt.port.ex;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.GLObject;
import net.fexcraft.app.fmt.polygon.Group;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.utils.Axis3DL;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.lib.common.math.Vec3f;
import net.fexcraft.lib.frl.Polyhedron;
import net.fexcraft.lib.frl.Vertex;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.time.Year;
import java.util.*;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_OBJ;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class ObjExporter implements Exporter {

    private static final List<String> categories = Arrays.asList("model");
    private static final ExSetList settings = new ExSetList();

    public ObjExporter(){
        settings.add(new Setting("include_names", false, "exporter-obj"));
        settings.add(new Setting("rotate_model", false, "exporter-obj"));
        settings.add(new Setting("rotate_y", 180f, "exporter-obj"));
        settings.add(new Setting("rotate_z", 180f, "exporter-obj"));
        settings.add(new Setting("rotate_x", 0f, "exporter-obj"));
        settings.add(new Setting("flip_u", false, "exporter-obj"));
        settings.add(new Setting("flip_v", false, "exporter-obj"));
        settings.add(new Setting("scale", 1f, "exporter-obj"));
        settings.add(new Setting("index_vertices", false, "exporter-obj"));
        settings.add(new Setting("groups_as_objects", false, "exporter-obj"));
    }

    @Override
    public String id(){
        return "obj";
    }

    @Override
    public String name(){
        return "OBJ (Wavefront .obj)";
    }

    @Override
    public FileChooser.FileType extensions(){
        return TYPE_OBJ;
    }

    @Override
    public List<String> categories(){
        return categories;
    }

    @Override
    public List<Setting<?>> settings(){
        return settings;
    }

    @Override
    public boolean nogroups(){
        return false;
    }

    @Override
    public String export(Model model, File file, List<Group> groups){
        StringBuffer buffer = new StringBuffer();
        boolean naming = settings.g("include_names").bool();
        boolean rotate = settings.g("rotate_model").bool();
        boolean flipu = settings.g("flip_u").bool();
        boolean flipv = settings.g("flip_v").bool();
        float scale = settings.g("scale").value();
        boolean doindex = settings.g("index_vertices").bool();
        String gpfx = settings.g("groups_as_objects").bool() ? "o" : "g";
        buffer.append("# FMT-Marker OBJ-3\n#\n");
        buffer.append("# Model exported via the Standard FMT OBJ Exporter\n");
        buffer.append("# FMT (Fex's Modelling Toolbox) v" + FMT.VERSION + " &copy; " + Year.now().getValue() + " fexcraft.net\n");
        buffer.append("# All rights reserved. For this Model's License contact the Author/Creator.\n#\n");
        if(model.getAuthors().size() > 0){
            for(String str : model.getAuthors().keySet()){
                buffer.append("# Creator: " + str + "\n");
            }
        }
        else{
            buffer.append("# Creator: Empty/FMT\n");
        }
        buffer.append("# Model Name: " + model.name + "\n\n");
        buffer.append("# TextureWidth: " + model.texSizeX + "\n");
        buffer.append("# TextureHeight: " + model.texSizeY + "\n\n");
        Axis3DL rot, rotg = null;
        if(rotate){
            float yaw = settings.g("rotate_y").value();
            float pit = settings.g("rotate_z").value();
            float rol = settings.g("rotate_x").value();
            rotg = new Axis3DL();
            rotg.setAngles(yaw, pit, rol);
        }
        if(!settings.g("groups_as_objects").bool()){
            buffer.append("o " + model.name + "\n\n");
        }
        boolean cyl;
        int index = 1;
        IndexVertex[] vertix = null;
        TreeMap<IndexVertex, Integer> indices = new TreeMap<>();
        for(Group group : groups){
            buffer.append("# Group Name: " + group.id + "\n");
            buffer.append(gpfx + " " + group.exportId(true) + "\n");
            buffer.append("usemtl fmt_material\n\n");
            rot = new Axis3DL();
            if(doindex) indices.clear();
            for(Polygon poly : group){
                if(poly.getShape().isMarker() || poly.getShape().isBoundingBox()) continue;
                cyl = poly.getShape().isCylinder();
                if(naming && poly.name(true) != null){
                    buffer.append("# ID: " + poly.name() + "\n");
                }
                rot.setAngles(-poly.rot.y, -poly.rot.z, -poly.rot.x);
                Polyhedron<GLObject> hed = poly.glm;
                for(net.fexcraft.lib.frl.Polygon poli : hed.polygons){
                    vertix = new IndexVertex[poli.vertices.length];
                    for(int i = 0; i < vertix.length; i++){
                        Vertex vert = poli.vertices[i];
                        Vec3f vrot = rot.get(vert.vector);
                        vrot.x += poly.pos.x;
                        vrot.y += poly.pos.y;
                        vrot.z += poly.pos.z;
                        if(rotate){
                            vrot = rotg.get(vrot);
                        }
                        vrot.x *= scale;
                        vrot.y *= scale;
                        vrot.z *= scale;
                        vertix[i] = new IndexVertex(vrot, vert.u, vert.v);
                        if(!doindex || cyl || !indices.containsKey(vertix[i])){
                            buffer.append("v " + vertix[i].x + " " + vertix[i].y + " " + vertix[i].z + "\n");
                            float u = flipu ? -vert.u + 1f : vert.u;
                            float v = flipv ? -vert.v + 1f : vert.v;
                            buffer.append("vt " + u + " " + v + "\n");
                        }
                    }
                    buffer.append("f");
                    for(int i = 0; i < vertix.length; i++){
                        int vert = index + i;
                        if(doindex && !cyl){
                            if(indices.containsKey(vertix[i])){
                                vert = indices.get(vertix[i]);
                            }
                            else{
                                indices.put(vertix[i], vert = index);
                                index++;
                            }
                        }
                        buffer.append(" " + vert + "/" + vert);
                    }
                    buffer.append("\n\n");
                    if(!doindex || cyl) index += vertix.length;
                }
            }
            buffer.append("\n");
        }
        buffer.append("# FMT-Marker OBJ-END\n");
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            writer.append(buffer);
            writer.flush();
            writer.close();
        }
        catch(Throwable e){
            Logging.log(e);
            return "Error:" + e.getMessage();
        }
        return "export.complete";
    }

    public static class IndexVertex implements Comparable<IndexVertex> {

        private float x, y, z, u, v;

        public IndexVertex(Vec3f vert, float u, float v){
            x = vert.x;
            y = vert.y;
            z = vert.z;
            this.u = u;
            this.v = v;
        }

        @Override
        public boolean equals(Object o){
            if(o instanceof IndexVertex == false) return false;
            IndexVertex i = (IndexVertex)o;
            return i.x == x && i.y == y && i.z == z && i.u == u && i.v == v;
        }

        @Override
        public int compareTo(@NotNull IndexVertex o){
            if(x < o.x) return -1;
            if(x > o.x) return 1;
            if(y < o.y) return -1;
            if(y > o.y) return 1;
            if(z < o.z) return -1;
            if(z > o.z) return 1;
            if(u < o.u) return -1;
            if(u > o.u) return 1;
            if(v < o.v) return -1;
            if(v > o.v) return 1;
            return 0;
        }

        @Override
        public String toString(){
            return "IV(" + x + ", " + y + ", " + z + ", " + u + ", " + v + ")";
        }

    }

}
