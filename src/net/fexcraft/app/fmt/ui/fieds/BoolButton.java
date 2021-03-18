package net.fexcraft.app.fmt.ui.fieds;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;

public class BoolButton extends Button implements Field {

	private String fieldid;

	public BoolButton(String id, int x, int y, int w, int h){
		super("false", x, y, w, h);
		this.fieldid = id;
		Settings.applyBorderless(this);
		Field.setupHoverCheck(this);
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				toggle();
			}
			else return;
		});
	}

	public BoolButton(Setting<Boolean> setting, int x, int y, int w, int h){
		super(setting.value + "", x, y, w, h);
		Settings.applyBorderless(this);
		Field.setupHoverCheck(this);
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				setting.toggle();
				getTextState().setText(setting.value + "");
			}
			else return;
		});
	}

	private void toggle(){
		boolean val = Boolean.parseBoolean(getTextState().getText());
		getTextState().setText(!val + "");
		//TODO update tracked model value/attribute
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
	public void apply(float f){
		getTextState().setText((f > .5) + "");
	}

	@Override
	public void scroll(double yoffset){
		apply(test(value(), yoffset > 0, 1f));//TODO global rate value
		//TODO update tracked model value/attribute
		//<>.update(this, fieldid, scroll > 0);
	}

	@Override
	public String id(){
		return fieldid;
	}

}
