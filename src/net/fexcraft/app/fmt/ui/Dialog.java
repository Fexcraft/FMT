package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;

public interface Dialog {
	
	public static ArrayList<Dialog> dialogs = new ArrayList<>();
	
	public static boolean anyVisible(){
		for(Dialog dialog : dialogs){
			if(dialog.visible()) return true;
		} return false;
	}
	
	public boolean visible();
	
	public void reset();

}
