package net.fexcraft.app.fmt.ui.components;

import java.util.ArrayList;

import com.spinyowl.legui.component.Label;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.ColorButton;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;

import static net.fexcraft.app.fmt.utils.Translator.translate;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PainterPalette extends EditorComponent {

	private ArrayList<ColorButton> grid = new ArrayList<>();
	private ArrayList<ColorButton> cols = new ArrayList<>();
	public static ArrayList<ColorButton> custom = new ArrayList<>();
	private int c = 28, r = 12;

	public PainterPalette(){
		super("painter.palette", 290, false, false);
		updcom.add(PainterColor.class, e -> {
			refresh(e.value(), e.upd_plt());
		});
		custom.clear();
		add(new Label(translate(LANG_PREFIX + "painter.palette.gradient"), L5, row(1), LW, HEIGHT));
		int yoff = row(1);
		for(int x = 0; x < c; x++){
			for(int y = 0; y < r; y++){
				ColorButton button = new ColorButton(this, x * 10 + 8, y * 10 + yoff, 10, 10);
				grid.add(button);
				add(button);
			}
		}
		add(new Label(translate(LANG_PREFIX + "painter.palette.spectrum"), L5, (row += 120), LW, HEIGHT));
		yoff = row(1);
		for(int i = 0; i < 36; i++){
			float c = i * (1f / 36);
			int r, g, b;
			//
			if(c >= 0 && c <= (1/6.f)){
				r = 255;
				g = (int)(1530 * c);
				b = 0;
			}
			else if( c > (1/6.f) && c <= (1/3.f) ){
				r = (int)(255 - (1530 * (c - 1/6f)));
				g = 255;
				b = 0;
			}
			else if( c > (1/3.f) && c <= (1/2.f)){
				r = 0;
				g = 255;
				b = (int)(1530 * (c - 1/3f));
			}
			else if(c > (1/2f) && c <= (2/3f)) {
				r = 0;
				g = (int)(255 - ((c - 0.5f) * 1530));
				b = 255;
			}
			else if( c > (2/3f) && c <= (5/6f) ){
				r = (int)((c - (2/3f)) * 1530);
				g = 0;
				b = 255;
			}
			else if(c > (5/6f) && c <= 1 ){
				r = 255;
				g = 0;
				b = (int)(255 - ((c - (5/6f)) * 1530));
			}
			else{
				r = 127;
				g = 127;
				b = 127;
			}
			ColorButton button = new ColorButton(this, 5 + (i * 8), yoff, 8, 20);
			button.set(new RGB(r, g, b).packed, FMT.rgba(r, g, b, 1));
			cols.add(button);
			add(button);
		}
		add(new Label(translate(LANG_PREFIX + "painter.palette.custom"), L5, row(1), LW, HEIGHT));
		yoff = row(1);
		for(int y = 0; y < 2; y++){
			for(int x = 0; x < 19; x++){
				ColorButton button = new ColorButton(this, 5 + (x * 15), yoff, 14, 14);
				button.set(RGB.WHITE.packed, FMT.rgba(0xffffff));
				custom.add(button);
				add(button);
			}
			yoff += 15;
		}
		refresh(null, true);
	}

	public static void saveCustom(){
		int am = 2 * 19 - 1;
		for(int i = am; i > 0; i--){
			custom.get(i).set(custom.get(i - 1).value());
		}
		custom.get(0).set(TexturePainter.CHANNELS[TexturePainter.ACTIVE].packed);
	}

	private void refresh(Integer value, boolean bool){
		if(!bool) return;
		int idx = 0;
		byte[] arr = value == null ? TexturePainter.getColor() : new RGB(value).toByteArray();
		for(int x = 0; x < c; x++){
			for(int z = 0; z < r; z++){
				int y = x * c + z;
				float e = (1f / (c * r)) * y, f = (1f / r) * z, h = (255f / c) * x;
				int r = (int)Math.abs((e * (arr[0] + 128)) + ((1 - f) * h));
				int g = (int)Math.abs((e * (arr[1] + 128)) + ((1 - f) * h));
				int b = (int)Math.abs((e * (arr[2] + 128)) + ((1 - f) * h));
				grid.get(idx++).set(new RGB(r, g, b).packed, FMT.rgba(r, g, b, 1));
			}
		}
	}

	@Override
	public PainterPalette load(JsonMap map){
		if(map.has("colors")){
			JsonArray array = map.getArray("colors");
			for(int y = 0; y < 2; y++){
				for(int x = 0; x < 19; x++){
					int z = x + 19 * y;
					custom.get(z).set(Integer.parseInt(array.get(z).string_value(), 16));
				}
			}
		}
		refresh(null, true);
		return this;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		JsonArray array = new JsonArray();
		for(int y = 0; y < 2; y++){
			for(int x = 0; x < 19; x++){
				array.add(Integer.toHexString(custom.get(x + 19 * y).value()));
			}
		}
		map.add("colors", array);
		return map;
	}

}
