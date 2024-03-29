package net.fexcraft.app.fmt.ui.fields;

import static com.spinyowl.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import java.util.function.Consumer;

import com.spinyowl.legui.component.Button;
import com.spinyowl.legui.event.MouseClickEvent;
import com.spinyowl.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.EditorComponent;

public class BoolButton extends Button implements Field {

	private PolygonValue poly_value;
	private Setting<Boolean> setting;

	public BoolButton(EditorComponent comp, float x, float y, float w, float h, PolygonValue val){
		super("false", x, y, w, h);
		this.poly_value = val;
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		Field.setupUpdatesAndListeners(this, comp.getUpdCom(), val);
	}

	public BoolButton(Setting<Boolean> setting, float x, float y, float w, float h){
		super(setting.value + "", x, y, w, h);
		Settings.applyMenuTheme(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		this.setting = setting;
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				setting.toggle();
				getTextState().setText(setting.value + "");
			}
			else return;
		});
	}

	public BoolButton(float x, float y, float w, float h, boolean def, Consumer<Boolean> cons){
		super(def + "", x, y, w, h);
		Settings.applyMenuTheme(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				boolean bool = !Boolean.parseBoolean(getTextState().getText());
				cons.accept(bool);
				getTextState().setText(bool + "");
			}
			else return;
		});
	}

	protected void toggle(){
		boolean val = Boolean.parseBoolean(getTextState().getText());
		getTextState().setText(!val + "");
		FMT.MODEL.updateValue(poly_value, this, 0);
	}

	@Override
	public float value(){
		return Boolean.parseBoolean(getTextState().getText()) ? 1 : 0;
	}

	@Override
	public float test(float value, boolean positive, float rate){
		return positive ? 1 : 0;
	}

	@Override
	public BoolButton apply(float f){
		getTextState().setText((f > .5) + "");
		return this;
	}

	@Override
	public void scroll(double yoffset){
		/*apply(test(value(), yoffset > 0, Editor.RATE));
		if(poly_value != null){
			FMT.MODEL.updateValue(poly_value, this);
		}*/
	}

	@Override
	public String id(){
		return poly_value.toString();
	}

	@Override
	public PolygonValue polyval(){
		return poly_value;
	}

	@Override
	public Setting<?> setting(){
		return setting;
	}

}
