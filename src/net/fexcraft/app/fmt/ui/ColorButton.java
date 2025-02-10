package net.fexcraft.app.fmt.ui;

import com.spinyowl.legui.component.Component;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.input.Mouse;
import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.texture.TexturePainter;
import org.joml.Vector4f;

public class ColorButton extends Component {

	private int value;

	public ColorButton(EditorComponent root, int x, int y, int w, int h){
		setPosition(x, y);
		setSize(w, h);
		Settings.applyBorderless(this);
		getListenerMap().addListener(MouseClickEvent.class, e -> {
			if(e.getButton() == Mouse.MouseButton.MOUSE_BUTTON_LEFT && e.getAction() == MouseClickEvent.MouseClickAction.CLICK){
				TexturePainter.updateColor(value, TexturePainter.ACTIVE, true);
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

	public int value(){
		return value;
	}

}
