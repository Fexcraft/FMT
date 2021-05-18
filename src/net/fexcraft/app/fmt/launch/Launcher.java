package net.fexcraft.app.fmt.launch;

import java.io.File;

import net.fexcraft.app.json.FJHandler;
import net.fexcraft.app.json.FJMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Launcher {
	
	public static void main(String[] args){
		FJMap map = FJHandler.parse(new File("./catalog.fmt"));
		FJMap lau = FJHandler.parse(new File("./launch.fmt"));
	}

}
