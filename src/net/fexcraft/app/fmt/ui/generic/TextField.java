package net.fexcraft.app.fmt.ui.generic;

import java.util.ArrayList;
import java.util.Optional;
import org.newdawn.slick.Color;

import net.fexcraft.app.fmt.FMTB;
import net.fexcraft.app.fmt.ui.Element;
import net.fexcraft.app.fmt.ui.editor.TextureEditor;
import net.fexcraft.app.fmt.utils.GGR;
import net.fexcraft.app.fmt.utils.TextureManager;
import net.fexcraft.lib.common.math.RGB;

public class TextField extends Element {
	
	private static final ArrayList<TextField> fields = new ArrayList<>();
	private RGB hovercolor = new RGB(112, 255, 127), inactivecol = new RGB(235, 201, 201), hoversel = new RGB(255, 127, 0);
	private boolean centered, selected, number, background = true;
	private Color color = Color.white;
	private float min, max, value;
	private String text, tempval;
	private int width;

	public TextField(Element parent, String id, int width, int x, int y){
		super(parent, id); fields.add(this);
		this.width = width; this.height = 26;
		if(parent == null){ this.x = x; this.y = y; }
		else{ this.x = parent.x + x; this.y = parent.y + y; }
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
	
	public TextField setColor(Color newcol){
		this.color = newcol; return this;
	}

	@Override
	public void renderSelf(int rw, int rh){
		if(enabled) (isSelected() ? hovered ? hoversel : hovercolor : hovered ? RGB.BLACK : inactivecol).glColorApply();
		if(background) this.renderQuad(x, y, width, height, "ui/background");
		if(enabled) RGB.glColorReset();
		if(!number && text == null) return;
		String text = number ? (tempval == null ? value : "*" + tempval) + "" : tempval == null ? this.text : tempval;
		TextureManager.unbind();
		if(centered){
			int x = width / 2 - (font.getWidth(text) / 2);
			int y = height / 2 - (font.getHeight(text) / 2);
			font.drawString(this.x + x, this.y + y, text, color);
		}
		else{
			font.drawString(x + 2, y + 2, text, color);
		}
		RGB.glColorReset();
	}

	@Override
	protected boolean processButtonClick(int x, int y, boolean left){
		if(this.isSelected()){ this.onReturn(); this.selected = false; return true; }
		deselectAll(); return this.selected = true;
	}

	public static void deselectAll(){
		fields.forEach(elm -> { if(elm.isSelected()) elm.onReturn(); elm.selected = false; }); TextureEditor.reset();
	}
	
	@Override
	public void hovered(int mouseX, int mouseY){
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
	
	public void applyChange(float f){
		this.value = f;
	}

	public static boolean anySelected(){
		return fields.stream().filter(pre -> pre.isSelected()).findFirst().isPresent();
	}

	public static TextField getSelected(){
		Optional<TextField> sel = fields.stream().filter(pre -> pre.isSelected()).findFirst();
		return sel.isPresent() ? sel.get() : null;
	}

	public void onInput(int id, String key){
		if(number && !isNumber(id, tempval == null || tempval.length() == 0, key)){ return; }
		if(number){
			if(tempval == null){
				tempval = key.equals("-") && !(value + "").contains("-") ? key : value + "";
			}
			if(key.equals("-")){
				if(tempval.length() == 0) tempval = (value + "").contains("-") ? key : key + value; return;
			}
			if(key.equals(".") && tempval.indexOf(".") >= 0) return;
			float fl = parseFloat(value, tempval + key);
			if(fl < min){ tempval = min + ""; return; }
			if(fl > max){ tempval = max + ""; return; }
			tempval = fl % 1.0f != 0 ? fl + "" : tempval + key;
			return;
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
		return id < 12;
	}

	public void onBackSpace(){
		if(tempval == null || tempval.length() <= 1) tempval = "";
		else tempval = tempval.substring(0, tempval.length() - 1);
	}

	public void onReturn(){
		if(number){
			if(tempval != null && tempval.length() > 0) value = parseFloat(value, tempval);
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

	public Element setColor(String string, RGB rgb){
		switch(string){
			case "hover": this.hovercolor = rgb; break;
			case "hover_sel": case "hover_selected": this.hoversel = rgb; break;
			case "inactive": this.inactivecol = rgb; break;
		} return this;
	}
}