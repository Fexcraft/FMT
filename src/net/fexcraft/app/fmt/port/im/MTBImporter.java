package net.fexcraft.app.fmt.port.im;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.polygon.Vector3F;
import net.fexcraft.app.fmt.update.UpdateEvent.ModelTexGroup;
import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.polygon.Box;
import net.fexcraft.app.fmt.polygon.Model;
import net.fexcraft.app.fmt.polygon.Shapebox;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.texture.Texture;
import net.fexcraft.app.fmt.texture.TextureGroup;
import net.fexcraft.app.fmt.texture.TextureManager;
import net.fexcraft.app.fmt.ui.FileChooser;
import net.fexcraft.lib.common.utils.ZipUtil;

import java.io.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static net.fexcraft.app.fmt.utils.Logging.log;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class MTBImporter implements Importer {

    public static FileChooser.FileType TYPE_MTB = new FileChooser.FileType("SMP Toolbox V2 Save File", "*.mtb");
    private static final List<String> categories = Arrays.asList("model");

    @Override
    public String id() {
        return "mtb";
    }

    @Override
    public String name() {
        return ".MTB (SMP Toolbox V2)";
    }

    @Override
    public FileChooser.FileType extensions() {
        return TYPE_MTB;
    }

    @Override
    public List<String> categories() {
        return categories;
    }

    @Override
    public List<Setting<?>> settings() {
        return Collections.emptyList();
    }

    /**
     * @author EternalBlueFlame, FEX___96
     */
    @Override
    public String _import(Model model, File file){
        try{
            boolean loadtex = ZipUtil.contains(file, "Model.png");
            ZipFile zip = new ZipFile(file);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            InputStream stream = null;
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.getName().equals("Model.txt")){
                    stream = zip.getInputStream(entry);
                    break;
                }
            }
            if(stream == null){
                zip.close();
                return "importer.mtb.stream_null";
            }
            String[] lines = convertStreamToString(stream).split("\n");
            for(String s : lines){
                String[] parts = s.split("\\u007C");
                parts[0] = parts[0].trim();
                if(parts[0].equals("TexSizeX")){
                    model.texSizeX = Integer.parseInt(parts[1].trim());
                }
                else if(parts[0].equals("TexSizeY")){
                    model.texSizeY = Integer.parseInt(parts[1].trim());
                }
                //
                else if(parts[0].equals("ModelAuthor") && parts.length > 1){
                    model.addAuthor(parts[1], false);
                }
                else if(parts[0].equals("ModelName") && parts.length > 1){
                    model.name = parts[1];
                }
                else if(parts[0].equals("Element")){
                    Box polygon = null;
                    switch(parts[5]){
                        case "Box":{
                            polygon = new Box(model);
                            break;
                        }
                        case "Shapebox":{
                            Shapebox sbox = new Shapebox(model);
                            polygon = sbox;
                            sbox.cor0 = new Vector3F(getFloatFromString(parts[20]), getFloatFromString(parts[28]), getFloatFromString(parts[36]));
                            sbox.cor1 = new Vector3F(getFloatFromString(parts[21]), getFloatFromString(parts[29]), getFloatFromString(parts[37]));
                            sbox.cor2 = new Vector3F(getFloatFromString(parts[22]), getFloatFromString(parts[30]), getFloatFromString(parts[38]));
                            sbox.cor3 = new Vector3F(getFloatFromString(parts[23]), getFloatFromString(parts[31]), getFloatFromString(parts[39]));
                            sbox.cor4 = new Vector3F(getFloatFromString(parts[24]), getFloatFromString(parts[32]), getFloatFromString(parts[40]));
                            sbox.cor5 = new Vector3F(getFloatFromString(parts[25]), getFloatFromString(parts[33]), getFloatFromString(parts[41]));
                            sbox.cor6 = new Vector3F(getFloatFromString(parts[26]), getFloatFromString(parts[34]), getFloatFromString(parts[42]));
                            sbox.cor7 = new Vector3F(getFloatFromString(parts[27]), getFloatFromString(parts[35]), getFloatFromString(parts[43]));
                            break;
                        }
                    }
                    if(polygon == null) continue;
                    //
                    polygon.name(parts[3]);
                    polygon.size = new Vector3F(getFloatFromString(parts[9]), getFloatFromString(parts[10]), getFloatFromString(parts[11]));
                    polygon.off = new Vector3F(getFloatFromString(parts[15]), getFloatFromString(parts[16]), getFloatFromString(parts[17]));
                    polygon.pos = new Vector3F(getFloatFromString(parts[6]), getFloatFromString(parts[7]), getFloatFromString(parts[8]));
                    polygon.textureX = Integer.parseInt(parts[18]);
                    polygon.textureY = Integer.parseInt(parts[19]);
                    //
                    polygon.rot = new Vector3F(getFloatFromString(parts[12]), getFloatFromString(parts[13]), getFloatFromString(parts[14]));
                    polygon.rot.z = -polygon.rot.z;
                    //
                    if(model.get("group" + parts[4]) == null){
                        model.addGroup("group" + parts[4]);
                    }
                    model.get("group" + parts[4]).add(polygon);
                }
            }
            stream.close();
            if(loadtex){
                try{
                    if(zip.getEntry("Model.png") == null){
                        log("No Texture found in MTB, skipping texture loading.");
                        model.texgroup = null;
                    }
                    else{
                        TextureManager.loadFromStream(zip.getInputStream(zip.getEntry("Model.png")), "group-default", false, true);
                        Texture tex = TextureManager.get("group-default", true);
                        boolean empty = true;
                        byte[] bts;
                        for(int x = 0; x < tex.getWidth(); x++){
                            if(!empty) break;
                            for(int y = 0; y < tex.getWidth(); y++){
                                bts = tex.get(x, y);
                                if(bts[0] > 0 || bts[1] > 0 || bts[2] > 0){
                                    empty = false;
                                    break;
                                }
                            }
                        }
                        if(!empty){
                            TextureManager.addGroup(new TextureGroup("default"));
                            model.texgroup = TextureManager.getGroup("default");
                            model.texgroup.reAssignTexture();
                            UpdateHandler.update(new ModelTexGroup(FMT.MODEL, FMT.MODEL.texgroup));
                        }
                        else{
                            log("Texture in MTB is blank, not creating a group.");
                            TextureManager.remove("group-default");
                        }
                    }
                }
                catch(Exception e){
                    log(e);
                    log("Could not load texture from MTB.");
                }
            }
            zip.close();
            return "import.complete";
        }
        catch(IOException e){
            log(e);
            return "import.errors";
        }
    }

    public static float getFloatFromString(String s){
        return Float.parseFloat(s.replace(",", ".").trim());
    }

    static String convertStreamToString(java.io.InputStream stream){
        return new BufferedReader(new InputStreamReader(stream)).lines().collect(Collectors.joining("\n"));
    }

}
