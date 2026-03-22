package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.Logging;
import org.lwjgl.glfw.GLFW;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.ui.editor.EditorTab.FS;
import static net.fexcraft.app.fmt.settings.Settings.ROUNDING_DIGITS;
import static net.fexcraft.app.fmt.utils.Logging.log;
import static org.lwjgl.glfw.GLFW.*;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public class Field extends Element {

	public static NumberFormat nf;
	public static DecimalFormat df;
	static { updateRoundingDigits(); }
	public static int col_field = 0xa6b3b3;

	public final FieldType type;
	public Consumer<String> consumer;
	public String previous;
	private Element clear;
	private Element reset;
	private float min_val = Integer.MIN_VALUE;
	private float max_val = Integer.MAX_VALUE;
	private PolygonValue polyval;

	public Field(FieldType ftype, float width){
		super();
		type = ftype;
		size(width - (type.text() ? FS * 2 : 10), FS);
		hoverable = true;
		selectable = true;
		color(col_field);
	}

	public Field(FieldType type, float width, Consumer<String> cons){
		this(type, width);
		consumer = cons;
	}

	public Field(FieldType type, float width, UpdateCompound updcom, PolygonValue val){
		this(type, width);
		polyval = val;
		updcom.add(UpdateEvent.PolygonValueEvent.class, event -> {
			if(!event.first()) return;
			if(event.value().equals(val)){
				text(event.polygon().getValue(val));
			}
		});
		updcom.add(UpdateEvent.PolygonSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0)){
				text(FMT.MODEL.first_selected().getValue(val));
			}
			else if(event.selected() == 0) text(0);
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0 && FMT.MODEL.first_selected() != null)){
				text(FMT.MODEL.first_selected().getValue(val));
			}
			else if(event.selected() == 0) text(0);
		});
		consumer = input -> {
			FMT.MODEL.updateValue(val, this, 0);
		};
		onscroll(si -> {
			float flat = scroll(si.sy() > 0 ? Editor.RATE : -Editor.RATE);
			text(flat);
			previous = text.text();
			FMT.MODEL.updateValue(val, this, 0);
		});
	}

	@Override
	public void init(Object... args){
		text(type.text() ? "" : "0");
		text.autoscale = true;
		add(clear = new Element().color(0xffe600).size(type.text() ? FS : 5, FS).pos(w, 0)
			.text_centered(true).hoverable(true).onclick(info -> clear_text())
			.hint("editor.info.field_clear"));
		clear.hide();
		add(reset = new Element().color(0xf02c00).size(type.text() ? FS : 5, FS).pos(w + (type.text() ? FS : 5), 0)
			.text_centered(true).hoverable(true).onclick(info -> reset_text())
			.hint("editor.info.field_reset"));
		if(type.text()){
			clear.text("C");
			reset.text("R");
		}
		reset.hide();
	}

	@Override
	public Element text(Object ntext){
		super.text(ntext);
		return this;
	}

	@Override
	protected void onSelect(){
		previous = text.text();
		clear.show();
		reset.show();
	}

	@Override
	protected void onDeselect(Element current){
		reset_text();
		clear.hide();
		reset.hide();
	}

	public void clear_text(){
		text(type == FieldType.TEXT ? "" : "0");
	}

	public void reset_text(){
		text(previous);
	}

	public Field range(float min, float max){
		min_val = min;
		max_val = max;
		return this;
	}

	public Field min_range(float min){
		min_val = min;
		max_val = Integer.MAX_VALUE;
		return this;
	}

	public float parse_float(){
		String str = text.text().replaceAll("[^0-9\\.\\-]", "");
		if(str.isEmpty()) str = "0";
		if(str.endsWith(".")) str += "0";
		float res = Float.parseFloat(str);
		if(res < min_val) res = min_val;
		if(res > max_val) res = max_val;
		return res;
	}

	public float parse_int(){
		String str = text.text().replaceAll("[^0-9\\.\\-]", "");
		if(str.isEmpty()) str = "0";
		if(str.endsWith(".")) str.replace(".", "");
		int res = Integer.parseInt(str);
		if(res < min_val) res = (int)min_val;
		if(res > max_val) res = (int)max_val;
		return res;
	}

	private float scroll(float rate){
		float flat = parse_float();
		flat += rate;
		if(flat > max_val) flat = max_val;
		if(flat < min_val) flat = min_val;
		try{
			Number num = nf.parse(df.format(flat));
			flat = type.decimal() ? num.floatValue() : num.intValue();
		} catch(ParseException e){
			log(e);
		}
		return flat;
	}

	public Field set(float val){
		text(val);
		return this;
	}

	public boolean onInput(int key, int code, int action, int mods){
		if(key == GLFW_KEY_LEFT_SHIFT || key == GLFW_KEY_RIGHT_SHIFT){
			return false;
		}
		if(key == GLFW_KEY_ESCAPE){
			select(null);
			return false;
		}
		if(action != GLFW_RELEASE) return true;
		if(key == GLFW_KEY_ENTER || key == GLFW_KEY_KP_ENTER){
			if(consumer != null) consumer.accept(text.text());
			previous = text.text();
			return true;
		}
		String txt = text.text();
		if(key == GLFW_KEY_BACKSPACE){
			if(!txt.isEmpty()){
				text(txt.substring(0, txt.length() - 1));
			}
			return true;
		}
		if(type.text()){
			if(key >= GLFW_KEY_APOSTROPHE && key <= GLFW_KEY_GRAVE_ACCENT){
				String kn = GLFW.glfwGetKeyName(key, code);
				if(kn == null) kn = "";
				text(txt + kn);
			}
			if(key == GLFW_KEY_SPACE){
				text(txt + " ");
			}
		}
		else{
			if(key >= GLFW_KEY_0 && key <= GLFW_KEY_9){
				int n = key - GLFW_KEY_0;
				if(txt.equals("0")) text(txt.substring(1) + n);
				else text(txt + n);
			}
			if(key == GLFW_KEY_MINUS){
				if(txt.contains("-")){
					text(txt.replace("-", ""));
				}
				else{
					text("-" + txt);
				}
			}
			if(key == GLFW_KEY_PERIOD){
				if(txt.isEmpty() || txt.equals("-")){
					text(txt + "0.");
				}
				else if(!txt.contains(".")){
					text(txt + ".");
				}
			}
		}
		return true;
	}

	public PolygonValue polyval(){
		return polyval;
	}

	public static enum FieldType {

		TEXT,
		FLOAT,
		INT;

		public boolean text(){
			return this == TEXT;
		}

		public boolean decimal(){
			return this == FLOAT;
		}

		public boolean integer(){
			return this == INT;
		}

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

	public static float round(float flat){
		try{
			return nf.parse(df.format(flat)).floatValue();
		} catch(ParseException e){
			Logging.log(e);
			return flat;
		}
	}

}
