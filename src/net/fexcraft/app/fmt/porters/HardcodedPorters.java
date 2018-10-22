package net.fexcraft.app.fmt.porters;

import com.google.gson.JsonObject;
import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeType;
import net.fexcraft.lib.tmt.ModelRendererTurbo;

import javax.swing.*;
import java.io.*;
import java.nio.file.Files;
import java.util.Enumeration;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class HardcodedPorters {


    public static String[] extensions(){
        return new String[]{".mtb"};
    }

    public static boolean redirect(File f){
        if(f.getName().contains(extensions()[0])){
            importMTB(f);
            return true;
        }
        return false;
    }

    public static float getFloatFromString(String s){
        return Float.parseFloat(s.replace(",",".").trim());
    }

    static String convertStreamToString(java.io.InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream))
                .lines().collect(Collectors.joining("\n"));
    }

    public static void importMTB(File f){
        try {
            GroupCompound compound = new GroupCompound();
            ZipFile zip = new ZipFile(f);
            Enumeration<? extends ZipEntry> entries = zip.entries();
            InputStream stream=null;
            while(entries.hasMoreElements()){
                ZipEntry entry = entries.nextElement();
                if(entry.getName().equals("Model.txt")){
                    stream=zip.getInputStream(entry);
                    break;
                }
            }
            if(stream==null){
                JOptionPane.showMessageDialog(null, "Import Failed, MTB appears corrupt.", "Status", JOptionPane.INFORMATION_MESSAGE);
                return;
            }


            String[] file = convertStreamToString(stream).split("\n"); //Files.readAllLines(stream.toPath());
            for(String s:file){
                String[] parts = s.split("\\u007C");
                parts[0]=parts[0].trim();
                if(parts[0].equals("TexSizeX")) {
                    compound.textureX=Integer.parseInt(parts[1].trim());
                } else if(parts[0].equals("TexSizeY")) {
                    compound.textureY=Integer.parseInt(parts[1]);
                }if(parts[0].equals("ModelAuthor") && parts.length>1) {
                    compound.creators.add(parts[1]);
                }  else if(parts[0].equals("Element")){
                    if(parts[5].equals("Box")){
                        PolygonWrapper polygon = new PolygonWrapper(compound) {
                            @Override
                            public void recompile() {
                                this.turbo = new ModelRendererTurbo(null,parts[3]);
                                turbo.texoffx=Integer.parseInt(parts[18]);
                                turbo.texoffy=Integer.parseInt(parts[19]);
                                turbo.addBox(getFloatFromString(parts[15]),getFloatFromString(parts[16]),getFloatFromString(parts[17]),
                                        getFloatFromString(parts[9]),getFloatFromString(parts[10]),getFloatFromString(parts[11]));
                                turbo.setRotationPoint(getFloatFromString(parts[6]),getFloatFromString(parts[7]),getFloatFromString(parts[8]));
                                turbo.rotateAngleX=getFloatFromString(parts[12]);
                                if(turbo.rotateAngleX!=0){
                                    turbo.rotateAngleX*=-0.01745329259;
                                }
                                turbo.rotateAngleY=getFloatFromString(parts[13]);
                                if(turbo.rotateAngleY!=0){
                                    turbo.rotateAngleY*=-0.01745329259;
                                }
                                turbo.rotateAngleZ=getFloatFromString(parts[14]);
                                if(turbo.rotateAngleZ!=0){
                                    turbo.rotateAngleZ*=-0.01745329259;
                                }
                            }

                            @Override
                            public ShapeType getType() {
                                return ShapeType.BOX;
                            }

                            @Override
                            protected JsonObject populateJson(JsonObject obj, boolean export) {
                                return null;
                            }
                        };

                        compound.add(polygon);
                    }
                }


            }
            stream.close();

            FMTB.MODEL=compound;


        } catch (IOException e){
            //literally not even possible.
        }

    }
}
