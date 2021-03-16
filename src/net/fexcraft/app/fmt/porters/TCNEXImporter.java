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
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.wrappers.BoxWrapper;
import net.fexcraft.app.fmt.wrappers.GroupCompound;
import net.fexcraft.app.fmt.wrappers.PolygonWrapper;
import net.fexcraft.app.fmt.wrappers.TurboList;
import net.fexcraft.lib.common.math.Vec3f;

/**
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class TCNEXImporter extends ExImPorter {
	
	private static String[] extensions = new String[]{ "Techne/Vanilla Format Java Importer", "*.java" };
	protected ArrayList<Setting> settings = new ArrayList<>();
	
	public TCNEXImporter(){
		//
	}

    public String[] getExtensions(){
        return extensions;
    }
    
    public String getId(){
    	return "java_importer";
    }
    
    public String getName(){
    	return "Techne Exported Java Importer";
    }
    
	@Override
	public GroupCompound importModel(File file, Map<String, Setting> settings){
        GroupCompound compound = new GroupCompound(file);
        compound.addAuthor("TCNEX Imported", false, true);
        compound.getGroups().add(new TurboList("imported"));
        try{
            Scanner scanner = new Scanner(file);
            Pattern delc = Pattern.compile("(.*) = new ModelRenderer\\(this, (\\d+), (\\d+)\\);.*");
            Pattern box = Pattern.compile("(.*)\\.addBox\\((.*)\\);.*");
            Pattern rotpoint = Pattern.compile("(.*)\\.setRotationPoint\\((.*)\\);");
            Pattern rotation = Pattern.compile("setRotation\\((.*), (\\d+)F, (\\d+)F, (\\d+)F\\);");
            Pattern texsize = Pattern.compile("textureWidth = (\\d+); textureHeight = (\\d+);");
            ArrayList<TemporaryPolygon> polis = new ArrayList<>();
            while(scanner.hasNext()){
            	String line = scanner.nextLine().trim();
            	if(line.length() < 2) continue;
            	if(line.startsWith("public class")){
            		compound.name = line.split(" ")[2].substring(5);
            		continue;
            	}
            	if(line.startsWith("textureWidth") && !line.contains("textureHeight")){
            		compound.textureSizeX = parseI(line.split(" ")[2]);
            		continue;
            	}
            	if(line.startsWith("textureHeight") && !line.contains("textureWidth")){
            		compound.textureSizeY = parseI(line.split(" ")[2]);
            		continue;
            	}
            	Matcher matcher = delc.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = new TemporaryPolygon();
            		poly.name = matcher.group(1);
            		poly.u = parseI(matcher.group(2));
            		poly.v = parseI(matcher.group(3));
            		polis.add(poly);
            		continue;
            	}
            	matcher = texsize.matcher(line);
            	if(matcher.matches()){
            		compound.textureSizeX = parseI(matcher.group(1));
            		compound.textureSizeY = parseI(matcher.group(2));
            	}
            	matcher = box.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = get(matcher.group(1), polis);
            		String[] array = matcher.group(2).split(", ");
        			poly.wrapper = new BoxWrapper(compound);
            		((BoxWrapper)poly.wrapper).size = newVec3f(array[3], array[4], array[5]);
            		poly.wrapper.off = newVec3f(array[0], array[1], array[2]);
            		poly.wrapper.name = poly.name;
            		poly.wrapper.textureX = poly.u;
            		poly.wrapper.textureY = poly.v;
            		compound.add(poly.wrapper, "imported", false);
            		continue;
            	}
            	matcher = rotpoint.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = get(matcher.group(1), polis);
            		String[] array = matcher.group(2).split(", ");
            		poly.wrapper.pos = newVec3f(array[0], array[1], array[2]);
            		continue;
            	}
            	matcher = rotation.matcher(line);
            	if(matcher.matches()){
            		TemporaryPolygon poly = get(matcher.group(1), polis);
            		poly.wrapper.rot.xCoord = (float)Math.toDegrees(parseF(matcher.group(2)));
            		poly.wrapper.rot.xCoord = (float)Math.toDegrees(parseF(matcher.group(3)));
            		poly.wrapper.rot.xCoord = (float)Math.toDegrees(parseF(matcher.group(4)));
            		continue;
            	}
            }
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
		public PolygonWrapper wrapper;
		
	}
	
	public static TemporaryPolygon get(String uid, ArrayList<TemporaryPolygon> polis){
		for(TemporaryPolygon poly : polis){
			if(poly.name.equals(uid)){
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