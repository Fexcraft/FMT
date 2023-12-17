package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.polygon.Box;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.polygon.Vector3F;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.ui.FileChooser;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class DFMImporter implements Importer {

    public static FileChooser.FileType TYPE_DFM_JAVA = new FileChooser.FileType("Flansmod Format Java", "*.java");
    private static final List<String> categories = Arrays.asList("model");
    private static final ArrayList<Setting<?>> settings = new ArrayList<>();

    public DFMImporter(){
        settings.add(new Setting("agreement", false, "importer-dfm"));
        settings.add(new Setting("degree_based", false, "importer-dfm"));
    }

    @Override
    public String id() {
        return "dfm_java";
    }

    @Override
    public String name() {
        return ".JAVA (Default Flansmod Format)";
    }

    @Override
    public FileChooser.FileType extensions() {
        return TYPE_DFM_JAVA;
    }

    @Override
    public List<String> categories() {
        return categories;
    }

    @Override
    public List<Setting<?>> settings() {
        return settings;
    }

    /**
     * @author EternalBlueFlame, FEX___96
     */
    @Override
    public String _import(Model model, File file){
        if(!settings.get(0).bool()){
            return "importer.dfm.no_agreement";
        }
        boolean degrees = settings.get(1).bool();
        String line = null;
        try{
            Scanner scanner = new Scanner(file);
            Pattern creator = Pattern.compile("\\/\\/ Model Creator: (.*)");
            Pattern groupdef = Pattern.compile("(.*) = new ModelRendererTurbo\\[\\d+\\];");
            Pattern declaration = Pattern.compile("(.*)\\[(\\d+)\\] = new ModelRendererTurbo\\(this, (\\d+), (\\d+), .*, .*\\);(.*)");
            Pattern box = Pattern.compile("(.*)\\[(\\d+)\\]\\.add.*Box\\((.*)\\);.*");
            Pattern rotpoint = Pattern.compile("(.*)\\[(\\d+)\\]\\.setRotationPoint\\((.*)\\);");
            Pattern pospoint = Pattern.compile("(.*)\\[(\\d+)\\]\\.setPosition\\((.*)\\);");
            String component = degrees ? "rotationAngle" : "rotateAngle";
            Pattern rotangle = Pattern.compile("(.*)\\[(\\d+)\\]\\." + component + "(.) = (\\d)+F;");
            ArrayList<TemporaryPolygon> polis = new ArrayList<>();
            while(scanner.hasNext()){
                try{
                    line = scanner.nextLine().trim();
                    if(line.length() < 2) continue;
                    if(line.startsWith("public class")){
                        model.name = line.split(" ")[2].substring(5);
                        continue;
                    }
                    if(line.startsWith("int textureX")){
                        model.texSizeX = parseI(line.split(" ")[3]);
                        continue;
                    }
                    if(line.startsWith("int textureY")){
                        model.texSizeY = parseI(line.split(" ")[3]);
                        continue;
                    }
                    Matcher matcher = groupdef.matcher(line);
                    if(matcher.matches()){
                        model.addGroup(null, matcher.group(1));
                        continue;
                    }
                    matcher = creator.matcher(line);
                    if(matcher.matches()){
                        model.addAuthor(matcher.group(1), true);
                        continue;
                    }
                    matcher = declaration.matcher(line);
                    if(matcher.matches()){
                        TemporaryPolygon poly = new TemporaryPolygon();
                        poly.group = matcher.group(1);
                        poly.index = parseI(matcher.group(2));
                        poly.u = parseI(matcher.group(3));
                        poly.v = parseI(matcher.group(4));
                        poly.name = matcher.group(5).replace(" // ", "");
                        polis.add(poly);
                        continue;
                    }
                    matcher = box.matcher(line);
                    if(matcher.matches()){
                        boolean shapebox = line.contains("ShapeBox");
                        TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
                        String[] array = matcher.group(3).split(", ");
                        if(shapebox){
                            Shapebox sbox = new Shapebox(model);
                            poly.polygon = sbox;
                            sbox.cor0 = newVec3f(array[7], array[8], array[9]);
                            sbox.cor1 = newVec3f(array[10], array[11], array[12]);
                            sbox.cor2 = newVec3f(array[13], array[14], array[15]);
                            sbox.cor3 = newVec3f(array[16], array[17], array[18]);
                            sbox.cor4 = newVec3f(array[19], array[20], array[21]);
                            sbox.cor5 = newVec3f(array[22], array[23], array[24]);
                            sbox.cor6 = newVec3f(array[25], array[26], array[27]);
                            sbox.cor7 = newVec3f(array[28], array[29], array[30]);
                        }
                        else{
                            poly.polygon = new Box(model);
                        }
                        ((Box)poly.polygon).size = newVec3f(array[3], array[4], array[5]);
                        poly.polygon.off = newVec3f(array[0], array[1], array[2]);
                        poly.polygon.name(poly.name);
                        poly.polygon.textureX = poly.u;
                        poly.polygon.textureY = poly.v;
                        model.add(null, poly.group, poly.polygon);
                        continue;
                    }
                    matcher = rotpoint.matcher(line);
                    if(matcher.matches()){
                        TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
                        String[] array = matcher.group(3).split(", ");
                        poly.polygon.pos = newVec3f(array[0], array[1], array[2]);
                        continue;
                    }
                    matcher = pospoint.matcher(line);
                    if(matcher.matches()){
                        TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
                        String[] array = matcher.group(3).split(", ");
                        poly.polygon.pos = newVec3f(array[0], array[1], array[2]);
                        continue;
                    }
                    matcher = rotangle.matcher(line);
                    if(matcher.matches()){
                        TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
                        String axis = matcher.group(3).toLowerCase();
                        float value = parseF(matcher.group(4));
                        switch(axis){
                            case "x":{
                                poly.polygon.rot.x = degrees ? value : (float)Math.toDegrees(value);
                                break;
                            }
                            case "y":{
                                poly.polygon.rot.y = degrees ? value : (float)Math.toDegrees(value);
                                break;
                            }
                            case "z":{
                                poly.polygon.rot.z = degrees ? value : (float)Math.toDegrees(value);
                                break;
                            }
                        }
                        continue;
                    }
                }
                catch(Exception e){
                    log("Parsing error at line: " + line);
                    continue;
                }
            }
            scanner.close();
            return "import.complete";
        }
        catch(Exception e){
            //shouldn't happen, but still.
            log(e);
            return "import.errors";
        }
    }

    private Vector3F newVec3f(String string1, String string2, String string3){
        return new Vector3F(parseF(string1), parseF(string2), parseF(string3));
    }

    private static final class TemporaryPolygon {

        public int u, v;
        public String name;
        public String group;
        public int index;
        public Polygon polygon;

    }

    public static TemporaryPolygon get(String group, String index, ArrayList<TemporaryPolygon> polis){
        int idx = parseI(index);
        for(TemporaryPolygon poly : polis){
            if(poly.group.equals(group) && poly.index == idx){
                return poly;
            }
        }
        return null;
    }

    public static int parseI(String string){
        return Integer.parseInt(string.replace(";", ""));
    }

    public static float parseF(String s){
        return Float.parseFloat(s.replace("F", ""));
    }

}
