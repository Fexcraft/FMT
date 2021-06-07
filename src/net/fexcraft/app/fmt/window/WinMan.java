package net.fexcraft.app.fmt.window;

import java.util.HashMap;

/**
 * 
 * @author Ferdinand Calo' (FEX___96)
 *
 */
public class WinMan {
	
	public static final HashMap<String, WinCon> WINDOWS = new HashMap<>();

	public static void add(String title, WinCon wincon){
		if(WINDOWS.containsKey(title)) WINDOWS.get(title).close();
		WINDOWS.put(title, wincon);
	}
	
}
