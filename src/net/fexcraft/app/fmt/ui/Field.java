package net.fexcraft.app.fmt.ui;

import net.fexcraft.app.fmt.FMT;
import net.fexcraft.app.fmt.oui.Editor;
import net.fexcraft.app.fmt.update.PolyVal.PolygonValue;
import net.fexcraft.app.fmt.update.UpdateEvent;
import net.fexcraft.app.fmt.update.UpdateHandler.UpdateCompound;
import net.fexcraft.app.fmt.utils.Logging;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.util.tinyfd.TinyFileDialogs;

import java.math.RoundingMode;
import java.nio.ByteBuffer;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;
import java.util.function.Consumer;

import static net.fexcraft.app.fmt.settings.Settings.GENERIC_FIELD;
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

	public final FieldType type;
	public Consumer<Field> consumer;
	public String previous;
	private Element clear;
	private Element reset;
	private Element color;
	private float min_val = Integer.MIN_VALUE;
	private float max_val = Integer.MAX_VALUE;
	private PolygonValue polyval;

	public Field(FieldType ftype, float width){
		super();
		text("");
		text_pos(5, -2);
		type = ftype;
		size(type.width(width), FS);
		hoverable = true;
		selectable = true;
		color(GENERIC_FIELD.value);
	}

	public Field(FieldType type, float width, Consumer<Field> cons){
		this(type, width);
		consumer = cons;
	}

	public Field(FieldType type, float width, UpdateCompound updcom, PolygonValue val){
		this(type, width);
		polyval = val;
		updcom.add(UpdateEvent.PolygonValueEvent.class, event -> {
			if(!event.first()) return;
			if(event.value().equals(val)){
				text(type_format(event.polygon().getValue(val)));
			}
		});
		updcom.add(UpdateEvent.PolygonSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0)){
				text(type_format(FMT.MODEL.first_selected().getValue(val)));
			}
			else if(event.selected() == 0) text(0);
		});
		updcom.add(UpdateEvent.GroupSelected.class, event -> {
			if(event.prevselected() < 0) return;
			else if(event.selected() == 1 || (event.prevselected() == 0 && event.selected() == 0 && FMT.MODEL.first_selected() != null)){
				text(type_format(FMT.MODEL.first_selected().getValue(val)));
			}
			else if(event.selected() == 0) text(0);
		});
		consumer = input -> {
			FMT.MODEL.updateValue(val, this, 0);
		};
		onscroll(si -> {
			float flat = scroll(si.sy() > 0 ? Editor.RATE : -Editor.RATE);
			text(type_format(flat));
			previous = text.text();
			FMT.MODEL.updateValue(val, this, 0);
		});
	}

	public Field consumer(Consumer<Field> cons){
		consumer = cons;
		return this;
	}

	@Override
	public void init(Object... args){
		text(type.text() ? "" : "0");
		text.autoscale = true;
		if(!type.info()){
			add(clear = new Element().color(0xffe600).size(type.text() ? FS : 5, FS).pos(w, 0)
				.text_centered(true).hoverable(true).onclick(info -> clear_text())
				.hint("editor.info.field_clear"));
			clear.hide();
			add(reset = new Element().color(0xf02c00).size(type.text() ? FS : 5, FS).pos(w + (type.text() ? FS : 5), 0)
				.text_centered(true).hoverable(true).onclick(info -> reset_text())
				.hint("editor.info.field_reset"));
			reset.hide();
		}
		if(type.text()){
			clear.text("C");
			reset.text("R");
		}
		if(type.color()){
			add(color = new Element().color(0x000000).size(20, 20).pos(w + 13, 3));
			add(new Element().color(GENERIC_FIELD.value).size(FS, FS).pos(w + 10 + FS, 0)
				.text("CP").text_autoscale().onclick(ci -> {
					try(MemoryStack stack = MemoryStack.stackPush()){
						ByteBuffer color = stack.malloc(3);
						String result = TinyFileDialogs.tinyfd_colorChooser("Choose a Color", "#" + text.text(), null, color);
						if(result == null) return;
						text(result);
						consumer.accept(this);
					}
					catch(Exception e){
						e.printStackTrace();
					}
				}).hint("editor.info.colorpicker"));
		}
	}

	@Override
	public Element text(Object ntext){
		super.text(ntext);
		return this;
	}

	@Override
	protected void onSelect(){
		previous = text.text();
		if(type.info()) return;
		clear.show();
		reset.show();
	}

	@Override
	protected void onDeselect(Element current){
		reset_text();
		if(type.info()) return;
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

	public Field deg_range(){
		min_val = -180;
		max_val = 180;
		return this;
	}

	public String get_text(){
		return text.text();
	}

	public float parse_float(){
		if(type.color()) return parse_int();
		String str = text.text().replaceAll("[^0-9\\.\\-]", "");
		if(str.isEmpty()) str = "0";
		if(str.endsWith(".")) str += "0";
		float res = Float.parseFloat(str);
		if(res < min_val) res = min_val;
		if(res > max_val) res = max_val;
		return res;
	}

	public float parse_int(){
		int res;
		if(type.color()){
			String str = text.text().replaceAll("[^0-9a-f]", "");
			res = Integer.parseInt(str, 16);
		}
		else{
			String str = text.text().replaceAll("[^0-9\\.\\-]", "");
			if(str.isEmpty()) str = "0";
			if(str.endsWith(".")) str = str.replace(".", "");
			res = Integer.parseInt(str);
		}
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
			if(consumer != null) consumer.accept(this);
			if(polyval == null && type == FieldType.COLOR) color.color((int)parse_int());
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
		else if(type.color()){
			if((key >= GLFW_KEY_0 && key <= GLFW_KEY_9) || (key >= GLFW_KEY_A && key <= GLFW_KEY_F)){
				String kn = GLFW.glfwGetKeyName(key, code);
				text(txt + kn);
			}
		}
		else if(type.integer() || type.decimal()){
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

	public Object type_format(float value){
		if(type.color()){
			color.color((int)value);
			return Integer.toHexString(color.col_def.packed);
		}
		return value;
	}

	public static enum FieldType {

		TEXT,
		FLOAT,
		INT,
		COLOR,
		INFO;

		public boolean text(){
			return this == TEXT;
		}

		public boolean decimal(){
			return this == FLOAT;
		}

		public boolean integer(){
			return this == INT;
		}

		public boolean color(){
			return this == COLOR;
		}

		public boolean info(){
			return this == INFO;
		}

		public float width(float width){
			switch(this){
				case TEXT: return width - FS - FS;
				case FLOAT:
				case INT: return width - 10;
				case COLOR: return width - FS - FS - 10;
				case INFO: return width;
			}
			return width;
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
