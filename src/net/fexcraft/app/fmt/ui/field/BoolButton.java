package net.fexcraft.app.fmt.ui.field;

import static org.liquidengine.legui.event.MouseClickEvent.MouseClickAction.CLICK;

import org.liquidengine.legui.component.Button;
import org.liquidengine.legui.event.MouseClickEvent;
import org.liquidengine.legui.listener.MouseClickEventListener;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.utils.Setting;
import net.fexcraft.app.fmt.utils.Settings;

public class BoolButton extends Button implements Field {

	private String fieldid;

	public BoolButton(String id, int x, int y, int w, int h){
		super("false", x, y, w, h);
		this.fieldid = id;
		Settings.THEME_CHANGE_LISTENER.add(bool -> this.getStyle().setBorderRadius(0f));
		UserInterfaceUtils.setupHoverCheck(this);
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				toggle();
			}
			else return;
		});
	}

	public BoolButton(Setting setting, int x, int y, int w, int h){
		super(setting.getBooleanValue() + "", x, y, w, h);
		Settings.THEME_CHANGE_LISTENER.add(bool -> this.getStyle().setBorderRadius(0f));
		UserInterfaceUtils.setupHoverCheck(this);
		this.getListenerMap().addListener(MouseClickEvent.class, (MouseClickEventListener)event -> {
			if(event.getAction() == CLICK){
				setting.toggle();
				getTextState().setText(setting.getBooleanValue() + "");
			}
			else return;
		});
	}

	private void toggle(){
		boolean val = Boolean.parseBoolean(getTextState().getText());
		getTextState().setText(!val + "");
		FMTB.MODEL.updateValue(this, fieldid);
	}

	@Override
	public float getValue(){
		return Boolean.parseBoolean(getTextState().getText()) ? 1 : 0;
	}

	@Override
	public float tryAdd(float value, boolean positive, float rate){
		return positive ? 1 : 0;
	}

	@Override
	public void apply(float f){
		getTextState().setText((f > .5) + "");
	}

	@Override
	public void onScroll(double yoffset){
		apply(tryAdd(getValue(), yoffset > 0, FMTB.MODEL.rate));
		FMTB.MODEL.updateValue(this, fieldid, yoffset > 0);
	}

	@Override
	public String id(){
		return fieldid;
	}

}
