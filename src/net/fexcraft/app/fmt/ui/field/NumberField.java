package net.fexcraft.app.fmt.ui.field;

import java.text.NumberFormat;
import java.util.Locale;

import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.lwjgl.glfw.GLFW;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.UserInterfaceUtils;
import net.fexcraft.app.fmt.utils.Settings.Setting;

public class NumberField extends TextInput implements Field {
	
	public static final NumberFormat nf = NumberFormat.getInstance(Locale.US);
	static { nf.setMaximumFractionDigits(4); }

	public NumberField(int x, int y, int w, int h){
		super("0", x, y, w, h); getStyle().setFontSize(20f); UserInterfaceUtils.setupHoverCheck(this);
	}
	
	public NumberField(Setting setting, int x, int y, int w, int h){
		super(setting.toString(), x, y, w, h); getStyle().setFontSize(20f); UserInterfaceUtils.setupHoverCheck(this);
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){ setting.validateAndApply(getTextState().getText()); }
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){ setting.validateAndApply(getTextState().getText()); }
		});
	}

	private String fieldid;
	private boolean floatfield;
	private float min, max;
	private Float value = null;
	private Runnable update;
	
	@SuppressWarnings("unchecked")
	public NumberField setup(String id, float min, float max, boolean flaot){
		floatfield = flaot; this.min = min; this.max = max; fieldid = id;
		addTextInputContentChangeEventListener(event -> {
			UserInterfaceUtils.validateNumber(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()) FMTB.MODEL.updateValue(this, id);
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER) FMTB.MODEL.updateValue(this, id);
		});
		return this;
	}
	
	@SuppressWarnings("unchecked")
	public NumberField setup(float min, float max, boolean flaot, Runnable update){
		floatfield = flaot; this.min = min; this.max = max; this.update = update;
		addTextInputContentChangeEventListener(event -> {
			UserInterfaceUtils.validateNumber(event); value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()) update.run();
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER) update.run();
		});
		return this;
	}
	
	@Override
	public float getValue(){
		if(value != null) return value;
		float newval = 0; String text = this.getTextState().getText();
		try{
			newval = floatfield ? nf.parse(text).floatValue() : nf.parse(text).intValue();
			//newval = floatfield ? Float.parseFloat(text) : Integer.parseInt(text);
		} catch(Exception e){ e.printStackTrace(); }
		if(newval > max) newval = max; else if(newval < min) newval = min;
		if(!(newval + "").equals(text)) apply(newval);
		return value = newval;
	}

	@Override
	public float tryAdd(float flat, boolean positive, float rate){
		flat += positive ? rate : -rate; if(flat > max) flat = max; if(flat < min) flat = min; return floatfield ? flat : (int)flat;
	}

	@Override
	public void apply(float val){
		getTextState().setText((value = val) + ""); setCaretPosition(getTextState().getText().length());
	}

	@Override
	public void onScroll(double yoffset){
		apply(tryAdd(getValue(), yoffset > 0, FMTB.MODEL.rate)); //Print.console(value);
		if(fieldid != null) FMTB.MODEL.updateValue(this, fieldid, yoffset > 0); if(update != null) update.run();
	}

	@Override
	public String id(){
		return fieldid;
	}
	
	@Override
	public Runnable update(){
		return update;
	}
	
}
