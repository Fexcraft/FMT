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

    public static int getSideFromName(String MrMysterious){
        switch (MrMysterious){
            case "MR_BOTTOM":{return ModelRendererTurbo.MR_BOTTOM;}
            case "MR_LEFT":{return ModelRendererTurbo.MR_LEFT;}
            case "MR_RIGHT":{return ModelRendererTurbo.MR_RIGHT;}
            case "MR_FRONT":{return ModelRendererTurbo.MR_FRONT;}
            case "MR_BACK":{return ModelRendererTurbo.MR_BACK;}
            case "MR_TOP": default:{return ModelRendererTurbo.MR_TOP;}
        }
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
                    PolygonWrapper polygon = new PolygonWrapper(compound) {
                        @Override
                        public void recompile() {
                            name = parts[3];
                            turbo = new ModelRendererTurbo(null, parts[3]);
                            turbo.texoffx = Integer.parseInt(parts[18]);
                            turbo.texoffy = Integer.parseInt(parts[19]);
                            turbo.setRotationPoint(getFloatFromString(parts[6]), getFloatFromString(parts[7]), getFloatFromString(parts[8]));
                            turbo.rotateAngleX = getFloatFromString(parts[12]);
                            if (turbo.rotateAngleX != 0) {
                                turbo.rotateAngleX *= 0.01745329259;
                            }
                            turbo.rotateAngleY = getFloatFromString(parts[13]);
                            if (turbo.rotateAngleY != 0) {
                                turbo.rotateAngleY *= 0.01745329259;
                            }
                            turbo.rotateAngleZ = getFloatFromString(parts[14]);
                            if (turbo.rotateAngleZ != 0) {
                                turbo.rotateAngleZ *= -0.01745329259;
                            }


                            switch (parts[5]){
                                case "Box":{
                                    turbo.addBox(getFloatFromString(parts[15]), getFloatFromString(parts[16]), getFloatFromString(parts[17]),
                                            getFloatFromString(parts[9]), getFloatFromString(parts[10]), getFloatFromString(parts[11]));
                                    break;}
                                case "Shapebox":{
                                    turbo.addShapeBox(getFloatFromString(parts[15]), getFloatFromString(parts[16]), getFloatFromString(parts[17]),
                                            getFloatFromString(parts[9]), getFloatFromString(parts[10]), getFloatFromString(parts[11]),
                                            0,
                                            getFloatFromString(parts[20]),getFloatFromString(parts[28]),getFloatFromString(parts[36]),
                                            getFloatFromString(parts[21]),getFloatFromString(parts[29]),getFloatFromString(parts[37]),
                                            getFloatFromString(parts[22]),getFloatFromString(parts[30]),getFloatFromString(parts[38]),
                                            getFloatFromString(parts[23]),getFloatFromString(parts[31]),getFloatFromString(parts[39]),
                                            getFloatFromString(parts[24]),getFloatFromString(parts[32]),getFloatFromString(parts[40]),
                                            getFloatFromString(parts[25]),getFloatFromString(parts[33]),getFloatFromString(parts[41]),
                                            getFloatFromString(parts[26]),getFloatFromString(parts[34]),getFloatFromString(parts[42]),
                                            getFloatFromString(parts[27]),getFloatFromString(parts[35]),getFloatFromString(parts[43])
                                            );
                                    break;
                                }
                                case "Trapezoid":{
                                    turbo.addTrapezoid(getFloatFromString(parts[15]), getFloatFromString(parts[16]), getFloatFromString(parts[17]),
                                            getFloatFromString(parts[9]), getFloatFromString(parts[10]), getFloatFromString(parts[11]),
                                            0,getFloatFromString(parts[45]),getSideFromName(parts[44]));
                                    break;
                                }
                                case "FlexTrapezoid":{
                                    turbo.addFlexTrapezoid(getFloatFromString(parts[15]), getFloatFromString(parts[16]), getFloatFromString(parts[17]),
                                            getFloatFromString(parts[9]), getFloatFromString(parts[10]), getFloatFromString(parts[11]),
                                            0F,getFloatFromString(parts[52]),
                                            getFloatFromString(parts[53]),getFloatFromString(parts[54]),getFloatFromString(parts[55]),
                                            getFloatFromString(parts[55]), getFloatFromString(parts[55]),
                                            getSideFromName(parts[44]));
                                    break;
                                }
                                case "FlexBox":{
                                    turbo.addFlexBox(getFloatFromString(parts[15]), getFloatFromString(parts[16]), getFloatFromString(parts[17]),
                                            getFloatFromString(parts[9]), getFloatFromString(parts[10]), getFloatFromString(parts[11]),
                                            0F,
                                            getFloatFromString(parts[47]),getFloatFromString(parts[48]),
                                            getFloatFromString(parts[49]), getFloatFromString(parts[50]),
                                            getSideFromName(parts[44])
                                            );
                                    break;
                                }
                            }
                        }

                        @Override
                        public ShapeType getType() {
                            switch (parts[5]) {
                                case "Box": {
                                    return ShapeType.BOX;
                                }
                                default:{
                                    return ShapeType.SHAPEBOX;
                                }
                            }
                        }

                        @Override
                        protected JsonObject populateJson(JsonObject obj, boolean export) {
                            return null;
                        }
                    };

                    compound.add(polygon);
                }
            }
            stream.close();

            FMTB.MODEL=compound;


        } catch (IOException e){
            //literally not even possible.
        }

    }




    public void saveMTB(){

    }
}
