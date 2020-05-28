package net.fexcraft.app.fmt.porters;

import static net.fexcraft.app.fmt.utils.Logging.log;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.fexcraft.app.fmt.porters.PorterManager.ExImPorter;
import net.fexcraft.app.fmt.ui.DialogBox;
import net.fexcraft.app.fmt.utils.SessionHandler;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.ShapeboxWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.Vec3f;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class DFMImporter extends ExImPorter {
	
	private static String[] extensions = new String[]{ "Flansmod Format Java Importer", "*.java" };
	protected ArrayList<Setting> settings = new ArrayList<>();
	
	public DFMImporter(){
		settings.add(new Setting("agreement", false));
		settings.add(new Setting("degree based", false));
	}

    public String[] getExtensions(){
        return extensions;
    }
    
    public String getId(){
    	return "java_importer";
    }
    
    public String getName(){
    	return "Standard (F) Java Importer";
    }
    
	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
        GroupCompound compound = new GroupCompound(file);
		if(!settings.get("agreement").getBooleanValue()){
			DialogBox.showOK("eximporter.import.failed", null, null, "eximporter.dfm_importer.no_agreement");
			return compound;
		}
		if(!SessionHandler.getLicenseStatus().equals("creator")){
			DialogBox.showOK("eximporter.import.failed", null, null, "NO PERMISSION");
			return compound;
		}
		if(!SessionHandler.getUserName().equals("Ferdinand")){
			DialogBox.showOK("eximporter.import.failed", null, null, "NO PERMISSION");
			return compound;
		}
        boolean degrees = settings.get("degree based").getBooleanValue();
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
            	String line = scanner.nextLine().trim();
            	if(line.length() < 2) continue;
            	if(line.startsWith("public class")){
            		compound.name = line.split(" ")[2].substring(5);
            		continue;
            	}
            	if(line.startsWith("int textureX")){
            		compound.textureSizeX = parseI(line.split(" ")[3]);
            		continue;
            	}
            	if(line.startsWith("int textureY")){
            		compound.textureSizeY = parseI(line.split(" ")[3]);
            		continue;
            	}
            	Matcher matcher = groupdef.matcher(line);
            	if(matcher.matches()){
            		compound.getGroups().add(new TurboList(matcher.group(1)));
            		continue;
            	}
            	matcher = creator.matcher(line);
            	if(matcher.matches()){
            		compound.addAuthor(matcher.group(1), false, true);
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
            			ShapeboxWrapper wrapper = new ShapeboxWrapper(compound);
            			poly.wrapper = wrapper;
            			wrapper.cor0 = newVec3f(array[7], array[8], array[9]);
            			wrapper.cor1 = newVec3f(array[10], array[11], array[12]);
            			wrapper.cor2 = newVec3f(array[13], array[14], array[15]);
            			wrapper.cor3 = newVec3f(array[16], array[17], array[18]);
            			wrapper.cor4 = newVec3f(array[19], array[20], array[21]);
            			wrapper.cor5 = newVec3f(array[22], array[23], array[24]);
            			wrapper.cor6 = newVec3f(array[25], array[26], array[27]);
            			wrapper.cor7 = newVec3f(array[28], array[29], array[30]);
            		}
            		else{
            			poly.wrapper = new BoxWrapper(compound);
            		}
            		((BoxWrapper)poly.wrapper).size = newVec3f(array[3], array[4], array[5]);
            		poly.wrapper.off = newVec3f(array[0], array[1], array[2]);
            		poly.wrapper.name = poly.name;
            		poly.wrapper.textureX = poly.u;
            		poly.wrapper.textureY = poly.v;
            		compound.add(poly.wrapper, poly.group, false);
            		continue;
            	}
            	matcher = rotpoint.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
            		String[] array = matcher.group(3).split(", ");
            		poly.wrapper.pos = newVec3f(array[0], array[1], array[2]);
            		continue;
            		
            	}
            	matcher = pospoint.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
            		String[] array = matcher.group(3).split(", ");
            		poly.wrapper.pos = newVec3f(array[0], array[1], array[2]);
            		continue;
            		
            	}
            	matcher = rotangle.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = get(matcher.group(1), matcher.group(2), polis);
            		String axis = matcher.group(3).toLowerCase();
            		float value = parseF(matcher.group(4));
            		switch(axis){
            			case "x":{
            				poly.wrapper.rot.xCoord = degrees ? value : (float)Math.toDegrees(value);
            				break;
            			}
            			case "y":{
            				poly.wrapper.rot.yCoord = degrees ? value : (float)Math.toDegrees(value);
            				break;
            			}
            			case "z":{
            				poly.wrapper.rot.zCoord = degrees ? value : (float)Math.toDegrees(value);
            				break;
            			}
            		}
            		continue;
            	}
            }
            /*if(!compound.creators.contains(SessionHandler.getUserName())){
    			DialogBox.showOK("eximporter.import.failed", null, null, "NOT AUTHORIZED, CREATOR COULD NOT BE CONFIRMED");
    			return null;
            }*/
        	scanner.close();
            return compound;
        }
        catch(IOException e){
        	//shouldn't happen, but still.
        	log(e);
        	return null;
        }
	}
	
	private Vec3f newVec3f(String string1, String string2, String string3){
		return new Vec3f(parseF(string1), parseF(string2), parseF(string3));
	}

	private static final class TemporaryPolygon {
		
		public int u, v;
		public String name;
		public String group;
		public int index;
		public PolygonWrapper wrapper;
		
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

	@Override
	public String exportModel(GroupCompound compound, File file, Map<String, Setting> settings){
		return "This isn't an exporter as of now.";
	}
	
	@Override
	public boolean isImporter(){
		return true;
	}
	
	@Override
	public boolean isExporter(){
		return false;
	}

	@Override
	public List<Setting> getSettings(boolean export){
		return settings;
	}

	@Override
	public String[] getCategories(){
		return new String[]{ "model" };
	}
    
}