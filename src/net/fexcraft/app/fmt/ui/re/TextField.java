package net.fexcraft.app.fmt.ui.re;

import java.util.ArrayList;
import java.util.Optional;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.FontRenderer;
import net.fexcraft.app.fmt.ui.NewElement;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.lib.common.math.RGB;

public class TextField extends NewElement {
	
	public static final ArrayList<TextField> FIELDS = new ArrayList<>();
	private RGB hoversel = new RGB(255, 127, 0);
	private boolean centered, selected, number, background = true, withcommas = false;
	private float min, max, value;
	private String text, tempval;
	private RGB textcolor = new RGB(212, 212, 212), hovertextcolor = null;

	public TextField(NewElement root, String id, String style, int width, int x, int y){
		super(root, id, style == null ? "field" : style); FIELDS.add(this); this.setSize(width, 26);
		this.setPosition(x, y, root == null ? 1 : root.z + 1);
		this.setHoverColor(0xff70ff7f, false);
		this.setHoverColor(0xffebc9c9, true);
	}
	
	public TextField setText(String string, boolean centered){
		this.text = string; this.centered = centered; return this;
	}
	
	public TextField setAsNumberfield(float min, float max, boolean centered){
		this.number = true; this.min = min; this.max = max; this.centered = centered; value = 0; return this;
	}
	
	public TextField setRenderBackground(boolean bool){
		this.background = bool; return this;
	}
	
	public TextField setColor(RGB newcol){
		textcolor = newcol; return this;
	}
	
	public TextField setWithCommas(boolean bool){
		this.withcommas = bool; return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(enabled) (isSelected() ? hovered ? hoversel : hovercolor : hovered ? RGB.BLACK : discolor).glColorApply();
		if(background) this.renderQuad(x, y, width, height, "ui/background_dark");
		if(enabled) RGB.glColorReset();
		if(!number && text == null) return;
		String tex = number ? (tempval == null ? value : "*" + tempval) + "" : tempval == null ? this.text : tempval;
		if(centered){
			int x = width / 2 - (FontRenderer.getWidth(tex, 1) / 2), y = height / 2 - 10;
			FontRenderer.drawText(tex, this.x + x, this.y + y, 1, hovered && hovertextcolor != null ? hovertextcolor : textcolor);
		}
		else{
			FontRenderer.drawText(tex, this.x + 2, this.y + 2, 1, hovered && hovertextcolor != null ? hovertextcolor : textcolor);
		}
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		if(this.isSelected()){ this.onReturn(); this.selected = false; return true; }
		deselectAll(); return this.selected = true;
	}

	public static void deselectAll(){
		FIELDS.forEach(elm -> { if(elm.isSelected()) elm.onReturn(); elm.selected = false; });
	}
	
	@Override
	public void hovered(float mouseX, float mouseY){
		this.hovered = mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + height;
	}

	public final String getText(){
		return text;
	}

	public void validateChange(String string){
		this.text = string; //TODO
	}
	
	@Override
	protected boolean processScrollWheel(int wheel){
		return FMTB.MODEL.updateValue(this, this.id + (wheel > 0 ? "+" : "-")) || true;
	}

	public float tryChange(boolean positive, float rate){
		if(!number) return 0; float f = value;
		f = positive ? f + rate : f - rate;
		return f >= min && f <= max ? value = f : value;
	}
	
	public float tryChange(float otherval, boolean positive, float rate){
		if(!number) return 0; float f = otherval;
		f = positive ? f + rate : f - rate;
		return f >= min && f <= max ? otherval = f : otherval;
	}
	
	public TextField applyChange(float f){
		this.value = f; return this;
	}
	
	public TextField applyChange(String s){
		this.text = s; return this;
	}

	public static boolean anySelected(){
		return FIELDS.stream().filter(pre -> pre.isSelected()).findFirst().isPresent();
	}

	public static TextField getSelected(){
		Optional<TextField> sel = FIELDS.stream().filter(pre -> pre.isSelected()).findFirst();
		return sel.isPresent() ? sel.get() : null;
	}

	public void onInput(int id, String key){
		if(id < 0){
			if(number && !isFloat(key)){ tempval = "+.0"; }
			else{ if(tempval == null) tempval = number ? "" : text; tempval += key; } return;
		}
		if(number && !isNumber(id, tempval == null || tempval.length() == 0, key)){ return; }
		if(number){
			if(tempval == null){
				tempval = key.equals("-") && !(value + "").contains("-") ? key + value : value + "";
			}
			if(key.equals("-")){
				if(tempval.length() == 0) tempval = (value + "").contains("-") ? key : key + value; return;
			}
			if(key.equals(".") && tempval.indexOf(".") >= 0) return;
			/*float fl = parseFloat(value, tempval + key);
			if(fl < min){ tempval = min + ""; return; }
			if(fl > max){ tempval = max + ""; return; }
			tempval = fl % 1.0f != 0 ? fl + "" : tempval + key;*///check this onReturn instead
			tempval = tempval + key; return;
		}
		else{
			if(key.equals("-") && GGR.isShiftDown()) key = "_";
			if(tempval == null) tempval = text;
			if(tempval.length() == 0 && key.equals(" ")) return;
			tempval += key; return;
		}
	}

	private float parseFloat(float def, String string){
		try{
			return Float.parseFloat(string);
		}
		catch(Exception e){
			e.printStackTrace();
			return def;
		}
	}

	private boolean isNumber(int id, boolean first, String key){
		if(key.equals(" ")) return false;
		if(first && key.equals("-")) return true;
		if(!first && key.equals(".")) return true;
		if(!first && key.equals(",") && withcommas) return true;
		return id < 12 || id > 70;
	}

	private boolean isFloat(String string){
		try{ Float.parseFloat(string); return true; }
		catch(Exception e){ return false; }
	}

	public void onBackSpace(boolean clear){
		if(tempval == null || tempval.length() <= 1) tempval = "";
		else if(clear) tempval = ""; else tempval = tempval.substring(0, tempval.length() - 1);
	}

	public void onReturn(){
		if(number){
			if(tempval != null && tempval.length() > 0) value = parseFloat(value, tempval);
			if(value < min) value = min; if(value > max) value = max;
		}
		else{
			if(tempval != null && tempval.length() > 0) text = tempval;
		}
		tempval = null; if(number) updateNumberField(); else updateTextField(); return;
	}

	protected void updateNumberField(){
		FMTB.MODEL.updateValue(this);
	}

	protected void updateTextField(){}

	public float getFloatValue(){
		return value;
	}
	
	public String getTextValue(){
		return text;
	}

	public int getIntegerValue(){
		return (int)value;
	}

	public boolean isSelected(){
		return selected;
	}
	
	@Override
	public boolean deselect(){
		super.deselect(); this.selected = false; return true;
	}

	public TextField setColor(String string, RGB rgb){
		switch(string){
			case "hover": this.hovercolor = rgb; break;
			case "hover_sel": case "hover_selected": this.hoversel = rgb; break;
			case "inactive": this.discolor = rgb; break;
		} return this;
	}

	public static ArrayList<TextField> getAllFields(){ return FIELDS; }

	public static TextField getFieldById(String string){
		for(TextField field : FIELDS) if(field.id.equals(string)) return field; return null;
	}
	
	public static class BooleanField extends TextField {

		public BooleanField(NewElement root, String id, int width, int x, int y){
			super(root, id, "field:boolean", width, x, y);
		}
		
		@Override
		protected boolean processButtonClick(int x, int y, boolean left){
			this.applyChange(!(this.getIntegerValue() == 1) ? 1 : 0); this.updateNumberField(); return true;
		}
		
		@Override
		public TextField applyChange(float f){
			super.applyChange(f); this.setText((this.getIntegerValue() == 1) + "", true); return this;
		}
		
	}

	public TextField setColorOnHover(RGB rgb){
		hovertextcolor = rgb; return this;
	}
	
}