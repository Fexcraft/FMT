package net.fexcraft.app.fmt.ui.fields;

import static net.fexcraft.app.fmt.settings.Settings.ROUNDING_DIGITS;
import static net.fexcraft.app.fmt.utils.Logging.log;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.Consumer;

import net.fexcraft.app.fmt.update.UpdateHandler;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateHolder;
import org.liquidengine.legui.component.TextInput;
import org.liquidengine.legui.event.FocusEvent;
import org.liquidengine.legui.event.KeyEvent;
import org.liquidengine.legui.listener.FocusEventListener;
import org.liquidengine.legui.listener.KeyEventListener;
import org.lwjgl.glfw.GLFW;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.settings.Setting;
import net.fexcraft.app.fmt.settings.Settings;
import net.fexcraft.app.fmt.ui.Editor;
import net.fexcraft.app.fmt.ui.EditorComponent;
import net.fexcraft.app.fmt.utils.Logging;

public class NumberField extends TextInput implements Field {
	
	private static NumberFormat nf;
	private static DecimalFormat df;
	static { updateRoundingDigits(); }
	private Setting<?> setting;

	public NumberField(UpdateHolder holder, float x, float y, float w, float h){
		super("0", x, y, w, h);
		Settings.applyBorderless(this);
		Settings.applyGrayText(this);
		Field.setupHoverCheck(this);
		rootholder = holder;
	}

	public NumberField(EditorComponent comp, float x, float y, float w, float h){
		this(comp.getUpdateHolder(), x, y, w, h);
	}

	public NumberField(Setting<?> setting, float x, float y, float w, float h){
		super(setting.value.toString(), x, y, w, h);
		Settings.applyMenuTheme(this);
		Settings.applyGrayText(this);
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
		this.setting = setting;
	}

	private UpdateHolder rootholder;
	private EditorComponent comp;
	private PolygonValue poly_value;
	private boolean floatfield, indexfield;
	private float min, max;
	protected Float value = null;
	protected Consumer<NumberField> update;
	
	public NumberField setup(float min, float max, boolean flaot, PolygonValue val){
		floatfield = flaot;
		this.min = min;
		this.max = max;
		poly_value = val;
		Field.setupHolderAndListeners(this, rootholder, val);
		return this;
	}
	
	public NumberField setup(float min, float max, boolean flaot, Consumer<NumberField> update){
		floatfield = flaot;
		this.min = min;
		this.max = max;
		this.update = update;
		addTextInputContentChangeEventListener(event -> {
			Field.validateNumber(event);
			value = null;
		});
		getListenerMap().addListener(FocusEvent.class, (FocusEventListener)listener -> {
			if(!listener.isFocused()) update.accept(this);
		});
		getListenerMap().addListener(KeyEvent.class, (KeyEventListener)listener -> {
			if(listener.getKey() == GLFW.GLFW_KEY_ENTER) update.accept(this);
		});
		return this;
	}
	
	public NumberField index(){
		indexfield = true;
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
		flat += positive ? rate : -rate;
		if(flat > max) flat = max;
		if(flat < min) flat = min;
		try{
			Number num = nf.parse(df.format(flat));
			return floatfield ? num.floatValue() : indexfield && positive && num.floatValue() % 1f > 0 ? num.intValue() + 1 : num.intValue();
		}
		catch(ParseException e){
			log(e);
			return flat;
		}
	}

	@Override
	public NumberField apply(float val){
		getTextState().setText((value = val) + "");
		setCaretPosition(getTextState().getText().length());
		return this;
	}

	@Override
	public void scroll(double scroll){
		apply(test(value(), scroll > 0, Editor.RATE));
		if(poly_value != null){
			FMT.MODEL.updateValue(poly_value, this, 0);
		}
		if(update != null) update.accept(this);
	}

	@Override
	public String id(){
		return poly_value.toString();
	}
	
	@Override
	public Consumer<NumberField> update(){
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

	@Override
	public PolygonValue polyval(){
		return poly_value;
	}
	
	public static DecimalFormat getFormat(){
		return df;
	}

	@Override
	public Setting<?> setting(){
		return setting;
	}
	
	public static float round(float flat){
		try{
			return nf.parse(df.format(flat)).floatValue();
		}
		catch(ParseException e){
			Logging.log(e);
			return flat;
		}
	}
	
}
