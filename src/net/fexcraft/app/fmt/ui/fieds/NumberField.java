package net.fexcraft.app.fmt.ui.fieds;

import static net.fexcraft.app.fmt.settings.Settings.ROUNDING_DIGITS;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.lwjgl.glfw.GLFW;

import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;

public class NumberField extends TextInput implements Field {
	
	private static NumberFormat nf;
	private static DecimalFormat df;
	static { updateRoundingDigits(); }

	public NumberField(float x, float y, float w, float h){
		super("0", x, y, w, h);
		Settings.applyBorderless(this);
		Field.setupHoverCheck(this);
	}

	public NumberField(Setting<?> setting, float x, float y, float w, float h){
		super(setting.toString(), x, y, w, h);
		Settings.applyBorderless(this);
		Field.setupHoverCheck(this);
		getListenerMap().addListener(FocusEvent.class, listener -> {
			if(!listener.isFocused()){
				setting.validate(true, getTextState().getText());
			}
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
				setting.validate(true, getTextState().getText()); 
			}
		});
	}

	private String fieldid;
	private boolean floatfield;
	private float min, max;
	private Float value = null;
	private Runnable update;
	
	public NumberField setup(String id, float min, float max, boolean flaot){
		floatfield = flaot;
		this.min = min;
		this.max = max;
		fieldid = id;
		addTextInputContentChangeEventListener(event -> {
			Field.validateNumber(event);
			value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()){
				//TODO update tracked model value/attribute
			}
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER){
				//TODO update tracked model value/attribute
			}
		});
		return this;
	}
	
	public NumberField setup(float min, float max, boolean flaot, Runnable update){
		floatfield = flaot;
		this.min = min;
		this.max = max;
		this.update = update;
		addTextInputContentChangeEventListener(event -> {
			Field.validateNumber(event);
			value = null;
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
	public float value(){
		if(value != null) return value;
		float newval = 0; String text = this.getTextState().getText();
		try{
			newval = floatfield ? nf.parse(text).floatValue() : nf.parse(text).intValue();
		}
		catch(Exception e){
			log(e);
		}
		if(newval > max) newval = max; else if(newval < min) newval = min;
		if(!(newval + "").equals(text)) apply(newval);
		return value = newval;
	}

	@Override
	public float test(float flat, boolean positive, float rate){
		flat += positive ? rate : -rate; if(flat > max) flat = max; if(flat < min) flat = min;
		try{
			Number num = nf.parse(df.format(flat));
			return floatfield ? num.floatValue() : num.intValue();
		}
		catch(ParseException e){
			log(e);
			return flat;
		}
	}

	@Override
	public void apply(float val){
		getTextState().setText((value = val) + ""); setCaretPosition(getTextState().getText().length());
	}

	@Override
	public void scroll(double scroll){
		apply(test(value(), scroll > 0, 1f));//TODO global rate value
		if(fieldid != null){
			//TODO update tracked model value/attribute
			//<>.update(this, fieldid, scroll > 0);
			if(update != null) update.run();
		}
	}

	@Override
	public String id(){
		return fieldid;
	}
	
	@Override
	public Runnable update(){
		return update;
	}

	public static void updateRoundingDigits(){
		nf = NumberFormat.getInstance(Locale.US);
		nf.setMaximumFractionDigits(ROUNDING_DIGITS.value);
		String str = "#.";
		for(int i = 0; i < nf.getMaximumFractionDigits(); i++){
			str += "#";
		}
		df = new DecimalFormat(str, new DecimalFormatSymbols(Locale.US));
		df.setRoundingMode(RoundingMode.HALF_EVEN);
	}
	
	public NumberField floatbased(boolean bool){
		this.floatfield = bool;
		return this;
	}
	
}
