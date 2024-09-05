package net.fexcraft.app.fmt.ui;

import com.spinyowl.legui.component.Component;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuButton;
import net.fexcraft.app.fmt.ui.ToolbarMenu.MenuLayer;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import org.joml.Vector2f;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.ArrayList;
import java.util.function.Supplier;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PosCopyMenu {
	
	private static MenuLayer layer;
	private static float x, y, z;
	static {
		ArrayList<Component> components = new ArrayList<>();
		components.add(new MenuButton(0, "poscopymenu.str_spaced", () -> {
			setCP(v(x) + " " + v(y) + " " + v(z));
			layer.hide();
		}));
		components.add(new MenuButton(1, "poscopymenu.str_comma", () -> {
			setCP(v(x) + ", " + v(y) + ", " + v(z));
			layer.hide();
		}));
		components.add(new MenuButton(2, "poscopymenu.json_arr", () -> {
			setCP("[ " + v(x) + ", " + v(y) + ", " + v(z) + " ]");
			layer.hide();
		}));
		components.add(new MenuButton(3, "poscopymenu.json_map", () -> {
			JsonMap map = new JsonMap();
			map.add("x", v(x));
			map.add("y", v(y));
			map.add("z", v(z));
			setCP(JsonHandler.toString(map, JsonHandler.PrintOption.FLAT_SPACED));
			layer.hide();
		}));
		layer = new MenuLayer(null, new Vector2f((float)GGR.posx, (float)GGR.posy), components, null){
			@Override
			public boolean timed(){
				return true;
			}
		}.expand(100);
	}

	private static String v(float x){
		if(x % 1f == 0) return (int)x + "";
		return x + "";
	}

	private static void setCP(String s){
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		cp.setContents(new StringSelection(s), new StringSelection("fmt_pos"));
	}

	public static void show(Supplier<float[]> supp){
		float[] arr = supp.get();
		x = arr[0]; y = arr[1]; z = arr[2];
		layer.setPosition((float)GGR.posx, (float)GGR.posy);
		layer.show();
	}

}
