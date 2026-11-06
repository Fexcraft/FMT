package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_0;
import static net.fexcraft.app.fmt.settings.Settings.GENERIC_BACKGROUND_1;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FF;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PosPasteButton extends Element {

	private Consumer<float[]> consumer;
	private Element menu;

	public PosPasteButton(int idx, int y, PolyVal val){
		this(idx, y, val, null);
	}

	public PosPasteButton(int idx, int y, PolyVal val, Consumer<float[]> supp){
		pos(FF - 15 - (idx * 25), y + 5);
		size(20, 20);
		texture("icons/component/move_left");
		if(supp == null) supp = fa -> {};
		selectable = true;
		consumer = supp;
		hint("editor.info.poly_value_paste");
	}

	@Override
	protected void onSelect(){
		if(elements == null){
			menu = new Element();
			add(menu.size(220, 170).pos(w, 0).color(GENERIC_BACKGROUND_0.value).border(RGB.BLACK));
			menu.add(new Element().size(200, 30).pos(10, 10).hoverable(true)
				.translate("editor.info.poly_value_paste.spaced").color(GENERIC_BACKGROUND_1.value).border(RGB.BLACK)
				.onclick(ci -> {
					try{
						String[] str = getCP().split(" ");
						float[] arr = new float[3];
						if(str.length > 0) arr[0] = Float.parseFloat(str[0].trim());
						if(str.length > 1) arr[1] = Float.parseFloat(str[1].trim());
						if(str.length > 2) arr[2] = Float.parseFloat(str[2].trim());
						consumer.accept(arr);
						menu.hide();
					}
					catch(NumberFormatException e){
						e.printStackTrace();
					}
				}));
			menu.add(new Element().size(200, 30).pos(10, 50).hoverable(true)
				.translate("editor.info.poly_value_paste.comma").color(GENERIC_BACKGROUND_1.value).border(RGB.BLACK)
				.onclick(ci -> {
					try{
						String[] str = getCP().split(",");
						float[] arr = new float[3];
						if(str.length > 0) arr[0] = Float.parseFloat(str[0].trim());
						if(str.length > 1) arr[1] = Float.parseFloat(str[1].trim());
						if(str.length > 2) arr[2] = Float.parseFloat(str[2].trim());
						consumer.accept(arr);
						menu.hide();
					}
					catch(NumberFormatException e){
						e.printStackTrace();
					}
				}));
			menu.add(new Element().size(200, 30).pos(10, 90).hoverable(true)
				.translate("editor.info.poly_value_paste.json_array").color(GENERIC_BACKGROUND_1.value).border(RGB.BLACK)
				.onclick(ci -> {
					try{
						JsonArray jar = JsonHandler.parse(getCP(), false).asArray();
						float[] arr = new float[3];
						arr[0] = jar.size() > 0 ? jar.get(0).float_value() : 0f;
						arr[1] = jar.size() > 1 ? jar.get(1).float_value() : 0f;
						arr[2] = jar.size() > 2 ? jar.get(2).float_value() : 0f;
						consumer.accept(arr);
						menu.hide();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}));
			menu.add(new Element().size(200, 30).pos(10, 130).hoverable(true)
				.translate("editor.info.poly_value_paste.json_map").color(GENERIC_BACKGROUND_1.value).border(RGB.BLACK)
				.onclick(ci -> {
					try{
						JsonMap map = JsonHandler.parse(getCP(), true).asMap();
						float[] arr = new float[3];
						arr[0] = map.getFloat("x", 0f);
						arr[1] = map.getFloat("y", 0f);
						arr[2] = map.getFloat("z", 0f);
						consumer.accept(arr);
						menu.hide();
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}));
		}
		menu.show();
	}

	@Override
	protected void onDeselect(Element current){
		if(menu != null) menu.hide();
	}

	private static String v(float x){
		if(x % 1f == 0) return (int)x + "";
		return x + "";
	}

	private static String getCP(){
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		Transferable data = cp.getContents(null);
		if(!data.isDataFlavorSupported(DataFlavor.stringFlavor)) return "";
		try{
			return data.getTransferData(DataFlavor.stringFlavor).toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return "";
		}
	}

}
