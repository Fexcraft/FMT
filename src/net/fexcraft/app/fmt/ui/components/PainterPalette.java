package net.fexcraft.app.fmt.ui.components;

import static net.fexcraft.app.fmt.utils.Translator.translate;

import java.util.ArrayList;

import javax.swing.*;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.component.Label;

public class PainterPalette extends EditorComponent {

	private ArrayList<Component> cols = new ArrayList<>();
	private int c = 28, r = 12;
	private RunButton button;
	private boolean prim;

	public PainterPalette(){
		this(true);
	}

	public PainterPalette(boolean bool){
		super("painter.palette", 160, false, false);
		add(button = new RunButton("", L5 + LW - 88, row(0) + 5, 80, 20, () -> {
			prim = !prim;
			refresh(null);
		}));
		prim = bool;
		updcom.add(PainterColor.class, e -> {
			if(e.primary() == prim){
				refresh(e.value());
			}
		});
		int yoff = row(1) + 5;
		for(int x = 0; x < c; x++){
			for(int y = 0; y < r; y++){
				ColorButton button = new ColorButton(x * 10 + 8, y * 10 + yoff, 10);
				cols.add(button);
				add(button);
			}
		}
		refresh(null);
	}

	private void refresh(Integer value){
		button.getTextState().setText(prim ? "primary" : "secondary");
		int idx = 0;
		byte[] arr = value == null ? prim ? TexturePainter.getPrimaryColor() : TexturePainter.getSecondaryColor() : new RGB(value).toByteArray();
		for(int x = 0; x < c; x++){
			for(int z = 0; z < r; z++){
				int y = x * c + z;
				float e = (1f / (c * r)) * y, f = (1f / r) * z, h = (255f / c) * x;
				int r = (int)Math.abs((e * (arr[0] + 128)) + ((1 - f) * h));
				int g = (int)Math.abs((e * (arr[1] + 128)) + ((1 - f) * h));
				int b = (int)Math.abs((e * (arr[2] + 128)) + ((1 - f) * h));
				cols.get(idx++).getStyle().getBackground().setColor(FMT.rgba(r, g, b, 1));
			}
		}
	}

	@Override
	public PainterPalette load(JsonMap map){
		prim = map.getBoolean("primary", true);
		refresh(null);
		return this;
	}

	@Override
	public JsonMap save(){
		JsonMap map = new JsonMap();
		map.add("id", id);
		map.add("primary", prim);
		return map;
	}

	public static class ColorButton extends Component {

		public ColorButton(int x, int y, int size){
			setPosition(x, y);
			setSize(size, size);
			Settings.applyBorderless(this);
		}

	}

}
