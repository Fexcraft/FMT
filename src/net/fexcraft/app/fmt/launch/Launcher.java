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
	}

}
