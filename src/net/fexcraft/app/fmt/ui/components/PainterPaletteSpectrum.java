package net.fexcraft.app.fmt.ui.components;

import java.util.ArrayList;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.ui.components.PainterPaletteGradient.ColorButton;
import net.fexcraft.app.fmt.ui.fields.RunButton;
import net.fexcraft.app.fmt.update.UpdateEvent.PainterColor;
import net.fexcraft.app.json.JsonMap;
import net.fexcraft.lib.common.math.RGB;
import org.joml.Vector4f;
import org.liquidengine.legui.component.Component;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.event.MouseClickEvent.MouseClickAction;
import org.liquidengine.legui.input.Mouse.MouseButton;
import org.liquidengine.legui.style.Style.DisplayType;

public class PainterPaletteSpectrum extends EditorComponent {

	private ArrayList<ColorButton> cols = new ArrayList<>();
	private int c = 28, r = 12;
	private RunButton button;
	private boolean prim;

	public PainterPaletteSpectrum(){
		this(true);
	}

	public PainterPaletteSpectrum(boolean bool){
		super("painter.palette.spectrum", 60, false, false);
		add(button = new RunButton("", L5 + LW - 88, row(0) + 5, 80, 20, () -> {
			prim = !prim;
			refresh();
		}));
		prim = bool;
		int yoff = row(1) + 5;
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
		refresh();
	}

	private void refresh(){
		button.getTextState().setText(prim ? "primary" : "secondary");
	}

	@Override
	public PainterPaletteSpectrum load(JsonMap map){
		prim = map.getBoolean("primary", true);
		refresh();
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

		private int value;

		public ColorButton(PainterPaletteSpectrum root, int x, int y, int w, int h){
			setPosition(x, y);
			setSize(w, h);
			Settings.applyBorderless(this);
			getListenerMap().addListener(MouseClickEvent.class, e -> {
				if(e.getButton() == MouseButton.MOUSE_BUTTON_LEFT && e.getAction() == MouseClickAction.CLICK){
					TexturePainter.updateColor(value, root.prim, true);
				}
			});
		}

		public void set(int rgb, Vector4f color){
			getStyle().getBackground().setColor(color);
			value = rgb;
		}

	}

	@Override
	protected void toggleIconSpace(boolean bool){
		button.getStyle().setDisplay(bool ? DisplayType.NONE : DisplayType.MANUAL);
	}

}
