package net.fexcraft.app.fmt.launch;

import java.io.File;

import net.fexcraft.app.fyadf.FDFHandler;
import net.fexcraft.app.fyadf.FDFMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class Launcher {
	
	public static void main(String[] args){
		FDFMap map = FDFHandler.parse(new File("./catalog.fmt"));
	}

}
