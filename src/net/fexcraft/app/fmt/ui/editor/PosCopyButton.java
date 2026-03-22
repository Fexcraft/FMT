package net.fexcraft.app.fmt.ui.editor;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.polygon.Polygon;
import net.fexcraft.app.fmt.update.PolyVal;
import net.fexcraft.app.json.JsonHandler;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.util.function.Function;
import java.util.function.Supplier;

import static net.fexcraft.app.fmt.FMT.MODEL;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_bd;
import static net.fexcraft.app.fmt.ui.FMTInterface.col_cd;
import static net.fexcraft.app.fmt.ui.editor.EditorTab.FF;
import static net.fexcraft.app.fmt.update.PolyVal.PolygonValue.of;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PosCopyButton extends Element {

	private static Function<PolyVal, float[]> SUPPLIER = val -> {
		float[] arr = new float[3];
		if(MODEL.selected().isEmpty()) return arr;
		Polygon poly = MODEL.selected().get(0);
		arr[0] = poly.getValue(of(val, PolyVal.ValAxe.X));
		arr[1] = poly.getValue(of(val, PolyVal.ValAxe.Y));
		arr[2] = poly.getValue(of(val, PolyVal.ValAxe.Z));
		if(FMT.MODEL.orient.rect() || val == PolyVal.SIZE){
			arr[0] *= .0625f;
			arr[1] *= .0625f;
			arr[2] *= .0625f;
		}
		else{
			float v = arr[0];
			arr[0] = arr[2] * -.0625f;
			arr[1] *= -.0625f;
			arr[2] = v * -.0625f;
		}
		return arr;
	};
	private Supplier<float[]> supplier;
	private Element menu;

	public PosCopyButton(int idx, int y, PolyVal val){
		this(idx, y, val, null);
	}

	public PosCopyButton(int idx, int y, PolyVal val, Supplier<float[]> supp){
		pos(FF - 15 - (idx * 25), y + 5);
		size(20, 20);
		texture("icons/configeditor/confirm");
		if(supp == null) supp = () -> SUPPLIER.apply(val);
		selectable = true;
		supplier = supp;
		hint("editor.info.poly_value_copy");
	}

	@Override
	protected void onSelect(){
		if(elements == null){
			menu = new Element();
			add(menu.size(220, 170).pos(w, 0).color(col_cd).border(RGB.BLACK));
			menu.add(new Element().size(200, 30).pos(10, 10).hoverable(true)
				.translate("editor.info.poly_value_copy.spaced").color(col_bd).border(RGB.BLACK)
				.onclick(ci -> {
					float[] arr = supplier.get();
					setCP(v(arr[0]) + " " + v(arr[1]) + " " + v(arr[2]));
					menu.hide();
				}));
			menu.add(new Element().size(200, 30).pos(10, 50).hoverable(true)
				.translate("editor.info.poly_value_copy.comma").color(col_bd).border(RGB.BLACK)
				.onclick(ci -> {
					float[] arr = supplier.get();
					setCP(v(arr[0]) + ", " + v(arr[1]) + ", " + v(arr[2]));
					menu.hide();
				}));
			menu.add(new Element().size(200, 30).pos(10, 90).hoverable(true)
				.translate("editor.info.poly_value_copy.json_array").color(col_bd).border(RGB.BLACK)
				.onclick(ci -> {
					float[] arr = supplier.get();
					setCP("[ " + v(arr[0]) + ", " + v(arr[1]) + ", " + v(arr[2]) + " ]");
					menu.hide();
				}));
			menu.add(new Element().size(200, 30).pos(10, 130).hoverable(true)
				.translate("editor.info.poly_value_copy.json_map").color(col_bd).border(RGB.BLACK)
				.onclick(ci -> {
					float[] arr = supplier.get();
					JsonMap map = new JsonMap();
					map.add("x", v(arr[0]));
					map.add("y", v(arr[1]));
					map.add("z", v(arr[2]));
					setCP(JsonHandler.toString(map, JsonHandler.PrintOption.FLAT_SPACED));
					menu.hide();
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

	private static void setCP(String s){
		Clipboard cp = Toolkit.getDefaultToolkit().getSystemClipboard();
		cp.setContents(new StringSelection(s), new StringSelection("fmt_pos"));
	}

}
