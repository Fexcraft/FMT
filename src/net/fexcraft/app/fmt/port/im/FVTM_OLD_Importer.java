package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.polygon.*;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.app.fmt.utils.Logging;
import net.fexcraft.app.fmt.utils.SessionHandler;
import org.joml.Vector3f;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static net.fexcraft.app.fmt.ui.FileChooser.TYPE_JAVA;

public class FVTM_OLD_Importer implements Importer {

    private static final List<String> categories = Arrays.asList("model");

    @Override
    public String id(){
        return "fvtm_old";
    }

    @Override
    public String name() {
        return "Old FVTM Java Models (Decompiled)";
    }

    @Override
    public FileChooser.FileType extensions() {
        return TYPE_JAVA;
    }

    @Override
    public List<String> categories() {
        return categories;
    }

    @Override
    public List<Setting<?>> settings() {
        return Collections.emptyList();
    }

    @Override
    public String _import(Model model, File file) {
        if(!SessionHandler.getUserId().equals("1")) return "invalid";
        Scanner scanner = null;
        String msg = "import.complete";
        String[] vals;
        int idx = -1;
        try {
            String line = null;
            scanner = new Scanner(file);
            Polygon poly = null;
            String group = null;
            while(scanner.hasNext()){
                line = scanner.nextLine().trim();
                if(line.length() < 2) continue;
                if(line.startsWith("public class")){
                    model.name = line.split(" ")[2];
                    continue;
                }
                if(line.contains("textureX = ")){
                    model.texSizeX = Integer.parseInt(line.substring(line.indexOf("=") + 2, line.indexOf(";")));
                    continue;
                }
                if(line.contains("textureY = ")){
                    model.texSizeY = Integer.parseInt(line.substring(line.indexOf("=") + 2, line.indexOf(";")));
                    continue;
                }
                if(line.contains("addToCreators")){
                    model.addAuthor(line.split("\\\"")[1], true);
                }
                if(line.startsWith("TurboList")){
                    if(poly != null && group != null) model.add(null, group, poly);
                    model.addGroup(null, group = line.split(" ")[1]);
                    continue;
                }
                if(line.contains("new ModelRendererTurbo")){
                    if(poly != null) model.add(null, group, poly);
                    if(line.contains("addBox")){
                        Box box = new Box(model);
                        vals = line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf(")")).split(",");
                        box.off.x = pf(vals[0]);
                        box.off.y = pf(vals[1]);
                        box.off.z = pf(vals[2]);
                        box.size.x = pf(vals[3]);
                        box.size.y = pf(vals[4]);
                        box.size.z = pf(vals[5]);
                        poly = box;
                    }
                    else if(line.contains("addCyl")){
                        Cylinder cyl = new Cylinder(model);
                        vals = line.substring(line.lastIndexOf("r(") + 2, line.lastIndexOf(")")).split(",");
                        cyl.off.x = pf(vals[0]);
                        cyl.off.y = pf(vals[1]);
                        cyl.off.z = pf(vals[2]);
                        cyl.radius = pf(vals[3]);
                        cyl.length = pf(vals[4]);
                        cyl.segments = pi(vals[5]);
                        cyl.base = pf(vals[6]);
                        cyl.top = pf(vals[7]);
                        cyl.direction = pi(vals[8]);
                        if(!vals[9].contains("null")){
                            vals = line.substring(line.lastIndexOf("(") + 1, line.lastIndexOf(")")).split(",");
                            cyl.topoff = new Vector3f(pf(vals[0]), pf(vals[1]), pf(vals[2]));
                        }
                        poly = cyl;
                    }
                    else{//probably a shapebox
                        poly = new Shapebox(model);
                    }
                    idx = line.indexOf(",");
                    poly.textureX = Integer.parseInt(line.substring(idx + 2, idx = line.indexOf(",", idx + 2)));
                    poly.textureY = Integer.parseInt(line.substring(idx + 2, line.indexOf(",", idx + 2)));
                }
                if(line.contains("addShapeBox")){
                    vals = line.substring(line.indexOf("(") + 1, line.indexOf(")")).split(",");
                    Shapebox sbox = (Shapebox)poly;
                    sbox.off.x = pf(vals[0]);
                    sbox.off.y = pf(vals[1]);
                    sbox.off.z = pf(vals[2]);
                    sbox.size.x = pf(vals[3]);
                    sbox.size.y = pf(vals[4]);
                    sbox.size.z = pf(vals[5]);
                    sbox.cor0.x = pf(vals[6]);
                    sbox.cor0.y = pf(vals[8]);
                    sbox.cor0.z = pf(vals[9]);
                    sbox.cor1.x = pf(vals[10]);
                    sbox.cor1.y = pf(vals[11]);
                    sbox.cor1.z = pf(vals[12]);
                    sbox.cor2.x = pf(vals[13]);
                    sbox.cor2.y = pf(vals[14]);
                    sbox.cor2.z = pf(vals[15]);
                    sbox.cor3.x = pf(vals[16]);
                    sbox.cor3.y = pf(vals[17]);
                    sbox.cor3.z = pf(vals[18]);
                    sbox.cor4.x = pf(vals[19]);
                    sbox.cor4.y = pf(vals[20]);
                    sbox.cor4.z = pf(vals[21]);
                    sbox.cor5.x = pf(vals[22]);
                    sbox.cor5.y = pf(vals[23]);
                    sbox.cor5.z = pf(vals[24]);
                    sbox.cor6.x = pf(vals[25]);
                    sbox.cor6.y = pf(vals[26]);
                    sbox.cor6.z = pf(vals[27]);
                    sbox.cor7.x = pf(vals[28]);
                    sbox.cor7.y = pf(vals[29]);
                    sbox.cor7.z = pf(vals[30]);
                    continue;
                }
                if(line.contains("setRotationPoint")){
                    idx = line.indexOf("setRotationPoint");
                    vals = line.substring(line.indexOf("(", idx) + 1, line.indexOf(")", idx)).split(",");
                    poly.pos.x = pf(vals[0]);
                    poly.pos.y = pf(vals[1]);
                    poly.pos.z = pf(vals[2]);
                }
                if(line.contains("setRotationAngle")){
                    idx = line.indexOf("setRotationAngle");
                    vals = line.substring(line.indexOf("(", idx) + 1, line.indexOf(")", idx)).split(",");
                    poly.rot.x = pf(vals[0]);
                    poly.rot.y = pf(vals[1]);
                    poly.rot.z = pf(vals[2]);
                }
            }
            if(poly != null) model.add(null, group, poly);
        }
        catch(Exception e){
            e.printStackTrace();
            msg = "import.errors";
        }
        finally{
            if(scanner != null) scanner.close();
        }
        return msg;
    }

    public static float pf(String s){
        return Float.parseFloat(s.trim().replace("F", ""));
    }

    public static int pi(String s){
        return Integer.parseInt(s.trim());
    }

}
