package net.fexcraft.app.fmt.launch;

import java.io.File;

import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Launcher {
	
	public static void main(String[] args){
		JsonMap map = JsonHandler.parse(new File("./catalog.fmt"));
		JsonMap lau = JsonHandler.parse(new File("./launch.fmt"));
		lau.add("test2", 12323.23f);
		lau.addArray("test.23323we");
		lau.getMap("test").getArray("23323we").add(23);
		lau.getMap("test").getArray("23323we").add("test");
		lau.addArray("test.nanine");
		lau.getMap("test").getArray("nanine").add("nanino");
		lau.getMap("test").getArray("nanine").add("nen");
		lau.getMap("test").getArray("nanine").addArray();
		lau.getMap("test").getArray("nanine").addMap();
		System.out.println(map);
		System.out.println(JsonHandler.toString(lau, true, false));
		System.out.println(JsonHandler.toString(lau, true, true));
	}

}
