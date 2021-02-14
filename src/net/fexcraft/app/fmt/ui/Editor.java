package net.fexcraft.app.fmt.ui;

import java.util.ArrayList;
import java.util.HashMap;

import org.liquidengine.legui.component.Panel;

import com.google.gson.JsonObject;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.utils.Jsoniser;

public class Editor extends Panel {
	
	public static HashMap<String, Editor> EDITORS = new HashMap<>();
	public static ArrayList<Editor> EDITORLIST = new ArrayList<>();
	public static int WIDTH = 310;
	public boolean alignment;
	public String name;
	
	public Editor(String id, String name, boolean left){
		Settings.applyBorderless(this);
		EDITORS.put(id, this);
		EDITORLIST.add(this);
		alignment = left;
		align();
	}

	public Editor(String key, JsonObject obj){
		this(key, Jsoniser.get(obj, "name", "Nameless Editor"), Jsoniser.get(obj, "alignment", true));
	}

	public void align(){
		this.setPosition(alignment ? 0 : FMT.WIDTH - WIDTH, ToolbarMenu.HEIGHT);
		this.setSize(WIDTH, FMT.HEIGHT - ToolbarMenu.HEIGHT);
	}
	
}
