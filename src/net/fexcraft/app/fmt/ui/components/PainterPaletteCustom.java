package net.fexcraft.app.fmt.ui.components;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.event.MouseClickEvent.MouseClickAction;
import com.spinyowl.legui.input.Mouse.MouseButton;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.json.JsonArray;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import org.joml.Vector4f;

import java.util.ArrayList;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class PainterPaletteCustom extends EditorComponent {

	private ArrayList<ColorButton> cols = new ArrayList<>();

	public PainterPaletteCustom(){
		super("painter.palette.custom", 70, false, false);
		int yoff = row(1) + 5;
		for(int y = 0; y < 2; y++){
			for(int x = 0; x < 19; x++){
				ColorButton button = new ColorButton(this, 5 + (x * 15), yoff, 14, 14);
				button.set(RGB.WHITE.packed, FMT.rgba(0xffffff));
				cols.add(button);
				add(button);
			}
			yoff += 15;
		}
	}

	@Override
	public PainterPaletteCustom load(JsonMap map){
		if(map.has("colors")){
			JsonArray array = map.getArray("colors");
			for(int y = 0; y < 2; y++){
				for(int x = 0; x < 19; x++){
					int i = Integer.parseInt(array.get(x + 19 * y).string_value());
					cols.get(x + 19 * y).set(i, FMT.rgba(i));
				}
			}
		}
		return this;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		JsonArray array = new JsonArray();
		for(int y = 0; y < 2; y++){
			for(int x = 0; x < 19; x++){
				array.add(cols.get(x + 19 * y).value);
			}
		}
		map.add("colors", array);
		return map;
	}

	public static class ColorButton extends Component {

		private int value;

		public ColorButton(PainterPaletteCustom root, int x, int y, int w, int h){
			setPosition(x, y);
			setSize(w, h);
			Settings.applyBorderless(this);
			getListenerMap().addListener(MouseClickEvent.class, e -> {
				if(e.getButton() == MouseButton.MOUSE_BUTTON_LEFT && e.getAction() == MouseClickAction.CLICK){
					TexturePainter.updateColor(value, true, true);
				}
			});
		}

		public void set(int rgb, Vector4f color){
			getStyle().getBackground().setColor(color);
			value = rgb;
		}

		public void set(int rgb){
			getStyle().getBackground().setColor(FMT.rgba(rgb));
			value = rgb;
		}

	}

}
